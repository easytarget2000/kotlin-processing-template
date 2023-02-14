package eu.ezytarget.processingtemplate.layer

import eu.ezytarget.processingtemplate.Color
import eu.ezytarget.processingtemplate.nextFloatInRange
import processing.core.PApplet
import kotlin.random.Random

internal object GrainyGridLayerFactory {
    private const val MIN_LINE_WIDTH = 1f
    private const val MAX_LINE_WIDTH = 10f
    private const val MIN_DISTANCE = 60
    private const val MAX_DISTANCE = 120

    fun next(random: Random) = GrainyGridLayer(
        rotationRadian = random.nextFloat() * PApplet.TAU,
        lineWidth = random.nextFloatInRange(MIN_LINE_WIDTH, MAX_LINE_WIDTH),
        distance = random.nextInt(MIN_DISTANCE, MAX_DISTANCE),
        initialColor = Color.randomSolid(random, 1f)
    )

}
