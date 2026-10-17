# Security Evaluation — PawPawPenguins Calculator 1.1.4

**Review date:** 2026-10-17  
**Version:** 1.1.4 (version code 6)  
**Package:** `com.pawpawpenguins.calculator`  
**Review type:** Source review and change-focused checks. This is not an independent penetration test or a formal security certification.

## Change summary

The app's display name is now **PawPawPenguins Calculator**. The two separate in-app mascot pictures have been replaced by one landscape illustration showing the harp seal teaching the penguin with a math board. The scene has an accessibility description. Calculator logic and permissions are unchanged.

## Security and privacy findings

- The manifest declares no runtime permissions and no network permission.
- The launcher activity is exported only for Android's `MAIN` / `LAUNCHER` entry point.
- The reviewed app source has no account, analytics, advertising, location, Bluetooth, cloud, or external-storage integration.
- Calculator values remain in transient app memory; the app does not save or transmit them.
- The new illustration is a bundled local resource and makes no network requests.

## Verification status

- Source review: **PASS** for the listed permissions, data handling, and integrations.
- Change review: **PASS** for the visible app name, version code/name, single scene resource reference, and accessibility description.
- Arithmetic engine source and existing unit tests were not changed.
- Android compilation, automated unit tests, APK signing, APK SHA-256, and physical-device appearance/accessibility checks are **pending** for 1.1.4. No 1.1.3 build hash or test result is carried forward as evidence for this version.

## Limits and next checks

Build and run the existing Gradle unit tests, inspect the packaged APK identity and permissions, verify the APK signature, and record its SHA-256. Then check the scene crop and app title on the target phone, including a screen-reader description. CI checks cannot establish phone appearance or usability. The debug build is not a release-signed Play Store package; a persistent release key is needed for seamless in-place updates across builds.
