package eu.ezytarget.processingtemplate

internal data class Color(
    var value1: Float,
    var value2: Float,
    var value3: Float,
    var alpha: Float,
) {
    companion object {
        val BLACK_SOLID = Color(0f, 0f, 0f, 1f)
        val ZERO = Color(0f, 0f, 0f, 0f)
    }
}
