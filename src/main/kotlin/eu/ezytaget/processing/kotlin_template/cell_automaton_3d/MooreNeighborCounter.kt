package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

class MooreNeighborCounter : NeighborCounter() {

    override fun count(cells: Array<Array<ShortArray>>, xIndex: Int, yIndex: Int, zIndex: Int, maxCellIndex: Int): Int {
        var aliveNeighborCounter = 0

        aliveNeighborCounter += mooreNeighborsInXPlane(cells, xIndex - 1, yIndex, zIndex, maxCellIndex)
        aliveNeighborCounter += mooreNeighborsInXPlane(cells, xIndex, yIndex, zIndex, maxCellIndex, ignoreCenter = true)
        aliveNeighborCounter += mooreNeighborsInXPlane(cells, xIndex + 1, yIndex, zIndex, maxCellIndex)

        return aliveNeighborCounter
    }

    private fun mooreNeighborsInXPlane(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int,
            ignoreCenter: Boolean = false
    ): Int {
        var aliveNeighborCounter = 0
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex - 1, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex - 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex - 1, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex, zIndex - 1, maxCellIndex)
        if (!ignoreCenter) {
            aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex, zIndex, maxCellIndex)
        }
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex, zIndex + 1, maxCellIndex)

        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex + 1, zIndex - 1, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex + 1, zIndex, maxCellIndex)
        aliveNeighborCounter += oneIfAlive(cells, xIndex, yIndex + 1, zIndex + 1, maxCellIndex)

        return aliveNeighborCounter
    }

}