package tt.co.jesses.moonlight.android.data.repository

import tt.co.jesses.moonlight.android.data.model.MoonIlluminationData
import javax.inject.Inject

/**
 * Top level class responsible for gating access to Moon APIs
 */
class MoonlightRepository @Inject constructor(
    private val dataSource: MoonlightDataSource,
) {

    /**
     *
     */
    fun getMoonIllumination(): MoonIlluminationData {
        return dataSource.getMoonIllumination()
    }
}