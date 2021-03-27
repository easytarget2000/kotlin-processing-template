package eu.ezytaget.processing.kotlin_template.realms.holodeck

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PGraphics
import kotlin.math.min
import kotlin.random.Random

class Holodeck(random: Random = Random.Default): Realm(random) {

    var colorValue1 = 0.19f

    var colorValue2 = 1f

    var colorValue3 = 0.7f

    var alpha = 1f

    var numberOfColumns = 32

    var lineWidth = 2f

    override fun setup(pApplet: PApplet, pGraphics: PGraphics) {
        super.setup(pApplet, pGraphics)
        lineWidth = min(pGraphics.width.toFloat(), pGraphics.height.toFloat()) / 256f
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)
        beginDraw(pGraphics)

        val width = pGraphics.width.toFloat()
        val height = pGraphics.height.toFloat()

        pGraphics.strokeWeight(lineWidth)
        pGraphics.stroke(colorValue1, colorValue2, colorValue3, alpha)
        pGraphics.noFill()

        val spacing = width / numberOfColumns.toFloat()

        (0 until numberOfColumns).forEach { columnIndex ->
            val x = columnIndex.toFloat() * spacing
            pGraphics.line(x, 0f, 0f, x, height, 0f)
        }

        val lastColumnX = width - 1f
        pGraphics.line(lastColumnX, 0f, 0f, lastColumnX, height, 0f)

        val numberOfRows = (numberOfColumns / (width / height)).toInt()

        (0 until numberOfRows).forEach { rowIndex ->
            val y = rowIndex.toFloat() * spacing
            pGraphics.line(0f, y, 0f, width, y, 0f)
        }

        val lastColumnY = height - 1f
        pGraphics.line(0f, lastColumnY, 0f, width, lastColumnY, 0f)

        endDraw(pGraphics)
    }
}