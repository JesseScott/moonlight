package tt.co.jesses.moonlight.android.view.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.android.data.repository.MoonlightRepository

import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MoonlightViewModel @Inject constructor(
    private val repository: MoonlightRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoonlightUiState())
    val uiState: StateFlow<MoonlightUiState> = _uiState.asStateFlow()

    val refreshCycle: Duration
        get() {
            return 30.seconds // todo figure out Debug flag
        }

    init {
        getMoonIllumination()
    }

    fun getMoonIllumination() {
        viewModelScope.launch {
            val illuminationData = repository.getMoonIllumination()
            _uiState.value = _uiState.value.copy(illuminationData = illuminationData)
        }
    }
}