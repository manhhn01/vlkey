package com.ime.vnkeyboard

import android.view.KeyEvent
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import kotlin.math.max

class InputConnectionCommitter(
    private val inputConnection: InputConnection,
) : TextCommitter {
    override fun beginBatchEdit(): Boolean {
        return inputConnection.beginBatchEdit()
    }

    override fun endBatchEdit(): Boolean {
        return inputConnection.endBatchEdit()
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        return inputConnection.deleteSurroundingText(beforeLength, afterLength)
    }

    override fun commitText(text: String, newCursorPosition: Int): Boolean {
        return inputConnection.commitText(text, newCursorPosition)
    }

    override fun replaceBeforeCursor(beforeLength: Int, text: String): Boolean {
        if (beforeLength <= 0) {
            return text.isEmpty() || inputConnection.commitText(text, 1)
        }

        // 1) Select prior chars + commitText (most reliable in Chrome).
        if (replaceViaSelection(beforeLength, text)) {
            return true
        }

        val before = inputConnection.getTextBeforeCursor(beforeLength, 0)
        val deleteLen = before?.length ?: beforeLength

        // 2) deleteSurroundingText + commit
        if (deleteLen > 0) {
            inputConnection.deleteSurroundingText(deleteLen, 0)
            val still = inputConnection.getTextBeforeCursor(deleteLen, 0)
            val deleted =
                before == null || still == null || still.toString() != before.toString()
            if (deleted) {
                return text.isEmpty() || inputConnection.commitText(text, 1)
            }
            // 3) Chrome no-op delete: synthesize DEL key events into the editor.
            if (!deleteViaKeyEvents(deleteLen)) {
                return false
            }
        }
        return text.isEmpty() || inputConnection.commitText(text, 1)
    }

    private fun replaceViaSelection(beforeLength: Int, text: String): Boolean {
        val extracted = inputConnection.getExtractedText(ExtractedTextRequest(), 0) ?: return false
        if (extracted.selectionStart < 0 || extracted.selectionEnd < 0) return false
        val cursor = max(extracted.selectionStart, extracted.selectionEnd)
        val start = max(0, cursor - beforeLength)
        if (!inputConnection.setSelection(start, cursor)) {
            return false
        }
        return inputConnection.commitText(text, 1)
    }

    private fun deleteViaKeyEvents(count: Int): Boolean {
        repeat(count) {
            val down = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
            val up = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
            if (!inputConnection.sendKeyEvent(down) || !inputConnection.sendKeyEvent(up)) {
                return false
            }
        }
        return true
    }
}
