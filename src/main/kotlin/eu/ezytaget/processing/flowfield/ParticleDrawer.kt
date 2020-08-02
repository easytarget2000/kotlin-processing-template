package eu.ezytaget.processing.flowfield

class ParticleDrawer(var maxColorValue: Float = 1f) {

    var hue = maxColorValue * 0.8f
    var hueVelocity = 0.083f
    var maxHue = maxColorValue
    var minHue = maxColorValue * 0.4f

    var saturation = maxColorValue * 0.7f
    var brightness = maxColorValue
    var alpha = maxColorValue * 0.1f

    fun draw(particle: Particle, pApplet: PApplet) {
        pApplet.stroke(
                hue,
                saturation,
                brightness,
                alpha
        )
        pApplet.strokeWeight(1f)
        pApplet.line(
                particle.x,
                particle.y,
                particle.previousX,
                particle.previousY
        )
        particle.updatePreviouslyDrawnPos()
    }

    fun update() {
        hue += hueVelocity
        if (hue > maxHue) {
            hue = minHue
        } else if (hue < minHue) {
            hue = maxHue
        }
    }

}