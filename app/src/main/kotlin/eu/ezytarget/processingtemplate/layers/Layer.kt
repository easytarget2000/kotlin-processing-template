package eu.ezytarget.processingtemplate.layers

import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.BeatIntervalUpdate
import processing.core.PGraphics

internal interface Layer {
    enum class Intensity {
        LOW, MEDIUM, HIGH,
    }

    var intensity: Intensity

    fun update(deltaTime: Long)
    fun update(beatStatus: Map<BeatInterval, BeatIntervalUpdate>)
    fun draw(pGraphics: PGraphics)
}
