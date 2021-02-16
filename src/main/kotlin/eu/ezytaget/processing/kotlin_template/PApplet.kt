package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.char_raster.CharRaster
import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytaget.processing.kotlin_template.realms.Realm
import eu.ezytaget.processing.kotlin_template.realms.julia_set.JuliaSetRealm
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.event.MouseEvent
import processing.core.PGraphics
import eu.ezytaget.processing.kotlin_template.realms.tesseract.Tesseract
import eu.ezytaget.processing.kotlin_template.realms.tesseract.TesseractProjector
import kotlin.random.Random


class PApplet : processing.core.PApplet() {

    private val random = Random.Default

    private val clapper = Clapper()

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var waitingForClickToDraw = false

    private var radiusFactorVelocity = 0f

    private var backgroundAlpha = 1f

    private var xRotation = 1f

    private var zRotation = 1f

    private var xRotationVelocity = 0.021f

    private var zRotationVelocity = 0.002f

    private lateinit var tesseracts: List<Tesseract>

    private var tesseractProjector = TesseractProjector()

    private var angle = 0f

    private var lastLaserClearMillis = 0L

    private var calm = true

    private var raster: CharRaster = CharRaster()

    private var clearFrameOnTextSizeFinding = false

    private val realms = mutableListOf<Realm>()

    private var applyCharRaster = false

    private var smearPixels = true

    private var laserClearMode = true

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

        clearFrame()
        clapper.start()

        kaleidoscope = createGraphics(width, height, RENDERER)
        kaleidoscope.beginDraw()
        kaleidoscope.colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        kaleidoscope.endDraw()

        initRealms(pGraphics = kaleidoscope)

        setPerspective()

        raster?.setup(pApplet = this)

        randomSeed(System.currentTimeMillis())
    }

    private var numberOfIterationsPerFrame = 1

    override fun draw() {
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
    }

    private fun iterateDraw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = backgroundAlpha)
        }

        if (DRAW_FRAME_RATE) {
            drawFrameRate()
        }

        realms.forEach {
            it.update(pApplet = this)
        }

        kaleidoscope.beginDraw()
        kaleidoscope.clear()
        realms.forEach {
            it.drawIn(pGraphics = kaleidoscope)
        }
        kaleidoscope.endDraw()

        pushStyle()
        val numberOfKaleidoscopeEdges = 9
        repeat(numberOfKaleidoscopeEdges) {
            pushMatrix()
            translate(width / 2f, height / 2f)
            rotate((it / numberOfKaleidoscopeEdges.toFloat()) * PConstants.TWO_PI)
            image(kaleidoscope, -100f, -kaleidoscope.height / 2f)
            popMatrix()
        }
        popStyle()

        updateClapper()

        // Tesseract:
        push()

        translate(width / 2f, height / 2f)
        updateRotations()
        rotateX(-PConstants.PI / 2f)
        angle += 0.02f

        strokeWeight(1f)
        repeat(3) {
            pushMatrix()
            rotateZ((it / 3f) * PConstants.TWO_PI)
            tesseracts.forEach { tesseract ->
                tesseractProjector.draw(tesseract, angle, pApplet = this)
            }
            popMatrix()
        }

        tesseractProjector.updateColorValues()

        pop()

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            ' ' ->
                clapper.tapBpm()
            'x' ->
                clearFrame()
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

    private fun initRealms(pGraphics: PGraphics) {
        val juliaSetRealm = JuliaSetRealm()
        juliaSetRealm.setup(pGraphics)
        juliaSetRealm.brightness = 1f
        juliaSetRealm.alpha = 1f

//        realms.add(juliaSetRealm)

        val numberOfTesseracts = random.nextInt(from = 1, until = 10)
        tesseracts = (0 until numberOfTesseracts).map {
            val scale = random.nextFloat(from = 0.1f, until = 0.5f)
            Tesseract(scale)
        }
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

    private fun clearAll() {
        initRealms(pGraphics = kaleidoscope)
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

    private fun drawFrameRate() {
        pushStyle()
        stroke(1f)
        text(frameRate.toString(), 100f, 100f)
        popStyle()
    }

    private fun updateRotations() {
        zRotation += zRotationVelocity
        rotateZ(zRotation)
        xRotation += xRotationVelocity
        rotateX(xRotation)
    }

    private fun updateClapper() {
        val clapperResult = clapper.update()

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            random.maybe(probability = 0.9f) {
                bounce()
            }
        }

        if (clapperResult[BeatInterval.TwoWhole]?.didChange == true) {
            random.maybe(probability = 0.2f) {
                initRealms(pGraphics = kaleidoscope)
            }
//            random.maybe(probability = 0.8f) {
//                initTesseracts()
//            }

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
        }

        if (clapperResult[BeatInterval.EightWhole]?.didChange == true) {
            clearAll()
        }
    }

    private fun bounce() {
        radiusFactorVelocity = 0.05f
    }

    private fun setRandomBackgroundAlpha() {
        if (!random.maybe { backgroundAlpha = random(MAX_COLOR_VALUE / 64f) }) {
            backgroundAlpha = 1f
        }
    }

    private fun setRandomXRotationVelocity() {
        xRotationVelocity = random(-MAX_ROTATION_VELOCITY, MAX_ROTATION_VELOCITY)
    }

    private fun setRandomZRotationVelocity() {
        zRotationVelocity = random(-MAX_ROTATION_VELOCITY, MAX_ROTATION_VELOCITY)
    }

    private fun setTextSize(relativeTextSizeValue: Float) {
        val raster = raster ?: return

        if (clearFrameOnTextSizeFinding) {
            background(0.1f, 1f, 1f, 1f)
        }

        val textSize = map(relativeTextSizeValue, 0f, width.toFloat(), 4f, 32f)

        raster.setTextSize(pApplet = this, textSize = textSize)
    }

    companion object {

        private const val CLICK_TO_DRAW = false

        private const val FULL_SCREEN = true

        private const val WIDTH = 800

        private const val HEIGHT = 600

        private const val RENDERER = PConstants.P3D

        private const val DISPLAY_ID = 1

        private const val COLOR_MODE = PConstants.HSB

        private const val MAX_COLOR_VALUE = 1f

        private const val FRAME_RATE = 60f

        private const val DRAW_BACKGROUND_ON_DRAW = false

        private const val DRAW_FRAME_RATE = false

        private const val MAX_ROTATION_VELOCITY = 0.03f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }

    }
}