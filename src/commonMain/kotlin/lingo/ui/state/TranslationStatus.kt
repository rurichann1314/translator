package lingo.ui.state

import lingo.core.model.TranslationResult

/**
 * 界面阶段，使用密封接口表达互斥状态。
 */
sealed interface TranslationStatus {

    /** 尚未开始，或内容变更后旧结果作废。 */
    data object Idle : TranslationStatus

    /** 请求进行中。 */
    data object Loading : TranslationStatus

    /** 成功，携带完整结果。 */
    data class Success(val result: TranslationResult) : TranslationStatus

    /** 失败：message 面向用户，detail 为各通道的失败原因。 */
    data class Failure(val message: String, val detail: String) : TranslationStatus
}
