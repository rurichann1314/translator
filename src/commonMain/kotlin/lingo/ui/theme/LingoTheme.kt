package lingo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily

/**
 * 全局主题：深色配色 + 紫青渐变强调色。
 * 深色底便于衬托卡片与渐变。
 */
@Composable
fun LingoTheme(
    fontFamily: FontFamily,
    content: @Composable () -> Unit,
) {
    val typography = remember(fontFamily) { lingoTypography(fontFamily) }
    val colors = remember {
        darkColorScheme(
            primary = LingoColors.AccentEnd,
            onPrimary = LingoColors.BackgroundTop,
            secondary = LingoColors.AccentStart,
            onSecondary = LingoColors.TextPrimary,
            background = LingoColors.BackgroundTop,
            onBackground = LingoColors.TextPrimary,
            surface = LingoColors.BackgroundMid,
            onSurface = LingoColors.TextPrimary,
            surfaceVariant = LingoColors.CardFillStrong,
            onSurfaceVariant = LingoColors.TextSecondary,
            error = LingoColors.DangerText,
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content,
    )
}
