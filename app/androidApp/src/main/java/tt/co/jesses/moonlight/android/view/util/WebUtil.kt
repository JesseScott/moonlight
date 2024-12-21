package tt.co.jesses.moonlight.android.view.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun Context.launchCustomTabs(url: String) {
    CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
}