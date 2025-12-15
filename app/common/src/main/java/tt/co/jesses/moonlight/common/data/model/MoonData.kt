package tt.co.jesses.moonlight.common.data.model

import androidx.annotation.StringRes
import tt.co.jesses.moonlight.common.R

/**
 * Raw data representation of Moon illumination
 */
data class MoonData(
    @StringRes val fractionRes: Int = R.string.data_fraction,
    val fraction: Float = 0.0f,
    @StringRes val phaseRes: Int = R.string.data_phase,
    val phase: Float = 0.0f,
    @StringRes val angleRes: Int = R.string.data_angle,
    val angle: Float = 0.0f,
    @StringRes val azimuthRes: Int = R.string.data_azimuth,
    val azimuth: Float = 0.0f,
    @StringRes val altitudeRes: Int = R.string.data_altitude,
    val altitude: Float = 0.0f,
    @StringRes val distanceRes: Int = R.string.data_distance,
    val distance: Float = 0.0f,
    @StringRes val parallacticAngleRes: Int = R.string.data_parallactic_angle,
    val parallacticAngle: Float = 0.0f,
)
