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
- GitHub Actions run [39028548338](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/39028548338), run 21: **PASS** — Gradle unit tests, debug APK assembly, and the source arithmetic smoke test.
- APK SHA-256: `1c20007f50a977310dd8c7d184fb68ff33afbd612e8e5291b28c613b8073a7ea`.
- Downloaded CI artifact ZIP SHA-256: `e791ed89ec9c12082adbb18e3e0dd5517de6fff1cd0f46f05bedf31362715654`.
- APK signature verification was **not independently run** in this workspace; the APK is the Gradle debug build artifact. No 1.1.3 hash or test result is carried forward as evidence for this version.
- Physical-device appearance and accessibility checks remain **pending**.

## Limits and next checks

Check the scene crop and app title on the target phone, including a screen-reader description. CI checks cannot establish phone appearance or usability. The debug build is not a release-signed Play Store package; a persistent release key is needed for seamless in-place updates across builds.
