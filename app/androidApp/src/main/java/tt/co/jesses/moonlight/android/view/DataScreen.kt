package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.app.MyApplicationTheme
import tt.co.jesses.moonlight.android.view.state.MoonlightUiState
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.AccordionSection
import tt.co.jesses.moonlight.android.view.sub.TableLike
import tt.co.jesses.moonlight.android.view.sub.TextOnGradient
import tt.co.jesses.moonlight.android.view.util.Constants.bodyFontSize
import tt.co.jesses.moonlight.common.util.GradientUtil

@Composable
fun DataScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DataScreen(
        uiState = uiState,
    )
}

@Composable
fun DataScreen(
    uiState: MoonlightUiState,
) {
    val illuminationData = uiState.illuminationData
    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val padding = 16.dp
    val textStyle = TextStyle(
        textAlign = TextAlign.Start,
        color = GradientUtil.TextColor
    )

    var descriptionOpen by rememberSaveable { mutableStateOf(false) }

    TextOnGradient(
        title = stringResource(id = R.string.title_data),
        colors = colorList,
        textAreaFraction = GradientUtil.textAreaFraction(illuminationData),
    ) {
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.fractionRes),
                    "%.2f".format(illuminationData.fraction)
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.phaseRes),
                    degrees(illuminationData.phase)
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.angleRes),
                    degrees(illuminationData.angle)
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.azimuthRes),
                    positionValue(degrees(illuminationData.azimuth), illuminationData.hasPosition)
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.altitudeRes),
                    positionValue(degrees(illuminationData.altitude), illuminationData.hasPosition)
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.distanceRes),
                    positionValue("%,.0f km".format(illuminationData.distance), illuminationData.hasPosition)
                )
            )
        }
        if (!illuminationData.hasPosition) {
            Spacer(Modifier.padding(top = padding / 2))
            Text(
                text = stringResource(R.string.data_location_unavailable),
                fontSize = bodyFontSize,
                style = textStyle,
            )
        }
        Spacer(Modifier.padding(top = padding / 2))
        AccordionSection(
            title = stringResource(R.string.data_description_header),
            expanded = descriptionOpen,
            onToggle = { descriptionOpen = !descriptionOpen },
        ) {
            Text(
                text = stringResource(R.string.data_description),
                fontSize = bodyFontSize,
                style = textStyle.copy(
                    lineBreak = LineBreak.Paragraph
                ),
            )
        }
    }
}

private fun degrees(value: Float): String = "%.1f°".format(value)

private fun positionValue(value: String, hasPosition: Boolean): String =
    if (hasPosition) value else "--"

@Preview(showBackground = true)
@Composable
fun DataScreenPreview() {
    MyApplicationTheme {
        DataScreen(
            uiState = MoonlightUiState()
        )
    }
}
