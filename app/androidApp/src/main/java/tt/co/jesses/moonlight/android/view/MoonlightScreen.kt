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
import tt.co.jesses.moonlight.android.domain.normalize

@Preview
@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    val normalizedPhase = ((illuminationData.phase + 180f) / 360f).normalize()
    val normalizedAngle = ((illuminationData.angle + 180f) / 360f).normalize()
    val normalizedAzimuth = ((illuminationData.azimuth + 180f) / 360f).normalize()
    Log.d("MoonlightScreen", "normalizedPhase: $normalizedPhase")
    Log.d("MoonlightScreen", "normalizedAngle: $normalizedAngle")
    Log.d("MoonlightScreen", "illuminationData: $illuminationData")

    val fraction = illuminationData.fraction
    val phase = illuminationData.phase
    val angle = illuminationData.angle
    val azimuth = illuminationData.azimuth.coerceAtMost(255f)
    val altitude = illuminationData.altitude
    val parallacticAngle = illuminationData.parallacticAngle
    val alpha = illuminationData.fraction.normalize()

    val rgbaAzimuth = Color(
        red = normalizedAzimuth,
        green = normalizedAzimuth,
        blue = normalizedAzimuth,
        alpha = alpha,
        colorSpace = ColorSpaces.Srgb,
    )
    val rgbPhase = Color(
        red = normalizedPhase,
        green = normalizedPhase,
        blue = normalizedPhase,
        alpha = alpha,
        colorSpace = ColorSpaces.Srgb,
    )
    val hue = Color.hsl(
        hue = angle,
        saturation = 1.0f,
        lightness = 0.5f,
        alpha = alpha,
        colorSpace = ColorSpaces.Srgb,
    )
    val modifier = Modifier.fillMaxHeight().fillMaxWidth()
    val brush = Brush.sweepGradient(
        0f to Color.White,
        0.25f to Color.LightGray,
        0.5f to hue,
        0.75f to Color.LightGray,
        1f to Color.White,
        center = Offset(0f, 0f),
    )
    val colorFilter = ColorFilter.lighting(
        multiply = Color.Yellow,
        add = Color.Blue,
    )
    val colorFilter2 = ColorFilter.tint(
        color = hue,
    )
    val blendMode = BlendMode.SrcOver

    Canvas(modifier = modifier) {
        drawRect(
            brush = brush,
            topLeft = Offset.Zero,
            size = size,
            alpha = alpha,
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}

