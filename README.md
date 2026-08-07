# VN Telex HW

Minimal Android IME: physical keyboard Telex → Vietnamese via delete+commit (no composition).

## Requirements

- Android 15+ device
- JDK 17+
- Android SDK 35
- Physical Bluetooth/USB keyboard for real use

## Build

```bash
./gradlew :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Install

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Then: Settings → System → Keyboard → On-screen keyboard → Manage keyboards → enable **VN Telex HW** → choose it as current IME.

## Unit tests

```bash
./gradlew :app:testDebugUnitTest
```

## Manual checklist

1. Connect hardware keyboard
2. Open Chrome text field — type `chaof` → expect `chào`
3. Backspace → `chao`
4. Type `f` again → `chào`, then Space → word ends
5. Click mid-word / move cursor → next letters start fresh (no stuck accents)
6. Ctrl+C / Ctrl+V still work in the editor
7. Ctrl+Space switches to the next enabled keyboard/IME (no space inserted)
8. Open Lark Web composer — repeat steps 2–7 (primary success target)
9. Confirm soft keyboard area is blank (no key UI)
