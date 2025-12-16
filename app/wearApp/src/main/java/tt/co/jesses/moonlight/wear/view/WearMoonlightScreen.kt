package tt.co.jesses.moonlight.wear.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import tt.co.jesses.moonlight.common.data.model.MoonData
import tt.co.jesses.moonlight.wear.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.wear.view.util.GradientUtil
import tt.co.jesses.moonlight.wear.view.util.angledGradientBackground

@Composable
fun WearMoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val illuminationData = uiState.illuminationData

    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val gradientModifier = Modifier
        .fillMaxSize()
        .angledGradientBackground(
            colors = colorList,
            degrees = 270f,
        )

    Box(modifier = gradientModifier) {
        // We can add text overlay or complications here if needed,
        // but for now just the gradient as requested.
    }

    LaunchedEffect(Unit) {
        while(isActive) {
            delay(viewModel.refreshCycle)
            viewModel.getMoonIllumination()
        }
    }
}
