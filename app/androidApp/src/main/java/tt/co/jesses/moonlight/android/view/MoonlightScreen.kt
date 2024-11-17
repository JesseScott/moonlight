package tt.co.jesses.moonlight.android.view

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.util.GradientUtil
import tt.co.jesses.moonlight.android.view.util.angledGradientBackground
import tt.co.jesses.moonlight.android.view.util.bounded

@Preview
@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    Log.d("MoonlightScreen", "MoonlightScreen: $illuminationData")
    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val gradientModifier = Modifier
        .angledGradientBackground(
            colors = colorList,
            degrees = 270f,
        )
        .bounded()

    Canvas(modifier = gradientModifier) {}
}

