package eu.ezytaget.processing.flowfield

import eu.ezytaget.processing.flowfield.palettes.DuskPalette
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
    private var backgroundAlpha = 1f
    private var xRotation = 1f
    private var zRotation = 1f
    private var xRotationVelocity = 0.021f
    private var zRotationVelocity = 0.002f
    private lateinit var flowField: FlowField
    private lateinit var particles: List<Particle>
    private val particleDrawer = ParticleDrawer()
    private val numberOfParticles = 30_000

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
        frameRate(FRAME_RATE)

        clearFrame()

        initFlowField()
        initParticles()

        setPerspective()

        clapper.bpm = 132f
        clapper.start()
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

        updateRotations()
        updateClapper()

        flowField.update(pApplet = this)

        particles.forEach {
            it.update(endX = width.toFloat(), endY = height.toFloat(), flowField = flowField)
            particleDrawer.draw(it, pApplet = this)
        }

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

    private fun initFlowField() {
        flowField = FlowField(
                width,
                height,
                scale = 40f
        )
    }

    private fun initParticles() {
        val minX = 0f
        val minY = 0f
        val maxX = width.toFloat()
        val maxY = height.toFloat()
        particles = (0 until numberOfParticles).map {
            val particleStartPosition = PVector(
                    random.nextFloat(from = minX, until = maxX),
                    random.nextFloat(from = minY, until = maxY)
            )
            val particleMaxSpeed = random.nextFloat(from = 2f, until = 8f)
            Particle(particleStartPosition, particleMaxSpeed)
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
        pushStyle()
        stroke(1f)
        text(frameRate.toString(), 100f, 100f)
        popStyle()
    }

    private fun updateRotations() {
        val rotationTranslationX = width / 2f
        val rotationTranslationY = height / 2f

        translate(rotationTranslationX, rotationTranslationY)
        zRotation += zRotationVelocity
        rotateZ(zRotation)
        xRotation += xRotationVelocity
        rotateX(xRotation)
        translate(-rotationTranslationX, -rotationTranslationY)
    }

    private fun updateClapper() {
        val clapperResult = clapper.update()

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            particleDrawer.update()
        }

        if (clapperResult[BeatInterval.TwoWhole]?.didChange == true) {
            random.maybe(probability = 0.2f) {
                initFlowField()
            }
            random.maybe(probability = 0.2f) {
                initParticles()
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
            random.maybe(probability = 0.5f) {
                clearFrame()
            }
        }

        if (clapperResult[BeatInterval.SixteenWhole]?.didChange == true) {
            background(0)
        }
    }

    private fun setRandomBackgroundAlpha() {
        if (!random.maybe { backgroundAlpha = random(MAX_COLOR_VALUE / 64f) }) {
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
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        private const val FRAME_RATE = 60f
        private const val DRAW_BACKGROUND_ON_DRAW = false
        private const val DRAW_FRAME_RATE = false
        private const val MAX_ROTATION_VELOCITY = 0.03f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }
    }
}