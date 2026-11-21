package lingo.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import lingo.ui.theme.LingoColors

/**
 * 页面背景：竖向渐变 + 三团低透明度径向光斑。
 *
 * 光斑用于衬托玻璃卡片，全部使用 Canvas 绘制，不依赖图片资源。
 * 全部使用 Canvas 绘制，不依赖图片资源。
 */
@Composable
fun AppBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to LingoColors.BackgroundTop,
                    0.45f to LingoColors.BackgroundMid,
                    1.0f to LingoColors.BackgroundBottom,
                ),
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGlow(
                center = Offset(size.width * 0.12f, size.height * 0.06f),
                radius = size.minDimension * 0.85f,
                color = LingoColors.AccentStart.copy(alpha = 0.36f),
            )
            drawGlow(
                center = Offset(size.width * 0.92f, size.height * 0.88f),
                radius = size.minDimension * 0.75f,
                color = LingoColors.AccentEnd.copy(alpha = 0.24f),
            )
            drawGlow(
                center = Offset(size.width * 0.78f, size.height * 0.04f),
                radius = size.minDimension * 0.5f,
                color = LingoColors.AccentWarm.copy(alpha = 0.16f),
            )
        }
    }
}

private fun DrawScope.drawGlow(center: Offset, radius: Float, color: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color, Color.Transparent),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}
