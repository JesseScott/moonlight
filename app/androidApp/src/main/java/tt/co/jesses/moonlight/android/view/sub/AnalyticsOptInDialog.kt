package tt.co.jesses.moonlight.android.view.sub

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            // Scrolls so the whole text stays reachable at large font sizes
            Text(
                text = stringResource(R.string.analytics_consent_body),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
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