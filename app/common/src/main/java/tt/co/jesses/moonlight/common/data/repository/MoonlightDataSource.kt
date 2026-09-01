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
    fun getMoonIllumination(latitude: Double = 0.0, longitude: Double = 0.0): MoonData {
        val illumination = MoonIllumination.compute().execute()
        val position = runCatching {
            MoonPosition.compute().at(latitude, longitude).execute()
        }.getOrElse { e ->
            if (tt.co.jesses.moonlight.common.BuildConfig.DEBUG) {
                Log.w(TAG, "Failed to compute MoonPosition", e)
            }
            null
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
        )
    }

    companion object {
        private val TAG = MoonlightDataSource::class.java.simpleName
    }
}