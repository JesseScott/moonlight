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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.HyperLinkTextEngine
import tt.co.jesses.moonlight.android.view.sub.HyperlinkText
import tt.co.jesses.moonlight.android.view.util.Constants.basePadding
import tt.co.jesses.moonlight.android.view.util.Constants.bodyFontSize
import tt.co.jesses.moonlight.android.view.util.Constants.headerFontSize
import tt.co.jesses.moonlight.android.view.util.Constants.linkTextColor
import tt.co.jesses.moonlight.android.view.util.GradientUtil
import tt.co.jesses.moonlight.android.view.util.VersionUtil
import tt.co.jesses.moonlight.android.view.util.angledGradientBackground
import tt.co.jesses.moonlight.android.view.util.basePadding
import tt.co.jesses.moonlight.android.view.util.bounded
import tt.co.jesses.moonlight.android.view.util.smallPadding

@Preview
@Composable
fun AboutScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val creditData = viewModel.uiState.collectAsState().value.creditData
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val versionInfo = VersionUtil.getVersionName(context = LocalContext.current)

    val textStyle = TextStyle(
        textAlign = TextAlign.Start,
        color = Color.DarkGray
    )

    val hyperLinkTextEngine = HyperLinkTextEngine(
        textStyle = textStyle,
        linkTextColor = linkTextColor,
        fontSize = bodyFontSize,
    )

    val gradientModifier = Modifier
        .angledGradientBackground(
            colors = colorList,
            degrees = 270f,
        )
        .bounded(
            start = basePadding,
            top = basePadding,
        )

    Column(
        modifier = gradientModifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        /// TITLE
        Text(
            text = stringResource(creditData.creditTitle),
            fontSize = headerFontSize,
            style = textStyle.copy(
                textDecoration = TextDecoration.Underline
            ),
        )
        Spacer(Modifier.basePadding())

        /// CREDITS
        Text(
            text = "credits",
            fontSize = bodyFontSize,
            style = textStyle.copy(
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(Modifier.smallPadding())
        HyperlinkText(
            fullTextResId = creditData.madeByFull,
            hyperLinks = mutableMapOf(
                stringResource(id = creditData.madeByKey) to stringResource(id = creditData.madeByValue),
                stringResource(id = creditData.inspiredByKey) to stringResource(id = creditData.inspiredByValue)
            ),
            hyperLinkTextEngine = hyperLinkTextEngine,
        )
        Spacer(Modifier.basePadding())

        /// ACKNOWLEDGEMENTS
        Text(
            text = "acknowledgements",
            fontSize = bodyFontSize,
            style = textStyle.copy(
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(Modifier.smallPadding())
        HyperlinkText(
            fullTextResId = creditData.sourceFull,
            hyperLinks = mutableMapOf(
                stringResource(id = creditData.sourceKey) to stringResource(id = creditData.sourceValue),
                stringResource(id = creditData.suncalcKey) to stringResource(id = creditData.suncalcValue)
            ),
            hyperLinkTextEngine = hyperLinkTextEngine,
        )
        Spacer(Modifier.smallPadding())
        Text(
            text = "open source licenses",
            fontSize = bodyFontSize,
            style = textStyle
        )
        Spacer(Modifier.basePadding())

        /// INFO
        Text(
            text = "info",
            fontSize = bodyFontSize,
            style = textStyle.copy(
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(Modifier.smallPadding())
        Text(
            text = "Version: $versionInfo",
            fontSize = bodyFontSize,
            style = textStyle
        )
        Text(
            text = stringResource(R.string.credits_info_help),
            fontSize = bodyFontSize,
            style = textStyle
        )
        Text(
            text = stringResource(R.string.credits_info_coffee),
            fontSize = bodyFontSize,
            style = textStyle
        )
        Spacer(Modifier.basePadding())

    }
}