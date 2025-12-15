package tt.co.jesses.moonlight.android.view.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri

object EmailUtil {
    fun composeEmail(
        addresses: Array<String>,
        subject: String,
        context: Context,
    ) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        intent.resolveActivity(context.packageManager)?.let {
            startActivity(context, intent, null)
        }
    }
}