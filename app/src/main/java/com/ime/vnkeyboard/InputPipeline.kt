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
        if (buffer.isEmpty) {
            return commitManager.deleteOne(committer)
        }
        val converted = TelexEngine.convert(buffer.raw)
        if (converted.isEmpty()) {
            buffer.clear()
            return commitManager.deleteOne(committer)
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
