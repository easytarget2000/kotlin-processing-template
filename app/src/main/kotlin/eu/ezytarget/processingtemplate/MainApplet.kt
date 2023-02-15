package eu.ezytarget.processingtemplate

import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import eu.ezytarget.processingtemplate.layers.paddedgrid.PaddedGridLayerFactory
import eu.ezytarget.processingtemplate.layers.Layer
import eu.ezytarget.processingtemplate.layers.grainygrid.GrainyGridLayerFactory
import processing.core.PApplet
import kotlin.random.Random

internal class MainApplet(
    val random: Random = Random.Default
) : PApplet() {
    private var layers = mutableListOf<Layer>()
    private val clapper = Clapper().also { it.bpm = 130f }
    private var lastUpdateTimestamp = now()
    private var clearBackgroundOnDraw = true
    private var clearBackgroundColor = Color.BLACK_SOLID
    private var rotationAngle = 0f

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
            else -> println("keyPressed(): Unhandled key = $key")
        }
    }

    private fun initLayers() {
        layers = mutableListOf(
//            GrainyGridLayerFactory.next(random),
            PaddedGridLayerFactory.next(random),
            PaddedGridLayerFactory.next(random),
        )
        layers.forEach { it.setColorMax(COLOR_MAX) }
    }

    private fun setLayersIntensity(intensity: Layer.Intensity) {
        println("setLayersIntensity(): intensity = $intensity")
        layers.forEach { it.intensity = intensity }
    }

    private fun clearBackground() {
        background(
            clearBackgroundColor.value1,
            clearBackgroundColor.value2,
            clearBackgroundColor.value3,
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
    }

    private fun updateClapper() {
        val result = clapper.update()
        if (result[BeatInterval.Quarter]?.didChange == true) {
            toggleClearBackgroundOnDraw()
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
