package lingo.core.translate.dto

import kotlinx.serialization.Serializable

/**
 * 微软免费接口（edge.microsoft.com/translate/translatetext）的返回体。
 * 一次可以传多段，返回的是同样长度的数组。
 *
 * 内部结构使用嵌套类，不为每个小 DTO 单独建文件。
 */
@Serializable
internal data class MicrosoftTranslateDto(
    /** 只有源语言为空（自动检测）时才会返回 */
    val detectedLanguage: DetectedLanguageDto? = null,
    val translations: List<TranslatedTextDto> = emptyList(),
) {
    @Serializable
    internal data class DetectedLanguageDto(
        val language: String = "",
        val score: Double = 0.0,
    )

    @Serializable
    internal data class TranslatedTextDto(
        val text: String = "",
        val to: String = "",
    )
}
