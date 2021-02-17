package eu.ezytaget.processing.kotlin_template.realms

import processing.core.PApplet
import processing.core.PGraphics

abstract class Realm {

    fun setup(pGraphics: PGraphics) { }

    fun setup(pApplet: PApplet) {
        setup(pApplet.graphics)
    }

    fun update(pApplet: PApplet) { }

    fun drawIn(pGraphics: PGraphics) { }

    fun drawIn(pApplet: PApplet) {
        drawIn(pApplet.graphics)
    }

    fun handleMouseClick(button: Int, mouseX: Int, mouseY: Int, pApplet: PApplet) { }

}