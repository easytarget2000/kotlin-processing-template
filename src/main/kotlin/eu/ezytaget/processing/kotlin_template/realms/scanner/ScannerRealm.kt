package eu.ezytaget.processing.kotlin_template.realms.scanner

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PGraphics
import kotlin.random.Random

class ScannerRealm(random: Random = Random.Default): Realm(random) {

    var progress = 0f

    var speed = 0.001f

    var colorValue1 = 1f

    var colorValue2 = 0f

    var colorValue3 = 1f

    var alpha = 1f

    override fun update(pApplet: PApplet) {
        super.update(pApplet)

        progress += speed
        if (progress > 1f) {
            progress = 0f
        }
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)

        val width = pGraphics.width
        val height = pGraphics.height
        val numberOfPixels = width * height
        val startPixel = numberOfPixels * progress
        val lineLength = (numberOfPixels * speed).toInt()
        val endPixel = startPixel + lineLength
        val endPixelOvershot = endPixel - numberOfPixels
        val endPixelAdjusted = if (endPixelOvershot > 0) {
            endPixelOvershot
        } else {
            endPixel
        }

        beginDraw(pGraphics)
        pGraphics.stroke(colorValue1, colorValue2, colorValue3, alpha)

        var x = startPixel % width
        var y = startPixel / width
        (0 until lineLength).forEach { _ ->
            x += 1f
            if (x > width) {
                y += 1f
                x = 0f
            }

            pGraphics.point(x, y)
        }

        endDraw(pGraphics)
    }
}