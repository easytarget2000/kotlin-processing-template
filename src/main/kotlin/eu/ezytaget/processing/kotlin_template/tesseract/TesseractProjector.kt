package eu.ezytaget.processing.kotlin_template.tesseract

import processing.core.PApplet
import processing.core.PVector

object TesseractProjector {

    fun draw(tesseract: Tesseract, angle: Float, pApplet: PApplet) {
        val projectedVerticesIn3D = tesseract.vertices.map { vertex4D ->
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

            val xyRotatedVertex = MatrixCalculator.matmul4(rotationXY, vertex4D)
            val fullyRotatedVertex = MatrixCalculator.matmul4(rotationZW, xyRotatedVertex)

            val distance = 2f
            val w = 1 / (distance - fullyRotatedVertex.w)
            val projection = arrayOf(
                    floatArrayOf(w, 0f, 0f, 0f),
                    floatArrayOf(0f, w, 0f, 0f),
                    floatArrayOf(0f, 0f, w, 0f)
            )
            val projected = MatrixCalculator.matmul(projection, fullyRotatedVertex)
            projected.mult(pApplet.width * tesseract.scale)

            projected
        }

        pApplet.strokeWeight(4f)
        pApplet.stroke(1f)

        (0..3).forEach { vertexIndex ->
            connectVertices(0, vertexIndex, (vertexIndex + 1) % 4, projectedVerticesIn3D, pApplet)
            connectVertices(0, vertexIndex + 4, (vertexIndex + 1) % 4 + 4, projectedVerticesIn3D, pApplet)
            connectVertices(0, vertexIndex, vertexIndex + 4, projectedVerticesIn3D, pApplet)
        }

        (0..3).forEach { vertexIndex ->
            connectVertices(8, vertexIndex, (vertexIndex + 1) % 4, projectedVerticesIn3D, pApplet)
            connectVertices(8, vertexIndex + 4, (vertexIndex + 1) % 4 + 4, projectedVerticesIn3D, pApplet)
            connectVertices(8, vertexIndex, vertexIndex + 4, projectedVerticesIn3D, pApplet)
        }

        (0..7).forEach { vertexIndex ->
            connectVertices(0, vertexIndex, vertexIndex + 8, projectedVerticesIn3D, pApplet)
        }
    }

    private fun connectVertices(offset: Int = 0, index1: Int, index2: Int, vertices: List<PVector>, pApplet: PApplet) {
        val vertex1 = vertices[index1 + offset]
        val vertex2 = vertices[index2 + offset]
        pApplet.line(vertex1.x, vertex1.y, vertex1.z, vertex2.x, vertex2.y, vertex2.z)
    }
}