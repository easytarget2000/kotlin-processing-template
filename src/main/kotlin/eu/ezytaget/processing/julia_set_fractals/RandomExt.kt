package eu.ezytaget.processing.julia_set_fractals

import kotlin.math.nextDown
import kotlin.random.Random

fun Random.nextFloat(from: Float = 0f, until: Float = 1f): Float {
    val size = until - from
    val r = if (size.isInfinite() && from.isFinite() && until.isFinite()) {
        val r1 = nextFloat() * (until / 2f - from / 2f)
        from + r1 + r1
    } else {
        from + nextFloat() * size
    }

    return if (r >= until) {
        until.nextDown()
    } else {
        r
    }
}

fun Random.maybe(probability: Float = 0.5f, lambda: (() -> Unit)) = if (nextFloat(1f) < probability) {
    lambda()
    true
} else {
    false
}