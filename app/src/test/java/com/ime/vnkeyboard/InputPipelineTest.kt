package com.ime.vnkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InputPipelineTest {
    private fun newPipeline(): Triple<InputPipeline, FakeCommitter, WordBuffer> {
        val buffer = WordBuffer()
        val cm = CommitManager(buffer)
        return Triple(InputPipeline(buffer, cm), FakeCommitter(), buffer)
    }

    @Test
    fun types_chao() {
        val (p, fake, buffer) = newPipeline()
        for (ch in "chaof") {
            assertTrue(p.onLetter(fake, ch))
        }
        assertEquals("chaof", buffer.raw)
        assertEquals(
            "chào",
            fake.ops.last { it.startsWith("commit:") }.removePrefix("commit:").substringBefore(":"),
        )
        assertEquals(4, buffer.committedLength)
    }

    @Test
    fun space_commits_and_clears() {
        val (p, fake, buffer) = newPipeline()
        for (ch in "chaof") p.onLetter(fake, ch)
        fake.ops.clear()
        assertTrue(p.onTerminator(fake, ' '))
        assertEquals(listOf("begin", "commit: :1", "end"), fake.ops)
        assertTrue(buffer.isEmpty)
    }

    @Test
    fun backspace_deletes_whole_character_not_just_tone() {
        val (p, fake, buffer) = newPipeline()
        for (ch in "chaof") p.onLetter(fake, ch)
        fake.ops.clear()
        assertTrue(p.onBackspace(fake))
        assertEquals("chà", TelexEngine.convert(buffer.raw))
        assertEquals("commit:chà:1", fake.ops.last { it.startsWith("commit:") })
        assertEquals(3, buffer.committedLength)
    }

    @Test
    fun backspace_on_single_toned_char_clears_word() {
        val (p, fake, buffer) = newPipeline()
        for (ch in "as") p.onLetter(fake, ch)
        fake.ops.clear()
        assertTrue(p.onBackspace(fake))
        assertTrue(buffer.isEmpty)
        assertEquals(0, buffer.committedLength)
        assertTrue(fake.ops.any { it == "del:1:0" })
    }

    @Test
    fun backspace_empty_deletes_one() {
        val (p, fake, _) = newPipeline()
        assertTrue(p.onBackspace(fake))
        assertEquals(listOf("begin", "del:1:0", "end"), fake.ops)
    }
}
