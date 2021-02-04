package eu.ezytaget.processing.kotlin_template

import processing.core.PApplet.map
import processing.core.PConstants.*

import processing.core.PImage

import processing.core.PFont


class Raster() {

    enum class Style {
        pixelField, ascii
    }

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
    private val individualMaskedLayers = mutableListOf<PImage>()

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

    var minGlitchThreshold = 1f

    var maxGlitchTreshold = 0.3f

    var noiseXOffset = 0f

    var noiseYOffset = 0f

    var noiseScale = 1e-2f

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

        var rasterString = ""

        (0 until numberOfRows).forEach { rowIndex ->
            val floatRowIndex = rowIndex.toFloat()
            val glitchThreshold = map(
                floatRowIndex,
                0f,
                floatNumberOfRows,
                1f,
                0.6f
            )
            (0 until numberOfColumns).forEach { columnIndex ->
                val inputPixelX = map(columnIndex.toFloat(), 0f, floatNumberOfColumns, 0f, floatWidth).toInt()
                val inputPixelY = map(floatRowIndex, 0f, floatNumberOfRows, 0f, floatHeight).toInt()

                when (style) {
                    Style.pixelField -> {
                        val continuousPixelIndex = ((inputPixelY * width) + inputPixelX)
                        pApplet.pixels[continuousPixelIndex] = pImage.pixels[continuousPixelIndex]
                    }
                    Style.ascii -> {
                        val brightness = accumulatedBrightness(
                            pApplet,
                            pImage.pixels,
                            pImage.width,
                            inputPixelX,
                            inputPixelY,
                            glitchThreshold
                        )
                        val shade = shades.firstOrNull { it.second > brightness } ?: shades.last()
                        rasterString += shade.first
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
//                pApplet.textSize(textSize)
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
        val noise = pApplet.random(1f)
//        val noise = pApplet.noise(
//            (x.toFloat() + noiseXOffset) * noiseScale,
//            (y.toFloat() + noiseYOffset) * noiseScale
//        )
        if (noise > glitchThreshold) {
            return pApplet.random(1f) //map(noise, glitchThreshold, 1f, 0f, 1f)
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