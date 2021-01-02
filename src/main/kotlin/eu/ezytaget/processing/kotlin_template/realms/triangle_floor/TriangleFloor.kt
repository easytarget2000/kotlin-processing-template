package eu.ezytaget.processing.kotlin_template.realms.triangle_floor

import eu.ezytaget.processing.kotlin_template.nextFloat
import processing.core.PApplet
import processing.core.PConstants.CENTER
import processing.core.PConstants.TRIANGLE
import processing.core.PShape
import processing.core.PVector
import kotlin.math.PI
import kotlin.random.Random


class TriangleFloor {

    var maxVelocity = 0.2f

    var random = Random.Default

    var numberOfStartItems = 3

    private val drivers = mutableListOf<PolygonDriver>()

    fun start(pApplet: PApplet) {
        val width = pApplet.width.toFloat()
        val height = pApplet.height.toFloat()

        (0 until numberOfStartItems).forEach { _ ->
            addItem(pApplet, width / 2f, height / 2f)//random.nextFloat(width), random.nextFloat(height))
        }
    }

    fun handleMouseClick(pMouseButton: Int, mouseX: Int, mouseY: Int, pApplet: PApplet) {
        addItem(pApplet, mouseX.toFloat(), mouseY.toFloat())
    }

    fun addItem(pApplet: PApplet, x: Float, y: Float) {
        addItem(pApplet, PVector(x, y))
    }

    fun addItem(pApplet: PApplet, position: PVector) {
        val velocity = PVector(
            maxVelocity / 2f - random.nextFloat(maxVelocity),
            maxVelocity / 2f - random.nextFloat(maxVelocity),
            0f
        )
        val angularVelocity = PI.toFloat() * random.nextFloat(0.001f)
        val maxJitter = maxVelocity / 16f
        val polygonDriver = PolygonDriver(
            buildShape(pApplet = pApplet, position = position),
            position,
            velocity,
            angularVelocity,
            maxJitter,
            random
        )

        drivers.add(polygonDriver)
    }

    fun updateAndDrawIn(pApplet: PApplet) {
        pApplet.push()

        pApplet.strokeWeight(32f)

        drivers.forEach {
            it.update()
            it.drawIn(pApplet)
        }

        pApplet.pop()
    }

    private fun buildShape(pApplet: PApplet, position: PVector): PShape {
        val shapeX1 = 0f
        val shapeY1 = 0f
        val shapeX2 = 400f
        val shapeY2 = 64f
        val shapeX3 = 320f
        val shapeY3 = 512f

        val shape: PShape = pApplet.createShape(
            TRIANGLE,
            shapeX1,
            shapeY1,
            shapeX2,
            shapeY2,
            shapeX3,
            shapeY3
        )
//        shape.translate(position.x, position.y)

        shape.stroke(0.5f)
        // The last 3 values are a workaround for Shape#rotate() on PMatrix3D.
        shape.rotate(random.nextFloat() * TWO_PI, 0f, 0f, 1f)

        return shape
    }

    companion object {
        private const val TWO_PI = PI.toFloat() * 2f
    }
}