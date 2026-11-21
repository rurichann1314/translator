package lingo.core.detect

import lingo.core.model.DetectedLanguage
import lingo.core.model.Language

/**
 * 本地语言检测：按字符所属文字系统判断语言，不依赖网络。
 *
 * 拉丁字母语言之间再用高频词打分区分英/法/德/西/葡/意。
 * 接口不可用时也能立即给出结果，无需额外请求。
 * 同一段文本取命中词最多的语言作为结果。
 */
class LanguageDetector {

    fun detect(text: String): DetectedLanguage? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return null

        var hiragana = 0
        var katakana = 0
        var hangul = 0
        var han = 0
        var hanTraditional = 0
        var arabic = 0
        var cyrillic = 0
        var latin = 0

        for (ch in trimmed) {
            when {
                ch in '\u3040'..'\u309F' -> hiragana++
                ch in '\u30A0'..'\u30FF' -> katakana++
                ch in '\uAC00'..'\uD7AF' || ch in '\u1100'..'\u11FF' -> hangul++
                ch in '\u4E00'..'\u9FFF' -> {
                    han++
                    if (ch in TRADITIONAL_ONLY) hanTraditional++
                }
                ch in '\u0600'..'\u06FF' -> arabic++
                ch in '\u0400'..'\u04FF' -> cyrillic++
                ch in 'a'..'z' || ch in 'A'..'Z' -> latin++
            }
        }

        // 出现假名即判为日语
        if (hiragana + katakana > 0) {
            return DetectedLanguage(Language.JAPANESE, 0.95)
        }
        if (hangul > 0) {
            return DetectedLanguage(Language.KOREAN, 0.95)
        }
        if (arabic > 0) {
            return DetectedLanguage(Language.ARABIC, 0.9)
        }
        if (cyrillic > 0) {
            return DetectedLanguage(Language.RUSSIAN, 0.9)
        }
        if (han > 0) {
            // 繁体专用字出现就判繁体，否则按简体处理
            return if (hanTraditional > 0) {
                DetectedLanguage(Language.ZH_HANT, 0.85)
            } else {
                DetectedLanguage(Language.ZH_HANS, 0.85)
            }
        }
        if (latin > 0) {
            return detectLatin(trimmed, latin)
        }
        return null
    }

    /** 拉丁字母语言之间用高频词投票 */
    private fun detectLatin(text: String, latinCount: Int): DetectedLanguage {
        val words = text.lowercase()
            .split(' ', '\n', '\t', ',', '.', '!', '?', ';', ':', '"', '\'', '，', '。')
            .filter { it.isNotEmpty() && it.all { c -> c in 'a'..'z' || c == '\'' } }

        val scores = mutableMapOf<Language, Int>()
        for ((language, keywords) in STOP_WORDS) {
            val hits = words.count { it in keywords }
            if (hits > 0) scores[language] = hits
        }

        val best = scores.maxByOrNull { it.value }
            ?: return DetectedLanguage(Language.ENGLISH, 0.4)

        // 命中词占比越高，置信度越高
        val confidence = (0.45 + best.value.toDouble() / words.size.coerceAtLeast(1) * 0.5)
            .coerceAtMost(0.98)
        return DetectedLanguage(best.key, confidence)
    }

    private companion object {
        /** 只在繁体里出现的字（拿来做简体/繁体判别） */
        val TRADITIONAL_ONLY: Set<Char> = "這們說國學會時對開關語詞讀寫買賣東車馬鳥魚龍讓聽從後來為麼幾樣電腦網頁裡發現問題實現這個那個".toSet()

        val STOP_WORDS: Map<Language, Set<String>> = mapOf(
            Language.ENGLISH to setOf("the", "and", "is", "are", "you", "for", "that", "with", "this", "have", "not", "but", "what", "your", "was", "it's", "don't", "i'm"),
            Language.FRENCH to setOf("le", "la", "les", "des", "est", "une", "pour", "que", "qui", "dans", "avec", "vous", "nous", "pas", "ce", "je", "et"),
            Language.GERMAN to setOf("der", "die", "das", "und", "ist", "nicht", "ein", "eine", "mit", "für", "sich", "auf", "ich", "wir", "sie", "auch", "dem"),
            Language.SPANISH to setOf("el", "los", "las", "es", "que", "una", "para", "con", "por", "como", "pero", "muy", "está", "esto", "yo", "más"),
            Language.PORTUGUESE to setOf("os", "as", "que", "uma", "para", "com", "por", "não", "muito", "está", "você", "isso", "mais", "eu", "do"),
            Language.ITALIAN to setOf("il", "lo", "gli", "è", "che", "una", "per", "con", "non", "molto", "sono", "questo", "anche", "della"),
        )
    }
}
