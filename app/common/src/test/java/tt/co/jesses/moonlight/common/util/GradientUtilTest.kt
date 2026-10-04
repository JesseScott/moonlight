package tt.co.jesses.moonlight.common.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

class GradientUtilTest {

    private val delta = 0.0001f

    @Test
    fun `full moon high in the sky is warm gold, saturated, light and opaque`() {
        val moon = GradientUtil.moonHsl(phase = 0f, fraction = 1f, altitude = 90f)

        assertEquals(45f, moon.hue, delta)
        assertEquals(0.65f, moon.saturation, delta)
        assertEquals(0.75f, moon.lightness, delta)
        assertEquals(1f, moon.alpha, delta)
    }

    @Test
    fun `new moon at the horizon is cool blue, quiet, dark and mostly transparent`() {
        val moon = GradientUtil.moonHsl(phase = -180f, fraction = 0f, altitude = 0f)

        assertEquals(230f, moon.hue, 0.001f)
        assertEquals(0.25f, moon.saturation, delta)
        assertEquals(0.35f, moon.lightness, delta)
        assertEquals(0.25f, moon.alpha, delta)
    }

    @Test
    fun `waxing and waning moons of the same fraction have different hues`() {
        val waxing = GradientUtil.moonHsl(phase = -90f, fraction = 0.5f, altitude = 45f)
        val waning = GradientUtil.moonHsl(phase = 90f, fraction = 0.5f, altitude = 45f)

        assertNotEquals(waxing.hue, waning.hue)
        assertEquals(50f, waning.hue - waxing.hue, 0.01f)
        assertEquals(waxing.saturation, waning.saturation, delta)
        assertEquals(waxing.lightness, waning.lightness, delta)
        assertEquals(waxing.alpha, waning.alpha, delta)
    }

    @Test
    fun `the colour has no jump where the lunar cycle wraps around`() {
        val justBeforeNew = GradientUtil.moonHsl(phase = 179.9f, fraction = 0.0000015f, altitude = 45f)
        val justAfterNew = GradientUtil.moonHsl(phase = -179.9f, fraction = 0.0000015f, altitude = 45f)

        assertTrue(abs(justBeforeNew.hue - justAfterNew.hue) < 0.5f)
    }

    @Test
    fun `saturation grows with altitude`() {
        val horizon = GradientUtil.moonHsl(0f, 1f, 0f).saturation
        val middle = GradientUtil.moonHsl(0f, 1f, 45f).saturation
        val zenith = GradientUtil.moonHsl(0f, 1f, 90f).saturation

        assertTrue(horizon < middle)
        assertTrue(middle < zenith)
    }

    @Test
    fun `below the horizon the colour is darker and more muted`() {
        val above = GradientUtil.moonHsl(0f, 1f, 0f)
        val below = GradientUtil.moonHsl(0f, 1f, -20f)

        assertTrue(below.lightness < above.lightness)
        assertTrue(below.saturation < above.saturation)
        assertEquals(above.hue, below.hue, delta)
        assertEquals(above.alpha, below.alpha, delta)
    }

    @Test
    fun `the below horizon wash fades in gradually`() {
        val horizon = GradientUtil.moonHsl(0f, 1f, 0f).lightness
        val slightlyBelow = GradientUtil.moonHsl(0f, 1f, -6f).lightness
        val wellBelow = GradientUtil.moonHsl(0f, 1f, -30f).lightness
        val straightDown = GradientUtil.moonHsl(0f, 1f, -90f).lightness

        assertTrue(horizon > slightlyBelow)
        assertTrue(slightlyBelow > wellBelow)
        assertEquals(wellBelow, straightDown, delta)
    }

    @Test
    fun `hue stays within 0 to 360 over a whole cycle`() {
        for (phase in -180..180 step 5) {
            val fraction = (1 + kotlin.math.cos(Math.toRadians(phase.toDouble())).toFloat()) / 2
            val hue = GradientUtil.moonHsl(phase.toFloat(), fraction, 30f).hue
            assertTrue(hue >= 0f && hue < 360f, "hue $hue out of range at phase $phase")
        }
    }

    @Test
    fun `out of range inputs are clamped`() {
        val moon = GradientUtil.moonHsl(phase = 0f, fraction = 2f, altitude = 500f)

        assertEquals(1f, moon.alpha, delta)
        assertEquals(0.65f, moon.saturation, delta)
    }

    @Test
    fun `270 degrees runs bottom to top, straight up the middle`() {
        val line = angledGradientLine(width = 1080f, height = 2400f, degrees = 270f)!!

        assertEquals(540f, line.startX, delta)
        assertEquals(540f, line.endX, delta)
        assertEquals(2400f, line.startY, delta)
        assertEquals(0f, line.endY, delta)
    }

    @Test
    fun `0 and 90 degrees run left to right and top to bottom`() {
        val across = angledGradientLine(width = 1080f, height = 2400f, degrees = 0f)!!
        assertEquals(0f, across.startX, delta)
        assertEquals(1080f, across.endX, delta)
        assertEquals(across.startY, across.endY, 0.01f)

        val down = angledGradientLine(width = 1080f, height = 2400f, degrees = 90f)!!
        assertEquals(0f, down.startY, delta)
        assertEquals(2400f, down.endY, delta)
        assertEquals(down.startX, down.endX, 0.01f)
    }

    @Test
    fun `an area with no height can not be drawn`() {
        assertEquals(null, angledGradientLine(width = 1080f, height = 0f, degrees = 270f))
    }
}
