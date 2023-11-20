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
        verticalArrangement = Arrangement.Center
    ) {
        Text(textAlign = TextAlign.Start, text = "Moonlight")

        Spacer(Modifier.padding(padding))
        Row {
            Column {
                Text(textAlign = TextAlign.Start, text = stringResource(illuminationData.fractionRes))
            }
            Spacer(Modifier.padding(padding))
            Column {
                Text(textAlign = TextAlign.End, text = "${illuminationData.fraction}")
            }
        }

        Spacer(Modifier.padding(padding))
        Row {
            Column {
                Text(textAlign = TextAlign.Start, text = stringResource(illuminationData.phaseRes))
            }
            Spacer(Modifier.padding(padding))
            Column {
                Text(textAlign = TextAlign.End, text = "${illuminationData.phase}")
            }
        }

        Spacer(Modifier.padding(padding))
        Row {
            Column {
                Text(textAlign = TextAlign.Start, text = stringResource(illuminationData.angleRes))
            }
            Spacer(Modifier.padding(padding))
            Column {
                Text(textAlign = TextAlign.End, text = "${illuminationData.angle}")
            }
        }

        Spacer(Modifier.padding(padding))
        Row {
            Column {
                Text(textAlign = TextAlign.Start, text = stringResource(illuminationData.azimuthRes))
            }
            Spacer(Modifier.padding(padding))
            Column {
                Text(textAlign = TextAlign.End, text = "${illuminationData.azimuth}")
            }
        }

        Spacer(Modifier.padding(padding))
        Row {
            Column {
                Text(textAlign = TextAlign.Start, text = stringResource(illuminationData.altitudeRes))
            }
            Spacer(Modifier.padding(padding))
            Column {
                Text(textAlign = TextAlign.End, text = "${illuminationData.altitude}")
            }
        }

        Spacer(Modifier.padding(padding))
        Row {
            Column {
                Text(textAlign = TextAlign.Start, text = stringResource(illuminationData.distanceRes))
            }
            Spacer(Modifier.padding(padding))
            Column {
                Text(textAlign = TextAlign.End, text = "${illuminationData.distance}")
            }
        }

        Spacer(Modifier.padding(padding))
        Row {
            Column {
                Text(textAlign = TextAlign.Start, text = stringResource(illuminationData.parallacticAngleRes))
            }
            Spacer(Modifier.padding(padding))
            Column {
                Text(textAlign = TextAlign.End, text = "${illuminationData.parallacticAngle}")
            }
        }
    }

}