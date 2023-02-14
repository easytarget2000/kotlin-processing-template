package eu.ezytarget.processingtemplate.layer

import processing.core.PApplet
import processing.core.PGraphics

internal interface Layer {
    enum class Intensity {
        LOW, MEDIUM, HIGH,
    }

    var intensity: Intensity

    fun update(deltaTime: Long)

    fun draw(pGraphics: PGraphics)
}
