package tt.co.jesses.moonlight.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    rememberNavController()
                    val coroutineScope = rememberCoroutineScope()
                    val scaffoldState = rememberScaffoldState()
                    val viewModel: MoonlightViewModel = viewModel()
                    val pagerState = rememberPagerState(
                        pageCount = { Screens.values().size },
                        initialPage = 0,
                    )
                    val hasSwiped by viewModel.hasSwiped.collectAsState(initial = false)

                    LaunchedEffect(key1 = hasSwiped) {
                        if (!hasSwiped) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Swipe to see more"
                                )
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
                    Scaffold(scaffoldState = scaffoldState) { paddingValues ->
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
