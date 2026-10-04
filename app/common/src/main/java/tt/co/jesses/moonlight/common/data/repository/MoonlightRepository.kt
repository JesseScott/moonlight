package tt.co.jesses.moonlight.common.data.repository

import tt.co.jesses.moonlight.common.data.model.MoonData
import javax.inject.Inject

/**
 * Top level class responsible for gating access to Moon APIs
 */
class MoonlightRepository @Inject constructor(
    private val dataSource: MoonlightDataSource,
) {

    /**
     * Phase, angle, azimuth, altitude and parallactic angle are in degrees, distance in km and fraction is 0 to 1
     */
    fun getMoonIllumination(latitude: Double? = null, longitude: Double? = null): MoonData {
        return dataSource.getMoonIllumination(latitude, longitude)
    }
}
