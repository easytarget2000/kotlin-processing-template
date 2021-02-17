package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

abstract class NeighborCounter() {

    abstract fun count(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int,
            minAliveCellValue: Short
    ): Int

    internal fun isAlive(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int,
            minAliveCellValue: Short
    ) = if (xIndex in 0 until maxCellIndex &&
            yIndex in 0 until maxCellIndex &&
            zIndex in 0 until maxCellIndex
    ) {
        cells[xIndex][yIndex][zIndex] >= minAliveCellValue
    } else {
        false
    }

    internal fun oneIfAlive(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int,
            minAliveCellValue: Short
    ) = if (isAlive(cells, xIndex, yIndex, zIndex, maxCellIndex, minAliveCellValue)) {
        1
    } else {
        0
    }

}
