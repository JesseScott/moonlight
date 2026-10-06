package tt.co.jesses.moonlight.common.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance

class TelemetryPolicyTest {

    @Test
    fun `nothing is collected until the user accepts`() {
        val unset = telemetryStateFor(AnalyticsAcceptance.UNSET, isDebugBuild = false)
        assertFalse(unset.collectionEnabled)
        assertFalse(unset.analyticsStorageGranted)
    }

    @Test
    fun `declining collects nothing`() {
        val rejected = telemetryStateFor(AnalyticsAcceptance.REJECTED, isDebugBuild = false)
        assertFalse(rejected.collectionEnabled)
        assertFalse(rejected.analyticsStorageGranted)
    }

    @Test
    fun `accepting turns on both usage data and crash reports`() {
        val accepted = telemetryStateFor(AnalyticsAcceptance.ACCEPTED, isDebugBuild = false)
        assertTrue(accepted.collectionEnabled)
        assertTrue(accepted.analyticsStorageGranted)
    }

    @Test
    fun `the ad consents are never granted, even when the user accepts`() {
        for (choice in AnalyticsAcceptance.entries) {
            val state = telemetryStateFor(choice, isDebugBuild = false)
            assertFalse(state.adStorageGranted, "ad storage for $choice")
            assertFalse(state.adUserDataGranted, "ad user data for $choice")
            assertFalse(state.adPersonalizationGranted, "ad personalization for $choice")
        }
    }

    @Test
    fun `debug builds never collect, whatever was chosen`() {
        for (choice in AnalyticsAcceptance.entries) {
            val state = telemetryStateFor(choice, isDebugBuild = true)
            assertFalse(state.collectionEnabled, "collection for $choice")
            assertFalse(state.analyticsStorageGranted, "analytics storage for $choice")
        }
    }

    @Test
    fun `data is cleared only when collection is turned off after being on`() {
        assertTrue(shouldClearTelemetryData(AnalyticsAcceptance.ACCEPTED, AnalyticsAcceptance.REJECTED))
        assertTrue(shouldClearTelemetryData(AnalyticsAcceptance.ACCEPTED, AnalyticsAcceptance.UNSET))
        assertFalse(shouldClearTelemetryData(AnalyticsAcceptance.UNSET, AnalyticsAcceptance.REJECTED))
        assertFalse(shouldClearTelemetryData(AnalyticsAcceptance.REJECTED, AnalyticsAcceptance.ACCEPTED))
        assertFalse(shouldClearTelemetryData(AnalyticsAcceptance.ACCEPTED, AnalyticsAcceptance.ACCEPTED))
        assertFalse(shouldClearTelemetryData(null, AnalyticsAcceptance.REJECTED))
    }
}
