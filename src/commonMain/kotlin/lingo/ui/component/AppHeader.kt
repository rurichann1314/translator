package lingo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lingo.ui.theme.LingoColors
@Composable
fun AppHeader(
    providerNames: List<String>,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(size = 16.dp))
                .background(
                    Brush.linearGradient(listOf(LingoColors.AccentStart, LingoColors.AccentEnd)),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Hello!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0A0E1A),
            )
        }

        Spacer(Modifier.width(14.dp))

        Column {
            Text(
                text = "ここから",
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.horizontalGradient(
                        listOf(LingoColors.TextPrimary, LingoColors.AccentEnd),
                    ),
                    letterSpacing = 2.sp,
                ),
            )
            Text(
                text = "This is my translator",
                style = MaterialTheme.typography.labelMedium,
                color = LingoColors.TextMuted,
            )
        }

        Spacer(Modifier.weight(1f))

        if (!compact) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                providerNames.forEachIndexed { index, providerName ->
                    InfoChip(
                        text = if (index == 0) "$providerName driven" else "$providerName by rurichann",
                        accent = if (index == 0) LingoColors.Success else LingoColors.Warning,
                    )
                }
            }
        }
    }
}
