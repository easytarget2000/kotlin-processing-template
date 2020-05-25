package eu.ezytaget.processing.kotlin_template

import processing.core.PConstants

class PApplet : processing.core.PApplet() {

    override fun settings() {
        size(WIDTH, HEIGHT, RENDERER)
    }

    override fun setup() {
    }

    override fun draw() {
    }

    companion object {
        private const val WIDTH = 800
        private const val HEIGHT = 600
        private const val RENDERER = PConstants.P3D

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }
    }
}