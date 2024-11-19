package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.TableLike
import tt.co.jesses.moonlight.android.view.util.Constants.bodyFontSize
import tt.co.jesses.moonlight.android.view.util.Constants.headerFontSize
import tt.co.jesses.moonlight.android.view.util.GradientUtil
import tt.co.jesses.moonlight.android.view.util.angledGradientBackground
import tt.co.jesses.moonlight.android.view.util.bounded

@Preview
@Composable
fun DataScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val padding = 16.dp
    val textStyle = TextStyle(
        textAlign = TextAlign.Start,
        color = Color.DarkGray
    )

    val gradientModifier = Modifier
        .angledGradientBackground(
            colors = colorList,
            degrees = 270f,
        )
        .bounded(
            start = padding,
            top = padding,
        )

    Column(
        modifier = gradientModifier,
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
                    "${illuminationData.fraction}"
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.phaseRes),
                    "${illuminationData.phase}"
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.angleRes),
                    "${illuminationData.angle}"
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.azimuthRes),
                    "${illuminationData.azimuth}"
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.altitudeRes),
                    "${illuminationData.altitude}"
                )
            )
        }
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.distanceRes),
                    "${illuminationData.distance}"
                )
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