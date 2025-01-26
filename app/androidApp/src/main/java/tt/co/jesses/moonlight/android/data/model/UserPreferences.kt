package tt.co.jesses.moonlight.android.data.model

data class UserPreferences(
    val analyticsAcceptance: Int = AnalyticsAcceptance.UNSET.ordinal,
)

enum class AnalyticsAcceptance {
    UNSET,
    ACCEPTED,
    REJECTED;

    fun valueOf(value: Int): AnalyticsAcceptance {
        return when (value) {
            0 -> UNSET
            1 -> ACCEPTED
            2 -> REJECTED
            else -> UNSET
        }
    }
}

