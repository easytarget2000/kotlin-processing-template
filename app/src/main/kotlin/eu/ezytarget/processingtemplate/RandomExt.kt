package eu.ezytarget.processingtemplate

import kotlin.math.nextDown
import kotlin.random.Random

fun Random.nextFloat(until: Float = 1f) = nextFloatInRange(from = 0f, until = until)

fun Random.nextFloatInRange(from: Float, until: Float): Float {
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

fun Random.maybe(probability: Float = 0.5f, lambda: (() -> Unit)) =
    if (nextFloatInRange(from = 0f, until = 1f) < probability) {
        lambda()
        true
    } else {
        false
    }
