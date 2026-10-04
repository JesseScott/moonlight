package tt.co.jesses.moonlight.android.view

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Canvas
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.app.MainActivity
import tt.co.jesses.moonlight.android.app.MyApplicationTheme
import tt.co.jesses.moonlight.android.view.state.MoonlightUiState
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.AnalyticsOptInDialog
import tt.co.jesses.moonlight.common.util.GradientUtil
import tt.co.jesses.moonlight.common.util.angledGradientBackground
import tt.co.jesses.moonlight.android.view.util.bounded
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance
import kotlin.time.Duration

@Composable
fun MoonlightScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MoonlightScreen(
        uiState = uiState,
        onUpdateAnalyticsAcceptance = { viewModel.updateAnalyticsAcceptance(it) },
    )
}

@Composable
fun MoonlightScreen(
    uiState: MoonlightUiState,
    onUpdateAnalyticsAcceptance: (AnalyticsAcceptance) -> Unit = {},
) {
    val illuminationData = uiState.illuminationData

    val activity = LocalActivity.current as? MainActivity
    val logger = activity?.logger
    logger?.logConsole("MoonlightScreen: $illuminationData")
    rememberCoroutineScope()
    remember { SnackbarHostState() }

    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val gradientModifier = Modifier
        .angledGradientBackground(
            colors = colorList,
            degrees = 270f,
        )
        .bounded()

    Canvas(modifier = gradientModifier) {}

    if (uiState.isAnalyticsPreferencePending) {
        AnalyticsOptInDialog(
            onDismissRequest = {
                // Do nothing, the dialog should be mandatory for compliance if we follow v2 strictly
                // or just rely on the user to pick one.
            },
            onConfirmation = { optedIn ->
                onUpdateAnalyticsAcceptance(
                    if (optedIn) AnalyticsAcceptance.ACCEPTED else AnalyticsAcceptance.REJECTED
                )
            }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun MoonlightScreenPreview() {
    MyApplicationTheme {
        MoonlightScreen(
            uiState = MoonlightUiState()
        )
    }
}
