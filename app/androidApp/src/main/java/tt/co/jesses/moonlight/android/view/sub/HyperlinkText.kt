package tt.co.jesses.moonlight.android.view.sub

import androidx.annotation.StringRes
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.core.text.toSpannable
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.domain.EventNames
import tt.co.jesses.moonlight.android.domain.Logger

data class HyperLinkTextEngine(
    val textStyle : TextStyle = TextStyle.Default,
    val linkTextColor : Color = Color.Red,
    val linkTextFontWeight : FontWeight = FontWeight.Normal,
    val linkTextDecoration : TextDecoration = TextDecoration.None,
    val fontSize : TextUnit = TextUnit.Unspecified,
)

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    @StringRes fullTextResId: Int,
    hyperLinks: Map<String, String>,
    hyperLinkTextEngine: HyperLinkTextEngine,
    logger: Logger,
) {
    val appName = LocalContext.current.getText(R.string.app)
    val fullText = LocalContext.current.getText(fullTextResId).toSpannable()
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("$appName ")
        }
        append(fullText)
        for ((key, value) in hyperLinks) {
            val startIndex = fullText.indexOf(key) + appName.length + 1
            val endIndex = startIndex + key.length
            addStyle(
                style = SpanStyle(
                    color = hyperLinkTextEngine.linkTextColor,
                    fontSize = hyperLinkTextEngine.fontSize,
                    fontWeight = hyperLinkTextEngine.linkTextFontWeight,
                    textDecoration = hyperLinkTextEngine.linkTextDecoration
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = value,
                start = startIndex,
                end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(
                fontSize = hyperLinkTextEngine.fontSize
            ),
            start = 0,
            end = fullText.length
        )
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        style = hyperLinkTextEngine.textStyle,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                    logger.logEvent(
                        eventName = EventNames.Action.LINK,
                        params = mapOf(
                            EventNames.Action.Type.URL to stringAnnotation.item
                        ),
                    )
                }
        }
    )
}