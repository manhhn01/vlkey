package com.ime.vnkeyboard

class ConsumedKeyCodes {
    private val counts = mutableMapOf<Int, Int>()

    fun add(keyCode: Int) {
        counts[keyCode] = counts.getOrDefault(keyCode, 0) + 1
    }

    fun remove(keyCode: Int): Boolean {
        val count = counts[keyCode] ?: return false
        if (count == 1) {
            counts.remove(keyCode)
        } else {
            counts[keyCode] = count - 1
        }
        return true
    }

    fun clear() {
        counts.clear()
    }
}
