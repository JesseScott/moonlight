package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel,
    onNavigate: (String) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val fraction = uiState.value.illuminationData.fraction
    val padding = 16.dp
    val normalizedPhase = ((uiState.value.illuminationData.phase + 180f) / 360f).coerceIn(0.0, 1.0)

    Column(
        modifier = Modifier
            .padding(start = padding, top = padding)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onNavigate("data")
                    }
                )
            },
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Moonlight")
        Spacer(Modifier.padding(padding))
        Text(text = "Fraction: $fraction")
        Text(text = "Phase: ${uiState.value.illuminationData.phase}")
        Text(text = "nPhase: $normalizedPhase")
        Text(text = "Angle: ${uiState.value.illuminationData.angle}")
        Text(text = "Azimuth: ${uiState.value.illuminationData.azimuth}")
        Text(text = "Altitude: ${uiState.value.illuminationData.altitude}")
        Text(text = "Distance: ${uiState.value.illuminationData.distance}")
        Text(text = "Parallactic Angle: ${uiState.value.illuminationData.parallacticAngle}")
    }
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        drawRect(
            brush = Brush.sweepGradient(
                0f to Color.White,
                0.25f to Color.LightGray,
                0.5f to Color.DarkGray,
                0.75f to Color.LightGray,
                1f to Color.White
            ),
            topLeft = Offset.Zero,
            size = size,
            alpha = fraction.toFloat(),
            colorFilter = ColorFilter.lighting(
                multiply = Color.White,
                add = Color.Yellow,
            ),
            blendMode = BlendMode.SrcOver,
        )
    }
}