package eu.ezytarget.processingtemplate

import CathodeRayer
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import eu.ezytarget.processingtemplate.layers.Layer
import eu.ezytarget.processingtemplate.layers.paddedgrid.PaddedGridLayerFactory
import eu.ezytarget.processingtemplate.layers.testimage.TestImageLayer
import eu.ezytarget.processingtemplate.layers.tiles.TileLayer
import processing.core.PApplet
import processing.core.PFont
import processing.video.Capture
import kotlin.random.Random

internal class MainApplet(
    val random: Random = Random.Default
) : PApplet() {
    private var layers = mutableListOf<Layer>()
    private val clapper = Clapper().also {
        it.bpm = 139.9f
        it.bpmListener = { requestShowBpm() }
    }
    private var interestingBpm = clapper.bpm
    private lateinit var bpmFont: PFont
    private var showBpmFrameCount = 0
    private var lastUpdateTimestamp = now()
    private var clearBackgroundOnDraw = true
    private var clearBackgroundColor = HSB1Color(0f, 0f, 0f, 1F)
    private var rotationAngle = 0f
    private var drawTestImage = false
    private val testImageLayer = TestImageLayer()
    private var cameraCapture: Capture? = null

    public override fun runSketch() {
        super.runSketch()
    }

    override fun settings() {
//        size(800, 600, P2D)
        fullScreen(P2D, 2)
    }

    override fun setup() {
        colorMode(HSB, COLOR_MAX)
        lastUpdateTimestamp = now()
        initLayers()
        clapper.start()

        val fonts = PFont.list()
        val bpmFontName = fonts.first { it.contains("mono", ignoreCase = true) } ?: fonts.first()
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

        if (clearBackgroundOnDraw) {
            clearBackground()
        }
        update()

        layers.forEach { layer ->
            graphics.push()
            layer.draw(graphics)
            graphics.pop()
        }

        random.maybe(0.05f) { requestShowBpm() }
        showBpmIfRequested()
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
//            GrainyGridLayerFactory.next(random),
//            PaddedGridLayerFactory.next(random),
//            PaddedGridLayerFactory.next(random),
//            PaddedGridLayerFactory.next(random),
//            PaddedGridLayerFactory.next(random),
//            TestImageLayer(),
//            TileLayer(random),
//            TileLayer(random),
            CathodeRayer(Layer.Intensity.LOW),
        )
    }

    private fun setLayersIntensity(intensity: Layer.Intensity) {
        println("setLayersIntensity(): intensity = $intensity")
        layers.forEach { it.intensity = intensity }
    }

    private fun clearBackground() {
        this.graphics.pushStyle()
        this.graphics.fill(
            this.clearBackgroundColor.hue,
            this.clearBackgroundColor.saturation,
            this.clearBackgroundColor.brightness,
            this.clearBackgroundColor.alpha,
        )
        this.graphics.rect(0F, 0F, this.graphics.width.toFloat(), this.graphics.height.toFloat())
        this.graphics.popStyle()
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
        return
        val result = clapper.update()

        layers.forEach { it.update(result) }

        if (result[BeatInterval.Quarter]?.didChange == true) {
            toggleClearBackgroundOnDraw()
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
        val alpha = showBpmFrameCount.toFloat() / SHOW_BPM_FRAME_DURATION.toFloat()

        textFont(bpmFont)
        textAlign(CENTER)
        textSize(textSize)
        noStroke()
        fill(0f, alpha)

        text(formattedBpm, width / 2f, height * 0.7f)

        --showBpmFrameCount
    }

    companion object {
        const val COLOR_MAX = 1f
        const val SHOW_BPM_FRAME_DURATION = 10

        private fun now() = System.currentTimeMillis()
    }
}
