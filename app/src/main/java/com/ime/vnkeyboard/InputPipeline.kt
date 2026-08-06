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
        if (!buffer.backspaceRaw()) {
            return commitManager.deleteOne(committer)
        }
        val converted = TelexEngine.convert(buffer.raw)
        return commitManager.replaceWord(committer, converted)
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
