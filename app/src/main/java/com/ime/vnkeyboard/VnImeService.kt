package com.ime.vnkeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo

class VnImeService : InputMethodService() {
    private val buffer = WordBuffer()
    private val commitManager = CommitManager(buffer)
    private val pipeline = InputPipeline(buffer, commitManager)
    private var suppressSelectionClear = false

    override fun onCreateInputView(): View {
        return View(this)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        pipeline.clear()
    }

    override fun onFinishInput() {
        pipeline.clear()
        super.onFinishInput()
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int,
    ) {
        super.onUpdateSelection(
            oldSelStart,
            oldSelEnd,
            newSelStart,
            newSelEnd,
            candidatesStart,
            candidatesEnd,
        )
        if (!suppressSelectionClear) {
            pipeline.clear()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val action = KeyClassifier.classify(
            event.action,
            event.keyCode,
            event.unicodeChar,
            event.metaState,
        )
        when (action) {
            is KeyAction.Ignore -> return super.onKeyDown(keyCode, event)
            is KeyAction.Shortcut -> {
                pipeline.clear()
                return false
            }
            else -> {
                val committer = currentInputConnection?.let(::InputConnectionCommitter)
                suppressSelectionClear = true
                try {
                    val handled = when (action) {
                        is KeyAction.Letter -> pipeline.onLetter(committer, action.char)
                        is KeyAction.Backspace -> pipeline.onBackspace(committer)
                        is KeyAction.Terminator -> pipeline.onTerminator(committer, action.char)
                        is KeyAction.PassThrough -> pipeline.onPassThrough(committer, action.char)
                        else -> false
                    }
                    return handled || committer == null
                } finally {
                    suppressSelectionClear = false
                }
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val action = KeyClassifier.classify(
            KeyEvent.ACTION_DOWN,
            event.keyCode,
            event.unicodeChar,
            event.metaState,
        )
        return when (action) {
            is KeyAction.Letter,
            is KeyAction.Backspace,
            is KeyAction.Terminator,
            is KeyAction.PassThrough,
            -> true
            else -> super.onKeyUp(keyCode, event)
        }
    }
}
