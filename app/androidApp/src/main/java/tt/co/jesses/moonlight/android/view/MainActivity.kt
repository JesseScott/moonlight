package tt.co.jesses.moonlight.android.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tt.co.jesses.moonlight.android.app.MyApplicationTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MoonlightView()
                }
            }
        }
    }
}

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

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        MoonlightView()
    }
}
