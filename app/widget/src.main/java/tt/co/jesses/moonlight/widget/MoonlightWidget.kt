package tt.co.jesses.moonlight.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.BitmapImageProvider
import androidx.glance.GlanceId
import androidx.glance.Image
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import tt.co.jesses.moonlight.widget.util.GradientUtil
import tt.co.jesses.moonlight.widget.util.drawAngledGradient

class MoonlightWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MoonlightWidgetContent(context)
        }
    }

    @Composable
    fun MoonlightWidgetContent(context: Context) {
        val width = 256
        val height = 256
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
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
            )
            Text("Moonlight Widget")
        }
    }
}
