package tt.co.jesses.moonlight.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.HyperlinkText
import tt.co.jesses.moonlight.android.view.util.GradientUtil
import tt.co.jesses.moonlight.android.view.util.angledGradientBackground
import tt.co.jesses.moonlight.android.view.util.bounded

@Preview
@Composable
fun AboutScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val creditData = viewModel.uiState.collectAsState().value.creditData
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData

    val padding = 16.dp
    val textStyle = TextStyle(
        textAlign = TextAlign.Start,
        color = Color.DarkGray
    )
    val linkTextColor = Color.Red
    val headerFontSize = 24.sp
    val bodyFontSize = 18.sp

    val colorList = GradientUtil.generateHSLColor(
        hue = illuminationData.phase,
        saturation = illuminationData.azimuth,
        lightness = illuminationData.altitude,
        alpha = illuminationData.fraction,
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
            text = stringResource(creditData.creditTitle),
            fontSize = headerFontSize,
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
            fontSize = bodyFontSize
        )
        Spacer(Modifier.padding(padding))
        HyperlinkText(
            fullTextResId = creditData.madeWithFull,
            hyperLinks = mutableMapOf(
                stringResource(id = creditData.madeWithKey) to stringResource(id = creditData.madeWithValue)
            ),
            textStyle = textStyle,
            linkTextColor = linkTextColor,
            fontSize = bodyFontSize
        )
    }

}