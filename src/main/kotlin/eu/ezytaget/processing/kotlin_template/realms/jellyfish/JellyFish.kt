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

class JellyFish(
    random: Random = Random.Default,
    private val numberOfCurves: Int = 800,
    private val minRadiusFactor: Float = 0.33f,
    private val maxRadiusFactor: Float = 0.66f,
    private val radiusNoiseOffset: Float = 13.17f,
    private val maxControlRadiusFactor: Float = 8f,
    private val point1AngleNoiseOffset: Float = 19.5f,
    private val point2AngleNoiseOffset: Float = 29.1f
) : Realm(random) {

    private data class Curve(
        val controlPoint1: PVector,
        val point1: PVector,
        val point2: PVector,
        val controlPoint2: PVector
    )

    var drawPoints = false

    var lineAlpha = 0.1f

    var minControlAngleOffset = -TWO_PI * 0.3f

    var maxControlAngleOffset = -minControlAngleOffset

    var curveDetail = 40

    private lateinit var curves: List<Curve>

    override fun setup(pApplet: PApplet, pGraphics: PGraphics) {
        super.setup(pApplet, pGraphics)

        val width = pGraphics.width.toFloat()
        val height = pGraphics.height.toFloat()
        val radiusRange = (maxRadiusFactor - minRadiusFactor)
        val maxRadius = min(width, height) * maxRadiusFactor
        val maxControlRadius = maxRadius * maxControlRadiusFactor
        val center = PVector(width / 2f, height / 2f, 0f)

        curves = (0 until numberOfCurves).map { index ->
            val progress = ((index + 1).toFloat() / numberOfCurves)
            val radiusFactor = minRadiusFactor + (pApplet.noise(progress + radiusNoiseOffset) * radiusRange)
            val radius = min(width, height) * radiusFactor

            val point1AngleToCenter = pApplet.noise(progress + point1AngleNoiseOffset) * TWO_PI
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

            val point2AngleToCenter = pApplet.noise(progress + point2AngleNoiseOffset) * TWO_PI

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