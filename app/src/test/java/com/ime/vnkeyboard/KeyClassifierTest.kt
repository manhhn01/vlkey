package com.ime.vnkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyClassifierTest {
    private val DOWN = 0
    private val CTRL = 0x1000

    @Test
    fun letter() {
        assertEquals(KeyAction.Letter('a'), KeyClassifier.classify(DOWN, 29, 'a'.code, 0))
    }

    @Test
    fun backspace() {
        assertEquals(KeyAction.Backspace, KeyClassifier.classify(DOWN, 67, 0, 0))
    }

    @Test
    fun space_terminator() {
        assertEquals(KeyAction.Terminator(' '), KeyClassifier.classify(DOWN, 62, ' '.code, 0))
    }

    @Test
    fun ctrl_shortcut() {
        assertEquals(KeyAction.Shortcut, KeyClassifier.classify(DOWN, 31, 'c'.code, CTRL))
    }

    @Test
    fun punct_terminator() {
        assertEquals(KeyAction.Terminator('.'), KeyClassifier.classify(DOWN, 56, '.'.code, 0))
    }

    @Test
    fun enter_and_tab_are_platform_keys() {
        assertEquals(KeyAction.PlatformKey, KeyClassifier.classify(DOWN, 66, '\n'.code, 0))
        assertEquals(KeyAction.PlatformKey, KeyClassifier.classify(DOWN, 61, '\t'.code, 0))
        assertEquals(KeyAction.PlatformKey, KeyClassifier.classify(DOWN, 66, 0, 0))
        assertEquals(KeyAction.PlatformKey, KeyClassifier.classify(DOWN, 61, 0, 0))
    }

    @Test
    fun digit_passthrough() {
        assertEquals(KeyAction.PassThrough('1'), KeyClassifier.classify(DOWN, 8, '1'.code, 0))
    }

    @Test
    fun non_printable_control_ignored() {
        assertEquals(KeyAction.Ignore, KeyClassifier.classify(DOWN, 0, '\u0001'.code, 0))
    }

    @Test
    fun action_up_ignored() {
        assertTrue(KeyClassifier.classify(1, 29, 'a'.code, 0) is KeyAction.Ignore)
    }
}
