package tt.co.jesses.moonlight.android.view

import androidx.annotation.StringRes
import tt.co.jesses.moonlight.android.R

sealed class Screens(val route: String, @StringRes val resourceId: Int) {
    object Moon : Screens(MOON_ROUTE, R.string.title_moon)
    object Data : Screens(DATA_ROUTE, R.string.title_data)
    object About : Screens(ABOUT_ROUTE, R.string.title_about)

    companion object {
        private const val MOON_ROUTE = "moon"
        private const val DATA_ROUTE = "data"
        private const val ABOUT_ROUTE = "about"

        fun fromRoute(route: String?): Screens = when (route?.substringBefore("/")) {
            Moon.route -> Moon
            Data.route -> Data
            About.route -> About
            else -> Moon
        }
    }
}
