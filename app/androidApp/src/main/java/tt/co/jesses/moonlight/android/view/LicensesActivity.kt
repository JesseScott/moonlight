package tt.co.jesses.moonlight.android.view

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tt.co.jesses.moonlight.android.R
import tt.co.jesses.moonlight.android.app.MyApplicationTheme
import tt.co.jesses.moonlight.android.view.util.Constants
import tt.co.jesses.moonlight.common.util.LicenseEntry
import tt.co.jesses.moonlight.common.util.parseLicenses

/**
 * The open source licenses screen. The Google one (play-services-oss-licenses) pulled in the Material Components
 * library, which Play's Android vitals flags for deprecated edge-to-edge APIs, so this is a small screen of our own
 * that reads the same list the oss-licenses Gradle plugin generates at build time.
 */
class LicensesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val licenses = readLicenses(this)
        setContent {
            MyApplicationTheme {
                LicensesScreen(licenses)
            }
        }
    }
}

/**
 * The plugin writes two raw resources: the license texts back to back, and a metadata file with one
 * "offset:length name" line per library (libraries that share a license point at the same text).
 */
fun readLicenses(context: Context): List<LicenseEntry> = runCatching {
    val texts = context.resources.openRawResource(R.raw.third_party_licenses).use { it.readBytes() }
    val metadata = context.resources.openRawResource(R.raw.third_party_license_metadata).use { it.readBytes() }
    parseLicenses(texts, metadata.toString(Charsets.UTF_8))
}.getOrDefault(emptyList())

@Composable
fun LicensesScreen(licenses: List<LicenseEntry>) {
    var open by rememberSaveable { mutableStateOf<Int?>(null) }
    val textColor = Color(0xFF222222)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F4F7))
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = Constants.basePadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.widthIn(max = Constants.maxContentWidth).fillMaxWidth()) {
            Text(
                text = stringResource(R.string.credits_oss),
                modifier = Modifier
                    .padding(top = Constants.basePadding, bottom = Constants.smallPadding)
                    .semantics { heading() },
                fontSize = Constants.headerFontSize,
                color = textColor,
                textDecoration = TextDecoration.Underline,
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(licenses.size) { index ->
                    val expanded = open == index
                    val entry = licenses[index]
                    val state = stringResource(
                        if (expanded) R.string.accordion_expanded else R.string.accordion_collapsed
                    )
                    Column(Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .clickable(role = Role.Button) { open = if (expanded) null else index }
                                .semantics(mergeDescendants = true) { stateDescription = state },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = entry.name,
                                modifier = Modifier.weight(1f),
                                fontSize = Constants.bodyFontSize,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = textColor,
                            )
                        }
                        if (expanded) {
                            Text(
                                text = entry.text,
                                modifier = Modifier.padding(bottom = Constants.basePadding),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = textColor,
                            )
                        }
                    }
                }
            }
        }
    }
}
