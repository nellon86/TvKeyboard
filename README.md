TvKeyboard
==========

A modified version of [LeanKeyboard](https://github.com/yuliskov/LeanKeyboard) by Yurii L, released under the same [MIT License](./LICENSE).

### Changes in this fork
 * **Own package ID** `com.nellon.tvkeyboard` and name *TvKeyboard*, so it installs alongside the original LeanKeyboard.
 * **Remote media buttons** while the keyboard is open:
   * Play / Play-Pause acts as the keyboard's Enter (Go, Search, Done…).
   * Previous moves the cursor to the start of the text.
   * Next moves the cursor to the end of the text.
 * **Material design**: rounded key caps, accent-filled focused key, regular-weight letters, vector icons, `?123` / `ABC` mode keys, pill-shaped Enter button.
 * **Compact panel**: the keyboard sits in a rounded panel centred at the bottom of the screen instead of a full-width background.
 * **Row wraparound**: pressing right on the last key of a row jumps back to its first key, and left on the first key jumps to the last. Up/down wrapping can be turned on with the *Cyclic navigation* setting.

### Build
```
./gradlew assembleOriginDebug
```
The APK is written to `leankeykeyboard/build/outputs/apk/origin/debug/`.
