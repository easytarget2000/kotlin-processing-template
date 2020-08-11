package eu.ezytaget.processing.barnsley_fern

import processing.core.PApplet.map
import processing.core.PVector
import kotlin.random.Random

class FernDrawer(
        private var position: PVector = PVector(0f, 0f, 0f),
        private val random: Random = Random.Default
) {

    var pStem = 0.1f
    var pSmallLeaflets = 0.85f
    var pLargeLeftHandLeaflet = 0.07f
    var pLargeRightHandLeaflet = 0.07f

    fun drawShape(pApplet: PApplet, numberOfSteps: Int = 100) {
        pApplet.stroke(1f)
        pApplet.strokeWeight(2f)
        repeat(numberOfSteps) {
            drawOnce(pApplet)
            advance()
        }
    }

    private fun drawOnce(pApplet: PApplet) {
        val mappedX = map(position.x, -2.182f, 2.6558f, 0f, pApplet.width.toFloat())
        val mappedY = map(position.y, 0f, 9.9983f, pApplet.height.toFloat(), 0f)
        pApplet.point(mappedX, mappedY, position.z)
//        pApplet.point(50f, 50f)
    }

    fun advance(): PVector {
        var a = 0f
        var b = 0f
        var c = 0f
        var d = 0f
        var e = 0f
        var f = 0f

        random.maybe(pStem) {
            a = 0f
            b = 0f
            c = 0f
            d = 0.16f
            e = 0f
            f = 0f
        }
        random.maybe(pSmallLeaflets) {
            a = 0.85f
            b = 0.04f
            c = -0.04f
            d = 0.85f
            e = 0f
            f = 1.6f
        }
        random.maybe(pLargeLeftHandLeaflet) {
            a = 0.2f
            b = -0.26f
            c = 0.23f
            d = 0.22f
            e = 0f
            f = 1.6f
        }
        random.maybe(pLargeRightHandLeaflet) {
            a = -0.15f
            b = 0.28f
            c = 0.26f
            d = 0.24f
            e = 0f
            f = 0.44f
        }

        position = PVector(
                (a * position.x) + (b * position.y) + e,
                (c * position.x) + (d * position.y) + f
        )

        return position
    }
}