package com.ime.vnkeyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
            fake.ops.last { it.startsWith("replace:") }.substringAfterLast(":"),
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
        assertEquals("replace:4:chà", fake.ops.last { it.startsWith("replace:") })
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
        assertTrue(fake.ops.any { it == "replace:1:" })
    }

    @Test
    fun backspace_empty_is_not_handled_by_pipeline() {
        // Let the platform deliver KEYCODE_DEL — deleteSurroundingText is unreliable in WebViews.
        val (p, fake, _) = newPipeline()
        assertFalse(p.onBackspace(fake))
        assertTrue(fake.ops.isEmpty())
    }
}
