package eu.ezytaget.processing.kotlin_template.realms.cell_automaton_3d

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PGraphics
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class CellAutomaton3D(
        random: Random = Random.Default,
        private val numOfCellsPerSide: Int = 32,
        val sideLength: Float,
        private val nearDeathSurvivalCondition: ((Int) -> Boolean) = { numberOfAliveNeighbors ->
            numberOfAliveNeighbors in 0..6
        },
        private val birthCondition: ((Int) -> Boolean) = { numberOfAliveNeighbors ->
            numberOfAliveNeighbors == 1 || numberOfAliveNeighbors == 3
        },
        numberOfStates: Short = random.nextInt(from = 2, until = 20).toShort(),
        private val neighborCounter: NeighborCounter = MooreNeighborCounter()
): Realm(random) {

    private val cellSize = sideLength / numOfCellsPerSide
    private fun nowMillis() = System.currentTimeMillis()
    private val birthValue = (numberOfStates - 1).toShort()

    var cellDrawProbability: Float = 1f

    private var cells = cells { xIndex, yIndex, zIndex ->
        val centerIndex = numOfCellsPerSide / 2
        val centerCubeSize = random.nextInt(2, 8)
        val centerCubeSizeHalf = centerCubeSize / 2
        val centerCubeStart = centerIndex - centerCubeSizeHalf
        val centerCubeEnd = centerIndex + centerCubeSizeHalf

//        xIndex in centerCubeStart..centerCubeEnd &&
//                yIndex in centerCubeStart..centerCubeEnd &&
//                zIndex in centerCubeStart..centerCubeEnd

//        ((xIndex == centerCubeStart || xIndex == centerCubeEnd) and (
//                (yIndex in centerCubeStart..centerCubeEnd) and
//                        (zIndex in centerCubeStart..centerCubeEnd)
//                )) or
//                ((xIndex in centerCubeStart..centerCubeEnd) and (
//                        (yIndex == centerCubeStart || yIndex == centerCubeEnd) and
//                                (zIndex in centerCubeStart..centerCubeEnd)
//                        )) or
//                        ((xIndex in centerCubeStart..centerCubeEnd) and
//                                (yIndex in centerCubeStart..centerCubeEnd) and
//                                (zIndex == centerCubeStart || zIndex == centerCubeEnd))

        (random.nextFloat() < 0.0001f) or
                ((xIndex == centerIndex) and
                        (yIndex == centerIndex) and
                        (zIndex == centerIndex))


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

    override fun update(pApplet: processing.core.PApplet) {
        super.update(pApplet)
        
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

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)

        pGraphics.beginDraw()
        pGraphics.push()

        pGraphics.lights()
        pGraphics.translate(pGraphics.width / 2f, pGraphics.height / 2f)

        val benchmarkStartMillis = nowMillis()

        pGraphics.push()
        pGraphics.noStroke()
//        pGraphics.stroke(1f, 1f, 1f, 1f)

        val distanceToCenter = (cells.size / 2f) * cellSize
        pGraphics.translate(
                -distanceToCenter,
                -distanceToCenter,
                -distanceToCenter
        )

        if (DRAW_BOUNDING_BOX) {
            drawBoundingBox(pGraphics)
        }

        forEachCell { cellValue, xIndex, yIndex, zIndex ->
            if (cellDrawProbability == 1f || random.nextFloat() < cellDrawProbability) {
                drawCell(pGraphics, cellValue, xIndex, yIndex, zIndex)
            }
        }

        pGraphics.pop()

        if (BENCHMARK) {
            val duration = nowMillis() - benchmarkStartMillis
            println("Benchmark: draw(): $duration ms")
        }

        pGraphics.pop()
        pGraphics.endDraw()
    }

    private fun drawCell(
            pGraphics: PGraphics,
            cellValue: Short,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int
    ) {
        if (cellValue == DEAD_CELL_VALUE && !DRAW_GRID) {
            return
        }

        pGraphics.push()
        pGraphics.translate(
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

        if (DRAW_GRID) {
            pGraphics.stroke(1f)
            pGraphics.noFill()
            pGraphics.box(cellSize)
            pGraphics.noStroke()
        }

        pGraphics.fill(
                distanceToCenter / centerIndex,
                1f,
                1f,
                cellValue.toFloat() / (birthValue * 2f)
        )
        pGraphics.box(cellSize)
        pGraphics.pop()
    }

    private fun drawBoundingBox(pGraphics: processing.core.PGraphics) {
        pGraphics.pushStyle()
        pGraphics.stroke(1f)
        pGraphics.noFill()
        pGraphics.box(sideLength)
        pGraphics.popStyle()
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
        private const val DRAW_GRID = false
        private const val DRAW_BOUNDING_BOX = false
    }

}
