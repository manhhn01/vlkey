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
    fun repeating_diacritic_key_undoes_to_literal() {
        // Bamboo Telex one-shot escape: undo + append; further keys stay literal (no toggle).
        assertEquals("uw", TelexEngine.convert("uww"))
        assertEquals("uww", TelexEngine.convert("uwww"))
        assertEquals("ow", TelexEngine.convert("oww"))
        assertEquals("oww", TelexEngine.convert("owww"))
        assertEquals("aw", TelexEngine.convert("aww"))
        assertEquals("aa", TelexEngine.convert("aaa"))
        assertEquals("aâ", TelexEngine.convert("aaaa"))
        assertEquals("ee", TelexEngine.convert("eee"))
        assertEquals("oo", TelexEngine.convert("ooo"))
        assertEquals("dd", TelexEngine.convert("ddd"))
        assertEquals("uaw", TelexEngine.convert("uaww"))
        assertEquals("uow", TelexEngine.convert("uowww"))
        // Keep tone when undoing horn/circumflex.
        assertEquals("úw", TelexEngine.convert("uwsw"))
        assertEquals("áa", TelexEngine.convert("aasa"))
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
    fun repeating_tone_key_undoes_to_literal() {
        // Bamboo Telex: same tone key → undo + append (one-shot, not toggle).
        assertEquals("as", TelexEngine.convert("ass"))
        assertEquals("ass", TelexEngine.convert("asss"))
        assertEquals("as", TelexEngine.convert("afss"))
        assertEquals("bas", TelexEngine.convert("bass"))
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
    fun uow_bamboo_two_step() {
        // Bamboo: first w horns o only; second w completes ư; third undoes.
        assertEquals("uơ", TelexEngine.convert("uow"))
        assertEquals("ươ", TelexEngine.convert("uoww"))
        assertEquals("uow", TelexEngine.convert("uowww"))
        assertEquals("ươ", TelexEngine.convert("uwow"))
        assertEquals("uow", TelexEngine.convert("uwoww"))
        // SuperKey-style: uơ + final → ươ…
        assertEquals("ương", TelexEngine.convert("uowng"))
        assertEquals("ướng", TelexEngine.convert("uowngs"))
        assertEquals("đường", TelexEngine.convert("dduowngf"))
    }

    @Test
    fun toTelexRaw_round_trips_common_words() {
        for (word in listOf("chà", "chào", "ươ", "ương", "ướng", "á", "đường", "ưa", "mưa", "việt")) {
            assertEquals(word, TelexEngine.convert(TelexEngine.toTelexRaw(word)))
        }
    }

    @Test
    fun ua_w_makes_ua_horn() {
        assertEquals("ưa", TelexEngine.convert("uaw"))
        assertEquals("mưa", TelexEngine.convert("muaw"))
        assertEquals("mửa", TelexEngine.convert("muawr"))
    }

    @Test
    fun mark_family_switching() {
        // Bamboo: switch within a/â/ă and o/ô/ơ families.
        assertEquals("ă", TelexEngine.convert("aaw"))
        assertEquals("ơ", TelexEngine.convert("oow"))
        assertEquals("â", TelexEngine.convert("awa"))
        assertEquals("ô", TelexEngine.convert("owo"))
        assertEquals("ắ", TelexEngine.convert("aasw"))
        assertEquals("mâ", TelexEngine.convert("mawa"))
        assertEquals("mô", TelexEngine.convert("mowo"))
    }

    @Test
    fun uoro_plus_o_makes_uo_circumflex() {
        assertEquals("uô", TelexEngine.convert("uowo"))
        assertEquals("uô", TelexEngine.convert("uowwo"))
    }

    @Test
    fun tone_before_ie_digraph_still_makes_viet() {
        assertEquals("việt", TelexEngine.convert("vijeet"))
        assertEquals("việt", TelexEngine.convert("vieejt"))
        assertEquals("việt", TelexEngine.convert("vietj"))
    }

    @Test
    fun ie_ye_promotes_circumflex_before_final() {
        assertEquals("tiếng", TelexEngine.convert("tiengs"))
        assertEquals("kiệm", TelexEngine.convert("kiemj"))
        assertEquals("uyên", TelexEngine.convert("uyen"))
    }

    @Test
    fun ieu_yeu_promotes_before_glide_u() {
        assertEquals("liêu", TelexEngine.convert("lieu"))
        // hỏi = ể (U+1EC3); nặng = ệ (U+1EC7)
        assertEquals("li\u1EC3u", TelexEngine.convert("lieur"))
        assertEquals("li\u1EC3u", TelexEngine.convert("lieeur"))
        assertEquals("li\u1EC7u", TelexEngine.convert("lieuj"))
        assertEquals("yêu", TelexEngine.convert("yeu"))
        assertEquals("yếu", TelexEngine.convert("yeus"))
        // Extra e after tone used to yield lieụe when u took the tone
        assertEquals("li\u1EC7ue", TelexEngine.convert("lieuje"))
    }
}
