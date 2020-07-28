package eu.ezytaget.processing.kotlin_template.tesseract

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector

object TesseractProjector {

    fun draw(tesseract: Tesseract, angle: Float, pApplet: PApplet) {

        val projected3d = tesseract.vertices.map {
            val rotationXY = arrayOf(
                    floatArrayOf(PApplet.cos(angle), -PApplet.sin(angle), 0f, 0f),
                    floatArrayOf(PApplet.sin(angle), PApplet.cos(angle), 0f, 0f),
                    floatArrayOf(0f, 0f, 1f, 0f),
                    floatArrayOf(0f, 0f, 0f, 1f)
            )
            val rotationZW = arrayOf(
                    floatArrayOf(1f, 0f, 0f, 0f),
                    floatArrayOf(0f, 1f, 0f, 0f),
                    floatArrayOf(0f, 0f, PApplet.cos(angle), -PApplet.sin(angle)),
                    floatArrayOf(0f, 0f, PApplet.sin(angle), PApplet.cos(angle))
            )

            var rotated: P4Vector = MatrixCalculator.matmul4(rotationXY, it)
            rotated = MatrixCalculator.matmul4(rotationZW, rotated)
            val distance = 2f
            val w = 1 / (distance - rotated.w)
            val projection = arrayOf(floatArrayOf(w, 0f, 0f, 0f), floatArrayOf(0f, w, 0f, 0f), floatArrayOf(0f, 0f, w, 0f))
            val projected: PVector = MatrixCalculator.matmul(projection, rotated)
            projected.mult(pApplet.width / 8.toFloat())

            pApplet.stroke(1f, 1f)
            pApplet.strokeWeight(32f)
            pApplet.noFill()
            pApplet.point(projected.x, projected.y, projected.z)

            projected
        }

        pApplet.strokeWeight(4f)
        pApplet.stroke(1f)

        for (i in 0..3) {
            connectVertices(0, i, (i + 1) % 4, projected3d, pApplet)
            connectVertices(0, i + 4, (i + 1) % 4 + 4, projected3d, pApplet)
            connectVertices(0, i, i + 4, projected3d, pApplet)
        }
        for (i in 0..3) {
            connectVertices(8, i, (i + 1) % 4, projected3d, pApplet)
            connectVertices(8, i + 4, (i + 1) % 4 + 4, projected3d, pApplet)
            connectVertices(8, i, i + 4, projected3d, pApplet)
        }
        for (i in 0..7) {
            connectVertices(0, i, i + 8, projected3d, pApplet)
        }
    }

    private fun connectVertices(offset: Int, i: Int, j: Int, points: List<PVector>, pApplet: PApplet) {
        val a = points[i + offset]
        val b = points[j + offset]
        pApplet.line(a.x, a.y, a.z, b.x, b.y, b.z)
    }
}