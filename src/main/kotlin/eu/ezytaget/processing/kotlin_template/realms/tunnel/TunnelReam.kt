package eu.ezytaget.processing.kotlin_template.realms.tunnel

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PConstants.CENTER
import processing.core.PGraphics
import kotlin.random.Random

class TunnelReam(random: Random = Random.Default): Realm(random) {

    var tunnelDiameter = 400f

    var colorValue1 = 1f

    var colorValue2 = 0f

    var colorValue3 = 1f

    var alpha = 1f

    var segmentWidth = 40f

    var numberOfSegments = 64

    var relativeSegmentOffset = 0f

    var velocity = 0.01f

    override fun update(pApplet: PApplet) {
        super.update(pApplet)
        relativeSegmentOffset += velocity
        if (relativeSegmentOffset > 1f) {
            relativeSegmentOffset = 0f
        }
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)
        beginDraw(pGraphics)

        val startZ = segmentWidth * (numberOfSegments / 3f) + (relativeSegmentOffset * segmentWidth)
        pGraphics.translate(pGraphics.width / 2f, pGraphics.height / 2f, startZ)

        pGraphics.stroke(colorValue1, colorValue2, colorValue3, alpha)
        pGraphics.noFill()
        pGraphics.ellipseMode(CENTER)

        (0 until numberOfSegments).forEach { _ ->
            drawSegment(pGraphics)
            pGraphics.translate(0f, 0f, -segmentWidth)
        }

        endDraw(pGraphics)
    }

    private fun drawSegment(pGraphics: PGraphics) {
        pGraphics.ellipse(0f, 0f, tunnelDiameter, tunnelDiameter)
    }
}