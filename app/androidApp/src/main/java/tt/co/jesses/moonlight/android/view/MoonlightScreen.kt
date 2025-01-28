package tt.co.jesses.moonlight.android.view

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.android.app.MainActivity
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
    val shouldShowAnalyticsModal = viewModel.uiState.collectAsState().value.shouldShowAnalyticsModal

    val activity = LocalContext.current as Activity
    val logger = (activity as? MainActivity)?.logger
    logger?.logConsole("MoonlightScreen: $illuminationData")
    logger?.logConsole("MoonlightScreen 1: $shouldShowAnalyticsModal")
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }

    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val gradientModifier = Modifier
        .angledGradientBackground(
            colors = colorList,
            degrees = 270f,
        )
        .bounded()

    Canvas(modifier = gradientModifier) {}

    LaunchedEffect(Unit) {
        while(isActive) {
            delay(viewModel.refreshCycle)
            viewModel.getMoonIllumination()
            logger?.logConsole("MoonlightScreen 2: ${viewModel.uiState.value.shouldShowAnalyticsModal}")
            if (viewModel.uiState.value.shouldShowAnalyticsModal) {
                logger?.logConsole("MoonlightScreen 4: ${viewModel.uiState.value.shouldShowAnalyticsModal}")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Analytics are not enabled. Please enable them to support the app - no PII is collected, analytics are simply for app improvement.",
                        actionLabel = "supportAction"
                    ).also {
                        logger?.logConsole("showing analytics modal")
                    }
                }
            }
        }
    }
}

