package lingo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import lingo.ui.component.AppBackground
import lingo.ui.component.AppHeader
import lingo.ui.component.ResultCard
import lingo.ui.component.SourceCard
import lingo.ui.component.SwapButton
import lingo.ui.component.TranslateButton
import lingo.ui.state.TranslatorStore
import lingo.ui.theme.LingoColors

/**
 * 主页：宽屏左右排布，窄屏上下堆叠。
 *
 * 只负责布局与尺寸，业务状态全部来自 [TranslatorStore]。
 * 组件本身无状态。
 */
@Composable
fun TranslatorScreen(store: TranslatorStore) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        AppBackground()

        val wide = maxWidth >= 900.dp
        val horizontalPadding = if (wide) 40.dp else 20.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(if (wide) 36.dp else 24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 1180.dp),
            ) {
                AppHeader(
                    providerNames = store.providerNames,
                    compact = !wide,
                )

                Spacer(Modifier.height(24.dp))

                if (wide) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SourceCard(
                            text = store.sourceText,
                            language = store.sourceLanguage,
                            detected = store.detected,
                            onTextChange = store::updateSourceText,
                            onLanguageSelected = store::selectSourceLanguage,
                            onClear = store::clear,
                            modifier = Modifier.weight(1f),
                        )
                        SwapButton(onClick = store::swapLanguages)
                        ResultCard(
                            target = store.targetLanguage,
                            status = store.status,
                            copied = store.copied,
                            onTargetSelected = store::selectTargetLanguage,
                            onCopy = store::copyResult,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SourceCard(
                            text = store.sourceText,
                            language = store.sourceLanguage,
                            detected = store.detected,
                            onTextChange = store::updateSourceText,
                            onLanguageSelected = store::selectSourceLanguage,
                            onClear = store::clear,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            SwapButton(onClick = store::swapLanguages)
                        }
                        ResultCard(
                            target = store.targetLanguage,
                            status = store.status,
                            copied = store.copied,
                            onTargetSelected = store::selectTargetLanguage,
                            onCopy = store::copyResult,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                TranslateButton(
                    loading = store.isLoading,
                    enabled = store.canTranslate,
                    onClick = store::translate,
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "微软翻译免费接口 · 失败自动切换 MyMemory · 全部在你的浏览器里跑，没有后端",
                    style = MaterialTheme.typography.labelSmall,
                    color = LingoColors.TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
