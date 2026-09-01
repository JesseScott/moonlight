package tt.co.jesses.moonlight.common.data.repository

import org.junit.jupiter.api.Assertions.assertNotNull
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
}
