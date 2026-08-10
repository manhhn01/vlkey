# Development

## Prerequisites

- JDK **17**
- Android SDK **35** (`compileSdk` / `minSdk` / `targetSdk`)
- A device or emulator on Android 15+ for manual checks
- Hardware keyboard (Bluetooth/USB) for real typing tests

Create `local.properties` (gitignored) with your SDK path:

```properties
sdk.dir=/path/to/Android/Sdk
```

Do not commit secrets or machine-local paths.

## Gradle tasks

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleRelease   # if signing is configured locally
```

Debug APK:

`app/build/outputs/apk/debug/app-debug.apk`

Install:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Package layout

```
app/src/main/java/com/ime/vnkeyboard/
  VnImeService.kt              # InputMethodService
  KeyClassifier.kt             # KeyAction routing
  InputPipeline.kt             # Telex + commit orchestration
  TelexEngine.kt               # Pure Telex rules
  WordBuffer.kt                # Word state + committedLength
  CommitManager.kt             # Batch replace/commit/backspace
  TextCommitter.kt             # Committer interface
  InputConnectionCommitter.kt  # InputConnection implementation
  SelectionEchoGuard.kt        # Caret echo vs real move
  ConsumedKeyCodes.kt          # Key-up pairing helpers
```

Unit tests:

```
app/src/test/java/com/ime/vnkeyboard/
  TelexEngineTest.kt
  InputPipelineTest.kt
  CommitManagerTest.kt
  KeyClassifierTest.kt
  SelectionEchoGuardTest.kt
  WordBufferTest.kt
  ConsumedKeyCodesTest.kt
```

## Test approach

Telex and pipeline logic are pure Kotlin and covered by JVM unit tests. Add a failing test
before you change `TelexEngine` or commit behavior. See [CONTRIBUTING.md](../CONTRIBUTING.md).

## Manual device checklist

After install and enabling **VN Telex HW**:

1. Connect a hardware keyboard.
2. Type `chaof` → `chào` in Notes or Messages.
3. Type `ddungs` → `đúng`.
4. Undo check: `uww` → `uw` (not toggle back to `ư`).
5. Arrow left mid-word, then type a letter. Buffer should reset (no ghost previous word).
6. Ctrl+Space switches IME.
7. Enter / Tab still work in the editor.
8. Ctrl+C / Ctrl+V still work in the editor.
9. Soft keyboard area stays blank (no key UI).
10. Optional: Lark chat + a Chrome page field for regression.

Weak spots (Chrome omnibox, Termux): [limitations.md](limitations.md).

## Version

`versionName` / `versionCode` live in `app/build.gradle.kts`. Keep
[CHANGELOG.md](../CHANGELOG.md) in sync when you cut a release.
