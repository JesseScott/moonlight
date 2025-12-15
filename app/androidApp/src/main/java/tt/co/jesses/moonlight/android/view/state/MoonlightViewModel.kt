package tt.co.jesses.moonlight.android.view.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.android.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.android.data.repository.MoonlightRepository
import tt.co.jesses.moonlight.android.data.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MoonlightViewModel @Inject constructor(
    private val moonlightRepository: MoonlightRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoonlightUiState())
    val uiState: StateFlow<MoonlightUiState> = _uiState.asStateFlow()

    val refreshCycle: Duration
        get() {
            return 30.seconds // todo figure out Debug flag
        }

    init {
        getMoonIllumination()
        shouldShowAnalyticsModal()
    }

    fun getMoonIllumination() {
        viewModelScope.launch {
            val illuminationData = moonlightRepository.getMoonIllumination()
            _uiState.value = _uiState.value.copy(illuminationData = illuminationData)
        }
    }

    private fun shouldShowAnalyticsModal() {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.fetchInitialPreferences()
            val analyticsAcceptance = AnalyticsAcceptance.values()[userPreferences.analyticsAcceptance]
            _uiState.value = _uiState.value.copy(isAnalyticsPreferencePending = analyticsAcceptance == AnalyticsAcceptance.UNSET)
        }
    }

    fun updateAnalyticsAcceptance(analyticsAcceptance: AnalyticsAcceptance) {
        viewModelScope.launch {
            userPreferencesRepository.updateAnalyticsAcceptance(analyticsAcceptance.ordinal)
        }
    }
}