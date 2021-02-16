// Based on Julia Set by
// The Coding Train / Daniel Shiffman
// https://thecodingtrain.com/CodingChallenges/022-juliaset.html
// https://youtu.be/fAsaSkmbF5s
// https://editor.p5js.org/codingtrain/sketches/G6qbMmaI

package eu.ezytaget.processing.julia_set_fractals

class JuliaSetDrawer(var maxColorValue: Float = 1f) {

    var maxIterationsPerPoint = 16
    var minIterationsPerPointToDraw = maxIterationsPerPoint / 2
    var maxIterationsPerPointToDraw = maxIterationsPerPoint - 1
    var maxDivergence = 4f
    var hue = 0.9f
    var hueVelocity = 0.01f
    var maxHue = maxColorValue
    var minHue = maxColorValue * 0.7f
    var saturation = maxColorValue * 0.66f
    var brightness = maxColorValue * 0.7f
    var alpha = maxColorValue * 0.8f

    fun draw(juliaSet: JuliaSet, pApplet: PApplet, pixelStepSize: Int = 1) {
        // Establish a range of values on the complex plane
        // A different range will allow us to "zoom" in or out on the fractal

        val scaleWidth = juliaSet.scaleWidth / pixelStepSize.toFloat()
        val scaleHeight = juliaSet.scaleHeight / pixelStepSize.toFloat()

        // Start at negative half the width and height
        val startX = -scaleWidth / 2f
        val startY = -scaleHeight / 2f

        // Make sure we can write to the pixels[] array.
        // Only need to do this once since we don't do any other drawing.
        pApplet.loadPixels()

        // x goes from xmin to xmax
        val maxX = startX + scaleWidth
        // y goes from ymin to ymax
        val maxY = startY + scaleHeight

        // Calculate amount we increment x,y for each pixel
        val width = pApplet.width
        val height = pApplet.height
        val scaleXDelta = (maxX - startX) / width
        val scaleYDelta = (maxY - startY) / height

        // Start y
        var y = startY
        val ca = juliaSet.cA
        val cb = juliaSet.cB

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
                            pApplet,
                            width,
                            pixelX,
                            pixelY,
                            pApplet.color(hue, saturation, brightness, alpha)
                    )
                }

                x += scaleXDelta
            }

            y += scaleYDelta
        }
        pApplet.updatePixels()

        hue += hueVelocity
        if (hue > maxHue) {
            hue = minHue
        }
    }

    companion object {
        private fun setPixel(pApplet: PApplet, width: Int, x: Int, y: Int, color: Int) {
            pApplet.pixels[x + (y * width)] = color
        }
    }
}