package com.ime.vnkeyboard

sealed class KeyAction {
    data class Letter(val char: Char) : KeyAction()
    object Backspace : KeyAction()
    data class Terminator(val char: Char) : KeyAction()
    data class PassThrough(val char: Char) : KeyAction()
    object Shortcut : KeyAction()
    object SwitchIme : KeyAction()
    object PlatformKey : KeyAction()
    /** Caret/navigation keys — clear word buffer, then let the platform handle. */
    object Navigate : KeyAction()
    object Ignore : KeyAction()
}

object KeyClassifier {
    private const val ACTION_DOWN = 0
    private const val KEYCODE_DPAD_UP = 19
    private const val KEYCODE_DPAD_DOWN = 20
    private const val KEYCODE_DPAD_LEFT = 21
    private const val KEYCODE_DPAD_RIGHT = 22
    private const val KEYCODE_TAB = 61
    private const val KEYCODE_SPACE = 62
    private const val KEYCODE_ENTER = 66
    private const val KEYCODE_DEL = 67
    private const val KEYCODE_ESCAPE = 111
    private const val KEYCODE_FORWARD_DEL = 112
    private const val KEYCODE_PAGE_UP = 92
    private const val KEYCODE_PAGE_DOWN = 93
    private const val KEYCODE_MOVE_HOME = 122
    private const val KEYCODE_MOVE_END = 123
    private const val META_CTRL_ON = 0x1000
    private const val META_ALT_ON = 0x02
    private const val META_META_ON = 0x10000

    private val terminators = setOf(' ', '.', ',', ':', ';', '!', '?', ')', ']', '}')

    private val navigateKeyCodes = setOf(
        KEYCODE_DPAD_UP,
        KEYCODE_DPAD_DOWN,
        KEYCODE_DPAD_LEFT,
        KEYCODE_DPAD_RIGHT,
        KEYCODE_PAGE_UP,
        KEYCODE_PAGE_DOWN,
        KEYCODE_MOVE_HOME,
        KEYCODE_MOVE_END,
        KEYCODE_ESCAPE,
        KEYCODE_FORWARD_DEL,
    )

    fun classify(action: Int, keyCode: Int, unicodeChar: Int, metaState: Int): KeyAction {
        if (action != ACTION_DOWN) return KeyAction.Ignore
        val ctrlHeld = metaState and META_CTRL_ON != 0
        if (ctrlHeld && keyCode == KEYCODE_SPACE) return KeyAction.SwitchIme
        val shortcutMeta = META_CTRL_ON or META_ALT_ON or META_META_ON
        if (metaState and shortcutMeta != 0) return KeyAction.Shortcut
        if (keyCode == KEYCODE_DEL) return KeyAction.Backspace
        if (keyCode == KEYCODE_ENTER || keyCode == KEYCODE_TAB) return KeyAction.PlatformKey
        if (keyCode in navigateKeyCodes) return KeyAction.Navigate
        if (unicodeChar == 0) return KeyAction.Ignore

        val char = unicodeChar.toChar()
        if (char == '\n' || char == '\t') return KeyAction.PlatformKey
        if (char in terminators) return KeyAction.Terminator(char)
        if (char.isLetter() && char.code < 128) return KeyAction.Letter(char)
        return if (char.isISOControl()) KeyAction.Ignore else KeyAction.PassThrough(char)
    }
}
