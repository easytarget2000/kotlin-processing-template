package eu.ezytaget.processing.kotlin_template.tesseract

class Tesseract(
        val scale: Float = 0.25f
) {

    val vertices = arrayOf(
            P4Vector(-1f, -1f, -1f, 1f),
            P4Vector(1f, -1f, -1f, 1f),
            P4Vector(1f, 1f, -1f, 1f),
            P4Vector(-1f, 1f, -1f, 1f),
            P4Vector(-1f, -1f, 1f, 1f),
            P4Vector(1f, -1f, 1f, 1f),
            P4Vector(1f, 1f, 1f, 1f),
            P4Vector(-1f, 1f, 1f, 1f),
            P4Vector(-1f, -1f, -1f, -1f),
            P4Vector(1f, -1f, -1f, -1f),
            P4Vector(1f, 1f, -1f, -1f),
            P4Vector(-1f, 1f, -1f, -1f),
            P4Vector(-1f, -1f, 1f, -1f),
            P4Vector(1f, -1f, 1f, -1f),
            P4Vector(1f, 1f, 1f, -1f),
            P4Vector(-1f, 1f, 1f, -1f)
    )
}