package lingo.core.platform

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual fun copyTextToClipboard(text: String) {
    runCatching {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }
}
