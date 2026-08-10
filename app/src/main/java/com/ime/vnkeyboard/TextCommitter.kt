package com.ime.vnkeyboard

interface TextCommitter {
    fun beginBatchEdit(): Boolean
    fun endBatchEdit(): Boolean
    fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean
    fun commitText(text: String, newCursorPosition: Int): Boolean

    /**
     * Replace up to [beforeLength] characters before the cursor with [text].
     * Preferred over deleteSurroundingText+commitText on Chrome, where delete is async/no-op.
     */
    fun replaceBeforeCursor(beforeLength: Int, text: String): Boolean
}
