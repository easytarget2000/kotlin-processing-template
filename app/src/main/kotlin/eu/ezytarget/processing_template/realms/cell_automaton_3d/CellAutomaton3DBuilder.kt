package eu.ezytarget.processing_template.realms.cell_automaton_3d

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder
import kotlin.math.min
import kotlin.random.Random

class CellAutomaton3DBuilder: RealmBuilder {

    var random = Random.Default

    var sideLength = 200f

    var minNumberOfCellsPerSide = 32

    var maxNumberOfCellsPerSide = 64

    var vonNeumannProbability = 0.1f

    override fun build(): Realm {
        val neighborCounter = if (random.nextFloat() < vonNeumannProbability) {
            VonNeumannNeighborCounter()
        } else {
            MooreNeighborCounter()
        }

        return CellAutomaton3D(
            numOfCellsPerSide = random.nextInt(minNumberOfCellsPerSide, maxNumberOfCellsPerSide),
            sideLength = sideLength,
            neighborCounter = neighborCounter
        )
    }
}