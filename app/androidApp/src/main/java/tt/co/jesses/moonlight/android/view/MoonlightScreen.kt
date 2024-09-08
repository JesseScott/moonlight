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
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    Log.d("MoonlightScreen", "MoonlightScreen: $illuminationData")
    val fraction = illuminationData.fraction
    val phase = illuminationData.phase
    val angle = illuminationData.angle
    val azimuth = illuminationData.azimuth
    val altitude = illuminationData.altitude
    val parallacticAngle = illuminationData.parallacticAngle
    val alpha = illuminationData.fraction


    val hue = Color.hsl(
        hue = altitude,
        saturation = phase,
        lightness = azimuth,
        alpha = alpha,
        colorSpace = ColorSpaces.Srgb,
    )
    val grayscale = Color.hsl(
        hue = 0f,
        saturation = 0f,
        lightness = altitude,
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
        0.5f to hue,
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
            //colorFilter = colorFilter,
            blendMode = blendMode,
        )
    }
}

