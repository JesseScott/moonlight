package tt.co.jesses.moonlight.common.util

import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance

/**
 * What analytics and crash reporting are allowed to do for a given user choice.
 *
 * One choice covers everything that is collected (usage data and crash reports). Until the user accepts, nothing
 * is collected. The ad consents are never granted: moonlight has no ads and no use for them.
 */
data class TelemetryState(
    /** Firebase Analytics and Crashlytics collection are switched on */
    val collectionEnabled: Boolean,
    /** Consent Mode: analytics storage */
    val analyticsStorageGranted: Boolean,
) {
    val adStorageGranted: Boolean = false
    val adUserDataGranted: Boolean = false
    val adPersonalizationGranted: Boolean = false
}

/**
 * @param isDebugBuild debug builds share the release Firebase project, so they never collect, whatever was chosen,
 * to keep test sessions out of the real analytics and crash reports.
 */
fun telemetryStateFor(acceptance: AnalyticsAcceptance, isDebugBuild: Boolean): TelemetryState {
    val collect = acceptance == AnalyticsAcceptance.ACCEPTED && !isDebugBuild
    return TelemetryState(collectionEnabled = collect, analyticsStorageGranted = collect)
}

/** When someone turns collection off, what was kept on the device and not yet sent should be dropped */
fun shouldClearTelemetryData(previous: AnalyticsAcceptance?, current: AnalyticsAcceptance): Boolean =
    previous == AnalyticsAcceptance.ACCEPTED && current != AnalyticsAcceptance.ACCEPTED
