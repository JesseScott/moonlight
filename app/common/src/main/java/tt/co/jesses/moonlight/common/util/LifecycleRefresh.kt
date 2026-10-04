package tt.co.jesses.moonlight.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlin.time.Duration

/**
 * Calls [onRefresh] every [refreshCycle], but only while the lifecycle is at least STARTED (the screen is
 * visible). Nothing runs while the app is in the background, and when it comes back [onRefresh] is called
 * straight away so it never shows data from before it left. Suspends forever; cancel it to stop.
 *
 * A plain `while (isActive) { delay(...) }` in a composable keeps running after the activity is stopped
 * (pressing Home), because coroutines in composition are not lifecycle-aware.
 */
suspend fun Lifecycle.refreshWhileStarted(refreshCycle: Duration, onRefresh: () -> Unit) {
    var hasBeenStarted = false
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        // The first start is covered by the initial load; later starts mean we are coming back
        if (hasBeenStarted) onRefresh()
        hasBeenStarted = true
        while (true) {
            delay(refreshCycle)
            onRefresh()
        }
    }
}

/**
 * Composable form of [refreshWhileStarted], tied to the lifecycle of the current screen.
 */
@Composable
fun RefreshWhileStarted(refreshCycle: Duration, onRefresh: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnRefresh by rememberUpdatedState(onRefresh)
    LaunchedEffect(lifecycleOwner, refreshCycle) {
        lifecycleOwner.lifecycle.refreshWhileStarted(refreshCycle) { currentOnRefresh() }
    }
}
