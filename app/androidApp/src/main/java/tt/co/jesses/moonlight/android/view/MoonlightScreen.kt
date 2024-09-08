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
import tt.co.jesses.moonlight.android.domain.radians

@Preview
@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    Log.d("MoonlightScreen", "illuminationData: $illuminationData")

    val fraction = illuminationData.fraction
    val phase = illuminationData.phase
    val angle = illuminationData.angle.radians()
    val azimuth = illuminationData.azimuth.coerceAtMost(255f)
    val altitude = illuminationData.altitude
    val parallacticAngle = illuminationData.parallacticAngle
    val alpha = 0.8f //illuminationData.fraction.normalize()

    val normalizedPhase = ((phase + 180f) / 360f).normalize()
    val normalizedAngle = ((angle + 180f) / 360f).normalize()
    val normalizedAzimuth = ((azimuth + 180f) / 360f).normalize()
    val normalizedAltitude = ((altitude + 180f) / 360f).normalize()

    val hue = Color.hsl(
        hue = normalizedAngle,
        saturation = normalizedPhase,
        lightness = normalizedAzimuth,
        alpha = alpha,
        colorSpace = ColorSpaces.Srgb,
    )
    val grayscale = Color.hsl(
        hue = 0f,
        saturation = 0f,
        lightness = normalizedAltitude,
        alpha = alpha,
        colorSpace = ColorSpaces.Srgb,
    )
    val silverColor = Color(0xFFC0C0C0)
    val silverBlueColor = Color(0xFF2596BE)
    val lsb = Color(0xFFCCE5FF)
    val blue = Color.Blue

    val offset = Offset.Unspecified // Offset(0f, 0f)
    val brush = Brush.sweepGradient(
        0f to grayscale,
        //0.05f to Color.DarkGray,
        //0.1f to Color.LightGray,
        0.5f to silverBlueColor,
        //0.9f to Color.LightGray,
        //0.95f to Color.DarkGray,
        1f to grayscale,
        center = offset,
    )

    val colorFilter = ColorFilter.lighting(
        multiply = hue,
        add = lsb,
    )
    val colorFilter2 = ColorFilter.tint(
        color = lsb,
        blendMode = BlendMode.Src,
    )

    val blendMode = BlendMode.SrcOver
    val modifier = Modifier.fillMaxHeight().fillMaxWidth()
    Canvas(modifier = modifier) {
        drawRect(
            brush = brush,
            topLeft = Offset.Zero,
            size = size,
            alpha = alpha,
            colorFilter = colorFilter2,
            blendMode = blendMode,
        )
    }
}

