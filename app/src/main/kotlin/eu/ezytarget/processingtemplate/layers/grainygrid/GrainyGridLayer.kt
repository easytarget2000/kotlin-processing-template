package eu.ezytarget.processingtemplate.layers.grainygrid

import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.BeatIntervalUpdate
import eu.ezytarget.processingtemplate.HSB1Color
import eu.ezytarget.processingtemplate.layers.Layer
import processing.core.PGraphics
import processing.core.PVector

internal class GrainyGridLayer: Layer {
    private var offset = PVector(0f, 0f, 0f)
    private var offsetVelocity = PVector(0.9f, 0.1f, 0f)
    private var lineWidth = 1f
    private var distance = 3
    private var color = HSB1Color(1f, 0.5f, 0.5f, 1f)

    override var intensity: Layer.Intensity = Layer.Intensity.MEDIUM

    override fun update(deltaTime: Long) {
        offset = PVector.add(offset, offsetVelocity)
    }

    override fun update(beatStatus: Map<BeatInterval, BeatIntervalUpdate>) {}

    override fun draw(pGraphics: PGraphics) {
        pGraphics.strokeWeight(lineWidth)
        pGraphics.stroke(color.hue, color.saturation, color.brightness, color.alpha)

        val startX = 0 - distance
        val endX = pGraphics.width + distance
        val startY = 0 - distance
        val endY = pGraphics.height + distance

        val gridOffsetX = offset.x % distance.toFloat()
        val gridOffsetY = offset.y % distance.toFloat()

        for (x in startX..endX step distance) {
            pGraphics.line(
                x.toFloat() + gridOffsetX,
                startY.toFloat() + gridOffsetY,
                x.toFloat() + gridOffsetX,
                endY.toFloat() + gridOffsetY,
            )
            for (y in startY..endY step distance) {
                pGraphics.line(
                    startX.toFloat() + gridOffsetX,
                    y.toFloat() + gridOffsetY,
                    endX.toFloat() + gridOffsetX,
                    y.toFloat() + gridOffsetY,
                )
            }
        }
    }
}
