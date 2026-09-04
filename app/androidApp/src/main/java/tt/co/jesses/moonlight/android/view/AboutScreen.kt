package tt.co.jesses.moonlight.android.view

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.oss.licenses.v2.OssLicensesMenuActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.app.MainActivity
import tt.co.jesses.moonlight.android.app.MyApplicationTheme
import tt.co.jesses.moonlight.android.domain.EventNames
import tt.co.jesses.moonlight.android.view.state.MoonlightUiState
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.HyperLinkTextEngine
import tt.co.jesses.moonlight.android.view.sub.HyperlinkText
import tt.co.jesses.moonlight.android.view.util.Constants
import tt.co.jesses.moonlight.android.view.util.Constants.basePadding
import tt.co.jesses.moonlight.android.view.util.Constants.bodyFontSize
import tt.co.jesses.moonlight.android.view.util.Constants.headerFontSize
import tt.co.jesses.moonlight.android.view.util.GradientUtil
import tt.co.jesses.moonlight.android.view.util.VersionUtil
import tt.co.jesses.moonlight.android.view.util.angledGradientBackground
import tt.co.jesses.moonlight.android.view.util.basePadding
import tt.co.jesses.moonlight.android.view.util.launchCustomTabs
import tt.co.jesses.moonlight.android.view.util.smallPadding
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun AboutScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AboutScreen(
        uiState = uiState,
        onRefresh = { viewModel.getMoonIllumination() },
        refreshCycle = viewModel.refreshCycle
    )
}

@Composable
fun AboutScreen(
    uiState: MoonlightUiState,
    onRefresh: () -> Unit = {},
    refreshCycle: Duration = 30.seconds,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as? MainActivity
    val logger = activity?.logger

    val creditData = uiState.creditData
    val illuminationData = uiState.illuminationData
    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current
    val versionInfo = try {
        if (isPreview) "1.0 (1)" else VersionUtil.getVersionName(context = context)
    } catch (_: Exception) {
        "1.0 (1)"
    }

    val feedbackMessage = stringResource(R.string.credits_info_feedback_message)
    val feedbackAction = stringResource(R.string.credits_info_feedback_action)
    val feedbackUrl = stringResource(R.string.credits_info_feedback_action_url)

    val supportMessage = stringResource(R.string.credits_info_coffee_message)
    val supportAction = stringResource(R.string.credits_info_coffee_action)
    val supportUrl = stringResource(R.string.credits_info_coffee_action_url)

    val textStyle = TextStyle(
        textAlign = TextAlign.Start,
        color = Color.DarkGray
    )
    val borderStroke = BorderStroke(
        width = Constants.strokeWidth,
        color = Color.DarkGray,
    )
    val hyperLinkTextEngine = HyperLinkTextEngine(
        textStyle = textStyle,
        linkTextColor = Color.DarkGray,
        fontSize = bodyFontSize,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .angledGradientBackground(
                colors = colorList,
                degrees = 270f,
            )
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(start = basePadding, top = basePadding, end = basePadding, bottom = basePadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                text = stringResource(R.string.credits_credits_header),
                fontSize = bodyFontSize,
                style = textStyle.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(Modifier.smallPadding())
            if (logger != null) {
                HyperlinkText(
                    modifier = Modifier.padding(end = basePadding),
                    fullTextResId = creditData.madeByFull,
                    hyperLinks = mutableMapOf(
                        stringResource(id = R.string.app) to "",
                        stringResource(id = creditData.madeByKey) to stringResource(id = creditData.madeByValue),
                        stringResource(id = creditData.inspiredByKey) to stringResource(id = creditData.inspiredByValue)
                    ),
                    hyperLinkTextEngine = hyperLinkTextEngine,
                    logger = logger,
                )
            }
            Spacer(Modifier.basePadding())

            /// ACKNOWLEDGEMENTS
            Text(
                text = stringResource(R.string.credits_ack_header),
                fontSize = bodyFontSize,
                style = textStyle.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(Modifier.smallPadding())
            if (logger != null) {
                HyperlinkText(
                    modifier = Modifier.padding(end = basePadding),
                    fullTextResId = creditData.sourceFull,
                    hyperLinks = mutableMapOf(
                        stringResource(id = R.string.app) to "",
                        stringResource(id = creditData.sourceKey) to stringResource(id = creditData.sourceValue),
                        stringResource(id = creditData.suncalcKey) to stringResource(id = creditData.suncalcValue)
                    ),
                    hyperLinkTextEngine = hyperLinkTextEngine,
                    logger = logger,
                )
            }
            Spacer(Modifier.smallPadding())

            TextButton(
                onClick = {
                    context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                    logger?.logEvent(
                        eventName = EventNames.Action.BUTTON,
                        params = mapOf(
                            EventNames.Action.Type.OSS to EventNames.Action.Params.BUTTON_CLICK
                        ),
                    )
                },
                border = borderStroke,
            ) {
                Text(
                    text = stringResource(R.string.credits_oss),
                    fontSize = bodyFontSize,
                    style = textStyle,
                )
            }
            Spacer(Modifier.basePadding())

            /// INFO
            Text(
                text = stringResource(R.string.credits_info_header),
                fontSize = bodyFontSize,
                style = textStyle.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(Modifier.smallPadding())

            TextButton(
                onClick = {
                    coroutineScope.launch {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = supportMessage,
                            actionLabel = supportAction
                        ).also {
                            logger?.logEvent(
                                eventName = EventNames.Action.SNACKBAR,
                                params = mapOf(
                                    EventNames.Action.Type.COFFEE to EventNames.Action.Params.SNACKBAR_SHOWN
                                ),
                            )
                        }
                        when (snackbarResult) {
                            SnackbarResult.ActionPerformed -> {
                                context.launchCustomTabs(url = supportUrl)
                                logger?.logEvent(
                                    eventName = EventNames.Action.SNACKBAR,
                                    params = mapOf(
                                        EventNames.Action.Type.COFFEE to EventNames.Action.Params.BUTTON_CLICK
                                    ),
                                )
                            }
                            else -> { /** do nothing */ }
                        }
                    }
                },
                border = borderStroke,
            ) {
                Text(
                    text = stringResource(R.string.credits_info_coffee),
                    fontSize = bodyFontSize,
                    style = textStyle,
                )
            }

            TextButton(
                onClick = {
                    coroutineScope.launch {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = feedbackMessage,
                            actionLabel = feedbackAction,
                        ).also {
                            logger?.logEvent(
                                eventName = EventNames.Action.SNACKBAR,
                                params = mapOf(
                                    EventNames.Action.Type.FEEDBACK to EventNames.Action.Params.SNACKBAR_SHOWN
                                ),
                            )
                        }
                        when (snackbarResult) {
                            SnackbarResult.ActionPerformed -> {
                                context.launchCustomTabs(url = feedbackUrl)
                                logger?.logEvent(
                                    eventName = EventNames.Action.SNACKBAR,
                                    params = mapOf(
                                        EventNames.Action.Type.FEEDBACK to EventNames.Action.Params.BUTTON_CLICK
                                    ),
                                )
                            }
                            else -> { /** do nothing */ }
                        }
                    }
                },
                border = borderStroke,
            ) {
                Text(
                    text = stringResource(R.string.credits_info_feedback),
                    fontSize = bodyFontSize,
                    style = textStyle,
                )
            }
            Spacer(Modifier.smallPadding())

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.credits_info_version_label))
                    }
                    append(" ")
                    append(versionInfo)
                },
                fontSize = bodyFontSize,
                style = textStyle
            )
            Spacer(Modifier.smallPadding())
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    LaunchedEffect(Unit) {
        while(isActive) {
            delay(refreshCycle)
            onRefresh()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    MyApplicationTheme {
        AboutScreen(
            uiState = MoonlightUiState()
        )
    }
}
