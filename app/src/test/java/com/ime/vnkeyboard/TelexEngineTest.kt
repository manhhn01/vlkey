package com.ime.vnkeyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class TelexEngineTest {
    @Test
    fun diacritics() {
        assertEquals("â", TelexEngine.convert("aa"))
        assertEquals("ă", TelexEngine.convert("aw"))
        assertEquals("ê", TelexEngine.convert("ee"))
        assertEquals("ô", TelexEngine.convert("oo"))
        assertEquals("ơ", TelexEngine.convert("ow"))
        assertEquals("ư", TelexEngine.convert("uw"))
        assertEquals("đ", TelexEngine.convert("dd"))
    }

    @Test
    fun chao_huyen() {
        assertEquals("chào", TelexEngine.convert("chaof"))
    }

    @Test
    fun tones() {
        assertEquals("á", TelexEngine.convert("as"))
        assertEquals("à", TelexEngine.convert("af"))
        assertEquals("ả", TelexEngine.convert("ar"))
        assertEquals("ã", TelexEngine.convert("ax"))
        assertEquals("ạ", TelexEngine.convert("aj"))
    }

    @Test
    fun remove_tone_with_z() {
        assertEquals("a", TelexEngine.convert("asz"))
    }

    @Test
    fun preserve_case() {
        assertEquals("CHÀO", TelexEngine.convert("CHAOF"))
    }

    @Test
    fun incomplete_stays_literal() {
        assertEquals("chao", TelexEngine.convert("chao"))
    }

    @Test
    fun tone_without_vowel_stays_literal() {
        assertEquals("s", TelexEngine.convert("s"))
    }

    @Test
    fun replace_existing_tone() {
        assertEquals("á", TelexEngine.convert("afs"))
    }

    @Test
    fun prefer_marked_vowel_for_tone() {
        assertEquals("ấ", TelexEngine.convert("aas"))
    }

    @Test
    fun tone_placement_for_oi() {
        assertEquals("nói", TelexEngine.convert("nois"))
        assertEquals("hỏi", TelexEngine.convert("hoir"))
        assertEquals("mọi", TelexEngine.convert("moij"))
    }

    @Test
    fun qu_and_gi_are_initial_consonant_clusters() {
        assertEquals("quán", TelexEngine.convert("quans"))
        assertEquals("giá", TelexEngine.convert("gias"))
    }

    @Test
    fun tone_goes_on_middle_vowel_of_three_vowel_cluster() {
        assertEquals("ngoài", TelexEngine.convert("ngoaif"))
        assertEquals("loại", TelexEngine.convert("loaji"))
    }

    @Test
    fun uow_and_uwow_make_uoro() {
        assertEquals("ươ", TelexEngine.convert("uow"))
        assertEquals("ươ", TelexEngine.convert("uwow"))
        assertEquals("ương", TelexEngine.convert("uowng"))
        assertEquals("ướng", TelexEngine.convert("uowngs"))
        assertEquals("đường", TelexEngine.convert("dduowngf"))
    }

    @Test
    fun toTelexRaw_round_trips_common_words() {
        for (word in listOf("chà", "chào", "ươ", "ương", "ướng", "á", "đường")) {
            assertEquals(word, TelexEngine.convert(TelexEngine.toTelexRaw(word)))
        }
    }
}
