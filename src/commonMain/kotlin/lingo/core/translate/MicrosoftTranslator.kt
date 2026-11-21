package lingo.core.translate

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import lingo.core.error.TranslationException
import lingo.core.model.Language
import lingo.core.model.TranslationResult
import lingo.core.translate.dto.MicrosoftTranslateDto
class MicrosoftTranslator(
    private val httpClient: HttpClient,
    private val json: Json = DEFAULT_JSON,
) : Translator {

    override val name: String = PROVIDER_NAME

    override suspend fun translate(text: String, source: Language, target: Language): TranslationResult {
        if (text.isBlank()) throw TranslationException("要翻译的内容是空的")
        if (!target.asTarget) throw TranslationException("目标语言不能是「自动检测」")

        val url = buildString {
            append(ENDPOINT)
            append("?from=").append(source.microsoftCode.encodeURLParameter())
            append("&to=").append(target.microsoftCode.encodeURLParameter())
            append("&isEnterpriseClient=false")
        }

        val response = try {
            httpClient.post(url) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(buildJsonArray { add(text) }.toString())
            }
        } catch (e: Throwable) {
            throw TranslationException(message = "请求没发出去（${e.message ?: "网络异常"}）", cause = e)
        }

        val body = response.bodyAsText()
        if (response.status.value != 200) {
            throw TranslationException("微软接口返回 ${response.status.value}：${body.take(160).ifBlank { "空响应" }}")
        }

        val items = try {
            json.decodeFromString<List<MicrosoftTranslateDto>>(body)
        } catch (e: Throwable) {
            throw TranslationException("微软接口返回的内容读不懂：${body.take(160)}", cause = e)
        }

        val item = items.firstOrNull() ?: throw TranslationException("微软接口返回了空数组")
        val translated = item.translations.firstOrNull()?.text?.takeIf { it.isNotBlank() }
            ?: throw TranslationException("微软接口没给出译文：${body.take(160)}")

        return TranslationResult(
            text = translated,
            sourceLanguage = source,
            targetLanguage = target,
            detectedLanguage = if (source.isAuto) {
                Language.fromMicrosoftCodeOrNull(item.detectedLanguage?.language)?.takeIf { !it.isAuto }
            } else {
                source
            },
            providerName = name,
        )
    }
    companion object {
        const val PROVIDER_NAME: String = "Microsoft Service"
        const val ENDPOINT: String = "https://edge.microsoft.com/translate/translatetext"

        private val DEFAULT_JSON = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}
