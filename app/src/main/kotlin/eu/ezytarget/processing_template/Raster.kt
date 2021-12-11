package eu.ezytarget.processing_template

import processing.core.PApplet.map
import processing.core.PConstants.*

import processing.core.PImage

import processing.core.PFont


class Raster() {

    enum class Style {
        pixelField, ascii
    }

    private lateinit var monoFont: PFont

    var numberOfColumns = 256

    var numberOfRows = 128

    var bypass = false

    var style = Style.ascii

    private var textSize = 32f

    var rasterSizeFinderChar = "▓"

    var textColor = 1f

    var minNumberOfColumns = 16

    var numberOfSamples = 30
        set(value) {
            field = value
            numberOfSamplesHalf = value / 2
        }

    var textLeadingRatio = 1.1f

    var shades = manyShades

    var glitch = false

    var minGlitchThreshold = 1f

    var maxGlitchTreshold = 0.3f

    var noiseXOffset = 0f

    var noiseYOffset = 0f

    var noiseScale = 1e-2f

    var debugRaster = false

    var debugChar = blockShades.last().first

    private var numberOfSamplesHalf = numberOfSamples / 2

    fun setup(pApplet: PApplet, baseFontSize: Float = 24f) {
        setTextSize(pApplet, baseFontSize)
    }

    fun setTextSize(pApplet: PApplet, textSize: Float) {
        pApplet.push()
        monoFont = pApplet.createFont("andalemo.ttf", textSize)
        pApplet.textFont(monoFont)
        pApplet.textLeading(textLeadingRatio)

        findRasterSizeByTextSize(pApplet)

        pApplet.pop()

        this.textSize = textSize
    }

    fun drawIn(pApplet: PApplet, pImage: PImage? = null) {
        pApplet.push()
        pApplet.textAlign(LEFT, TOP)
        pApplet.textFont(monoFont)

        val width = pApplet.width
        val height = pApplet.height

        val floatWidth = width.toFloat()
        val floatHeight = height.toFloat()

        pImage?.resize(width, height)

        if (bypass) {
            pImage?.let { pApplet.image(it, 0f, 0f) }
            return
        }

        pImage?.loadPixels()
        pApplet.loadPixels()

        val floatNumberOfColumns = numberOfColumns.toFloat()
        val floatNumberOfRows = numberOfRows.toFloat()

        val pixels = if (pImage == null) {
            pApplet.pixels
        } else {
            pImage.pixels
        }

        var rasterString = ""

        (0 until numberOfRows).forEach { rowIndex ->
            val floatRowIndex = rowIndex.toFloat()
            val glitchThreshold = map(
                    floatRowIndex,
                    0f,
                    floatNumberOfRows,
                    minGlitchThreshold,
                    maxGlitchTreshold
            )
            (0 until numberOfColumns).forEach { columnIndex ->
                val inputPixelX = map(columnIndex.toFloat(), 0f, floatNumberOfColumns, 0f, floatWidth).toInt()
                val inputPixelY = map(floatRowIndex, 0f, floatNumberOfRows, 0f, floatHeight).toInt()

                when (style) {
                    Style.pixelField -> {
                        val continuousPixelIndex = ((inputPixelY * width) + inputPixelX)
                        pApplet.pixels[continuousPixelIndex] = pixels[continuousPixelIndex]
                    }
                    Style.ascii -> {
                        val brightness = accumulatedBrightness(
                                pApplet,
                                pixels,
                                width,
                                inputPixelX,
                                inputPixelY,
                                glitchThreshold
                        )
                        val shade = shades.firstOrNull { it.second > brightness } ?: shades.last()
                        rasterString += if (debugRaster) {
                            debugChar
                        } else {
                            shade.first
                        }
                    }
                }
            }

            rasterString += "\n"
        }

        when (style) {
            Style.pixelField -> {
                pApplet.updatePixels()
            }
            Style.ascii -> {
                pApplet.background(0)
                pApplet.textLeading(textSize * textLeadingRatio)

                pApplet.fill(textColor)
                pApplet.text(rasterString, 0f, 0f, floatWidth, floatHeight)
            }
        }

        pApplet.pop()
    }

    fun findRasterSizeByTextSize(pApplet: PApplet) {
        val width = pApplet.width
        val maxNumberOfColumns = width / 2
        val floatWidth = width.toFloat()

        numberOfColumns = (minNumberOfColumns..maxNumberOfColumns).firstOrNull {
            val testString = (0..it).joinToString(separator = "") { rasterSizeFinderChar }
            pApplet.textWidth(testString) >= floatWidth
        } ?: maxNumberOfColumns

        val height = pApplet.height
        val rowHeight = pApplet.textAscent() * textLeadingRatio

        numberOfRows = (height.toFloat() / rowHeight).toInt()

//        println("DEBUG: Raster: findRasterSizeByTextSize(): width: $width, numberOfColumns: $numberOfColumns," +
//                "maxNumberOfColumns: $maxNumberOfColumns, numberOfRows: $numberOfRows")
    }

    private fun accumulatedBrightness(
            pApplet: PApplet,
            sourcePixels: IntArray,
            sourceWidth: Int,
            x: Int,
            y: Int,
            glitchThreshold: Float
    ): Float {
        if (glitch) {
            val noise = pApplet.random(1f)
//        val noise = pApplet.noise(
//            (x.toFloat() + noiseXOffset) * noiseScale,
//            (y.toFloat() + noiseYOffset) * noiseScale
//        )
            if (noise > glitchThreshold) {
                return pApplet.random(1f) //map(noise, glitchThreshold, 1f, 0f, 1f)
            }
        }

        var brightnessSum = 0f
        for (xOffset in -numberOfSamplesHalf until numberOfSamplesHalf) {
            val offsetX = xOffset + x
            if (offsetX < 0 || offsetX > sourceWidth) {
                continue
            }
            brightnessSum += brightness(pApplet, sourcePixels, sourceWidth, offsetX, y)
        }
        return brightnessSum / numberOfSamples.toFloat()
    }

    private fun brightness(pApplet: PApplet, sourcePixels: IntArray, sourceWidth: Int, x: Int, y: Int): Float {
        val imagePixelIndex = y * sourceWidth + x
        val imagePixel = sourcePixels[imagePixelIndex]
        return pApplet.brightness(imagePixel) / colorRange
    }

    companion object {

        var colorRange = 1f

        val blockShades = charsToEquallyDistributedShades(' ', '░', '▒', '▓')

        val manyShades = charsToEquallyDistributedShades(
                ' ', '.', ',', ';', '~', '|', '\\', '=', '&', '%', '@'
        )

        private fun charsToEquallyDistributedShades(vararg chars: Char): List<Pair<Char, Double>> {
            val stepSize = colorRange / chars.size
            return chars.mapIndexed { index, char ->
                char to ((index + 1) * stepSize).toDouble()
            }
        }
    }

}