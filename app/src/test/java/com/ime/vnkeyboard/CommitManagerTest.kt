package com.ime.vnkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeCommitter : TextCommitter {
    val ops = mutableListOf<String>()
    var failDelete = false
    var failCommit = false

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        ops += "del:$beforeLength:$afterLength"
        return !failDelete
    }

    override fun commitText(text: String, newCursorPosition: Int): Boolean {
        ops += "commit:$text:$newCursorPosition"
        return !failCommit
    }
}

class CommitManagerTest {
    @Test
    fun replaceWord_deletes_then_commits() {
        val buffer = WordBuffer()
        buffer.append('a')
        buffer.committedLength = 1
        val fake = FakeCommitter()
        val cm = CommitManager(buffer)
        assertTrue(cm.replaceWord(fake, "á"))
        assertEquals(listOf("del:1:0", "commit:á:1"), fake.ops)
        assertEquals(1, buffer.committedLength)
    }

    @Test
    fun null_committer_clears_buffer() {
        val buffer = WordBuffer()
        buffer.append('a')
        buffer.committedLength = 1
        val cm = CommitManager(buffer)
        assertFalse(cm.replaceWord(null, "á"))
        assertTrue(buffer.isEmpty)
        assertEquals(0, buffer.committedLength)
    }

    @Test
    fun failed_delete_clears_buffer() {
        val buffer = WordBuffer()
        buffer.append('a')
        buffer.committedLength = 1
        val fake = FakeCommitter().apply { failDelete = true }
        val cm = CommitManager(buffer)
        assertFalse(cm.replaceWord(fake, "á"))
        assertTrue(buffer.isEmpty)
    }

    @Test
    fun commitRaw_and_clear() {
        val buffer = WordBuffer()
        buffer.append('a')
        buffer.committedLength = 1
        val fake = FakeCommitter()
        val cm = CommitManager(buffer)
        assertTrue(cm.commitRaw(fake, " "))
        assertEquals(listOf("commit: :1"), fake.ops)
        assertTrue(buffer.isEmpty)
    }

    @Test
    fun deleteOne_when_empty_buffer() {
        val buffer = WordBuffer()
        val fake = FakeCommitter()
        val cm = CommitManager(buffer)
        assertTrue(cm.deleteOne(fake))
        assertEquals(listOf("del:1:0"), fake.ops)
    }
}
