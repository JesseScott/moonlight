package tt.co.jesses.moonlight.wear.view.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.common.data.model.MoonData
import tt.co.jesses.moonlight.common.data.repository.MoonlightRepository
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class MoonlightUiState(
    val illuminationData: MoonData = MoonData(),
)

@HiltViewModel
class MoonlightViewModel @Inject constructor(
    private val moonlightRepository: MoonlightRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoonlightUiState())
    val uiState: StateFlow<MoonlightUiState> = _uiState.asStateFlow()

    val refreshCycle: Duration
        get() = 30.seconds

    init {
        getMoonIllumination()
    }

    fun getMoonIllumination() {
        viewModelScope.launch {
            val illuminationData = moonlightRepository.getMoonIllumination()
            _uiState.value = _uiState.value.copy(illuminationData = illuminationData)
        }
    }
}
