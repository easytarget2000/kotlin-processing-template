package eu.ezytarget.processingtemplate.realms

import processing.core.PApplet
import processing.core.PGraphics
import kotlin.random.Random

abstract class Realm(var random: Random = Random.Default) {

    open fun setup(pApplet: PApplet, pGraphics: PGraphics = pApplet.graphics) { }

    open fun update(pApplet: PApplet) {}

    open fun drawIn(pGraphics: PGraphics) {}

    fun drawIn(pApplet: PApplet) {
        drawIn(pApplet.graphics)
    }

    open fun bounce(pApplet: PApplet) { }

    open fun handleMouseClick(button: Int, mouseX: Int, mouseY: Int, pApplet: PApplet) {}

    open fun setRandomStyle() { }

    protected fun beginDraw(pGraphics: PGraphics) {
        pGraphics.beginDraw()
        pGraphics.push()
    }

    protected fun endDraw(pGraphics: PGraphics) {
        pGraphics.pop()
        pGraphics.endDraw()
    }
}