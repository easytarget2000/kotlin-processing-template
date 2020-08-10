package eu.ezytaget.processing.julia_set_fractals

import eu.ezytaget.processing.julia_set_fractals.palettes.DuskPalette
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
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
    private var automatonUpdateDelay = 16
    private lateinit var juliaSet: JuliaSet
    private val juliaSetDrawer = JuliaSetDrawer()

    override fun settings() {
        if (FULL_SCREEN) {
            fullScreen(RENDERER)
        } else {
            size(WIDTH, HEIGHT, RENDERER)
        }
    }

    override fun setup() {
        frameRate(FRAME_RATE)
        colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        clearFrame()
        frameRate(FRAME_RATE)
        clapper.bpm = 130f
        clapper.start()

        initJuliaSet()

        setPerspective()
    }

    override fun draw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = backgroundAlpha)
        }

        if (DRAW_FRAME_RATE) {
            drawFrameRate()
        }

//        translate(width / 2f, height / 2f)

        juliaSet.update()
        juliaSetDrawer.draw(juliaSet, pApplet = this)

//        updateRotations()
        updateClapper()

        loadPixels()
        pixels.forEachIndexed { index, pixelValue ->
            if (index + 1 == pixels.size) {
                return
            }
            val neighborValue = pixels[index + 1]
            pixels[index] = pixelValue - neighborValue
        }
        updatePixels()

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            'x' -> {
                clearAll()
            }
            ' ' -> {
                clapper.tapBpm()
            }
        }
    }

    /*
    Implementations
     */

    private fun initJuliaSet() {
        val scaleWidth = random.nextFloat(from = 2f, until = 6f)
        val scaleHeight = (scaleWidth * height.toFloat()) / width.toFloat()
        val angle = random.nextFloat(from = 0f, until = PConstants.TWO_PI)
        val angleVelocity = random.nextFloat(from = -0.5f, until = 0.1f)
        val aAngleFactor = random.nextFloat(from = -2f, until = 2f)

        juliaSet = JuliaSet(
                scaleWidth = scaleWidth,
                scaleHeight = scaleHeight,
                angle = angle,
                angleVelocity = angleVelocity,
                aAngleFactor = aAngleFactor
        )
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
        initJuliaSet()
        clearFrame()
    }

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
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

        randomSeed(System.currentTimeMillis())

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
        }

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            random.maybe(probability = 0.9f) {
                bounce()
            }
        }

        if (clapperResult[BeatInterval.TwoWhole]?.didChange == true) {
            random.maybe(probability = 0.2f) {
                initJuliaSet()
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

    companion object {
        private const val CLICK_TO_DRAW = false
        private const val FULL_SCREEN = true
        private const val WIDTH = 1400
        private const val HEIGHT = 900
        private const val RENDERER = PConstants.P3D
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