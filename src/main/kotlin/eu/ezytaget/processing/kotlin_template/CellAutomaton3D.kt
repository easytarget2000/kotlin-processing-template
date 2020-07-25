package eu.ezytaget.processing.kotlin_template

import kotlin.math.min
import kotlin.random.Random

class CellAutomaton3D(numOfCellsPerSide: Int = 8, random: Random = Random.Default) {

    var cellSizeRatio = 0.02f

    private val cells = Array(numOfCellsPerSide) { xIndex ->
        Array(numOfCellsPerSide) { yIndex ->
            BooleanArray(numOfCellsPerSide) { zIndex ->
                true
//                if (random.nextBoolean()) {
//                    ACTIVE_VALUE
//                } else {
//                    INACTIVE_VALUE
//                }
            }
        }
    }

    fun updateAndDraw(pApplet: PApplet) {
        val cellSize = min(pApplet.width, pApplet.height) * cellSizeRatio

        pApplet.noFill()
        pApplet.stroke(1f, 1f, 1f, 1f)

        pApplet.pushMatrix()
        val distanceToCenter = (cells.size / 2f) * cellSize
        pApplet.translate(
                -distanceToCenter,
                -distanceToCenter
        )

        val numOfCellsPerSide = cells.size

        cells.indices.forEach { xIndex ->
            val xStrip = cells[xIndex]
            xStrip.indices.forEach { yIndex ->
                val yStrip = xStrip[yIndex]
                yStrip.indices.forEach { zIndex ->
                    val cellValue = yStrip[zIndex]
                    var activeNeighbourCounter = numberOfVonNeumannNeighbours(xIndex, numOfCellsPerSide, yIndex, zIndex)

                    println(activeNeighbourCounter)

                    drawCell(pApplet, cellValue, cellSize, xIndex, yIndex, zIndex, activeNeighbourCounter)
                }
            }
        }

        pApplet.popMatrix()
    }

    private fun numberOfVonNeumannNeighbours(xIndex: Int, numOfCellsPerSide: Int, yIndex: Int, zIndex: Int): Int {
        var activeNeighbourCounter = 0
        if (xIndex in 1 until numOfCellsPerSide - 1) {
            val previousXStrip = cells[xIndex - 1]
            if (previousXStrip[yIndex][zIndex]) {
                ++activeNeighbourCounter
            }
            val nextXStrip = cells[xIndex + 1]
            if (nextXStrip[yIndex][zIndex]) {
                ++activeNeighbourCounter
            }
        }

        if (yIndex in 1 until numOfCellsPerSide - 1) {
            val previousYStrip = cells[xIndex][yIndex - 1]
            if (previousYStrip[zIndex]) {
                ++activeNeighbourCounter
            }
            val nextYStrip = cells[xIndex][yIndex + 1]
            if (nextYStrip[zIndex]) {
                ++activeNeighbourCounter
            }
        }

        if (zIndex in 1 until numOfCellsPerSide - 1) {
            val previousZCell = cells[xIndex][yIndex][zIndex - 1]
            if (previousZCell) {
                ++activeNeighbourCounter
            }

            val nextZCell = cells[xIndex][yIndex][zIndex + 1]
            if (nextZCell) {
                ++activeNeighbourCounter
            }
        }
        return activeNeighbourCounter
    }

    private fun drawCell(
            pApplet: PApplet,
            cellValue: Boolean,
            cellSize: Float,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            numberOfActiveNeighbours: Int
    ) {
        pApplet.pushMatrix()
        pApplet.translate(
                xIndex * cellSize,
                yIndex * cellSize,
                zIndex * cellSize
        )
        pApplet.stroke(
                numberOfActiveNeighbours.toFloat() / 6f,
                1f,
                1f,
                1f
        )
        pApplet.box(cellSize)
        pApplet.popMatrix()
    }

    companion object {
        private const val ACTIVE_VALUE: Short = 1
        private const val INACTIVE_VALUE: Short = 0
    }
}
