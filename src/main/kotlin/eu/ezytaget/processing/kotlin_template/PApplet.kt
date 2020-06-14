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
            backgroundDrawer.draw(pApplet = this, alpha = 0.01f)
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
        val numberOfVertices = 9
        val numberOfRings = 128
        val ringDepth = smallestScreenSize * 32f
        val patternDepth = numberOfRings * ringDepth

        val tunnelCenterX = width / 2f
        val tunnelCenterY = height / 2f

        noFill()

        val startAngle = millis() / 10000f
        val hue = (millis() % 10_000).toFloat() / 10_000f

        for (ringIndex in 0 until numberOfRings) {
            val progress = ringIndex.toFloat() / numberOfRings.toFloat()
            val ringRadius = (1f - progress) * maxRadius
            val ringX = tunnelCenterX
            val ringY = tunnelCenterY
            val brightness = 1f - progress

            stroke(hue, 1f, brightness, 1f)
            drawPolygon(
                    x = ringX,
                    y = ringY,
                    angle = startAngle + (progress * PConstants.TWO_PI),
                    radius = ringRadius,
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
        private const val FULL_SCREEN = false
        private const val WIDTH = 800
        private const val HEIGHT = 600
        private const val RENDERER = PConstants.P2D
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        private const val FRAME_RATE = 60f
        private const val DRAW_BACKGROUND_ON_DRAW = true

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }
    }
}