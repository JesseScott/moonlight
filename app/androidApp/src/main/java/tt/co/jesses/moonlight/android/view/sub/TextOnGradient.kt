package tt.co.jesses.moonlight.android.view.sub

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.view.util.Constants
import tt.co.jesses.moonlight.android.view.util.Constants.bodyFontSize
import tt.co.jesses.moonlight.common.util.GradientUtil
import tt.co.jesses.moonlight.common.util.angledGradientBackground

/**
 * A text page on the moon gradient. The top of the gradient is always the same light silver to light blue, whatever
 * the moon is doing, so dark text passes contrast there in every moon state; the lower half darkens with the moon
 * and does not. The text therefore stays in the top [textAreaFraction] of the screen and scrolls inside it, and the
 * rest of the screen shows the gradient. See [tt.co.jesses.moonlight.common.util.GradientUtil.textAreaFraction].
 *
 * @param title the page title, kept at the top while the text under it scrolls
 * @param textAreaFraction how much of the screen height, from the top, the text may use
 * @param overlay drawn over the whole screen, for example a snackbar host
 */
@Composable
fun TextOnGradient(
    title: String,
    colors: List<Color>,
    textAreaFraction: Float,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val areaFraction by animateFloatAsState(targetValue = textAreaFraction, label = "textAreaFraction")
    Box(
        modifier = modifier
            .fillMaxSize()
            .angledGradientBackground(colors = colors, degrees = 270f)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxHeight(areaFraction)
                .widthIn(max = Constants.maxContentWidth)
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                ),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(start = Constants.basePadding, top = Constants.basePadding, end = Constants.basePadding)
                    .semantics { heading() },
                fontSize = Constants.headerFontSize,
                color = GradientUtil.TextColor,
                textDecoration = TextDecoration.Underline,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fadeScrollEdges(
                        canScrollBackward = scrollState.canScrollBackward,
                        canScrollForward = scrollState.canScrollForward,
                    )
                    .verticalScroll(scrollState)
                    .padding(Constants.basePadding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                content = content,
            )
        }
        overlay()
    }
}

/** Fades the text out at an edge that has more to scroll to, so the cut-off reads as "scrolls" and not "ends" */
private fun Modifier.fadeScrollEdges(canScrollBackward: Boolean, canScrollForward: Boolean) = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val fade = Constants.scrollFadeHeight.toPx().coerceAtMost(size.height / 2)
        if (canScrollForward) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = size.height - fade,
                    endY = size.height,
                ),
                blendMode = BlendMode.DstOut,
            )
        }
        if (canScrollBackward) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = 0f,
                    endY = fade,
                ),
                blendMode = BlendMode.DstOut,
            )
        }
    }

/**
 * A heading that opens and closes the text under it. The state is hoisted so the page can keep one section open
 * at a time.
 */
@Composable
fun AccordionSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = GradientUtil.TextColor,
    content: @Composable ColumnScope.() -> Unit,
) {
    val state = stringResource(if (expanded) R.string.accordion_expanded else R.string.accordion_collapsed)
    val bringIntoView = remember { BringIntoViewRequester() }
    // Opening a section scrolls it up under the page title, so its text gets the whole text area
    LaunchedEffect(expanded) {
        if (expanded) {
            withFrameNanos { }
            bringIntoView.bringIntoView()
        }
    }
    Column(modifier = modifier.fillMaxWidth().bringIntoViewRequester(bringIntoView)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .clickable(role = Role.Button, onClick = onToggle)
                .semantics(mergeDescendants = true) {
                    heading()
                    stateDescription = state
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                fontSize = bodyFontSize,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = color,
            )
        }
        if (expanded) {
            content()
        }
    }
}
