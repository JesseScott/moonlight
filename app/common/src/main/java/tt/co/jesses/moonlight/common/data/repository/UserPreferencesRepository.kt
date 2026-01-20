package tt.co.jesses.moonlight.common.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.common.data.model.UserPreferences
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val _isAnalyticsPreferencePending = MutableStateFlow(true)

    suspend fun fetchInitialPreferences(): UserPreferences {
        val preferences = dataStore.data.first().toPreferences()
        return UserPreferences(
            analyticsAcceptance = preferences[PreferencesKeys.ANALYTICS_ACCEPTANCE] ?: AnalyticsAcceptance.UNSET.ordinal
        )
    }

    suspend fun updateAnalyticsAcceptance(acceptance: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANALYTICS_ACCEPTANCE] = acceptance
        }
    }

    private object PreferencesKeys {
        val ANALYTICS_ACCEPTANCE = intPreferencesKey("analytics_acceptance")
    }

    companion object {
        const val USER_PREFERENCES_NAME = "user_preferences"
    }
}