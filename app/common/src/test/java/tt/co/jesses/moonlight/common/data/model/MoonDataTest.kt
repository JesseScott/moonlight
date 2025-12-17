package tt.co.jesses.moonlight.common.data.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tt.co.jesses.moonlight.common.R

class MoonDataTest {

    @Test
    fun `default values should be set correctly`() {
        val moonData = MoonData()

        assertEquals(R.string.data_fraction, moonData.fractionRes)
        assertEquals(0.0f, moonData.fraction)
        assertEquals(R.string.data_phase, moonData.phaseRes)
        assertEquals(0.0f, moonData.phase)
        assertEquals(R.string.data_angle, moonData.angleRes)
        assertEquals(0.0f, moonData.angle)
        assertEquals(R.string.data_azimuth, moonData.azimuthRes)
        assertEquals(0.0f, moonData.azimuth)
        assertEquals(R.string.data_altitude, moonData.altitudeRes)
        assertEquals(0.0f, moonData.altitude)
        assertEquals(R.string.data_distance, moonData.distanceRes)
        assertEquals(0.0f, moonData.distance)
        assertEquals(R.string.data_parallactic_angle, moonData.parallacticAngleRes)
        assertEquals(0.0f, moonData.parallacticAngle)
    }

    @Test
    fun `copy should work as expected`() {
        val original = MoonData()
        val copy = original.copy(fraction = 0.5f)

        assertEquals(0.0f, original.fraction)
        assertEquals(0.5f, copy.fraction)
    }
}
