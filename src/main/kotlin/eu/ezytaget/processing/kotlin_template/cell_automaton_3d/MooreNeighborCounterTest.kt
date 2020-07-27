package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

import org.junit.jupiter.api.Test
import kotlin.math.max

internal class MooreNeighborCounterTest {

    private val target = MooreNeighborCounter()

    @Test
    fun count_given3x3Cube_returnsExpectedValues() {
        val deadCellValue: Short = 0
        val aliveCellValue:Short = 1
        val numOfCellsPerSide = 5
        val maxCellIndex = numOfCellsPerSide - 1

        val cells = Array(numOfCellsPerSide) { xIndex ->
            Array(numOfCellsPerSide) { yIndex ->
                ShortArray(numOfCellsPerSide) { zIndex ->
                    if (xIndex in 1..3 && yIndex in 1..3 && zIndex in 1..3) {
                        aliveCellValue
                    } else {
                        deadCellValue
                    }
                }
            }
        }

        val centerResult = target.count(
            cells,
                xIndex = 2,
                yIndex = 2,
                zIndex = 2,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = aliveCellValue
        )
        assert(centerResult == 26)
    }
}