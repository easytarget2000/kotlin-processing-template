import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.BeatIntervalUpdate
import eu.ezytarget.processingtemplate.layers.Layer
import processing.core.PGraphics
import kotlin.math.floor

internal class CathodeRayer(override var intensity: Layer.Intensity) : Layer {
    var progressSpeed = 0.0043F
    var numberOfLines = 16
        set(value) {
            require(value > 0)
            field = value
        }
    var rayDiameterScale = 2F
        set(value) {
            require(field > 0F)
            field = value
        }
    private var progress = 0F
    private val progressPerLine: Float
        get() = 1F / this.numberOfLines.toFloat()

    override fun update(deltaTime: Long) {
        this.progress += this.progressSpeed
        if (this.progress > 1F) {
            this.progress = 0F
        }
    }

    override fun update(beatStatus: Map<BeatInterval, BeatIntervalUpdate>) {}

    override fun draw(pGraphics: PGraphics) {
        val progressPerLine = this.progressPerLine
        val lineIndex = floor(this.progress / progressPerLine)
        val progressOnLine = lineIndex * progressPerLine
        val horizontalProgress = (this.progress - progressOnLine) / progressPerLine
        val lineHeight = pGraphics.height / (this.numberOfLines + 1).toFloat()
        val rayDiameter = this.rayDiameterScale * lineHeight

        pGraphics.noStroke()
        pGraphics.fill(1F)
        pGraphics.circle(
            horizontalProgress * pGraphics.width,
            (lineIndex + 1) * lineHeight,
            rayDiameter
        )
    }
}
