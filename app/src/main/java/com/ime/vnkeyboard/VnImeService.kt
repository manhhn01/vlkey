package com.ime.vnkeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo

class VnImeService : InputMethodService() {
    private val buffer = WordBuffer()
    private val commitManager = CommitManager(buffer)
    private val pipeline = InputPipeline(buffer, commitManager)
    private var suppressSelectionCount = 0
    private val consumedKeyCodes = ConsumedKeyCodes()

    override fun onCreateInputView(): View {
        return View(this)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        pipeline.clear()
        suppressSelectionCount = 0
        consumedKeyCodes.clear()
    }

    override fun onFinishInput() {
        pipeline.clear()
        suppressSelectionCount = 0
        consumedKeyCodes.clear()
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
        val (newCount, shouldClear) = SelectionEchoGuard.onSelection(suppressSelectionCount)
        suppressSelectionCount = newCount
        if (shouldClear) {
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
                val handled = when (action) {
                    is KeyAction.Letter -> pipeline.onLetter(committer, action.char)
                    is KeyAction.Backspace -> pipeline.onBackspace(committer)
                    is KeyAction.Terminator -> pipeline.onTerminator(committer, action.char)
                    is KeyAction.PassThrough -> pipeline.onPassThrough(committer, action.char)
                    else -> false
                }
                if (handled && committer != null) {
                    suppressSelectionCount =
                        SelectionEchoGuard.onSelfEdit(suppressSelectionCount)
                }
                val consumed = handled || committer == null
                if (consumed && event.repeatCount == 0) {
                    consumedKeyCodes.add(keyCode)
                }
                return consumed
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (consumedKeyCodes.remove(keyCode)) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }
}
