package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

abstract class NeighborCounter {

    var deadCellValue = 0

    abstract fun count(cells: Array<Array<ShortArray>>, xIndex: Int, yIndex: Int, zIndex: Int, maxCellIndex: Int): Int

    internal fun isAlive(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int
    ) = if (xIndex in 0 until maxCellIndex &&
            yIndex in 0 until maxCellIndex &&
            zIndex in 0 until maxCellIndex
    ) {
        cells[xIndex][yIndex][zIndex] > deadCellValue
    } else {
        false
    }

    internal fun oneIfAlive(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int
    ) = if (isAlive(cells, xIndex, yIndex, zIndex, maxCellIndex)) {
        1
    } else {
        0
    }

}
