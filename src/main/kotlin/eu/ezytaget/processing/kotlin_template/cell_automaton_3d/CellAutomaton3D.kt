package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

import eu.ezytaget.processing.kotlin_template.PApplet
import kotlin.random.Random

class CellAutomaton3D(
        private val numOfCellsPerSide: Int = 64,
        val sideLength: Float,
        private val nearDeathSurvivalCondition: ((Int) -> Boolean) = { numberOfAliveNeighbors ->
            numberOfAliveNeighbors == 2
        },
        private val birthCondition: ((Int) -> Boolean) = { numberOfAliveNeighbors ->
            numberOfAliveNeighbors == 2
        },
        private val numberOfStates: Int = 3,
        private val neighborCounter: NeighborCounter = MooreNeighborCounter(),
        random: Random = Random.Default
) {

    private val cellSize = sideLength / numOfCellsPerSide
    private fun nowMillis() = System.currentTimeMillis()
    private val birthValue = (numberOfStates - 1).toShort()

    private var cells = cells { xIndex, yIndex, zIndex ->
        val centerIndex = numOfCellsPerSide / 2
        val centerCubeSizeHalf = numOfCellsPerSide / 16
        val centerCubeStart = centerIndex - centerCubeSizeHalf
        val centerCubeEnd = centerIndex + centerCubeSizeHalf

        xIndex in centerCubeStart..centerCubeEnd &&
                yIndex in centerCubeStart..centerCubeEnd &&
                zIndex in centerCubeStart..centerCubeEnd &&
                random.nextBoolean()

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
                    birthValue
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
            val numberOfAliveNeighbors = neighborCounter.count(
                    cells,
                    xIndex,
                    yIndex,
                    zIndex,
                    maxCellIndex
            )

            if (cellValue == NEAR_DEATH_CELL_VALUE) {
                if (nearDeathSurvivalCondition(numberOfAliveNeighbors)) {
                    newCells[xIndex][yIndex][zIndex] = NEAR_DEATH_CELL_VALUE
                }
            } else if (cellValue == DEAD_CELL_VALUE) {
                if (birthCondition(numberOfAliveNeighbors)) {
                    newCells[xIndex][yIndex][zIndex] = birthValue
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
                cellValue.toFloat() / numberOfStates.toFloat(),
                (yIndex * zIndex).toFloat() / numOfCellsPerSide.toFloat(),
                1f
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
