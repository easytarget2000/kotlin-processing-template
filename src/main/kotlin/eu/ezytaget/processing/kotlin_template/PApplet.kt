package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import processing.core.PConstants
import processing.core.PVector

class PApplet : processing.core.PApplet() {

    private lateinit var camera: Camera

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
        camera = Camera(
                position = PVector(0f, 0f, (height/2f) / tan(PI * 30f / 18f)),
                focusVector = PVector(0f, 0f, -(height/2f) / tan(PI * 30f / 18f))
        )
        lights()
    }

    override fun draw() {
        camera.adjustAppletCamera(pApplet = this)

        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = 1f)
        }
//
//        noStroke()
//        fill(1f, 1f, 1f, 1f)
//
//        translate(width / 2f, height / 2f, -100f)
//        rotateX(frameCount / 100f * PI / 2f)

//        rect(0f, 0f, 200f, 200f)

        val originLineLength = 100f
        box(originLineLength / 10f)

        stroke(1f, 1f, 1f, 1f)
        line(0f, 0f, 0f, originLineLength, 0f, 0f)
        stroke(0.66f, 1f, 1f, 1f)
        line(0f, 0f, 0f, 0f, originLineLength, 0f)
        stroke(0.33f, 1f, 1f, 1f)
        line(0f, 0f, 0f, 0f, 0f, originLineLength)


        noStroke()
        fill(1f, 1f, 1f, 1f)
        rect(0f, 0f, 200f, 200f)

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            'w' ->
                camera.moveForward()
            'a' ->
                camera.moveLeft()
            'd' ->
                camera.moveRight()
            's' ->
                camera.moveBackward()
            ' ' ->
                camera.moveUp()
        }
    }

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
    }

    companion object {
        private const val CLICK_TO_DRAW = false
        private const val FULL_SCREEN = false
        private const val WIDTH = 800
        private const val HEIGHT = 600
        private const val RENDERER = PConstants.P3D
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