package tt.co.jesses.moonlight.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tt.co.jesses.moonlight.android.view.AboutScreen
import tt.co.jesses.moonlight.android.view.DataScreen
import tt.co.jesses.moonlight.android.view.MoonlightScreen
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.state.Screens

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
                    rememberNavController()
                    val viewModel: MoonlightViewModel = viewModel()
                    HorizontalPager(
                        state = rememberPagerState(
                            pageCount = { Screens.values().size },
                            initialPage = 0
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> MoonlightScreen(viewModel = viewModel)
                            1 -> DataScreen(viewModel = viewModel)
                            2 -> AboutScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        //MoonlightScreen {}
    }
}
