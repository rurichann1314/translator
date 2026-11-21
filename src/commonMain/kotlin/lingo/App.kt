package lingo

import androidx.compose.runtime.Composable
import lingo.ui.screen.TranslatorScreen
import lingo.ui.state.rememberTranslatorStore
import lingo.ui.theme.FontGate
import lingo.ui.theme.LingoTheme

/**
 * 应用根组件：字体闸门 → 主题 → 页面。
 *
 * 主题字体必须使用 FontGate 加载完成的那一份，
 * 否则首帧中文会显示为方框。
 */
@Composable
fun App() {
    FontGate { fontFamily ->
        LingoTheme(fontFamily = fontFamily) {
            TranslatorScreen(store = rememberTranslatorStore())
        }
    }
}
