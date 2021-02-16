package eu.ezytaget.processing.julia_set_fractals

import eu.ezytaget.processing.julia_set_fractals.palettes.DuskPalette
import eu.ezytaget.processing.kotlin_template.char_raster.CharRaster
import eu.ezytaget.processing.kotlin_template.maybe
import eu.ezytaget.processing.kotlin_template.nextFloat
import eu.ezytaget.processing.kotlin_template.realms.Realm
import eu.ezytaget.processing.kotlin_template.realms.camera.CameraRealm
import eu.ezytaget.processing.kotlin_template.realms.julia_set.JuliaSet
import eu.ezytaget.processing.kotlin_template.realms.julia_set.JuliaSetDrawer
import eu.ezytaget.processing.kotlin_template.realms.stripes.StripesRealm
import eu.ezytaget.processing.kotlin_template.realms.triangle_floor.TriangleFloor
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.event.MouseEvent
import processing.core.PGraphics
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

    private var raster: CharRaster? = CharRaster()

    private var clearFrameOnTextSizeFinding = false

    private var automatonUpdateDelay = 16

    private var realms = emptyList<Realm>()

    private lateinit var juliaSet: JuliaSet
    
    private val juliaSetDrawer = JuliaSetDrawer()
    private lateinit var kaleidoscope: PGraphics

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
        frameRate(FRAME_RATE)

        kaleidoscope = createGraphics(width, height, RENDERER)
        kaleidoscope.beginDraw()
        kaleidoscope.colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        kaleidoscope.endDraw()

        initJuliaSet(pGraphics = kaleidoscope)

        setPerspective()

        raster?.setup(pApplet = this)

        realms.forEach {
            it.setup(pApplet = this)
        }

        randomSeed(System.currentTimeMillis())
    }

    private var numberOfIterationsPerFrame = 10

    override fun draw() {
        (0 .. numberOfIterationsPerFrame).forEach { _ ->
            push()
            iterateDraw()
            pop()
        }

//        raster?.drawIn(pApplet = this)
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


        juliaSet.update()

        kaleidoscope.beginDraw()
        kaleidoscope.clear()
        juliaSetDrawer.draw(juliaSet, pGraphics = kaleidoscope)
        kaleidoscope.endDraw()

        pushStyle()
        val numberOfKaleidoscopeEdges = 9
        repeat(numberOfKaleidoscopeEdges) {
            pushMatrix()
            translate(width / 2f, height / 2f)
            rotate((it / numberOfKaleidoscopeEdges.toFloat()) * PConstants.TWO_PI)
            image(kaleidoscope, -100f, -kaleidoscope.height / 2f)
            popMatrix()
        }
        popStyle()

        updateClapper()

        loadPixels()
        pixels.forEachIndexed { index, pixelValue ->
            if (index + 10 == pixels.size) {
                return
            }
            val neighborValue = pixels[index + 10]
            pixels[index] = pixelValue - neighborValue
        }
        updatePixels()

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

    override fun mouseClicked(event: MouseEvent?) {
        super.mouseClicked(event)
        if (event == null) {
            return
        }

        realms.forEach{
            it.handleMouseClick(event.button, event.x, event.y, pApplet = this)
        }

        setTextSize(relativeTextSizeValue = event.x.toFloat())
    }

    /*
    Implementations
     */

    private fun initJuliaSet(pGraphics: PGraphics) {
        //initJuliaSet(): scaleWidth: 5.711875, scaleHeight: 3.569922, angle 4.8934402, angleVelocity: 0.05238408, aAngleFactor: -0.7362766
        //initJuliaSet(): scaleWidth: 3.7624247, scaleHeight: 2.3515155, angle 2.9225054, angleVelocity: 0.05977559, aAngleFactor: 0.59833264
        //initJuliaSet(): scaleWidth: 2.4918487, scaleHeight: 1.5574055, angle 5.00991, angleVelocity: -0.31708914, aAngleFactor: -0.54648113
        // NO: initJuliaSet(): scaleWidth: 4.356219, scaleHeight: 2.7226367, angle 5.680613, angleVelocity: 0.0038700998, aAngleFactor: 0.030716658
        val width = pGraphics.width
        val height = pGraphics.height

        val scaleWidth = random.nextFloat(from = 2.5f, until = 7f) // 5.5, 5.13, 2.23
        val scaleHeight = (scaleWidth * height.toFloat()) / width.toFloat() // 3.4, 3.21, 1.39
        val angle = random.nextFloat(from = 3f, until = PConstants.TWO_PI) // 0.5, 6.2, 4.19
        val angleVelocity = random.nextFloat(from = -0.05f, until = 0.05f) // 0.09, -0.29, -0.28
        val aAngleFactor = random.nextFloat(from = 1f, until = 2f) // 1.69, 0.8, -1.35

        juliaSet = JuliaSet(
                scaleWidth = scaleWidth,
                scaleHeight = scaleHeight,
                angle = angle,
                angleVelocity = angleVelocity,
                aAngleFactor = aAngleFactor
        )

        println(
                "initJuliaSet(): scaleWidth: $scaleWidth, scaleHeight: $scaleHeight, angle $angle, " +
                        "angleVelocity: $angleVelocity, aAngleFactor: $aAngleFactor"
        )
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

    private fun clearAll() {
        initJuliaSet(pGraphics = kaleidoscope)
        clearFrame()
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
            random.maybe(probability = 0.2f) {
                initJuliaSet(pGraphics = kaleidoscope)
            }
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

        if (clapperResult[BeatInterval.EightWhole]?.didChange == true) {
            clearAll()
        }
    }

    private fun bounce() {
        radiusFactorVelocity = 0.05f
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

    private fun setTextSize(relativeTextSizeValue: Float) {
        val raster = raster ?: return

        if (clearFrameOnTextSizeFinding) {
            background(0.1f, 1f, 1f, 1f)
        }

        val textSize = map(relativeTextSizeValue, 0f, width.toFloat(), 4f, 32f)

        raster.setTextSize(pApplet = this, textSize = textSize)
    }

    companion object {

        private const val CLICK_TO_DRAW = false

        private const val FULL_SCREEN = true

        private const val WIDTH = 800

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