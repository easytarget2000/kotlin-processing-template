package eu.ezytarget.processingtemplate.layers.grainygrid

import eu.ezytarget.processingtemplate.Color
import eu.ezytarget.processingtemplate.nextFloatInRange
import processing.core.PApplet
import kotlin.random.Random

internal object GrainyGridLayerFactory {
    private const val MIN_LINE_WIDTH = 1f
    private const val MAX_LINE_WIDTH = 6f
    private const val MIN_GRID_SIZE = 60
    private const val MAX_GRID_SIZE = 120

    fun next(random: Random) = GrainyGridLayer(
        rotationRadian = random.nextFloat() * PApplet.TAU,
        lineWidth = random.nextFloatInRange(MIN_LINE_WIDTH, MAX_LINE_WIDTH),
        gridSize = random.nextInt(MIN_GRID_SIZE, MAX_GRID_SIZE),
        initialColor = Color.randomSolid(random, 1f)
    )

}
