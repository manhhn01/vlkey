# Task 1 Report: Gradle app skeleton + TelexEngine (TDD)

**Date:** 2026-08-06  
**Branch:** `feature/vn-telex-hw`  
**Worktree:** `/home/mark/Documents/Code/ime/.worktrees/vn-telex-hw`

## Summary

Created the Android Gradle project skeleton for VN Telex HW (`com.ime.vnkeyboard`, API 35) and implemented stateless `TelexEngine.convert(raw: String): String` with TDD. All 9 unit tests pass.

## Environment setup

The build machine had no JDK, Gradle, or Android SDK initially.

| Component | Action |
|-----------|--------|
| JDK 17 | Installed via `apt` (`openjdk-17-jdk-headless`) |
| Android SDK | Downloaded cmdline-tools; installed `platforms;android-35`, `build-tools;35.0.0` to `~/Android/Sdk` |
| `local.properties` | Created (gitignored) pointing at SDK |
| Gradle wrapper | Generated with Gradle 8.11.1 |

## Files created

| File | Purpose |
|------|---------|
| `settings.gradle.kts` | Root project `VnTelexHw`, `:app` module |
| `build.gradle.kts` | AGP 8.7.3, Kotlin 2.0.21 plugin declarations |
| `gradle.properties` | JVM args, AndroidX, Kotlin style |
| `app/build.gradle.kts` | App module config (min/target/compile SDK 35) |
| `app/src/main/AndroidManifest.xml` | Minimal application stub |
| `app/src/main/res/values/strings.xml` | App label "VN Telex HW" |
| `app/src/main/java/com/ime/vnkeyboard/TelexEngine.kt` | Telex conversion engine |
| `app/src/test/java/com/ime/vnkeyboard/TelexEngineTest.kt` | 9 unit tests |
| `gradle/wrapper/*`, `gradlew`, `gradlew.bat` | Gradle wrapper |
| `.gitignore` | Standard Android ignores (added after initial commit cleanup) |

## TDD evidence

### RED — tests before implementation

Command:

```bash
./gradlew :app:testDebugUnitTest --tests com.ime.vnkeyboard.TelexEngineTest
```

Result: **BUILD FAILED** — compile error as expected:

```
Unresolved reference 'TelexEngine'
```

(22 compilation errors across `TelexEngineTest.kt`, one per `TelexEngine.convert` call.)

### GREEN — after implementation

First run with brief-provided `findToneTargetIndex` (last-vowel fallback): **2 failures**

| Test | Expected | Actual |
|------|----------|--------|
| `chao_huyen` | `chào` | `chaò` |
| `preserve_case` | `CHÀO` | `CHAÒ` |

Root cause: tone key `f` targeted last vowel `o` in `chao`; Vietnamese places tone on first vowel of `ao` diphthong.

**Fix applied:** Extended `findToneTargetIndex` with `diphthongToneOnFirst` set (oa/oe/uy, ai/ao/au/…, etc.) — tone on first vowel of recognized pairs; default remains last vowel per design spec.

Second run:

```bash
./gradlew :app:testDebugUnitTest --tests com.ime.vnkeyboard.TelexEngineTest
./gradlew :app:testDebugUnitTest
```

Result: **BUILD SUCCESSFUL** — 9/9 tests passed.

## Self-review

### Correctness

- All brief-specified test cases pass.
- Diacritics (`aa`→â, `dd`→đ, etc.), tones (s/f/r/x/j), tone removal (`z`), case preservation, literal fallback, tone replacement, and marked-vowel preference (`aas`→ấ) behave as specified.
- Diphthong tone placement fix aligns with design examples (`chào`, not `chaò`).

### Deviations from brief

1. **`findToneTargetIndex` enhancement** — Brief code used simple last-vowel scan; required diphthong rules for `chao_huyen` / `preserve_case`. Change is minimal and matches design intent (§6.4 example `Chào`).
2. **`.gitignore` + cleanup commit** — Initial `git add app` accidentally staged `app/build/` artifacts; removed in follow-up commit `42d277c`.
3. **`local.properties`** — Created for local SDK path; correctly excluded from git.

### Concerns

- Fresh clones need JDK 17 + Android SDK 35 (or CI provisioning); no checked-in SDK path.
- Diphthong list is MVP heuristic; full Unikey parity is explicitly out of scope.
- `ae84fbf` briefly contained build artifacts; history still includes that blob in first commit (cleaned in `42d277c`).

## Commits

| SHA | Subject |
|-----|---------|
| `ae84fbf` | feat: scaffold app and add TelexEngine |
| `42d277c` | chore: ignore Android build outputs |

## Test summary

```
./gradlew :app:testDebugUnitTest
BUILD SUCCESSFUL — TelexEngineTest: 9 tests, 0 failures
```

## Review fix: restore `.worktrees/` in `.gitignore`

**Finding:** Important — `.gitignore` had Android build ignores only; `.worktrees/` was dropped when Android entries were added.

**Fix:** Prepended `.worktrees/` to `.gitignore` while keeping all Android build output ignores (including `/app/build/`).

**Commit:** `d53920f` — fix: restore .worktrees/ in .gitignore alongside Android ignores

**Check output:**

```
$ test -f .gitignore && grep -n worktrees .gitignore
1:.worktrees/

$ grep -n 'app/build' .gitignore
14:/app/build/
```
