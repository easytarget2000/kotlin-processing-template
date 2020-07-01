package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import processing.core.PConstants
import processing.core.PVector
import kotlin.random.Random

class PApplet : processing.core.PApplet() {

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var waitingForClickToDraw = false

    private val random = Random.Default

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
        noCursor()
        lights()
    }

    override fun draw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = 0.1f)
        }

        drawRandomPointsInCircle()

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {}

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
    }

    private var numberOfPointsPerRound = 100

    private var insideCirclePointHue = 0.1f

    private var outsideCirclePointHue = 0.8f

    private var pointBrightness = 1f

    private var pointSaturation = 1f

    private var pointAlpha = 1f

    private fun drawRandomPointsInCircle() {
        val fieldStartX = 0.0
        val fieldStartY = 0.0
        val fieldSize = max(width, height).toDouble()
        val fieldRadius = fieldSize / 2.0
        val fieldCenterX = fieldStartX + (fieldSize / 2.0)
        val fieldCenterY = fieldStartY + (fieldSize / 2.0)

        repeat((0 until numberOfPointsPerRound).count()) {
            drawRandomPoint(fieldSize, fieldRadius, fieldStartX, fieldStartY, fieldCenterX, fieldCenterY)
        }
    }

    private fun drawRandomPoint(
            fieldSize: Double,
            fieldRadius: Double,
            fieldStartX: Double,
            fieldStartY: Double,
            fieldCenterX: Double,
            fieldCenterY: Double
    ): Boolean {
        val pointX = random.nextDouble(fieldStartX, fieldSize)
        val pointY = random.nextDouble(fieldStartY, fieldSize)
        val isInCircle = if (distance(pointX, pointY, fieldCenterX, fieldCenterY) > fieldRadius) {
            stroke(insideCirclePointHue, pointBrightness, pointSaturation, pointAlpha)
            false
        } else {
            stroke(outsideCirclePointHue, pointBrightness, pointSaturation, pointAlpha)
            true
        }

        point(pointX.toFloat(), pointY.toFloat())

        return isInCircle
    }

    companion object {
        private const val CLICK_TO_DRAW = false
        private const val FULL_SCREEN = false
        private const val WIDTH = 800
        private const val HEIGHT = 700
        private const val RENDERER = PConstants.P3D
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        private const val FRAME_RATE = 60f
        private const val DRAW_BACKGROUND_ON_DRAW = false

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }

        private fun distance(x1: Double, y1: Double, x2: Double, y2: Double) =
                kotlin.math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)))
    }
}