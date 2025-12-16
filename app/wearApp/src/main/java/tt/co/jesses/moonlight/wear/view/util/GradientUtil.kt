package tt.co.jesses.moonlight.wear.view.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import tt.co.jesses.moonlight.common.data.model.MoonData

object GradientUtil {

    private val silverColor = Color(0xFFC0C0C0)
    private val lsb = Color(0xFFCCE5FF)

    fun generateHSLColor(
        moonData: MoonData,
    ): List<Color> {
        val hsl = Color.hsl(
            hue = moonData.phase,
            saturation = moonData.altitude,
            lightness = moonData.angle,
            alpha = moonData.fraction,
            colorSpace = ColorSpaces.Srgb,
        )
        return listOf(hsl, silverColor, lsb)
    }
}
