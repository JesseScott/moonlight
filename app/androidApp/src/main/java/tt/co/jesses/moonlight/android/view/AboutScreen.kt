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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.view.sub.HyperlinkText

@Composable
fun AboutScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val creditData = viewModel.uiState.collectAsState().value.creditData
    val padding = 16.dp
    val textStyle = TextStyle(
        textAlign = TextAlign.Start,
        color = Gray
    )
    val linkTextColor = Color.Red
    val fontSize = 18.sp
    Column(
        modifier = Modifier
            .padding(start = padding, top = padding)
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(creditData.creditTitle),
            fontSize = fontSize,
            style = textStyle,
        )
        Spacer(Modifier.padding(padding))
        HyperlinkText(
            fullTextResId = creditData.madeByFull,
            hyperLinks = mutableMapOf(
                stringResource(id = creditData.madeByKey) to stringResource(id = creditData.madeByValue),
                stringResource(id = creditData.inspiredByKey) to stringResource(id = creditData.inspiredByValue)
            ),
            textStyle = textStyle,
            linkTextColor = linkTextColor,
            fontSize = fontSize
        )
        Spacer(Modifier.padding(padding))
        HyperlinkText(
            fullTextResId = creditData.madeWithFull,
            hyperLinks = mutableMapOf(
                stringResource(id = creditData.madeWithKey) to stringResource(id = creditData.madeWithValue)
            ),
            textStyle = textStyle,
            linkTextColor = linkTextColor,
            fontSize = fontSize
        )
    }

}