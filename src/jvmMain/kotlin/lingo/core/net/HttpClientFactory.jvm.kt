package lingo.core.net

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

actual fun createHttpClient(): HttpClient = HttpClient(CIO) {
    installLingoDefaults()
}
