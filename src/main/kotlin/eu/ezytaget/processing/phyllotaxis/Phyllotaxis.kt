package eu.ezytaget.processing.phyllotaxis

data class Phyllotaxis(
        var phiDegrees: Float = 97.5f,
        var phiDegreesVelocity: Float = 0.01f,
        var n: Int = 512,
        var nVelocity: Int = 0,
        var c: Int = 12
) {
    fun update() {
        n += 1
        phiDegrees += phiDegreesVelocity
    }
}