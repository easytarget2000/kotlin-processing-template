package eu.ezytaget.processing.kotlin_template

import kotlin.math.min
import kotlin.random.Random

class CellAutomaton3D(numOfCellsPerSide: Int = 64, var sideLength: Float, random: Random = Random.Default) {

    private val cellSize
        get() = sideLength / numOfCellsPerSide

    private fun nowMillis() = System.currentTimeMillis()

    private var cells = Array(numOfCellsPerSide) { xIndex ->
        Array(numOfCellsPerSide) { yIndex ->
            BooleanArray(numOfCellsPerSide) { zIndex ->
//                xIndex == (numOfCellsPerSide / 2) && yIndex == (numOfCellsPerSide / 2) && zIndex == (numOfCellsPerSide / 2)
                random.nextBoolean()
            }
        }
    }

    private val numOfCellsPerSide = cells.size

    fun update() {
        val benchmarkStartMillis = nowMillis()

        val newCells = cells.copyOf()

        forEachCell { cellValue, xIndex, yIndex, zIndex ->
            val activeNeighbourCounter = numberOfVonNeumannNeighbours(xIndex, numOfCellsPerSide, yIndex, zIndex)
            if (cellValue) {
                if (activeNeighbourCounter !in 0..6) {
                    newCells[xIndex][yIndex][zIndex] = false
                }
            } else {
                if (activeNeighbourCounter == 1 || activeNeighbourCounter == 3) {
                    newCells[xIndex][yIndex][zIndex] = true
                }
            }
        }

        cells = newCells

        if (BENCHMARK) {
            val duration = nowMillis() - benchmarkStartMillis
            println("Benchmark: update(): $duration ms")
        }
    }

    fun draw(pApplet: PApplet) {
        val benchmarkStartMillis = nowMillis()

        pApplet.push()
        pApplet.noFill()
        pApplet.stroke(1f, 1f, 1f, 1f)

        val distanceToCenter = (cells.size / 2f) * cellSize
        pApplet.translate(
                -distanceToCenter,
                -distanceToCenter,
                -distanceToCenter
        )

        forEachCell { cellValue, xIndex, yIndex, zIndex ->
            drawCell(pApplet, cellValue, xIndex, yIndex, zIndex)
        }

        pApplet.pop()

        drawBoundingBox(pApplet)

        if (BENCHMARK) {
            val duration = nowMillis() - benchmarkStartMillis
            println("Benchmark: draw(): $duration ms")
        }
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
            xIndex: Int,
            yIndex: Int,
            zIndex: Int
    ) {
        if (!cellValue) {
            return
        }

        pApplet.push()
        pApplet.translate(
                xIndex * cellSize,
                yIndex * cellSize,
                zIndex * cellSize
        )
        pApplet.stroke(
                xIndex.toFloat() / numOfCellsPerSide.toFloat(),
                yIndex.toFloat() / numOfCellsPerSide.toFloat(),
                zIndex.toFloat() / numOfCellsPerSide.toFloat(),
                1f
        )
        pApplet.box(cellSize)
        pApplet.pop()
    }

    private fun drawBoundingBox(pApplet: PApplet) {
        pApplet.pushStyle()
        pApplet.stroke(1f)
        pApplet.noFill()
        pApplet.box(sideLength)
        pApplet.popStyle()
    }

    private fun forEachCell(action: (cellValue: Boolean, xIndex: Int, yIndex: Int, zIndex: Int) -> Unit) {
        cells.indices.forEach { xIndex ->
            val xStrip = cells[xIndex]
            xStrip.indices.forEach { yIndex ->
                val yStrip = xStrip[yIndex]
                yStrip.indices.forEach { zIndex ->
                    val cellValue = yStrip[zIndex]
                    action(cellValue, xIndex, yIndex, zIndex)
                }
            }
        }
    }

    companion object {
        private const val BENCHMARK = false
        private const val ACTIVE_VALUE: Short = 1
        private const val INACTIVE_VALUE: Short = 0
    }
}
