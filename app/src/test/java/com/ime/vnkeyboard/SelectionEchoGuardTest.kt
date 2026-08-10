package com.ime.vnkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectionEchoGuardTest {
    @Test
    fun two_self_edits_then_two_selection_updates_do_not_clear() {
        var count = SelectionEchoGuard.onSelfEdit(SelectionEchoGuard.onSelfEdit(0))

        val first = SelectionEchoGuard.onSelection(count)
        count = first.first
        val second = SelectionEchoGuard.onSelection(count)

        assertEquals(1, first.first)
        assertFalse(first.second)
        assertEquals(0, second.first)
        assertFalse(second.second)
    }

    @Test
    fun selection_update_with_no_self_edit_clears() {
        val result = SelectionEchoGuard.onSelection(0)

        assertEquals(0, result.first)
        assertTrue(result.second)
    }

    @Test
    fun self_edit_count_is_capped() {
        var count = 0
        repeat(20) {
            count = SelectionEchoGuard.onSelfEdit(count)
        }

        assertEquals(8, count)
    }

    @Test
    fun expanded_selection_always_clears_pending_echoes() {
        val result = SelectionEchoGuard.onSelection(3, isCollapsed = false)

        assertEquals(0, result.first)
        assertTrue(result.second)
    }
}
