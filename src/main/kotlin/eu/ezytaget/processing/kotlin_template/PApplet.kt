package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import processing.core.PConstants

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
            backgroundDrawer.draw(pApplet = this, alpha = 0.1f)
        }

        val lineLength = height / 4f

        stroke(0f, 0f, 1f, 0.1f)

        repeat((0 .. 1024).count()) {
            bentLine(random(width.toFloat()), random(height.toFloat()), lineLength)
        }

        stroke(0.5f, 1f, 1f, 1f)
        bentLine(mouseX.toFloat(), mouseY.toFloat(), lineLength)


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

    private fun bentLine(centerX: Float, centerY: Float, zeroLength: Float) {
        pushMatrix()

        val relativeDistanceToXCenter = (centerX - (width / 2f)) / (width / 2f)
        val normalizedDistanceToXCenter = abs(relativeDistanceToXCenter)
        val length = zeroLength + ((height - zeroLength) * normalizedDistanceToXCenter)

        val relativeDistanceToYCenter = (centerY - (height / 2f)) / (height / 2f)
        val rotationSign = relativeDistanceToXCenter
        translate(centerX, centerY)
        rotate(relativeDistanceToYCenter * relativeDistanceToXCenter)
        translate(0f,- (length / 2f))
        line(
                0f,
                0f,
                0f,
                length
        )
        popMatrix()
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
        private const val DRAW_BACKGROUND_ON_DRAW = true

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }
    }
}