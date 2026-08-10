package com.ime.vnkeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import kotlin.math.max

class VnImeService : InputMethodService() {
    private val buffer = WordBuffer()
    private val commitManager = CommitManager(buffer)
    private val pipeline = InputPipeline(buffer, commitManager)
    private var suppressSelectionCount = 0
    private var expectedCursor: Int? = null
    private val consumedKeyCodes = ConsumedKeyCodes()

    override fun onCreateInputView(): View {
        return View(this)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        clearWordState()
        consumedKeyCodes.clear()
    }

    override fun onFinishInput() {
        clearWordState()
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
        val collapsed = newSelStart == newSelEnd
        // Empty buffer: nothing to protect. Non-empty: only trust echoes at expectedCaret.
        // If we could not read a caret after commit, fall back to echo credit briefly.
        val matches = when {
            buffer.isEmpty -> true
            !collapsed -> false
            expectedCursor != null -> newSelStart == expectedCursor
            else -> suppressSelectionCount > 0
        }
        val (newCount, shouldClear) = SelectionEchoGuard.onSelection(
            suppressSelectionCount,
            isCollapsed = collapsed,
            matchesExpectedCursor = matches,
        )
        suppressSelectionCount = newCount
        if (shouldClear) {
            clearWordState()
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
            is KeyAction.Navigate -> {
                clearWordState()
                return false
            }
            is KeyAction.Shortcut -> {
                clearWordState()
                return false
            }
            is KeyAction.SwitchIme -> {
                clearWordState()
                switchToNextInputMethod(false)
                if (event.repeatCount == 0) {
                    consumedKeyCodes.add(keyCode)
                }
                return true
            }
            is KeyAction.PlatformKey -> {
                clearWordState()
                return false
            }
            else -> {
                val committer = currentInputConnection?.let(::InputConnectionCommitter)
                if (committer == null) {
                    clearWordState()
                    return false
                }
                // No active Telex word: let the system handle Backspace normally.
                if (action is KeyAction.Backspace && buffer.isEmpty) {
                    return false
                }
                val handled = when (action) {
                    is KeyAction.Letter -> pipeline.onLetter(committer, action.char)
                    is KeyAction.Backspace -> pipeline.onBackspace(committer)
                    is KeyAction.Terminator -> pipeline.onTerminator(committer, action.char)
                    is KeyAction.PassThrough -> pipeline.onPassThrough(committer, action.char)
                    else -> false
                }
                if (!handled) {
                    if (action is KeyAction.Backspace) {
                        return false
                    }
                    if (event.repeatCount == 0) {
                        consumedKeyCodes.add(keyCode)
                    }
                    return true
                }
                if (action is KeyAction.Letter || action is KeyAction.Backspace) {
                    expectedCursor = readCursorPosition()
                    suppressSelectionCount =
                        SelectionEchoGuard.onSelfEdit(suppressSelectionCount)
                } else {
                    // Terminator / pass-through end the word.
                    expectedCursor = null
                    suppressSelectionCount = 0
                }
                if (event.repeatCount == 0) {
                    consumedKeyCodes.add(keyCode)
                }
                return true
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (consumedKeyCodes.remove(keyCode)) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun clearWordState() {
        pipeline.clear()
        suppressSelectionCount = 0
        expectedCursor = null
    }

    private fun readCursorPosition(): Int? {
        val ic = currentInputConnection ?: return null
        // Length of text before caret == absolute caret index in most editors.
        val before = ic.getTextBeforeCursor(100_000, 0)
        if (before != null) return before.length
        val extracted = ic.getExtractedText(ExtractedTextRequest(), 0) ?: return null
        if (extracted.selectionStart < 0 || extracted.selectionEnd < 0) return null
        return max(extracted.selectionStart, extracted.selectionEnd)
    }
}
