package eu.ezytaget.processing.kotlin_template

import kotlin.random.Random

class CellAutomaton3D(
        private val numOfCellsPerSide: Int = 128,
        val sideLength: Float,
        private val nearDeathSurvivalRange: IntRange = 0..6,
        private val numberOfStates: Int = 2,
        random: Random = Random.Default
) {

    private val cellSize = sideLength / numOfCellsPerSide
    private fun nowMillis() = System.currentTimeMillis()
    private val BIRTH_VALUE = (numberOfStates - 1).toShort()

    private var cells = cells { xIndex, yIndex, zIndex ->
        val centerIndex = numOfCellsPerSide / 2
        xIndex in (centerIndex - 1..centerIndex + 1) &&
                yIndex in (centerIndex - 1..centerIndex + 1) &&
                zIndex in (centerIndex - 1..centerIndex + 1)

//                random.nextBoolean()
//                val lastIndex = numOfCellsPerSide - 1
//                (xIndex == 0) or (xIndex == lastIndex) or
//                        (yIndex == 0) or (yIndex == lastIndex) or
//                        (zIndex == 0) or (zIndex == lastIndex)
    }

    private fun cells(
            initialization: (xIndex: Int, yIndex: Int, zIndex: Int) -> Boolean
    ) = Array(numOfCellsPerSide) { xIndex ->
        Array(numOfCellsPerSide) { yIndex ->
            ShortArray(numOfCellsPerSide) { zIndex ->
                if (initialization(xIndex, yIndex, zIndex)) {
                    BIRTH_VALUE
                } else {
                    DEAD_CELL_VALUE
                }
            }
        }
    }

    fun update() {
        val benchmarkStartMillis = nowMillis()

        val newCells = cells { _, _, _ ->
            false
        }

        forEachCell { cellValue, xIndex, yIndex, zIndex ->
            val activeNeighbourCounter = numberOfVonNeumannNeighbours(xIndex, numOfCellsPerSide, yIndex, zIndex)
            if (cellValue == NEAR_DEATH_CELL_VALUE) {
                if (activeNeighbourCounter in nearDeathSurvivalRange) {
                    newCells[xIndex][yIndex][zIndex] = NEAR_DEATH_CELL_VALUE
                }
            } else if (cellValue == DEAD_CELL_VALUE) {
                if (activeNeighbourCounter == 1 || activeNeighbourCounter == 3) {
                    newCells[xIndex][yIndex][zIndex] = BIRTH_VALUE
                }
            } else {
                newCells[xIndex][yIndex][zIndex] = (cellValue - 1).toShort()
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
            if (previousXStrip[yIndex][zIndex] > DEAD_CELL_VALUE) {
                ++activeNeighbourCounter
            }
            val nextXStrip = cells[xIndex + 1]
            if (nextXStrip[yIndex][zIndex] > DEAD_CELL_VALUE) {
                ++activeNeighbourCounter
            }
        }

        if (yIndex in 1 until numOfCellsPerSide - 1) {
            val previousYStrip = cells[xIndex][yIndex - 1]
            if (previousYStrip[zIndex] > DEAD_CELL_VALUE) {
                ++activeNeighbourCounter
            }
            val nextYStrip = cells[xIndex][yIndex + 1]
            if (nextYStrip[zIndex] > DEAD_CELL_VALUE) {
                ++activeNeighbourCounter
            }
        }

        if (zIndex in 1 until numOfCellsPerSide - 1) {
            val previousZCell = cells[xIndex][yIndex][zIndex - 1]
            if (previousZCell > DEAD_CELL_VALUE) {
                ++activeNeighbourCounter
            }

            val nextZCell = cells[xIndex][yIndex][zIndex + 1]
            if (nextZCell > DEAD_CELL_VALUE) {
                ++activeNeighbourCounter
            }
        }

        return activeNeighbourCounter
    }

    private fun drawCell(
            pApplet: PApplet,
            cellValue: Short,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int
    ) {
        if (cellValue == DEAD_CELL_VALUE) {
            return
        }

        pApplet.push()
        pApplet.translate(
                xIndex * cellSize,
                yIndex * cellSize,
                zIndex * cellSize
        )
        pApplet.fill(
                xIndex.toFloat() / numOfCellsPerSide.toFloat(),
                yIndex.toFloat() / numOfCellsPerSide.toFloat(),
                zIndex.toFloat() / numOfCellsPerSide.toFloat(),
                cellValue.toFloat() / numberOfStates.toFloat()
        )
        pApplet.noStroke()
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

    private fun forEachCell(action: (cellValue: Short, xIndex: Int, yIndex: Int, zIndex: Int) -> Unit) {
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
        private const val NEAR_DEATH_CELL_VALUE: Short = 1
        private const val DEAD_CELL_VALUE: Short = 0
    }
}
