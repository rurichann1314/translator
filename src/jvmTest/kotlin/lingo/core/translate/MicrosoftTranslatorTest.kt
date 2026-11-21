package lingo.core.translate

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import lingo.core.model.Language
import lingo.core.net.createHttpClient

/**
 * 微软通道的网络测试，会实际发起请求。
 *
 * 需要能访问 edge.microsoft.com。
 * 失败时优先检查网络与接口变更。
 */
class MicrosoftTranslatorTest {

    private val translator = MicrosoftTranslator(createHttpClient())

    @Test
    fun 英文自动检测翻成简体中文() = runBlocking {
        val result = translator.translate(
            text = "Hello world, this is a test.",
            source = Language.AUTO,
            target = Language.ZH_HANS,
        )

        assertTrue(result.text.isNotBlank(), "译文不能是空的")
        assertTrue(
            result.text.any { it in '\u4E00'..'\u9FFF' },
            "翻成中文就该有汉字，实际返回：${result.text}",
        )
        assertEquals(Language.ENGLISH, result.detectedLanguage, "自动检测应该认出这是英语")
        assertEquals(MicrosoftTranslator.PROVIDER_NAME, result.providerName)
    }

    @Test
    fun 中文自动检测翻成英文() = runBlocking {
        val result = translator.translate(
            text = "今天天气不错，我们去吃薯条吧。",
            source = Language.AUTO,
            target = Language.ENGLISH,
        )

        assertEquals(Language.ZH_HANS, result.detectedLanguage)
        assertTrue(result.text.isNotBlank())
        assertTrue(
            result.text.none { it in '\u4E00'..'\u9FFF' },
            "翻成英文不该再剩汉字，实际返回：${result.text}",
        )
    }

    @Test
    fun 显式指定源语言也能翻() = runBlocking {
        val result = translator.translate(
            text = "Good morning!",
            source = Language.ENGLISH,
            target = Language.JAPANESE,
        )

        assertEquals(Language.ENGLISH, result.detectedLanguage)
        assertTrue(result.text.isNotBlank())
    }
}
