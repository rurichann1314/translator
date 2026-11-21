package lingo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.delay
import lingo.generated.resources.NotoSansSC
import lingo.generated.resources.Res
import lingo.ui.component.SplashScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.getFontResourceBytes
import org.jetbrains.compose.resources.rememberResourceEnvironment

/**
 * 在渲染界面前先加载思源黑体。
 *
 * WASM 环境没有系统字体，字体在运行时异步下载。
 * Compose 的 Font(resource) 在下载完成前会返回内置拉丁字体（Roboto），
 * 此时中文会显示为方框。
 * 这里先读取字体字节（浏览器随后会缓存），再渲染界面。
 * 启动阶段显示一致的背景画面，避免首帧出现方框。
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun FontGate(content: @Composable (FontFamily) -> Unit) {
    val environment = rememberResourceEnvironment()
    var bytesLoaded by remember { mutableStateOf(false) }
    var splashDone by remember { mutableStateOf(false) }

    LaunchedEffect(environment) {
        // 读取失败时继续渲染，避免白屏
        runCatching { getFontResourceBytes(environment, Res.font.NotoSansSC) }
        bytesLoaded = true
    }

    // 字体字节已在浏览器缓存中，
    // 这里留出短暂过渡，覆盖字体解码与首帧排版耗时。
    LaunchedEffect(bytesLoaded) {
        if (bytesLoaded) {
            delay(450)
            splashDone = true
        }
    }

    if (!splashDone) {
        SplashScreen()
        return
    }

    val font = Font(Res.font.NotoSansSC)
    val family = remember(font) { FontFamily(font) }
    content(family)
}
