package lingo.core.net

import io.ktor.client.HttpClient

/**
 * 平台相关的 HTTP 客户端。
 * WASM 下用浏览器自带的 fetch，JVM 下用 CIO（只为了跑测试）。
 */
expect fun createHttpClient(): HttpClient
