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

/**
 * Explains why moonlight wants the device's location, shown before the system permission prompt
 */
@Composable
fun LocationRationaleDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.location_rationale_title))
        },
        text = {
            // Scrolls so the whole text stays reachable at large font sizes
            Text(
                text = stringResource(R.string.location_rationale_body),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(stringResource(R.string.location_rationale_continue))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.location_rationale_not_now))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun LocationRationaleDialogPreview() {
    MyApplicationTheme {
        LocationRationaleDialog(
            onDismissRequest = {},
            onConfirmation = {},
        )
    }
}
