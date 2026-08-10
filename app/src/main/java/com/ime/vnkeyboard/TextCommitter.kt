package com.ime.vnkeyboard

interface TextCommitter {
    fun beginBatchEdit(): Boolean
    fun endBatchEdit(): Boolean
    fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean
    fun commitText(text: String, newCursorPosition: Int): Boolean
}
