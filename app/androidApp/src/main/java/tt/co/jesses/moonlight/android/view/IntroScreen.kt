package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.app.MyApplicationTheme
import tt.co.jesses.moonlight.android.view.state.MoonlightUiState
import tt.co.jesses.moonlight.android.view.sub.TextOnGradient
import tt.co.jesses.moonlight.android.view.util.Constants
import tt.co.jesses.moonlight.android.view.util.Constants.bodyFontSize
import tt.co.jesses.moonlight.common.util.GradientUtil

/**
 * Shown once, on first launch, before anything is asked: it says what the app does and what comes next, so the
 * system location prompt never appears out of the blue (#191). Usage data is a switch here, off unless turned on.
 *
 * @param askForLocation false when location is already granted, so the page does not promise a prompt
 * @param onContinue called with the usage data choice; the caller then asks for location
 * @param onNotNow called with the usage data choice; location is not asked for
 */
@Composable
fun IntroScreen(
    uiState: MoonlightUiState,
    askForLocation: Boolean,
    onContinue: (shareUsage: Boolean) -> Unit,
    onNotNow: (shareUsage: Boolean) -> Unit,
) {
    val illuminationData = uiState.illuminationData
    var shareUsage by rememberSaveable(uiState.isAnalyticsAccepted) { mutableStateOf(uiState.isAnalyticsAccepted) }
    val borderStroke = BorderStroke(width = Constants.strokeWidth, color = GradientUtil.TextColor)

    TextOnGradient(
        title = stringResource(R.string.intro_title),
        colors = GradientUtil.generateHSLColor(illuminationData),
        textAreaFraction = GradientUtil.textAreaFraction(illuminationData),
    ) {
        Text(
            text = stringResource(R.string.intro_what_it_does),
            fontSize = bodyFontSize,
            color = GradientUtil.TextColor,
        )
        if (askForLocation) {
            IntroPoint(
                title = stringResource(R.string.intro_location_title),
                body = stringResource(R.string.intro_location_body),
            )
        }
        Spacer(Modifier.height(Constants.basePadding))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .toggleable(
                    value = shareUsage,
                    role = Role.Switch,
                    onValueChange = { shareUsage = it },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.credits_privacy_switch),
                fontSize = bodyFontSize,
                fontWeight = FontWeight.Bold,
                color = GradientUtil.TextColor,
                modifier = Modifier.weight(1f).padding(end = Constants.basePadding),
            )
            Switch(
                checked = shareUsage,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = GradientUtil.TextColor,
                    uncheckedThumbColor = GradientUtil.TextColor,
                    uncheckedTrackColor = Color.Transparent,
                    uncheckedBorderColor = GradientUtil.TextColor,
                ),
            )
        }
        Text(
            text = stringResource(R.string.intro_usage_body),
            fontSize = bodyFontSize,
            color = GradientUtil.TextColor,
        )
        Spacer(Modifier.height(Constants.basePadding))
        TextButton(
            onClick = { onContinue(shareUsage) },
            border = borderStroke,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.intro_continue),
                fontSize = bodyFontSize,
                color = GradientUtil.TextColor,
            )
        }
        if (askForLocation) {
            TextButton(
                onClick = { onNotNow(shareUsage) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.intro_not_now),
                    fontSize = bodyFontSize,
                    color = GradientUtil.TextColor,
                )
            }
        }
    }
}

@Composable
private fun IntroPoint(title: String, body: String) {
    Spacer(Modifier.height(Constants.basePadding))
    Text(
        text = title,
        fontSize = bodyFontSize,
        fontWeight = FontWeight.Bold,
        color = GradientUtil.TextColor,
        modifier = Modifier.semantics { heading() },
    )
    Text(
        text = body,
        fontSize = bodyFontSize,
        color = GradientUtil.TextColor,
    )
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    MyApplicationTheme {
        IntroScreen(
            uiState = MoonlightUiState(),
            askForLocation = true,
            onContinue = {},
            onNotNow = {},
        )
    }
}
