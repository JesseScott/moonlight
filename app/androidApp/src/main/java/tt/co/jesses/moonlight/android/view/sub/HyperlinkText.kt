package tt.co.jesses.moonlight.android.view.sub

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.core.text.toSpannable
import tt.co.jesses.moonlight.android.domain.EventNames
import tt.co.jesses.moonlight.android.domain.Logger
import tt.co.jesses.moonlight.android.view.util.launchCustomTabs

data class HyperLinkTextEngine(
    val textStyle: TextStyle = TextStyle.Default,
    val linkTextColor: Color = Color.Red,
    val linkTextFontWeight: FontWeight = FontWeight.Bold,
    val linkTextDecoration: TextDecoration = TextDecoration.None,
    val fontSize: TextUnit = TextUnit.Unspecified,
)

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    @StringRes fullTextResId: Int,
    hyperLinks: Map<String, String>,
    hyperLinkTextEngine: HyperLinkTextEngine,
    logger: Logger,
) {
    val context = LocalContext.current
    val fullText = context.getText(fullTextResId).toSpannable()
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {}
        append(fullText)
        for ((key, value) in hyperLinks) {
            val startIndex = fullText.indexOf(key)
            val endIndex = startIndex + key.length
            addLink(
                url = LinkAnnotation.Url(
                    url = value,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = hyperLinkTextEngine.linkTextColor,
                            fontSize = hyperLinkTextEngine.fontSize,
                            fontWeight = hyperLinkTextEngine.linkTextFontWeight,
                            textDecoration = hyperLinkTextEngine.linkTextDecoration
                        )
                    ),
                    linkInteractionListener = {
                        val url = (it as LinkAnnotation.Url).url
                        if (url.isNotEmpty()) {
                            context.launchCustomTabs(url = url)
                            logger.logEvent(
                                eventName = EventNames.Action.LINK,
                                params = mapOf(
                                    EventNames.Action.Type.URL to url
                                ),
                            )
                        }
                    }
                ),
                start = startIndex,
                end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(
                fontSize = hyperLinkTextEngine.fontSize
            ), start = 0, end = fullText.length
        )
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        style = hyperLinkTextEngine.textStyle,
    )
}