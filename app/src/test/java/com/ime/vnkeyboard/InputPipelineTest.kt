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
        assertEquals("chào", fake.ops.last().removePrefix("commit:").substringBefore(":"))
        assertEquals(4, buffer.committedLength)
    }

    @Test
    fun space_commits_and_clears() {
        val (p, fake, buffer) = newPipeline()
        for (ch in "chaof") p.onLetter(fake, ch)
        fake.ops.clear()
        assertTrue(p.onTerminator(fake, ' '))
        assertEquals(listOf("commit: :1"), fake.ops)
        assertTrue(buffer.isEmpty)
    }

    @Test
    fun backspace_rebuilds() {
        val (p, fake, buffer) = newPipeline()
        for (ch in "chaof") p.onLetter(fake, ch)
        fake.ops.clear()
        assertTrue(p.onBackspace(fake))
        assertEquals("chao", buffer.raw)
        assertEquals("commit:chao:1", fake.ops.last())
    }

    @Test
    fun backspace_empty_deletes_one() {
        val (p, fake, _) = newPipeline()
        assertTrue(p.onBackspace(fake))
        assertEquals(listOf("del:1:0"), fake.ops)
    }
}
