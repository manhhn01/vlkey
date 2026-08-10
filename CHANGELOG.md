# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versioning: [Semantic Versioning](https://semver.org/spec/v2.0.0.html), aligned with
`versionName` in `app/build.gradle.kts`.

## [Unreleased]

### Documentation

- Open-source README, LICENSE, CONTRIBUTING, SECURITY, and `docs/` guides

## [0.1.0] - 2026-08-10

### Added

- Hardware-only Vietnamese Telex IME (`VN Telex HW`) with no composition APIs
- `TelexEngine`: marks, tones, `ươ` / `ưa`, ie/ye→iê/yê promotion
- Bamboo-aligned one-shot mark/tone undo; `uow` two-step; mark-family switching; `ươo`→`uô`
- Ctrl+Space to switch to the next IME
- Character-wise backspace for toned glyphs
- Chrome-oriented `replaceBeforeCursor` (selection replace, delete fallback, DEL keys)
- Navigation-key buffer clear and expected-caret selection guard (Lark arrow ghost-word fix)
- Empty-buffer Backspace passed to the platform

### Fixed

- Hardware Enter/Tab not swallowed (platform keys)
- Selection echo clearing mid-word; key repeat tracking
- Partial InputConnection mutation consuming keys incorrectly

[Unreleased]: https://github.com/manhhn01/vlkey/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/manhhn01/vlkey/releases/tag/v0.1.0
