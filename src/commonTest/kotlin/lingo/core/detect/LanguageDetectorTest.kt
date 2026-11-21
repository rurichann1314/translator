package lingo.core.detect

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import lingo.core.model.Language

/**
 * 语言检测器测试：纯逻辑、不联网。
 * 位于 commonTest，可随各平台目标一起执行。
 */
class LanguageDetectorTest {

    private val detector = LanguageDetector()

    @Test
    fun 日文靠假名认出来() {
        val result = detector.detect("こんにちは、元気ですか？")
        assertEquals(Language.JAPANESE, result?.language)
    }

    @Test
    fun 韩文靠谚文认出来() {
        val result = detector.detect("안녕하세요, 반갑습니다")
        assertEquals(Language.KOREAN, result?.language)
    }

    @Test
    fun 简体中文() {
        val result = detector.detect("今天天气很好，我们去公园散步吧。")
        assertEquals(Language.ZH_HANS, result?.language)
    }

    @Test
    fun 繁体中文() {
        val result = detector.detect("今天天氣很好，我們去公園散步吧。")
        assertEquals(Language.ZH_HANT, result?.language)
    }

    @Test
    fun 俄文() {
        val result = detector.detect("Привет, как дела?")
        assertEquals(Language.RUSSIAN, result?.language)
    }

    @Test
    fun 阿拉伯文() {
        val result = detector.detect("مرحبا كيف حالك")
        assertEquals(Language.ARABIC, result?.language)
    }

    @Test
    fun 英文() {
        val result = detector.detect("Good morning, how are you doing today?")
        assertEquals(Language.ENGLISH, result?.language)
    }

    @Test
    fun 法文有足够线索时不会被当成英文() {
        val result = detector.detect("Bonjour, je ne sais pas ce que vous voulez dire avec cette phrase.")
        assertEquals(Language.FRENCH, result?.language)
    }

    @Test
    fun 德文() {
        val result = detector.detect("Das ist nicht mein Problem, und ich habe keine Zeit dafür.")
        assertEquals(Language.GERMAN, result?.language)
    }

    @Test
    fun 空文本检测不出来() {
        assertNull(detector.detect("   "))
    }

    @Test
    fun 检测结果带置信度() {
        val result = detector.detect("Hello world")
        assertTrue(result != null && result.confidence > 0.0)
    }
}
