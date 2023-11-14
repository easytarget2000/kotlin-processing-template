package eu.ezytarget.processingtemplate

import processing.core.PImage
import processing.video.Capture


internal class CameraCaptureManager {
    private var capture: Capture? = null

    fun startCapture(pApplet: MainApplet, qualifier: String) {
        val requestedWidth = 1920//pApplet.width
        val requestedHeight = 1080
        val fps = 20F

        val deviceNames = Capture.list()

        if (deviceNames.isEmpty()) {
            System.err.println("There are no cameras available for capture.")
            this.capture = null
            return
        }

        println("Available cameras:")
        deviceNames.forEach(::println)

        val lookupDeviceIndex = deviceNames.indexOfFirst {
            it.contains(qualifier, ignoreCase = true)
        }

        val selectedDeviceIndex = if (lookupDeviceIndex < 0) {
            0
        } else {
            lookupDeviceIndex
        }

        println("Selected camera: ${deviceNames[selectedDeviceIndex]}")

        val fullDeviceId =
            "pipeline:avfvideosrc device-index=$selectedDeviceIndex"
        this.capture =
            Capture(pApplet, requestedWidth, requestedHeight, fullDeviceId, fps)
        this.capture?.start()
    }

    fun read(): PImage? {
        val capture = this.capture ?: return null

        if (capture.available()) {
            capture.read()
        }

        return capture
    }

    fun stop() {
        this.capture?.stop()
    }
}
