package eu.ezytaget.processing.reaction_diffusion

import processing.core.PApplet
import processing.core.PVector


fun PVector.rotateY(theta: Float): PVector {
    val tempX = x
    x = x * PApplet.cos(theta) - z * PApplet.sin(theta)
    z = tempX * PApplet.sin(theta) + z * PApplet.cos(theta)
    return this
}

fun PVector.rotateZ(theta: Float): PVector {
    val tempX = x
    x = x * PApplet.cos(theta) - y * PApplet.sin(theta)
    y = tempX * PApplet.sin(theta) + y * PApplet.cos(theta)
    return this
}