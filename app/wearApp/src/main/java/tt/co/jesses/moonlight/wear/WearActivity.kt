package tt.co.jesses.moonlight.wear

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tt.co.jesses.moonlight.wear.view.WearMoonlightScreen
import tt.co.jesses.moonlight.wear.view.state.MoonlightViewModel

@AndroidEntryPoint
class WearActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MoonlightViewModel = viewModel()
            val locationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { viewModel.getMoonIllumination() }
            LaunchedEffect(Unit) {
                if (!viewModel.hasLocationPermission()) {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
            WearMoonlightScreen(viewModel = viewModel)
        }
    }
}
