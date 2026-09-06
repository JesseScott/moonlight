package tt.co.jesses.moonlight.android.view.util

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.bounded(start: Dp = 0.dp, top: Dp = 0.dp) = this
    .padding(start = start, top = top)
    .fillMaxWidth()
    .fillMaxHeight()

fun Modifier.basePadding() = this.padding(Constants.basePadding)

fun Modifier.smallPadding() = this.padding(Constants.smallPadding)
