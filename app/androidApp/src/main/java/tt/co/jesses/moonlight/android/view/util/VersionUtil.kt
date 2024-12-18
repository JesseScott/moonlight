package tt.co.jesses.moonlight.android.view.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object VersionUtil {
    @Suppress("DEPRECATION")
    fun getVersionName(context: Context): String {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            "${info.versionName} (${info.longVersionCode})"
        } else {
            "${info.versionName} (${info.versionCode})"
        }
    }
}