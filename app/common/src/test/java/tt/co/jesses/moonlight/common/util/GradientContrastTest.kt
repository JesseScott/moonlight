package tt.co.jesses.moonlight.common.util

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.cos
import kotlin.math.pow

/**
 * The Data and About screens put dark text on the gradient, inside the top [GradientUtil.TEXT_AREA_FRACTION]
 * of the screen. This checks that text stays readable (WCAG AA, 4.5:1) there for every moon state, so a change to
 * the colours can not silently break it.
 */
class GradientContrastTest {

    // The gradient's stops, bottom to top: the moon colour, silver, light blue (see GradientUtil.generateHSLColor)
    private val silver = doubleArrayOf(0xC0.toDouble(), 0xC0.toDouble(), 0xC0.toDouble(), 1.0)
    private val lightBlue = doubleArrayOf(0xCC.toDouble(), 0xE5.toDouble(), 0xFF.toDouble(), 1.0)

    // The text colour of the text pages (GradientUtil.TextColor, spelled out so a change to it is noticed here) and the
    // window colour the translucent moon colour sits over
    private val text = doubleArrayOf(0x22.toDouble(), 0x22.toDouble(), 0x22.toDouble())
    private val window = doubleArrayOf(0x30.toDouble(), 0x30.toDouble(), 0x30.toDouble())

    private val minimumContrast = 4.5

    @Test
    fun `the text colour is the one checked here`() {
        assertEquals(Color(0xFF222222), GradientUtil.TextColor)
    }

    @Test
    fun `dark text passes contrast in the text area for every moon state`() {
        var worst = Double.MAX_VALUE
        for (phase in -180..165 step 15) {
            val fraction = ((1 + cos(Math.toRadians(phase.toDouble()))) / 2).toFloat()
            for (altitude in listOf(-30f, -5f, 0f, 20f, 45f, 90f)) {
                val moon = GradientUtil.moonHsl(phase.toFloat(), fraction, altitude)
                // From the top of the screen down to the bottom of the text area, in small steps
                for (step in 0..20) {
                    val y = GradientUtil.TEXT_AREA_FRACTION * step / 20
                    worst = minOf(worst, contrast(text, colourAt(moon, y)))
                }
            }
        }
        assertTrue(worst >= minimumContrast, "worst contrast in the text area was %.2f:1".format(worst))
    }

    @Test
    fun `the check would notice text running further down than the text area`() {
        var worst = Double.MAX_VALUE
        for (phase in -180..165 step 15) {
            val fraction = ((1 + cos(Math.toRadians(phase.toDouble()))) / 2).toFloat()
            for (altitude in listOf(-30f, 0f, 90f)) {
                val moon = GradientUtil.moonHsl(phase.toFloat(), fraction, altitude)
                worst = minOf(worst, contrast(text, colourAt(moon, 0.75f)))
            }
        }
        assertTrue(worst < minimumContrast, "expected text at three quarters of the height to fail, worst was %.2f:1".format(worst))
    }

    @Test
    fun `the text area of a particular moon is never less than the shared one and stays readable all the way down`() {
        var smallest = 1f
        var largest = 0f
        for (phase in -180..165 step 15) {
            val fraction = ((1 + cos(Math.toRadians(phase.toDouble()))) / 2).toFloat()
            for (altitude in listOf(-30f, -5f, 0f, 20f, 45f, 90f)) {
                val moon = GradientUtil.moonHsl(phase.toFloat(), fraction, altitude)
                val area = GradientUtil.textAreaFraction(moon)
                smallest = minOf(smallest, area)
                largest = maxOf(largest, area)
                assertTrue(area >= GradientUtil.TEXT_AREA_FRACTION, "area $area for phase $phase altitude $altitude")
                for (step in 0..40) {
                    val worst = contrast(text, colourAt(moon, area * step / 40))
                    assertTrue(
                        worst >= minimumContrast,
                        "%.2f:1 at %.0f%% of the way down, phase %d altitude %.0f, text area %.2f"
                            .format(worst, 100.0 * step / 40 * area, phase, altitude, area)
                    )
                }
            }
        }
        println("text area ranges from $smallest to $largest")
        // A dark moon leaves the light top and a bit more, a bright one leaves most of the screen
        assertTrue(smallest < 0.7f, "darkest state gives $smallest")
        assertTrue(largest > 0.8f, "brightest state gives $largest")
    }

    /** The colour on screen at [y] (0 = top, 1 = bottom): the gradient, then the translucent moon colour over the window */
    private fun colourAt(moon: MoonHsl, y: Float): DoubleArray {
        val bottom = hslToRgba(moon)
        val t = 1.0 - y // 0 at the bottom, 1 at the top
        val (from, to, u) = if (t <= 0.5) Triple(bottom, silver, t / 0.5) else Triple(silver, lightBlue, (t - 0.5) / 0.5)
        val c = DoubleArray(4) { from[it] + (to[it] - from[it]) * u }
        return DoubleArray(3) { c[it] * c[3] + window[it] * (1 - c[3]) }
    }

    private fun hslToRgba(moon: MoonHsl): DoubleArray {
        val h = moon.hue / 360.0
        val s = moon.saturation.toDouble()
        val l = moon.lightness.toDouble()
        val q = if (l < 0.5) l * (1 + s) else l + s - l * s
        val p = 2 * l - q
        fun channel(offset: Double): Double {
            var t = h + offset
            if (t < 0) t += 1
            if (t > 1) t -= 1
            val v = when {
                t < 1.0 / 6 -> p + (q - p) * 6 * t
                t < 0.5 -> q
                t < 2.0 / 3 -> p + (q - p) * (2.0 / 3 - t) * 6
                else -> p
            }
            return v * 255
        }
        return doubleArrayOf(channel(1.0 / 3), channel(0.0), channel(-1.0 / 3), moon.alpha.toDouble())
    }

    private fun luminance(rgb: DoubleArray): Double {
        fun linear(c: Double): Double {
            val v = c / 255
            return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * linear(rgb[0]) + 0.7152 * linear(rgb[1]) + 0.0722 * linear(rgb[2])
    }

    private fun contrast(a: DoubleArray, b: DoubleArray): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
    }
}
