package com.ime.vnkeyboard

object SelectionEchoGuard {
    private const val MAX_PENDING_ECHOES = 8

    fun onSelfEdit(count: Int): Int = (count + 1).coerceAtMost(MAX_PENDING_ECHOES)

    fun onSelection(count: Int, isCollapsed: Boolean = true): Pair<Int, Boolean> {
        if (!isCollapsed) return 0 to true
        return if (count > 0) {
            (count - 1) to false
        } else {
            0 to true
        }
    }
}
