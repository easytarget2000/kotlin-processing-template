package eu.ezytaget.processing.kotlin_template.realms.tesseract

import eu.ezytaget.processing.kotlin_template.nextFloat
import eu.ezytaget.processing.kotlin_template.realms.Realm
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PVector

class TesseractRealm(maxColorValue: Float = 1f): Realm() {

    var strokeWeight = 4f

    var hue = 0.5f

    var hueVelocity = 0.01f

    var minHue = 0f

    var maxHue = maxColorValue

    var saturation = 0.7f

    var brightness = 1f

    var alpha = 0.9f

    private var angle = 0f

    private lateinit var tesseracts: List<Tesseract>

    override fun setup(pGraphics: PGraphics) {
        super.setup(pGraphics)

        val numberOfTesseracts = random.nextInt(from = 1, until = 10)
        tesseracts = (0 until numberOfTesseracts).map {
            val scale = random.nextFloat(from = 0.1f, until = 0.5f)
            Tesseract(scale)
        }
    }

    override fun update(pApplet: PApplet) {
        super.update(pApplet)
        updateColorValues()
        angle += 0.02f
    }

    override fun drawIn(pGraphics: PGraphics) {
        super.drawIn(pGraphics)

        pGraphics.beginDraw()
        pGraphics.push()

        pGraphics.translate(pGraphics.width / 2f, pGraphics.height / 2f)
//        pGraphics.updateRotations()
        pGraphics.rotateX(-PConstants.PI / 2f)

        pGraphics.strokeWeight(1f)
        repeat(3) {
            pGraphics.pushMatrix()
            pGraphics.rotateZ((it / 3f) * PConstants.TWO_PI)
            tesseracts.forEach { tesseract ->
                draw(tesseract, pGraphics)
            }
            pGraphics.popMatrix()
        }

        pGraphics.pop()
        pGraphics.endDraw()
    }

    private fun draw(tesseract: Tesseract, pGraphics: PGraphics) {
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
            projected.mult(pGraphics.width * tesseract.scale)

            projected
        }

        pGraphics.strokeWeight(strokeWeight)
        pGraphics.stroke(hue, saturation, brightness, alpha)

        (0..3).forEach { vertexIndex ->
            connectVertices(0, vertexIndex, (vertexIndex + 1) % 4, projectedVerticesIn3D, pGraphics)
            connectVertices(0, vertexIndex + 4, (vertexIndex + 1) % 4 + 4, projectedVerticesIn3D, pGraphics)
            connectVertices(0, vertexIndex, vertexIndex + 4, projectedVerticesIn3D, pGraphics)
        }

        (0..3).forEach { vertexIndex ->
            connectVertices(8, vertexIndex, (vertexIndex + 1) % 4, projectedVerticesIn3D, pGraphics)
            connectVertices(8, vertexIndex + 4, (vertexIndex + 1) % 4 + 4, projectedVerticesIn3D, pGraphics)
            connectVertices(8, vertexIndex, vertexIndex + 4, projectedVerticesIn3D, pGraphics)
        }

        (0..7).forEach { vertexIndex ->
            connectVertices(0, vertexIndex, vertexIndex + 8, projectedVerticesIn3D, pGraphics)
        }
    }

    private fun updateColorValues() {
        hue += hueVelocity
        if (hue > maxHue) {
            hue = minHue
        }
    }

    private fun connectVertices(offset: Int = 0, index1: Int, index2: Int, vertices: List<PVector>, pGraphics: PGraphics) {
        val vertex1 = vertices[index1 + offset]
        val vertex2 = vertices[index2 + offset]
        pGraphics.line(vertex1.x, vertex1.y, vertex1.z, vertex2.x, vertex2.y, vertex2.z)
    }

}
