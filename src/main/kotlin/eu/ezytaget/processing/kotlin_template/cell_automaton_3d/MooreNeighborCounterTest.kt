package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

import org.junit.jupiter.api.Test

internal class MooreNeighborCounterTest {

    private val target = MooreNeighborCounter()

    private val numOfCellsPerSideFor3x3 = 5

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
        val deadCellValue: Short = 0
        val aliveCellValue: Short = 1
        val numOfCellsPerSide = 5
        val maxCellIndex = numOfCellsPerSide - 1

        val cornerResult1 = target.count(
                cells3x3,
                yIndex = 1,
                xIndex = 1,
                zIndex = 1,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = aliveCellValue
        )
        assert(cornerResult1 == 7)

        val cornerResult2 = target.count(
                cells3x3,
                yIndex = 3,
                xIndex = 3,
                zIndex = 3,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = aliveCellValue
        )
        assert(cornerResult1 == cornerResult2)

    }

    @Test
    fun count_given3x3CellValues_returnsExpectedSideCenterValues() {
        val deadCellValue: Short = 0
        val aliveCellValue: Short = 1
        val numOfCellsPerSide = 5
        val maxCellIndex = numOfCellsPerSide - 1

        val sideCenterResult1 = target.count(
                cells3x3,
                xIndex = 1,
                yIndex = 2,
                zIndex = 2,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = aliveCellValue
        )
        val sideCenterResult2 = target.count(
                cells3x3,
                xIndex = 3,
                yIndex = 2,
                zIndex = 2,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = aliveCellValue
        )

        assert(sideCenterResult1 == sideCenterResult2)
        assert(sideCenterResult1 == 17)
    }

}
