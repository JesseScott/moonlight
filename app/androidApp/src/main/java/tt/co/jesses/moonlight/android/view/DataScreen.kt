package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.view.sub.TableLike

@Composable
fun DataScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    val padding = 16.dp

    Column(
        modifier = Modifier
            .padding(start = padding, top = padding)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(textAlign = TextAlign.Start, text = stringResource(id = R.string.title_data))

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
        Row {
            TableLike(
                data = Pair(
                    stringResource(illuminationData.parallacticAngleRes),
                    "${illuminationData.parallacticAngle}"
                )
            )
        }
    }

}