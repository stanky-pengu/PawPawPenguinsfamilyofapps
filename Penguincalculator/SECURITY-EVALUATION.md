# Security Evaluation — PawPawPenguins Calculator 1.2.0

**Review date:** 2026-10-17  
**Version:** 1.2.0 (version code 7)  
**Package:** `com.pawpawpenguins.calculator`  
**Review type:** Source review and change-focused checks. This is not an independent penetration test or a formal security certification.

## Change summary

The landscape layout now provides a scientific keypad with trigonometric functions (degrees/radians), logarithms, square root, square, reciprocal, and pi. The app rebuilds its view on rotation while retaining calculator state. The portrait keypad, branding, and bundled teaching scene remain available. A small gold “FRIEND MADE” seal sits beside the brand label. The scientific functions add only local math operations; no permissions, network access, or data storage were added.

## Security and privacy findings

- The manifest declares no runtime permissions and no network permission.
- The launcher activity is exported only for Android's `MAIN` / `LAUNCHER` entry point.
- The reviewed app source has no account, analytics, advertising, location, Bluetooth, cloud, or external-storage integration.
- Calculator values remain in transient app memory; the app does not save or transmit them.
- The new illustration is a bundled local resource and makes no network requests.

## Verification status

- Source review: **PASS** for the listed permissions, data handling, and integrations.
- Change review: **PASS** for version code/name, landscape keypad/layout, responsive rebuild, local math functions, and the bundled, accessible lesson illustration.
- GitHub Actions run [39029410654](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/39029410654), calculator workflow run 31: **PASS** — Gradle unit tests, debug APK assembly, and arithmetic smoke tests.
- Unit tests cover degree-based trigonometry, logarithms, pi with a pending operation, and invalid input domains.
- APK SHA-256: `7c944b7a6c9a87774b0c5516a0e0580b8fab41292bf1a9c36f16f776831f581a`.
- Downloaded CI artifact ZIP SHA-256: `54b424cc2908eb00447279dc3d89dfdc7c4d57c50fb7a5384e6e4467d7190079`.
- APK signature verification and physical-device rotation/usability checks: **pending**.

## Limits and next checks

Check the scene crop, keypad sizing, rotation state preservation, and screen-reader descriptions on the target phone. The repository-wide emulator workflow tests a different app, so it does not verify this calculator's screen rotation. CI checks cannot establish phone appearance or usability. The debug build is not a release-signed Play Store package; a persistent release key is needed for seamless in-place updates across builds.
