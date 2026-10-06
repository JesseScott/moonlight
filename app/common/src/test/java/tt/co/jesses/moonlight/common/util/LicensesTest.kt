package tt.co.jesses.moonlight.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LicensesTest {

    private val texts = "Apache 2.0 textMIT text".toByteArray()

    @Test
    fun `libraries are matched to their license text and sorted by name`() {
        val metadata = "0:15 Zebra\n15:8 Alpha\n0:15 Middle\n"

        assertEquals(
            listOf(
                LicenseEntry("Alpha", "MIT text"),
                LicenseEntry("Middle", "Apache 2.0 text"),
                LicenseEntry("Zebra", "Apache 2.0 text"),
            ),
            parseLicenses(texts, metadata),
        )
    }

    @Test
    fun `lines that make no sense are skipped`() {
        val metadata = "garbage\n0:15\n:5 NoOffset\n99:5 PastTheEnd\n-1:3 Negative\n0:15 Fine"

        assertEquals(listOf(LicenseEntry("Fine", "Apache 2.0 text")), parseLicenses(texts, metadata))
    }

    @Test
    fun `offsets are in bytes, so text with accents is cut in the right place`() {
        val accented = "café licenseplain".toByteArray()
        val length = "café license".toByteArray().size

        assertEquals(
            listOf(LicenseEntry("Lib", "café license")),
            parseLicenses(accented, "0:$length Lib"),
        )
    }
}
