package eu.ezytaget.processing.kotlin_template.realms.triangle_floor

import eu.ezytaget.processing.kotlin_template.nextFloat
import jogamp.graph.font.typecast.TypecastRenderer
import jogamp.graph.font.typecast.TypecastRenderer.buildShape
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PConstants.CENTER

import processing.core.PVector
import kotlin.math.PI
import kotlin.random.Random
import processing.core.PConstants.TRIANGLE

import processing.opengl.PShapeOpenGL.createShape

import processing.core.PShape
import processing.opengl.PShapeOpenGL


class TriangleFloor {

    var maxVelocity = 0.2f

    var random = Random.Default

    private val drivers = mutableListOf<PolygonDriver>()

    fun addItem(position: PVector) {
        val velocity = PVector(
            maxVelocity / 2f - random.nextFloat(maxVelocity),
            maxVelocity / 2f - random.nextFloat(maxVelocity),
            0f
        )
        val angularVelocity: Float = PI * random(0.001f)
        val maxJitter = maxVelocity / 16f
        val polygonDriver = PolygonDriver(
            buildShape(position),
            velocity,
            angularVelocity,
            maxJitter,
            random
        )

        drivers.plus(polygonDriver)
    }

    private fun buildShape(pApplet: PApplet, position: PVector): PShape? {
        pApplet.shapeMode(CENTER)

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
        shape.translate(position.x, position.y)
        shape.setFill(-0x1)
        shape.setStroke(-0x1000000)
        shape.rotate(random.nextFloat() * TWO_PI)

        return shape
    }

    companion object {
        private const val TWO_PI = PI.toFloat() * 2f
    }
}