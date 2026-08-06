package com.ime.vnkeyboard

class CommitManager(private val buffer: WordBuffer) {
    fun replaceWord(committer: TextCommitter?, converted: String): Boolean {
        if (committer == null) {
            buffer.clear()
            return false
        }
        val before = buffer.committedLength
        if (before > 0 && !committer.deleteSurroundingText(before, 0)) {
            buffer.clear()
            return false
        }
        if (converted.isNotEmpty()) {
            if (!committer.commitText(converted, 1)) {
                buffer.clear()
                return false
            }
        }
        buffer.committedLength = converted.length
        return true
    }

    fun commitRaw(committer: TextCommitter?, text: String): Boolean {
        if (committer == null) {
            buffer.clear()
            return false
        }
        if (!committer.commitText(text, 1)) {
            buffer.clear()
            return false
        }
        buffer.clear()
        return true
    }

    fun deleteOne(committer: TextCommitter?): Boolean {
        if (committer == null) {
            buffer.clear()
            return false
        }
        if (!committer.deleteSurroundingText(1, 0)) {
            buffer.clear()
            return false
        }
        buffer.clear()
        return true
    }
}
