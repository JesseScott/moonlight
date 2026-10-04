package tt.co.jesses.moonlight.common.data.repository

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import tt.co.jesses.moonlight.common.data.model.MoonData
import tt.co.jesses.moonlight.common.util.GradientUtil

/**
 * The moon's current colour, for surfaces that live outside the app's screens (the widget and live wallpaper)
 * and so have no ViewModel or dependency injection.
 *
 * Location is only used if the app already has permission and a fix from the last day; otherwise the altitude is unknown
 * and the gradient assumes a mid-sky moon (see [GradientUtil.generateHSLColor]).
 */
class MoonColorSource(
    private val repository: MoonlightRepository,
    private val locationDataSource: LocationDataSource,
) {

    suspend fun moonData(): MoonData {
        val coordinates = locationDataSource.getCoordinates(
            allowFreshFix = false,
            maxFixAgeMs = LocationDataSource.BACKGROUND_MAX_FIX_AGE_MS,
        )
        return repository.getMoonIllumination(
            latitude = coordinates?.latitude,
            longitude = coordinates?.longitude,
        )
    }

    /** The gradient stops, bottom to top, as ARGB ints */
    suspend fun argbColors(): List<Int> =
        GradientUtil.generateHSLColor(moonData()).map { it.toArgb() }

    companion object {
        fun create(context: Context): MoonColorSource = MoonColorSource(
            repository = MoonlightRepository(MoonlightDataSource()),
            locationDataSource = LocationDataSource(context.applicationContext),
        )
    }
}
