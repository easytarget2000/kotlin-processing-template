package eu.ezytaget.processing.reaction_diffusion

import eu.ezytaget.processing.reaction_diffusion.palettes.DuskPalette
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
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

    private var dA = 1.0f
    private var dB = 0.5f
    private var feed = 0.055f
    private var k = 0.062f

    private lateinit var grid: Array<Array<Cell>>
    private lateinit var prev: Array<Array<Cell>>

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

        initGrid()
    }

    private fun initGrid() {
        grid = (0 until width).map {
            (0 until height).map {
                Cell(a = 1f, b = 0f)
            }.toTypedArray()
        }.toTypedArray()

        repeat((0 until 10).count()) {
            val startX = random.nextInt(from = 20, until = width - 20)
            val startY = random.nextInt(from = 20, until = height - 20)

            (startX until startX + 10).forEach {column ->
                (startY until startY + 10).forEach { row ->
                    grid[column][row] = Cell(a = 1f, b = 1f)
                }
            }
        }

        prev = grid.copyOf()
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
//        updateClapper()

        for (i in 0..10) {
            update()
            swap()
        }

        loadPixels()
        for (i in 1 until width - 1) {
            for (j in 1 until height - 1) {
                val (a, b) = grid[i][j]
                val pos = i + j * width
                pixels[pos] = color((a - b) * 255)
            }
        }
        updatePixels()

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    private fun update() {
        for (i in 1 until width - 1) {
            for (j in 1 until height - 1) {
                val (a, b) = prev.get(i).get(j)
                val newspot: Cell = grid.get(i).get(j)
                var laplaceA = 0f
                laplaceA += a * -1f
                laplaceA += prev.get(i + 1).get(j).a * 0.2f
                laplaceA += prev.get(i - 1).get(j).a * 0.2f
                laplaceA += prev.get(i).get(j + 1).a * 0.2f
                laplaceA += prev.get(i).get(j - 1).a * 0.2f
                laplaceA += prev.get(i - 1).get(j - 1).a * 0.05f
                laplaceA += prev.get(i + 1).get(j - 1).a * 0.05f
                laplaceA += prev.get(i - 1).get(j + 1).a * 0.05f
                laplaceA += prev.get(i + 1).get(j + 1).a * 0.05f
                var laplaceB = 0f
                laplaceB += b * -1
                laplaceB += prev.get(i + 1).get(j).b * 0.2f
                laplaceB += prev.get(i - 1).get(j).b * 0.2f
                laplaceB += prev.get(i).get(j + 1).b * 0.2f
                laplaceB += prev.get(i).get(j - 1).b * 0.2f
                laplaceB += prev.get(i - 1).get(j - 1).b * 0.05f
                laplaceB += prev.get(i + 1).get(j - 1).b * 0.05f
                laplaceB += prev.get(i - 1).get(j + 1).b * 0.05f
                laplaceB += prev.get(i + 1).get(j + 1).b * 0.05f
                newspot.a = a + (dA * laplaceA - a * b * b + feed * (1 - a)) * 1f
                newspot.b = b + (dB * laplaceB + a * b * b - (k + feed) * b) * 1f
                newspot.a = constrain(newspot.a, 0f, 1f)
                newspot.b = constrain(newspot.b, 0f, 1f)
            }
        }
    }

    fun swap() {
        val temp: Array<Array<Cell>> = prev
        prev = grid
        grid = temp
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

    private fun updateClapper() {
        val clapperResult = clapper.update()

        randomSeed(System.currentTimeMillis())

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
        }

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            random.maybe(probability = 0.9f) {
                bounce()
            }
        }

        if (clapperResult[BeatInterval.TwoWhole]?.didChange == true) {
            random.maybe(probability = 0.2f) {}
            random.maybe {
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