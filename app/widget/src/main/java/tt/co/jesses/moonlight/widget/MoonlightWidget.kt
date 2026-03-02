package tt.co.jesses.moonlight.widget

import android.content.Context
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.glance.BitmapImageProvider
import androidx.glance.GlanceId
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import tt.co.jesses.moonlight.common.util.GradientUtil
import tt.co.jesses.moonlight.common.util.drawAngledGradient

class MoonlightWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MoonlightWidgetContent(context)
        }
    }

    @Composable
    fun MoonlightWidgetContent(context: Context) {
        val size = LocalSize.current
        val density = LocalContext.current.resources.displayMetrics.density
        val widthPx = (size.width.value * density).toInt()
        val heightPx = (size.height.value * density).toInt()

        val bitmap = createBitmap(widthPx, heightPx)
        val canvas = Canvas(bitmap)

        drawAngledGradient(
            degrees = 270f,
            canvas = canvas,
            colors = GradientUtil.generateHSLColor().map { it.toArgb() }
        )

        Box(
            modifier = androidx.glance.GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = BitmapImageProvider(bitmap),
                contentDescription = "Moonlight gradient background",
                modifier = androidx.glance.GlanceModifier.fillMaxSize()
            )
        }
    }
}
