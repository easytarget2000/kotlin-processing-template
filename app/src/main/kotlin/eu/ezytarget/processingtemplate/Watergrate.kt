package eu.ezytarget.processingtemplate

import processing.core.PApplet
import processing.core.PApplet.map
import processing.core.PConstants.CENTER
import processing.core.PGraphics

internal class Watergrate {
    private lateinit var targetGraphics: PGraphics
    var numberOfRows = 16u

    fun setup(pApplet: PApplet){
        this.targetGraphics = pApplet.createGraphics(
            pApplet.width, pApplet.height,
            PApplet.P2D
        )
        this.targetGraphics.beginDraw()
        this.targetGraphics.colorMode(PApplet.RGB, 255f)
        this.targetGraphics.endDraw()
    }

    fun apply(sourceGraphics: PGraphics): PGraphics {
        if (numberOfRows < 1u) {
            return this.targetGraphics
        }

        this.targetGraphics.beginDraw()
        this.targetGraphics.push()
        this.targetGraphics.background(0f)

        this.targetGraphics.ellipseMode(CENTER)
        this.targetGraphics.noFill()

        sourceGraphics.loadPixels()

        val elementSize = this.targetGraphics.height / this.numberOfRows.toInt()

        ((elementSize / 2) until targetGraphics.width
                step elementSize).forEach { elementX ->
            ((elementSize / 2) until targetGraphics.height
                    step elementSize).forEach { elementY ->
                drawElement(
                    color = colorAroundPixel(
                        sourceGraphics.pixels,
                        x = elementX,
                        y = elementY,
                        sourceWidth = sourceGraphics.width,
                    ),
                    centerX = elementX,
                    centerY = elementY,
                    size = elementSize.toFloat()
                )
            }
        }

        this.targetGraphics.pop()
        this.targetGraphics.endDraw()

        return this.targetGraphics
    }

    private fun drawElement(
        color: Int,
        centerX: Int,
        centerY: Int,
        size: Float,
    ) {
        val centerXFloat = centerX.toFloat()
        val centerYFloat = centerY.toFloat()

        for (i in size.toInt() downTo 0 step 1) {
            val ringRadius = i.toFloat()
            val inter = map(ringRadius, 0f, size, 0f, 1f)
            val ringColor =
                targetGraphics.lerpColor(color, 0x0, inter)
            targetGraphics.stroke(ringColor)
            targetGraphics.ellipse(
                centerXFloat,
                centerYFloat,
                ringRadius,
                ringRadius,
            )
        }
    }

    companion object {


        private fun colorAroundPixel(
            pixels: IntArray,
            x: Int,
            y: Int,
            sourceWidth: Int
        ) = pixels[x + (y * sourceWidth)]

    }
}