package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import processing.core.PConstants
import processing.core.PVector

class PApplet : processing.core.PApplet() {

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var waitingForClickToDraw = false

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
            backgroundDrawer.draw(pApplet = this, alpha = 1f)
        }

        drawTunnel()


        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
        }
    }

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
    }

    var currentTunnelSegment = PVector(0f, 0f, 0f)
    lateinit var nextTunnelSegment: PVector

    private fun drawTunnel() {
        pushStyle()

        val smallestScreenSize = min(width, height)
        val maxRadius = smallestScreenSize
        val numberOfVertices = 6
        val numberOfRings = 32
        val ringDepth = smallestScreenSize * 32f
        val patternDepth = numberOfRings * ringDepth

        val tunnelCenterX = width / 2f
        val tunnelCenterY = height / 2f

        noFill()

        val startAngle = millis() / 10_000f
        val hue = (millis() % 10_000).toFloat() / 10_000f
        val radiusOffset = -((millis() % 10_000).toFloat() / 10_000f) * smallestScreenSize
        val minBrightness = 0.4f

        for (ringIndex in 0 until numberOfRings) {
            val progress = ringIndex.toFloat() / numberOfRings.toFloat()
            val brightness = minBrightness + ((1f - minBrightness) * (1f - progress))
            val ringX = tunnelCenterX
            val ringY = tunnelCenterY
            val ringAngle = startAngle + (progress * PConstants.TWO_PI * 4f)
            val ringRadius = (1f - progress) * maxRadius

            stroke(hue, 1f, brightness, 1f)
            drawPolygon(
                    x = ringX,
                    y = ringY,
                    angle = ringAngle,
                    radius = ringRadius + radiusOffset,
                    numberOfVertices = numberOfVertices
            )
        }

        popStyle()
    }

    private fun drawPolygon(x: Float, y: Float, angle: Float, radius: Float, numberOfVertices: Int) {
        val deltaAngle = PConstants.TWO_PI / numberOfVertices.toFloat()
        beginShape()
        for (vertexIndex in 0 until numberOfVertices) {
            val vertexAngle = (deltaAngle * vertexIndex) + angle
            val sx = x + (cos(vertexAngle) * radius)
            val sy = y + (sin(vertexAngle) * radius)
            vertex(sx, sy)
        }
        endShape(PConstants.CLOSE)
    }

    companion object {
        private const val CLICK_TO_DRAW = false
        private const val FULL_SCREEN = true
        private const val WIDTH = 1400
        private const val HEIGHT = 900
        private const val RENDERER = PConstants.P2D
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        private const val FRAME_RATE = 60f
        private const val DRAW_BACKGROUND_ON_DRAW = false

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }
    }
}