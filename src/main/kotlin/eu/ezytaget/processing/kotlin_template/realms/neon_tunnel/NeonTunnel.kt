package eu.ezytaget.processing.kotlin_template.realms.neon_tunnel

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PApplet.map
import processing.core.PGraphics
import processing.core.PConstants.DEG_TO_RAD
import kotlin.math.cos
import kotlin.math.sin


class NeonTunnel : Realm() {

    private var t = 0

    override fun update(pApplet: PApplet) {
        super.update(pApplet)
        t = pApplet.frameCount
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)

        var angle = 0f

        var radius = 16f
        while (radius < 1024) {
            var tmp_x = 0f
            var tmp_y = 0f
            pGraphics.rotateY(t * 0.0025f)
            angle += t * 0.25f

            //ofColor c;
            //final color c;
            pGraphics.stroke(map(angle.toInt() % 360f, 0f, 360f, 0f, 255f), 255f, 255f)
            //ofSetColor(c);
            var deg = t.toFloat()
            while (deg < t + 256f) {
                val x = 1920f + radius * cos(deg * DEG_TO_RAD)
                val y = 512f + radius * sin(deg * DEG_TO_RAD)
                if (tmp_x != 0f && tmp_y != 0f) {
                    pGraphics.line(x, y, tmp_x, tmp_y)
                }
                tmp_x = x
                tmp_y = y
                deg += 0.1f
            }
            radius += 32f
        }
    }
}