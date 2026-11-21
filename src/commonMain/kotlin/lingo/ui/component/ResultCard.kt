package lingo.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lingo.core.model.Language
import lingo.ui.state.TranslationStatus
import lingo.ui.theme.LingoColors
@Composable
fun ResultCard(
    target: Language,
    status: TranslationStatus,
    copied: Boolean,
    onTargetSelected: (Language) -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "译文",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = LingoColors.TextMuted,
            )
            Spacer(Modifier.weight(1f))
            LanguagePill(
                language = target,
                choices = Language.targetChoices,
                onSelect = onTargetSelected,
            )
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 168.dp),
        ) {
            when (status) {
                TranslationStatus.Idle -> Text(
                    text = "译文会出现在这里。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LingoColors.TextMuted,
                )

                TranslationStatus.Loading -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = LingoColors.AccentEnd,
                        strokeWidth = 2.dp,
                    )
                    Text(
                        text = "コアに接続中…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LingoColors.TextSecondary,
                    )
                }

                is TranslationStatus.Success -> SelectionContainer {
                    Text(
                        text = status.result.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 29.sp,
                            color = LingoColors.TextPrimary,
                        ),
                    )
                }

                is TranslationStatus.Failure -> Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = status.message,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = LingoColors.DangerText,
                    )
                    if (status.detail.isNotBlank()) {
                        Text(
                            text = status.detail,
                            style = MaterialTheme.typography.labelSmall,
                            color = LingoColors.DangerText.copy(alpha = 0.75f),
                        )
                    }
                    Text(
                        text = "可以再点一次翻译重试，或者换成 MyMemory 支持的语言组合。",
                        style = MaterialTheme.typography.labelSmall,
                        color = LingoColors.TextMuted,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (status is TranslationStatus.Success) {
                InfoChip(
                    text = status.result.providerName,
                    accent = LingoColors.Success,
                )
                Text(
                    text = "${status.result.elapsedMillis} ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = LingoColors.TextMuted,
                )
            }
            Spacer(Modifier.weight(1f))
            if (status is TranslationStatus.Success) {
                Text(
                    text = if (copied) "已复制" else "复制",
                    modifier = Modifier.clickable(onClick = onCopy),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = if (copied) LingoColors.Success else LingoColors.AccentEnd,
                )
            }
        }
    }
}
