package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AboutScreen(
    viewModel: MoonlightViewModel = viewModel(),
    onNavigate: (String) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val padding = 16.dp

    Column(
        modifier = Modifier
            .padding(start = padding, top = padding)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onNavigate(Screens.Moon.route)
                    }
                )
            },
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "${uiState.value.creditData.creditTitle}")
        Spacer(Modifier.padding(padding))
        Text(text = "Made by: ${uiState.value.creditData.madeBy}")
        Text(text = "Made with: ${uiState.value.creditData.madeWith}")
    }

}