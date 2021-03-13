package eu.ezytaget.processing.kotlin_template.realms.test_image

import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PGraphics
import kotlin.random.Random

class TestImageRealm(random: Random = Random.Default): Realm(random) {

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)
        beginDraw(pGraphics)

        pGraphics.noStroke()

        val width = pGraphics.width.toFloat()
        val height = pGraphics.height.toFloat()

        pGraphics.fill(0.9f, 1f, 1f, 1f)
        pGraphics.rect(0f, 0f, width / 3f, height)
        pGraphics.fill(0.5f, 1f, 1f, 1f)
        pGraphics.rect(width / 3f, 0f, 2 * (width / 3f), height)
        pGraphics.fill(0.2f, 1f, 1f, 1f)
        pGraphics.rect(2 * (width / 3f), 0f, width, height)

        endDraw(pGraphics)
    }
}