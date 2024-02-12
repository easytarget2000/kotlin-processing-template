package eu.ezytarget.processingtemplate

import eu.ezytarget.clapper.BeatInterval
import eu.ezytarget.clapper.Clapper
import eu.ezytarget.processingtemplate.char_raster.CharRaster
import eu.ezytarget.processingtemplate.palettes.DuskPalette
import eu.ezytarget.processingtemplate.realms.RealmsManager
import processing.core.PConstants
import processing.core.PGraphics
import processing.event.MouseEvent
import themidibus.MidiBus
import kotlin.random.Random


class MainApplet : processing.core.PApplet() {
    private val random = Random.Default
    private val clapper = Clapper().also { it.bpm = 130f }
    private var runClapper = true
    private val backgroundDrawer =
        BackgroundDrawer(DuskPalette(), alpha = 0.01f)

    private var drawBackgroundOnDraw = true
        set(value) {
            field = value
            println("PApplet: drawBackgroundOnDraw: set: $drawBackgroundOnDraw")
        }

    private var backgroundAlpha = 0.1f
    private var useBackgroundDrawerPalette = false
    private var waitingForClickToDraw = false
    private var radiusFactorVelocity = 0f
    private var lastLaserClearMillis = 0L
    private var raster: CharRaster = CharRaster()
    private var applyCharRaster = false
    private var clearFrameOnTextSizeFinding = false
    private val realmsManager = RealmsManager()
    private var smearPixels = false
    private var laserClearMode = false
    private val captureManager = CameraCaptureManager()
    private var drawCameraCapture = false
    private var cameraFpsModulo = 4
    private var noiseSeedSalt = System.currentTimeMillis()

    private lateinit var kaleidoscope: PGraphics
    private var minNumberOfKaleidoscopeEdges = 2
    private var maxNumberOfKaleidoscopeEdges = 8
    private var numberOfKaleidoscopeEdges = this.minNumberOfKaleidoscopeEdges
        set(value) {
            field = value
            println("PApplet: numberOfKaleidoscopeEdges: set: $numberOfKaleidoscopeEdges")
        }

    private var lastLoggedFrameRate: Float? = null
    private var frameRateLoggingThreshold = 7f
    private var numberOfIterationsPerFrame = 1

    public override fun runSketch() {
        super.runSketch()
    }

    override fun settings() {
        if (AppletConfig.FULL_SCREEN) {
            fullScreen(AppletConfig.RENDERER, AppletConfig.DISPLAY_ID)
        } else {
            size(AppletConfig.WIDTH, AppletConfig.HEIGHT, AppletConfig.RENDERER)
        }
    }

    override fun setup() {
        frameRate(AppletConfig.FRAME_RATE)
        colorMode(AppletConfig.COLOR_MODE, AppletConfig.MAX_COLOR_VALUE)

        this.kaleidoscope =
            this.createGraphics(width, height, AppletConfig.RENDERER)
        this.kaleidoscope.beginDraw()
        this.kaleidoscope.colorMode(
            AppletConfig.COLOR_MODE,
            AppletConfig.MAX_COLOR_VALUE
        )
        this.kaleidoscope.smooth()
        this.kaleidoscope.lights()
        this.kaleidoscope.ambientLight(0.8f, 1f, 1f)
        this.kaleidoscope.endDraw()

        this.setupMidiInput()

        this.raster.setup(pGraphics = this.kaleidoscope)

        this.captureManager.startCapture(pApplet = this, qualifier = "webcam")

        this.initRealms()
        this.randomSeed(System.currentTimeMillis())

        this.clearFrame()
        this.clapper.start()
    }

    override fun draw() {
        if (AppletConfig.CLICK_TO_DRAW && this.waitingForClickToDraw) {
            return
        }

//        this.backgroundAlpha = noise(frameCount / 1000f)

        if (this.drawBackgroundOnDraw) {
            this.clearWithBackground()
        }

        if (runClapper) {
            this.updateClapper()
        }

        (0..numberOfIterationsPerFrame).forEach { _ ->
            push()
            iterateDraw()
            pop()
        }

        if (this.smearPixels) {
            this.smearPixels()
        }

        if (this.laserClearMode) {
            this.laserClear()
        }

        if (this.applyCharRaster) {
            this.raster.drawIn(pApplet = this)
        }

        if (this.drawCameraCapture) {
            this.drawCameraCaptureIfScheduled()
        }

        this.logFrameRateIfNeeded()
    }

    override fun keyPressed() {
        when (key) {
            AppletConfig.CLAPPER_TAP_BPM_KEY ->
                clapper.tapBpm()

            AppletConfig.CLEAR_FRAME_KEY ->
                clearFrame()

            AppletConfig.TOGGLE_CLEAR_BACKGROUND_KEY ->
                toggleClearBackground()

            AppletConfig.TOGGLE_SMEAR_PIXELS_KEY ->
                toggleSmearPixels()

            AppletConfig.TOGGLE_LASER_CLEAR_MODE_KEY ->
                toggleLaserClearMode()

            AppletConfig.INIT_REALMS_KEY ->
                initRealms()

            AppletConfig.SET_NUMBER_OF_KALEIDOSCOPE_EDGES_KEY ->
                setRandomNumberOfKaleidoscopeEdges()

            AppletConfig.SET_NEXT_NOISE_SEED_KEY ->
                setNextNoiseSeed()
        }
    }

    override fun mouseClicked(event: MouseEvent?) {
        super.mouseClicked(event)
        if (event == null) {
            return
        }

        this.realmsManager.handleMouseClick(
            event.button,
            event.x,
            event.y,
            pApplet = this
        )
    }

    /*
    Implementations
     */

    private fun setupMidiInput() {
        val midiDevices = MidiBus.availableInputs()
        midiDevices.forEach {
            println("MIDI input: $it")
        }
    }

    private fun initRealms() {
        this.realmsManager.initRandomRealms(
            pApplet = this,
            pGraphics = kaleidoscope
        )
    }

    private fun clearWithBackground() {
        this.backgroundDrawer.draw(
            pApplet = this,
            alpha = this.backgroundAlpha
        )
//            backgroundDrawer.draw(kaleidoscope, alpha = backgroundAlpha)
        this.kaleidoscope.beginDraw()
        this.kaleidoscope.clear()
        this.kaleidoscope.endDraw()
    }

    private fun iterateDraw() {
        realmsManager.update(pApplet = this)

        kaleidoscope.beginDraw()
        realmsManager.drawIn(pGraphics = kaleidoscope, frameCount = frameCount)
        kaleidoscope.endDraw()

        if (numberOfKaleidoscopeEdges <= 1) {
            image(kaleidoscope, 0f, 0f)
        } else {
            push()
            repeat(numberOfKaleidoscopeEdges) {
                pushMatrix()
                translate(width / 2f, height / 2f)
                rotate((it / numberOfKaleidoscopeEdges.toFloat()) * PConstants.TWO_PI)
                image(kaleidoscope, -100f, -kaleidoscope.height / 2f)
                popMatrix()
            }
            pop()
        }
    }

    private fun clearAll() {
        initRealms()
        clearFrame()
    }

    private fun clearFrame() {
        if (this.useBackgroundDrawerPalette) {
            this.backgroundDrawer.drawRandomColor(
                pApplet = this,
                alpha = 1f,
                random = random
            )
        } else {
            this.backgroundDrawer.draw(pApplet = this, alpha = 1f)
        }
    }

    private fun smearPixels() {
        this.loadPixels()
        this.pixels.forEachIndexed { i, _ ->
            if (i + 10 < pixels.size) {
                val neighborValue = pixels[i + 10]
                pixels[i] = pixels[i] - neighborValue
            }
        }
        this.updatePixels()
    }

    private fun laserClear() {
        val nowMillis = System.currentTimeMillis()
        val clearNow = this.random.nextBoolean() &&
                (nowMillis - this.lastLaserClearMillis) > 70L
        if (clearNow) {
            clearFrame()
            this.lastLaserClearMillis = nowMillis
        }
    }

    private fun drawCameraCaptureIfScheduled() {
        if (this.frameCount % this.cameraFpsModulo != 0) {
            return
        }

        this.captureManager.read()?.let {
            this.graphics.image(
                it,
                0F,
                0F,
                this.graphics.width.toFloat(),
                this.graphics.height.toFloat()
            )
        }
    }

    private fun updateClapper() {
        val clapperResult = clapper.update()

        if (clapperResult[BeatInterval.Quarter]?.didChange == true) {
            random.maybe(probability = 0.5f) {
                realmsManager.cellAutomaton3D?.update()
            }
        }

        if (clapperResult[BeatInterval.Whole]?.didChange == true) {
            this.random.maybe(probability = 0.9f) {
                bounce()
            }
            this.random.maybe {
                setRandomZRotationVelocity()
            }
            this.random.maybe(probability = 0.9f) {
                clearFrame()
            }
            this.random.maybe(probability = 0.1f) {
                setRandomNumberOfKaleidoscopeEdges()
            }
        }

        if (clapperResult[BeatInterval.TwoWhole]?.didChange == true) {
            random.maybe(probability = 0.2f) {
                initRealms()
            }
            random.maybe(probability = 0.5f) {
                toggleSmearPixels()
            }
            random.maybe {
                realmsManager.setRandomStyle()
            }
            random.maybe {
                setRandomBackgroundAlpha()
            }
            random.maybe {
                setRandomXRotationVelocity()
            }
            random.maybe {
                setRandomZRotationVelocity()
            }
            random.maybe {
                setRandomBackgroundAlpha()
            }
            random.maybe {
                realmsManager.scanStripesRealm?.resetProgress()
            }
            random.maybe {
                setNextNoiseSeed()
            }
            random.maybe {
                toggleLaserClearMode()
            }
        }

        if (clapperResult[BeatInterval.EightWhole]?.didChange == true) {
            clearAll()
        }
    }

    private fun bounce() {
        radiusFactorVelocity = 0.05f
        realmsManager.bounce(pApplet = this)
    }

    private fun setRandomBackgroundAlpha() {
        if (!this.random.maybe {
                this.backgroundAlpha =
                    random(AppletConfig.MAX_COLOR_VALUE / 64f)
            }) {
            this.backgroundAlpha = 1f
        }
    }

    private fun setRandomXRotationVelocity() {
        this.realmsManager.tesseractRealm?.xRotationVelocity = random(
            -AppletConfig.MAX_ROTATION_VELOCITY,
            AppletConfig.MAX_ROTATION_VELOCITY
        )
    }

    private fun setRandomZRotationVelocity() {
        realmsManager.tesseractRealm?.zRotationVelocity = random(
            -AppletConfig.MAX_ROTATION_VELOCITY,
            AppletConfig.MAX_ROTATION_VELOCITY
        )
        realmsManager.scanStripesRealm?.setRandomRotationVelocity()
    }

    private fun toggleClearBackground() {
        this.drawBackgroundOnDraw = !this.drawBackgroundOnDraw
    }

    private fun toggleSmearPixels() {
        smearPixels = !smearPixels
    }

    private fun toggleLaserClearMode() {
        this.laserClearMode = !this.laserClearMode
    }

    private fun setTextSize(relativeTextSizeValue: Float) {
        val raster = raster ?: return

        if (clearFrameOnTextSizeFinding) {
            background(0.1f, 1f, 1f, 1f)
        }

        val textSize = map(relativeTextSizeValue, 0f, width.toFloat(), 4f, 32f)

        raster.setTextSize(pGraphics = this.kaleidoscope, textSize = textSize)
    }

    private fun setNextNoiseSeed() {
        val seed = frameCount.toLong() + noiseSeedSalt
        println(
            "PApplet: setNextNoiseSeed(): " +
                    "noiseSeedSalt: $noiseSeedSalt, seed: $seed"
        )
        noiseSeed(seed)
    }

    private fun setRandomNumberOfKaleidoscopeEdges() {
        if (this.random.nextFloat() < 0.3f) {
            this.numberOfKaleidoscopeEdges = this.minNumberOfKaleidoscopeEdges
            return
        }
        this.numberOfKaleidoscopeEdges = this.random.nextInt(
            from = this.minNumberOfKaleidoscopeEdges,
            until = this.maxNumberOfKaleidoscopeEdges
        )
    }

    private fun logFrameRateIfNeeded() {
        val lastLoggedFrameRate = lastLoggedFrameRate
        val frameRate = frameRate
        val shouldLogFrameRate = if (lastLoggedFrameRate == null) {
            true
        } else {
            abs(lastLoggedFrameRate - frameRate) > frameRateLoggingThreshold
        }

        if (shouldLogFrameRate) {
            println("PApplet: frameRate: $frameRate")
            this.lastLoggedFrameRate = frameRate
        }
    }
}