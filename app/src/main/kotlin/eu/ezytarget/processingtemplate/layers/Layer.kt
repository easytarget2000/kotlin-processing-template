package eu.ezytarget.processingtemplate.layers

import processing.core.PGraphics

internal interface Layer {
    enum class Intensity {
        LOW, MEDIUM, HIGH,
    }

    var intensity: Intensity

    fun setColorMax(colorMax: Float)
    fun update(deltaTime: Long)
    fun draw(pGraphics: PGraphics)
}