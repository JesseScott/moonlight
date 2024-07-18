package tt.co.jesses.moonlight.android.domain


fun Float.normalize(): Float {
    return this.coerceIn(0.0f, 1.0f)
}

fun Float.radians(): Float {
    return Math.abs(this) // naive implementation
}
