package tt.co.jesses.moonlight.common.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MoonlightDataSourceTest {

    private lateinit var dataSource: MoonlightDataSource

    @BeforeEach
    fun setUp() {
        dataSource = MoonlightDataSource()
    }

    @Test
    fun `getMoonIllumination with default coordinates should not throw exception`() {
        val result = dataSource.getMoonIllumination()
        assertNotNull(result)
    }

    @Test
    fun `getMoonIllumination with custom coordinates should not throw exception`() {
        val result = dataSource.getMoonIllumination(latitude = 37.7749, longitude = -122.4194)
        assertNotNull(result)
    }

    @Test
    fun `getMoonIllumination without a location should not report a position`() {
        val result = dataSource.getMoonIllumination()
        assertFalse(result.hasPosition)
        assertEquals(0f, result.azimuth)
        assertEquals(0f, result.altitude)
        assertEquals(0f, result.distance)
    }

    @Test
    fun `getMoonIllumination with a location should report a position`() {
        val result = dataSource.getMoonIllumination(latitude = 37.7749, longitude = -122.4194)
        assertTrue(result.hasPosition)
        // The moon is always hundreds of thousands of km away
        assertTrue(result.distance > 300_000f)
    }

    @Test
    fun `position depends on location but illumination does not`() {
        val sanFrancisco = dataSource.getMoonIllumination(37.7749, -122.4194)
        val sydney = dataSource.getMoonIllumination(-33.8688, 151.2093)
        assertEquals(sanFrancisco.fraction, sydney.fraction, 0.001f)
        assertNotEquals(sanFrancisco.altitude, sydney.altitude)
    }
}
