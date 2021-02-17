package eu.ezytaget.processing.kotlin_template.realms

import processing.core.PApplet
import processing.core.PGraphics
import kotlin.random.Random

abstract class Realm(var random: Random = Random.Default) {

    open fun setup(pGraphics: PGraphics) {}

    fun setup(pApplet: PApplet) {
        setup(pApplet.graphics)
    }

    open fun update(pApplet: PApplet) {}

    open fun drawIn(pGraphics: PGraphics) {}

    fun drawIn(pApplet: PApplet) {
        drawIn(pApplet.graphics)
    }

    open fun handleMouseClick(button: Int, mouseX: Int, mouseY: Int, pApplet: PApplet) {}

}