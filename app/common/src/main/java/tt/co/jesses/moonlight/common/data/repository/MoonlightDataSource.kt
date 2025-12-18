package tt.co.jesses.moonlight.common.data.repository

import android.util.Log
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonPosition
import tt.co.jesses.moonlight.common.data.model.MoonData
import tt.co.jesses.moonlight.common.BuildConfig
import javax.inject.Inject

/**
 * Class responsible for accessing [org.shredzone.commons.suncalc] library
 */
class MoonlightDataSource @Inject constructor() {

    /**
     * Gets [MoonIllumination] and [MoonPosition] from Suncalc and maps to [MoonData]
     */
    fun getMoonIllumination(): MoonData {
        val illumination = MoonIllumination.compute().execute()
        val position = MoonPosition.compute().execute()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "MoonIllumination from SunCalc: $illumination")
            Log.d(TAG, "MoonPosition from SunCalc: $position")
        }
        return MoonData(
            fraction = illumination.fraction.toFloat(),
            phase = illumination.phase.toFloat(),
            angle = illumination.angle.toFloat(),
            azimuth = position.azimuth.toFloat(),
            altitude = position.altitude.toFloat(),
            distance = position.distance.toFloat(),
            parallacticAngle = position.parallacticAngle.toFloat(),
        )
    }

    companion object {
        private val TAG = MoonlightDataSource::class.java.simpleName
    }
}