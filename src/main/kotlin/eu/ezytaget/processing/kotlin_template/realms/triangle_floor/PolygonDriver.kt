package eu.ezytaget.processing.kotlin_template.realms.triangle_floor

import processing.core.PApplet
import processing.core.PVector

import processing.core.PShape
import kotlin.random.Random


internal class PolygonDriver (
    private val shape: PShape,
    private val velocity: PVector,
    private val angularVelocity: Float,
    private val maxJitter: Float,
    private val random: Random = Random.Default
) {

    private val nextJitter: Float
        get() = maxJitter / 2f - (random.nextFloat() * maxJitter)

    fun update() {
        shape.translate(velocity.x, velocity.y)
        velocity.add(nextJitter, nextJitter, 0f)

        // The last 3 values are a workaround for Shape#rotate() on PMatrix3D.
        shape.rotate(angularVelocity, 0f, 0f, 1f)
    }

    fun drawIn(pApplet: PApplet) {
        pApplet.shape(shape)
    }

}