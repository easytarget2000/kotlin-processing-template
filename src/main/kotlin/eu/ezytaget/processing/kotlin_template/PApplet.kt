package eu.ezytaget.processing.kotlin_template

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

    private var drawBackgroundOnDraw = true

    private var backgroundAlpha = 0.01f

    private lateinit var starPositions: List<PVector>

    private var numberOfStars = 20000

    private var maxStarRadius = 8f

    private var speed = 20f

    private var radiusFactorVelocity = 0f

    private var xRotation = 1f

    private var zRotation = 1f

    private var xRotationVelocity = 0.021f

    private var zRotationVelocity = 0.002f

    override fun settings() {
        if (FULL_SCREEN) {
            fullScreen(RENDERER, DISPLAY_ID)
        } else {
            size(WIDTH, HEIGHT, RENDERER)
        }
    }

    private val maxStarZ: Float
        get() = max(width, height).toFloat()

    override fun setup() {
        frameRate(FRAME_RATE)
        colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        clearFrame()
        noCursor()
        clapper.bpm = 128f
        clapper.start()

        val starPositions = mutableListOf<PVector>()
        repeat((0 until numberOfStars).count()) {
            starPositions.add(nextRandomPosition())
        }
        this.starPositions = starPositions

        frameRate(FRAME_RATE)
        clapper.start()

        setPerspective()
    }

    override fun draw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (drawBackgroundOnDraw) {
            backgroundDrawer.draw(pApplet = this, alpha = backgroundAlpha)
        }

        val width = width.toFloat()
        val height = height.toFloat()
//        fill(1f)
        translate(width / 2f, height / 2f)
        rotate(frameCount / 1000f)

        starPositions = starPositions.map {
            val z = it.z - speed
            drawStar(it, z, width, height)
            if (z < 100f) {
                nextRandomPosition(randomZ = false)
            } else {
                PVector(
                        it.x,
                        it.y,
                        z
                )
            }

        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = backgroundAlpha)
        }

        if (DRAW_FRAME_RATE) {
            drawFrameRate()
        }

        translate(width / 2f, height / 2f)
        updateRotations()
        updateClapper()

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun mouseClicked() {
        super.mouseClicked()
        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = false
        }
    }

    private fun drawStar(position: PVector, nextZ: Float, width: Float, height: Float) {
//        val brightness = map(nextZ, 0f, maxStarZ, 1f, 0.5f)
        stroke(position.x / position.y, 1f, 1f, 0.5f)

        val depthAdjustedX = map(position.x / position.z, 0f, 1f, 0f, width)
        val depthAdjustedY = map(position.y / position.z, 0f, 1f, 0f, height)
        val depthAdjustedNextX = map(position.x / nextZ, 0f, 1f, 0f, width)
        val depthAdjustedNextY = map(position.y / nextZ, 0f, 1f, 0f, height)
        line(depthAdjustedX, depthAdjustedY, depthAdjustedNextX, depthAdjustedNextY)
    }

    override fun keyPressed() {
        when (key) {
            'x' ->
                clearFrame()
            ' ' ->
                clapper.tapBpm()
        }
    }

    private fun nextRandomPosition(randomZ: Boolean = true) = PVector(
            random(-width / 2f, width / 2f),
            random(-height / 2f, height / 2f),
            if (randomZ) {
                random(maxStarZ)
            } else {
                maxStarZ
            }
    )
    /*
    Implementations
     */


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

        private const val FULL_SCREEN = true

        private const val WIDTH = 1400

        private const val HEIGHT = 900

        private const val RENDERER = PConstants.P3D

        private const val DISPLAY_ID = 2

        private const val COLOR_MODE = PConstants.HSB

        private const val MAX_COLOR_VALUE = 1f

        private const val FRAME_RATE = 60f

        private const val DESIRED_RADIUS_FACTOR = 1f

        private const val RADIUS_FACTOR_TOLERANCE = 0.01f

        private const val RADIUS_FACTOR_PULL = 0.001f

        private const val DRAW_BACKGROUND_ON_DRAW = false

        private const val DRAW_FRAME_RATE = false

        private const val MAX_ROTATION_VELOCITY = 0.03f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }

    }
}