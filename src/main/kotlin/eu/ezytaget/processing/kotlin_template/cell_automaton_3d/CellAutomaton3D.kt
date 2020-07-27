package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

import eu.ezytaget.processing.kotlin_template.PApplet
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class CellAutomaton3D(
        private val numOfCellsPerSide: Int = 64,
        val sideLength: Float,
        private val nearDeathSurvivalCondition: ((Int) -> Boolean) = { numberOfAliveNeighbors ->
            numberOfAliveNeighbors in 4..7
        },
        private val birthCondition: ((Int) -> Boolean) = { numberOfAliveNeighbors ->
            numberOfAliveNeighbors in 6..8
        },
        private val numberOfStates: Short = 20,
        private val neighborCounter: NeighborCounter = MooreNeighborCounter(),
        random: Random = Random.Default
) {

    private val cellSize = sideLength / numOfCellsPerSide
    private fun nowMillis() = System.currentTimeMillis()
    private val birthValue = (numberOfStates - 1).toShort()

    private var cells = cells { xIndex, yIndex, zIndex ->
        val centerIndex = numOfCellsPerSide / 2
        val centerCubeSizeHalf = 2
        val centerCubeStart = centerIndex - centerCubeSizeHalf
        val centerCubeEnd = centerIndex + centerCubeSizeHalf

        xIndex in centerCubeStart..centerCubeEnd &&
                yIndex in centerCubeStart..centerCubeEnd &&
                zIndex in centerCubeStart..centerCubeEnd

//        (xIndex == centerIndex) and
//                (yIndex == centerIndex) and
//                (zIndex == centerIndex)
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
                    maxCellIndex,
                    minAliveCellValue = birthValue
            )

            if (cellValue == NEAR_DEATH_CELL_VALUE && nearDeathSurvivalCondition(numberOfAliveNeighbors)) {
                newCells[xIndex][yIndex][zIndex] = NEAR_DEATH_CELL_VALUE
            } else if (cellValue == DEAD_CELL_VALUE && birthCondition(numberOfAliveNeighbors)) {
                newCells[xIndex][yIndex][zIndex] = birthValue
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
        pApplet.noStroke()
//        pApplet.stroke(1f, 1f, 1f, 1f)

        val distanceToCenter = (cells.size / 2f) * cellSize
        pApplet.translate(
                -distanceToCenter,
                -distanceToCenter,
                -distanceToCenter
        )
//        pApplet.strokeWeight(4f)

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
        val centerIndex = numOfCellsPerSide / 2
        val distanceToCenter = sqrt(
                (xIndex - centerIndex).toFloat().pow(2f) +
                        (yIndex - centerIndex).toFloat().pow(2f) +
                        (zIndex - centerIndex).toFloat().pow(2f)
        )

        pApplet.fill(
                distanceToCenter / numOfCellsPerSide,
                1f,
                1f,
                cellValue.toFloat() / numberOfStates.toFloat()
        )
//        pApplet.point(0f, 0f)
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
