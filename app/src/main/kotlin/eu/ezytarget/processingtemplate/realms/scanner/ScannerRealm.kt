package eu.ezytarget.processingtemplate.realms.scanner

import eu.ezytarget.processingtemplate.realms.Realm
import processing.core.PApplet
import processing.core.PGraphics
import kotlin.random.Random

class ScannerRealm(random: Random = Random.Default): Realm(random) {

    var progress = 0f

    var speed = 0.004f

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
        val startPixel = (numberOfPixels * progress).toInt()
        val lineLength = (numberOfPixels * speed).toInt()

        beginDraw(pGraphics)
        pGraphics.stroke(colorValue1, colorValue2, colorValue3, alpha)

        var x = startPixel % width
        var y = startPixel / width
        (0 until lineLength).forEach { _ ->
            ++x
            if (x > width) {
                ++y
                x = 0
            }

            pGraphics.point(x.toFloat(), y.toFloat())
        }

        endDraw(pGraphics)
    }

}