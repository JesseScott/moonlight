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
    fun `getMoonIllumination should return the data in real units`() {
        val rawData = MoonData(
            fraction = 0.5f,
            phase = 99f,
            angle = 100f,
            azimuth = 48f,
            altitude = -2.5f,
            distance = 371_000f,
            parallacticAngle = -30f,
            hasPosition = true,
        )
        whenever(dataSource.getMoonIllumination()).thenReturn(rawData)

        assertEquals(rawData, repository.getMoonIllumination())
    }

    @Test
    fun `getMoonIllumination should pass the location through to the data source`() {
        val data = MoonData(hasPosition = true)
        whenever(dataSource.getMoonIllumination(37.77, -122.42)).thenReturn(data)

        val result = repository.getMoonIllumination(latitude = 37.77, longitude = -122.42)

        assertEquals(true, result.hasPosition)
    }
}
