package eu.ezytarget.processingtemplate

import processing.core.PImage
import processing.video.Capture


internal class CameraCaptureManager {
    private var capture: Capture? = null

    fun startCapture(pApplet: MainApplet, qualifier: String? = null) {
        val requestedWidth = pApplet.width
        val requestedHeight = pApplet.height
        val fps = 60F

        val devices = Capture.list()

        if (devices.isEmpty()) {
            System.err.println("There are no cameras available for capture.")
            this.capture = null
            return
        }

        println("Available cameras:")
        devices.forEach(::println)

        val selectedDeviceId = if (qualifier.isNullOrBlank()) {
            devices.firstOrNull()
        } else {
            devices.firstOrNull { it.contains(qualifier, ignoreCase = true) }
        }

        println("Selected camera: $selectedDeviceId")

//        this.capture = Capture(pApplet, requestedWidth, requestedHeight, selectedDeviceId, fps)
        this.capture = Capture(pApplet, selectedDeviceId)
        this.capture?.start()
    }

    fun read(): PImage? {
        val capture = this.capture ?: return null

        if (capture.available()) {
            capture.read()
        }

        return capture
    }
}
