// Based on Particle.pde by Daniel Shiffman:
// http://youtube.com/thecodingtrain
// http://codingtra.in
//
// Coding Challenge #24: Perlin Noise Flow  Field
// https://youtu.be/BjoM9oKOAKY

package eu.ezytaget.processing.flowfield

import processing.core.PVector
import kotlin.math.floor

class Particle internal constructor(
        startPosition: PVector,
        private var maxSpeed: Float
) {

    private var vel: PVector = PVector()
    private var acc: PVector = PVector()
    private var position = startPosition
    private var previousPos: PVector = position.copy()

    val x
        get() = position.x

    val y
        get() = position.y

    val previousX
        get() = previousPos.x

    val previousY
        get() = previousPos.y

    fun update(startX: Float = 0f, startY: Float = 0f, endX: Float, endY: Float, flowField: FlowField) {
        updatePosition()
        wrapAroundEdges(startX = startX, startY = startY, endX = endX, endY = endY)
        follow(flowField)
    }

    private fun follow(flowField: FlowField) {
        val x = floor(position.x / flowField.scale).toInt()
        val y = floor(position.y / flowField.scale).toInt()
        val index = x + y * flowField.cols
        val force = flowField.vectors[index]
        applyForce(force)
    }

    private fun applyForce(force: PVector?) {
        acc.add(force)
    }

    private fun updatePosition() {
        position.add(vel)
        vel.limit(maxSpeed)
        vel.add(acc)
        acc.mult(0f)
    }

    private fun wrapAroundEdges(startX: Float, startY: Float, endX: Float, endY: Float) {
        if (position.x > endX) {
            position.x = startX
            updatePreviouslyDrawnPos()
        } else if (position.x < startX) {
            position.x = endX
            updatePreviouslyDrawnPos()
        }

        if (position.y > endY) {
            position.y = startY
            updatePreviouslyDrawnPos()
        } else if (position.y < startY) {
            position.y = endY
            updatePreviouslyDrawnPos()
        }
    }

    fun updatePreviouslyDrawnPos() {
        previousPos = position.copy()
    }

}
