package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytaget.processing.kotlin_template.tesseract.MatrixCalculator.matmul
import eu.ezytaget.processing.kotlin_template.tesseract.MatrixCalculator.matmul4
import eu.ezytaget.processing.kotlin_template.tesseract.P4Vector
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
    private var radiusFactorVelocity = 0f
    private var backgroundAlpha = 1f
    private var xRotation = 1f
    private var zRotation = 1f
    private var xRotationVelocity = 0.021f
    private var zRotationVelocity = 0.002f
    private var automatonUpdateDelay = 16

    private var points = arrayOfNulls<P4Vector>(16)
    private var angle = 0f

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
        frameRate(FRAME_RATE)
        clapper.start()
        setPerspective()

        points[0] = P4Vector(-1f, -1f, -1f, 1f)
        points[1] = P4Vector(1f, -1f, -1f, 1f)
        points[2] = P4Vector(1f, 1f, -1f, 1f)
        points[3] = P4Vector(-1f, 1f, -1f, 1f)
        points[4] = P4Vector(-1f, -1f, 1f, 1f)
        points[5] = P4Vector(1f, -1f, 1f, 1f)
        points[6] = P4Vector(1f, 1f, 1f, 1f)
        points[7] = P4Vector(-1f, 1f, 1f, 1f)
        points[8] = P4Vector(-1f, -1f, -1f, -1f)
        points[9] = P4Vector(1f, -1f, -1f, -1f)
        points[10] = P4Vector(1f, 1f, -1f, -1f)
        points[11] = P4Vector(-1f, 1f, -1f, -1f)
        points[12] = P4Vector(-1f, -1f, 1f, -1f)
        points[13] = P4Vector(1f, -1f, 1f, -1f)
        points[14] = P4Vector(1f, 1f, 1f, -1f)
        points[15] = P4Vector(-1f, 1f, 1f, -1f)
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

//        translate(width / 2f, height / 2f)
//        updateRotations()
        updateClapper()
        drawTesseract()

        lights()

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    private fun drawTesseract() {
        background(0)
        translate(width / 2.toFloat(), height / 2.toFloat())
        rotateX(-PConstants.PI / 2)



        val projected3d = points.mapIndexed { index, v ->
            val v = v!!

            val rotationXY = arrayOf(
                    floatArrayOf(cos(angle), -sin(angle), 0f, 0f),
                    floatArrayOf(sin(angle), cos(angle), 0f, 0f),
                    floatArrayOf(0f, 0f, 1f, 0f),
                    floatArrayOf(0f, 0f, 0f, 1f)
            )
            val rotationZW = arrayOf(
                    floatArrayOf(1f, 0f, 0f, 0f),
                    floatArrayOf(0f, 1f, 0f, 0f),
                    floatArrayOf(0f, 0f, cos(angle), -sin(angle)),
                    floatArrayOf(0f, 0f, sin(angle), cos(angle))
            )

            var rotated: P4Vector = matmul4(rotationXY, v)
            rotated = matmul4(rotationZW, rotated)
            val distance = 2f
            val w = 1 / (distance - rotated.w)
            val projection = arrayOf(floatArrayOf(w, 0f, 0f, 0f), floatArrayOf(0f, w, 0f, 0f), floatArrayOf(0f, 0f, w, 0f))
            val projected: PVector = matmul(projection, rotated)
            projected.mult(width / 8.toFloat())
            stroke(1f, 1f)
            strokeWeight(32f)
            noFill()
            point(projected.x, projected.y, projected.z)

            projected
        }

        // Connecting
        for (i in 0..3) {
            connect(0, i, (i + 1) % 4, projected3d)
            connect(0, i + 4, (i + 1) % 4 + 4, projected3d)
            connect(0, i, i + 4, projected3d)
        }
        for (i in 0..3) {
            connect(8, i, (i + 1) % 4, projected3d)
            connect(8, i + 4, (i + 1) % 4 + 4, projected3d)
            connect(8, i, i + 4, projected3d)
        }
        for (i in 0..7) {
            connect(0, i, i + 8, projected3d)
        }

        //angle = map(mouseX, 0, width, 0, TWO_PI);
        angle += 0.02f
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

    private fun connect(offset: Int, i: Int, j: Int, points: List<PVector>) {
        val a = points[i + offset]
        val b = points[j + offset]
        strokeWeight(4f)
        stroke(1f)
        line(a.x, a.y, a.z, b.x, b.y, b.z)
    }

    private fun updateClapper() {
        val clapperResult = clapper.update()

        randomSeed(System.currentTimeMillis())

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
        }

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            maybe(probability = 0.9f) {
                bounce()
            }
        }

        if (clapperResult[BeatInterval.TwoWhole]?.didChange == true) {
            maybe(probability = 0.2f) {
            }
            maybe {
                clearFrame()
            }
            maybe {
                setRandomBackgroundAlpha()
            }
            maybe {
                setRandomXRotationVelocity()
            }
            maybe {
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
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        private const val FRAME_RATE = 60f
        private const val DRAW_BACKGROUND_ON_DRAW = false
        private const val DRAW_FRAME_RATE = false
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