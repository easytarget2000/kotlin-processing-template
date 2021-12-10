package eu.ezytarget.processing_template.realms.tree_realms

import eu.ezytarget.processing_template.realms.Realm
import processing.core.PApplet
import processing.core.PConstants.RADIUS
import processing.core.PGraphics
import kotlin.math.min
import kotlin.random.Random

class TreeRingsRealm(random: Random = Random.Default) : Realm(random) {

    var lineWidth = 16f

    var hue = 1f

    var saturation = 1f

    var brightness = 1f

    var alpha = 1f

    var fill = false

    var defaultRadiusFactor = 0.35f

    var maxRadiusFactor = 0.45f

    var currentRadiusFactor = defaultRadiusFactor

    var currentRadiusFactorVelocity = 0f

    var maxRadiusFactorVelocity = maxRadiusFactor / 100f

    override fun update(pApplet: PApplet) {
        super.update(pApplet)

        currentRadiusFactor += currentRadiusFactorVelocity
        if (currentRadiusFactor > maxRadiusFactor) {
            currentRadiusFactor = maxRadiusFactor
        }
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)
        beginDraw(pGraphics)

        val width = pGraphics.width.toFloat()
        val height = pGraphics.height.toFloat()

        val centerX = width / 2f
        val centerY = height / 2f

        val radius = min(width, height) * currentRadiusFactor

        if (fill) {
            pGraphics.noStroke()
            pGraphics.fill(hue, saturation, brightness, alpha)
        } else {
            pGraphics.strokeWeight(lineWidth)
            pGraphics.stroke(hue, saturation, brightness, alpha)
            pGraphics.noFill()
        }
        pGraphics.ellipseMode(RADIUS)

        pGraphics.circle(centerX, centerY, radius)

        endDraw(pGraphics)
    }

    override fun bounce(pApplet: PApplet) {
        super.bounce(pApplet)

        currentRadiusFactorVelocity = maxRadiusFactorVelocity
        currentRadiusFactor = defaultRadiusFactor
    }

    override fun setRandomStyle() {
        super.setRandomStyle()
        hue = random.nextFloat()
    }
}