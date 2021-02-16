package eu.ezytaget.processing.kotlin_template.realms

import processing.core.PApplet
import processing.core.PGraphics

interface Realm {

    fun setup(pGraphics: PGraphics)

    fun update(pApplet: PApplet)

    fun drawIn(pGraphics: PGraphics)

    fun handleMouseClick(button: Int, mouseX: Int, mouseY: Int, pApplet: PApplet)

}