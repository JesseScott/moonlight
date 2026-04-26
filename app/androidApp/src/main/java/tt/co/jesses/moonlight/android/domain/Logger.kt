package tt.co.jesses.moonlight.android.domain

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.common.data.repository.UserPreferencesRepository
import tt.co.jesses.moonlight.android.view.util.VersionUtil
import javax.inject.Inject

/**
 * Logger class for handling analytics and console logging.
 * Implements EU Consent Mode v2 (Advanced).
 * Ref: https://developers.google.com/tag-platform/security/guides/app-consent?platform=android&consentmode=advanced
 */
class Logger @Inject constructor(
    @ApplicationContext context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
) {

    init {
        observeAnalyticsAcceptance()
    }

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val versionName = VersionUtil.getVersionName(context)

    private fun observeAnalyticsAcceptance() {
        CoroutineScope(Dispatchers.IO).launch {
            userPreferencesRepository.analyticsAcceptance.collect { acceptance ->
                updateConsent(acceptance)
            }
        }
    }

    private fun updateConsent(acceptance: AnalyticsAcceptance) {
        val status = if (acceptance == AnalyticsAcceptance.ACCEPTED) {
            FirebaseAnalytics.ConsentStatus.GRANTED
        } else {
            FirebaseAnalytics.ConsentStatus.DENIED
        }
        val consentMap = mutableMapOf<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus>()
        consentMap[FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE] = status
        consentMap[FirebaseAnalytics.ConsentType.AD_STORAGE] = status
        consentMap[FirebaseAnalytics.ConsentType.AD_USER_DATA] = status
        consentMap[FirebaseAnalytics.ConsentType.AD_PERSONALIZATION] = status

        firebaseAnalytics.setConsent(consentMap)
        logConsole("Consent updated: $status")
    }

    fun logScreen(screen: String) {
        logConsole(screen)
        firebaseAnalytics.logEvent(screen) {
            param(EventNames.Screen.Params.SCREEN, screen)
            param(EventNames.Property.VERSION, versionName)
        }
    }

    fun logEvent(
        eventName: String,
        params: Map<String, String> = emptyMap(),
    ) {
        logConsole(eventName)
        firebaseAnalytics.logEvent(eventName) {
            if (params.isNotEmpty()) {
                val key = params.keys.first()
                val value = params.values.first()
                param(key, value)
            }
            param(EventNames.Property.VERSION, versionName)
        }
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
