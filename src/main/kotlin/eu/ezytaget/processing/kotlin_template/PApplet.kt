package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.GlobalConstants.N
import eu.ezytaget.processing.kotlin_template.GlobalConstants.SCALE
import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.core.PVector

class PApplet : processing.core.PApplet() {

    private val clapper = Clapper()
    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)
    private var waitingForClickToDraw = false
    private var backgroundAlpha = 0.1f
    private var xRotationVelocity = 0.01f
    private var zRotationVelocity = 0.02f

    private var t = 0f
    private lateinit var fluidField: FluidField

    override fun settings() {
        size(N * SCALE, N * SCALE)
    }

    override fun setup() {
        frameRate(FRAME_RATE)
//        colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        clearFrame()
        noCursor()
//        lights()

        clapper.bpm = INITIAL_BPM
        clapper.start()

        initFluidField()
    }

    override fun draw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = backgroundAlpha)
        }

        updateAndDrawFluidField()


        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            ' ' ->
                clapper.tapBpm()
        }
    }

    /*
    Implementations
     */

    private fun initFluidField() {
        fluidField = FluidField(dt = 0.2f, diffusion = 0f, viscosity = 0.0000001f)
    }

    private fun setPerspective() {
        val cameraZ = ((height / 2f) / tan(PI * 60f / 360f))
        perspective(
                PI / 3f,
                width.toFloat() / height.toFloat(),
                cameraZ / 10f,
                cameraZ * 30f
        )
    }

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
    }

    private fun updateClapper() {
    }

    private fun updateAndDrawFluidField() {
        val cx = (0.5f * width / SCALE).toInt()
        val cy = (0.5f * height / SCALE).toInt()

        (-1..1).forEach { i ->
            (-1..1).forEach { j ->
                fluidField.addDensity(cx + i, cy + j, random(50f, 150f));
            }
        }

        repeat((0 until 2).count()) {
            val angle = noise(t) * TWO_PI * 2;
            val v = PVector.fromAngle(angle);
            v.mult(0.2f);
            t += 0.01f;
            fluidField.addVelocity(cx, cy, v.x, v.y);
        }

        fluidField.step(this)
        fluidField.renderD(this)
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
        private const val INITIAL_BPM = 140f
        private const val WIDTH = 1400
        private const val HEIGHT = 900
        private const val RENDERER = PConstants.P3D
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        private const val FRAME_RATE = 60f
        private const val DRAW_BACKGROUND_ON_DRAW = true
        private const val DESIRED_RADIUS_FACTOR = 1f
        private const val RADIUS_FACTOR_TOLERANCE = 0.01f
        private const val RADIUS_FACTOR_PULL = 0.01f
        private const val MAX_ROTATION_VELOCITY = 0.03f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }
    }
}