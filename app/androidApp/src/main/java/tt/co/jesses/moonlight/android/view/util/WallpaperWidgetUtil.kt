package tt.co.jesses.moonlight.android.view.util

import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import tt.co.jesses.moonlight.widget.MoonlightWallpaperService
import tt.co.jesses.moonlight.widget.MoonlightWidgetReceiver

/**
 * Helpers to point people at the live wallpaper and the home screen widget, which otherwise live in the
 * system's wallpaper picker and widget list where they are easy to miss.
 */
object WallpaperWidgetUtil {

    fun supportsLiveWallpaper(context: Context): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER)

    /** Opens the system's wallpaper preview with moonlight already selected. False if nothing could open it. */
    fun openWallpaperPreview(context: Context): Boolean {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            .putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(context, MoonlightWallpaperService::class.java),
            )
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    fun canPinWidget(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            AppWidgetManager.getInstance(context).isRequestPinAppWidgetSupported

    /** Asks the launcher to add the widget (the system shows its own confirmation). */
    fun pinWidget(context: Context): Boolean =
        canPinWidget(context) &&
            AppWidgetManager.getInstance(context).requestPinAppWidget(
                ComponentName(context, MoonlightWidgetReceiver::class.java),
                null,
                null,
            )
}
