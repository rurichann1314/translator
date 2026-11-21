package lingo.core.error

/**
 * 翻译失败时抛出的统一异常。
 *
 * [failures] 记录每条通道各自的失败原因，
 * 界面可据此展示可排查的明细，例如
 * 微软返回 400、MyMemory 超时。
 */
class TranslationException(
    message: String,
    val failures: List<ProviderFailure> = emptyList(),
    cause: Throwable? = null,
) : Exception(message, cause) {

    /** 给界面用的多行错误详情 */
    fun detailText(): String =
        if (failures.isEmpty()) {
            message.orEmpty()
        } else {
            failures.joinToString(separator = "\n")
        }
}
