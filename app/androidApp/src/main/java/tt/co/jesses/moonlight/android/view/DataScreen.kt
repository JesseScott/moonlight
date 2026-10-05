package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.app.MyApplicationTheme
import tt.co.jesses.moonlight.android.view.state.MoonlightUiState
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.TableLike
import tt.co.jesses.moonlight.android.view.util.Constants
import tt.co.jesses.moonlight.android.view.util.Constants.bodyFontSize
import tt.co.jesses.moonlight.android.view.util.Constants.headerFontSize
import tt.co.jesses.moonlight.common.util.GradientUtil
import tt.co.jesses.moonlight.common.util.angledGradientBackground
import kotlin.time.Duration

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
        color = Color.DarkGray
    )

    Box(
        modifier = Modifier
            .angledGradientBackground(
                colors = colorList,
                degrees = 270f,
            )
            .fillMaxSize()
    ) {
    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .widthIn(max = Constants.maxContentWidth)
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
            .padding(start = padding, top = padding, end = padding, bottom = padding),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.title_data),
            fontSize = headerFontSize,
            style = textStyle.copy(
                textDecoration = TextDecoration.Underline
            ),
        )

        Spacer(Modifier.padding(padding))
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
        Spacer(Modifier.padding(padding))
        Row {
            Text(
                text = stringResource(R.string.data_description),
                fontSize = bodyFontSize,
                modifier = Modifier.padding(end = padding),
                style = textStyle.copy(
                    lineBreak = LineBreak.Paragraph
                ),
            )
        }
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
