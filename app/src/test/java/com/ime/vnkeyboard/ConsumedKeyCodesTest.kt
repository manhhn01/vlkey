package com.ime.vnkeyboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsumedKeyCodesTest {
    @Test
    fun overlapping_key_downs_consume_each_matching_key_up() {
        val keys = ConsumedKeyCodes()

        keys.add(29)
        keys.add(30)

        assertTrue(keys.remove(29))
        assertTrue(keys.remove(30))
        assertFalse(keys.remove(29))
    }

    @Test
    fun repeated_key_code_tracks_each_key_down() {
        val keys = ConsumedKeyCodes()

        keys.add(29)
        keys.add(29)

        assertTrue(keys.remove(29))
        assertTrue(keys.remove(29))
        assertFalse(keys.remove(29))
    }

    @Test
    fun clear_discards_all_consumed_key_downs() {
        val keys = ConsumedKeyCodes()
        keys.add(29)
        keys.add(30)

        keys.clear()

        assertFalse(keys.remove(29))
        assertFalse(keys.remove(30))
    }
}
