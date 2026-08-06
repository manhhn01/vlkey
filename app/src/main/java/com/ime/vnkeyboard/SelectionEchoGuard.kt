package com.ime.vnkeyboard

object SelectionEchoGuard {
    fun onSelfEdit(count: Int): Int = count + 1

    fun onSelection(count: Int): Pair<Int, Boolean> {
        return if (count > 0) {
            (count - 1) to false
        } else {
            0 to true
        }
    }
}
