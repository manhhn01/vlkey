package com.ime.vnkeyboard

object TelexEngine {
    private val toneKeys = setOf('s', 'f', 'r', 'x', 'j', 'z')

    private val markedVowels = listOf('ê', 'ô', 'ơ', 'â', 'ă', 'ư', 'Ê', 'Ô', 'Ơ', 'Â', 'Ă', 'Ư')

    private val baseVowels = setOf(
        'a', 'ă', 'â', 'e', 'ê', 'i', 'o', 'ô', 'ơ', 'u', 'ư', 'y',
        'A', 'Ă', 'Â', 'E', 'Ê', 'I', 'O', 'Ô', 'Ơ', 'U', 'Ư', 'Y',
    )

    private val toneMap: Map<Char, Map<Char, Char>> = mapOf(
        's' to mapOf(
            'a' to 'á', 'ă' to 'ắ', 'â' to 'ấ', 'e' to 'é', 'ê' to 'ế',
            'i' to 'í', 'o' to 'ó', 'ô' to 'ố', 'ơ' to 'ớ', 'u' to 'ú',
            'ư' to 'ứ', 'y' to 'ý',
        ),
        'f' to mapOf(
            'a' to 'à', 'ă' to 'ằ', 'â' to 'ầ', 'e' to 'è', 'ê' to 'ề',
            'i' to 'ì', 'o' to 'ò', 'ô' to 'ồ', 'ơ' to 'ờ', 'u' to 'ù',
            'ư' to 'ừ', 'y' to 'ỳ',
        ),
        'r' to mapOf(
            'a' to 'ả', 'ă' to 'ẳ', 'â' to 'ẩ', 'e' to 'ẻ', 'ê' to 'ể',
            'i' to 'ỉ', 'o' to 'ỏ', 'ô' to 'ổ', 'ơ' to 'ở', 'u' to 'ủ',
            'ư' to 'ử', 'y' to 'ỷ',
        ),
        'x' to mapOf(
            'a' to 'ã', 'ă' to 'ẵ', 'â' to 'ẫ', 'e' to 'ẽ', 'ê' to 'ễ',
            'i' to 'ĩ', 'o' to 'õ', 'ô' to 'ỗ', 'ơ' to 'ỡ', 'u' to 'ũ',
            'ư' to 'ữ', 'y' to 'ỹ',
        ),
        'j' to mapOf(
            'a' to 'ạ', 'ă' to 'ặ', 'â' to 'ậ', 'e' to 'ẹ', 'ê' to 'ệ',
            'i' to 'ị', 'o' to 'ọ', 'ô' to 'ộ', 'ơ' to 'ợ', 'u' to 'ụ',
            'ư' to 'ự', 'y' to 'ỵ',
        ),
    )

    private val stripTone: Map<Char, Char> = buildMap {
        for (tone in toneMap.values) {
            for ((base, accented) in tone) {
                put(accented, base)
                put(accented.uppercaseChar(), base.uppercaseChar())
            }
        }
    }

    private val toneKeyByVowel: Map<Char, Char> = buildMap {
        for ((toneKey, tone) in toneMap) {
            for (accented in tone.values) {
                put(accented, toneKey)
                put(accented.uppercaseChar(), toneKey)
            }
        }
    }

    fun convert(raw: String): String {
        if (raw.isEmpty()) return ""
        val out = StringBuilder()
        for (ch in raw) {
            applyKey(out, ch)
        }
        return out.toString()
    }

    /** Intermediate forms after each key — for tests/debugging. */
    fun convertSteps(raw: String): List<String> {
        val out = StringBuilder()
        val steps = mutableListOf<String>()
        for (ch in raw) {
            applyKey(out, ch)
            steps += out.toString()
        }
        return steps
    }

    private fun applyKey(out: StringBuilder, ch: Char) {
        val lower = ch.lowercaseChar()
        if (lower in toneKeys) {
            if (!applyToneKey(out, lower)) {
                out.append(ch)
            }
            promoteUowTail(out)
            promoteIeYeCircumflex(out)
            return
        }
        if (!applyDiacriticKey(out, ch)) {
            out.append(ch)
            if (isVowelChar(ch)) {
                repositionExistingTone(out)
            }
        }
        promoteUowTail(out)
        promoteIeYeCircumflex(out)
    }

    private fun isVowelChar(ch: Char): Boolean {
        val base = stripTone[ch] ?: ch
        return base in baseVowels || stripTone.containsKey(ch)
    }

    // Finals after ie/ye that force e → ê (includes glide u for iêu/yêu).
    private val ieYeFinalSingles = setOf('c', 't', 'n', 'p', 'm', 'u')
    private val ieYeFinalDigraphs = setOf("ch", "nh", "ng")

    /**
     * Vietnamese rhyme rule: ie/ye + final (c/t/n/p/m/u/ch/nh/ng) ⇒ iê/yê.
     * So `vietj` → `việt`, `lieur` → `liệu`, `yeur` → `yếu`.
     */
    private fun promoteIeYeCircumflex(out: StringBuilder) {
        if (out.length < 3) return
        for (i in 0 until out.length - 1) {
            val lead = (stripTone[out[i]] ?: out[i]).lowercaseChar()
            if (lead != 'i' && lead != 'y') continue
            val eChar = out[i + 1]
            val eBase = (stripTone[eChar] ?: eChar).lowercaseChar()
            if (eBase != 'e' && eBase != 'ê') continue
            if (eBase == 'ê') continue
            val rest = buildString {
                for (j in i + 2 until out.length) {
                    append((stripTone[out[j]] ?: out[j]).lowercaseChar())
                }
            }
            if (!isIeYeFinal(rest)) continue
            val tone = toneKeyByVowel[eChar]
            val upper = eChar.isUpperCase() || (stripTone[eChar] ?: eChar).isUpperCase()
            var promoted: Char = if (upper) 'Ê' else 'ê'
            if (tone != null) {
                val toned = toneMap[tone]?.get(promoted.lowercaseChar()) ?: promoted
                promoted = if (upper) toned.uppercaseChar() else toned
            }
            out.setCharAt(i + 1, promoted)
        }
    }

    private fun isIeYeFinal(rest: String): Boolean {
        if (rest.isEmpty()) return false
        if (rest in ieYeFinalDigraphs) return true
        if (rest.length == 1 && rest[0] in ieYeFinalSingles) return true
        return false
    }

    /** Rebuild Telex keystrokes that convert back to [vietnamese] (for character-wise backspace). */
    fun toTelexRaw(vietnamese: String): String {
        if (vietnamese.isEmpty()) return ""
        val sb = StringBuilder()
        for (ch in vietnamese) {
            val tone = toneKeyByVowel[ch]
            val base = stripTone[ch] ?: ch
            val upper = base.isUpperCase()
            val piece = when (base.lowercaseChar()) {
                'ă' -> if (upper) "AW" else "aw"
                'â' -> if (upper) "AA" else "aa"
                'ê' -> if (upper) "EE" else "ee"
                'ô' -> if (upper) "OO" else "oo"
                'ơ' -> if (upper) "OW" else "ow"
                'ư' -> if (upper) "UW" else "uw"
                'đ' -> if (upper) "DD" else "dd"
                else -> base.toString()
            }
            sb.append(piece)
            if (tone != null) sb.append(tone)
        }
        return sb.toString()
    }

    private fun repositionExistingTone(out: StringBuilder) {
        val tonedIndex = out.indices.firstOrNull { out[it] in toneKeyByVowel } ?: return
        val toned = out[tonedIndex]
        val toneKey = toneKeyByVowel.getValue(toned)
        out.setCharAt(tonedIndex, stripTone.getValue(toned))
        applyToneKey(out, toneKey)
    }

    private fun applyDiacriticKey(out: StringBuilder, ch: Char): Boolean {
        if (out.isEmpty()) return false
        val keyLower = ch.lowercaseChar()

        // ươ/ưo/uơ + o → uô (Bamboo regUhO)
        if (keyLower == 'o' && out.length >= 2) {
            val first = out[out.lastIndex - 1]
            val second = out.last()
            val firstBase = (stripTone[first] ?: first).lowercaseChar()
            val secondBase = (stripTone[second] ?: second).lowercaseChar()
            val keptTone = toneKeyByVowel[second] ?: toneKeyByVowel[first]
            val oUpper = second.isUpperCase() || ch.isUpperCase()
            val uUpper = first.isUpperCase()
            if (firstBase == 'ư' && (secondBase == 'ơ' || secondBase == 'o')) {
                out.setCharAt(out.lastIndex - 1, if (uUpper) 'U' else 'u')
                out.setCharAt(out.lastIndex, withTone(if (oUpper) 'Ô' else 'ô', keptTone))
                return true
            }
            if (firstBase == 'u' && secondBase == 'ơ') {
                out.setCharAt(out.lastIndex, withTone(if (oUpper) 'Ô' else 'ô', keptTone))
                return true
            }
        }

        // Bamboo w digraphs (before single-letter w rules):
        // uow→uơ, uoww→ươ, uowww→uow; uwow→ươ; uaw→ưa / uaww→uaw
        if (keyLower == 'w' && out.length >= 2) {
            val first = out[out.lastIndex - 1]
            val second = out.last()
            val firstBase = (stripTone[first] ?: first).lowercaseChar()
            val secondBase = (stripTone[second] ?: second).lowercaseChar()
            val firstTone = toneKeyByVowel[first]
            val secondTone = toneKeyByVowel[second]

            // ươ + w → undo to uow; ưa + w → uaw
            if (firstBase == 'ư' && (secondBase == 'ơ' || secondBase == 'a')) {
                out.setCharAt(out.lastIndex - 1, restorePlainKeepingTone(first, 'u'))
                if (secondBase == 'ơ') {
                    out.setCharAt(out.lastIndex, restorePlainKeepingTone(second, 'o'))
                }
                out.append(ch)
                return true
            }
            // uơ + w → complete ươ
            if (firstBase == 'u' && secondBase == 'ơ') {
                out.setCharAt(
                    out.lastIndex - 1,
                    withTone(if (first.isUpperCase()) 'Ư' else 'ư', firstTone),
                )
                return true
            }
            // ưo + w → ươ (uwow)
            if (firstBase == 'ư' && secondBase == 'o') {
                out.setCharAt(
                    out.lastIndex,
                    withTone(
                        if (second.isUpperCase() || ch.isUpperCase()) 'Ơ' else 'ơ',
                        secondTone,
                    ),
                )
                return true
            }
            // uo + w → uơ (horn on o only; tone prefers o, else moves from u)
            if (firstBase == 'u' && secondBase == 'o') {
                val keptTone = secondTone ?: firstTone
                val uUpper = first.isUpperCase()
                val oUpper = second.isUpperCase() || ch.isUpperCase()
                out.setCharAt(out.lastIndex - 1, if (uUpper) 'U' else 'u')
                out.setCharAt(out.lastIndex, withTone(if (oUpper) 'Ơ' else 'ơ', keptTone))
                return true
            }
            // ua + w → ưa
            if (firstBase == 'u' && secondBase == 'a') {
                val keptTone = secondTone ?: firstTone
                val uUpper = first.isUpperCase()
                val aUpper = second.isUpperCase() || ch.isUpperCase()
                out.setCharAt(out.lastIndex - 1, if (uUpper) 'Ư' else 'ư')
                out.setCharAt(out.lastIndex, if (aUpper) 'A' else 'a')
                if (keptTone != null) {
                    applyToneKey(out, keptTone)
                }
                return true
            }
        }

        val last = out.last()
        val lastBase = stripTone[last] ?: last
        val lastLower = lastBase.lowercaseChar()
        val upper = ch.isUpperCase() || last.isUpperCase()
        val lastTone = toneKeyByVowel[last]

        // Mark-family switch: â↔ă via w/a, ô↔ơ via w/o (before undo/apply).
        val switched: Char? = when {
            keyLower == 'w' && lastLower == 'â' -> if (upper) 'Ă' else 'ă'
            keyLower == 'w' && lastLower == 'ô' -> if (upper) 'Ơ' else 'ơ'
            keyLower == 'a' && lastLower == 'ă' -> if (upper) 'Â' else 'â'
            keyLower == 'o' && lastLower == 'ơ' -> if (upper) 'Ô' else 'ô'
            else -> null
        }
        if (switched != null) {
            out.setCharAt(out.lastIndex, withTone(switched, lastTone))
            return true
        }

        // Same diacritic key again → undo mark and append literal key (ư+w → uw).
        val undonePlain = undoDiacriticPlain(lastLower, keyLower)
        if (undonePlain != null) {
            out.setCharAt(out.lastIndex, restorePlainKeepingTone(last, undonePlain))
            out.append(ch)
            return true
        }

        val replacement: Char? = when {
            lastLower == 'd' && keyLower == 'd' -> if (upper) 'Đ' else 'đ'
            lastLower == 'a' && keyLower == 'a' -> if (upper) 'Â' else 'â'
            lastLower == 'a' && keyLower == 'w' -> if (upper) 'Ă' else 'ă'
            lastLower == 'e' && keyLower == 'e' -> if (upper) 'Ê' else 'ê'
            lastLower == 'o' && keyLower == 'o' -> if (upper) 'Ô' else 'ô'
            lastLower == 'o' && keyLower == 'w' -> if (upper) 'Ơ' else 'ơ'
            lastLower == 'u' && keyLower == 'w' -> if (upper) 'Ư' else 'ư'
            else -> null
        }
        if (replacement == null) return false
        out.setCharAt(out.lastIndex, replacement)
        val markedBases = setOf('ă', 'â', 'ê', 'ô', 'ơ', 'ư', 'đ')
        if (lastTone != null && replacement.lowercaseChar() in markedBases) {
            applyToneKey(out, lastTone)
        }
        return true
    }

    /** Plain vowel/consonant after undoing the mark created by [keyLower], or null if not undoable. */
    private fun undoDiacriticPlain(markedLower: Char, keyLower: Char): Char? = when {
        keyLower == 'w' && markedLower == 'ư' -> 'u'
        keyLower == 'w' && markedLower == 'ơ' -> 'o'
        keyLower == 'w' && markedLower == 'ă' -> 'a'
        keyLower == 'a' && markedLower == 'â' -> 'a'
        keyLower == 'e' && markedLower == 'ê' -> 'e'
        keyLower == 'o' && markedLower == 'ô' -> 'o'
        keyLower == 'd' && markedLower == 'đ' -> 'd'
        else -> null
    }

    /** Drop horn/circumflex/breve from [current], keep its tone and case, using [plainLower]. */
    private fun restorePlainKeepingTone(current: Char, plainLower: Char): Char {
        val tone = toneKeyByVowel[current]
        val upper = current.isUpperCase() || (stripTone[current] ?: current).isUpperCase()
        return withTone(if (upper) plainLower.uppercaseChar() else plainLower, tone)
    }

    private fun withTone(baseChar: Char, tone: Char?): Char {
        if (tone == null) return baseChar
        val lower = baseChar.lowercaseChar()
        val toned = toneMap[tone]?.get(lower) ?: return baseChar
        return if (baseChar.isUpperCase()) toned.uppercaseChar() else toned
    }

    /**
     * Bamboo SuperKey shortcut: `uơ`/`ưo` + following letter(s) → `ươ`…
     * So `uowng` → `ương` even though first `w` only made `uơ`.
     */
    private fun promoteUowTail(out: StringBuilder) {
        if (out.length < 3) return
        for (i in 0 until out.length - 2) {
            val a = (stripTone[out[i]] ?: out[i]).lowercaseChar()
            val b = (stripTone[out[i + 1]] ?: out[i + 1]).lowercaseChar()
            if (!((a == 'u' && b == 'ơ') || (a == 'ư' && b == 'o'))) continue
            var hasTail = false
            for (j in i + 2 until out.length) {
                if ((stripTone[out[j]] ?: out[j]).isLetter()) {
                    hasTail = true
                    break
                }
            }
            if (!hasTail) continue
            val t0 = toneKeyByVowel[out[i]]
            val t1 = toneKeyByVowel[out[i + 1]]
            val uUpper = out[i].isUpperCase() || (stripTone[out[i]] ?: out[i]).isUpperCase()
            val oUpper = out[i + 1].isUpperCase() || (stripTone[out[i + 1]] ?: out[i + 1]).isUpperCase()
            out.setCharAt(i, withTone(if (uUpper) 'Ư' else 'ư', t0))
            out.setCharAt(i + 1, withTone(if (oUpper) 'Ơ' else 'ơ', t1))
        }
    }

    private fun applyToneKey(out: StringBuilder, toneKey: Char): Boolean {
        if (toneKey == 'z') {
            val idx = findToneTargetIndex(out) ?: return false
            val current = out[idx]
            val base = stripTone[current] ?: current
            val baseLower = base.lowercaseChar()
            if (base == current && stripTone[current] == null && current.lowercaseChar() in baseVowels.map { it.lowercaseChar() }) {
                // already untoned vowel — z consumes
                return true
            }
            if (stripTone[current] == null && current.lowercaseChar() !in "aăâeêioôơuưy") return false
            out.setCharAt(idx, if (current.isUpperCase()) baseLower.uppercaseChar() else baseLower)
            return true
        }
        // Literal tone-key residue (e.g. ass→as): further same key only appends (asss→ass).
        if (out.isNotEmpty() && out.last().lowercaseChar() == toneKey) {
            return false
        }
        val idx = findToneTargetIndex(out) ?: return false
        val current = out[idx]
        val base = stripTone[current] ?: current
        val baseLower = base.lowercaseChar()
        // Same tone already present → strip and append key (Bamboo one-shot undo).
        if (toneKeyByVowel[current] == toneKey) {
            val upper = current.isUpperCase() || base.isUpperCase()
            out.setCharAt(idx, if (upper) baseLower.uppercaseChar() else baseLower)
            return false
        }
        val tonedLower = toneMap[toneKey]?.get(baseLower) ?: return false
        out.setCharAt(idx, if (current.isUpperCase() || base.isUpperCase()) tonedLower.uppercaseChar() else tonedLower)
        return true
    }

    private val diphthongToneOnFirst = setOf(
        "oa", "oe", "uy",
        "ai", "ao", "au", "ay", "eo", "ia", "iu", "oi",
        "ua", "ue", "ui", "uo", "uu",
        "ya", "ye", "yi", "yo", "yu",
    )

    private fun findToneTargetIndex(out: StringBuilder): Int? {
        val vowelIndices = mutableListOf<Int>()
        for (i in out.indices) {
            val c = out[i]
            val base = stripTone[c] ?: c
            if (base in baseVowels || stripTone.containsKey(c)) {
                vowelIndices.add(i)
            }
        }
        if (out.length >= 2) {
            val initial = "${out[0].lowercaseChar()}${out[1].lowercaseChar()}"
            if (initial == "qu" || initial == "gi") {
                vowelIndices.remove(1)
            }
        }
        if (vowelIndices.isEmpty()) return null

        for (i in vowelIndices.reversed()) {
            val c = out[i]
            val base = (stripTone[c] ?: c).lowercaseChar()
            if (markedVowels.any { it.lowercaseChar() == base }) return i
        }

        if (vowelIndices.size == 1) return vowelIndices[0]
        if (vowelIndices.size >= 3) return vowelIndices[vowelIndices.size / 2]

        for (j in 0 until vowelIndices.size - 1) {
            val v1 = (stripTone[out[vowelIndices[j]]] ?: out[vowelIndices[j]]).lowercaseChar()
            val v2 = (stripTone[out[vowelIndices[j + 1]]] ?: out[vowelIndices[j + 1]]).lowercaseChar()
            if ("$v1$v2" in diphthongToneOnFirst) {
                return vowelIndices[j]
            }
        }

        return vowelIndices.last()
    }
}
