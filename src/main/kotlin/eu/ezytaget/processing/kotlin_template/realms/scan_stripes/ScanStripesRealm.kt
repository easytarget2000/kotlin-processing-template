package eu.ezytaget.processing.kotlin_template.realms.scan_stripes

import eu.ezytaget.processing.kotlin_template.nextFloat
import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PApplet.dist
import processing.core.PGraphics
import processing.core.PVector
import kotlin.math.max
import kotlin.random.Random

class ScanStripesRealm(random: Random = Random.Default) : Realm(random) {

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

    var maxRotationVelocity = 0.02f

    var density = 32

    var lineWidth = density / 4f

    var progress = PROGRESS_START

    var progressVelocity = 0.1f

    var drawPoints = true

    var pointDistance = density.toFloat()

    var pointProbability = 0.5f

    lateinit var pointOfInterest: PVector

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

        pointOfInterest = PVector(0f, 0f)

        pGraphics.translate(width / 2f, height / 2f)
        pGraphics.rotate(rotation)
        pGraphics.strokeWeight(lineWidth)

        val yOverdraw = pGraphics.height / 3
        val heightHalf = pGraphics.height / 2

        val startX = progress * -width
        val endX = progress * width

        ((-heightHalf - yOverdraw) until (heightHalf + yOverdraw)).forEach { y ->
            if (y % density == 0) {
                pGraphics.stroke(1f)
                drawLineIn(pGraphics, startX, y.toFloat(), endX)
            }
        }

        endDraw(pGraphics)
    }

    private fun drawLineIn(pGraphics: PGraphics, startX: Float, y: Float, endX: Float) {
        val maxDistanceToPOI = max(pGraphics.width, pGraphics.height) / 2f

        if (drawPoints) {
            var x = startX
            while (x <= endX) {
                val normDistanceToPOI = dist(x, y, pointOfInterest.x, pointOfInterest.y) / maxDistanceToPOI
                pGraphics.strokeWeight(lineWidth * (1f - normDistanceToPOI))
                pGraphics.point(x, y)

                x += pointDistance
            }
        } else {
            pGraphics.line(startX, y, endX, y)
        }
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