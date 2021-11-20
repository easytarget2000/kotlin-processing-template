package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.char_raster.CharRaster
import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytaget.processing.kotlin_template.realms.RealmsManager
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.core.PGraphics
import processing.event.MouseEvent
import kotlin.random.Random


class PApplet : processing.core.PApplet() {

    private val random = Random.Default

    private val clapper = Clapper()

    private var runClapper = false

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var drawBackgroundOnDraw = true
        set(value) {
            field = value
            println("PApplet: drawBackgroundOnDraw: set: $drawBackgroundOnDraw")
        }

    private var backgroundAlpha = 0.1f

    private var useBackgroundDrawerPalette = false

    private var waitingForClickToDraw = false

    private var radiusFactorVelocity = 0f

    private var lastLaserClearMillis = 0L

    private var raster: CharRaster = CharRaster()

    private var clearFrameOnTextSizeFinding = false

    private val realmsManager = RealmsManager()

    private var applyCharRaster = false

    private var smearPixels = false

    private var laserClearMode = false

    private var noiseSeedSalt = System.currentTimeMillis()

    private var numberOfKaleidoscopeEdges = 1
        set(value) {
            field = value
            println("PApplet: numberOfKaleidoscopeEdges: set: $numberOfKaleidoscopeEdges")
        }

    private var minNumberOfKaleidoscopeEdges = 1

    private var maxNumberOfKaleidoscopeEdges = 6

    private var lastLoggedFrameRate: Float? = null

    private var frameRateLoggingThreshold = 7f

    private lateinit var kaleidoscope: PGraphics

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
        lights()
        ambientLight(0.8f, 1f, 1f)

        raster.setup(pApplet = this)

        clearFrame()
        clapper.start()
        clapper.bpm = 116.9f

        kaleidoscope = createGraphics(width, height, RENDERER)
        kaleidoscope.beginDraw()
        kaleidoscope.colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        kaleidoscope.endDraw()

        initRealms()

        raster.setup(pApplet = this)

        randomSeed(System.currentTimeMillis())
    }

    private var numberOfIterationsPerFrame = 1

    override fun draw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        backgroundAlpha = noise(frameCount / 1000f)

        if (drawBackgroundOnDraw) {
            backgroundDrawer.draw(pApplet = this, alpha = backgroundAlpha)
//            backgroundDrawer.draw(kaleidoscope, alpha = backgroundAlpha)

            kaleidoscope.beginDraw()
            kaleidoscope.clear()
            kaleidoscope.endDraw()
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
            laserClear()
        }

        if (applyCharRaster) {
            raster.drawIn(pApplet = this)
        }

        logFrameRateIfNeeded()
    }

    override fun keyPressed() {
        when (key) {
            CLAPPER_TAP_BPM_KEY ->
                clearFrame()
//                clapper.tapBpm()
            CLEAR_FRAME_KEY ->
                clearFrame()
            TOGGLE_SMEAR_PIXELS_KEY ->
                toggleSmearPixels()
            TOGGLE_LASER_CLEAR_MODE_KEY ->
                toggleLaserClearMode()
            INIT_REALMS_KEY ->
                initRealms()
            SET_NUMBER_OF_KALEIDOSCOPE_EDGES_KEY ->
                setRandomNumberOfKaleidoscopeEdges()
            SET_NEXT_NOISE_SEED_KEY ->
                setNextNoiseSeed()
        }
    }

    override fun mouseClicked(event: MouseEvent?) {
        super.mouseClicked(event)
        if (event == null) {
            return
        }

        realmsManager.handleMouseClick(event.button, event.x, event.y, pApplet = this)

        setTextSize(relativeTextSizeValue = event.x.toFloat())
    }

    /*
    Implementations
     */

    private fun initRealms() {
        realmsManager.initRandomRealms(pApplet = this, pGraphics = kaleidoscope)
    }

    private fun iterateDraw() {
        realmsManager.update(pApplet = this)

        kaleidoscope.beginDraw()
        realmsManager.drawIn(pGraphics = kaleidoscope, frameCount = frameCount)
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
        if (useBackgroundDrawerPalette) {
            backgroundDrawer.drawRandomColor(pApplet = this, alpha = 1f, random = random)
        } else {
            backgroundDrawer.draw(pApplet = this, alpha = 1f)
        }
    }

    private fun smearPixels() {
        loadPixels()
        pixels.forEachIndexed { i, _ ->
            if (i + 10 < pixels.size) {
                val neighborValue = pixels[i + 10]
                pixels[i] = pixels[i] - neighborValue
            }
        }
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
                realmsManager.cellAutomaton3D?.update()
            }
        }

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            random.maybe(probability = 0.9f) {
                bounce()
            }

            random.maybe {
                setRandomZRotationVelocity()
            }

            random.maybe(probability = 0.9f) {
                clearFrame()
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
                realmsManager.setRandomStyle()
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
                realmsManager.scanStripesRealm?.resetProgress()
            }
            random.maybe {
                setNextNoiseSeed()
            }
            random.maybe {
                toggleLaserClearMode()
            }
        }

        if (clapperResult[BeatInterval.EightWhole]?.didChange == true) {
            clearAll()
        }
    }

    private fun bounce() {
        radiusFactorVelocity = 0.05f
        realmsManager.bounce(pApplet = this)
    }

    private fun setRandomBackgroundAlpha() {
        if (!random.maybe { backgroundAlpha = random(MAX_COLOR_VALUE / 64f) }) {
            backgroundAlpha = 1f
        }
    }

    private fun setRandomXRotationVelocity() {
        realmsManager.tesseractRealm?.xRotationVelocity = random(-MAX_ROTATION_VELOCITY, MAX_ROTATION_VELOCITY)
    }

    private fun setRandomZRotationVelocity() {
        realmsManager.tesseractRealm?.zRotationVelocity = random(-MAX_ROTATION_VELOCITY, MAX_ROTATION_VELOCITY)
        realmsManager.scanStripesRealm?.setRandomRotationVelocity()
    }

    private fun toggleSmearPixels() {
        smearPixels = !smearPixels
    }

    private fun toggleLaserClearMode() {
        laserClearMode = !laserClearMode
    }

    private fun setTextSize(relativeTextSizeValue: Float) {
        val raster = raster ?: return

        if (clearFrameOnTextSizeFinding) {
            background(0.1f, 1f, 1f, 1f)
        }

        val textSize = map(relativeTextSizeValue, 0f, width.toFloat(), 4f, 32f)

        raster.setTextSize(pApplet = this, textSize = textSize)
    }

    private fun setNextNoiseSeed() {
        val seed = frameCount.toLong() + noiseSeedSalt
        println("PApplet: setNextNoiseSeed(): noiseSeedSalt: $noiseSeedSalt, seed: $seed")
        noiseSeed(seed)
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

        private const val TOGGLE_LASER_CLEAR_MODE_KEY = 'l'

        private const val TOGGLE_SMEAR_PIXELS_KEY = 's'

        private const val CLEAR_FRAME_KEY = 'x'

        private const val SET_NEXT_NOISE_SEED_KEY = 'n'

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }

    }
}