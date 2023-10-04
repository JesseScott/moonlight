package tt.co.jesses.moonlight.android.data.model

/**
 * Raw data representation of Moon illumination
 */
data class MoonData(
    val fraction: Double = 0.0,
    val phase: Double = 0.0,
    val angle: Double = 0.0,
    val azimuth: Double = 0.0,
    val altitude: Double = 0.0,
    val distance: Double = 0.0,
    val parallacticAngle: Double = 0.0,
)
