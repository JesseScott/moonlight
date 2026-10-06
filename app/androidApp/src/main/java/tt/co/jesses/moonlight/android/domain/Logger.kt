package tt.co.jesses.moonlight.android.domain

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.android.BuildConfig
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.common.data.repository.UserPreferencesRepository
import tt.co.jesses.moonlight.android.view.util.VersionUtil
import javax.inject.Inject
import javax.inject.Singleton
import tt.co.jesses.moonlight.common.util.shouldClearTelemetryData
import tt.co.jesses.moonlight.common.util.telemetryStateFor

/**
 * Logger class for handling analytics, crash reporting and console logging.
 * Everything is off until the user accepts (EU Consent Mode v2; the ad consents are never granted).
 * Ref: https://developers.google.com/tag-platform/security/guides/app-consent?platform=android&consentmode=advanced
 */
@Singleton
class Logger @Inject constructor(
    @ApplicationContext context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
) {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics = FirebaseCrashlytics.getInstance()
    private val versionName = VersionUtil.getVersionName(context)

    init {
        observeAnalyticsAcceptance()
    }

    private fun observeAnalyticsAcceptance() {
        CoroutineScope(Dispatchers.IO).launch {
            var previous: AnalyticsAcceptance? = null
            userPreferencesRepository.analyticsAcceptance.collect { acceptance ->
                applyChoice(acceptance, previous)
                previous = acceptance
            }
        }
    }

    /**
     * One choice covers everything that is collected: usage data (Analytics) and crash reports (Crashlytics).
     * Both are off in the manifest until the user accepts. See [telemetryStateFor] for the rules.
     */
    private fun applyChoice(acceptance: AnalyticsAcceptance, previous: AnalyticsAcceptance?) {
        val state = telemetryStateFor(acceptance, isDebugBuild = BuildConfig.DEBUG)
        firebaseAnalytics.setAnalyticsCollectionEnabled(state.collectionEnabled)
        crashlytics.setCrashlyticsCollectionEnabled(state.collectionEnabled)
        firebaseAnalytics.setConsent(
            mapOf(
                FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE to state.analyticsStorageGranted.toStatus(),
                FirebaseAnalytics.ConsentType.AD_STORAGE to state.adStorageGranted.toStatus(),
                FirebaseAnalytics.ConsentType.AD_USER_DATA to state.adUserDataGranted.toStatus(),
                FirebaseAnalytics.ConsentType.AD_PERSONALIZATION to state.adPersonalizationGranted.toStatus(),
            )
        )
        if (shouldClearTelemetryData(previous, acceptance)) clearData()
        logConsole("Telemetry choice $acceptance: collection ${if (state.collectionEnabled) "on" else "off"}")
    }

    /** Forget the random installation ID and drop what has not been sent yet */
    private fun clearData() {
        runCatching { firebaseAnalytics.resetAnalyticsData() }
        runCatching { crashlytics.deleteUnsentReports() }
    }

    private fun Boolean.toStatus() =
        if (this) FirebaseAnalytics.ConsentStatus.GRANTED else FirebaseAnalytics.ConsentStatus.DENIED

    fun logScreen(screen: String) {
        logConsole(screen)
        firebaseAnalytics.logEvent(
            screen,
            Bundle().apply {
                putString(EventNames.Screen.Params.SCREEN, screen)
                putString(EventNames.Property.VERSION, versionName)
            }
        )
    }

    fun logEvent(
        eventName: String,
        params: Map<String, String> = emptyMap(),
    ) {
        logConsole(eventName)
        firebaseAnalytics.logEvent(
            eventName,
            Bundle().apply {
                if (params.isNotEmpty()) {
                    val key = params.keys.first()
                    val value = params.values.first()
                    putString(key, value)
                }
                putString(EventNames.Property.VERSION, versionName)
            }
        )
    }

    fun logConsole(message: String) {
        if (tt.co.jesses.moonlight.android.BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    companion object {
        private val TAG = Logger::class.java.simpleName
    }
}
