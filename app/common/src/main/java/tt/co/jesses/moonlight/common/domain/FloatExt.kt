package tt.co.jesses.moonlight.common.domain

/**
 * Normalize a float value to a range
 * Used for values that have different floors/ceilings
 */
fun Float.normalize(min: Float, max: Float): Float {
    return (this - min) / (max - min)
}
