package tt.co.jesses.moonlight.android.view.sub

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.app.MyApplicationTheme

@Composable
fun AnalyticsOptInDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (optedIn: Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.analytics_consent_title))
        },
        text = {
            Text(text = stringResource(R.string.analytics_consent_body))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(true)
                }
            ) {
                Text(stringResource(R.string.analytics_action_enable))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onConfirmation(false)
                }
            ) {
                Text(stringResource(R.string.analytics_action_disable))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun AnalyticsOptInDialogPreview() {
    MyApplicationTheme {
        AnalyticsOptInDialog(
            onDismissRequest = { /* Preview: Log or do nothing */ },
            onConfirmation = { _ -> /* Preview: Log choice (optedIn) */ }
        )
    }
}