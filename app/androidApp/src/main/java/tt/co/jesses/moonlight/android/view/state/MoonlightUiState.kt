package tt.co.jesses.moonlight.android.view.state

import tt.co.jesses.moonlight.android.data.model.CreditData
import tt.co.jesses.moonlight.common.data.model.MoonData

data class MoonlightUiState(
    val illuminationData: MoonData = MoonData(),
    val creditData: CreditData = CreditData(),
    val isAnalyticsAccepted: Boolean = false,
    /** Whether the first-launch intro should show; null until the saved preferences have been read */
    val isIntroPending: Boolean? = null,
)
