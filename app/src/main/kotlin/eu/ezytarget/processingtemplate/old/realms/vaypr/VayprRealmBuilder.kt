package eu.ezytarget.processing_template.realms.vaypr

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder
import processing.core.PVector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class VayprRealmBuilder : RealmBuilder {

    var worldWidth = 100f

    var worldHeight = 100f

    var originX = worldWidth / 2f

    var originY = worldHeight / 2f

    var numberOfParticles = 256

    var z = 0f

    var verbose = false;

    var preferredDistancePushFactor = 1.01f

    override fun build(): Realm {
        var firstParticle: Particle? = null

        val smallestWorldLength = min(worldWidth, worldHeight)

        val offsetLength = smallestWorldLength / 8f
        val twoPi = PI.toFloat() * 2f
        val maxParticleJitter = smallestWorldLength / 512f
        val particleRadius = smallestWorldLength / 512f
        val particlePushForce = particleRadius * 4f
        val particleGravityToNext = -particleRadius * 0.5f
        val maxParticleInteractionDistance = smallestWorldLength / 6f

        var lastParticle: Particle? = null
        for (particleIndex in 0 until numberOfParticles) {
            val progress = particleIndex.toFloat() / numberOfParticles.toFloat()
            if (verbose) {
                println("ParticleField.Builder: build(): progress: $progress")
            }
            val offsetX = offsetLength * cos(x = progress * twoPi)
            val offsetY = offsetLength * sin(x = progress * twoPi)

            val particlePosition = PVector(originX + offsetX, originY + offsetY, z)
            val particle = Particle(
                id = particleIndex,
                position = particlePosition,
                radius = particleRadius,
                pushForce = particlePushForce,
                gravityToNext = particleGravityToNext,
                preferredDistanceToNext = 0f,   // Will be set later.
                maxInteractionDistance = maxParticleInteractionDistance,
                maxJitter = maxParticleJitter
            )

            if (firstParticle == null) {
                firstParticle = particle
                if (verbose) {
                    println("ParticleField.Builder: build(): firstParticle: $firstParticle")
                }
            } else {
                lastParticle!!.next = particle
                particle.preferredDistanceToNext =
                    particle.position.dist(lastParticle.position) * preferredDistancePushFactor

                if (verbose) {
                    println("ParticleField.Builder: build(): particle: $particle")
                }
            }

            lastParticle = particle
        }

        lastParticle!!.next = firstParticle!!
        firstParticle.preferredDistanceToNext =
            firstParticle.position.dist(lastParticle.position) * preferredDistancePushFactor

        if (verbose) {
            println("ParticleField.Builder: build(): firstParticle: $firstParticle")
        }

        return VayprRealm(firstParticle, originX, originY)
    }
}