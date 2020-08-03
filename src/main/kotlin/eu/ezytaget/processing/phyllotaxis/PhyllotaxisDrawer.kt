package eu.ezytaget.processing.phyllotaxis

import processing.core.PApplet.max
import processing.core.PApplet.radians
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class PhyllotaxisDrawer(maxColorValue: Float = 1f) {

    var saturation = maxColorValue * 0.5f
    var brightness = maxColorValue
    var alpha = maxColorValue

    fun draw(phyllotaxis: Phyllotaxis, pApplet: PApplet) {
        pApplet.pushStyle()
        pApplet.noStroke()

        pApplet.beginShape()
        (16 until phyllotaxis.n).forEach {
            val a = it * radians(phyllotaxis.phiDegrees)
            val r  = phyllotaxis.c * sqrt(it.toFloat())
            val x = r * cos(a)
            val y: Float = r * sin(a)
            var hu = it / 3f % 360f
            pApplet.fill(hu / 255f, saturation, brightness, alpha * 0.5f)
            pApplet.vertex(x, y, 0f)
//            pApplet.stroke(hu / 255f, saturation, brightness, alpha)
//            pApplet.point(x + 1f, y + 1f)
        }
        pApplet.endShape()

        pApplet.popStyle()
    }

}