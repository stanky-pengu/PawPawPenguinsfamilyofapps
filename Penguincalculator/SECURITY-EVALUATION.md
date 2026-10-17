# Penguin Calculator — Source Security Evaluation

**Version:** 1.1.2 rebuild (version code 4)  
**Review date:** 2026-10-16  
**Scope:** Source, manifest, launcher icon resources, and in-app seal buddy. This is a source review, not a third-party audit or Play Store approval.

## Summary

The app performs arithmetic locally. The Android manifest requests no runtime permissions and declares no network permission. The reviewed source contains no advertising, analytics, account, location, Bluetooth, or cloud code. Calculator values are held in process memory and are not saved or transmitted by this implementation. Version 1.1.2 adds a locally bundled seal illustration beside the existing penguin. It does not change arithmetic, data handling, or requested permissions.

## Checks performed

- Reviewed the manifest for permissions, exported components, and launcher icon references.
- Reviewed adaptive and density-specific launcher images; they use the in-app penguin holding a calculator.
- Reviewed the bundled seal image and its accessible image description in the screen layout.
- Reviewed the source for network, external storage, account, analytics, and advertising integrations.
- Reviewed the arithmetic engine for input length, division-by-zero handling, decimal arithmetic, and clear/backspace behavior.
- GitHub Actions calculator workflow run [38990805561](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/38990805561) completed successfully for commit `2364bc75b9456b8d76b951eb58e3e92373678bff`. Gradle `test` and `assembleDebug` passed, and the standalone engine smoke checks passed.
- The APK package contains the new seal image and “Cute baby seal buddy” accessibility description. Source and packaged seal image SHA-256 values match.
- Compared with the saved 1.1.1 APK, the 1.1.2 APK has a different debug signing certificate. Android will reject it as an in-place update; uninstall 1.1.1 before installing this test build. Calculator values are transient and are not saved, so this app has no stored calculator history to preserve.
- No physical-device installation, visual layout test, or screen-reader test has been performed.

## Findings and limits

- **Permissions:** none requested. The launcher activity is exported as required for Android launch and has only the MAIN/LAUNCHER intent filter.
- **Data handling:** entered values are transient UI state. This version does not implement history or persistent storage.
- **Network behavior:** no internet permission or network library is included in source.
- **Input and calculation:** numeric input is capped at 30 characters. Division by zero produces a readable error state. Division uses 18-digit precision with half-up rounding.
- **Build identity:** version 1.1.2, version code 4, package `com.pawpawpenguins.calculator`. APK SHA-256: `7d7f58e7ac4823ed3e55f6376158826bc9df40f5f0723d3cc6d6d0835a51750a`. The debug signing certificate SHA-256 is `7d78bdfd09f0ccd8704c5e4e04eb696308f8cbe7e89b83a2b1552be296c10851`; the saved 1.1.1 APK uses `d3bff0d7fa9820623f53126c19c20c27e8c430c6397a22b4a07a250522f86225`. These are different test keys. The source archive digest is separate from the APK digest.
- **Compatibility and accessibility:** oversized controls and labels are present in source. The seal has a content description. Physical screen-reader behavior and responsive layout still need Android-device verification.

## Disposition

Suitable for source review and controlled testing after removing the earlier debug-signed version. CI verifies compilation and automated arithmetic checks, not on-device appearance or usability. The seal is a static local image with an accessibility description. This is not an independent penetration test, production signing review, or proof of device-tested behavior. A future update key must be preserved if in-place installs are needed.
