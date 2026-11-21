package lingo.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lingo.core.model.DetectedLanguage
import lingo.core.model.Language
import lingo.ui.theme.LingoColors

/**
 * 原文输入卡片：语言药丸 + 多行输入框 + 字数/检测提示。
 */
@Composable
fun SourceCard(
    text: String,
    language: Language,
    detected: DetectedLanguage?,
    onTextChange: (String) -> Unit,
    onLanguageSelected: (Language) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "原文",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = LingoColors.TextMuted,
            )
            Spacer(Modifier.weight(1f))
            LanguagePill(
                language = language,
                choices = Language.sourceChoices,
                onSelect = onLanguageSelected,
            )
        }

        Spacer(Modifier.height(16.dp))

        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 168.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                lineHeight = 29.sp,
                color = LingoColors.TextPrimary,
            ),
            cursorBrush = SolidColor(LingoColors.AccentEnd),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    if (text.isEmpty()) {
                        Text(
                            text = "把要翻译的句子粘进来，或者直接开始打字…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LingoColors.TextMuted,
                        )
                    }
                    innerTextField()
                }
            },
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (language.isAuto && detected != null) {
                InfoChip(
                    text = "检测：${detected.language.chineseName}",
                    accent = LingoColors.Warning,
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "${text.length} 字",
                style = MaterialTheme.typography.labelSmall,
                color = LingoColors.TextMuted,
            )
            if (text.isNotEmpty()) {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "清空",
                    modifier = Modifier.clickable(onClick = onClear),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = LingoColors.AccentEnd,
                )
            }
        }
    }
}
