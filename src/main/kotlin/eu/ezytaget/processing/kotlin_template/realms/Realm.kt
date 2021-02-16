package eu.ezytaget.processing.kotlin_template.realms

import processing.core.PApplet

interface Realm {

    fun setup(pApplet: PApplet)

    fun drawIn(pApplet: PApplet)

    fun handleMouseClick(button: Int, mouseX: Int, mouseY: Int, pApplet: PApplet)


}