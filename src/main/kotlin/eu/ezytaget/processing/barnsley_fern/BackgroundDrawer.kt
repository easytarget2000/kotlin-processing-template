package eu.ezytaget.processing.barnsley_fern

import eu.ezytaget.processing.barnsley_fern.palettes.Palette
import kotlin.random.Random

class BackgroundDrawer(private val palette: Palette, var alpha: Float = 0.1f) {

    var rgbColor: Int = 0

    fun drawRandomColor(pApplet: PApplet, random: Random, alpha: Float = this.alpha) {
        rgbColor = palette.randomColor(random)
        draw(pApplet, alpha)
    }

    fun draw(pApplet: PApplet, alpha: Float = this.alpha) {
        if (alpha < 1f) {
            pApplet.noStroke()
            pApplet.fill(pApplet.hue(rgbColor), pApplet.saturation(rgbColor), pApplet.saturation(rgbColor), alpha)
            pApplet.rect(0f, 0f, pApplet.width.toFloat(), pApplet.height.toFloat())
        } else {
            pApplet.background(rgbColor)
        }
    }
}