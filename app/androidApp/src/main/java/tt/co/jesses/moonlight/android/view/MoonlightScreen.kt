package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import tt.co.jesses.moonlight.android.domain.EventNames
import tt.co.jesses.moonlight.android.domain.Logger
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.util.GradientUtil
import tt.co.jesses.moonlight.android.view.util.angledGradientBackground
import tt.co.jesses.moonlight.android.view.util.bounded
import kotlin.time.Duration.Companion.seconds

@Preview
@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val logger = Logger(LocalContext.current)
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    logger.logConsole("MoonlightScreen: $illuminationData")
    logger.logScreen(EventNames.Screen.MOONLIGHT_SCREEN)

    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val gradientModifier = Modifier
        .angledGradientBackground(
            colors = colorList,
            degrees = 270f,
        )
        .bounded()

    Canvas(modifier = gradientModifier) {}

    LaunchedEffect(Unit) {
        while(true) {
            delay(30.seconds)
            viewModel.getMoonIllumination()
        }
    }
}

