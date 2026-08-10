# Contributing

## Setup

1. Clone the repo.
2. Install JDK 17 and Android SDK 35.
3. Create `local.properties` with your SDK path (Gradle leaves this file uncommitted):

```properties
sdk.dir=/path/to/Android/Sdk
```

4. Run unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

5. Build a debug APK:

```bash
./gradlew :app:assembleDebug
```

More detail: [docs/development.md](docs/development.md).

## How we work

- Prefer small PRs with a clear problem statement.
- For Telex or commit-path changes, add or update unit tests first
  (`TelexEngineTest`, `InputPipelineTest`, `CommitManagerTest`, `KeyClassifierTest`,
  `SelectionEchoGuardTest`).
- Product rule: do not call IME composition APIs (`setComposingText`, composing region for
  intermediate Telex). Use replace + `commitText` only.
- Match Kotlin style under `app/src/main/java/com/ime/vnkeyboard/`.
- Do not commit `local.properties`, APKs, or secrets.

## Pull requests

1. Branch from the branch the project currently ships from.
2. Summarize what broke, what you changed, and how you tested
   (`./gradlew :app:testDebugUnitTest` plus any device checks).
3. Update [CHANGELOG.md](CHANGELOG.md) under `Unreleased` when users can see the change.
4. Update `docs/` when behavior or limits change.

## Scope

| In scope | Out of scope (unless agreed) |
|----------|------------------------------|
| Telex engine, key routing, InputConnection replace | Soft keyboard UI |
| Buffer clear / caret policy | VNI / VIQR / Telex W |
| Chrome / Lark edge-case hardening | Full Unikey parity |

## Questions

Open an issue with the editor app, Android version, and a key sequence that reproduces the
bug (`|` for caret).
