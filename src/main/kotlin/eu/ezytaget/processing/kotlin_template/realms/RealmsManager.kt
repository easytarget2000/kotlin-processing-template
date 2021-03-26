package eu.ezytaget.processing.kotlin_template.realms

import eu.ezytaget.processing.kotlin_template.maybe
import eu.ezytaget.processing.kotlin_template.realms.cell_automaton_3d.CellAutomaton3D
import eu.ezytaget.processing.kotlin_template.realms.cell_automaton_3d.MooreNeighborCounter
import eu.ezytaget.processing.kotlin_template.realms.cell_automaton_3d.VonNeumannNeighborCounter
import eu.ezytaget.processing.kotlin_template.realms.jellyfish.JellyFish
import eu.ezytaget.processing.kotlin_template.realms.julia_set.JuliaSetRealm
import eu.ezytaget.processing.kotlin_template.realms.scan_stripes.ScanStripesRealm
import eu.ezytaget.processing.kotlin_template.realms.scanner.ScannerRealm
import eu.ezytaget.processing.kotlin_template.realms.tesseract.TesseractRealm
import eu.ezytaget.processing.kotlin_template.realms.test_image.TestImageRealm
import eu.ezytaget.processing.kotlin_template.realms.tree_realms.TreeRingsRealm
import processing.core.PApplet
import processing.core.PGraphics
import kotlin.math.min
import kotlin.random.Random

class RealmsManager {

    val tesseractRealm: TesseractRealm?
        get() = realms.firstOrNull { it is TesseractRealm } as? TesseractRealm

    val cellAutomaton3D: CellAutomaton3D?
        get() = realms.firstOrNull { it is CellAutomaton3D } as? CellAutomaton3D

    val scanStripesRealm: ScanStripesRealm?
        get() = realms.firstOrNull { it is ScanStripesRealm } as? ScanStripesRealm

    private val realms = mutableListOf<Realm>()

    fun initRandomRealms(
        pApplet: PApplet,
        pGraphics: PGraphics,
        random: Random = Random.Default
    ) {
        realms.clear()

        random.maybe {
            val juliaSetRealm = JuliaSetRealm()
            juliaSetRealm.setup(pApplet, pGraphics)
            juliaSetRealm.brightness = 1f
            juliaSetRealm.alpha = 1f
            realms.add(juliaSetRealm)
        }

        random.maybe {
            val tesseractRealm = TesseractRealm()
            realms.add(tesseractRealm)
        }

        random.maybe {
            val testImageRealm = TestImageRealm()
            realms.add(testImageRealm)
        }

        random.maybe {
            val automatonSize = min(pGraphics.width.toFloat(), pGraphics.height.toFloat()) * 0.9f

            val neighborCounter = if (random.nextDouble(1.0) > 0.5) {
                VonNeumannNeighborCounter()
            } else {
                MooreNeighborCounter()
            }

            val cellAutomaton = CellAutomaton3D(
                numOfCellsPerSide = random.nextDouble(24.0, 48.0).toInt(),
                sideLength = automatonSize,
                neighborCounter = neighborCounter
            )
            realms.add(cellAutomaton)
        }

        random.maybe {
            val scanStripesRealm = ScanStripesRealm()
            realms.add(scanStripesRealm)
        }

        random.maybe {
            val treeRingsRealm = TreeRingsRealm()
            realms.add(treeRingsRealm)
        }

        random.maybe {
            val scannerRealm = ScannerRealm()
            realms.add(scannerRealm)
        }

        random.maybe {
            val jellyfish = JellyFish()
            realms.add(jellyfish)
        }

        realms.forEach { it.setup(pApplet) }
    }

    fun handleMouseClick(button: Int, mouseX: Int, mouseY: Int, pApplet: PApplet) {
        realms.forEach {
            it.handleMouseClick(button, mouseX, mouseY, pApplet)
        }
    }

    fun update(pApplet: PApplet) {
        realms.forEach {
            it.update(pApplet)
        }
    }

    fun bounce(pApplet: PApplet) {
        realms.forEach { it.bounce(pApplet) }
    }

    fun drawIn(pGraphics: PGraphics) {
        realms.forEachIndexed { index, realm ->
//            if (index == frameCount % realms.size) {
            realm.drawIn(pGraphics)
//            }
        }
    }

    fun setRandomStyle() {
        realms.forEach { it.setRandomStyle() }
    }
}