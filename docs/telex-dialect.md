# Telex dialect

Rules follow [Bamboo-style Telex](https://github.com/BambooEngine/bamboo-core), plus one UX
extension for `ie` / `ye` before finals.

This dialect is a Bamboo Telex subset (no Unikey, VNI, or Telex W).

## Marks

| Key sequence | Result |
|--------------|--------|
| `aa` | â |
| `aw` | ă |
| `ee` | ê |
| `oo` | ô |
| `ow` | ơ |
| `uw` | ư |
| `dd` | đ |

Also: `uow` → `uơ`, then `uoww` → `ươ` (two-step). SuperKey-style tails such as
`uowng` → `ương` are promoted when the engine can.

`ưa` and related forms cover common syllables.

## Tones

| Key | Tone |
|-----|------|
| `s` | sắc |
| `f` | huyền |
| `r` | hỏi |
| `x` | ngã |
| `j` | nặng |
| `z` | remove tone |

## One-shot undo

Repeating the same mark or tone key undoes once and appends the literal key (Bamboo-style
escape). One-shot only; no perpetual odd/even toggle.

| Type | Result |
|------|--------|
| `uw` | ư |
| `uww` | uw |
| `uwww` | uww |
| `as` | ás |
| `ass` | as |

Same idea for other marks (`aa` → â, `aaa` → aa) and tones.

## Mark-family switch

| Type | Result |
|------|--------|
| `aaw` | ă |
| `awa` | â |
| `oow` | ơ |
| `owo` | ô |

`ươ` / `ưo` / `uơ` + `o` → `uô` (e.g. `ươo` → `uô`).

## ie / ye promotion (extension)

Bamboo often leaves these alone. This IME promotes **ie → iê** and **ye → yê** before
glide/finals (`lieeu` → `liệu`, `yeu` → `yêu`). That is intentional product behavior.

## Examples

| Keys | Display |
|------|---------|
| `chaof` | chào |
| `ddungs` | đúng |
| `uowng` | ương |
| `hoawcs` | hoặc |
| `tuwowng` | tưởng |
| `lieeu` | liệu |
| `yeus` | yếu |

Edge cases and unit expectations live in `TelexEngineTest` under
`app/src/test/java/com/ime/vnkeyboard/`.
