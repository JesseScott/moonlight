package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
fun AboutScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val creditData = viewModel.uiState.collectAsState().value.creditData
    val padding = 32.dp
    Column(
        modifier = Modifier
            .padding(start = padding, top = padding)
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            textAlign = TextAlign.Start,
            text = stringResource(creditData.creditTitle)
        )
        Spacer(Modifier.padding(padding))
        Text(textAlign = TextAlign.Start, text = stringResource(creditData.madeBy))
        Spacer(Modifier.padding(padding))
        Text(textAlign = TextAlign.Start, text = stringResource(creditData.madeWith))
    }

}