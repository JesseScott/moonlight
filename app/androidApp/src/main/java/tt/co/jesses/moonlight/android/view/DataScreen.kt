package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DataScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()
    val fraction = uiState.value.illuminationData.fraction
    val padding = 16.dp

    Column(
        modifier = Modifier.padding(start = padding, top = padding).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Moonlight")
        Spacer(Modifier.padding(padding))
        Text(text = "Fraction: $fraction")
        Text(text = "Phase: ${uiState.value.illuminationData.phase}")
        Text(text = "Angle: ${uiState.value.illuminationData.angle}")
        Text(text = "Azimuth: ${uiState.value.illuminationData.azimuth}")
        Text(text = "Altitude: ${uiState.value.illuminationData.altitude}")
        Text(text = "Distance: ${uiState.value.illuminationData.distance}")
        Text(text = "Parallactic Angle: ${uiState.value.illuminationData.parallacticAngle}")
    }

}