package eu.ezytarget.processingtemplate.realms.vaypr

import processing.core.PVector
import processing.core.PVector.sub
import kotlin.random.Random

data class Particle(
        val id: Int,
        val position: PVector,
        var pushForce: Float,
        var radius: Float,
        var gravityToNext: Float,
        var preferredDistanceToNext: Float,
        var maxInteractionDistance: Float,
        var maxJitter: Float
) {
    lateinit var next: Particle

    private val maxJitterHalf = maxJitter / 2f

    fun update(random: Random) {
        position.add(jitter(random))

        val velocity = PVector(0f, 0f, 0f)
        var otherParticle = next
        do {
            val vectorToOther = sub(otherParticle.position, position)
            val distanceToOther = vectorToOther.mag()
            if (distanceToOther > maxInteractionDistance) {
                otherParticle = otherParticle.next
                continue
            }

            val force = if (otherParticle == next) {
                if (distanceToOther > preferredDistanceToNext) {
                    distanceToOther / pushForce
                } else {
                    gravityToNext
                }
            } else {
                if (distanceToOther < radius) {
                    -radius;
                } else {
                    -(pushForce / distanceToOther);
                }
            }

            vectorToOther.setMag(force)
            velocity.add(vectorToOther)

            otherParticle = otherParticle.next
        } while (otherParticle != this)

        position.add(velocity)
    }

    private fun force(distanceToOther: Float) = if (distanceToOther < preferredDistanceToNext) {
        preferredDistanceToNext / distanceToOther
    } else {
        -(distanceToOther / preferredDistanceToNext)
    } * 0.01f

    private fun jitter(random: Random) = PVector(
            (random.nextFloat() * maxJitter) - maxJitterHalf,
            (random.nextFloat() * maxJitter) - maxJitterHalf,
            0f
    )
}