package eu.ezytaget.processing.kotlin_template.realms.jellyfish

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PGraphics
import processing.core.PVector
import java.lang.Float.min
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class JellyFish(
    random: Random = Random.Default,
    val numberOfCurves: Int = 80,
    val maxRadiusFactor: Float = 0.33f
): Realm(random) {

    private data class Curve(
        val point1: PVector,
        val point2: PVector
    )

    var drawPoints = true

    private lateinit var curves: List<Curve>

    override fun setup(pGraphics: PGraphics) {
        super.setup(pGraphics)

        val width = pGraphics.width.toFloat()
        val height = pGraphics.height.toFloat()
        val maxRadius = min(width, height) * maxRadiusFactor
        val center = PVector(width / 2f, height / 2f, 0f)

        curves = (0 until numberOfCurves).map { index ->
            val point1AngleToCenter = ((index + 1).toFloat() / numberOfCurves) * PI
            val point1 = PVector(
                center.x + (cos(point1AngleToCenter) * maxRadius),
                center.y + (sin(point1AngleToCenter) * maxRadius),
                0f
            )

            val oppositeAngle = point1AngleToCenter + PI

            val point2 = PVector(
                center.x + (cos(oppositeAngle) * maxRadius),
                center.y + (sin(oppositeAngle) * maxRadius),
                0f
            )

            Curve(point1 = point1, point2 = point2)
        }
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)
        beginDraw(pGraphics)

        pGraphics.stroke(1f)

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
            pGraphics.fill(0.5f, 1f, 1f, 1f)
            pGraphics.circle(curve.point2.x, curve.point2.y, 8f)
        }

        pGraphics.stroke(1f)

        pGraphics.line(
            curve.point1.x, curve.point1.y, curve.point1.z,
            curve.point2.x, curve.point2.y, curve.point2.z,
        )
    }

    private companion object {

        const val PI = kotlin.math.PI.toFloat()

    }
}