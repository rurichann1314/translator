package lingo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lingo.core.model.Language
import lingo.ui.theme.LingoColors
@Composable
fun LanguagePill(
    language: Language,
    choices: List<Language>,
    onSelect: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(percent = 50)

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(shape)
                .background(LingoColors.CardFillStrong)
                .border(width = 1.dp, color = LingoColors.CardBorder, shape = shape)
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = language.nativeName,
                style = MaterialTheme.typography.labelLarge,
                color = LingoColors.TextPrimary,
            )
            Text(
                text = "▾",
                style = MaterialTheme.typography.labelSmall,
                color = LingoColors.TextMuted,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            choices.forEach { choice ->
                val selected = choice == language
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selected) LingoColors.AccentEnd else LingoColors.CardBorder,
                                    ),
                            )
                            Column {
                                Text(
                                    text = choice.nativeName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selected) LingoColors.AccentEnd else LingoColors.TextPrimary,
                                )
                                if (choice.nativeName != choice.chineseName) {
                                    Text(
                                        text = choice.chineseName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = LingoColors.TextMuted,
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelect(choice)
                    },
                )
            }
        }
    }
}
