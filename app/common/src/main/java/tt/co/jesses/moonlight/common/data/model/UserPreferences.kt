package tt.co.jesses.moonlight.common.data.model

data class UserPreferences(
    val analyticsAcceptance: Int = AnalyticsAcceptance.UNSET.ordinal,
)

enum class AnalyticsAcceptance {
    UNSET,
    ACCEPTED,
    REJECTED;
}
