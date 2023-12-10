package eu.ezytarget.processingtemplate

import processing.core.PApplet

class PerlinNoiseSource(private val pApplet: PApplet) {

    fun next(x: Float) = pApplet.noise(x)

    fun next(x: Float, y: Float) = pApplet.noise(x, y)

    fun next(x: Float, y: Float, z: Float) = pApplet.noise(x, y, z)

}