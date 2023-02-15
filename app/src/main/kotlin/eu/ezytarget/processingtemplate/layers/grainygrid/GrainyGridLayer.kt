package eu.ezytarget.processingtemplate.layers.grainygrid

import eu.ezytarget.processingtemplate.Color
import eu.ezytarget.processingtemplate.layers.Layer
import processing.core.PGraphics
import processing.core.PVector

internal class GrainyGridLayer(
    var rotationRadian: Float = 0f,
    var lineWidth: Float = 4f,
    var distance: Int = 90,
    var initialColor: Color = Color(1f, 0.2f, 0.5f, 1f),
) : Layer {
    override var intensity: Layer.Intensity = Layer.Intensity.MEDIUM
        set(value) {
            field = value
            color = initialColor
            colorVelocity = colorVelocity(field)
            offsetVelocity = offsetVelocity(field)
        }

    private var offset = PVector(0f, 0f, 0f)
    private var offsetVelocity: PVector = offsetVelocity(intensity)
    private var colorMax = 1f
    private var color = initialColor
    private var colorVelocity = colorVelocity(intensity)

    init {
        println(
            "GrainyGridLayer: init: rotationRadian = $rotationRadian, lineWidth = $lineWidth, distance = $distance," +
                    " initialColor = $initialColor"
        )
    }

    override fun setColorMax(colorMax: Float) {
        this.colorMax = colorMax
    }

    override fun update(deltaTime: Long) {
        offset = PVector.add(offset, offsetVelocity)
        color = color.wrapAroundAdd(colorVelocity, colorMax)
    }

    override fun draw(pGraphics: PGraphics) {
        pGraphics.push()


        pGraphics.strokeWeight(lineWidth)
        pGraphics.stroke(color.value1, color.value2, color.value3, color.alpha)

        val canvasSize = pGraphics.width.coerceAtLeast(pGraphics.height)

        pGraphics.translate(pGraphics.width / 2f, pGraphics.height / 2f)
        pGraphics.rotate(rotationRadian)
//        pGraphics.translate(-pGraphics.width / 2f, -pGraphics.height / 2f)

        pGraphics.point(pGraphics.width / 2f, pGraphics.height / 2f)

        val startX = (-canvasSize / 2) - distance
        val endX = (canvasSize / 2) + distance
        val startY = (-canvasSize / 2) - distance
        val endY = (canvasSize / 2) + distance

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

        pGraphics.pop()
    }

    companion object {
        private fun colorVelocity(intensity: Layer.Intensity) = when (intensity) {
            Layer.Intensity.LOW -> Color(0.005f, 0f, 0f, 0f)
            Layer.Intensity.MEDIUM -> Color(0.01f, 0f, 0f, 0f)
            Layer.Intensity.HIGH -> Color(0.05f, 0.05f, 0f, 0f)
        }

        private fun offsetVelocity(intensity: Layer.Intensity) = when (intensity) {
            Layer.Intensity.LOW -> PVector(-0.2f, -0.1f, 0f)
            Layer.Intensity.MEDIUM -> PVector(0.5f, 0.1f, 0f)
            Layer.Intensity.HIGH -> PVector(0.9f, 0.3f, 0f)
        }
    }
}
