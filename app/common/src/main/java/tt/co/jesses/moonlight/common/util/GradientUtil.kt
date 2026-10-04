package tt.co.jesses.moonlight.common.util

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import tt.co.jesses.moonlight.common.data.model.MoonData
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * The colour of the moon, as HSL + alpha. Hue is in degrees (0..360), the rest are 0..1.
 */
data class MoonHsl(
    val hue: Float,
    val saturation: Float,
    val lightness: Float,
    val alpha: Float,
)

object GradientUtil {
    private val silverColor = Color(0xFFC0C0C0)
    private val lsb = Color(0xFFCCE5FF)

    private const val HUE_NEW_MOON = 230f // cool blue
    private const val HUE_FULL_MOON = 45f // warm gold
    private const val HUE_WAX_WANE_TILT = 25f // waxing leans one way, waning the other

    private const val SATURATION_HORIZON = 0.25f
    private const val SATURATION_ZENITH = 0.65f

    private const val LIGHTNESS_NEW_MOON = 0.35f
    private const val LIGHTNESS_FULL_MOON = 0.75f

    private const val ALPHA_NEW_MOON = 0.25f

    private const val ZENITH_DEGREES = 90f
    private const val TWILIGHT_DEGREES = 12f // how far below the horizon the muted wash takes to fully fade in
    private const val NIGHT_SATURATION_DROP = 0.6f
    private const val NIGHT_LIGHTNESS_DROP = 0.5f

    /** Used when there is no location, and so no real altitude */
    const val NEUTRAL_ALTITUDE_DEGREES = 45f

    /**
     * Maps the moon to a colour.
     *
     * - hue follows the phase: new moon is [HUE_NEW_MOON], full moon is [HUE_FULL_MOON], and a waxing moon is
     *   tilted one way and a waning moon the other (zero tilt at new and full moon, so the cycle has no jump)
     * - saturation grows with altitude: [SATURATION_HORIZON] at the horizon to [SATURATION_ZENITH] at the zenith
     * - lightness and alpha grow with the illuminated [fraction]
     * - below the horizon the colour fades to a darker, more muted wash
     *
     * @param phase degrees, -180 (new, waxing) through 0 (full) to 180 (waning, new)
     * @param fraction illuminated fraction, 0 (new) to 1 (full)
     * @param altitude degrees above the horizon, -90 to 90
     */
    fun moonHsl(phase: Float, fraction: Float, altitude: Float): MoonHsl {
        val illumination = fraction.coerceIn(0f, 1f)
        val up = (altitude / ZENITH_DEGREES).coerceIn(0f, 1f)
        val night = (-altitude / TWILIGHT_DEGREES).coerceIn(0f, 1f)

        val tilt = HUE_WAX_WANE_TILT * sin(Math.toRadians(phase.toDouble())).toFloat()
        val hue = (HUE_NEW_MOON + (HUE_FULL_MOON - HUE_NEW_MOON) * illumination + tilt).mod(360f)

        val saturation = (SATURATION_HORIZON + (SATURATION_ZENITH - SATURATION_HORIZON) * up) *
            (1f - NIGHT_SATURATION_DROP * night)
        val lightness = (LIGHTNESS_NEW_MOON + (LIGHTNESS_FULL_MOON - LIGHTNESS_NEW_MOON) * illumination) *
            (1f - NIGHT_LIGHTNESS_DROP * night)
        val alpha = ALPHA_NEW_MOON + (1f - ALPHA_NEW_MOON) * illumination

        return MoonHsl(hue = hue, saturation = saturation, lightness = lightness, alpha = alpha)
    }

    fun generateHSLColor(
        moonData: MoonData? = null,
    ): List<Color> {
        val hsl = if (moonData != null) {
            // Without a location the altitude is unknown, so assume the moon is part way up the sky
            val altitude = if (moonData.hasPosition) moonData.altitude else NEUTRAL_ALTITUDE_DEGREES
            val moon = moonHsl(moonData.phase, moonData.fraction, altitude)
            Color.hsl(
                hue = moon.hue,
                saturation = moon.saturation,
                lightness = moon.lightness,
                alpha = moon.alpha,
                colorSpace = ColorSpaces.Srgb,
            )
        } else {
            Color.hsl(
                hue = 0f,
                saturation = 0f,
                lightness = 0f,
                alpha = 1f,
            )
        }
        return listOf(hsl, silverColor, lsb)
    }
}

/**
 * The start and end of a linear gradient across a [width] x [height] area, in pixels.
 */
data class GradientLine(val startX: Float, val startY: Float, val endX: Float, val endY: Float)

/**
 * Where a gradient at [degrees] starts and ends so that it just covers a [width] x [height] area.
 * Like CSS gradient angles, but measured counter-clockwise from the +x axis with y pointing down:
 * 0 runs left to right, 90 top to bottom, 180 right to left and 270 bottom to top.
 *
 * Shared by the Compose background and the canvas version (used by the widget and live wallpaper) so they
 * always agree. Returns null for an area with no height (or width), which can not be drawn.
 */
fun angledGradientLine(width: Float, height: Float, degrees: Float): GradientLine? {
    val gamma = atan2(height, width)

    if (gamma == 0f || (gamma == (PI / 2).toFloat())) {
        return null
    }

    val degreesNormalised = (degrees % 360).let { if (it < 0) it + 360 else it }
    val alpha = (degreesNormalised * PI / 180).toFloat()

    val gradientLength = when (alpha) {
        in 0f..gamma, in (2 * PI - gamma)..2 * PI -> {
            width / cos(alpha)
        }
        in gamma..(PI - gamma).toFloat() -> {
            height / sin(alpha)
        }
        in (PI - gamma)..(PI + gamma) -> {
            width / -cos(alpha)
        }
        in (PI + gamma)..(2 * PI - gamma) -> {
            height / -sin(alpha)
        }
        else -> hypot(width, height)
    }

    val centerX = width / 2
    val centerY = height / 2
    val centerOffsetX = cos(alpha) * gradientLength / 2
    val centerOffsetY = sin(alpha) * gradientLength / 2

    return GradientLine(
        startX = centerX - centerOffsetX,
        startY = centerY - centerOffsetY,
        endX = centerX + centerOffsetX,
        endY = centerY + centerOffsetY,
    )
}

fun Modifier.angledGradientBackground(colors: List<Color>, degrees: Float) = this.drawBehind {
    val line = angledGradientLine(size.width, size.height, degrees) ?: return@drawBehind

    drawRect(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(line.startX, line.startY),
            end = Offset(line.endX, line.endY),
        ),
        size = size,
    )
}

fun drawAngledGradient(degrees: Float, canvas: Canvas, colors: List<Int>) {
    val width = canvas.width.toFloat()
    val height = canvas.height.toFloat()
    val line = angledGradientLine(width, height, degrees) ?: return

    val paint = Paint().apply {
        shader = LinearGradient(
            line.startX,
            line.startY,
            line.endX,
            line.endY,
            colors.toIntArray(),
            null,
            Shader.TileMode.CLAMP
        )
    }

    canvas.drawRect(0f, 0f, width, height, paint)
}
