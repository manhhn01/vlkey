package com.ime.vnkeyboard

class CommitManager(private val buffer: WordBuffer) {
    fun replaceWord(committer: TextCommitter?, converted: String): Boolean {
        if (committer == null) {
            buffer.clear()
            return false
        }
        committer.beginBatchEdit()
        return try {
            val before = buffer.committedLength
            if (before > 0 || converted.isNotEmpty()) {
                if (!committer.replaceBeforeCursor(before, converted)) {
                    buffer.clear()
                    return false
                }
            }
            buffer.committedLength = converted.length
            true
        } finally {
            committer.endBatchEdit()
        }
    }

    fun commitRaw(committer: TextCommitter?, text: String): Boolean {
        if (committer == null) {
            buffer.clear()
            return false
        }
        committer.beginBatchEdit()
        return try {
            if (!committer.commitText(text, 1)) {
                buffer.clear()
                return false
            }
            buffer.clear()
            true
        } finally {
            committer.endBatchEdit()
        }
    }

    fun deleteOne(committer: TextCommitter?): Boolean {
        if (committer == null) {
            buffer.clear()
            return false
        }
        committer.beginBatchEdit()
        return try {
            if (!committer.replaceBeforeCursor(1, "")) {
                buffer.clear()
                return false
            }
            buffer.clear()
            true
        } finally {
            committer.endBatchEdit()
        }
    }
}
