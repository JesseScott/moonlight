package tt.co.jesses.moonlight.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.domain.EventNames
import tt.co.jesses.moonlight.android.domain.Logger
import tt.co.jesses.moonlight.android.view.AboutScreen
import tt.co.jesses.moonlight.android.view.DataScreen
import tt.co.jesses.moonlight.android.view.MoonlightScreen
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.state.Screens
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    rememberNavController()
                    val snackbarHostState = remember { SnackbarHostState() }
                    val viewModel: MoonlightViewModel = viewModel()
                    val pagerState = rememberPagerState(
                        pageCount = { Screens.entries.size },
                        initialPage = 0,
                    )
                    val hasSwiped by viewModel.hasSwiped.collectAsState(initial = false)

                    LaunchedEffect(key1 = hasSwiped) {
                        if (!hasSwiped) {
                            delay(5000)
                            val result = snackbarHostState.showSnackbar(
                                message = getString(R.string.swipe_to_see_more),
                                actionLabel = getString(R.string.ok),
                                duration = SnackbarDuration.Long
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    }

                    LaunchedEffect(pagerState) {
                        snapshotFlow { pagerState.currentPage }.collect { page ->
                            if (page > 0) {
                                viewModel.setHasSwiped(true)
                            }
                            val screen = when(page) {
                                0 -> EventNames.Screen.MOONLIGHT_SCREEN
                                1 -> EventNames.Screen.DATA_SCREEN
                                2 -> EventNames.Screen.ABOUT_SCREEN
                                else -> null
                            }
                            screen?.let { logger.logScreen(it) }
                            logger.logConsole("Page changed to $screen")
                        }
                    }
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { paddingValues ->
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
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
}

//@Preview
//@Composable
//fun DefaultPreview() {
//    MyApplicationTheme {
//        MoonlightScreen { MoonlightViewModel() }
//    }
//}
