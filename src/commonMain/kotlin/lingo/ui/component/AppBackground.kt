package lingo.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import lingo.generated.resources.Res
import lingo.generated.resources.miku
import org.jetbrains.compose.resources.painterResource

/**
 * 页面背景：铺满一张图，再叠一层渐变蒙版压低亮度。
 *
 * 图片本身信息量较大，直接当背景会让卡片上的文字失去对比度，
 * 所以图片整体降低不透明度，蒙版上下更暗、中间稍透，保证中间区域能看出画面。
 */
@Composable
fun AppBackground() {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.miku),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.42f,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0xF00A0E1A),
                        0.45f to Color(0xB3120E28),
                        1.0f to Color(0xF2070D18),
                    ),
                ),
        )
    }
}
