// Based on Flowfield.pde by Daniel Shiffman:
// http://youtube.com/thecodingtrain
// http://codingtra.in
//
// Coding Challenge #24: Perlin Noise Flow  Field
// https://youtu.be/BjoM9oKOAKY

package eu.ezytaget.processing.flowfield

import processing.core.PConstants.TWO_PI
import processing.core.PVector
import kotlin.math.floor


class FlowField internal constructor(
        pixelWidth: Int,
        pixelHeight: Int,
        val scale: Float
) {
    var inc = 0.1f
    var zoff = 0f

    val cols = floor(x = pixelWidth / scale).toInt() + 1
    val rows = floor(x = pixelHeight / scale).toInt() + 1

    val vectors = (0 until (cols * rows)).map {
        PVector()
    }.toMutableList()

    fun update(pApplet: PApplet) {
        var xoff = 0f
        for (y in 0 until rows) {
            var yoff = 0f
            for (x in 0 until cols) {
                val angle: Float = pApplet.noise(xoff, yoff, zoff) * TWO_PI * 4
                val v = PVector.fromAngle(angle)
                v.setMag(1f)
                val index = x + y * cols
                vectors[index] = v
                xoff += inc
            }
            yoff += inc
        }
        zoff += 0.004f
    }

    fun display(pApplet: PApplet) {
        for (y in 0 until rows) {
            for (x in 0 until cols) {
                val index = x + y * cols
                val v = vectors[index]
                pApplet.stroke(0f, 0f, 0f, 40f)
                pApplet.strokeWeight(0.1f)
                pApplet.pushMatrix()
                pApplet.translate(x * scale, y * scale)
                pApplet.rotate(v.heading())
                pApplet.line(0f, 0f, scale, 0f)
                pApplet.popMatrix()
            }
        }
    }

}
