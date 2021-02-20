package eu.ezytaget.processing.kotlin_template.realms.cell_automaton_3d

import org.junit.jupiter.api.Test

internal class MooreNeighborCounterTest {

    private val target = MooreNeighborCounter()

    private val numOfCellsPerSideFor3x3 = 5
    private val maxCellIndexFor3x3 = numOfCellsPerSideFor3x3 - 1

    private val deadCellValue: Short = 0
    private val aliveCellValue: Short = 5

    private val cells3x3 = Array(numOfCellsPerSideFor3x3) { xIndex ->
        Array(numOfCellsPerSideFor3x3) { yIndex ->
            ShortArray(numOfCellsPerSideFor3x3) { zIndex ->
                if (xIndex in 1..3 && yIndex in 1..3 && zIndex in 1..3) {
                    aliveCellValue
                } else {
                    deadCellValue
                }
            }
        }
    }

    @Test
    fun count_given3x3Cube_returnsExpectedCenterValue() {
        val maxCellIndex = numOfCellsPerSideFor3x3 - 1

        val centerResult = target.count(
                cells3x3,
                xIndex = 2,
                yIndex = 2,
                zIndex = 2,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = aliveCellValue
        )
        assert(centerResult == 26)
    }

    @Test
    fun count_given3x3Cube_returnsExpectedCornerValues() {
        val cornerIndices = listOf(1, 3)

        cornerIndices.forEach { xIndex ->
            cornerIndices.forEach {yIndex ->
                cornerIndices.forEach{ zIndex ->
                    val cornerResult = target.count(
                            cells3x3,
                            yIndex = xIndex,
                            xIndex = yIndex,
                            zIndex = zIndex,
                            maxCellIndex = maxCellIndexFor3x3,
                            minAliveCellValue = aliveCellValue
                    )
                    assert(cornerResult == 7)
                }
            }
        }
    }

    @Test
    fun count_given3x3CellValues_returnsExpectedSideCenterValues() {
        val sideIndices = listOf(1, 3)
        val centerIndex = 2
        val expectedResult = 17

        sideIndices.forEach {
            val sideCenterResult = target.count(
                    cells3x3,
                    xIndex = it,
                    yIndex = centerIndex,
                    zIndex = centerIndex,
                    maxCellIndex = maxCellIndexFor3x3,
                    minAliveCellValue = aliveCellValue
            )

            assert(sideCenterResult == expectedResult)
        }

        sideIndices.forEach {
            val sideCenterResult = target.count(
                    cells3x3,
                    xIndex = centerIndex,
                    yIndex = it,
                    zIndex = centerIndex,
                    maxCellIndex = maxCellIndexFor3x3,
                    minAliveCellValue = aliveCellValue
            )

            assert(sideCenterResult == expectedResult)
        }

        sideIndices.forEach {
            val sideCenterResult = target.count(
                    cells3x3,
                    xIndex = centerIndex,
                    yIndex = centerIndex,
                    zIndex = it,
                    maxCellIndex = maxCellIndexFor3x3,
                    minAliveCellValue = aliveCellValue
            )

            assert(sideCenterResult == expectedResult)
        }
    }

}
