package lingo.core.net

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

actual fun createHttpClient(): HttpClient = HttpClient(Js) {
    installLingoDefaults()
}
