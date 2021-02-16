package eu.ezytaget.processing.kotlin_template.realms.camera

import processing.core.PApplet
import processing.video.Capture

class CameraRealm {

    var capture: Capture? = null
        private set

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

    fun read(): Capture? {
        if (capture?.available() == false) {
            return null
        }

        capture?.read()
        return capture
    }

    fun drawIn(pApplet: PApplet) {
        pApplet.image(read() ?: return, 0f, 0f)
    }
}