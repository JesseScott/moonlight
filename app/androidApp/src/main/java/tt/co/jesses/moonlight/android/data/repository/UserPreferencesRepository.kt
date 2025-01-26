package tt.co.jesses.moonlight.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import tt.co.jesses.moonlight.android.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.android.data.model.UserPreferences
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val context: Context,
) {

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            val analyticsAcceptance = preferences[PreferencesKeys.ANALYTICS_ACCEPTANCE]?: AnalyticsAcceptance.UNSET.ordinal
            UserPreferences(analyticsAcceptance)
        }

    suspend fun fetchInitialPreferences(): UserPreferences {
        val preferences = dataStore.data.first().toPreferences()
        return mapUserPreferences(preferences)
    }

    suspend fun updateAnalyticsAcceptance(acceptance: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANALYTICS_ACCEPTANCE] = acceptance
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            analyticsAcceptance = preferences[PreferencesKeys.ANALYTICS_ACCEPTANCE] ?: AnalyticsAcceptance.UNSET.ordinal
        )
    }

    private object PreferencesKeys {
        val ANALYTICS_ACCEPTANCE = intPreferencesKey("analytics_acceptance")
    }

    companion object {
        private val TAG = UserPreferencesRepository::class.java.simpleName
        const val USER_PREFERENCES_NAME = "user_preferences"
    }
}