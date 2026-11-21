package lingo.core.model

/**
 * 语言条目：各通道使用的语言代号与界面显示名。
 *
 * 所有通道代号集中在此维护，
 * 避免出现界面可选但接口不认识的情况。
 */
enum class Language(
    /** 微软翻译用的 BCP-47 代号；自动检测时是空字符串 */
    val microsoftCode: String,
    /** MyMemory 用的代号；自动检测时是 Autodetect */
    val myMemoryCode: String,
    /** 中文名，界面主标题 */
    val chineseName: String,
    /** 该语言自己的写法，界面副标题 */
    val nativeName: String,
) {
    /** 只在"源语言"出现，目标语言不允许选它 */
    AUTO("", "Autodetect", "自动检测", "Auto"),

    ZH_HANS("zh-Hans", "zh-CN", "简体中文", "简体中文"),
    ZH_HANT("zh-Hant", "zh-TW", "繁体中文", "繁體中文"),
    ENGLISH("en", "en", "英语", "English"),
    JAPANESE("ja", "ja", "日语", "日本語"),
    KOREAN("ko", "ko", "韩语", "한국어"),
    FRENCH("fr", "fr", "法语", "Français"),
    GERMAN("de", "de", "德语", "Deutsch"),
    SPANISH("es", "es", "西班牙语", "Español"),
    RUSSIAN("ru", "ru", "俄语", "Русский"),
    PORTUGUESE("pt", "pt", "葡萄牙语", "Português"),
    ITALIAN("it", "it", "意大利语", "Italiano"),
    ARABIC("ar", "ar", "阿拉伯语", "العربية");

    val isAuto: Boolean get() = this == AUTO

    /** 可被选为目标语言的列表，AUTO 除外。 */
    val asTarget: Boolean get() = !isAuto

    companion object {
        /** 源语言可选项：自动检测排第一个 */
        val sourceChoices: List<Language> = entries.toList()

        /** 目标语言可选项：去掉自动检测 */
        val targetChoices: List<Language> = entries.filter { it.asTarget }

        /**
         * 把接口返回的语言代号归一成枚举。接口格式不统一：
         * zh-Hans / zh-CN / zh-cn / zh-CHS 都可能出现，所以这里统一小写再比对。
         */
        fun fromMicrosoftCodeOrNull(code: String?): Language? {
            val key = code?.trim()?.lowercase()
            if (key.isNullOrEmpty()) return AUTO
            return when {
                key == "zh" || key.startsWith("zh-hans") || key == "zh-cn" || key == "zh-chs" -> ZH_HANS
                key.startsWith("zh-hant") || key == "zh-tw" || key == "zh-hk" || key == "zh-cht" -> ZH_HANT
                key.startsWith("en") -> ENGLISH
                key.startsWith("ja") -> JAPANESE
                key.startsWith("ko") -> KOREAN
                key.startsWith("fr") -> FRENCH
                key.startsWith("de") -> GERMAN
                key.startsWith("es") -> SPANISH
                key.startsWith("ru") -> RUSSIAN
                key.startsWith("pt") -> PORTUGUESE
                key.startsWith("it") -> ITALIAN
                key.startsWith("ar") -> ARABIC
                else -> entries.firstOrNull { it.microsoftCode.equals(key, ignoreCase = true) }
            }
        }

        /** MyMemory 的方言代号同样要归一 */
        fun fromMyMemoryCodeOrNull(code: String?): Language? {
            val key = code?.trim()?.lowercase()
            if (key.isNullOrEmpty() || key == "autodetect") return AUTO
            return when {
                key.startsWith("zh-cn") || key.startsWith("zh-hans") || key == "zh" -> ZH_HANS
                key.startsWith("zh-tw") || key.startsWith("zh-hk") || key.startsWith("zh-hant") -> ZH_HANT
                else -> entries.firstOrNull { it.myMemoryCode.equals(key, ignoreCase = true) }
            }
        }
    }
}
