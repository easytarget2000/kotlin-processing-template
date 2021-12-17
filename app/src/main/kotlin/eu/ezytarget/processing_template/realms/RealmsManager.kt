package eu.ezytarget.processing_template.realms

import eu.ezytarget.processing_template.maybe
import eu.ezytarget.processing_template.realms.cell_automaton_3d.CellAutomaton3D
import eu.ezytarget.processing_template.realms.cell_automaton_3d.CellAutomaton3DBuilder
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
import eu.ezytarget.processing_template.realms.vaypr.VayprRealmBuilder
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
        TestImageRealmBuilder to 0.05f,
        ScanStripesRealmBuilder to 0.2f,
        TreeRingsRealmBuilder to 0f,
        ScannerRealmBuilder to 0.2f,
        JellyFishRealmBuilder to 0f,
        JuliaSetRealmBuilder() to 0.5f,
    )

    var drawAllAtOnce = true

    private val realms = mutableListOf<Realm>()

    fun initRandomRealms(
        pApplet: PApplet,
        pGraphics: PGraphics,
        random: Random = Random.Default
    ) {
        realms.clear()

        val width = pGraphics.width.toFloat()
        val height = pGraphics.height.toFloat()

        val cellAutomaton3DBuilder = CellAutomaton3DBuilder()
        cellAutomaton3DBuilder.sideLength = min(width, height) * 0.9f
        cellAutomaton3DBuilder.vonNeumannProbability = 0.5f
        realmBuildersAndProbabilities[cellAutomaton3DBuilder] = 0.3f

        realms.clear()

        realmBuildersAndProbabilities = mutableMapOf(
            VayprRealmBuilder().apply {
                originX = width / 2f
                originY = height / 2f
                worldWidth = min(width, height)
                worldHeight = min(width, height)
            } to 1f
        )

        realmBuildersAndProbabilities.forEach {
            random.maybe(probability = it.value) {
                realms.add(it.key.build())
            }
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