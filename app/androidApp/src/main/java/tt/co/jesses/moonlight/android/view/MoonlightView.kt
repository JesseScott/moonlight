package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MoonlightView(viewModel: MoonlightViewModel = viewModel()) {
    val uiState = viewModel.uiState.collectAsState()
    val padding = 16.dp
    Column(
        modifier = Modifier.padding(start = padding, top = padding),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Moonlight")
        Spacer(Modifier.padding(padding))
        Text(text = "Fraction: ${uiState.value.illuminationData.fraction}")
        Text(text = "Phase: ${uiState.value.illuminationData.phase}")
        Text(text = "Angle: ${uiState.value.illuminationData.angle}")
    }
}