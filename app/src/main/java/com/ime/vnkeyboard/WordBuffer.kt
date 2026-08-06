package com.ime.vnkeyboard

class WordBuffer {
    var raw: String = ""
        private set
    var committedLength: Int = 0

    val isEmpty: Boolean get() = raw.isEmpty()

    fun append(ch: Char) {
        raw += ch
    }

    fun backspaceRaw(): Boolean {
        if (raw.isEmpty()) return false
        raw = raw.dropLast(1)
        return true
    }

    fun clear() {
        raw = ""
        committedLength = 0
    }
}
