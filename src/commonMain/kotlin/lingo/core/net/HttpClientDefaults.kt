package lingo.core.net

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpTimeout

/** 所有平台共用的超时设置，避免单个通道阻塞界面。 */
internal fun <T : HttpClientEngineConfig> HttpClientConfig<T>.installLingoDefaults() {
    install(HttpTimeout) {
        requestTimeoutMillis = 20_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 20_000
    }
}
