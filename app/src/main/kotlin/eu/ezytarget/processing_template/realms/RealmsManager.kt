package eu.ezytarget.processing_template.realms

import eu.ezytarget.processing_template.maybe
import eu.ezytarget.processing_template.realms.cell_automaton_3d.CellAutomaton3D
import eu.ezytarget.processing_template.realms.cell_automaton_3d.MooreNeighborCounter
import eu.ezytarget.processing_template.realms.cell_automaton_3d.VonNeumannNeighborCounter
import eu.ezytarget.processing_template.realms.holodeck.HoloDeckBuilder
import eu.ezytarget.processing_template.realms.holodeck.Holodeck
import eu.ezytarget.processing_template.realms.jellyfish.JellyFish
import eu.ezytarget.processing_template.realms.jellyfish.JellyFishRealmBuilder
import eu.ezytarget.processing_template.realms.julia_set.JuliaSetRealm
import eu.ezytarget.processing_template.realms.julia_set.JuliaSetRealmBuilder
import eu.ezytarget.processing_template.realms.neon_tunnel.NeonTunnel
import eu.ezytarget.processing_template.realms.scan_stripes.ScanStripesRealm
import eu.ezytarget.processing_template.realms.scan_stripes.ScanStripesRealmBuilder
import eu.ezytarget.processing_template.realms.scanner.ScannerRealm
import eu.ezytarget.processing_template.realms.scanner.ScannerRealmBuilder
import eu.ezytarget.processing_template.realms.tesseract.TesseractRealm
import eu.ezytarget.processing_template.realms.tesseract.TesseractRealmBuilder
import eu.ezytarget.processing_template.realms.test_image.TestImageRealm
import eu.ezytarget.processing_template.realms.test_image.TestImageRealmBuilder
import eu.ezytarget.processing_template.realms.tree_realms.TreeRingsRealm
import eu.ezytarget.processing_template.realms.tree_realms.TreeRingsRealmBuilder
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

    var realmBuildersAndProbabilities = mutableMapOf(
        HoloDeckBuilder to 0.1f,
        TesseractRealmBuilder to 0.5f,
        TestImageRealmBuilder to 0f,
        ScanStripesRealmBuilder to 0.2f,
        TreeRingsRealmBuilder to 0f,
        ScannerRealmBuilder to 0.2f,
        JellyFishRealmBuilder to 0f,
        JuliaSetRealmBuilder() to 0.5f
    )

    var drawAllAtOnce = true

    private val realms = mutableListOf<Realm>()

    fun initRandomRealms(
        pApplet: PApplet,
        pGraphics: PGraphics,
        random: Random = Random.Default
    ) {
        realms.clear()

        realmBuildersAndProbabilities.forEach {
            random.maybe(probability = it.value) {
                realms.add(it.key.build())
            }
        }

        random.maybe {
            val automatonSize = min(pGraphics.width.toFloat(), pGraphics.height.toFloat()) * 0.9f

            val neighborCounter = if (random.nextDouble(1.0) > 1) {
                VonNeumannNeighborCounter()
            } else {
                MooreNeighborCounter()
            }

            val cellAutomaton = CellAutomaton3D(
                numOfCellsPerSide = random.nextDouble(32.0, 64.0).toInt(),
                sideLength = automatonSize,
                neighborCounter = neighborCounter
            )
            realms.add(cellAutomaton)
        }

        realms.forEach { it.setup(pApplet, pGraphics) }
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

    fun drawIn(pGraphics: PGraphics, frameCount: Int) {
        realms.forEachIndexed { index, realm ->
            if (drawAllAtOnce || index == frameCount % realms.size) {
                realm.drawIn(pGraphics)
            }
        }
    }

    fun setRandomStyle() {
        realms.forEach { it.setRandomStyle() }
    }
}