package tt.co.jesses.moonlight.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import tt.co.jesses.moonlight.common.data.repository.MoonColorSource
import tt.co.jesses.moonlight.common.util.drawAngledGradient

/**
 * Home screen widget: the moon's gradient, stretched to whatever size the widget is given.
 */
class MoonlightWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val colors = MoonColorSource.create(context).argbColors()
        provideContent {
            MoonlightWidgetContent(context, colors)
        }
    }

    @Composable
    fun MoonlightWidgetContent(context: Context, colors: List<Int>) {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        drawAngledGradient(degrees = 270f, canvas = Canvas(bitmap), colors = colors)

        Image(
            provider = ImageProvider(bitmap),
            contentDescription = context.getString(R.string.widget_content_description),
            contentScale = ContentScale.FillBounds,
            modifier = GlanceModifier.fillMaxSize(),
        )
    }

    private companion object {
        // Only a colour ramp, so a small bitmap is enough and keeps the widget update light
        const val BITMAP_SIZE = 128
    }
}
