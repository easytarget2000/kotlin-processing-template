package eu.ezytarget.processingtemplate

import processing.core.PImage
import processing.video.Capture


internal class CameraCaptureManager {
    private var capture: Capture? = null

    fun startCapture(pApplet: MainApplet, qualifier: String? = null) {
        val requestedWidth = pApplet.width
        val requestedHeight = pApplet.height
        val fps = 20F

        val deviceNames = Capture.list()

        if (deviceNames.isEmpty()) {
            System.err.println("There are no cameras available for capture.")
            this.capture = null
            return
        }

        println("Available cameras:")
        deviceNames.forEach(::println)

        val selectedDeviceIndex = if (qualifier.isNullOrBlank()) {
            0
        } else {
            deviceNames.indexOfFirst {
                it.contains(qualifier, ignoreCase = true)
            }
        }

        println("Selected camera: $deviceNames[selectedDeviceIndex]")

        val fullDeviceId =
            "pipeline:avfvideosrc device-index=$selectedDeviceIndex"
        this.capture =
            Capture(pApplet, requestedWidth, requestedHeight, fullDeviceId, fps)
//        this.capture = Capture(pApplet, selectedDeviceId, 30F)
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
