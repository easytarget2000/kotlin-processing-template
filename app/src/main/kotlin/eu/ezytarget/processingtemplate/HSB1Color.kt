package eu.ezytarget.processingtemplate

import kotlin.random.Random

internal data class HSB1Color(
    var hue: Float,
    var saturation: Float,
    var brightness: Float,
    var alpha: Float,
) {
    fun wrapAroundAdd(color2: HSB1Color): HSB1Color {
        return HSB1Color(
            (this.hue + color2.hue) % PADDED_MAX,
            (this.saturation + color2.saturation) % PADDED_MAX,
            (this.brightness + color2.brightness) % PADDED_MAX,
            (this.alpha + color2.alpha) % PADDED_MAX,
        )
    }

    companion object {
        val BLACK_SOLID = HSB1Color(0f, 0f, 0f, 1f)
        val ZERO = HSB1Color(0f, 0f, 0f, 0f)
        private const val PADDED_MAX = 1.01f

        fun randomSolid(random: Random = Random.Default) = HSB1Color(
            random.nextFloat(),
            random.nextFloat(),
            random.nextFloat(),
            1f,
        )
    }
}
