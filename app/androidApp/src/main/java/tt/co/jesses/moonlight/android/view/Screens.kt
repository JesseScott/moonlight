package tt.co.jesses.moonlight.android.view

import androidx.annotation.StringRes
import tt.co.jesses.moonlight.android.R

sealed class Screens(val route: String, @StringRes val resourceId: Int) {
    object Moon : Screens(MOON_ROUTE, R.string.title_moon)
    object Data : Screens(DATA_ROUTE, R.string.title_data)
    object Credits : Screens(CREDITS_ROUTE, R.string.title_credits)

    companion object {
        private const val MOON_ROUTE = "moon"
        private const val DATA_ROUTE = "data"
        private const val CREDITS_ROUTE = "credits"

        fun fromRoute(route: String?): Screens = when (route?.substringBefore("/")) {
            Moon.route -> Moon
            Data.route -> Data
            Credits.route -> Credits
            else -> Moon
        }
    }
}
