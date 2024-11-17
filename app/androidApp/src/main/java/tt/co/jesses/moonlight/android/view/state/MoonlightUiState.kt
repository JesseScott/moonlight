package tt.co.jesses.moonlight.android.view.state

import tt.co.jesses.moonlight.android.data.model.CreditData
import tt.co.jesses.moonlight.android.data.model.MoonData

data class MoonlightUiState(
    val illuminationData: MoonData = MoonData(),
    val creditData: CreditData = CreditData(),
)
