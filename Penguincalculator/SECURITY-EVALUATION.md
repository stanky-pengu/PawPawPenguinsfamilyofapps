# Penguin Calculator — Source Security Evaluation

**Version:** 1.1.0 rebuild  
**Review date:** 2026-10-16  
**Scope:** Source and manifest committed in this folder. This is a source review, not a third-party audit or Play Store approval.

## Summary

The app performs arithmetic locally. The Android manifest requests no runtime permissions and declares no network permission. The reviewed source contains no advertising, analytics, account, location, Bluetooth, or cloud code. Calculator values are held in process memory and are not saved or transmitted by this implementation.

## Checks performed

- Reviewed the manifest for permissions and exported components.
- Reviewed the source for network, external storage, account, analytics, and advertising integrations.
- Reviewed the arithmetic engine for input length, division-by-zero handling, decimal arithmetic, and clear/backspace behavior.
- Added standalone Java engine smoke checks to the repository. They were not run locally because this workspace has Java but no Java compiler, Gradle, or Android SDK; the repository CI workflow is configured to run them and assemble a debug APK. No APK was built or device-tested during this review.

## Findings and limits

- **Permissions:** none requested. The launcher activity is exported as required for Android launch and has only the MAIN/LAUNCHER intent filter.
- **Data handling:** entered values are transient UI state. This version does not implement history or persistent storage.
- **Network behavior:** no internet permission or network library is included in source.
- **Input and calculation:** numeric input is capped at 30 characters. Division by zero produces a readable error state. Division uses 18-digit precision with half-up rounding.
- **Build identity:** no release APK, signing certificate, or APK hash is included in this source-only review. CI is configured to assemble a debug APK; that artifact will be debug-signed and is for testing only.
- **Compatibility and accessibility:** oversized controls and labels are present in source. Screen-reader and device layout behavior still need Android-device verification.

## Disposition

Suitable for source review and controlled testing after CI produces an APK. Do not describe this review as an independent penetration test, a production signing review, or proof of device-tested behavior.
