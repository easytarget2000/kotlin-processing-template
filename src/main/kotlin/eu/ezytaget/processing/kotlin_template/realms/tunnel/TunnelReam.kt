package eu.ezytaget.processing.kotlin_template.realms.tunnel

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PConstants.CENTER
import processing.core.PGraphics
import kotlin.random.Random

class TunnelReam(random: Random = Random.Default): Realm(random) {

    var tunnelDiameter = 400f

    var colorValue1 = 1f

    var colorValue2 = 0f

    var colorValue3 = 1f

    var alpha = 1f

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)
        beginDraw(pGraphics)

        pGraphics.stroke(colorValue1, colorValue2, colorValue3, alpha)
        pGraphics.noFill()
        pGraphics.ellipseMode(CENTER)

        pGraphics.ellipse(0f, 0f, tunnelDiameter, tunnelDiameter)

        endDraw(pGraphics)
    }

}