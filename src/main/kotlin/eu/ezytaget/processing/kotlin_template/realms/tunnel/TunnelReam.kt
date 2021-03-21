package eu.ezytaget.processing.kotlin_template.realms.tunnel

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PConstants.*
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

    var numberOfEdges = 8

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

        pGraphics.lights()

        val startZ = segmentWidth * (numberOfSegments / 3f) + (relativeSegmentOffset * segmentWidth * 8f)
        pGraphics.translate(pGraphics.width / 2f, pGraphics.height / 2f, startZ)

        pGraphics.fill(colorValue1, colorValue2, colorValue3, 0.5f)
        pGraphics.noStroke()
        pGraphics.stroke(colorValue1, colorValue2, 0.5f, alpha)
        pGraphics.ellipseMode(CENTER)

        (0 until numberOfSegments).forEach { _ ->
            drawSegment(pGraphics)
            pGraphics.translate(0f, 0f, -segmentWidth)
        }

        endDraw(pGraphics)
    }

    private fun drawSegment(pGraphics: PGraphics) {
        pGraphics.push()

        val edgeLength = tunnelDiameter * 0.33f
        val edgeAngle = TWO_PI / numberOfEdges.toFloat()

        pGraphics.translate(-tunnelDiameter / 2f, 0f, 0f)

        pGraphics.rotateX(PI_HALF)
//        pGraphics.rotateY(PI_HALF / 2f)

        (0 until numberOfEdges).forEach { _ ->
            pGraphics.rect(0f, 0f, edgeLength, segmentWidth)
            pGraphics.translate(edgeLength, 0f, 0f)
            pGraphics.rotateY(edgeAngle)
        }
        pGraphics.pop()
    }

    companion object {

        private const val TWO_PI = kotlin.math.PI.toFloat() * 2f

        private const val PI_HALF = kotlin.math.PI.toFloat() / 2f

    }
}