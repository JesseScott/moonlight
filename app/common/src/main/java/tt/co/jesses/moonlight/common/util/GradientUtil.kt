package tt.co.jesses.moonlight.common.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object GradientUtil {
    private val silverColor = Color(0xFFC0C0C0)
    private val lsb = Color(0xFFCCE5FF)
    private val hsl = Color.hsl(
        hue = 0f,
        saturation = 0f,
        lightness = 0f,
        alpha = 1f,
    )

    fun generateHSLColor(): List<Color> {
        return listOf(hsl, silverColor, lsb)
    }
}

fun drawAngledGradient(degrees: Float, canvas: Canvas, colors: List<Int>) {
    val (width, height) = canvas.width.toFloat() to canvas.height.toFloat()
    val (x, y) = width to height
    val gamma = (degrees / 180f) * Math.PI
    val yComponent = cos(gamma)
    val xComponent = sin(gamma)
    val r = sqrt(x.pow(2) + y.pow(2)) / 2f
    val offset = android.graphics.PointF(x / 2f, y / 2f)
    val offset2 = android.graphics.PointF(xComponent.toFloat() * r, yComponent.toFloat() * r)

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
