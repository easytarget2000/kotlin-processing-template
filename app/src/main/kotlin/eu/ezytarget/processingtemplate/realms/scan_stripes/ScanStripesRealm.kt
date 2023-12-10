package eu.ezytarget.processingtemplate.realms.scan_stripes

import eu.ezytarget.processingtemplate.nextFloatInRange
import eu.ezytarget.processingtemplate.realms.Realm
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

    var defaultDensity = 64f

    var currentDensity = defaultDensity

    var maxDensity = defaultDensity * 2f

    var defaultDebounceVelocity = 0.1f

    var currentDensityVelocity = 2f

    var lineWidth = defaultDensity / 4f

    var progress = PROGRESS_START

    var progressVelocity = 0.1f

    var drawPoints = false

    var pointDistance = defaultDensity

    var pointProbability = 0.5f

    lateinit var pointOfInterest: PVector

    override fun update(pApplet: PApplet) {
        super.update(pApplet)
        rotation += rotationVelocity
        progress += progressVelocity
        if (progress > MAX_PROGRESS) {
            progress = MAX_PROGRESS
        }

        currentDensity += currentDensityVelocity
        if (currentDensity > maxDensity) {
            currentDensity = maxDensity
            currentDensityVelocity = 0f
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

        val yOverdraw = pGraphics.height / 3f
        val heightHalf = pGraphics.height / 2f

        val startX = progress * -width
        val endX = progress * width

        var y = (-heightHalf - yOverdraw)
        while (y < (heightHalf + yOverdraw)) {
            pGraphics.stroke(1f)
            drawLineIn(pGraphics, startX, y, endX)
            y += currentDensity
        }

        endDraw(pGraphics)
    }

    private fun drawLineIn(
        pGraphics: PGraphics,
        startX: Float,
        y: Float,
        endX: Float
    ) {
        val maxDistanceToPOI = max(pGraphics.width, pGraphics.height) / 2f

        if (drawPoints) {
            var x = startX
            while (x <= endX) {
                val normDistanceToPOI = dist(
                    x,
                    y,
                    pointOfInterest.x,
                    pointOfInterest.y
                ) / maxDistanceToPOI
                pGraphics.strokeWeight(lineWidth * (1f - normDistanceToPOI))
                pGraphics.point(x, y)

                x += pointDistance
            }
        } else {
            pGraphics.line(startX, y, endX, y)
        }
    }

    fun setRandomRotationVelocity() {
        this.rotationVelocity = this.random.nextFloatInRange(
            from = -maxRotationVelocity,
            until = maxRotationVelocity
        )
    }

    fun resetProgress() {
        this.progress = PROGRESS_START
    }

    override fun bounce(pApplet: PApplet) {
        super.bounce(pApplet)
        currentDensity = defaultDensity
        currentDensityVelocity = defaultDebounceVelocity
    }

    companion object {
        private const val PROGRESS_START = 0f
        private const val MAX_PROGRESS = 1f
    }
}