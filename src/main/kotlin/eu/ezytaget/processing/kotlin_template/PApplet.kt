package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.boids.Boid
import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.core.PVector
import kotlin.random.Random


class PApplet : processing.core.PApplet() {

    private val random = Random.Default

    private val clapper = Clapper()

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var waitingForClickToDraw = false

    private var radiusFactorVelocity = 0f

    private var backgroundAlpha = 0.1f

    private var xRotation = 1f

    private var zRotation = 1f

    private var xRotationVelocity = 0.021f

    private var zRotationVelocity = 0.002f

    private var boids = emptyList<Boid>()

    private val numberOfBoids = 1000

    override fun settings() {
        if (FULL_SCREEN) {
            fullScreen(RENDERER, FULL_SCREEN_DISPLAY_ID)
        } else {
            size(WIDTH, HEIGHT, RENDERER)
        }
    }

    override fun setup() {
        colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        clearFrame()
        frameRate(FRAME_RATE.toFloat())
        clapper.start()

        setPerspective()

        initBoids();
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

        drawBoids()

        stroke(1f)

//        translate(width / 2f, height / 2f)
//        updateRotations()
//        updateClapper()

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            ' ' ->
                clapper.tapBpm()
            'x' ->
                clear()
        }
    }

    /*
    Implementations
     */

    private fun initBoids() {
        val width = width.toFloat()
        val height = height.toFloat()
        boids = (0..numberOfBoids).map { initBoid(width, height) }
    }

    private fun initBoid(width: Float, height: Float): Boid {
        var velocity = PVector.random2D()
        velocity.setMag(random(2f, 4f))

        return Boid(
                position = PVector(random(width), random(height)),
                velocity = velocity,
                acceleration = PVector()
        )
    }

    private fun drawBoids() {
        val width = width.toFloat()
        val height = height.toFloat()

        boids.forEach {
            it.edges(width, height)
            it.flock(boids)
            it.update()
            it.show(pApplet = this)
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

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
    }

    private fun drawFrameRate() {
        if (frameCount.toInt() % (FRAME_RATE * 3) == 0) {
            kotlin.io.println(frameRate)
        }
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
            random.maybe(probability = 0.2f) {}
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

        private const val FULL_SCREEN = false

        private const val WIDTH = 1400

        private const val HEIGHT = 900

        private const val RENDERER = PConstants.P3D

        private const val FULL_SCREEN_DISPLAY_ID = 2

        private const val COLOR_MODE = PConstants.HSB

        private const val MAX_COLOR_VALUE = 1f

        private const val FRAME_RATE = 60

        private const val DRAW_BACKGROUND_ON_DRAW = false

        private const val DRAW_FRAME_RATE = true

        private const val MAX_ROTATION_VELOCITY = 0.03f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }

    }
}