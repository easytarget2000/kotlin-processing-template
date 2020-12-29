package eu.ezytaget.processing.kotlin_template.realms.camera

import eu.ezytaget.processing.kotlin_template.PApplet
import processing.video.Capture

class CameraRealm {

    var capture: Capture? = null

    fun setCaptureAndStart(pApplet: PApplet, preferredCameraName: String = "FaceTime") {
        capture?.stop()

        val cameras = Capture.list()

        val index = cameras.firstOrNull { it.contains(preferredCameraName, ignoreCase = true) }

        if (index == null) {
            capture = null
        } else {
            capture = Capture(pApplet, index)
            capture?.start()
        }
    }

    
}