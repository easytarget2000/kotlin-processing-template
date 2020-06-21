package eu.ezytaget.processing.kotlin_template

import eu.ezytaget.processing.kotlin_template.palettes.DuskPalette
import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import processing.core.PConstants
import processing.core.PVector

class PApplet : processing.core.PApplet() {

    private val clapper = Clapper()

    private var lastBeatIntervalCount = 0

    private val backgroundDrawer = BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var waitingForClickToDraw = false

    private var spheres: MutableList<Sphere> = mutableListOf()

    private var minNumberOfSpheres = 2

    private var maxNumberOfSpheres = 16

    private var radiusFactor = DESIRED_RADIUS_FACTOR

    private var drawNoiseSpheres = false

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
        clapper.start()
    }

    override fun draw() {
        if (CLICK_TO_DRAW && waitingForClickToDraw) {
            return
        }

        if (DRAW_BACKGROUND_ON_DRAW) {
            backgroundDrawer.draw(pApplet = this, alpha = 0.1f)
        }

        if(clapper.update()) {
            handleClapperValue()
        }

        updateRadiusFactor()

        translate(width / 2f, height / 2f)

        val baseRotation = ((millis() % 100_000) / 100_000f) * TWO_PI

        noFill()

        sphereDetail(16)

        spheres.forEach {
            pushMatrix()
            rotateY(baseRotation * it.rotationSpeed)
            stroke(it.colorValue1, it.colorValue2, it.colorValue3, it.alpha)
            val radius = it.radius * radiusFactor
            if (drawNoiseSpheres) {
                noiseSphere(radius, randomSeed = it.randomSeed)
            } else {
                sphere(radius)
            }
            popMatrix()
        }

        if (CLICK_TO_DRAW) {
            waitingForClickToDraw = true
        }
    }

    override fun keyPressed() {
        when (key) {
            ' ' ->
                clapper.tapBpm()
        }
    }

    private fun clearFrame() {
        backgroundDrawer.draw(
                pApplet = this,
                alpha = 1f
        )
    }

    private fun toggleDrawStyle() {
        drawNoiseSpheres = !drawNoiseSpheres
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

    private fun handleClapperValue() {
        val intervalNumbers = clapper.intervalNumbers
        if (lastBeatIntervalCount != intervalNumbers.getValue(BeatInterval.TwoWhole)) {

//            val random = random(1f)
//            when {
//                random < 0.2f -> {
//                    bounce()
//                }
//                random < 0.4f -> {
//                    initSpheres()
//                }
//                random < 0.6f -> {
//
//                }
//            }

            maybe { initSpheres() }
            maybe { toggleDrawStyle() }

            bounce()

            lastBeatIntervalCount = intervalNumbers.getValue(BeatInterval.TwoWhole)
        }
    }

    private fun maybe(probability: Float = 0.5f, lambda: (() -> Unit)) {
        if (random(1f) < probability) {
            lambda()
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

        if (radiusFactor < DESIRED_RADIUS_FACTOR) {
            radiusFactor = DESIRED_RADIUS_FACTOR
        } else if (radiusFactor > DESIRED_RADIUS_FACTOR) {
            radiusFactorVelocity -= RADIUS_FACTOR_PULL
        }

        radiusFactor += radiusFactorVelocity
    }

    private fun bounce() {
        radiusFactorVelocity = 0.05f
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