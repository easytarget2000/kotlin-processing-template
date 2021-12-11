// Based on Julia Set by
// The Coding Train / Daniel Shiffman
// https://thecodingtrain.com/CodingChallenges/022-juliaset.html
// https://youtu.be/fAsaSkmbF5s
// https://editor.p5js.org/codingtrain/sketches/G6qbMmaI

package eu.ezytarget.processing_template.realms.julia_set

import kotlin.math.cos
import kotlin.math.sin

class JuliaSet(
        val scaleWidth: Float,
        val scaleHeight: Float,
        private var cAFactor: Float = 1f,
        private var cBFactor: Float = -1f,
        private var angle: Float = 0f,
        private val angleVelocity: Float = 0.05f,
        private val aAngleFactor: Float = -2.1f
) {

    val cA: Float
        get() = cos(angle * aAngleFactor)

    val cB: Float
        get() = sin(angle)

    fun update() {
        angle += angleVelocity
    }

}
