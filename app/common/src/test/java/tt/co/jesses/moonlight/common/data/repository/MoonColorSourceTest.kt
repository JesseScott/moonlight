package tt.co.jesses.moonlight.common.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import tt.co.jesses.moonlight.common.data.model.Coordinates
import tt.co.jesses.moonlight.common.data.model.MoonData

class MoonColorSourceTest {

    private lateinit var repository: MoonlightRepository
    private lateinit var locationDataSource: LocationDataSource
    private lateinit var source: MoonColorSource

    @BeforeEach
    fun setUp() {
        repository = mock()
        locationDataSource = mock()
        source = MoonColorSource(repository, locationDataSource)
    }

    @Test
    fun `uses the location when there is one, without waiting for a fresh fix`() = runTest {
        val moon = MoonData(hasPosition = true)
        whenever(locationDataSource.getCoordinates(false)).thenReturn(Coordinates(49.26, -123.05))
        whenever(repository.getMoonIllumination(49.26, -123.05)).thenReturn(moon)

        assertEquals(moon, source.moonData())
        verify(locationDataSource).getCoordinates(false)
    }

    @Test
    fun `falls back to no location when there is none`() = runTest {
        val moon = MoonData(hasPosition = false)
        whenever(locationDataSource.getCoordinates(false)).thenReturn(null)
        whenever(repository.getMoonIllumination(null, null)).thenReturn(moon)

        assertEquals(moon, source.moonData())
    }
}
