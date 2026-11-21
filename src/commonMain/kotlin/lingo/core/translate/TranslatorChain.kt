package lingo.core.translate

import io.ktor.client.HttpClient

/**
 * 默认通道顺序：微软在前，MyMemory 兜底。
 *
 * 新增通道只需实现 [Translator] 并加入列表，
 * 界面层无需改动。
 */
fun defaultTranslators(httpClient: HttpClient): List<Translator> = listOf(
    MicrosoftTranslator(httpClient),
    MyMemoryTranslator(httpClient),
)
