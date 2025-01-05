package tt.co.jesses.moonlight.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import tt.co.jesses.moonlight.android.data.model.UserPreferences
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val context: Context,
) {

    suspend fun fetchInitialPreferences(): UserPreferences {
        val preferences = dataStore.data.first().toPreferences()
        return mapUserPreferences(preferences)
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            foo = preferences[PreferencesKeys.FOO_BOOLEAN] ?: false
        )
    }

    private object PreferencesKeys {
        val FOO_STRING = stringPreferencesKey("user_name")
        val FOO_BOOLEAN = booleanPreferencesKey("user_is_logged_in")
    }

    companion object {
        private val TAG = UserPreferencesRepository::class.java.simpleName
        const val USER_PREFERENCES_NAME = "user_preferences"
    }
}