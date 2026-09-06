package tt.co.jesses.moonlight.common.util

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PointF
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
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object GradientUtil {
    private val silverColor = Color(0xFFC0C0C0)
    private val lsb = Color(0xFFCCE5FF)

    fun generateHSLColor(
        moonData: MoonData? = null,
    ): List<Color> {
        val hsl = if (moonData != null) {
            Color.hsl(
                hue = moonData.phase,
                saturation = moonData.altitude,
                lightness = moonData.angle,
                alpha = moonData.fraction,
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

fun Modifier.angledGradientBackground(colors: List<Color>, degrees: Float) = this.drawBehind {
    val (x, y) = size
    val gamma = atan2(y, x)

    if (gamma == 0f || (gamma == (PI / 2).toFloat())) {
        return@drawBehind
    }

    val degreesNormalised = (degrees % 360).let { if (it < 0) it + 360 else it }
    val alpha = (degreesNormalised * PI / 180).toFloat()

    val gradientLength = when (alpha) {
        in 0f..gamma, in (2 * PI - gamma)..2 * PI -> {
            x / cos(alpha)
        }
        in gamma..(PI - gamma).toFloat() -> {
            y / sin(alpha)
        }
        in (PI - gamma)..(PI + gamma) -> {
            x / -cos(alpha)
        }
        in (PI + gamma)..(2 * PI - gamma) -> {
            y / -sin(alpha)
        }
        else -> hypot(x, y)
    }

    val centerOffsetX = cos(alpha) * gradientLength / 2
    val centerOffsetY = sin(alpha) * gradientLength / 2

    drawRect(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(center.x - centerOffsetX, center.y - centerOffsetY),
            end = Offset(center.x + centerOffsetX, center.y + centerOffsetY),
        ),
        size = size,
    )
}

fun drawAngledGradient(degrees: Float, canvas: Canvas, colors: List<Int>) {
    val (width, height) = canvas.width.toFloat() to canvas.height.toFloat()
    val (x, y) = width to height
    val gamma = (degrees / 180f) * Math.PI
    val yComponent = cos(gamma)
    val xComponent = sin(gamma)
    val r = sqrt(x.pow(2) + y.pow(2)) / 2f
    val offset = PointF(x / 2f, y / 2f)
    val offset2 = PointF(xComponent.toFloat() * r, yComponent.toFloat() * r)

    val gradient = LinearGradient(
        offset.x - offset2.x,
        offset.y - offset2.y,
        offset.x + offset2.x,
        offset.y + offset2.y,
        colors.toIntArray(),
        null,
        Shader.TileMode.CLAMP
    )

    val paint = Paint().apply {
        shader = gradient
    }

    canvas.drawRect(0f, 0f, width, height, paint)
}
