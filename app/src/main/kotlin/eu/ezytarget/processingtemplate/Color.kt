package eu.ezytarget.processingtemplate

import kotlin.random.Random

internal data class Color(
    var value1: Float,
    var value2: Float,
    var value3: Float,
    var alpha: Float,
) {
    fun wrapAroundAdd(color2: Color, max: Float): Color {
        val paddedMax = max + 0.01f
        return Color(
            (this.value1 + color2.value1) % paddedMax,
            (this.value2 + color2.value2) % paddedMax,
            (this.value3 + color2.value3) % paddedMax,
            (this.alpha + color2.alpha) % paddedMax,
        )
    }

    companion object {
        val BLACK_SOLID = Color(0f, 0f, 0f, 1f)
        val ZERO = Color(0f, 0f, 0f, 0f)

        fun randomSolid(random: Random = Random.Default, max: Float) = Color(
            random.nextFloat(max),
            random.nextFloat(max),
            random.nextFloat(max),
            max,
        )
    }
}
