package tt.co.jesses.moonlight.android.data.model

/**
 * Raw data representation of Moon illumination
 */
data class MoonIlluminationData(
    val fraction: Double = 0.0,
    val phase: Double = 0.0,
    val angle: Double = 0.0,
)
