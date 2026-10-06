package tt.co.jesses.moonlight.android.view

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.oss.licenses.v2.OssLicensesMenuActivity
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.app.MainActivity
import tt.co.jesses.moonlight.android.app.MyApplicationTheme
import tt.co.jesses.moonlight.android.domain.EventNames
import tt.co.jesses.moonlight.android.view.state.MoonlightUiState
import tt.co.jesses.moonlight.android.view.state.MoonlightViewModel
import tt.co.jesses.moonlight.android.view.sub.AccordionSection
import tt.co.jesses.moonlight.android.view.sub.HyperLinkTextEngine
import tt.co.jesses.moonlight.android.view.sub.HyperlinkText
import tt.co.jesses.moonlight.android.view.sub.TextOnGradient
import tt.co.jesses.moonlight.android.view.util.Constants
import tt.co.jesses.moonlight.android.view.util.Constants.basePadding
import tt.co.jesses.moonlight.android.view.util.Constants.bodyFontSize
import tt.co.jesses.moonlight.android.view.util.VersionUtil
import tt.co.jesses.moonlight.android.view.util.WallpaperWidgetUtil
import tt.co.jesses.moonlight.common.data.model.AnalyticsAcceptance
import tt.co.jesses.moonlight.common.util.GradientUtil
import tt.co.jesses.moonlight.android.view.util.basePadding
import tt.co.jesses.moonlight.android.view.util.launchCustomTabs
import tt.co.jesses.moonlight.android.view.util.smallPadding

@Composable
fun AboutScreen(
    viewModel: MoonlightViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AboutScreen(
        uiState = uiState,
        onAnalyticsChange = { accepted ->
            viewModel.updateAnalyticsAcceptance(
                if (accepted) AnalyticsAcceptance.ACCEPTED else AnalyticsAcceptance.REJECTED
            )
        },
    )
}

@Composable
fun AboutScreen(
    uiState: MoonlightUiState,
    onAnalyticsChange: (Boolean) -> Unit = {},
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

    val wallpaperUnavailableMessage = stringResource(R.string.credits_extras_wallpaper_unavailable)
    val widgetUnavailableMessage = stringResource(R.string.credits_extras_widget_unavailable)
    val canSetWallpaper = !isPreview && WallpaperWidgetUtil.supportsLiveWallpaper(context)
    val canPinWidget = !isPreview && WallpaperWidgetUtil.canPinWidget(context)

    val supportMessage = stringResource(R.string.credits_info_coffee_message)
    val supportAction = stringResource(R.string.credits_info_coffee_action)
    val supportUrl = stringResource(R.string.credits_info_coffee_action_url)
    val privacyPolicyUrl = stringResource(R.string.credits_privacy_policy_url)

    val textStyle = TextStyle(
        textAlign = TextAlign.Start,
        color = GradientUtil.TextColor
    )
    val borderStroke = BorderStroke(
        width = Constants.strokeWidth,
        color = GradientUtil.TextColor,
    )
    val hyperLinkTextEngine = HyperLinkTextEngine(
        textStyle = textStyle,
        linkTextColor = GradientUtil.TextColor,
        fontSize = bodyFontSize,
    )

    var openSection by rememberSaveable { mutableStateOf<Int?>(null) }

    TextOnGradient(
        title = stringResource(creditData.creditTitle),
        colors = colorList,
        textAreaFraction = GradientUtil.textAreaFraction(illuminationData),
        overlay = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.systemBars)
            )
        },
    ) {
        /// CREDITS
        AccordionSection(
            title = stringResource(R.string.credits_credits_header),
            expanded = openSection == 0,
            onToggle = { openSection = if (openSection == 0) null else 0 },
        ) {
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
        }
        /// ACKNOWLEDGEMENTS
        AccordionSection(
            title = stringResource(R.string.credits_ack_header),
            expanded = openSection == 1,
            onToggle = { openSection = if (openSection == 1) null else 1 },
        ) {
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
        }
        /// WALLPAPER AND WIDGET
        AccordionSection(
            title = stringResource(R.string.credits_extras_header),
            expanded = openSection == 2,
            onToggle = { openSection = if (openSection == 2) null else 2 },
        ) {
            Text(
                text = stringResource(R.string.credits_extras_description),
                fontSize = bodyFontSize,
                style = textStyle,
                modifier = Modifier.padding(end = basePadding),
            )
            Spacer(Modifier.smallPadding())

            if (canSetWallpaper) {
                TextButton(
                    onClick = {
                        logger?.logEvent(
                            eventName = EventNames.Action.BUTTON,
                            params = mapOf(
                                EventNames.Action.Type.WALLPAPER to EventNames.Action.Params.BUTTON_CLICK
                            ),
                        )
                        if (!WallpaperWidgetUtil.openWallpaperPreview(context)) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = wallpaperUnavailableMessage)
                            }
                        }
                    },
                    border = borderStroke,
                ) {
                    Text(
                        text = stringResource(R.string.credits_extras_wallpaper),
                        fontSize = bodyFontSize,
                        style = textStyle,
                    )
                }
            }
            if (canPinWidget) {
                TextButton(
                    onClick = {
                        logger?.logEvent(
                            eventName = EventNames.Action.BUTTON,
                            params = mapOf(
                                EventNames.Action.Type.WIDGET to EventNames.Action.Params.BUTTON_CLICK
                            ),
                        )
                        if (!WallpaperWidgetUtil.pinWidget(context)) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = widgetUnavailableMessage)
                            }
                        }
                    },
                    border = borderStroke,
                ) {
                    Text(
                        text = stringResource(R.string.credits_extras_widget),
                        fontSize = bodyFontSize,
                        style = textStyle,
                    )
                }
            } else if (!isPreview) {
                Text(
                    text = stringResource(R.string.credits_extras_widget_hint),
                    fontSize = bodyFontSize,
                    style = textStyle,
                    modifier = Modifier.padding(end = basePadding),
                )
            }
        }
        /// PRIVACY
        AccordionSection(
            title = stringResource(R.string.credits_privacy_header),
            expanded = openSection == 3,
            onToggle = { openSection = if (openSection == 3) null else 3 },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .toggleable(
                        value = uiState.isAnalyticsAccepted,
                        role = Role.Switch,
                        onValueChange = onAnalyticsChange,
                    )
                    .padding(end = basePadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.credits_privacy_switch),
                    fontSize = bodyFontSize,
                    style = textStyle,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = basePadding),
                )
                Switch(
                    checked = uiState.isAnalyticsAccepted,
                    onCheckedChange = null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = GradientUtil.TextColor,
                        uncheckedThumbColor = GradientUtil.TextColor,
                        uncheckedTrackColor = Color.Transparent,
                        uncheckedBorderColor = GradientUtil.TextColor,
                    ),
                )
            }
            Text(
                text = stringResource(R.string.credits_privacy_description),
                fontSize = bodyFontSize,
                style = textStyle,
                modifier = Modifier.padding(end = basePadding),
            )
            Spacer(Modifier.smallPadding())
            TextButton(
                onClick = {
                    context.launchCustomTabs(url = privacyPolicyUrl)
                    logger?.logEvent(
                        eventName = EventNames.Action.BUTTON,
                        params = mapOf(
                            EventNames.Action.Type.PRIVACY_POLICY to EventNames.Action.Params.BUTTON_CLICK
                        ),
                    )
                },
                border = borderStroke,
            ) {
                Text(
                    text = stringResource(R.string.credits_privacy_policy),
                    fontSize = bodyFontSize,
                    style = textStyle,
                )
            }
        }
        /// INFO
        AccordionSection(
            title = stringResource(R.string.credits_info_header),
            expanded = openSection == 4,
            onToggle = { openSection = if (openSection == 4) null else 4 },
        ) {

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
