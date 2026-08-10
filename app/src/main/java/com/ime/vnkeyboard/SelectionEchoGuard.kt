package com.ime.vnkeyboard

/**
 * Suppress buffer clear only for selection updates that still sit on the expected
 * caret after our own replace/commit. Credit drains on matching echoes; a mismatched
 * caret always clears (outranks credit).
 */
object SelectionEchoGuard {
    const val MAX_PENDING_ECHOES = 16
    const val SELF_EDIT_CREDIT = 4

    fun onSelfEdit(count: Int): Int =
        (count + SELF_EDIT_CREDIT).coerceAtMost(MAX_PENDING_ECHOES)

    /**
     * @return Pair(newCredit, shouldClearBuffer)
     */
    fun onSelection(
        count: Int,
        isCollapsed: Boolean,
        matchesExpectedCursor: Boolean,
    ): Pair<Int, Boolean> {
        if (!isCollapsed) {
            return 0 to true
        }
        if (matchesExpectedCursor) {
            return if (count > 0) (count - 1) to false else 0 to false
        }
        // Real caret jump / unknown expected — always clear.
        return 0 to true
    }
}
