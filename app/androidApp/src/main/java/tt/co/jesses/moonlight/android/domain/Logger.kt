package tt.co.jesses.moonlight.android.domain

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tt.co.jesses.moonlight.android.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.android.data.repository.UserPreferencesRepository
import tt.co.jesses.moonlight.android.view.util.VersionUtil
import javax.inject.Inject

class Logger @Inject constructor(
    @ApplicationContext context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
) {

    init {
        runBlocking {
            shouldEnableAnalytics()
        }
    }

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val versionName = VersionUtil.getVersionName(context)
    private var analyticsAccepted: Boolean = false

    private fun shouldEnableAnalytics() {
        CoroutineScope(Dispatchers.Default).launch {
            val userPreferences = userPreferencesRepository.fetchInitialPreferences()
            val analyticsAcceptance = AnalyticsAcceptance.values()[userPreferences.analyticsAcceptance]
            analyticsAccepted = analyticsAcceptance == AnalyticsAcceptance.ACCEPTED
        }
    }

    fun logScreen(screen: String) {
        logConsole(screen)
        if (analyticsAccepted) {
            firebaseAnalytics.logEvent(screen) {
                param(EventNames.Screen.Params.SCREEN, screen)
                param(EventNames.Property.VERSION, versionName)
            }
        } else {
            logConsole("Analytics not enabled")
        }
    }

    fun logEvent(
        eventName: String,
        params: Map<String, String> = emptyMap(),
    ) {
        logConsole(eventName)
        if (analyticsAccepted) {
            val key = params.keys.first()
            val value = params.values.first()
            firebaseAnalytics.logEvent(eventName) {
                param(key, value)
                param(EventNames.Property.VERSION, versionName)
            }
        } else {
            logConsole("Analytics not enabled")
        }
    }

    fun logConsole(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        private val TAG = Logger::class.java.simpleName
    }
}