package eu.ezytaget.processing.kotlin_template.realms.scan_stripes

import eu.ezytaget.processing.kotlin_template.nextFloat
import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PGraphics
import kotlin.random.Random

class ScanStripesRealm(random: Random = Random.Default): Realm(random) {

    var evenLineHue = 0f

    var evenLineSaturation = 0f

    var evenLineBrightness = 1f

    var evenLineAlpha = 1f

    var oddLineHue = 0f

    var oddLineSaturation = 0f

    var oddLineBrightness = 0f

    var oddLineAlpha = 1f

    var rotation = 0f

    var rotationVelocity = 0.01f

    var maxRotationVelocity = 0.05f

    var density = 9

    var progress = PROGRESS_START

    var progressVelocity = 0.01f

    override fun update(pApplet: PApplet) {
        super.update(pApplet)
        rotation += rotationVelocity
        progress += progressVelocity
        if (progress > MAX_PROGRESS) {
            progress = MAX_PROGRESS
        }
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)
        beginDraw(pGraphics)

        val height = pGraphics.height.toFloat()
        val width = pGraphics.width.toFloat()

        pGraphics.translate(width / 2f, height / 2f)
        pGraphics.rotate(rotation)

        val yOverdraw = pGraphics.height / 3
        val heightHalf = pGraphics.height / 2

        ((-heightHalf - yOverdraw) until (heightHalf + yOverdraw)).forEach { y ->
            if (y % density == 0) {
                pGraphics.stroke(1f)
            } else {
                pGraphics.stroke(0f, 0f)
            }

            pGraphics.line(progress * -width, y.toFloat(), progress * width, y.toFloat())
        }

        endDraw(pGraphics)
    }

    fun setRandomRotationVelocity() {
        rotationVelocity = random.nextFloat(from = -maxRotationVelocity, until = maxRotationVelocity)
    }

    fun resetProgress() {
        progress = PROGRESS_START
    }

    companion object {

        private const val PROGRESS_START = 0f

        private const val MAX_PROGRESS = 1f

    }
}