package eu.ezytarget.processingtemplate.realms

import eu.ezytarget.processingtemplate.maybe
import eu.ezytarget.processingtemplate.realms.cell_automaton_3d.CellAutomaton3D
import eu.ezytarget.processingtemplate.realms.cell_automaton_3d.CellAutomaton3DBuilder
import eu.ezytarget.processingtemplate.realms.cotton_ball.CottonBallRealm
import eu.ezytarget.processingtemplate.realms.holodeck.HoloDeckBuilder
import eu.ezytarget.processingtemplate.realms.jellyfish.JellyFishRealmBuilder
import eu.ezytarget.processingtemplate.realms.julia_set.JuliaSetRealmBuilder
import eu.ezytarget.processingtemplate.realms.neon_tunnel.NeonTunnel
import eu.ezytarget.processingtemplate.realms.scan_stripes.ScanStripesRealm
import eu.ezytarget.processingtemplate.realms.scan_stripes.ScanStripesRealmBuilder
import eu.ezytarget.processingtemplate.realms.scanner.ScannerRealmBuilder
import eu.ezytarget.processingtemplate.realms.tesseract.TesseractRealm
import eu.ezytarget.processingtemplate.realms.tesseract.TesseractRealmBuilder
import eu.ezytarget.processingtemplate.realms.test_image.TestImageRealmBuilder
import eu.ezytarget.processingtemplate.realms.tree_realms.TreeRingsRealmBuilder
import eu.ezytarget.processingtemplate.realms.vaypr.VayprRealmBuilder
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

    var realmBuildersAndProbabilities: Map<RealmBuilder, Float> = mutableMapOf(
//        HoloDeckBuilder to 0.05f,
        TesseractRealmBuilder to 0.3f,
        ScanStripesRealmBuilder to 0.3f,
        TreeRingsRealmBuilder to 0.3f,
        ScannerRealmBuilder to 0.9f,
//        JellyFishRealmBuilder to 1f,
//        JuliaSetRealmBuilder() to 0.3f,
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
//        this.realmBuildersAndProbabilities[cellAutomaton3DBuilder] = 0.05f

        this.realms.clear()

        this.realmBuildersAndProbabilities.plus(
            VayprRealmBuilder().apply {
                originX = width / 3f
                originY = height / 3f
                worldWidth = min(width, height)
                worldHeight = min(width, height)
            } to 0.7f,
        )

        realmBuildersAndProbabilities.forEach {
            random.maybe(probability = it.value) {
                realms.add(it.key.build())
            }
        }

        realms.forEach { it.setup(pApplet, pGraphics) }
    }

    fun handleMouseClick(
        button: Int,
        mouseX: Int,
        mouseY: Int,
        pApplet: PApplet
    ) {
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