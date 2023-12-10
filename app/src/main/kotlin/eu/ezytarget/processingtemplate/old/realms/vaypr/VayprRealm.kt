package eu.ezytarget.processing_template.realms.vaypr

import eu.ezytarget.processing_template.realms.Realm
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PConstants.LINES
import processing.core.PGraphics
import kotlin.random.Random

class VayprRealm(
    private val firstParticle: Particle,
    private val originX: Float,
    private val originY: Float,
) : Realm() {

    var particleAlpha = 0.1f

    var addParticleProbability = 1f / 1000f

    var maxNumOfParticles: Int = 256

    var drawLine = false

    var numberOfSlices: Int = 1

    var yRotationOffset: Float = 0f

    override fun update(pApplet: PApplet) {
        super.update(pApplet)
        var counter = 0

        var currentParticle = firstParticle
        do {
            if (++counter < maxNumOfParticles && addParticle(random)) {
                val newParticle = currentParticle.copy(id = random.nextInt())
                newParticle.next = currentParticle.next
                currentParticle.next = newParticle
            }

            update(currentParticle, random)
            currentParticle = currentParticle.next
        } while (currentParticle != firstParticle)
    }

    override fun drawIn(pGraphics: PGraphics) {
        pGraphics.pushMatrix()

        for (sliceIndex in 0 until numberOfSlices) {
            val yRotation = (sliceIndex.toFloat() / numberOfSlices.toFloat() * PConstants.TWO_PI) + yRotationOffset
            pGraphics.translate(originX / 2f, originY / 2f, 0f)
            pGraphics.rotateY(yRotation)
//            pApplet.translate(-originX / 2f, -originY / 2f, 0f)

//            drawIn(pApplet, drawLine)
            drawSlice(pGraphics)
        }

        pGraphics.popMatrix()
    }

    private fun drawSlice(pGraphics: PGraphics) {
        pGraphics.stroke(1f);
        pGraphics.strokeWeight(1f);

        if (drawLine) {
            pGraphics.beginShape(LINES)
        }
        var currentParticle = firstParticle
        do {
            if (drawLine) {
                pGraphics.vertex(originX - currentParticle.position.x, originY - currentParticle.position.y)
            } else {
                pGraphics.point(
                    currentParticle.position.x,
                    currentParticle.position.y,
                    currentParticle.position.z
                );
            }
            currentParticle = currentParticle.next
        } while (currentParticle != firstParticle)

        if (drawLine) {
            pGraphics.endShape()
        }
    }

    private fun update(particle: Particle, random: Random) {
        particle.update(random = random)
    }


    private fun addParticle(random: Random) = random.nextFloat() < addParticleProbability

    companion object {
        private const val VERBOSE = false
        private const val PREFERRED_DISTANCE_PUSH_FACTOR = 1.01f
        private var debugDrawIDs = listOf<Int>()
    }
}