package tt.co.jesses.moonlight.android.view.sub

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import tt.co.jesses.moonlight.android.app.MyApplicationTheme

// Assuming you have string resources, e.g., in app/src/main/res/values/strings.xml
// For preview purposes, I'll use direct strings but recommend stringResource for actual use.
// R.string.analytics_dialog_title, R.string.analytics_dialog_text, etc.

@Composable
fun AnalyticsOptInDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (optedIn: Boolean) -> Unit // Called with true for opt-in, false for opt-out
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Analytics Consent") // Replace with stringResource(R.string.analytics_dialog_title)
        },
        text = {
            Text(text = "To help us improve your app experience, we'd like to collect anonymous usage data. Your privacy is important to us, and no personally identifiable information will be collected or shared. You can change this setting later in the app settings.")
            // Replace with stringResource(R.string.analytics_dialog_text)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(true)
                }
            ) {
                Text("Enable")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onConfirmation(false)
                }
            ) {
                Text("Disable") // Replace with stringResource(R.string.disable)
            }
        },
        // modifier = Modifier.padding(16.dp) // Optional: Add padding or other modifiers
    )
}

@Preview(showBackground = true)
@Composable
fun AnalyticsOptInDialogPreview() {
    MyApplicationTheme {
        AnalyticsOptInDialog(
            onDismissRequest = { /* Preview: Log or do nothing */ },
            onConfirmation = { optedIn -> /* Preview: Log choice (optedIn) */ }
        )
    }
}