package eu.ezytarget.processingtemplate

import CathodeRayer
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import eu.ezytarget.processingtemplate.palettes.DuskPalette
import eu.ezytarget.processingtemplate.layers.Layer
import eu.ezytarget.processingtemplate.layers.testimage.TestImageLayer
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.core.PGraphics
import kotlin.random.Random

internal class MainApplet(
    val random: Random = Random.Default
) : PApplet() {
    private lateinit var kaleidoscope: PGraphics
    private var numberOfKaleidoscopeEdges = 1
        set(value) {
            field = value
            println("PApplet: numberOfKaleidoscopeEdges: set: $numberOfKaleidoscopeEdges")
        }
    private var minNumberOfKaleidoscopeEdges = 1
    private var maxNumberOfKaleidoscopeEdges = 6

    private var layers = mutableListOf<Layer>(CathodeRayer())

    private val clapper = Clapper().also {
        it.bpm = 133F
    }

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)
    private var backgroundAlpha = 0.1f

    private var interestingBpm = clapper.bpm
    private lateinit var bpmFont: PFont
    private var showBpmFrameCount = 0
    private var lastUpdateTimestamp = now()
    private var clearBackgroundOnDraw = true
    private var drawCameraCapture = false
    private var rotationAngle = 0f
    private var drawTestImage = false
    private val testImageLayer = TestImageLayer()
    private val captureManager = CameraCaptureManager()
    private var numberOfIterationsPerFrame = 1

    public override fun runSketch() {
        super.runSketch()
    }

    override fun settings() {
//        size(800, 600, P2D)
        fullScreen(RENDERER, 2)
    }

    override fun setup() {
        this.colorMode(COLOR_MODE, MAX_COLOR_VALUE)

        this.kaleidoscope = createGraphics(width, height, RENDERER)
        this.kaleidoscope.beginDraw()
        this.kaleidoscope.colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        this.kaleidoscope.endDraw()

        this.lastUpdateTimestamp = now()
        this.initLayers()
        this.clapper.start()
        this.captureManager.startCapture(pApplet = this, qualifier = "webcam")

        val fonts = PFont.list()
        val bpmFontName = fonts.first {
            it.contains("mono", ignoreCase = true)
        } ?: fonts.first()
        bpmFont = createFont(bpmFontName, 1000f, true)
        this.background(0F)
    }

    override fun draw() {
        if (drawTestImage) {
            clearBackground()

            graphics.push()
            testImageLayer.draw(graphics)
            graphics.pop()
            return
        }

        if (this.clearBackgroundOnDraw) {
            this.clearBackground()
        }
        this.update()

        if (this.drawCameraCapture && this.frameCount % 4 == 0) {
            this.captureManager.read()?.let {
                this.graphics.image(
                    it,
                    0F,
                    0F,
                    this.graphics.width.toFloat(),
                    this.graphics.height.toFloat()
                )
            }
        }

        this.kaleidoscope.beginDraw()
        //        realmsManager.drawIn(pGraphics = kaleidoscope, frameCount = frameCount)
        this.layers.forEach { layer ->
            this.kaleidoscope.push()
            layer.draw(this.kaleidoscope)
            this.kaleidoscope.pop()
        }
        this.kaleidoscope.endDraw()

        if (this.numberOfKaleidoscopeEdges <= 1) {
            image(this.kaleidoscope, 0f, 0f)
        } else {
            push()
            repeat(this.numberOfKaleidoscopeEdges) {
                pushMatrix()
                translate(this.width / 2f, this.height / 2f)
                rotate(
                    (it / this.numberOfKaleidoscopeEdges.toFloat())
                            * PConstants.TWO_PI
                )
                image(
                    this.kaleidoscope,
                    -100f,
                    -this.kaleidoscope.height / 2f
                )
                popMatrix()
            }
            pop()
        }
    }

    override fun keyPressed() {
        when (val key = this.key) {
            '1' -> setLayersIntensity(Layer.Intensity.LOW)
            '2' -> setLayersIntensity(Layer.Intensity.MEDIUM)
            '3' -> setLayersIntensity(Layer.Intensity.HIGH)
            'b' -> toggleClearBackgroundOnDraw()
            ' ' -> clapper.tapBpm()
            else -> println("keyPressed(): Unhandled key = $key")
        }
    }

    private fun initLayers() {
        layers = mutableListOf(
//            PaddedGridLayerFactory.next(random),
//            PaddedGridLayerFactory.next(random),
//            TestImageLayer(),
//            CathodeRayer(),
//            GrainyGridLayerFactory.next(random),
//            CathodeRayer(),
            )
    }

    private fun setLayersIntensity(intensity: Layer.Intensity) {
        println("setLayersIntensity(): intensity = $intensity")
        layers.forEach { it.intensity = intensity }
    }

    private fun clearBackground() {
        this.backgroundDrawer.draw(pApplet = this, alpha = this.backgroundAlpha)
//            backgroundDrawer.draw(kaleidoscope, alpha = this.backgroundAlpha)

        this.kaleidoscope.beginDraw()
        this.kaleidoscope.clear()
        this.kaleidoscope.endDraw()
//        this.graphics.pushStyle()
//        this.graphics.fill(
//            this.clearBackgroundColor.hue,
//            this.clearBackgroundColor.saturation,
//            this.clearBackgroundColor.brightness,
//            this.clearBackgroundColor.alpha,
//        )
//        this.graphics.rect(
//            0F,
//            0F,
//            this.graphics.width.toFloat(),
//            this.graphics.height.toFloat()
//        )
//        this.graphics.popStyle()
    }

    private fun update() {
        val deltaTime = now() - lastUpdateTimestamp
        lastUpdateTimestamp = now()

        updateClapper()

        layers.forEach {
            it.update(deltaTime)
        }
        testImageLayer.update(deltaTime)
    }

    private fun updateClapper() {
        val result = clapper.update()

        layers.forEach { it.update(result) }

        if (result[BeatInterval.Quarter]?.didChange == true) {
//            toggleClearBackgroundOnDraw()
            testImageLayer.drawCircle = !testImageLayer.drawCircle

            random.maybe(0.1f) {
                setLayersIntensity(Layer.Intensity.MEDIUM)
            }
            random.maybe(0.1f) {
                setLayersIntensity(Layer.Intensity.LOW)
            }
            random.maybe(0.1f) {
                setLayersIntensity(Layer.Intensity.HIGH)
            }

            random.maybe(0.1F) {
                this.graphics.background(1F)
            }
        }

        if (result[BeatInterval.Whole]?.didChange == true) {
            initLayers()
        }
    }

    private fun randomAngle() = random.nextFloat() * TAU

    private fun toggleClearBackgroundOnDraw() {
        clearBackgroundOnDraw = !clearBackgroundOnDraw
    }

    private fun requestShowBpm() {
        showBpmFrameCount = SHOW_BPM_FRAME_DURATION
    }

    private fun showBpmIfRequested() {
        if (showBpmFrameCount <= 0) {
            interestingBpm = clapper.bpm + random.nextFloatInRange(-1f, 1f)
            return
        }

        val formattedBpm = "%.1f".format(interestingBpm)
        val textSize = width.coerceAtLeast(height) * 0.3f
        val alpha =
            showBpmFrameCount.toFloat() / SHOW_BPM_FRAME_DURATION.toFloat()

        textFont(bpmFont)
        textAlign(CENTER)
        textSize(textSize)
        noStroke()
        fill(0f, alpha)

        text(formattedBpm, width / 2f, height * 0.7f)

        --showBpmFrameCount
    }

    companion object {
        private const val RENDERER = PConstants.P3D
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        const val SHOW_BPM_FRAME_DURATION = 10

        private fun now() = System.currentTimeMillis()
    }
}
