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

    fun convert(raw: String): String {
        if (raw.isEmpty()) return ""
        val out = StringBuilder()
        for (ch in raw) {
            val lower = ch.lowercaseChar()
            if (lower in toneKeys) {
                if (!applyToneKey(out, lower)) {
                    out.append(ch)
                }
                continue
            }
            if (!applyDiacriticKey(out, ch)) {
                out.append(ch)
            }
        }
        return out.toString()
    }

    private fun applyDiacriticKey(out: StringBuilder, ch: Char): Boolean {
        if (out.isEmpty()) return false
        val last = out.last()
        val lastLower = last.lowercaseChar()
        val keyLower = ch.lowercaseChar()
        val upper = ch.isUpperCase() || last.isUpperCase()

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
        return true
    }

    private fun applyToneKey(out: StringBuilder, toneKey: Char): Boolean {
        val idx = findToneTargetIndex(out) ?: return false
        val current = out[idx]
        val base = stripTone[current] ?: current
        val baseLower = base.lowercaseChar()
        if (toneKey == 'z') {
            if (base == current && stripTone[current] == null && current.lowercaseChar() in baseVowels.map { it.lowercaseChar() }) {
                // already untoned vowel — z consumes
                return true
            }
            if (stripTone[current] == null && current.lowercaseChar() !in "aăâeêioôơuưy") return false
            out.setCharAt(idx, if (current.isUpperCase()) baseLower.uppercaseChar() else baseLower)
            return true
        }
        val tonedLower = toneMap[toneKey]?.get(baseLower) ?: return false
        out.setCharAt(idx, if (current.isUpperCase() || base.isUpperCase()) tonedLower.uppercaseChar() else tonedLower)
        return true
    }

    private val diphthongToneOnFirst = setOf(
        "oa", "oe", "uy",
        "ai", "ao", "au", "ay", "eo", "ia", "iu",
        "ua", "ue", "ui", "uo", "uu",
        "ya", "ye", "yi", "yo", "yu",
    )

    private fun findToneTargetIndex(out: StringBuilder): Int? {
        for (i in out.indices.reversed()) {
            val c = out[i]
            val base = (stripTone[c] ?: c).lowercaseChar()
            if (markedVowels.any { it.lowercaseChar() == base }) return i
        }

        val vowelIndices = mutableListOf<Int>()
        for (i in out.indices) {
            val c = out[i]
            val base = stripTone[c] ?: c
            if (base in baseVowels || stripTone.containsKey(c)) {
                vowelIndices.add(i)
            }
        }
        if (vowelIndices.isEmpty()) return null
        if (vowelIndices.size == 1) return vowelIndices[0]

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
