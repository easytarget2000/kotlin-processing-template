package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

class VonNeumannNeighborCounter : NeighborCounter() {

    override fun count(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int,
            minAliveCellValue: Short
    ): Int {
        var aliveNeighborCounter = 0

        aliveNeighborCounter += oneIfAlive(cells, xIndex - 1, yIndex, zIndex, maxCellIndex, minAliveCellValue)
        aliveNeighborCounter += oneIfAlive(cells, xIndex + 1, yIndex, zIndex, maxCellIndex, minAliveCellValue)

        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex - 1, zIndex, maxCellIndex, minAliveCellValue)
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex + 1, zIndex, maxCellIndex, minAliveCellValue)

        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex, zIndex - 1, maxCellIndex, minAliveCellValue)
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex, zIndex + 1, maxCellIndex, minAliveCellValue)

        return aliveNeighborCounter
    }

}
