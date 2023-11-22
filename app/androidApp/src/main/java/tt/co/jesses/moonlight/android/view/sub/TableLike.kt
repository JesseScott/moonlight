package tt.co.jesses.moonlight.android.view.sub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableLike(
    data: Pair<String, String>,
    color: Color = Color.Gray,
    fontSize: TextUnit = 16.sp,
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = data.first,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                fontSize = fontSize,
                color = color,
            )
            Text(
                text = data.second,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                fontSize = fontSize,
                color = color,
            )
        }
    }
}