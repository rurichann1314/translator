package lingo.core.translate

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import lingo.core.error.TranslationException
import lingo.core.model.Language
import lingo.core.model.TranslationResult

/**
 * 编排逻辑测试：不联网，用假通道验证"微软优先、失败自动兜底"这套规则。
 *
 * [TranslationService] 只依赖 [Translator] 接口，
 * 因此可离线测试。
 */
class TranslationServiceTest {

    private fun fakeTranslator(
        channelName: String,
        behavior: suspend () -> TranslationResult,
    ): Translator = object : Translator {
        override val name: String = channelName
        override suspend fun translate(text: String, source: Language, target: Language): TranslationResult =
            behavior()
    }

    private fun success(text: String, channelName: String) = TranslationResult(
        text = text,
        sourceLanguage = Language.AUTO,
        targetLanguage = Language.ZH_HANS,
        detectedLanguage = Language.ENGLISH,
        providerName = channelName,
    )

    @Test
    fun 第一条通道成功时不会去碰第二条() = runBlocking {
        var secondCalled = false
        val service = TranslationService(
            listOf(
                fakeTranslator("通道A") { success("你好", "通道A") },
                fakeTranslator("通道B") {
                    secondCalled = true
                    success("你好", "通道B")
                },
            ),
        )

        val result = service.translate("hello", Language.AUTO, Language.ZH_HANS)

        assertEquals("你好", result.text)
        assertEquals("通道A", result.providerName)
        assertFalse(secondCalled, "第一条就成功了，第二条不该被调用")
    }

    @Test
    fun 第一条挂了会自动退到第二条() = runBlocking {
        val service = TranslationService(
            listOf(
                fakeTranslator("通道A") { throw TranslationException("接口 400") },
                fakeTranslator("通道B") { success("兜底译文", "通道B") },
            ),
        )

        val result = service.translate("hello", Language.AUTO, Language.ZH_HANS)

        assertEquals("兜底译文", result.text)
        assertEquals("通道B", result.providerName)
    }

    @Test
    fun 全部通道都挂了会抛出带明细的异常() = runBlocking {
        val service = TranslationService(
            listOf(
                fakeTranslator("通道A") { throw TranslationException("接口 400") },
                fakeTranslator("通道B") { throw TranslationException("超时") },
            ),
        )

        val error = assertFailsWith<TranslationException> {
            service.translate("hello", Language.AUTO, Language.ZH_HANS)
        }

        assertEquals(2, error.failures.size)
        assertTrue(error.detailText().contains("通道A"))
        assertTrue(error.detailText().contains("超时"))
    }

    @Test
    fun 空文本会被直接拒绝且不调用通道() = runBlocking {
        var called = false
        val service = TranslationService(
            listOf(
                fakeTranslator("通道A") {
                    called = true
                    success("不该出现", "通道A")
                },
            ),
        )

        assertFailsWith<TranslationException> {
            service.translate("   ", Language.AUTO, Language.ZH_HANS)
        }
        assertFalse(called, "空文本不该真的发请求")
    }

    @Test
    fun 成功结果里会带上耗时和通道名() = runBlocking {
        val service = TranslationService(
            listOf(
                fakeTranslator("通道A") {
                    kotlinx.coroutines.delay(20)
                    success("慢一点也没关系", "通道A")
                },
            ),
        )

        val result = service.translate("hello", Language.AUTO, Language.ZH_HANS)

        assertEquals("通道A", result.providerName)
        assertTrue(result.elapsedMillis >= 0L)
    }
}
