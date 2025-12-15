package tt.co.jesses.moonlight.android.view.sub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TableLike(
    data: Pair<String, String>,
    color: Color = Color.DarkGray,
    fontSize: TextUnit = 18.sp,
) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = data.first,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
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