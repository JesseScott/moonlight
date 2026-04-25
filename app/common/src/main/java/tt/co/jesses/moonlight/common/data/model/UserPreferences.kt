package tt.co.jesses.moonlight.common.data.model

data class UserPreferences(
    val analyticsAcceptance: Int = AnalyticsAcceptance.UNSET.ordinal,
    val hasSwiped: Boolean = false,
)

enum class AnalyticsAcceptance {
    UNSET,
    ACCEPTED,
    REJECTED;
}
