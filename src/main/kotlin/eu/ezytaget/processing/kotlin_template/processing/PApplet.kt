package eu.ezytaget.processing.kotlin_template.processing

class PApplet: processing.core.PApplet() {

    override fun setup() {
    }

    override fun draw() {
    }

    companion object {
        fun runInstance() {
            val instance = PApplet()
            instance.setSize(800, 600)
            instance.runSketch()
        }
    }
}