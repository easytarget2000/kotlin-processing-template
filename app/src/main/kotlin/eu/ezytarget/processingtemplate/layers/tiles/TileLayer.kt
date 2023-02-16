package eu.ezytarget.processingtemplate.layers.tiles

import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.BeatIntervalUpdate
import eu.ezytarget.processingtemplate.HSB1Color
import eu.ezytarget.processingtemplate.layers.Layer
import eu.ezytarget.processingtemplate.nextFloat
import processing.core.PConstants.CORNER
import processing.core.PGraphics
import processing.core.PVector
import kotlin.random.Random

internal class TileLayer(val random: Random) : Layer {

    private data class Tile(val color: HSB1Color, val row: Int, val column: Int, private var age: Float = 1f) {
        fun age() = this.age

        fun update(deltaTime: Long) {
            age -= AGE_VELOCITY
            age = age.coerceAtLeast(0f)
        }

        companion object {
            private const val AGE_VELOCITY = 0.01f
        }
    }

    override var intensity: Layer.Intensity = Layer.Intensity.MEDIUM
        set(value) {
            field = value
            tiles = initTiles(field)
        }

    private var tiles = initTiles(intensity)

    private val numberOfRows: Int
        get() = when (intensity) {
            Layer.Intensity.LOW -> 2
            Layer.Intensity.MEDIUM -> 3
            Layer.Intensity.HIGH -> 4
        }

    private val numberOfColumns: Int
        get() = when (intensity) {
            Layer.Intensity.LOW -> 4
            Layer.Intensity.MEDIUM -> 5
            Layer.Intensity.HIGH -> 6
        }

    override fun update(deltaTime: Long) {
        tiles.forEach { it.update(deltaTime) }
    }

    override fun update(beatStatus: Map<BeatInterval, BeatIntervalUpdate>) {
        when (intensity) {
            Layer.Intensity.LOW -> {
                if (beatStatus[BeatInterval.Half]?.didChange == true) {
                    replaceOldestTile()
                }
            }

            Layer.Intensity.MEDIUM -> {
                if (beatStatus[BeatInterval.Quarter]?.didChange == true) {
                    replaceOldestTile()
                }
            }

            Layer.Intensity.HIGH -> {
                if (beatStatus[BeatInterval.Eighth]?.didChange == true) {
                    replaceOldestTile()
                }
            }
        }
    }

    override fun draw(pGraphics: PGraphics) {
        val tileSize = PVector(
            pGraphics.width.toFloat() / numberOfColumns,
            pGraphics.height.toFloat() / numberOfRows,
        )

        pGraphics.noStroke()
        pGraphics.rectMode(CORNER)

        tiles.forEach { draw(it, tileSize, pGraphics) }
    }

    private fun draw(tile: Tile, size: PVector, pGraphics: PGraphics) {
        pGraphics.fill(tile.color.hue, tile.color.saturation, tile.age(), tile.color.alpha)
        pGraphics.rect(
            size.x * tile.column,
            size.y * tile.row,
            size.x,
            size.y,
        )
    }

    private fun initTiles(intensity: Layer.Intensity): MutableList<Tile> {
        val color = nextColor()

        return (0 until numberOfRows).map { row ->
            (0 until numberOfColumns).map { column ->
                return@map Tile(color, row, column, age = random.nextFloat(0.5f))
            }
        }.flatten().toMutableList()
    }

    private fun replaceOldestTile() {
        val color = nextColor()

        tiles.sortBy { it.age() }
        val oldestTile = tiles[0]
        tiles.removeAt(0)

        val tile = Tile(color, oldestTile.row, oldestTile.column)
        tiles.add(tile)
    }

    private fun nextColor() = HSB1Color(
        random.nextFloat(),
        1f,
        1f,
        0.1f,
    )

}
