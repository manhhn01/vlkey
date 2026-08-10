package com.ime.vnkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordBufferTest {
    @Test
    fun append_and_track_committed_length() {
        val b = WordBuffer()
        b.append('c')
        b.append('h')
        b.committedLength = 2
        assertEquals("ch", b.raw)
        assertEquals(2, b.committedLength)
    }

    @Test
    fun backspace_pops_raw() {
        val b = WordBuffer()
        b.append('a')
        b.append('s')
        assertTrue(b.backspaceRaw())
        assertEquals("a", b.raw)
    }

    @Test
    fun backspace_empty_returns_false() {
        val b = WordBuffer()
        assertFalse(b.backspaceRaw())
        assertTrue(b.isEmpty)
    }

    @Test
    fun clear_resets_all() {
        val b = WordBuffer()
        b.append('a')
        b.committedLength = 1
        b.clear()
        assertEquals("", b.raw)
        assertEquals(0, b.committedLength)
    }
}
