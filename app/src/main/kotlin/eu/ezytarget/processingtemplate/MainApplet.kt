package eu.ezytarget.processingtemplate

import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import eu.ezytarget.processingtemplate.layers.Layer
import eu.ezytarget.processingtemplate.layers.paddedgrid.PaddedGridLayerFactory
import eu.ezytarget.processingtemplate.layers.testimage.TestImageLayer
import eu.ezytarget.processingtemplate.layers.tiles.TileLayer
import processing.core.PApplet
import kotlin.random.Random

internal class MainApplet(
    val random: Random = Random.Default
) : PApplet() {
    private var layers = mutableListOf<Layer>()
    private val clapper = Clapper().also { it.bpm = 140f }
    private var lastUpdateTimestamp = now()
    private var clearBackgroundOnDraw = true
    private var clearBackgroundColor = HSB1Color.BLACK_SOLID
    private var rotationAngle = 0f
    private var drawTestImage = false
    private val testImageLayer = TestImageLayer()

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
            PaddedGridLayerFactory.next(random),
            PaddedGridLayerFactory.next(random),
            TileLayer(random),
        )
    }

    private fun setLayersIntensity(intensity: Layer.Intensity) {
        println("setLayersIntensity(): intensity = $intensity")
        layers.forEach { it.intensity = intensity }
    }

    private fun clearBackground() {
        background(
            clearBackgroundColor.hue,
            clearBackgroundColor.saturation,
            clearBackgroundColor.brightness,
            clearBackgroundColor.alpha,
        )
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

    companion object {
        const val COLOR_MAX = 1f

        private fun now() = System.currentTimeMillis()
    }
}
