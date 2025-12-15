package tt.co.jesses.moonlight.android.view

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import tt.co.jesses.moonlight.android.app.MainActivity
import tt.co.jesses.moonlight.android.app.MyApplicationTheme
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.AnalyticsOptInDialog
import tt.co.jesses.moonlight.android.view.util.GradientUtil
import tt.co.jesses.moonlight.android.view.util.angledGradientBackground
import tt.co.jesses.moonlight.android.view.util.bounded

@Preview
@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val illuminationData = uiState.illuminationData

    val activity = LocalContext.current as Activity
    val logger = (activity as? MainActivity)?.logger
    logger?.logConsole("MoonlightScreen: $illuminationData")
    rememberCoroutineScope()
    rememberScaffoldState()
    remember { SnackbarHostState() }

    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val gradientModifier = Modifier
        .angledGradientBackground(
            colors = colorList,
            degrees = 270f,
        )
        .bounded()

    Canvas(modifier = gradientModifier) {}

    var showAnalyticsDialog by rememberSaveable { mutableStateOf(!uiState.isAnalyticsPreferencePending) }
    if (uiState.isAnalyticsPreferencePending) {
        AnalyticsOptInDialog(
            onDismissRequest = {
                showAnalyticsDialog = false
            },
            onConfirmation = { optedIn ->
                viewModel.updateAnalyticsAcceptance(
                    if (optedIn) AnalyticsAcceptance.ACCEPTED else AnalyticsAcceptance.REJECTED
                )
                showAnalyticsDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        while(isActive) {
            delay(viewModel.refreshCycle)
            viewModel.getMoonIllumination()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoonlightScreenPreview() {
    MyApplicationTheme {
        MoonlightScreen()
    }
}

