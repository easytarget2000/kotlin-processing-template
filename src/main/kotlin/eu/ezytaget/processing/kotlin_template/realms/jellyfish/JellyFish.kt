package eu.ezytaget.processing.kotlin_template.realms.jellyfish

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PConstants.TWO_PI
import processing.core.PGraphics
import processing.core.PVector
import java.lang.Float.min
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class JellyFish(random: Random = Random.Default) : Realm(random) {

    private data class Curve(
        var controlPoint1: PVector,
        var point1: PVector,
        var point2: PVector,
        var controlPoint2: PVector
    )

    var numberOfCurves: Int = 800

    var minRadiusFactor: Float = 0.33f

    var maxRadiusFactor: Float = 0.66f

    var drawPoints = false

    var lineAlpha = 0.1f

    var minControlAngleOffset = -TWO_PI * 0.3f

    var maxControlAngleOffset = -minControlAngleOffset

    var curveDetail = 40

    var referenceWidth = 800f

    var referenceHeight = 600f

    var center = PVector(referenceWidth / 2f, referenceHeight / 2f, 0f)

    private lateinit var curves: List<Curve>

    override fun setup(pApplet: PApplet, pGraphics: PGraphics) {
        super.setup(pApplet, pGraphics)

        referenceWidth = pGraphics.width.toFloat()
        referenceHeight = pGraphics.height.toFloat()
        center = PVector(referenceWidth / 2f, referenceHeight / 2f, 0f)
    }

    override fun update(pApplet: PApplet) {
        super.update(pApplet)

        val frameCount = pApplet.frameCount
        val radiusNoiseOffset = frameCount.toFloat() / 100f
        val maxControlRadiusFactor = frameCount.toFloat() / 99f
        val point1AngleNoiseOffset = frameCount.toFloat() / 98f
        val point2AngleNoiseOffset = frameCount.toFloat() / 71f

        val radiusRange = (maxRadiusFactor - minRadiusFactor)
        val maxRadius = min(referenceWidth, referenceHeight) * maxRadiusFactor
        val maxControlRadius = maxRadius * maxControlRadiusFactor

        curves = (0 until numberOfCurves).map { index ->
            val progress = ((index + 1).toFloat() / numberOfCurves)
            val radiusFactor = minRadiusFactor + (pApplet.noise(progress + radiusNoiseOffset) * radiusRange)
            val radius = min(referenceWidth, referenceHeight) * radiusFactor

            val point1AngleToCenter = pApplet.noise(progress + point1AngleNoiseOffset) * TWO_PI
//            val point1AngleToCenter = progress * TWO_PI
            val point1 = PVector(
                center.x + (cos(point1AngleToCenter) * radius),
                center.y + (sin(point1AngleToCenter) * radius),
                0f
            )

            val control1AngleToCenter = point1AngleToCenter + controlAngleOffset(pApplet, noiseFactor = progress)
            val controlPoint1 = PVector(
                center.x + (cos(control1AngleToCenter) * maxControlRadius),
                center.y + (sin(control1AngleToCenter) * maxControlRadius)
            )

            val point2AngleToCenter = PI + (pApplet.noise(progress + point2AngleNoiseOffset) * TWO_PI)

            val point2 = PVector(
                center.x + (cos(point2AngleToCenter) * radius),
                center.y + (sin(point2AngleToCenter) * radius),
                0f
            )

            val control2AngleToCenter = point2AngleToCenter + controlAngleOffset(pApplet, noiseFactor = progress)
            val controlPoint2 = PVector(
                center.x + (cos(control2AngleToCenter) * maxControlRadius),
                center.y + (sin(control2AngleToCenter) * maxControlRadius)
            )

            Curve(
                controlPoint1 = controlPoint1,
                point1 = point1,
                point2 = point2,
                controlPoint2 = controlPoint2
            )
        }
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)
        beginDraw(pGraphics)

        pGraphics.smooth()
        pGraphics.curveDetail(curveDetail)

        curves.forEach {
            drawCurveIn(it, pGraphics)
        }

        endDraw(pGraphics)
    }

    private fun drawCurveIn(curve: Curve, pGraphics: PGraphics) {
        if (drawPoints) {
            pGraphics.noStroke()
            pGraphics.fill(0f, 1f, 1f, 1f)
            pGraphics.circle(curve.point1.x, curve.point1.y, 8f)
            pGraphics.fill(0f, 1f, 1f, 0.5f)
            pGraphics.circle(curve.controlPoint1.x, curve.controlPoint1.y, 8f)

            pGraphics.fill(0.5f, 1f, 1f, 1f)
            pGraphics.circle(curve.point2.x, curve.point2.y, 8f)
            pGraphics.fill(0.5f, 1f, 1f, 0.5f)
            pGraphics.circle(curve.controlPoint2.x, curve.controlPoint2.y, 8f)
        }

        pGraphics.stroke(1f, 0f, 1f, lineAlpha)
        pGraphics.noFill()

        pGraphics.curve(
            curve.controlPoint1.x, curve.controlPoint1.y, curve.controlPoint1.z,
            curve.point1.x, curve.point1.y, curve.point1.z,
            curve.point2.x, curve.point2.y, curve.point2.z,
            curve.controlPoint2.x, curve.controlPoint2.y, curve.controlPoint2.z,
        )
    }

    private fun controlAngleOffset(pApplet: PApplet, noiseFactor: Float): Float {
        return minControlAngleOffset + (pApplet.noise(noiseFactor) * (maxControlAngleOffset - minControlAngleOffset))
    }

    private companion object {

        const val PI = kotlin.math.PI.toFloat()

    }
}