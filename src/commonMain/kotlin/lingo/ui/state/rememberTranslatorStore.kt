package lingo.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import lingo.core.detect.LanguageDetector
import lingo.core.net.createHttpClient
import lingo.core.translate.TranslationService
import lingo.core.translate.defaultTranslators

/**
 * 创建与页面生命周期同长的 Store。
 *
 * HTTP 客户端与通道列表只初始化一次，
 * 避免重组时重复创建连接池。
 */
@Composable
fun rememberTranslatorStore(): TranslatorStore {
    val scope = rememberCoroutineScope()
    return remember {
        TranslatorStore(
            service = TranslationService(defaultTranslators(createHttpClient())),
            detector = LanguageDetector(),
            scope = scope,
        )
    }
}
