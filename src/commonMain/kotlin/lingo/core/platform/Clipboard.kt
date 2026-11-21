package lingo.core.platform

/**
 * 复制文本到系统剪贴板。
 * WASM 下走浏览器的 navigator.clipboard（localhost / https 才可用），
 * JVM 下走 AWT，只用于本地调试。
 */
expect fun copyTextToClipboard(text: String)
