package eu.ezytaget.processing.kotlin_template

import kotlin.random.Random

class CellAutomaton3D(
        private val numOfCellsPerSide: Int = 128,
        val sideLength: Float,
        private val nearDeathCondition: ((Int) -> Boolean) = { numberOfAliveNeighbors ->
            numberOfAliveNeighbors in 0 .. 6
        },
        private val birthCondition: ((Int) -> Boolean) = { numberOfAliveNeighbors ->
            numberOfAliveNeighbors == 1 || numberOfAliveNeighbors == 3
        },
        private val numberOfStates: Int = 2,
        private val countingAlgorithm: NeighborCountingAlgorithm = NeighborCountingAlgorithm.VonNeumann,
        random: Random = Random.Default
) {

    enum class NeighborCountingAlgorithm {
        VonNeumann, Moore
    }

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

        val maxCellIndex = numOfCellsPerSide - 1

        forEachCell { cellValue, xIndex, yIndex, zIndex ->
            val numberOfAliveNeighbors = when (countingAlgorithm) {
                NeighborCountingAlgorithm.Moore ->
                    numberOfMooreNeighbors(xIndex, maxCellIndex, yIndex, zIndex)
                NeighborCountingAlgorithm.VonNeumann ->
                    numberOfVonNeumannNeighbors(xIndex, maxCellIndex, yIndex, zIndex)
            }
            if (cellValue == NEAR_DEATH_CELL_VALUE) {
                if (nearDeathCondition(numberOfAliveNeighbors)) {
                    newCells[xIndex][yIndex][zIndex] = NEAR_DEATH_CELL_VALUE
                }
            } else if (cellValue == DEAD_CELL_VALUE) {
                if (birthCondition(numberOfAliveNeighbors)) {
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

    private fun numberOfVonNeumannNeighbors(xIndex: Int, maxCellIndex: Int, yIndex: Int, zIndex: Int): Int {
        var aliveNeighborCounter = 0

        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex, zIndex, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex, yIndex - 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex, yIndex + 1, zIndex, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex, yIndex, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex, yIndex, zIndex + 1, maxCellIndex)

        return aliveNeighborCounter
    }

    private fun numberOfMooreNeighbors(xIndex: Int, maxCellIndex: Int, yIndex: Int, zIndex: Int): Int {
        var aliveNeighborCounter = 0

        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex - 1, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex - 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex - 1, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex + 1, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex + 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex - 1, yIndex + 1, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex, yIndex - 1, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex, yIndex - 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex, yIndex - 1, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex, yIndex, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex, yIndex, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex, yIndex + 1, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex, yIndex + 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex, yIndex + 1, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex - 1, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex - 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex - 1, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex + 1, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex + 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(xIndex + 1, yIndex + 1, zIndex + 1, maxCellIndex)

        return aliveNeighborCounter
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

    private fun isAlive(xIndex: Int, yIndex: Int, zIndex: Int, maxCellIndex: Int) =
            if (xIndex in 0 until maxCellIndex &&
                    yIndex in 0 until maxCellIndex &&
                    zIndex in 0 until maxCellIndex
            ) {
                cells[xIndex][yIndex][zIndex] > DEAD_CELL_VALUE
            } else {
                false
            }

    private fun oneIfAlive(xIndex: Int, yIndex: Int, zIndex: Int, maxCellIndex: Int) =
            if (isAlive(xIndex, yIndex, zIndex, maxCellIndex)) {
                1
            } else {
                0
            }

    companion object {
        private const val BENCHMARK = false
        private const val NEAR_DEATH_CELL_VALUE: Short = 1
        private const val DEAD_CELL_VALUE: Short = 0

        private fun isAlive(cellValue: Short) = cellValue > DEAD_CELL_VALUE
    }
}
