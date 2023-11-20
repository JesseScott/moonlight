package tt.co.jesses.moonlight.android.data.model

import androidx.annotation.StringRes
import tt.co.jesses.moonlight.android.R

data class CreditData(
    @StringRes val creditTitle: Int = R.string.credits_title,
    @StringRes val madeBy: Int = R.string.credits_made_by,
    @StringRes val madeWith: Int = R.string.credits_made_with,
)
