# VN Telex HW

Android IME for physical keyboards. Types Vietnamese with Telex. Commits text with `deleteSurroundingText` / selection replace + `commitText`. Does not use
IME composition (`setComposingText`). Web editors that mishandle composition (Lark Web and
similar) get plain committed text.

> Bàn phím cứng + Telex tiếng Việt trên Android. Không dùng IME composition.

## Features

- Telex dialect aligned with Bamboo-style rules (marks, tones, one-shot undo, `ươ` / `ưa`)
- Hardware keyboard only (soft input view is blank on purpose)
- Ctrl+Space switches to the next enabled IME
- Ctrl / Alt / Meta shortcuts pass through to the editor
- Android 15+ (`minSdk` 35)

## Non-goals

- Soft keyboard UI, swipe, emoji, prediction
- VNI / VIQR / Telex W
- Full Unikey compatibility
- Network, accounts, or cloud sync

## Requirements

- Android 15+ device
- JDK 17+
- Android SDK 35
- Bluetooth or USB keyboard for real use

Create `local.properties` (gitignored) with your SDK path:

```properties
sdk.dir=/path/to/Android/Sdk
```

## Build and install

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

On the device:

1. **Settings → System → Keyboard → On-screen keyboard → Manage keyboards**
2. Enable **VN Telex HW**
3. Select it as the current input method
4. Connect a hardware keyboard and type in a text field

## Tests

```bash
./gradlew :app:testDebugUnitTest
```

Full checklist and package layout: [docs/development.md](docs/development.md).

## Telex (short)

| Keys | Result |
|------|--------|
| `aa` `aw` `ee` `oo` `ow` `uw` `dd` | â ă ê ô ơ ư đ |
| `s` `f` `r` `x` `j` | sắc huyền hỏi ngã nặng |
| `z` | remove tone |
| same mark/tone key again | undo + append literal (`uww` → `uw`) |
| `uow` / `uoww` | `uơ` / `ươ` |

Full rules: [docs/telex-dialect.md](docs/telex-dialect.md).

## Architecture

`KeyEvent` → `KeyClassifier` → `InputPipeline` + `TelexEngine` → `CommitManager` /
`InputConnectionCommitter` (replace previous word, commit new text). Word state lives in
`WordBuffer`. Navigation keys and unexpected caret moves clear it.

Details: [docs/architecture.md](docs/architecture.md).

## Known limitations

Works in the Lark app and most Chrome page fields. Chrome URL autocomplete, Termux, and
some weak `InputConnection` editors still break. See [docs/limitations.md](docs/limitations.md).

## Reference only

The `example/` tree holds a vendored [ibus-bamboo](https://github.com/BambooEngine/ibus-bamboo)
checkout for Telex rule comparison. It is not part of the Android app build.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Security reports: [SECURITY.md](SECURITY.md).

## License

[MIT](LICENSE) © manhhn01

Telex behavior was checked against [Bamboo](https://github.com/BambooEngine/bamboo-core)
(MIT). This repo is a separate Android IME.
