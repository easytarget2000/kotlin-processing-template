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

import javax.swing.Spring.height


class Boid(
    var position: PVector,
    var velocity: PVector,
    var acceleration: PVector,
    var maxForce: Int = 1,
    var maxSpeed: Int = 4
) {

    var alignmentWeight = 0.5f

    var cohesionWeight = 1f

    var separationWeight = 1f

    fun edges(width: Float, height: Float) {
        if (position.x > width) {
            position.x = 0f
        } else if (position.x < 0) {
            position.x = width
        }

        if (position.y > height) {
            position.y = 0f
        } else if (position.y < 0) {
            position.y = height
        }
    }

    fun align(boids: List<Boid>): PVector {
        val perceptionRadius = 50
        val steering = PVector()
        var total = 0
        for (other in boids) {
            if (other == this) {
                continue
            }
            val d = dist(position.x, position.y, other.position.x, other.position.y)
            if (d < perceptionRadius) {
                steering.add(other.velocity)
                total++
            }
        }
        if (total > 0) {
            steering.div(total.toFloat())
            steering.setMag(maxSpeed.toFloat())
            steering.sub(velocity)
            steering.limit(maxForce.toFloat())
        }
        return steering
    }

    fun separation(boids: List<Boid>): PVector {
        val perceptionRadius = 50
        val steering = PVector()
        var total = 0
        for (other in boids) {
            if (other == this) {
                continue
            }

            val d= dist(position.x, position.y, other.position.x, other.position.y)
            if (d < perceptionRadius) {
                val diff = PVector.sub(position, other.position)
                diff.div(d * d)
                steering.add(diff)
                total++
            }
        }
        if (total > 0) {
            steering.div(total.toFloat())
            steering.setMag(maxSpeed.toFloat())
            steering.sub(velocity)
            steering.limit(maxForce.toFloat())
        }
        return steering
    }

    fun cohesion(boids: List<Boid>): PVector {
        val perceptionRadius = 100
        val steering = PVector()
        var total = 0
        for (other in boids) {
            if (other == this) {
                continue
            }
            val d: Float = dist(position.x, position.y, other.position.x, other.position.y)
            if (d < perceptionRadius) {
                steering.add(other.position)
                total++
            }
        }
        if (total > 0) {
            steering.div(total.toFloat())
            steering.sub(position)
            steering.setMag(maxSpeed.toFloat())
            steering.sub(velocity)
            steering.limit(maxForce.toFloat())
        }
        return steering
    }

    fun flock(boids: List<Boid>) {
        val alignment = align(boids)
        val cohesion = cohesion(boids)
        val separation = separation(boids)
        alignment.mult(alignmentWeight)
        cohesion.mult(cohesionWeight)
        separation.mult(separationWeight)
        acceleration.add(alignment)
        acceleration.add(cohesion)
        acceleration.add(separation)
    }

    fun update() {
        position.add(velocity)
        velocity.add(acceleration)
        velocity.limit(maxSpeed.toFloat())
        acceleration.mult(0f)
    }

    fun show(pApplet: PApplet) {
        pApplet.strokeWeight(2f)
        pApplet.stroke(1f, 0.7f)
        pApplet.point(position.x, position.y)
    }

}