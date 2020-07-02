package eu.ezytaget.processing.kotlin_template

fun PApplet.maybe(probability: Float = 0.5f, lambda: (() -> Unit)) = if (random(1f) < probability) {
    lambda()
    true
} else {
    false
}