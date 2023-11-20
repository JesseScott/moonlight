package tt.co.jesses.moonlight.android.view

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
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    val normalizedPhase = ((illuminationData.phase + 180f) / 360f).coerceIn(0.0, 1.0)

    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
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
            alpha = illuminationData.fraction.toFloat(),
            colorFilter = ColorFilter.lighting(
                multiply = Color.White,
                add = Color.Yellow,
            ),
            blendMode = BlendMode.SrcOver,
        )
    }
}