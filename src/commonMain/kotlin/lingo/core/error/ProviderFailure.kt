package lingo.core.error

/** 单条通道的失败记录，失败原因会汇总到最终错误信息中。 */
data class ProviderFailure(
    val providerName: String,
    val reason: String,
) {
    override fun toString(): String = "$providerName：$reason"
}
