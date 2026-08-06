package com.ime.vnkeyboard

sealed class KeyAction {
    data class Letter(val char: Char) : KeyAction()
    object Backspace : KeyAction()
    data class Terminator(val char: Char) : KeyAction()
    data class PassThrough(val char: Char) : KeyAction()
    object Shortcut : KeyAction()
    object Ignore : KeyAction()
}

object KeyClassifier {
    private const val ACTION_DOWN = 0
    private const val KEYCODE_DEL = 67
    private const val META_CTRL_ON = 0x1000
    private const val META_ALT_ON = 0x02
    private const val META_META_ON = 0x10000

    private val terminators = setOf(' ', '\n', '\t', '.', ',', ':', ';', '!', '?', ')', ']', '}')

    fun classify(action: Int, keyCode: Int, unicodeChar: Int, metaState: Int): KeyAction {
        if (action != ACTION_DOWN) return KeyAction.Ignore
        val shortcutMeta = META_CTRL_ON or META_ALT_ON or META_META_ON
        if (metaState and shortcutMeta != 0) return KeyAction.Shortcut
        if (keyCode == KEYCODE_DEL) return KeyAction.Backspace
        if (unicodeChar == 0) return KeyAction.Ignore

        val char = unicodeChar.toChar()
        if (char in terminators) return KeyAction.Terminator(char)
        if (char.isLetter() && char.code < 128) return KeyAction.Letter(char)
        return KeyAction.PassThrough(char)
    }
}
