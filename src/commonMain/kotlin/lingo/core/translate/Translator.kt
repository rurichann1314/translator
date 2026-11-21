package lingo.core.translate

import lingo.core.model.Language
import lingo.core.model.TranslationResult

/**
 * 翻译通道接口。TranslationService 只依赖该接口，
 * 不关心具体实现。
 */
interface Translator {
    /** 通道名，会显示在界面和错误信息里 */
    val name: String

    /**
     * 翻译一段文本。
     * @param source 源语言，[Language.AUTO] 表示让接口自己检测
     * @throws lingo.core.error.TranslationException 任何失败都应抛这个
     */
    suspend fun translate(text: String, source: Language, target: Language): TranslationResult
}
