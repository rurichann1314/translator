package lingo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lingo.ui.theme.LingoColors

/**
 * 主操作按钮：渐变底 + 按压禁用态 + 加载转圈。
 * 整块可点击，作为页面主操作按钮。
 */
@Composable
fun TranslateButton(
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val active = enabled && !loading
    val shape = RoundedCornerShape(18.dp)
    val background = if (active) {
        Brush.horizontalGradient(listOf(LingoColors.AccentStart, LingoColors.AccentEnd))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF232A42), Color(0xFF232A42)))
    }

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .background(background)
            .clickable(enabled = active, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = LingoColors.TextPrimary,
                    strokeWidth = 2.dp,
                )
            }
            Text(
                text = when {
                    loading -> "翻译中…"
                    else -> "翻 译"
                },
                style = MaterialTheme.typography.titleMedium,
                color = if (active || loading) Color(0xFF07101F) else LingoColors.TextMuted,
            )
        }
    }
}
