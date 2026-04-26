package tt.co.jesses.moonlight.android.view.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.common.data.repository.MoonlightRepository
import tt.co.jesses.moonlight.common.data.repository.UserPreferencesRepository
import tt.co.jesses.moonlight.android.BuildConfig
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

    val hasSwiped = userPreferencesRepository.hasSwiped

    val refreshCycle: Duration
        get() {
            return if (BuildConfig.DEBUG) {
                5.seconds
            } else {
                30.seconds
            }
        }

    init {
        getMoonIllumination()
        observeAnalyticsAcceptance()
    }

    fun getMoonIllumination() {
        viewModelScope.launch {
            val illuminationData = moonlightRepository.getMoonIllumination()
            _uiState.update { it.copy(illuminationData = illuminationData) }
        }
    }

    fun setHasSwiped(hasSwiped: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setHasSwiped(hasSwiped)
        }
    }

    private fun observeAnalyticsAcceptance() {
        viewModelScope.launch {
            userPreferencesRepository.analyticsAcceptance.collect { acceptance ->
                _uiState.update {
                    it.copy(isAnalyticsPreferencePending = acceptance == AnalyticsAcceptance.UNSET)
                }
            }
        }
    }

    fun updateAnalyticsAcceptance(analyticsAcceptance: AnalyticsAcceptance) {
        viewModelScope.launch {
            userPreferencesRepository.updateAnalyticsAcceptance(analyticsAcceptance.ordinal)
        }
    }
}
