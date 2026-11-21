package lingo.core.platform

/**
 * 浏览器剪贴板实现。
 * navigator.clipboard 仅在 localhost / https 下可用，失败时静默忽略。
 * @JsFun 位于 kotlin 包，无需 import。
 */
@JsFun("(text) => { try { navigator.clipboard.writeText(text); return true; } catch (e) { return false; } }")
private external fun writeToBrowserClipboard(text: String): Boolean

actual fun copyTextToClipboard(text: String) {
    runCatching { writeToBrowserClipboard(text) }
}
