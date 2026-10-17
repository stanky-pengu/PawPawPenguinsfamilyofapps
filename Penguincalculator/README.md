# PawPawPenguins Penguin Calculator

Version 1.1.3 is a rebranded rebuild of the earlier Big Calculator. The penguin mascot is joined by a seal buddy on the calculator screen, and the layout keeps the header and keypad clear of Android system bars.

## Preserved behavior

- Extra-large, high-contrast number display and oversized tap targets
- Cute penguin and seal buddy mascots
- Addition, subtraction, multiplication, division and percent
- Decimal input, sign toggle, clear, backspace and chained operations
- Dark screen, offline operation and no advertising

The earlier APK was available at `Big-Calculator-1.0.apk`, but its original project source is no longer available in the current workspace or saved files. This source rebuild uses the recorded feature set and is not represented as a byte-for-byte continuation of that APK.

## Build

Requires JDK 17, Gradle 8.9 and Android SDK Platform 35:

```sh
gradle :app:assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

The app has no network permission and stores no calculator history.
