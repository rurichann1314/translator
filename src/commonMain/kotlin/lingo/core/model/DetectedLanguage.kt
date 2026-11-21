package lingo.core.model

/**
 * 语言检测结果。[confidence] 为 0..1 的置信度。
 */
data class DetectedLanguage(
    val language: Language,
    val confidence: Double,
)