# Penguin Calculator — Source Security Evaluation

**Version:** 1.1.1 rebuild  
**Review date:** 2026-10-16  
**Scope:** Source, manifest, and launcher icon resources committed in this folder. This is a source review, not a third-party audit or Play Store approval.

## Summary

The app performs arithmetic locally. The Android manifest requests no runtime permissions and declares no network permission. The reviewed source contains no advertising, analytics, account, location, Bluetooth, or cloud code. Calculator values are held in process memory and are not saved or transmitted by this implementation.

## Checks performed

- Reviewed the manifest for permissions, exported components, and launcher icon references.
- Reviewed adaptive and density-specific launcher images; they use the same in-app penguin holding a calculator.
- Reviewed the source for network, external storage, account, analytics, and advertising integrations.
- Reviewed the arithmetic engine for input length, division-by-zero handling, decimal arithmetic, and clear/backspace behavior.
- Added standalone Java engine smoke checks to the repository. They could not be run locally because this workspace has Java but no Java compiler, Gradle, or Android SDK. GitHub Actions built version 1.1.1 and passed the engine smoke checks on 2026-10-16; the app has not been installed or tested on a physical Android device.

## Findings and limits

- **Permissions:** none requested. The launcher activity is exported as required for Android launch and has only the MAIN/LAUNCHER intent filter.
- **Data handling:** entered values are transient UI state. This version does not implement history or persistent storage.
- **Network behavior:** no internet permission or network library is included in source.
- **Input and calculation:** numeric input is capped at 30 characters. Division by zero produces a readable error state. Division uses 18-digit precision with half-up rounding.
- **Build identity:** GitHub Actions run [38952074499](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/38952074499) completed successfully for commit `070a8596aae34689485eec7c1225d4cd47eebbd1`; Gradle tests and debug APK assembly passed. APK SHA-256: `3c0e541bfa71ad9c0d78a0ac2cda923f4d05983e0a0a87f74a41f83500b5b606`. The 1.1.1 change adds the app mascot as launcher art and updates version metadata; it adds no permissions or runtime data handling. The artifact is debug-signed for testing; its signing certificate was not independently inspected. No physical-device installation or UI test has been performed.
- **Compatibility and accessibility:** oversized controls and labels are present in source. Screen-reader and device layout behavior still need Android-device verification.

## Disposition

Suitable for source review and controlled testing. The successful CI build verifies compilation and automated arithmetic tests, not device behavior. Do not describe this review as an independent penetration test, a production signing review, or proof of device-tested behavior.
