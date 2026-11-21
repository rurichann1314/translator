package lingo.core.translate

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import lingo.core.error.TranslationException
import lingo.core.model.Language
import lingo.core.model.TranslationResult
class MyMemoryTranslator(
    private val httpClient: HttpClient,
    private val json: Json = DEFAULT_JSON,
) : Translator {

    override val name: String = PROVIDER_NAME

    override suspend fun translate(text: String, source: Language, target: Language): TranslationResult {
        if (text.isBlank()) throw TranslationException("要翻译的内容是空的")
        if (!target.asTarget) throw TranslationException("目标语言不能是「自动检测」")

        val chunks = splitByLimit(text, MAX_CHUNK_LENGTH)
        val pieces = mutableListOf<String>()
        var detected: Language? = null

        for (chunk in chunks) {
            val piece = translateChunk(chunk, source, target)
            pieces += piece.translatedText
            if (detected == null) detected = piece.detectedLanguage
        }

        return TranslationResult(
            text = pieces.joinToString(separator = " "),
            sourceLanguage = source,
            targetLanguage = target,
            detectedLanguage = if (source.isAuto) detected else source,
            providerName = name,
        )
    }

    private suspend fun translateChunk(
        text: String,
        source: Language,
        target: Language,
    ): ChunkResult {
        val url = URLBuilder(ENDPOINT).apply {
            parameters.append("q", text)
            parameters.append("langpair", "${source.myMemoryCode}|${target.myMemoryCode}")
        }.buildString()

        val response = try {
            httpClient.get(url)
        } catch (e: Throwable) {
            throw TranslationException(message = "请求没发出去（${e.message ?: "网络异常"}）", cause = e)
        }

        val body = response.bodyAsText()
        if (response.status.value != 200) {
            throw TranslationException("MyMemory 返回 ${response.status.value}：${body.take(160)}")
        }

        val root = try {
            json.parseToJsonElement(body).jsonObject
        } catch (e: Throwable) {
            throw TranslationException("MyMemory 返回的内容读不懂：${body.take(160)}", cause = e)
        }

        val data: JsonObject? = root["responseData"]?.jsonObject
        val translated = data?.get("translatedText")?.jsonPrimitive?.content?.trim().orEmpty()

        if (translated.isEmpty()) {
            val detail = root["responseDetails"]?.jsonPrimitive?.content.orEmpty()
            throw TranslationException("MyMemory 没给出译文：${detail.take(160)}")
        }
        if (translated.contains("MYMEMORY WARNING", ignoreCase = true)) {
            throw TranslationException("MyMemory 今天的免费额度用完了")
        }

        return ChunkResult(
            translatedText = translated,
            detectedLanguage = Language.fromMyMemoryCodeOrNull(
                data?.get("detectedLanguage")?.jsonPrimitive?.content,
            )?.takeIf { !it.isAuto },
        )
    }
    private fun splitByLimit(text: String, limit: Int): List<String> {
        if (text.length <= limit) return listOf(text)

        val sentences = text.split('\n', '。', '！', '？', '.', '!', '?')
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val result = mutableListOf<String>()
        val buffer = StringBuilder()
        for (sentence in sentences) {
            if (sentence.length > limit) {
                if (buffer.isNotEmpty()) {
                    result += buffer.toString()
                    buffer.clear()
                }
                sentence.chunked(limit).forEach { result += it }
                continue
            }
            if (buffer.length + sentence.length + 1 > limit) {
                result += buffer.toString()
                buffer.clear()
            }
            if (buffer.isNotEmpty()) buffer.append(' ')
            buffer.append(sentence)
        }
        if (buffer.isNotEmpty()) result += buffer.toString()
        return result
    }

    private data class ChunkResult(
        val translatedText: String,
        val detectedLanguage: Language?,
    )

    companion object {
        const val PROVIDER_NAME: String = "出品"
        const val ENDPOINT: String = "https://api.mymemory.translated.net/get"

        private const val MAX_CHUNK_LENGTH = 450

        private val DEFAULT_JSON = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}
