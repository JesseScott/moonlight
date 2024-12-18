package tt.co.jesses.moonlight.android.domain

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import tt.co.jesses.moonlight.android.view.util.VersionUtil

class Logger(context: Context) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val versionName = VersionUtil.getVersionName(context)

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
        val key = params.keys.first()
        val value = params.values.first()
        firebaseAnalytics.logEvent(eventName) {
            param(key, value)
            param(EventNames.Property.VERSION, versionName)
        }
    }

    fun logConsole(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        private val TAG = Logger::class.java.simpleName
    }
}