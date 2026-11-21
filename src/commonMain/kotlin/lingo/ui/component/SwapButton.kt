package lingo.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import lingo.ui.theme.LingoColors

/**
 * 交换按钮：点击后箭头旋转半圈并对调语言。
 * 箭头用 Canvas 绘制，不引入图标库依赖。
 */
@Composable
fun SwapButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var turns by remember { mutableIntStateOf(0) }
    val rotation by animateFloatAsState(targetValue = turns * 180f)

    Box(
        modifier = modifier
            .size(44.dp)
            .graphicsLayer { rotationZ = rotation }
            .clip(CircleShape)
            .background(LingoColors.CardFillStrong)
            .border(width = 1.dp, color = LingoColors.CardBorder, shape = CircleShape)
            .clickable(enabled = enabled) {
                turns += 1
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val stroke = 1.8.dp.toPx()
            val w = size.width
            val h = size.height
            val color = if (enabled) LingoColors.AccentEnd else LingoColors.TextMuted

            // 上：向右的箭头
            drawLine(
                color = color,
                start = Offset(w * 0.12f, h * 0.30f),
                end = Offset(w * 0.88f, h * 0.30f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(w * 0.68f, h * 0.14f),
                end = Offset(w * 0.88f, h * 0.30f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(w * 0.68f, h * 0.46f),
                end = Offset(w * 0.88f, h * 0.30f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )

            // 下：向左的箭头
            drawLine(
                color = color,
                start = Offset(w * 0.88f, h * 0.70f),
                end = Offset(w * 0.12f, h * 0.70f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(w * 0.32f, h * 0.54f),
                end = Offset(w * 0.12f, h * 0.70f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(w * 0.32f, h * 0.86f),
                end = Offset(w * 0.12f, h * 0.70f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}
