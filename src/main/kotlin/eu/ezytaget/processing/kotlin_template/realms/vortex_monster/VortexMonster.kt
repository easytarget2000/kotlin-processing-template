package eu.ezytaget.processing.kotlin_template.realms.vortex_monster

import processing.core.PApplet
import processing.core.PApplet.lerp
import processing.core.PConstants.PI
import kotlin.math.cos
import kotlin.math.sin

class VortexMonster {

    private val MIN_NUMBER_OF_LINES = 1
    private val MAX_NUMBER_OF_LINES = 8
    private val numFrames = 128
    private val parts = 4
    private var t = 0f
    private val offset_factor = 2.0f
    private val m = 4096
    private val motion_rad = 2f
    private val size = 0.4f
    private var numberOfLines = MAX_NUMBER_OF_LINES / 3

    fun drawIn(pApplet: PApplet) {
        pApplet.push()

        val width = pApplet.width.toFloat()
        val height = pApplet.height.toFloat()
        val frameCount = pApplet.frameCount.toFloat()

        t = (frameCount - 1f) / numFrames.toFloat()
        if (pApplet.noise(frameCount) > 0.8f) {
            numberOfLines = 2//pApplet.random(MIN_NUMBER_OF_LINES, MAX_NUMBER_OF_LINES) as Int
        }

        if (pApplet.noise(frameCount * pApplet.frameRate) > 0.8f) {
//            pApplet.background(1f, 1f, 1f)
            return
        }

        pApplet.stroke(1f, 0.33f)
        pApplet.translate(width / 2f, height / 2f)
        for (i in 0 until numberOfLines) {
            pApplet.push()
            pApplet.rotate(i * TWO_PI / numberOfLines)
            drawThing(pApplet, j = i, width = width)
            pApplet.pop()
        }

        pApplet.pop()
    }

    private fun drawThing(pApplet: PApplet, j: Int, width: Float) {
        val ph2 = (-parts.toFloat() * j.toFloat()) / numberOfLines

        //float xx1 = x1(ph2);
        //float yy1 = y1(ph2);
        //float xx2 = x2(ph2);
        //float yy2 = y2(ph2);

        //stroke(255);
        //fill(255);
        //ellipse(xx1, yy1, 3, 3);
        //ellipse(xx2, yy2, 3, 3);
        (0 .. m).forEach {
            val tt = it.toFloat() / m.toFloat()

            val x1 = x1(pApplet, -offset_factor * tt + ph2, width)
            val x2 = x2(pApplet, -offset_factor * (1 - tt) + ph2, width)
            val xx = lerp(x1, x2, tt)

            val y1 = y1(pApplet, -offset_factor * tt + ph2, width)
            val y2 = y2(pApplet, -offset_factor * (1 - tt) + ph2, width)
            val yy = lerp(y1, y2, tt)

            pApplet.point(xx, yy)
        }
    }

    private fun x1(pApplet: PApplet, ph: Float, width: Float): Float {
        return 0f * width + size * width * noise_(pApplet,75f + motion_rad * cos(TWO_PI * (t + ph)), motion_rad * sin(TWO_PI * (t + ph)))
    }

    private fun y1(pApplet: PApplet, ph: Float, width: Float): Float {
        return size * width * noise_(pApplet,100f + motion_rad * cos(TWO_PI * (t + ph)), motion_rad * sin(TWO_PI * (t + ph)))
    }

    private fun x2(pApplet: PApplet, ph: Float, width: Float): Float {
        return 0.3f * width + 0.5f * size * width * noise_(pApplet, 200f + motion_rad * cos(TWO_PI * (t + ph)), motion_rad * sin(TWO_PI * (t + ph)))
    }

    private fun y2(pApplet: PApplet, ph: Float, width: Float): Float {
        return 0.5f * size * width * noise_(pApplet, 300f + motion_rad * cos(TWO_PI * (t + ph)), motion_rad * sin(TWO_PI * (t + ph)))
    }

    private fun noise_(pApplet: PApplet, x: Float, y: Float = 0f) = 1f - (pApplet.noise(x, y) * 2f)

    companion object {
        private const val TWO_PI = PI * 2f
    }
}
