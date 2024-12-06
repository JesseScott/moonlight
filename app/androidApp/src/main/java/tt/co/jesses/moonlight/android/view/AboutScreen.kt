package tt.co.jesses.moonlight.android.view

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.coroutines.launch
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
    val TAG = "AboutScreen"
    val creditData = viewModel.uiState.collectAsState().value.creditData
    val illuminationData = viewModel.uiState.collectAsState().value.illuminationData
    val colorList = GradientUtil.generateHSLColor(illuminationData)

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    val versionInfo = VersionUtil.getVersionName(context = context)

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

    Scaffold(scaffoldState = scaffoldState) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
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
                    text = stringResource(R.string.credits_credits_header),
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
                    text = stringResource(R.string.credits_ack_header),
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
                    text = stringResource(R.string.credits_oss),
                    fontSize = bodyFontSize,
                    style = textStyle.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                    }
                )
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
                Text(
                    text = "Version: $versionInfo",
                    fontSize = bodyFontSize,
                    style = textStyle
                )
                Spacer(Modifier.smallPadding())
                Text(
                    text = stringResource(R.string.credits_info_help),
                    fontSize = bodyFontSize,
                    style = textStyle,
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                                message = "Coming soon!",
                                actionLabel = "todo"
                            )
                            when (snackbarResult) {
                                SnackbarResult.Dismissed -> Log.d(TAG, "Help Snackbar dismissed")
                                SnackbarResult.ActionPerformed -> Log.d(TAG, "Help Snackbar button clicked")
                            }
                        }
                    }
                )
                Spacer(Modifier.smallPadding())
                Text(
                    text = stringResource(R.string.credits_info_coffee),
                    fontSize = bodyFontSize,
                    style = textStyle,
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                                message = "Coming soon!",
                                actionLabel = "todo"
                            )
                            when (snackbarResult) {
                                SnackbarResult.Dismissed -> Log.d(TAG, "Coffee Snackbar dismissed")
                                SnackbarResult.ActionPerformed -> Log.d(TAG, "Coffee Snackbar button clicked")
                            }
                        }
                    }
                )
                Spacer(Modifier.basePadding())
            }
        }
    }
}