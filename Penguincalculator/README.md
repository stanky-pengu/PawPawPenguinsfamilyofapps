# PawPawPenguins Calculator

Version 1.2.0 adds an automatic scientific layout in landscape, including trigonometry, logarithms, square root, square, reciprocal, pi, and a degrees/radians toggle. Rotate the phone back to portrait to return to the familiar large-button layout. The seal and penguin math lesson stays visible in both orientations, and the current calculation is preserved through rotation.

## Features

- Extra-large, high-contrast number display and oversized tap targets
- Addition, subtraction, multiplication, division and percent
- Landscape scientific functions: sin, cos, tan, ln, log, square root, square, reciprocal, and pi
- Trigonometry can use degrees or radians
- Automatically switches layouts when the phone rotates, preserving the active calculation
- Decimal input, sign toggle, clear, backspace and chained operations
- Offline operation, no advertising, and no saved calculator history
- Accessible math lesson scene description

Scientific functions operate on the displayed value. Invalid domains (such as the logarithm of zero) show an error; clear to continue.

## Build

Requires JDK 17, Gradle 8.9 and Android SDK Platform 35:

```sh
gradle :app:assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`
