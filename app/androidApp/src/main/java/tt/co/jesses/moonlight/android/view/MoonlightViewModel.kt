package tt.co.jesses.moonlight.android.view

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tt.co.jesses.moonlight.android.data.model.MoonData
import tt.co.jesses.moonlight.android.data.repository.MoonlightRepository
import tt.co.jesses.moonlight.android.domain.normalize
import javax.inject.Inject

@HiltViewModel
class MoonlightViewModel @Inject constructor(
    private val repository: MoonlightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoonlightUiState())
    val uiState: StateFlow<MoonlightUiState> = _uiState.asStateFlow()

    init {
        getMoonIllumination()
    }

    private fun getMoonIllumination() {
        val illuminationData = repository.getMoonIllumination()
        val normalizedData = normalizeData(illuminationData)
        Log.d(TAG, "Normalized MoonData: $normalizedData")
        _uiState.value = MoonlightUiState(illuminationData = normalizedData)
    }

    private fun normalizeData(moonData: MoonData): MoonData {
        return moonData.copy(
            fraction = moonData.fraction, // 0 to 1, no need to transform
            phase = moonData.phase.normalize(NEGATIVE_ONE_EIGHTY, ONE_EIGHTY), // -180 to 180
            angle = moonData.angle.normalize(NEGATIVE_ONE_EIGHTY, ONE_EIGHTY), // ??
            azimuth = moonData.azimuth.normalize(ZERO, ONE_EIGHTY), // 0 to 90
            altitude = moonData.altitude.normalize(NEGATIVE_THREE_SIXTY, THREE_SIXTY), // -360 to 360
            distance = moonData.distance, // absolute distance, no need to transform
            parallacticAngle = moonData.parallacticAngle.normalize(NEGATIVE_ONE_EIGHTY, ONE_EIGHTY), // ??
        )
    }

    companion object {
        private val TAG = MoonlightViewModel::class.java.simpleName

        private const val NEGATIVE_THREE_SIXTY = -360f
        private const val NEGATIVE_ONE_EIGHTY = -180f
        private const val ZERO = 0f
        private const val NINETY = 90f
        private const val ONE_EIGHTY = 180f
        private const val THREE_SIXTY = 360f
    }
}