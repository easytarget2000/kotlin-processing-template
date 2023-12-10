package eu.ezytarget.processingtemplate.realms.stripes

import processing.core.PApplet
import processing.core.PApplet.*
import kotlin.math.PI


class StripesRealm {

    var numFrames = 128

    fun draw(pApplet: PApplet) {
        val t = (pApplet.frameCount - 1f) % numFrames / numFrames

        val width = pApplet.width.toFloat()
        val height = pApplet.height.toFloat()

        // Draws every pixel
        pApplet.loadPixels()
        var i = 0
        while (i < width) {
            for (j in 0 until pApplet.height) {
                val color_ = brightness(i.toFloat(), j.toFloat(), t, width, height)
                pApplet.pixels[i + j * pApplet.width] = pApplet.color(color_)
            }
            ++i
        }

        pApplet.updatePixels()

//        // Draws a white rectangle
//        stroke(255)
//        noFill()
//        rect(margin, margin, width - 2 * margin, height - 2 * margin)
    }

    private fun brightness(x: Float, y: Float, t: Float, width: Float, height: Float) = ease(
            map(
                    sin(TWO_PI * (t + scalar_field_offset(x, y, width, height))),
                    -1f,
                    1f,
                    0f,
                    1f
            ),
            3f
    )

    private fun ease(p: Float, g: Float) = if (p < 0.5f) {
        0.5f * pow(2f * p, g)
    } else {
        1f - 0.5f * pow(2f * (1f - p), g)
    }

//float scalar_field_offset(float x, float y) {
//  float distance = dist(x, y, width / 2f, height / 2f);
//  return 0.5f * (x + y) /  distance;
//}

    //float scalar_field_offset(float x, float y) {
    //  float distance = dist(x, y, width / 2f, height / 2f);
    //  return 0.5f * (x + y) /  distance;
    //}
    var scale = 0.003f

//float scalar_field_offset(float x, float y) {
//  return 10f * noise(scale * x, scale * y);
//}


    //float scalar_field_offset(float x, float y) {
    //  return 10f * noise(scale * x, scale * y);
    //}
    private fun scalar_field_offset(x: Float, y: Float, width: Float, height: Float): Float {
        val distance = dist(x, y, 0.5f * width, 0.5f * height)
        return 300f / (25f + distance)
    }

    companion object {
        private const val TWO_PI = 2f * PI.toFloat()
    }
}