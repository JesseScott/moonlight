package tt.co.jesses.moonlight.common.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.common.data.model.UserPreferences
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val _isAnalyticsPreferencePending = MutableStateFlow(true)

    val hasSwiped: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_SWIPED] ?: false
    }

    suspend fun setHasSwiped(hasSwiped: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SWIPED] = hasSwiped
        }
    }

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
        val HAS_SWIPED = booleanPreferencesKey("has_swiped")
    }

    companion object {
        const val USER_PREFERENCES_NAME = "user_preferences"
    }
}