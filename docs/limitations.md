# Limitations

Compatibility matrix for VN Telex HW **0.1.0**. “Works” means Telex replace and caret clear
are good enough for daily typing, short of Unikey parity.

| Surface | Status | Notes |
|---------|--------|-------|
| Lark mobile app | Good | Primary target |
| Lark Web (in-app / Chrome) | Good | No composition avoids many web IME bugs |
| Chrome content fields | Good | Selection-based replace helps |
| Chrome omnibox / URL autocomplete | Weak | Expanded selection + blind replace can corrupt (`c` → suggest → `h` → wrong text). Non-collapsed selection clears the buffer; full “commit into selection” is out of scope for 0.1 |
| Termux | Weak | Replace often **appends** instead of replacing (`chào` → duplicated prefix). Needs post-replace verify / degrade mode later |
| Soft keyboard | Blank by design | Hardware typing only; on-screen view is empty |
| Android &lt; 15 | Unsupported | `minSdk` / `targetSdk` 35 |
| Unikey full matrix | Unsupported | Bamboo Telex subset + documented ie/ye extension |
| VNI / VIQR / Telex W | Unsupported | Not implemented |

## Platform keys

Enter and Tab are not consumed by the IME; the system handles them. Empty-buffer Backspace
also returns `false` so the editor deletes normally.

## Out of scope for claims

- Windows Unikey parity or full Telex dialect coverage
- Flawless WebView, terminal, or autocomplete fields
- Soft keyboard or prediction features

New editor bug: open an issue with app name, Android version, and key sequence (`|` for
caret). Skim this page first.
