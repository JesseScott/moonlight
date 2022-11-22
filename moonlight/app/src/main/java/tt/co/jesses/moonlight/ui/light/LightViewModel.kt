package tt.co.jesses.moonlight.ui.light

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.shredzone.commons.suncalc.MoonIllumination
import tt.co.jesses.moonlight.data.repository.MoonlightRepository

class LightViewModel(
    private val repository: MoonlightRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(LightViewState())
    val uiState: StateFlow<LightViewState> = _uiState.asStateFlow()

    fun getMoonIllumination() {
        val illumination = repository.getMoonIllumination()
        _uiState.value = LightViewState(light = illumination.fraction)
    }
}