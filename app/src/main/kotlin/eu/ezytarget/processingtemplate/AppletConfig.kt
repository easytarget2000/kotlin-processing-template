package eu.ezytarget.processingtemplate

import processing.core.PConstants

object AppletConfig {
    const val CLICK_TO_DRAW = false
    const val FULL_SCREEN = true
    const val WIDTH = 800
    const val HEIGHT = 600
    const val RENDERER = PConstants.P3D
    const val DISPLAY_ID = 2
    const val COLOR_MODE = PConstants.HSB
    const val MAX_COLOR_VALUE = 1f
    const val FRAME_RATE = 60f
    const val MAX_ROTATION_VELOCITY = 0.03f
    const val CLAPPER_TAP_BPM_KEY = ' '
    const val INIT_REALMS_KEY = 'i'
    const val TOGGLE_CLEAR_BACKGROUND_KEY = 'b'
    const val SET_NUMBER_OF_KALEIDOSCOPE_EDGES_KEY = 'k'
    const val TOGGLE_LASER_CLEAR_MODE_KEY = 'l'
    const val TOGGLE_SMEAR_PIXELS_KEY = 's'
    const val CLEAR_FRAME_KEY = 'x'
    const val SET_NEXT_NOISE_SEED_KEY = 'n'
}