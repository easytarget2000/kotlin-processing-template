package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.core.PImage
import kotlin.random.Random

class PApplet : processing.core.PApplet() {

    private val random = Random.Default

    private val clapper = Clapper()

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private val raster = Raster()

    private lateinit var pImage: PImage

    private var waitingForClickToDraw = false

    private var radiusFactorVelocity = 0f

    private var backgroundAlpha = 0.1f

    private var xRotation = 1f

    private var zRotation = 1f

    private var xRotationVelocity = 0.0021f

    private var zRotationVelocity = 0.002f

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
//        cameraRealm.setCaptureAndStart(pApplet = this)
        pImage = loadImage("input.jpg")

        clearFrame()
        clapper.start()

        val randomSeed = System.currentTimeMillis()
        randomSeed(randomSeed)
        println("DEBUG: PApplet: setup(): random seed: $randomSeed")
    }

    private var numberOfIterationsPerFrame = 10

    override fun draw() {
        backgroundDrawer.draw(pApplet = this, alpha = 1f)

//        val width = width.toFloat()
//        raster.numberOfColumns = map(mouseX.toFloat(), 0f, width, 0f, width).toInt()
//        val height = height.toFloat()
//        raster.numberOfRows = map(mouseY.toFloat(), 0f, height, 0f, height).toInt()

        raster.drawIn(pApplet = this, pImage = pImage)

//        println(frameRate)
    }

    override fun keyPressed() {
        when (key) {
            ' ' ->
                clapper.tapBpm()
            'x' ->
                clearFrame()
        }
    }

    /*
    Implementations
     */

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

//        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
//            random.maybe(probability = 0.9f) {
//                bounce()
//            }
//        }

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
//            random.maybe(probability = 0.2f) {}
            random.maybe(probability = 0.5f) {
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

        if (clapperResult[BeatInterval.SixteenWhole]?.didChange == true) {
            background(0)
        }
    }

    private fun bounce() {
        radiusFactorVelocity = 0.05f
    }

    private fun setRandomBackgroundAlpha() {
        if (!maybe { backgroundAlpha = random(MAX_COLOR_VALUE / 64f) }) {
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

        private const val WIDTH = 600

        private const val HEIGHT = 600

        private const val RENDERER = PConstants.P3D

        private const val DISPLAY_ID = 2

        private const val COLOR_MODE = PConstants.HSB

        private const val MAX_COLOR_VALUE = 1f

        private const val FRAME_RATE = 120f

        private const val DRAW_BACKGROUND_ON_DRAW = true

        private const val DRAW_FRAME_RATE = false

        private const val MAX_ROTATION_VELOCITY = 0.03f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }

    }
}