package tt.co.jesses.moonlight.common.data.repository

import android.util.Log
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonPosition
import tt.co.jesses.moonlight.common.data.model.MoonData
import javax.inject.Inject

/**
 * Class responsible for accessing [org.shredzone.commons.suncalc] library
 */
class MoonlightDataSource @Inject constructor() {

    /**
     * Gets [MoonIllumination] and [MoonPosition] from Suncalc and maps to [MoonData]
     */
    fun getMoonIllumination(latitude: Double? = null, longitude: Double? = null): MoonData {
        val illumination = MoonIllumination.compute().execute()
        // Illumination is the same everywhere, but the moon's position in the sky needs a real location
        val position = if (latitude == null || longitude == null) {
            null
        } else {
            runCatching {
                MoonPosition.compute().at(latitude, longitude).execute()
            }.getOrElse { e ->
                if (tt.co.jesses.moonlight.common.BuildConfig.DEBUG) {
                    Log.w(TAG, "Failed to compute MoonPosition", e)
                }
                null
            }
        }
        if (tt.co.jesses.moonlight.common.BuildConfig.DEBUG) {
            Log.d(TAG, "MoonIllumination from SunCalc: $illumination")
            Log.d(TAG, "MoonPosition from SunCalc: $position")
        }
        return MoonData(
            fraction = illumination.fraction.toFloat(),
            phase = illumination.phase.toFloat(),
            angle = illumination.angle.toFloat(),
            azimuth = position?.azimuth?.toFloat() ?: 0f,
            altitude = position?.altitude?.toFloat() ?: 0f,
            distance = position?.distance?.toFloat() ?: 0f,
            parallacticAngle = position?.parallacticAngle?.toFloat() ?: 0f,
            hasPosition = position != null,
        )
    }

    companion object {
        private val TAG = MoonlightDataSource::class.java.simpleName
    }
}