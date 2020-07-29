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