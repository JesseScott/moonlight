package tt.co.jesses.moonlight.android.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tt.co.jesses.moonlight.android.app.MyApplicationTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MoonlightViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //GreetingView(Greeting().greet())
                    MoonlightView()
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Composable
fun MoonlightView(
    viewModel: MoonlightViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    Text(text = "Moonlight")
    Text(text = uiState.value.illuminationData.phase.toString())
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        //GreetingView("Hello, Android!")
        MoonlightView()
    }
}
