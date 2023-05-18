package tt.co.jesses.moonlight.android.view

import tt.co.jesses.moonlight.android.data.model.MoonIlluminationData

data class MoonlightUiState(
    val isLoading: Boolean = false,
    val illuminationData: MoonIlluminationData = MoonIlluminationData()
)
