package com.ime.vnkeyboard

class InputPipeline(
    val buffer: WordBuffer,
    private val commitManager: CommitManager,
) {
    fun onLetter(committer: TextCommitter?, ch: Char): Boolean {
        buffer.append(ch)
        val converted = TelexEngine.convert(buffer.raw)
        return commitManager.replaceWord(committer, converted)
    }

    fun onBackspace(committer: TextCommitter?): Boolean {
        // Empty word buffer: do not handle — VnImeService returns false so the platform
        // processes KEYCODE_DEL. deleteSurroundingText(1) is flaky in many WebViews.
        if (buffer.isEmpty) {
            return false
        }
        val converted = TelexEngine.convert(buffer.raw)
        if (converted.isEmpty()) {
            buffer.clear()
            return false
        }
        // Delete one display character (e.g. á → gone), not just the tone key (á → a).
        val target = converted.dropLast(1)
        buffer.setRaw(TelexEngine.toTelexRaw(target))
        return commitManager.replaceWord(committer, target)
    }

    fun onTerminator(committer: TextCommitter?, ch: Char): Boolean {
        return commitManager.commitRaw(committer, ch.toString())
    }

    fun onPassThrough(committer: TextCommitter?, ch: Char): Boolean {
        return commitManager.commitRaw(committer, ch.toString())
    }

    fun clear() {
        buffer.clear()
    }
}
