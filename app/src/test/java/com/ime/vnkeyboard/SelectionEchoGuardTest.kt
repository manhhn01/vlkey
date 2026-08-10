package com.ime.vnkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectionEchoGuardTest {
    @Test
    fun matching_expected_cursor_does_not_clear() {
        var count = SelectionEchoGuard.onSelfEdit(0)
        assertEquals(SelectionEchoGuard.SELF_EDIT_CREDIT, count)

        val step = SelectionEchoGuard.onSelection(
            count,
            isCollapsed = true,
            matchesExpectedCursor = true,
        )
        assertEquals(SelectionEchoGuard.SELF_EDIT_CREDIT - 1, step.first)
        assertFalse(step.second)
    }

    @Test
    fun mismatched_caret_clears_even_with_credit() {
        val count = SelectionEchoGuard.onSelfEdit(0)
        val step = SelectionEchoGuard.onSelection(
            count,
            isCollapsed = true,
            matchesExpectedCursor = false,
        )
        assertEquals(0, step.first)
        assertTrue(step.second)
    }

    @Test
    fun expanded_selection_always_clears() {
        val count = SelectionEchoGuard.onSelfEdit(0)
        val step = SelectionEchoGuard.onSelection(
            count,
            isCollapsed = false,
            matchesExpectedCursor = true,
        )
        assertEquals(0, step.first)
        assertTrue(step.second)
    }

    @Test
    fun self_edit_count_is_capped() {
        var count = 0
        repeat(20) {
            count = SelectionEchoGuard.onSelfEdit(count)
        }
        assertEquals(SelectionEchoGuard.MAX_PENDING_ECHOES, count)
    }

    @Test
    fun matching_echoes_drain_credit_then_stay_without_clear() {
        var count = SelectionEchoGuard.onSelfEdit(0)
        repeat(SelectionEchoGuard.SELF_EDIT_CREDIT) {
            val step = SelectionEchoGuard.onSelection(count, true, matchesExpectedCursor = true)
            count = step.first
            assertFalse(step.second)
        }
        val after = SelectionEchoGuard.onSelection(0, true, matchesExpectedCursor = true)
        assertEquals(0, after.first)
        assertFalse(after.second)
    }
}
