package tt.co.jesses.moonlight.android.view.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces

object GradientUtil {

    private val silverColor = Color(0xFFC0C0C0)
    private val lsb = Color(0xFFCCE5FF)

    fun generateHSLColor(
        hue: Float,
        saturation: Float,
        lightness: Float,
        alpha: Float,
    ): List<Color> {
        val hsl = Color.hsl(
            hue = hue,
            saturation = saturation,
            lightness = lightness,
            alpha = alpha,
            colorSpace = ColorSpaces.Srgb,
        )
        return listOf(hsl, silverColor, lsb)
    }


}