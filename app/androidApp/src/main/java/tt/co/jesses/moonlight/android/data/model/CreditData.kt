package tt.co.jesses.moonlight.android.data.model

import androidx.annotation.StringRes
import tt.co.jesses.moonlight.android.R

/**
 * Data representing the [AboutScreen]
 */
data class CreditData(
    @StringRes val creditTitle: Int = R.string.title_about,
    @StringRes val madeByFull: Int = R.string.credits_made_by_full,
    @StringRes val madeByKey: Int = R.string.credits_made_by_key,
    @StringRes val madeByValue: Int = R.string.credits_made_by_value,
    @StringRes val inspiredByKey: Int = R.string.credits_inspired_by_key,
    @StringRes val inspiredByValue: Int = R.string.credits_inspired_by_value,
    @StringRes val sourceFull: Int = R.string.credits_source_full,
    @StringRes val sourceKey: Int = R.string.credits_source_key,
    @StringRes val sourceValue: Int = R.string.credits_source_value,
    @StringRes val siteKey: Int = R.string.credits_site_key,
    @StringRes val siteValue: Int = R.string.credits_site_value,
)
