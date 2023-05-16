package tt.co.jesses.moonlight.data.repository

import org.shredzone.commons.suncalc.MoonIllumination
import tt.co.jesses.moonlight.data.model.MoonIlluminationData

/**
 * Class reponsible for accessing [org.shredzone.commons.suncalc] library
 */
class MoonlightDataSource {

    /**
     * Gets [MoonIllumination] from Suncalc and maps to [MoonIlluminationData]
     */
    fun getMoonIllumination(): MoonIlluminationData {
        val computedData = MoonIllumination.compute().execute()
        return MoonIlluminationData(
            fraction = computedData.fraction,
            phase = computedData.phase,
            angle = computedData.angle
        )
    }
}