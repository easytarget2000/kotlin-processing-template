package eu.ezytaget.processing.barnsley_fern

import processing.core.PApplet
import processing.core.PConstants.HALF_PI
import processing.core.PVector

class Camera(
        var position: PVector,
        private var focusVector: PVector
) {

    var speed = 10f

    private val upVector = PVector(0f, 20f, 0f)

    private val forwardVector: PVector
        get() {
            val forwardVector = focusVector.copy()
            return forwardVector.setMag(speed)
        }

    private val leftVector: PVector
        get() = forwardVector.rotateY(HALF_PI)

    fun moveForward() {
        position = PVector.add(position, forwardVector)
    }

    fun moveBackward() {
        position = PVector.sub(position, forwardVector)
    }

    fun moveLeft() {
        position = PVector.add(position, leftVector)
    }

    fun moveRight() {
        position = PVector.sub(position, leftVector)
    }

    fun moveUp() {
        position = PVector.add(position, upVector)
    }

    fun moveDown() {
        position = PVector.sub(position, upVector)
    }

    fun adjustAppletCamera(pApplet: PApplet) {
        val cameraCenter = PVector.add(position, focusVector)
        pApplet.camera(
                position.x, position.y, position.z,
                cameraCenter.x, cameraCenter.y, cameraCenter.z,
                0f, -1f, 0f
        )
    }
}