package tt.co.jesses.moonlight.android.data.repository

import tt.co.jesses.moonlight.android.data.model.MoonData
import tt.co.jesses.moonlight.android.domain.normalize
import javax.inject.Inject

/**
 * Top level class responsible for gating access to Moon APIs
 */
class MoonlightRepository @Inject constructor(
    private val dataSource: MoonlightDataSource,
) {

    fun getMoonIllumination(): MoonData {
        return normalizeData(dataSource.getMoonIllumination())
    }

    private fun normalizeData(moonData: MoonData): MoonData {
        return moonData.copy(
            fraction = moonData.fraction, // 0 to 1, no need to transform
            phase = moonData.phase.normalize(NEGATIVE_ONE_EIGHTY, ONE_EIGHTY), // -180 to 180
            angle = moonData.angle.normalize(NEGATIVE_ONE_EIGHTY, ONE_EIGHTY), // ??
            azimuth = moonData.azimuth.normalize(ZERO, NINETY), // 0 to 90
            altitude = moonData.altitude.normalize(NEGATIVE_THREE_SIXTY, THREE_SIXTY), // -360 to 360
            distance = moonData.distance, // absolute distance, no need to transform
            parallacticAngle = moonData.parallacticAngle.normalize(NEGATIVE_ONE_EIGHTY, ONE_EIGHTY), // ??
        )
    }

    companion object {
        private const val NEGATIVE_THREE_SIXTY = -360f
        private const val NEGATIVE_ONE_EIGHTY = -180f
        private const val ZERO = 0f
        private const val NINETY = 90f
        private const val ONE_EIGHTY = 180f
        private const val THREE_SIXTY = 360f
    }
}