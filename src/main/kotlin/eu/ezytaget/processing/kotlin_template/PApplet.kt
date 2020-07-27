package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.cell_automaton_3d.CellAutomaton3D
import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants

class PApplet : processing.core.PApplet() {

    private val clapper = Clapper()
    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)
    private var waitingForClickToDraw = false
    private var radiusFactorVelocity = 0f
    private var backgroundAlpha = 1f
    private var xRotation = 1f
    private var zRotation = 1f
    private var xRotationVelocity = 0.021f
    private var zRotationVelocity = 0.002f
    private lateinit var cellAutomaton3D: CellAutomaton3D
    private var automatonUpdateDelay = 16

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
        initAutomaton()

        setPerspective()
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

        translate(width / 2f, height / 2f)
        updateRotations()
        updateClapper()

        if (frameCount % automatonUpdateDelay == 0) {
            cellAutomaton3D.update()
        }

        lights()
        cellAutomaton3D.draw(pApplet = this)

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            'x' ->
                initAutomaton()
            ' ' ->
                clapper.tapBpm()
        }
    }

    /*
    Implementations
     */

    private fun initAutomaton() {
        val automatonSize = min(width, height) * 0.9f
        cellAutomaton3D = CellAutomaton3D(
                numOfCellsPerSide = random(24f, 48f).toInt(),
                sideLength = automatonSize
        )
        automatonUpdateDelay = random(8f, 32f).toInt()
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

        randomSeed(System.currentTimeMillis())

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            maybe(probability = 0.9f) {
                bounce()
            }
        }

        if (clapperResult[BeatInterval.TwoWhole]?.didChange == true) {
            maybe(probability = 0.2f) {
                initAutomaton()
            }
            maybe() {
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
        private const val FULL_SCREEN = false
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