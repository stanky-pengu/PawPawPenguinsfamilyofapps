# Penguin Calculator — Source Security Evaluation

**Version:** 1.1.3 rebuild (version code 5)  
**Review date:** 2026-10-17  
**Scope:** Source, manifest, launcher icon resources, in-app seal buddy, and system-bar inset handling. This is a source review, not a third-party audit or Play Store approval.

## Summary

The app performs arithmetic locally. The Android manifest requests no runtime permissions and declares no network permission. The reviewed source contains no advertising, analytics, account, location, Bluetooth, or cloud code. Calculator values are held in process memory and are not saved or transmitted by this implementation. Version 1.1.3 preserves the seal buddy and adds padding from Android system-bar and display-cutout insets so the header and keypad are not covered by Android UI.

## Checks performed

- Reviewed the manifest for permissions, exported components, and launcher icon references.
- Reviewed adaptive and density-specific launcher images; they use the in-app penguin holding a calculator.
- Reviewed the bundled seal image and its accessible image description in the screen layout.
- Reviewed the source for network, external storage, account, analytics, and advertising integrations.
- Reviewed the arithmetic engine for input length, division-by-zero handling, decimal arithmetic, and clear/backspace behavior.
- Reviewed root-layout inset handling for status bar, navigation bar, and display cutouts through AndroidX Core.
- Calculator workflow run [38991598638](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/38991598638) completed successfully for 1.1.3, including Gradle unit tests and debug APK assembly.
- Verified CI artifact: `PenguinCalculator-1.1.3-debug.zip`, containing `app-debug.apk`. APK SHA-256: `17e11a8c00c8d16558d591fa0334aa22d32e04a41b25ca304a4e9cbdf81b69b9`.
- Physical-device layout and screen-reader checks remain pending; CI does not verify the visual behavior of Android system insets on a handset.

## Findings and limits

- **Permissions:** none requested. The launcher activity is exported as required for Android launch and has only the MAIN/LAUNCHER intent filter.
- **Data handling:** entered values are transient UI state. This version does not implement history or persistent storage.
- **Network behavior:** no internet permission or network library is included in source.
- **Input and calculation:** numeric input is capped at 30 characters. Division by zero produces a readable error state. Division uses 18-digit precision with half-up rounding.
- **Build identity:** version 1.1.3, version code 5, package `com.pawpawpenguins.calculator`. The delivered APK hash is recorded above. It is CI debug-signed, not release-signed.
- **Debug signing:** the saved 1.1.1 and 1.1.2 debug APKs use different certificates, so 1.1.2 could not update 1.1.1 in place. CI-generated debug keys are not a persistent release-signing setup; installing a later CI debug build may require uninstalling the previous one. Calculator input is transient and no saved history exists.
- **Compatibility and accessibility:** oversized controls and labels are present in source. The seal has a content description. The layout now applies system-bar and display-cutout insets, but physical screen-reader and layout behavior remain unverified.

## Disposition

Suitable for source review and controlled testing after build verification. CI can verify compilation and automated arithmetic checks, not on-device appearance or usability. This is not an independent penetration test, production signing review, or proof of device-tested behavior. A persistent private signing key is needed for seamless in-place updates across builds.
