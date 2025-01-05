package tt.co.jesses.moonlight.android.view.state

import tt.co.jesses.moonlight.android.data.model.CreditData
import tt.co.jesses.moonlight.android.data.model.MoonData
import tt.co.jesses.moonlight.android.data.model.UserPreferences

data class MoonlightUiState(
    val illuminationData: MoonData = MoonData(),
    val creditData: CreditData = CreditData(),
    val preferencesData: UserPreferences = UserPreferences(),
)
