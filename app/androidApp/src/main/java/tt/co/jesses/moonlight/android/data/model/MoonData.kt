package tt.co.jesses.moonlight.android.data.model

import androidx.annotation.StringRes
import tt.co.jesses.moonlight.android.R

/**
 * Raw data representation of Moon illumination
 */
data class MoonData(
    @StringRes val fractionRes: Int = R.string.data_fraction,
    val fraction: Double = 0.0,
    @StringRes val phaseRes: Int = R.string.data_phase,
    val phase: Double = 0.0,
    @StringRes val angleRes: Int = R.string.data_angle,
    val angle: Double = 0.0,
    @StringRes val azimuthRes: Int = R.string.data_azimuth,
    val azimuth: Double = 0.0,
    @StringRes val altitudeRes: Int = R.string.data_altitude,
    val altitude: Double = 0.0,
    @StringRes val distanceRes: Int = R.string.data_distance,
    val distance: Double = 0.0,
    @StringRes val parallacticAngleRes: Int = R.string.data_parallactic_angle,
    val parallacticAngle: Double = 0.0,
)
