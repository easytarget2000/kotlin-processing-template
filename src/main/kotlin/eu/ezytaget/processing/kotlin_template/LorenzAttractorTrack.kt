package eu.ezytaget.processing.kotlin_template

import processing.core.PVector

class LorenzAttractorTrack(
        initialPosition: PVector = PVector(0.1f, 0f, 0f),
        var scale: Float = 8f,
        var startHue: Float = 0f,
        var hueStepSize: Float = 0.1f,
        var sigma: Float = 10f,
        var rho: Float = 28f,
        var beta: Float = 8f / 3f
) {
    private val positions = mutableListOf<PVector>()
    private var lastPosition = initialPosition

    fun update(deltaTime: Float, steps: Int) {
        repeat((0 until steps).count()) {
            update(deltaTime)
        }
    }

    fun update(deltaTime: Float) {
        val movement = PVector(
                (sigma * (lastPosition.y - lastPosition.x)) * deltaTime,
                ((lastPosition.x * (rho - lastPosition.z)) - lastPosition.y) * deltaTime,
                ((lastPosition.x * lastPosition.y) - (beta * lastPosition.z)) * deltaTime
        )
        lastPosition = lastPosition.add(movement).copy()

        positions += lastPosition
    }

    fun draw(pApplet: PApplet, maxColorValue: Float = 1f) {
        pApplet.pushMatrix()
        pApplet.scale(scale)

        pApplet.beginShape()
        var hue = startHue
        positions.forEach {
            if (hue > maxColorValue) {
                hue = 0f
            }
            pApplet.stroke(hue, maxColorValue, maxColorValue, maxColorValue)

            pApplet.vertex(it.x, it.y, it.z)
        }
        pApplet.endShape()

        pApplet.popMatrix()
    }
}