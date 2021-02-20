package eu.ezytaget.processing.kotlin_template.realms.neon_tunnel

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PApplet.map
import processing.core.PGraphics


class NeonTunnel : Realm() {

    private var t = 0

    override fun update(pApplet: PApplet) {
        super.update(pApplet)
        t = pApplet.frameCount
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)

        var angle = 0f

        var radius = 64f
        while (radius < 1920f) {
            var tmp_x = 0f
            var tmp_y = 0f
            pGraphics.rotateY(t * 0.00025f)
            angle += t * 0.025f

            val hue = map(angle % 360f, 0f, 360f, 0f, 1f)
            pGraphics.stroke(
                    hue,
                    1f,
                    1f,
                    0.5f
            )

            pGraphics.noFill()
            pGraphics.strokeWeight(16f)
            pGraphics.ellipse(1920f, 512f, radius, radius)
            radius += 32f
        }
    }
}