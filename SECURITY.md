# Security policy

## Supported versions

| Version | Supported |
|---------|-----------|
| 0.1.x (Android 15+, `minSdk` 35) | Yes |
| Older Android | No |

## What this app does

VN Telex HW is a local Android IME. It opens no network connections, stores no accounts, and
ships no analytics. It reads hardware key events and writes text through `InputConnection`.

## Reporting a vulnerability

If you find a security issue (for example privilege escalation via the IME service, or unsafe
handling of editor text):

1. Email **manhhn01@gmail.com**. Do not open a public issue first.
2. Include Android version, steps to reproduce, and impact.
3. Give time for a fix before public disclosure.

Reports get an acknowledgement. Fixes ship in a patch release when needed.

## Non-security bugs

Typing glitches, Telex rule mistakes, and editor compatibility problems belong in public
GitHub issues. Read [docs/limitations.md](docs/limitations.md) before filing duplicates.
