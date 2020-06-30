package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.core.PVector

class PApplet : processing.core.PApplet() {

    private val clapper = Clapper()

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var waitingForClickToDraw = false

    private var drawBackgroundOnDraw = true

    private var backgroundAlpha = 1f

    private lateinit var starPositions: List<PVector>

    private var numberOfStars = 700

    private var maxStarRadius = 8f

    private var speed = 40f

    override fun settings() {
        if (FULL_SCREEN) {
            fullScreen(RENDERER)
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
        lights()
        clapper.bpm = 128f
        clapper.start()

        val starPositions = mutableListOf<PVector>()
        repeat((0 until numberOfStars).count()) {
            starPositions.add(nextRandomPosition())
        }
        this.starPositions = starPositions
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
        stroke(0.3f, 1f, 1f, 0.5f)
        fill(1f)
        translate(width / 2f, height / 2f)
        starPositions = starPositions.map {
            drawStar(it, width, height)
            val z = it.z - speed
            if (z < 1f) {
                nextRandomPosition(randomZ = false)
            } else {
                PVector(
                        it.x,
                        it.y,
                        z
                )
            }

        }

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    private fun drawStar(position: PVector, width: Float, height: Float) {
        val depthAdjustedX = map(position.x / position.z, 0f, 1f, 0f, width)
        val depthAdjustedY = map(position.y / position.z, 0f, 1f, 0f, height)
        val radius = map(position.z, 0f, maxStarZ, maxStarRadius, 0f)
        ellipse(depthAdjustedX, depthAdjustedY, radius, radius)
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

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
    }

    companion object {
        private const val CLICK_TO_DRAW = true
        private const val FULL_SCREEN = true
        private const val WIDTH = 1400
        private const val HEIGHT = 900
        private const val RENDERER = PConstants.P2D
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        private const val FRAME_RATE = 60f
        private const val DESIRED_RADIUS_FACTOR = 1f
        private const val RADIUS_FACTOR_TOLERANCE = 0.01f
        private const val RADIUS_FACTOR_PULL = 0.001f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }
    }
}