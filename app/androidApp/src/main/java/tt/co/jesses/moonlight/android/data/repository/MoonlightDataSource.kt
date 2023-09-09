package tt.co.jesses.moonlight.android.data.repository

import org.shredzone.commons.suncalc.MoonIllumination
import tt.co.jesses.moonlight.android.data.model.MoonIlluminationData
import javax.inject.Inject

/**
 * Class responsible for accessing [org.shredzone.commons.suncalc] library
 */
class MoonlightDataSource @Inject constructor() {

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