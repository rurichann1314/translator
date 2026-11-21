package lingo.core.model

/**
 * 一次成功翻译的结果。
 *
 * [detectedLanguage] 仅在源语言为自动检测时有意义；
 * [providerName] 是实际返回译文的通道，界面会展示它，
 * 便于确认是否发生了兜底。
 */
data class TranslationResult(
    val text: String,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val detectedLanguage: Language?,
    val providerName: String,
    val elapsedMillis: Long = 0L,
) {
    /** 界面副标题用：自动检测时显示检测结果，否则显示选中的语言 */
    val effectiveSource: Language get() = detectedLanguage ?: sourceLanguage
}
