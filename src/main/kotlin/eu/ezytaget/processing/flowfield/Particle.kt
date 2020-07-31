// Based on Particle.pde by Daniel Shiffman:
// http://youtube.com/thecodingtrain
// http://codingtra.in
//
// Coding Challenge #24: Perlin Noise Flow  Field
// https://youtu.be/BjoM9oKOAKY

package eu.ezytaget.processing.flowfield

import processing.core.PVector
import kotlin.math.floor
import kotlin.math.max

class Particle internal constructor(var pos: PVector, var maxSpeed: Float) {

    private var vel: PVector = PVector()
    private var acc: PVector = PVector()
    var previousPos: PVector = pos.copy()

    fun update(startX: Float = 0f, startY: Float = 0f, endX: Float, endY: Float, flowField: FlowField) {
        updatePosition()
        wrapAroundEdges(endX = endX, endY = endY)
        follow(flowField)
    }

    fun show(pApplet: PApplet, maxColorValue: Float = 1f) {
        pApplet.stroke(maxColorValue, maxColorValue / 100f)
        pApplet.strokeWeight(1f)
        pApplet.line(pos.x, pos.y, previousPos.x, previousPos.y)
        //point(pos.x, pos.y);
        updatePreviousPos()
    }

    private fun follow(flowField: FlowField) {
        val x = floor(pos.x / flowField.scale).toInt()
        val y = floor(pos.y / flowField.scale).toInt()
        val index = x + y * flowField.cols
        val force = flowField.vectors[index]
        applyForce(force)
    }

    private fun applyForce(force: PVector?) {
        acc.add(force)
    }

    private fun updatePosition() {
        pos.add(vel)
        vel.limit(maxSpeed)
        vel.add(acc)
        acc.mult(0f)
    }

    private fun wrapAroundEdges(startX: Float = 0f, startY: Float = 0f, endX: Float, endY: Float) {
        if (pos.x > endX) {
            pos.x = startX
            updatePreviousPos()
        } else if (pos.x < startX) {
            pos.x = endX
            updatePreviousPos()
        }

        if (pos.y > endY) {
            pos.y = startY
            updatePreviousPos()
        } else if (pos.y < startY) {
            pos.y = endY
            updatePreviousPos()
        }
    }

    private fun updatePreviousPos() {
        previousPos.x = pos.x
        previousPos.y = pos.y
    }

}
