// Based on Julia Set by
// The Coding Train / Daniel Shiffman
// https://thecodingtrain.com/CodingChallenges/022-juliaset.html
// https://youtu.be/fAsaSkmbF5s
// https://editor.p5js.org/codingtrain/sketches/G6qbMmaI

package eu.ezytaget.processing.julia_set_fractals

import kotlin.math.cos
import kotlin.math.sin

class JuliaSet(angle: Float = 0f, private val angleVelocity: Float = 0.1f) {

    private var angle: Float = angle

    val ca: Float
        get() = cos(angle * 3.213f)

    val cb: Float
        get() = sin(angle)


    fun update() {
        angle += angleVelocity
    }


}