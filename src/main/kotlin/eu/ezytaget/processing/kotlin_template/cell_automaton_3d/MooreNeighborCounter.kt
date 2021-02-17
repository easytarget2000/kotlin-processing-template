package eu.ezytaget.processing.kotlin_template.cell_automaton_3d

class MooreNeighborCounter : NeighborCounter() {

    override fun count(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int,
            minAliveCellValue: Short
    ) = ((xIndex - 1)..(xIndex + 1)).sumBy {
        neighborsInXPlane(
                cells,
                xIndex = it,
                yIndex = yIndex,
                zIndex = zIndex,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue,
                ignoreCenter = it == xIndex
        )
    }

    private fun neighborsInXPlane(
            cells: Array<Array<ShortArray>>,
            xIndex: Int,
            yIndex: Int,
            zIndex: Int,
            maxCellIndex: Int,
            minAliveCellValue: Short,
            ignoreCenter: Boolean = false
    ): Int {
        var aliveNeighborCounter = 0
        aliveNeighborCounter += oneIfAlive(
                cells,
                xIndex = xIndex,
                yIndex = yIndex - 1,
                zIndex = zIndex - 1,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue
        )
        aliveNeighborCounter += oneIfAlive(
                cells,
                xIndex = xIndex,
                yIndex = yIndex - 1,
                zIndex = zIndex,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue
        )
        aliveNeighborCounter += oneIfAlive(
                cells,
                xIndex = xIndex,
                yIndex = yIndex - 1,
                zIndex = zIndex + 1,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue
        )

        aliveNeighborCounter += oneIfAlive(
                cells,
                xIndex = xIndex,
                yIndex = yIndex,
                zIndex = zIndex - 1,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue
        )
        if (!ignoreCenter) {
            aliveNeighborCounter += oneIfAlive(
                    cells,
                    xIndex = xIndex,
                    yIndex = yIndex,
                    zIndex = zIndex,
                    maxCellIndex = maxCellIndex,
                    minAliveCellValue = minAliveCellValue
            )
        }
        aliveNeighborCounter += oneIfAlive(
                cells,
                xIndex = xIndex,
                yIndex = yIndex,
                zIndex = zIndex + 1,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue
        )

        aliveNeighborCounter += oneIfAlive(
                cells,
                xIndex = xIndex,
                yIndex = yIndex + 1,
                zIndex = zIndex - 1,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue
        )
        aliveNeighborCounter += oneIfAlive(
                cells,
                xIndex = xIndex,
                yIndex = yIndex + 1,
                zIndex = zIndex,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue
        )
        aliveNeighborCounter += oneIfAlive(
                cells,
                xIndex = xIndex,
                yIndex = yIndex + 1,
                zIndex = zIndex + 1,
                maxCellIndex = maxCellIndex,
                minAliveCellValue = minAliveCellValue
        )

        return aliveNeighborCounter
    }

}
