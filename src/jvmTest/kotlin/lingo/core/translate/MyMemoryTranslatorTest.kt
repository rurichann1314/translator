package lingo.core.translate

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import lingo.core.error.TranslationException
import lingo.core.model.Language
import lingo.core.net.createHttpClient

/**
 * MyMemory（translated.net）备用通道的网络测试，会实际发起请求。
 *
 * 该接口为免费额度制，额度耗尽时跳过断言并打印提示，
 * 避免误判为代码错误。
 */
class MyMemoryTranslatorTest {

    private val translator = MyMemoryTranslator(createHttpClient())

    @Test
    fun 英文翻成简体中文() = runBlocking {
        val result = try {
            translator.translate(
                text = "Good morning, my friend.",
                source = Language.ENGLISH,
                target = Language.ZH_HANS,
            )
        } catch (e: TranslationException) {
            if (e.message.orEmpty().contains("额度")) {
                println("MyMemory 免费额度已用完，本次跳过断言")
                return@runBlocking
            }
            throw e
        }

        assertTrue(result.text.isNotBlank(), "译文不能是空的")
        assertEquals(MyMemoryTranslator.PROVIDER_NAME, result.providerName)
        assertEquals(Language.ZH_HANS, result.targetLanguage)
    }

    @Test
    fun 自动检测会带回检测到的语言() = runBlocking {
        val result = try {
            translator.translate(
                text = "Bonjour tout le monde, comment allez-vous ?",
                source = Language.AUTO,
                target = Language.ENGLISH,
            )
        } catch (e: TranslationException) {
            if (e.message.orEmpty().contains("额度")) {
                println("MyMemory 免费额度已用完，本次跳过断言")
                return@runBlocking
            }
            throw e
        }

        assertTrue(result.text.isNotBlank())
        assertEquals(Language.FRENCH, result.detectedLanguage)
    }

    @Test
    fun 超过单次长度限制的长文本会自动切段() = runBlocking {
        val longText = buildString {
            repeat(40) { append("This is sentence number ").append(it).append(". ") }
        }
        assertTrue(longText.length > 450, "这段测试文本得真的超长才有意义")

        val result = try {
            translator.translate(
                text = longText,
                source = Language.ENGLISH,
                target = Language.ZH_HANS,
            )
        } catch (e: TranslationException) {
            if (e.message.orEmpty().contains("额度")) {
                println("MyMemory 免费额度已用完，本次跳过断言")
                return@runBlocking
            }
            throw e
        }

        assertTrue(result.text.isNotBlank(), "切段之后也应该有完整译文")
    }
}
