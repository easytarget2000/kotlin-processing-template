// Based on Julia Set by
// The Coding Train / Daniel Shiffman
// https://thecodingtrain.com/CodingChallenges/022-juliaset.html
// https://youtu.be/fAsaSkmbF5s
// https://editor.p5js.org/codingtrain/sketches/G6qbMmaI

package eu.ezytarget.processing_template.realms.julia_set

import eu.ezytarget.processing_template.nextFloat
import eu.ezytarget.processing_template.realms.Realm
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import kotlin.random.Random

class JuliaSetRealm(maxColorValue: Float = 1f, random: Random = Random.Default): Realm(random) {

    var maxIterationsPerPoint = 16
    var minIterationsPerPointToDraw = maxIterationsPerPoint / 2
    var maxIterationsPerPointToDraw = maxIterationsPerPoint - 1
    var maxDivergence = 4f
    var hue = 0.9f
    var hueVelocity = 0.05f
    var maxHue = maxColorValue
    var minHue = maxColorValue * 0.7f
    var saturation = maxColorValue * 0.66f
    var brightness = maxColorValue * 0.7f
    var alpha = maxColorValue * 0.8f
    var pixelStepSize = 3

    private lateinit var set: JuliaSet

    override fun setup(pApplet: PApplet, pGraphics: PGraphics) {
        //initJuliaSet(): scaleWidth: 5.711875, scaleHeight: 3.569922, angle 4.8934402, angleVelocity: 0.05238408, aAngleFactor: -0.7362766
        //initJuliaSet(): scaleWidth: 3.7624247, scaleHeight: 2.3515155, angle 2.9225054, angleVelocity: 0.05977559, aAngleFactor: 0.59833264
        //initJuliaSet(): scaleWidth: 2.4918487, scaleHeight: 1.5574055, angle 5.00991, angleVelocity: -0.31708914, aAngleFactor: -0.54648113
        // NO: initJuliaSet(): scaleWidth: 4.356219, scaleHeight: 2.7226367, angle 5.680613, angleVelocity: 0.0038700998, aAngleFactor: 0.030716658
        val width = pGraphics.width
        val height = pGraphics.height

        val scaleWidth = random.nextFloat(from = 2.5f, until = 7f) // 5.5, 5.13, 2.23
        val scaleHeight = (scaleWidth * height.toFloat()) / width.toFloat() // 3.4, 3.21, 1.39
        val angle = random.nextFloat(from = 3f, until = PConstants.TWO_PI) // 0.5, 6.2, 4.19
        val angleVelocity = random.nextFloat(from = -0.05f, until = 0.05f) // 0.09, -0.29, -0.28
        val aAngleFactor = random.nextFloat(from = 1f, until = 2f) // 1.69, 0.8, -1.35

        set = JuliaSet(
                scaleWidth = scaleWidth,
                scaleHeight = scaleHeight,
                angle = angle,
                angleVelocity = angleVelocity,
                aAngleFactor = aAngleFactor
        )

        PApplet.println(
                "JuliaSetRealm: setup(): scaleWidth: $scaleWidth, scaleHeight: $scaleHeight, angle $angle, " +
                        "angleVelocity: $angleVelocity, aAngleFactor: $aAngleFactor"
        )
    }

    override fun update(pApplet: PApplet) {
        set.update()
    }

    override fun drawIn(pGraphics: PGraphics) {
        // Establish a range of values on the complex plane
        // A different range will allow us to "zoom" in or out on the fractal

        val scaleWidth = set.scaleWidth / pixelStepSize.toFloat()
        val scaleHeight = set.scaleHeight / pixelStepSize.toFloat()

        // Start at negative half the width and height
        val startX = -scaleWidth / 2f
        val startY = -scaleHeight / 2f

        // Make sure we can write to the pixels[] array.
        // Only need to do this once since we don't do any other drawing.
        pGraphics.loadPixels()

        // x goes from xmin to xmax
        val maxX = startX + scaleWidth
        // y goes from ymin to ymax
        val maxY = startY + scaleHeight

        // Calculate amount we increment x,y for each pixel
        val width = pGraphics.width
        val height = pGraphics.height
        val scaleXDelta = (maxX - startX) / width
        val scaleYDelta = (maxY - startY) / height

        // Start y
        var y = startY
        val ca = set.cA
        val cb = set.cB

        (0 until height step pixelStepSize).forEach { pixelY ->
            var x = startX
            (0 until width step pixelStepSize).forEach { pixelX ->
                var a = x
                var b = y
                var n = 0
                while (n < maxIterationsPerPoint) {
                    val aSquared = a * a
                    val bSquared = b * b

                    if (aSquared + bSquared > maxDivergence) {
                        break
                    }

                    val twoAB = 2f * a * b
                    a = aSquared - bSquared + ca
                    b = twoAB + cb
                    n++
                }

                if (n in minIterationsPerPointToDraw..maxIterationsPerPointToDraw) {
//                    val hue = sqrt(n.toFloat() / maxIterationsPerPoint)
                    setPixel(
                            pGraphics,
                            width,
                            pixelX,
                            pixelY,
                            pGraphics.color(hue, saturation, brightness, alpha)
                    )
                }

                x += scaleXDelta
            }

            y += scaleYDelta
        }

        pGraphics.updatePixels()

        hue += hueVelocity
        if (hue > maxHue) {
            hue = minHue
        }
    }

    override fun handleMouseClick(button: Int, mouseX: Int, mouseY: Int, pApplet: PApplet) {
    }

    companion object {
        private fun setPixel(pGraphics: PGraphics, width: Int, x: Int, y: Int, color: Int) {
            pGraphics.pixels[x + (y * width)] = color
        }
    }
}