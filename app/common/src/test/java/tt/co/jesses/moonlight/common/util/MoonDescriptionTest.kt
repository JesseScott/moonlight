package tt.co.jesses.moonlight.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MoonDescriptionTest {

    @Test
    fun `the placeholder before any data is not described as a new moon`() {
        assertEquals(MoonPhaseName.UNKNOWN, moonPhaseName(phase = 0f, fraction = 0f))
    }

    @Test
    fun `almost none of the moon lit is a new moon, whichever way it is heading`() {
        assertEquals(MoonPhaseName.NEW, moonPhaseName(phase = -178f, fraction = 0.001f))
        assertEquals(MoonPhaseName.NEW, moonPhaseName(phase = 178f, fraction = 0.02f))
    }

    @Test
    fun `almost all of the moon lit is a full moon`() {
        assertEquals(MoonPhaseName.FULL, moonPhaseName(phase = 5f, fraction = 0.99f))
        assertEquals(MoonPhaseName.FULL, moonPhaseName(phase = -5f, fraction = 0.98f))
    }

    @Test
    fun `a negative phase is waxing and a positive phase is waning`() {
        assertEquals(MoonPhaseName.WAXING, moonPhaseName(phase = -90f, fraction = 0.5f))
        assertEquals(MoonPhaseName.WANING, moonPhaseName(phase = 90f, fraction = 0.5f))
        assertEquals(MoonPhaseName.WANING, moonPhaseName(phase = 109f, fraction = 0.34f))
    }

    @Test
    fun `the percentage lit is rounded and kept within 0 to 100`() {
        assertEquals(34, moonPercentLit(0.3399f))
        assertEquals(0, moonPercentLit(-0.2f))
        assertEquals(100, moonPercentLit(1.4f))
    }
}
