package eu.ezytaget.processing.kotlin_template

import processing.core.PApplet.map
import processing.core.PConstants.*

import java.awt.SystemColor.text

import processing.core.PImage

import processing.core.PFont


class Raster() {

    private val DEBUG_RASTER = false
    private val DEBUG_GLITCH_SETTING = 0
    private val SEED_SALT = 15
    private val FILE_PREFIX = "l15_"
    private val PART_ID = 1
    private val NUM_OF_GLITCH_VARIANTS = 2

    private val TEXT_SIZES = floatArrayOf(7f, 5f, 2f)
    private val NUMBERS_OF_CHAR_COLUMNS = intArrayOf(262, 349, 1045)
    private val NUMBERS_OF_CHAR_ROWS = intArrayOf(129, 180, 450)

    private val TOGGLE_SHOW_GLITCH_PROBABILITY = 0.2f //0.3f;

    private val USE_INDIVIDUAL_LAYERS = true

    private lateinit var monoFont: PFont
    private val individualEffectLayers = mutableListOf<PImage>()
    private val individualMaskedLayers= mutableListOf<PImage>()

    var numberOfColumns = 256
    var numberOfRows = 128

    fun setup(pApplet: PApplet) {
        monoFont = pApplet.createFont("andalemo.ttf", 32f)
    }

    var bypass = false

    fun drawIn(pApplet: PApplet, pImage: PImage) {
        pApplet.push()
        pApplet.textAlign(LEFT, TOP)
        pApplet.textFont(monoFont)

        val width = pApplet.width
        val height = pApplet.height

        val floatWidth = width.toFloat()
        val floatHeight = height.toFloat()

        pImage.resize(width, height)

        if (bypass) {
            pApplet.image(pImage, 0f, 0f)
            return
        }

        pImage.loadPixels()
        pApplet.loadPixels()

        val floatNumberOfColumns = numberOfColumns.toFloat()
        val floatNumberOfRows = numberOfRows.toFloat()

        (0 until numberOfColumns).forEach { columnIndex ->
            val floatColumnIndex = columnIndex.toFloat()
            (0 until numberOfRows).forEach { rowIndex ->

                val inputPixelX = map(floatColumnIndex, 0f, floatNumberOfColumns, 0f, floatWidth).toInt()
                val inputPixelY = map(rowIndex.toFloat(), 0f, floatNumberOfRows, 0f, floatHeight).toInt()

                val continuousPixelIndex = ((inputPixelY * width) + inputPixelX)
                pApplet.pixels[continuousPixelIndex] = pImage.pixels[continuousPixelIndex]
            }
        }

        pApplet.updatePixels()
//
//
//        val numberOfCharRows = NUMBERS_OF_CHAR_ROWS[glitchSetting]
//        for (row in 0 until numberOfCharRows) {
//            for (column in 0 until numberOfCharColumns) {
//                val layer2PixelX = map(column, 0f, numberOfCharColumns, 0f, rasterSource.width - 1f) as Int
//                val layer2PixelY = pApplet.map(row, 0f, numberOfCharRows, 0f, rasterSource.height - 1f) as Int
//                val imagePixelBrightness = accumulatedBrightness(
//                    rasterSource!!.pixels,
//                    rasterSource.width,
//                    layer2PixelX,
//                    layer2PixelY
//                )
//                val rasterChar = when {
//                    DEBUG_RASTER -> {
//                        '▓'
//                    }
//                    imagePixelBrightness > 0.15f -> {
//                        '▓'
//                    }
//                    imagePixelBrightness > 0.1f -> {
//                        '▒'
//                    }
//                    imagePixelBrightness > 0.05f -> {
//                        '░'
//                    }
//                    else -> {
//                        ' '
//                    }
//                }
//                rasterBuffer.append(rasterChar)
//            }
//            rasterBuffer.append('\n')
//        }
//        val rasterChars = rasterBuffer.toString()
//        pApplet.textSize(textSize)
//        pApplet.textLeading(textSize * 1.1f)
//        pApplet.stroke(1f)
//        val widthOverscan: Float = width * 0.05f
//        val widthOverscanHalf = widthOverscan / 2f
//        val verticalUnderscan: Float = height * 0.008f //height * 0.02f;
//        val verticalUnderscanHalf = verticalUnderscan / 2f
//        text(
//            rasterChars,
//            -widthOverscanHalf,
//            +verticalUnderscanHalf,
//            width + widthOverscanHalf,
//            height - verticalUnderscanHalf
//        )

        pApplet.pop()
    }



    val numberOfSamples = 30
    val numberOfSamplesHalf = numberOfSamples / 2

//    private fun accumulatedBrightness(sourcePixels: IntArray, sourceWidth: Int, x: Int, y: Int): Float {
//        var brightnessSum = 0f
//        for (xOffset in -numberOfSamplesHalf until numberOfSamplesHalf) {
//            val offsetX = xOffset + x
//            if (offsetX < 0 || offsetX > sourceWidth) {
//                continue
//            }
//            brightnessSum += brightness(sourcePixels, sourceWidth, offsetX, y)
//        }
//        return brightnessSum / numberOfSamples.toFloat()
//    }
//
//    private fun brightness(sourcePixels: Int, sourceWidth: Int, x: Int, y: Int): Float {
//        val imagePixelIndex = y * sourceWidth + x
//        val imagePixel = sourcePixels[imagePixelIndex]
//        return brightness(imagePixel) / 255f
//    }

}