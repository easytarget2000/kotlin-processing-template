package eu.ezytaget.processing.kotlin_template

import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector
import kotlin.random.Random

class SphereDrawer {

    var random: Random = Random.Default

    private val millis
        get() = System.currentTimeMillis()

    fun draw(pGraphics: PGraphics, spheres: Iterable<Sphere>, radiusFactor: Float, drawNoiseSpheres: Boolean) {
        pGraphics.push()
        pGraphics.translate(pGraphics.width / 2f, pGraphics.height / 2f)
//        xRotation += xRotationVelocity
//        pGraphics.rotateX(xRotation)
//        zRotation += zRotationVelocity
//        pGraphics.rotateZ(zRotation)

        val baseRotation = ((millis % 100_000) / 100_000f) * PApplet.TWO_PI

        pGraphics.noFill()

        pGraphics.sphereDetail(16)

        spheres.forEach {
            pGraphics.pushMatrix()
            pGraphics.rotateY(baseRotation * it.rotationSpeed)
            pGraphics.stroke(it.colorValue1, it.colorValue2, it.colorValue3, it.alpha)
            val radius = it.radius * radiusFactor
            if (drawNoiseSpheres) {
                noiseSphere(pGraphics, radius, randomSeed = it.randomSeed)
            } else {
                pGraphics.sphere(radius)
            }
            pGraphics.popMatrix()
        }
        pGraphics.pop()
    }

    private fun noiseSphere(pGraphics: PGraphics, radius: Float, randomSeed: Long, numberOfPoints: Int = 4096 * 2) {
        random = Random(randomSeed)
        repeat(numberOfPoints) {
            val randomVector = PVector(
                    random.nextFloat(-100f, 100f),
                    random.nextFloat(-100f, 100f),
                    random.nextFloat(-100f, 100f)
            ).setMag(radius)
            pGraphics.point(randomVector.x, randomVector.y, randomVector.z)
        }
    }
}