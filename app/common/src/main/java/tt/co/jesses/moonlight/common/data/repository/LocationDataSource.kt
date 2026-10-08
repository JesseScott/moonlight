package tt.co.jesses.moonlight.common.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import tt.co.jesses.moonlight.common.data.model.Coordinates
import java.util.concurrent.Executor
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Class responsible for finding the device's coarse location, without any Play Services dependency.
 * Returns null whenever a location can not be determined (no permission, providers off, timeout).
 */
class LocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun hasPermission(): Boolean =
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * @param allowFreshFix when false only an existing recent fix is used, so the call returns straight away.
     * Widgets and wallpapers run in the background, where waiting for a new fix is not wanted (or allowed).
     * @param maxFixAgeMs how old an existing fix may be. Callers that can ask for a fresh fix keep the short
     * default; background callers can not, so they accept an older one (the moon barely changes for a day's travel).
     */
    suspend fun getCoordinates(
        allowFreshFix: Boolean = true,
        maxFixAgeMs: Long = MAX_FIX_AGE_MS,
    ): Coordinates? {
        if (!hasPermission()) return null
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        val location = lastKnownLocation(manager, maxFixAgeMs) ?: if (allowFreshFix) currentLocation(manager) else null
        return location?.let { Coordinates(it.latitude, it.longitude) }
    }

    /** Providers that coarse permission is allowed to query, newest fix wins. */
    private fun providers(): List<String> = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) add(LocationManager.FUSED_PROVIDER)
        add(LocationManager.NETWORK_PROVIDER)
    }

    // Only reached from getCoordinates after hasPermission(), which lint can't see through; a SecurityException
    // (permission revoked in between) is caught by runCatching.
    @SuppressLint("MissingPermission")
    private fun lastKnownLocation(manager: LocationManager, maxFixAgeMs: Long): Location? {
        val candidates = (providers() + LocationManager.PASSIVE_PROVIDER).mapNotNull { provider ->
            runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
        }
        // The moon barely moves relative to a few km of travel, but don't trust a very old fix
        return candidates
            .filter { System.currentTimeMillis() - it.time < maxFixAgeMs }
            .maxByOrNull { it.time }
    }

    // See lastKnownLocation.
    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(manager: LocationManager): Location? {
        val provider = providers().firstOrNull { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }
            ?: return null
        return withTimeoutOrNull(CURRENT_LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val signal = CancellationSignal()
                continuation.invokeOnCancellation { signal.cancel() }
                runCatching {
                    LocationManagerCompat.getCurrentLocation(
                        manager,
                        provider,
                        signal,
                        Executor { it.run() },
                    ) { continuation.resume(it) }
                }.onFailure { continuation.resume(null) }
            }
        }
    }

    companion object {
        private const val MAX_FIX_AGE_MS = 6 * 60 * 60 * 1000L
        const val BACKGROUND_MAX_FIX_AGE_MS = 24 * 60 * 60 * 1000L
        private const val CURRENT_LOCATION_TIMEOUT_MS = 10_000L
    }
}
