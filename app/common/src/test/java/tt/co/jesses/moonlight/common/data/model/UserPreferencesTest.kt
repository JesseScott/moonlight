package tt.co.jesses.moonlight.common.data.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserPreferencesTest {

    @Test
    fun `default values should be set correctly`() {
        val userPreferences = UserPreferences()

        assertEquals(AnalyticsAcceptance.UNSET.ordinal, userPreferences.analyticsAcceptance)
    }

    @Test
    fun `analytics acceptance enum ordinal mapping`() {
        assertEquals(0, AnalyticsAcceptance.UNSET.ordinal)
        assertEquals(1, AnalyticsAcceptance.ACCEPTED.ordinal)
        assertEquals(2, AnalyticsAcceptance.REJECTED.ordinal)
    }
}
