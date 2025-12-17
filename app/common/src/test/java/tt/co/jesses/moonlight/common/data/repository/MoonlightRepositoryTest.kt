package tt.co.jesses.moonlight.common.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import tt.co.jesses.moonlight.common.data.model.MoonData

class MoonlightRepositoryTest {

    private lateinit var dataSource: MoonlightDataSource
    private lateinit var repository: MoonlightRepository

    @BeforeEach
    fun setUp() {
        dataSource = mock()
        repository = MoonlightRepository(dataSource)
    }

    @Test
    fun `getMoonIllumination should return normalized data`() {
        // Given
        val rawData = MoonData(
            fraction = 0.5f,
            phase = 200f,
            angle = 400f,
            azimuth = 100f,
            altitude = 400f,
            distance = 1000f,
            parallacticAngle = 200f
        )
        whenever(dataSource.getMoonIllumination()).thenReturn(rawData)

        // When
        val result = repository.getMoonIllumination()

        // Then
        // Calculations based on (val - min) / (max - min)
        // phase: (200 - (-180)) / (180 - (-180)) = 380 / 360 = 1.0555556
        assertEquals(1.0555556f, result.phase, 0.0001f)

        // angle: (400 - (-180)) / (180 - (-180)) = 580 / 360 = 1.6111112
        assertEquals(1.6111112f, result.angle, 0.0001f)

        // azimuth: (100 - 0) / (90 - 0) = 100 / 90 = 1.1111112
        assertEquals(1.1111112f, result.azimuth, 0.0001f)

        // altitude: (400 - (-360)) / (360 - (-360)) = 760 / 720 = 1.0555556
        assertEquals(1.0555556f, result.altitude, 0.0001f)

        // distance: not normalized
        assertEquals(1000f, result.distance)

        // parallacticAngle: (200 - (-180)) / (180 - (-180)) = 380 / 360 = 1.0555556
        assertEquals(1.0555556f, result.parallacticAngle, 0.0001f)
    }
}
