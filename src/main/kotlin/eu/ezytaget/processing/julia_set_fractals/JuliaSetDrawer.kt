// Based on Julia Set by
// The Coding Train / Daniel Shiffman
// https://thecodingtrain.com/CodingChallenges/022-juliaset.html
// https://youtu.be/fAsaSkmbF5s
// https://editor.p5js.org/codingtrain/sketches/G6qbMmaI

package eu.ezytaget.processing.julia_set_fractals

import kotlin.math.sqrt

object JuliaSetDrawer {

    fun draw(juliaSet: JuliaSet, pApplet: PApplet) {

        val width = pApplet.width
        val height = pApplet.height
        val widthF = width.toFloat()
        val heightF = height.toFloat()

        // Establish a range of values on the complex plane
        // A different range will allow us to "zoom" in or out on the fractal

        // It all starts with the width, try higher or lower values
        //float w = abs(sin(angle))*5
        val w = 5f
        val h = (w * heightF) / widthF

        // Start at negative half the width and height
        val startX = -w / 2f
        val startY = -h / 2f

        // Make sure we can write to the pixels[] array.
        // Only need to do this once since we don't do any other drawing.
        pApplet.loadPixels()

        // Maximum number of iterations for each point on the complex plane
        val maxiterations = 100

        // x goes from xmin to xmax
        val xmax = startX + w
        // y goes from ymin to ymax
        val ymax = startY + h

        // Calculate amount we increment x,y for each pixel
        val dx = (xmax - startX) / width
        val dy = (ymax - startY) / height

        // Start y
        var y = startY
        val ca = juliaSet.ca
        val cb = juliaSet.cb

        (0 until height).forEach { j ->

            var x = startX
            (0 until width).forEach { i ->

            // Now we test, as we iterate z = z^2 + cm does z tend towards infinity?
            var a = x
            var b = y
            var n = 0
            while (n < maxiterations) {
                val aa = a * a
                val bb = b * b
                // Infinity in our finite world is simple, let's just consider it 16
                if (aa + bb > 4.0) {
                    break  // Bail
                }
                val twoab = 2f * a * b
                a = aa - bb + ca
                b = twoab + cb
                n++
            }

            // We color each pixel based on how long it takes to get to infinity
            // If we never got there, let's pick the color black
            if (n == maxiterations) {
                pApplet.pixels[i+j*width] = pApplet.color(0)
            } else {
                // Gosh, we could make fancy colors here if we wanted
                val hu = sqrt(n.toFloat() / maxiterations)
                pApplet.pixels[i+j*width] = pApplet.color(hu, 1f, 0.75f)
            }
            x += dx
        }
            y += dy
        }
        pApplet.updatePixels()
    }
}