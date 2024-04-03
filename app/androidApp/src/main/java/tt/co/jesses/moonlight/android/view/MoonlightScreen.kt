package tt.co.jesses.moonlight.android.view

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    val normalizedPhase = ((illuminationData.phase + 180f) / 360f).coerceIn(0.0, 1.0).toFloat()
    val normalizedAngle = ((illuminationData.angle + 180f) / 360f).coerceIn(0.0, 1.0).toFloat()
    val normalizedAzimuth = ((illuminationData.azimuth + 180f) / 360f).coerceIn(0.0, 1.0).toFloat()
    Log.d("MoonlightScreen", "normalizedPhase: $normalizedPhase")
    Log.d("MoonlightScreen", "normalizedAngle: $normalizedAngle")
    Log.d("MoonlightScreen", "illuminationData: $illuminationData")

    val fraction = illuminationData.fraction.toFloat()
    val phase = illuminationData.phase.toFloat()
    val angle = illuminationData.angle.toFloat()
    val azimuth = illuminationData.azimuth.toFloat().coerceAtMost(255f)
    val altitude = illuminationData.altitude.toFloat()
    val parallacticAngle = illuminationData.parallacticAngle.toFloat()

    val rgbaAzimuth = Color(
        red = normalizedAzimuth,
        green = normalizedAzimuth,
        blue = normalizedAzimuth,
        alpha = 1.0f,
        colorSpace = ColorSpaces.Srgb,
    )
    val rgbPhase = Color(
        red = normalizedPhase,
        green = normalizedPhase,
        blue = normalizedPhase,
        alpha = 1.0f,
        colorSpace = ColorSpaces.Srgb,
    )
    val h = Color.hsl(
        hue = angle,
        saturation = 1.0f,
        lightness = 0.5f,
        alpha = 1.0f,
        colorSpace = ColorSpaces.Srgb,
    )
    val modifier = Modifier.fillMaxHeight().fillMaxWidth()
    val brush = Brush.sweepGradient(
        0f to Color.White,
        0.25f to Color.LightGray,
        0.5f to h,
        0.75f to Color.LightGray,
        1f to Color.White,
        center = Offset(0f, 0f),
    )
    val colorFilter = ColorFilter.lighting(
        multiply = Color.Yellow,
        add = Color.Blue,
    )
    val colorFilter2 = ColorFilter.tint(
        color = h,
    )
    val blendMode = BlendMode.SrcOver

    Canvas(modifier = modifier) {
        drawRect(
            brush = brush,
            topLeft = Offset.Zero,
            size = size,
            alpha = 0.75f, //illuminationData.fraction.toFloat(),
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}