# Penguin Calculator — Source Security Evaluation

**Version:** 1.1.2 rebuild (version code 4)  
**Review date:** 2026-10-16  
**Scope:** Source, manifest, launcher icon resources, and the added in-app seal buddy. This is a source review, not a third-party audit or Play Store approval.

## Summary

The app performs arithmetic locally. The Android manifest requests no runtime permissions and declares no network permission. The reviewed source contains no advertising, analytics, account, location, Bluetooth, or cloud code. Calculator values are held in process memory and are not saved or transmitted by this implementation. Version 1.1.2 adds a locally bundled seal illustration beside the existing penguin; it does not change arithmetic, data handling, or requested permissions.

## Checks performed

- Reviewed the manifest for permissions, exported components, and launcher icon references.
- Reviewed adaptive and density-specific launcher images; they use the same in-app penguin holding a calculator.
- Reviewed the new bundled seal buddy image and its accessible image description in the screen layout.
- Reviewed the source for network, external storage, account, analytics, and advertising integrations.
- Reviewed the arithmetic engine for input length, division-by-zero handling, decimal arithmetic, and clear/backspace behavior.
- The new 1.1.2 source has not yet completed CI build and automated test execution. No physical-device installation or UI test has been performed.
- Historical evidence: GitHub Actions run [38952074499](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/38952074499) completed for version 1.1.1; Gradle tests and debug APK assembly passed. Its result does not verify version 1.1.2.

## Findings and limits

- **Permissions:** none requested. The launcher activity is exported as required for Android launch and has only the MAIN/LAUNCHER intent filter.
- **Data handling:** entered values are transient UI state. This version does not implement history or persistent storage.
- **Network behavior:** no internet permission or network library is included in source.
- **Input and calculation:** numeric input is capped at 30 characters. Division by zero produces a readable error state. Division uses 18-digit precision with half-up rounding.
- **Build identity:** the 1.1.2 CI build, APK digest, signature, and smoke-test outcome are pending. Do not reuse the 1.1.1 APK digest as the identity of this release. The debug APK is for testing; no production signing review is claimed.
- **Compatibility and accessibility:** oversized controls and labels are present in source. The seal has a content description. Screen-reader and device layout behavior still need Android-device verification.

## Disposition

Suitable for source review. Build and automated test verification for 1.1.2 is pending; the previous 1.1.1 CI result does not verify this version. The seal is a static local image with an accessibility description. This is not an independent penetration test, a production signing review, or proof of device-tested behavior.
