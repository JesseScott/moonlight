package tt.co.jesses.moonlight.android.data.model

data class UserPreferences(
    val analyticsAcceptance: Int = AnalyticsAcceptance.UNSET.ordinal,
)

enum class AnalyticsAcceptance {
    UNSET,
    ACCEPTED,
    REJECTED;
}

