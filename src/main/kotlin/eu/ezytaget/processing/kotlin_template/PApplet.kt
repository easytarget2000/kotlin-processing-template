package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import processing.core.PConstants
import processing.core.PVector

class PApplet : processing.core.PApplet() {

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var waitingForClickToDraw = false

    private var spheres: MutableList<Sphere> = mutableListOf()

    private var minNumberOfSpheres = 2

    private var maxNumberOfSpheres = 16

    private var radiusFactor = DESIRED_RADIUS_FACTOR

    override fun settings() {
        if (FULL_SCREEN) {
            fullScreen(RENDERER)
        } else {
            size(WIDTH, HEIGHT, RENDERER)
        }
    }

    override fun setup() {
        frameRate(FRAME_RATE)
        colorMode(COLOR_MODE, MAX_COLOR_VALUE)
        clearFrame()
        noCursor()
        lights()
        initSpheres()
    }

    override fun draw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = 0.1f)
        }

        updateRadiusFactor()

        translate(width / 2f, height / 2f)

        val baseRotation = ((millis() % 10_000) / 10_000f) * TWO_PI

        spheres.forEach {
            pushMatrix()
            rotateY(baseRotation * it.rotationSpeed)
            stroke(it.colorValue1, it.colorValue2, it.colorValue3, it.alpha)
            noiseSphere(radius = it.radius * radiusFactor, randomSeed =  it.randomSeed)
            popMatrix()
        }

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            ' ' -> radiusFactorVelocity = 0.1f
        }
    }

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
    }


    private fun initSpheres() {
        spheres = mutableListOf()
        val numberOfSpheres = random(minNumberOfSpheres.toFloat(), (maxNumberOfSpheres + 1).toFloat()).toInt()
        val minRadius = width / 16f
        val maxRadius = width / 2f

        val minRotationSpeed = 2f
        val maxRotationSpeed = 8f
        val colorValue1 = random(1f)

        repeat((0..numberOfSpheres).count()) {
            val radius = if (it == 0) {
                maxRadius
            } else {
                random(minRadius, maxRadius)
            }

            val sphere = Sphere(
                    radius = radius,
                    randomSeed = random(2000f).toLong(),
                    rotationSpeed = random(minRotationSpeed, maxRotationSpeed),
                    colorValue1 = colorValue1,
                    colorValue3 = 1f,
                    colorValue2 = 1f,
                    alpha = 1f
            )
            spheres.plusAssign(sphere)
        }
    }

    private fun noiseSphere(radius: Float, randomSeed: Long, numberOfPoints: Int = 4096 * 2) {
        randomSeed(randomSeed)
        repeat((0..numberOfPoints).count()) {
            val randomVector = PVector(
                    random(-100f, 100f),
                    random(-100f, 100f),
                    random(-100f, 100f)
            ).setMag(radius)
            point(randomVector.x, randomVector.y, randomVector.z)
        }
    }

    private var radiusFactorVelocity = 0f

    private fun updateRadiusFactor() {
        radiusFactorVelocity -= 0.01f

        if (radiusFactor < DESIRED_RADIUS_FACTOR) {
            radiusFactor = DESIRED_RADIUS_FACTOR
        } else {
            radiusFactor += radiusFactorVelocity
        }
    }

    companion object {
        private const val CLICK_TO_DRAW = false
        private const val FULL_SCREEN = false
        private const val WIDTH = 1400
        private const val HEIGHT = 900
        private const val RENDERER = PConstants.P3D
        private const val COLOR_MODE = PConstants.HSB
        private const val MAX_COLOR_VALUE = 1f
        private const val FRAME_RATE = 60f
        private const val DRAW_BACKGROUND_ON_DRAW = true
        private const val DESIRED_RADIUS_FACTOR = 1f
        private const val RADIUS_FACTOR_TOLERANCE = 0.01f
        private const val RADIUS_FACTOR_PULL = 0.01f

        fun runInstance() {
            val instance = PApplet()
            instance.runSketch()
        }
    }
}