package tt.co.jesses.moonlight.android.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tt.co.jesses.moonlight.android.view.AboutScreen
import tt.co.jesses.moonlight.android.view.DataScreen
import tt.co.jesses.moonlight.android.view.MoonlightScreen
import tt.co.jesses.moonlight.android.view.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.Screens

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
                    val navController = rememberNavController()
                    val viewModel: MoonlightViewModel = viewModel()
                    NavHost(navController = navController, startDestination = Screens.Moon.route) {
                        composable(Screens.Moon.route) {
                            MoonlightScreen(
                                viewModel = viewModel,
                                onNavigate = {
                                    navController.navigate(route = it)
                                    println("Moon route: $it")
                                },
                            )
                        }
                        composable(Screens.Data.route) {
                            DataScreen(
                                viewModel = viewModel,
                                onNavigate = {
                                    navController.navigate(route = it)
                                    println("Data route: $it")
                                },
                            )
                        }
                        composable(Screens.About.route) {
                            AboutScreen(
                                viewModel = viewModel,
                                onNavigate = {
                                    navController.navigate(route = it)
                                    println("About route: $it")
                                },
                            )
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