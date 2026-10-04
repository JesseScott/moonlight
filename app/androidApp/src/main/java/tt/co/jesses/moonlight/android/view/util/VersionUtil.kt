package tt.co.jesses.moonlight.android.view.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import tt.co.jesses.moonlight.android.BuildConfig

object VersionUtil {
    @Suppress("DEPRECATION")
    fun getVersionName(context: Context): String {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        val version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            "${info.versionName} (${info.longVersionCode})"
        } else {
            "${info.versionName} (${info.versionCode})"
        }
        return if (BuildConfig.DEBUG) "$version DEBUG" else version
    }
}