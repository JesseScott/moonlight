package tt.co.jesses.moonlight.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FeedbackTest {

    @Test
    fun `fills in the app version and device`() {
        assertEquals(
            "https://tally.so/r/VpEka6?app_version=0.7.1%20%2815%29&device=Pixel%207%20Pro%2C%20Android%2016",
            feedbackFormUrl("0.7.1 (15)", "Pixel 7 Pro", "16"),
        )
    }

    @Test
    fun `encodes awkward characters`() {
        assertEquals(
            "https://tally.so/r/VpEka6?app_version=1.0%2Bbeta&device=SM-A546B%2F%26x%2C%20Android%2014",
            feedbackFormUrl("1.0+beta", "SM-A546B/&x", "14"),
        )
    }

    @Test
    fun `leaves out what can't be read`() {
        assertEquals("https://tally.so/r/VpEka6?device=Pixel%207", feedbackFormUrl(null, " Pixel 7 ", ""))
        assertEquals(
            "https://tally.so/r/VpEka6?app_version=0.7.1&device=Android%2016",
            feedbackFormUrl("0.7.1", null, "16"),
        )
        assertEquals("https://tally.so/r/VpEka6", feedbackFormUrl(null, null, null))
    }
}
