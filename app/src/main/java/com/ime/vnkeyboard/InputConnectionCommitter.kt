package com.ime.vnkeyboard

import android.view.inputmethod.InputConnection

class InputConnectionCommitter(
    private val inputConnection: InputConnection,
) : TextCommitter {
    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        return inputConnection.deleteSurroundingText(beforeLength, afterLength)
    }

    override fun commitText(text: String, newCursorPosition: Int): Boolean {
        return inputConnection.commitText(text, newCursorPosition)
    }
}
