package eu.ezytarget.processingtemplate.layers.testimage

import eu.ezytarget.processingtemplate.layers.Layer
import processing.core.PConstants.CENTER
import processing.core.PConstants.CORNERS
import processing.core.PGraphics
import processing.core.PVector

internal class TestImageLayer : Layer {
    override var intensity: Layer.Intensity = Layer.Intensity.MEDIUM

    var drawCircle = true
    private var colorMax = 1f
    private var rotationRadian = 0f

    override fun setColorMax(colorMax: Float) {
        this.colorMax = colorMax
    }

    override fun update(deltaTime: Long) {
        rotationRadian += 0.001f
    }

    override fun draw(pGraphics: PGraphics) {
        val canvasWidth = pGraphics.width.toFloat()
        val canvasHeight = pGraphics.height.toFloat()

        pGraphics.strokeWeight(5f)
        pGraphics.stroke(colorMax)
        pGraphics.noFill()
        pGraphics.rectMode(CORNERS)

        pGraphics.translate(canvasWidth / 2f, canvasHeight / 2f)
        pGraphics.rotate(rotationRadian)
        pGraphics.translate(-canvasWidth / 2f, -canvasHeight / 2f)

        pGraphics.rect(0f, 0f, canvasWidth, canvasHeight)

        val offset = PVector(pGraphics.width / 3f, pGraphics.height / 3f)
        pGraphics.rect(
            offset.x,
            offset.y,
            canvasWidth - offset.x,
            canvasHeight - offset.y
        )

        if (drawCircle) {
            pGraphics.ellipseMode(CENTER)
            pGraphics.ellipse(canvasWidth / 2f, canvasHeight / 2f, canvasWidth / 3f, canvasWidth / 3f)
        }
    }
}
