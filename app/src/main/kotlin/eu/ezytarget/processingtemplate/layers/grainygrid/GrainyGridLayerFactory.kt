package eu.ezytarget.processingtemplate.layers.grainygrid

import kotlin.random.Random

internal object GrainyGridLayerFactory {
    fun next(random: Random) = GrainyGridLayer()
}
