package tt.co.jesses.moonlight.android.data.repository

import tt.co.jesses.moonlight.android.data.model.MoonIlluminationData

/**
 * Top level class responsible for gating access to Moon APIs
 */
class MoonlightRepository(
    private val dataSource: MoonlightDataSource,
) {

    /**
     *
     */
    fun getMoonIllumination(): MoonIlluminationData {
        return dataSource.getMoonIllumination()
    }
}