/*
Based on Flocking project by Daniel Shiffman
https://thecodingtrain.com/CodingChallenges/124-flocking-boids.html
https://youtu.be/mhjuuHl6qHM
https://editor.p5js.org/codingtrain/sketches/ry4XZ8OkN
 */

package eu.ezytaget.processing.kotlin_template.boids

import processing.core.PApplet
import processing.core.PApplet.dist
import processing.core.PVector
import kotlin.math.abs


class Boid(
    var position: PVector,
    var velocity: PVector,
    var acceleration: PVector,
    var maxForce: Float = 1f,
    var maxSpeed: Float = 16f,
    var alignmentWeight: Float = 0.5f,
    var cohesionWeight: Float = 1f,
    var separationWeight: Float = 1.2f
) {

    var lastDrawnPosition = position

    fun edges(width: Float, height: Float) {
        if (position.x > width) {
            position.x = width
        } else if (position.x < 0f) {
            position.x = 0f
        }

        if (position.y > height) {
            position.y = height
        } else if (position.y < 0f) {
            position.y = 0f
        }
    }

    fun flock(boids: List<Boid>) {
        val perceptionRadius = 10
        val alignmentVector = PVector()
        val separationVector = PVector()
        val cohesionVector = PVector()

        var total = 0
        // CONTINUE HERE
        for (other in boids) {
            if (other == this) {
                continue
            }

            if (abs(position.x - other.position.x) > perceptionRadius) {
                continue
            }

            if (abs(position.y - other.position.y) > perceptionRadius) {
                continue
            }

            val d = dist(position.x, position.y, other.position.x, other.position.y)
            if (d < perceptionRadius) {
                alignmentVector.add(other.velocity)
                total++

                val diff = PVector.sub(position, other.position)
                diff.div(d * d)
                separationVector.add(diff)

                cohesionVector.add(other.position)
            }
        }
        if (total > 0) {
            val sumDivisor = total.toFloat()
            alignmentVector.div(sumDivisor)
            alignmentVector.setMag(maxSpeed)
            alignmentVector.sub(velocity)
            alignmentVector.limit(maxForce)

            separationVector.div(sumDivisor)
            separationVector.setMag(maxSpeed)
            separationVector.sub(velocity)
            separationVector.limit(maxForce)

            cohesionVector.div(sumDivisor)
            cohesionVector.sub(position)
            cohesionVector.setMag(maxSpeed)
            cohesionVector.sub(velocity)
            cohesionVector.limit(maxForce)
        }

        alignmentVector.mult(alignmentWeight)
        cohesionVector.mult(cohesionWeight)
        separationVector.mult(separationWeight)
        acceleration.add(alignmentVector)
        acceleration.add(cohesionVector)
        acceleration.add(separationVector)
    }

    fun update() {
        position.add(velocity)
        velocity.add(acceleration)
        velocity.limit(maxSpeed)
        acceleration.mult(0f)
    }

    fun show(pApplet: PApplet) {
        pApplet.strokeWeight(2f)
        pApplet.stroke(1f, 0.5f)
        pApplet.line(lastDrawnPosition.x, lastDrawnPosition.y, position.x, position.y)
        lastDrawnPosition = position.copy()
    }

}