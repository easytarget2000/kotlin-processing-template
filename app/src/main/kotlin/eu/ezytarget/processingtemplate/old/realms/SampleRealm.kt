package eu.ezytarget.processing_template.realms

import processing.core.PGraphics
import kotlin.random.Random

class SampleRealm(
        random: Random = Random.Default
): Realm(random) {

    var drawOutline = true

    var outlineHue = 1f

    var outLineSaturation = 1f

    var outlineBrightness = 1f

    var outlineAlpha = 1f

    var drawCenter = true

    var centerPointHue = 1f

    var centerPointSaturation = 1f

    var centerPointBrightness = 1f

    var centerPointAlpha = 1f

    var centerPointRadius = 8f

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)

        pGraphics.beginDraw()
        pGraphics.push()

        val width = pGraphics.width.toFloat()
        val height = pGraphics.height.toFloat()

        if (drawOutline) {
            pGraphics.noFill()
            pGraphics.stroke(outlineHue, outLineSaturation, outlineBrightness, outlineAlpha)
            pGraphics.rect(0f, 0f, width, height)
        }

        if (drawCenter) {
            pGraphics.noFill()
            pGraphics.stroke(centerPointHue, centerPointSaturation, centerPointBrightness, centerPointAlpha)
            pGraphics.strokeWeight(centerPointRadius)
            pGraphics.point(width / 2f, height / 2f)
        }

        pGraphics.pop()
        pGraphics.endDraw()
    }
}