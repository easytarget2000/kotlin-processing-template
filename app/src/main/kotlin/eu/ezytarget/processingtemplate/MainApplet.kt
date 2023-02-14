package eu.ezytarget.processingtemplate

import eu.ezytarget.processingtemplate.layer.GrainyGridLayer
import eu.ezytarget.processingtemplate.layer.Layer
import processing.core.PApplet

internal class MainApplet : PApplet() {
    private var layers = mutableListOf<Layer>()
    private var lastUpdateTimestamp = now()
    private var clearBackgroundOnDraw = true
    private var clearBackgroundColor = Color.BLACK_SOLID

    public override fun runSketch() {
        super.runSketch()
    }

    override fun settings() {
//        size(800, 600, P2D)
        fullScreen(P2D, 2)
    }

    override fun setup() {
        colorMode(HSB, 1f)
        lastUpdateTimestamp = now()
        layers = mutableListOf(GrainyGridLayer())
    }

    override fun draw() {
        if (clearBackgroundOnDraw) {
            clearBackground()
        }
        update()
        layers.forEach {
            it.draw(graphics)
        }
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

        layers.forEach {
            it.update(deltaTime)
        }
    }

    companion object {
        private fun now() = System.currentTimeMillis()
    }
}
