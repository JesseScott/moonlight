package tt.co.jesses.moonlight.common.util

import java.net.URLEncoder

/** The feedback form opened from About. It is run by Tally and gets only what the user types, plus the two values below. */
const val FEEDBACK_FORM_URL = "https://tally.so/r/VpEka6"

/**
 * The form's address with its hidden `app_version` and `device` fields filled in, so the user doesn't have to look
 * them up. They pre-fill visible questions the user can still change or clear. A value that can't be read is left out.
 */
fun feedbackFormUrl(appVersion: String?, model: String?, androidVersion: String?): String {
    val device = listOfNotNull(
        model?.trim()?.takeIf { it.isNotEmpty() },
        androidVersion?.trim()?.takeIf { it.isNotEmpty() }?.let { "Android $it" },
    ).joinToString(", ")
    val params = listOfNotNull(
        appVersion?.trim()?.takeIf { it.isNotEmpty() }?.let { "app_version" to it },
        device.takeIf { it.isNotEmpty() }?.let { "device" to it },
    )
    if (params.isEmpty()) return FEEDBACK_FORM_URL
    return FEEDBACK_FORM_URL + params.joinToString("&", prefix = "?") { (key, value) -> "$key=${encode(value)}" }
}

// URLEncoder writes form encoding, where a space is "+"; a query string is clearer with "%20".
// The String charset overload, because the Charset one needs API 33.
private fun encode(value: String) = URLEncoder.encode(value, "UTF-8").replace("+", "%20")
