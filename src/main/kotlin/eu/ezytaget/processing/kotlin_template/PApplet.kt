package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.char_raster.CharRaster
import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytaget.processing.kotlin_template.realms.Realm
import eu.ezytaget.processing.kotlin_template.realms.cell_automaton_3d.CellAutomaton3D
import eu.ezytaget.processing.kotlin_template.realms.cell_automaton_3d.MooreNeighborCounter
import eu.ezytaget.processing.kotlin_template.realms.cell_automaton_3d.VonNeumannNeighborCounter
import eu.ezytaget.processing.kotlin_template.realms.julia_set.JuliaSetRealm
import eu.ezytaget.processing.kotlin_template.realms.scan_stripes.ScanStripesRealm
import eu.ezytaget.processing.kotlin_template.realms.scanner.ScannerRealm
import eu.ezytaget.processing.kotlin_template.realms.tesseract.TesseractRealm
import eu.ezytaget.processing.kotlin_template.realms.test_image.TestImageRealm
import eu.ezytaget.processing.kotlin_template.realms.tree_realms.TreeRingsRealm
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.core.PGraphics
import processing.event.MouseEvent
import kotlin.random.Random


class PApplet : processing.core.PApplet() {

    private val random = Random.Default

    private val clapper = Clapper()

    private var runClapper = true

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var drawBackgroundOnDraw = true
        set(value) {
            field = value
            println("PApplet: drawBackgroundOnDraw: set: $drawBackgroundOnDraw")
        }

    private var backgroundAlpha = 1f
        set(value) {
            field = value
            println("PApplet: backgroundAlpha: set: $backgroundAlpha")
        }

    private var waitingForClickToDraw = false

    private var radiusFactorVelocity = 0f

    private var lastLaserClearMillis = 0L

    private var raster: CharRaster = CharRaster()

    private var clearFrameOnTextSizeFinding = false

    private val realms = mutableListOf<Realm>()

    private var applyCharRaster = false

    private var smearPixels = false

    private var laserClearMode = false

    private var numberOfKaleidoscopeEdges = 1
        set(value) {
            field = value
            println("PApplet: numberOfKaleidoscopeEdges: set: $numberOfKaleidoscopeEdges")
        }

    private var minNumberOfKaleidoscopeEdges = 1

    private var maxNumberOfKaleidoscopeEdges = 6

    private var lastLoggedFrameRate: Float? = null

    private var frameRateLoggingThreshold = 4.1f

    private lateinit var kaleidoscope: PGraphics

    private val tesseractRealm: TesseractRealm?
        get() = realms.firstOrNull { it is TesseractRealm } as? TesseractRealm

    private val cellAutomaton3D: CellAutomaton3D?
        get() = realms.firstOrNull { it is CellAutomaton3D } as? CellAutomaton3D

    private val scanStripesRealm: ScanStripesRealm?
        get() = realms.firstOrNull { it is ScanStripesRealm } as? ScanStripesRealm

    override fun settings() {
        if (FULL_SCREEN) {
            fullScreen(RENDERER, DISPLAY_ID)
        } else {
            size(WIDTH, HEIGHT, RENDERER)
        }
    }

    override fun setup() {
        frameRate(FRAME_RATE)
        colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        smooth()

        raster.setup(pApplet = this)

        clearFrame()
        clapper.start()
        clapper.bpm = 116.9f

        kaleidoscope = createGraphics(width, height, RENDERER)
        kaleidoscope.beginDraw()
        kaleidoscope.colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        kaleidoscope.endDraw()

        initRealms()

        setPerspective()

        raster.setup(pApplet = this)

        randomSeed(System.currentTimeMillis())
    }

    private var numberOfIterationsPerFrame = 1

    override fun draw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        kaleidoscope.clear()

        if (drawBackgroundOnDraw) {
            backgroundDrawer.draw(pApplet = this, alpha = backgroundAlpha)
        }

        if (runClapper) {
            updateClapper()
        }

        (0..numberOfIterationsPerFrame).forEach { _ ->
            push()
            iterateDraw()
            pop()
        }

        if (smearPixels) {
            smearPixels()
        }

        if (laserClearMode) {
//            laserClear()
        }

        if (applyCharRaster) {
            raster.drawIn(pApplet = this)
        }

        logFrameRateIfNeeded()
    }

    override fun keyPressed() {
        when (key) {
            CLAPPER_TAP_BPM_KEY ->
                clapper.tapBpm()
            CLEAR_FRAME_KEY ->
                clearFrame()
            TOGGLE_SMEAR_PIXELS_KEY ->
                toggleSmearPixels()
            INIT_REALMS_KEY ->
                initRealms()
            SET_NUMBER_OF_KALEIDOSCOPE_EDGES_KEY ->
                setRandomNumberOfKaleidoscopeEdges()
        }
    }

    override fun mouseClicked(event: MouseEvent?) {
        super.mouseClicked(event)
        if (event == null) {
            return
        }

        realms.forEach {
            it.handleMouseClick(event.button, event.x, event.y, pApplet = this)
        }

        setTextSize(relativeTextSizeValue = event.x.toFloat())
    }

    /*
    Implementations
     */

    private fun initRealms() {
        realms.clear()

        val juliaSetRealm = JuliaSetRealm()
        juliaSetRealm.setup(kaleidoscope)
        juliaSetRealm.brightness = 1f
        juliaSetRealm.alpha = 1f
//        realms.add(juliaSetRealm)

        val tesseractRealm = TesseractRealm()
        tesseractRealm.setup(pApplet = this)
//        realms.add(tesseractRealm)

        val automatonSize = min(width, height) * 0.9f

        val neighborCounter = if (random(1f) > 0.5f) {
            VonNeumannNeighborCounter()
        } else {
            MooreNeighborCounter()
        }

        if (random.nextBoolean()) {
            val testImageRealm = TestImageRealm()
//            realms.add(testImageRealm)
        }

        val cellAutomaton = CellAutomaton3D(
            numOfCellsPerSide = random(24f, 48f).toInt(),
            sideLength = automatonSize,
            neighborCounter = neighborCounter
        )
//        realms.add(cellAutomaton)

        val scanStripesRealm = ScanStripesRealm()
//        realms.add(scanStripesRealm)

        val treeRingsRealm = TreeRingsRealm()
//        realms.add(treeRingsRealm)

        val scannerRealm = ScannerRealm()
        realms.add(scannerRealm)
    }

    private fun setPerspective() {
        val cameraZ = ((height / 2f) / tan(PI * 60f / 360f))
//        perspective(
//                PI / 3f,
//                width.toFloat() / height.toFloat(),
//                cameraZ / 10f,
//                cameraZ * 30f
//        )
    }

    private fun iterateDraw() {
        realms.forEach {
            it.update(pApplet = this)
        }

        kaleidoscope.beginDraw()
        realms.forEachIndexed { index, realm ->
//            if (index == frameCount % realms.size) {
                realm.drawIn(pGraphics = kaleidoscope)
//            }
        }
        kaleidoscope.endDraw()

        if (numberOfKaleidoscopeEdges <= 1) {
            image(kaleidoscope, 0f, 0f)
        } else {
            push()
            repeat(numberOfKaleidoscopeEdges) {
                pushMatrix()
                translate(width / 2f, height / 2f)
                rotate((it / numberOfKaleidoscopeEdges.toFloat()) * PConstants.TWO_PI)
                image(kaleidoscope, -100f, -kaleidoscope.height / 2f)
                popMatrix()
            }
            pop()
        }

    }

    private fun clearAll() {
        initRealms()
        clearFrame()
    }

    private fun clearFrame() {
        backgroundDrawer.draw(
            pApplet = this,
            alpha = 1f
        )
    }

    private fun smearPixels() {
        loadPixels()
        pixels.forEachIndexed { i, _ ->
            if (i + 10 < pixels.size) {
                val neighborValue = pixels[i + 10]
                pixels[i] = pixels[i] - neighborValue
            }
        }
//            pixels.forEachIndexed { index, pixelValue ->
//                if (index + 10 == pixels.size) {
//                    return
//                }
//                val neighborValue = pixels[index + 10]
//                pixels[index] = pixelValue - neighborValue
//            }
        updatePixels()
    }

    private fun laserClear() {
        val nowMillis = System.currentTimeMillis()
        if (random.nextBoolean() && (nowMillis - lastLaserClearMillis) > 70L) {
            clearFrame()
            lastLaserClearMillis = nowMillis
        }
    }

    private fun updateClapper() {
        val clapperResult = clapper.update()

        if (clapperResult[BeatInterval.Quarter]?.didChange == true) {
            random.maybe(probability = 0.5f) {
                cellAutomaton3D?.update()
            }
        }

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            random.maybe(probability = 0.9f) {
                bounce()
            }

            random.maybe {
                setRandomZRotationVelocity()
            }
        }

        if (clapperResult[BeatInterval.TwoWhole]?.didChange == true) {
            random.maybe(probability = 0.2f) {
                initRealms()
            }
            random.maybe(probability = 0.1f) {
                toggleSmearPixels()
            }
            random.maybe(probability = 0.1f) {
                setRandomNumberOfKaleidoscopeEdges()
            }
            random.maybe {
                setRandomStyle()
            }
            random.maybe {
                clearFrame()
            }
            random.maybe {
                setRandomBackgroundAlpha()
            }
            random.maybe {
                setRandomXRotationVelocity()
            }
            random.maybe {
                setRandomZRotationVelocity()
            }
            random.maybe {
                setRandomBackgroundAlpha()
            }
            random.maybe {
                scanStripesRealm?.resetProgress()
            }
        }

        if (clapperResult[BeatInterval.EightWhole]?.didChange == true) {
            clearAll()
        }
    }

    private fun bounce() {
        radiusFactorVelocity = 0.05f
        realms.forEach { it.bounce(pApplet = this) }
    }

    private fun setRandomBackgroundAlpha() {
//        backgroundAlpha = random(MAX_COLOR_VALUE)
        if (!random.maybe { backgroundAlpha = random(MAX_COLOR_VALUE / 64f) }) {
            backgroundAlpha = 1f
        }
    }

    private fun setRandomXRotationVelocity() {
        tesseractRealm?.xRotationVelocity = random(-MAX_ROTATION_VELOCITY, MAX_ROTATION_VELOCITY)
    }

    private fun setRandomZRotationVelocity() {
        tesseractRealm?.zRotationVelocity = random(-MAX_ROTATION_VELOCITY, MAX_ROTATION_VELOCITY)
        scanStripesRealm?.setRandomRotationVelocity()
    }

    private fun toggleSmearPixels() {
        smearPixels = !smearPixels
    }

    private fun setRandomStyle() {
        realms.forEach { it.setRandomStyle() }
    }

    private fun setTextSize(relativeTextSizeValue: Float) {
        val raster = raster ?: return

        if (clearFrameOnTextSizeFinding) {
            background(0.1f, 1f, 1f, 1f)
        }

        val textSize = map(relativeTextSizeValue, 0f, width.toFloat(), 4f, 32f)

        raster.setTextSize(pApplet = this, textSize = textSize)
    }

    private fun setRandomNumberOfKaleidoscopeEdges() {
        if (random.nextFloat() < 0.5f) {
            numberOfKaleidoscopeEdges = minNumberOfKaleidoscopeEdges
            return
        }
        numberOfKaleidoscopeEdges = if (minNumberOfKaleidoscopeEdges >= maxNumberOfKaleidoscopeEdges) {
            maxNumberOfKaleidoscopeEdges
        } else {
            random.nextInt(from = minNumberOfKaleidoscopeEdges, until = maxNumberOfKaleidoscopeEdges)
        }
    }

    private fun logFrameRateIfNeeded() {
        val lastLoggedFrameRate = lastLoggedFrameRate
        val frameRate = frameRate
        val shouldLogFrameRate = if (lastLoggedFrameRate == null) {
            true
        } else {
            abs(lastLoggedFrameRate - frameRate) > frameRateLoggingThreshold
        }

        if (shouldLogFrameRate) {
            println("PApplet: frameRate: $frameRate")
            this.lastLoggedFrameRate = frameRate
        }
    }

    companion object {

        private const val CLICK_TO_DRAW = false

        private const val FULL_SCREEN = true

        private const val WIDTH = 800

        private const val HEIGHT = 600

        private const val RENDERER = PConstants.P3D

        private const val DISPLAY_ID = 2

        private const val COLOR_MODE = PConstants.HSB

        private const val MAX_COLOR_VALUE = 1f

        private const val FRAME_RATE = 60f

        private const val MAX_ROTATION_VELOCITY = 0.03f

        private const val CLAPPER_TAP_BPM_KEY = ' '

        private const val INIT_REALMS_KEY = 'i'

        private const val SET_NUMBER_OF_KALEIDOSCOPE_EDGES_KEY = 'k'

        private const val TOGGLE_SMEAR_PIXELS_KEY = 's'

        private const val CLEAR_FRAME_KEY = 'x'

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }

    }
}