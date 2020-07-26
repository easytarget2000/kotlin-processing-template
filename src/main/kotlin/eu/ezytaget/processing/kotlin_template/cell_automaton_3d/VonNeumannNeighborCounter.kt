package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

class VonNeumannNeighborCounter: NeighborCounter() {

    override fun count(cells: Array<Array<ShortArray>>, xIndex: Int, yIndex: Int, zIndex: Int, maxCellIndex: Int): Int {
        var aliveNeighborCounter = 0

        aliveNeighborCounter += oneIfAlive(cells,xIndex - 1, yIndex, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(cells,xIndex + 1, yIndex, zIndex, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex - 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex + 1, zIndex, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex, zIndex + 1, maxCellIndex)

        return aliveNeighborCounter
    }

}
