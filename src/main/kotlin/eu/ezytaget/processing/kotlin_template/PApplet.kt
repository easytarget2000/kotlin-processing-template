package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytaget.processing.kotlin_template.realms.camera.CameraRealm
import eu.ezytaget.processing.kotlin_template.realms.stripes.StripesRealm
import eu.ezytaget.processing.kotlin_template.realms.vortex_monster.VortexMonster
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.video.Capture
import kotlin.random.Random

class PApplet : processing.core.PApplet() {

    private val random = Random.Default

    private val clapper = Clapper()

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var waitingForClickToDraw = false

    private var radiusFactorVelocity = 0f

    private var backgroundAlpha = 1f

    private var xRotation = 1f

    private var zRotation = 1f

    private var xRotationVelocity = 0.0021f

    private var zRotationVelocity = 0.002f

    private var cameraRealm: CameraRealm? = null

    private var stripesRealm = StripesRealm()

    override fun settings() {
        if (FULL_SCREEN) {
            fullScreen(RENDERER, DISPLAY_ID)
        } else {
            size(WIDTH, HEIGHT, RENDERER)
        }
    }

    override fun setup() {
        frameRate(FRAME_RATE)
        colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        smooth()

        clearFrame()
        clapper.start()

        setPerspective()

        cameraRealm?.setCaptureAndStart(pApplet = this)

        randomSeed(System.currentTimeMillis())
    }

    private var numberOfIterationsPerFrame = 10

    override fun draw() {
        (0 .. numberOfIterationsPerFrame).forEach { _ ->
            push()
            iterateDraw()
            pop()
        }
    }

    private fun iterateDraw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = backgroundAlpha)
        }

        if (DRAW_FRAME_RATE) {
            drawFrameRate()
        }

        cameraRealm?.drawIn(pApplet = this)
        stripesRealm.draw(pApplet = this)

        translate(width / 2f, height / 2f)
        updateRotations()
        updateClapper()

//        drawSample()

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            ' ' ->
                clapper.tapBpm()
            'x' ->
                clearFrame()
        }
    }

    /*
    Implementations
     */

    private fun drawSample() {
        noFill()
        val hue = (frameCount % 1000) / 1000f
        val saturation = (frameCount % 3000) / 3000f
        val brightness = MAX_COLOR_VALUE * 1f //((frameCount % 6000) / 6000f)
        val alpha = 0.1f * MAX_COLOR_VALUE
        stroke(hue, saturation, brightness, alpha)

        val boxSize = min(width, height) * 0.5f
        box(boxSize)
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
        zRotation += zRotationVelocity
        rotateZ(zRotation)
        xRotation += xRotationVelocity
        rotateX(xRotation)
    }

    private fun updateClapper() {
        val clapperResult = clapper.update()

//        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
//            random.maybe(probability = 0.9f) {
//                bounce()
//            }
//        }

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
//            random.maybe(probability = 0.2f) {}
            random.maybe(probability = 0.5f) {
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

        private const val WIDTH = 600

        private const val HEIGHT = 600

        private const val RENDERER = PConstants.P3D

        private const val DISPLAY_ID = 2

        private const val COLOR_MODE = PConstants.HSB

        private const val MAX_COLOR_VALUE = 1f

        private const val FRAME_RATE = 120f

        private const val DRAW_BACKGROUND_ON_DRAW = false

        private const val DRAW_FRAME_RATE = false

        private const val MAX_ROTATION_VELOCITY = 0.03f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }

    }
}