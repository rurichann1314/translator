package lingo.core.translate

import kotlinx.coroutines.CancellationException
import lingo.core.error.ProviderFailure
import lingo.core.error.TranslationException
import lingo.core.model.Language
import lingo.core.model.TranslationResult
import kotlin.time.TimeSource

/**
 * 翻译服务：按顺序试每一条通道，第一个成功的就返回。
 *
 * 先试微软，失败后自动退到 MyMemory；
 * 结果中保留实际返回译文的通道名，便于确认是否发生了兜底。
 */
class TranslationService(
    private val translators: List<Translator>,
) {
    init {
        require(translators.isNotEmpty()) { "至少要有一条翻译通道" }
    }

    /** 界面上展示的通道顺序 */
    val providerNames: List<String> = translators.map { it.name }

    suspend fun translate(
        text: String,
        source: Language,
        target: Language,
    ): TranslationResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            throw TranslationException("先输入点要翻译的内容吧")
        }

        val failures = mutableListOf<ProviderFailure>()
        for (translator in translators) {
            val startMark = TimeSource.Monotonic.markNow()
            try {
                val result = translator.translate(trimmed, source, target)
                return result.copy(elapsedMillis = startMark.elapsedNow().inWholeMilliseconds)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                failures += ProviderFailure(
                    providerName = translator.name,
                    reason = e.message?.ifBlank { null } ?: (e::class.simpleName ?: "未知错误"),
                )
            }
        }

        throw TranslationException(
            message = "所有翻译通道都失败了",
            failures = failures,
        )
    }
}
