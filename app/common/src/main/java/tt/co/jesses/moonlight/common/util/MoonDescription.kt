package tt.co.jesses.moonlight.common.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import tt.co.jesses.moonlight.common.R
import tt.co.jesses.moonlight.common.data.model.MoonData
import kotlin.math.roundToInt

enum class MoonPhaseName { UNKNOWN, NEW, WAXING, FULL, WANING }

private const val NEW_MOON_FRACTION = 0.03f
private const val FULL_MOON_FRACTION = 0.97f

/**
 * A name for the phase, for people who can not see the colours: new and full moon when (almost) none or all of
 * the moon is lit, otherwise waxing (phase below 0) or waning (phase above 0).
 */
fun moonPhaseName(phase: Float, fraction: Float): MoonPhaseName = when {
    // The placeholder before the first real data arrives. No real moon has phase 0 (full) with none of it lit.
    phase == 0f && fraction == 0f -> MoonPhaseName.UNKNOWN
    fraction <= NEW_MOON_FRACTION -> MoonPhaseName.NEW
    fraction >= FULL_MOON_FRACTION -> MoonPhaseName.FULL
    phase < 0f -> MoonPhaseName.WAXING
    else -> MoonPhaseName.WANING
}

/** The percentage of the moon that is lit, 0 to 100 */
fun moonPercentLit(fraction: Float): Int = (fraction.coerceIn(0f, 1f) * 100).roundToInt()

/**
 * What a screen reader says for the gradient screens, which have no text of their own,
 * for example "Waning moon, 34% lit".
 */
@Composable
fun moonDescription(moonData: MoonData): String {
    val percent = moonPercentLit(moonData.fraction)
    return when (moonPhaseName(moonData.phase, moonData.fraction)) {
        MoonPhaseName.UNKNOWN -> stringResource(R.string.moon_description_loading)
        MoonPhaseName.NEW -> stringResource(R.string.moon_description_new)
        MoonPhaseName.FULL -> stringResource(R.string.moon_description_full)
        MoonPhaseName.WAXING -> stringResource(R.string.moon_description_waxing, percent)
        MoonPhaseName.WANING -> stringResource(R.string.moon_description_waning, percent)
    }
}
