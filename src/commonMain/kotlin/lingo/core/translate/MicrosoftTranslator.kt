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

/**
 * 微软翻译免费通道（Edge 浏览器内置翻译的底层引擎）。
 *
 * 该接口无需注册账号，直接 POST 一个 JSON 字符串数组即可。
 * 旧版 edge.microsoft.com/translate/auth 取临时 token 的写法
 * 已退役（返回 404），当前版本不需要 token。
 *
 * 响应头包含 Access-Control-Allow-Origin: *，
 * 浏览器中的 WASM 可直接调用，无需自建后端。
 */
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
            // 自动检测时 from 传空字符串；传 "auto" 接口会直接 400
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
        const val PROVIDER_NAME: String = "微软翻译"

        /** 微软的免费翻译端点（原 token 端点已退役） */
        const val ENDPOINT: String = "https://edge.microsoft.com/translate/translatetext"

        private val DEFAULT_JSON = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}
