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
    var separationWeight: Float = 1.2f,
    numberOfSegments: Int = 19,
    var segmentLength: Float = 10f
) {

    var lastDrawnPosition = position

    private var rootSegment: Segment

    init {
        var current = Segment(position.x, position.y, segmentLength, i = 0)

        for (i in 0..numberOfSegments) {
            val next = Segment(current, segmentLength, i)
            current.child = next
            current = next
        }
        rootSegment = current
    }

    // TODO: Make edges reflective instead of wrapping around.
    fun edges(width: Float, height: Float) {
        if (position.x > width) {
            position.x = 0f
        } else if (position.x < 0f) {
            position.x = width
        }

        if (position.y > height) {
            position.y = 0f
        } else if (position.y < 0f) {
            position.y = height
        }
    }

    fun flock(boids: List<Boid>) {
        val perceptionRadius = 10
        val alignmentVector = PVector()
        val separationVector = PVector()
        val cohesionVector = PVector()

        var total = 0
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

        rootSegment.follow(position.x, position.y)
        rootSegment.update()

        var next: Segment? = rootSegment.parent
        while (next != null) {
            next.follow()
            next.update()
            next = next.parent
        }
    }

    fun show(pApplet: PApplet) {
//        pApplet.strokeWeight(2f)
//        pApplet.stroke(1f, 0.5f)
//        pApplet.line(lastDrawnPosition.x, lastDrawnPosition.y, position.x, position.y)
//        lastDrawnPosition = position.copy()

        var next: Segment? = rootSegment.parent
        while (next != null) {
            next.show(pApplet)
            next = next.parent
        }
    }

}