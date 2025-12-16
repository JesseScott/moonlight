package tt.co.jesses.moonlight.wear.view.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun Modifier.angledGradientBackground(colors: List<Color>, degrees: Float) = this.then(
    Modifier.drawBehind {
        val (x, y) = size
        val gamma = (degrees / 180f) * PI
        val yComponent = cos(gamma)
        val xComponent = sin(gamma)
        val r = sqrt(x.pow(2) + y.pow(2)) / 2f
        val offset = Offset(x = x / 2f, y = y / 2f)
        val offset2 = Offset(x = xComponent.toFloat() * r, y = yComponent.toFloat() * r)

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = offset - offset2,
                end = offset + offset2,
            ),
            size = size,
        )
    }
)
