package lingo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 将 Material3 默认字号体系的字体替换为打包进产物的思源黑体。
 *
 * WASM 环境没有系统字体，
 * 不显式指定字体时中文会渲染为方框。
 */
internal fun lingoTypography(fontFamily: FontFamily): Typography {
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = base.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = base.displaySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold),
        headlineLarge = base.headlineLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
        headlineMedium = base.headlineMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold),
        headlineSmall = base.headlineSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        titleSmall = base.titleSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        bodyLarge = base.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = base.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = base.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = base.labelLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
        labelMedium = base.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = base.labelSmall.copy(fontFamily = fontFamily, letterSpacing = 0.6.sp),
    )
}

