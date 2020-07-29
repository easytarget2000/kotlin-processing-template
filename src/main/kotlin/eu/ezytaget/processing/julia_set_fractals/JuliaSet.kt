// Based on Julia Set by
// The Coding Train / Daniel Shiffman
// https://thecodingtrain.com/CodingChallenges/022-juliaset.html
// https://youtu.be/fAsaSkmbF5s
// https://editor.p5js.org/codingtrain/sketches/G6qbMmaI

package eu.ezytaget.processing.julia_set_fractals

import kotlin.math.cos
import kotlin.math.sin

class JuliaSet(
        val scaleWidth: Float,
        val scaleHeight: Float,
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
