package eu.ezytaget.processing.kotlin_template

import kotlin.math.min
import kotlin.random.Random

class CellAutomaton3D(numOfCellsPerSide: Int = 4, random: Random = Random.Default) {

    var cellSizeRatio = 0.02f

    private val cells = Array(numOfCellsPerSide) { xIndex ->
        Array(numOfCellsPerSide) { yIndex ->
            ShortArray(numOfCellsPerSide) { zIndex ->
                ACTIVE_VALUE
//                if (random.nextBoolean()) {
//                    ACTIVE_VALUE
//                } else {
//                    INACTIVE_VALUE
//                }
            }
        }
    }

    fun update() {

    }

    fun draw(pApplet: PApplet) {
        val cellSize = min(pApplet.width, pApplet.height) * cellSizeRatio

        pApplet.noFill()
        pApplet.stroke(1f, 1f, 1f, 1f)

        pApplet.pushMatrix()
        val distanceToCenter = (cells.size / 2f) * cellSize
        pApplet.translate(
                (pApplet.width / 2f) - distanceToCenter,
                (pApplet.height / 2f) - distanceToCenter
        )

        cells.indices.forEach { xIndex ->
            val xStrip = cells[xIndex]
            xStrip.indices.forEach { yIndex ->
                val yStrip = xStrip[yIndex]
                yStrip.indices.forEach { zIndex ->
                    val cell = yStrip[zIndex]
                    if (cell == ACTIVE_VALUE) {
                        pApplet.pushMatrix()
                        pApplet.translate(
                                xIndex * cellSize,
                                yIndex * cellSize,
                                zIndex * cellSize
                        )
                        pApplet.box(cellSize)
                        pApplet.popMatrix()
                    }
                }
            }
        }

        pApplet.popMatrix()
    }

    companion object {
        private const val ACTIVE_VALUE: Short = 1
        private const val INACTIVE_VALUE: Short = 0
    }
}
