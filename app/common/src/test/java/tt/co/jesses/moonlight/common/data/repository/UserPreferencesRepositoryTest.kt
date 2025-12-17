package tt.co.jesses.moonlight.common.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance

class UserPreferencesRepositoryTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: UserPreferencesRepository

    @BeforeEach
    fun setUp() {
        dataStore = mock()
        repository = UserPreferencesRepository(dataStore)
    }

    @Test
    fun `fetchInitialPreferences should return correct acceptance`() = runTest {
        // Given
        val key = intPreferencesKey("analytics_acceptance")
        val preferences = mutablePreferencesOf(key to AnalyticsAcceptance.ACCEPTED.ordinal)
        whenever(dataStore.data).thenReturn(flowOf(preferences))

        // When
        val result = repository.fetchInitialPreferences()

        // Then
        assertEquals(AnalyticsAcceptance.ACCEPTED.ordinal, result.analyticsAcceptance)
    }

    @Test
    fun `fetchInitialPreferences should return unset when key is missing`() = runTest {
        // Given
        val preferences = emptyPreferences()
        whenever(dataStore.data).thenReturn(flowOf(preferences))

        // When
        val result = repository.fetchInitialPreferences()

        // Then
        assertEquals(AnalyticsAcceptance.UNSET.ordinal, result.analyticsAcceptance)
    }

    @Test
    fun `updateAnalyticsAcceptance should update datastore`() = runTest {
        // Given
        val acceptance = AnalyticsAcceptance.REJECTED.ordinal

        // When
        repository.updateAnalyticsAcceptance(acceptance)

        // Then
        argumentCaptor<suspend (Preferences) -> Unit>().apply {
            verify(dataStore).edit(capture())
        }
    }
}
