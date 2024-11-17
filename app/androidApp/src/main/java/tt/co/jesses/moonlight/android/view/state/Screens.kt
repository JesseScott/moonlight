package tt.co.jesses.moonlight.android.view.state

import androidx.annotation.StringRes
import tt.co.jesses.moonlight.android.R

enum class Screens(val route: String, @StringRes val resourceId: Int) {
    Moon(route = "moon", R.string.title_moon),
    Data(route = "data", R.string.title_data),
    About(route = "about", R.string.title_about);
}
