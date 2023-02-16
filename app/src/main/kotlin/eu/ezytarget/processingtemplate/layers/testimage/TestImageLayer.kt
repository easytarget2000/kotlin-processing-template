package eu.ezytarget.processingtemplate.layers.testimage

import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.BeatIntervalUpdate
import eu.ezytarget.processingtemplate.layers.Layer
import processing.core.PConstants.CENTER
import processing.core.PConstants.CORNERS
import processing.core.PGraphics
import processing.core.PVector

internal class TestImageLayer : Layer {
    override var intensity: Layer.Intensity = Layer.Intensity.MEDIUM

    var drawCircle = true
    private var rotationRadian = 0f


    override fun update(deltaTime: Long) {
//        rotationRadian += 0.001f
    }

    override fun update(beatStatus: Map<BeatInterval, BeatIntervalUpdate>) {

    }

    override fun draw(pGraphics: PGraphics) {
        val canvasWidth = pGraphics.width.toFloat()
        val canvasHeight = pGraphics.height.toFloat()
        val canvasCenter = PVector(canvasWidth / 2f, canvasHeight / 2f)

        pGraphics.strokeWeight(5f)
        pGraphics.stroke(1f)
        pGraphics.noFill()
        pGraphics.rectMode(CORNERS)

        pGraphics.translate(canvasCenter.x, canvasCenter.y)
        pGraphics.rotate(rotationRadian)
        pGraphics.translate(-canvasCenter.x, -canvasCenter.y)

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
            pGraphics.ellipse(canvasCenter.x, canvasCenter.y, canvasWidth / 3f, canvasWidth / 3f)
        }

        pGraphics.line(
            canvasCenter.x - CROSSHAIR_RADIUS,
            canvasCenter.y,
            canvasCenter.x + CROSSHAIR_RADIUS,
            canvasCenter.y
        )
        pGraphics.line(
            canvasCenter.x,
            canvasCenter.y - CROSSHAIR_RADIUS,
            canvasCenter.x,
            canvasCenter.y + CROSSHAIR_RADIUS
        )
    }

    companion object {
        const val CROSSHAIR_RADIUS = 40f
    }
}
