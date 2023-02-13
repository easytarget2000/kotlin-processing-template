import processing.core.PApplet
import processing.core.PVector

internal class MainApplet : PApplet() {
    private var gridOffset = PVector(0f, 0f, 0f)
    private var gridOffsetVelocity = PVector(0.9f, 0.1f, 0f)
    private var gridLineWidth = 1f
    private var gridDistance = 3
    private var gridColor = Color(1f, 0.5f, 0.5f, 1f)

    public override fun runSketch() {
        super.runSketch()
    }

    override fun settings() {
//        size(800, 600, P2D)
        fullScreen(P2D, 2)
    }

    override fun setup() {
        colorMode(HSB, 1f)
    }

    override fun draw() {
        background(0)
        update()
        drawGrid()
    }

    private fun update() {
        gridOffset = PVector.add(gridOffset, gridOffsetVelocity)
    }

    private fun drawGrid() {
        strokeWeight(gridLineWidth)
        stroke(gridColor.value1, gridColor.value2, gridColor.value3, gridColor.alpha)

        val startX = 0 - gridDistance
        val endX = width + gridDistance
        val startY = 0 - gridDistance
        val endY = height + gridDistance

        val gridOffsetX = gridOffset.x % gridDistance.toFloat()
        val gridOffsetY = gridOffset.y % gridDistance.toFloat()

        for (x in startX..endX step gridDistance) {
            line(
                x.toFloat() + gridOffsetX,
                startY.toFloat() + gridOffsetY,
                x.toFloat() + gridOffsetX,
                endY.toFloat() + gridOffsetY,
            )
            for (y in startY..endY step gridDistance) {
                line(
                    startX.toFloat() + gridOffsetX,
                    y.toFloat() + gridOffsetY,
                    endX.toFloat() + gridOffsetX,
                    y.toFloat() + gridOffsetY,
                )
            }
        }
    }
}

internal data class Color(
    var value1: Float,
    var value2: Float,
    var value3: Float,
    var alpha: Float,
)
