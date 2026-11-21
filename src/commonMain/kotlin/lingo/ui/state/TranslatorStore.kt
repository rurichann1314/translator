package lingo.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lingo.core.detect.LanguageDetector
import lingo.core.error.TranslationException
import lingo.core.model.DetectedLanguage
import lingo.core.model.Language
import lingo.core.platform.copyTextToClipboard
import lingo.core.translate.TranslationService

/**
 * 界面状态容器：持有全部可变状态，Compose 只负责渲染。
 *
 * 业务逻辑不依赖 Compose，
 * 可直接在 JVM 上构造并测试。
 */
class TranslatorStore(
    private val service: TranslationService,
    private val detector: LanguageDetector,
    private val scope: CoroutineScope,
) {
    var sourceText by mutableStateOf("")
        private set

    var sourceLanguage by mutableStateOf(Language.AUTO)
        private set

    var targetLanguage by mutableStateOf(Language.ZH_HANS)
        private set

    var status by mutableStateOf<TranslationStatus>(TranslationStatus.Idle)
        private set

    /** 本地即时检测的猜测结果，为 null 表示还判断不出来 */
    var detected by mutableStateOf<DetectedLanguage?>(null)
        private set

    /** 复制按钮的"已复制"提示是否显示 */
    var copied by mutableStateOf(false)
        private set

    private var translateJob: Job? = null
    private var copiedJob: Job? = null

    val providerNames: List<String> = service.providerNames

    val isLoading: Boolean
        get() = status == TranslationStatus.Loading

    val canTranslate: Boolean
        get() = sourceText.isNotBlank() && !isLoading

    val characterCount: Int
        get() = sourceText.length

    fun updateSourceText(value: String) {
        if (value == sourceText) return
        sourceText = value
        detected = detector.detect(value)
        // 内容变化后旧译文失效，重置状态
        if (status is TranslationStatus.Success) {
            status = TranslationStatus.Idle
        }
        if (value.isBlank()) {
            status = TranslationStatus.Idle
        }
    }

    fun selectSourceLanguage(language: Language) {
        if (language == sourceLanguage) return
        sourceLanguage = language
        if (!language.isAuto) detected = null
    }

    fun selectTargetLanguage(language: Language) {
        if (!language.asTarget || language == targetLanguage) return
        targetLanguage = language
        if (status is TranslationStatus.Success) {
            status = TranslationStatus.Idle
        }
    }

    /**
     * 交换源/目标语言。
     * 源语言为自动检测时，用检测结果作为新的目标语言，否则无法对调。
     */
    fun swapLanguages() {
        val effectiveSource = if (sourceLanguage.isAuto) {
            detected?.language ?: Language.ENGLISH
        } else {
            sourceLanguage
        }
        if (effectiveSource == targetLanguage) return

        val previousResult = (status as? TranslationStatus.Success)?.result
        sourceLanguage = targetLanguage
        targetLanguage = effectiveSource

        // 把译文移到原文框，便于反向再翻一次
        if (previousResult != null) {
            sourceText = previousResult.text
            detected = detector.detect(previousResult.text)
            status = TranslationStatus.Idle
        }
    }

    fun clear() {
        translateJob?.cancel()
        sourceText = ""
        detected = null
        status = TranslationStatus.Idle
    }

    fun translate() {
        if (!canTranslate) return
        translateJob?.cancel()
        status = TranslationStatus.Loading

        translateJob = scope.launch {
            try {
                val result = service.translate(sourceText, sourceLanguage, targetLanguage)
                status = TranslationStatus.Success(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                status = TranslationStatus.Failure(
                    message = e.message?.ifBlank { null } ?: "翻译失败了",
                    detail = (e as? TranslationException)?.detailText().orEmpty(),
                )
            }
        }
    }

    fun copyResult() {
        val text = (status as? TranslationStatus.Success)?.result?.text
        if (text.isNullOrBlank()) return
        copyTextToClipboard(text)
        copied = true
        copiedJob?.cancel()
        copiedJob = scope.launch {
            delay(1800)
            copied = false
        }
    }
}
