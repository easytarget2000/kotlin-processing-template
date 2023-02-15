package eu.ezytarget.processingtemplate.layers.grainygrid

import eu.ezytarget.processingtemplate.Color
import eu.ezytarget.processingtemplate.layers.Layer
import processing.core.PGraphics
import processing.core.PVector

internal class GrainyGridLayer(
    var rotationRadian: Float = 0f,
    var lineWidth: Float = 4f,
    var gridSize: Int = 90,
    var initialColor: Color = Color(1f, 0.2f, 0.5f, 1f),
) : Layer {
    override var intensity: Layer.Intensity = Layer.Intensity.MEDIUM
        set(value) {
            field = value
            color = initialColor
            colorVelocity = colorVelocity(field)
            offsetVelocity = offsetVelocity(field)
            paddingVelocity = paddingVelocity(field)
            offset = PVector(0f, 0f, 0f)
        }

    private var offset = PVector(0f, 0f, 0f)
    private var offsetVelocity: PVector = offsetVelocity(intensity)
    private var colorMax = 1f
    private var color = initialColor
    private var colorVelocity = colorVelocity(intensity)
    private var padding = 1
    private var paddingVelocity = paddingVelocity(intensity)

    init {
        println(
            "GrainyGridLayer: init: rotationRadian = $rotationRadian, lineWidth = $lineWidth, distance = $gridSize," +
                    " initialColor = $initialColor"
        )
    }

    override fun setColorMax(colorMax: Float) {
        this.colorMax = colorMax
    }

    override fun update(deltaTime: Long) {
        offset = PVector.add(offset, offsetVelocity)
        color = color.wrapAroundAdd(colorVelocity, colorMax)
        padding += paddingVelocity
        padding %= gridSize
    }

    override fun draw(pGraphics: PGraphics) {
        pGraphics.strokeWeight(lineWidth)
        pGraphics.stroke(color.value1, color.value2, color.value3, color.alpha)
//        pGraphics.stroke(1f, 0f, 1f, 0.3f)

        pGraphics.point(pGraphics.width / 2f, pGraphics.height / 2f)

        val canvasSize = pGraphics.width.coerceAtLeast(pGraphics.height)
        val canvasWidth = canvasSize
        val canvasHeight = canvasSize

        val gridSizeF = gridSize.toFloat()

        pGraphics.translate(offset.x % gridSizeF, offset.y % gridSizeF)
        drawHorizontalLines(pGraphics, canvasWidth, canvasHeight, gridSizeF)
        drawVerticalLines(pGraphics, canvasWidth, canvasHeight, gridSizeF)
    }

    private fun drawHorizontalLines(
        pGraphics: PGraphics,
        canvasWidth: Int,
        canvasHeight: Int,
        gridSize: Float
    ) {
        pGraphics.push()
        for (y in 0..canvasHeight step this.gridSize) {
            pGraphics.push()
            for (x in 0..canvasWidth step this.gridSize) {
                pGraphics.line(
                    padding / 2f,
                    0f,
                    gridSize - padding,
                    0f,
                )
                pGraphics.translate(gridSize, 0f)
            }
            pGraphics.pop()
            pGraphics.translate(0f, gridSize)
        }
        pGraphics.pop()
    }

    private fun drawVerticalLines(
        pGraphics: PGraphics,
        canvasWidth: Int,
        canvasHeight: Int,
        gridSize: Float
    ) {
        pGraphics.push()
        for (x in 0..canvasWidth step this.gridSize) {
            pGraphics.push()
            for (y in 0..canvasHeight step this.gridSize) {
                pGraphics.line(
                    0f,
                    padding / 2f,
                    0f,
                    gridSize - padding,
                )
                pGraphics.translate(0f, gridSize)
            }
            pGraphics.pop()
            pGraphics.translate(gridSize, 0f)
        }
        pGraphics.pop()
    }

//    override fun draw(pGraphics: PGraphics) {
//        pGraphics.push()
//
//        pGraphics.strokeWeight(lineWidth)
//        pGraphics.stroke(color.value1, color.value2, color.value3, color.alpha)
//
//        val canvasSize = pGraphics.width.coerceAtLeast(pGraphics.height)
//
//        pGraphics.translate(pGraphics.width / 2f, pGraphics.height / 2f)
//        pGraphics.rotate(rotationRadian)
////        pGraphics.translate(-pGraphics.width / 2f, -pGraphics.height / 2f)
//
//        pGraphics.point(pGraphics.width / 2f, pGraphics.height / 2f)
//
//        val startX = (-canvasSize / 2) - distance
//        val endX = (canvasSize / 2) + distance
//        val startY = (-canvasSize / 2) - distance
//        val endY = (canvasSize / 2) + distance
//
//        val gridOffsetX = offset.x % distance.toFloat()
//        val gridOffsetY = offset.y % distance.toFloat()
//
//        for (x in startX..endX step distance) {
//            pGraphics.line(
//                x.toFloat() + gridOffsetX,
//                startY.toFloat() + gridOffsetY,
//                x.toFloat() + gridOffsetX,
//                endY.toFloat() + gridOffsetY,
//            )
//            for (y in startY..endY step distance) {
//                pGraphics.line(
//                    startX.toFloat() + gridOffsetX,
//                    y.toFloat() + gridOffsetY,
//                    endX.toFloat() + gridOffsetX,
//                    y.toFloat() + gridOffsetY,
//                )
//            }
//        }
//
//        pGraphics.pop()
//    }

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

        private fun paddingVelocity(intensity: Layer.Intensity) = when (intensity) {
            Layer.Intensity.LOW -> 0
            Layer.Intensity.MEDIUM -> 1
            Layer.Intensity.HIGH -> 2
        }
    }
}
