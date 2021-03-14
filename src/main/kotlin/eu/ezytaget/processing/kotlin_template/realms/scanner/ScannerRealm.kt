package eu.ezytaget.processing.kotlin_template.realms.scanner

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PGraphics
import kotlin.random.Random

class ScannerRealm(random: Random = Random.Default): Realm(random) {


    var lineLength = 800

    var progress = 0f

    var speed = 0.001f

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
        val endPixel = startPixel + lineLength
        val endPixelOvershot = endPixel - numberOfPixels
        val endPixelAdjusted = if (endPixelOvershot > 0) {
            endPixelOvershot
        } else {
            endPixel
        }

        beginDraw(pGraphics)
        // CONTINUE HERE.
        pGraphics.stroke(1f, 0f, 1f, 1f)

        val startX = startPixel % width
        val startY = startPixel / width
        var y = startY
        (0 until lineLength).forEach { offset ->
            val x = startX + offset
            if (x > width) {
                ++y
            } else {
                pGraphics.point(x, y)
            }
        }

        endDraw(pGraphics)
    }
}