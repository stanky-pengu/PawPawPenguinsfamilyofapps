# Security Evaluation — PawPawPenguins Calculator 1.2.0

**Review date:** 2026-10-17  
**Version:** 1.2.0 (version code 7)  
**Package:** `com.pawpawpenguins.calculator`  
**Review type:** Source review and change-focused checks. This is not an independent penetration test or a formal security certification.

## Change summary

The landscape layout now provides a scientific keypad with trigonometric functions (degrees/radians), logarithms, square root, square, reciprocal, and pi. The app rebuilds its view on rotation while retaining calculator state. The portrait keypad, branding, and bundled teaching scene remain available. The scientific functions add only local math operations; no permissions, network access, or data storage were added.

## Security and privacy findings

- The manifest declares no runtime permissions and no network permission.
- The launcher activity is exported only for Android's `MAIN` / `LAUNCHER` entry point.
- The reviewed app source has no account, analytics, advertising, location, Bluetooth, cloud, or external-storage integration.
- Calculator values remain in transient app memory; the app does not save or transmit them.
- The new illustration is a bundled local resource and makes no network requests.

## Verification status

- Source review: **PASS** for the listed permissions, data handling, and integrations.
- Change review: **PENDING** CI build and package review for version code/name, responsive layouts, and accessibility descriptions.
- Unit tests now cover degree-based trigonometry, logarithms, pi in a pending operation, and invalid input domains; execution is **pending CI**.
- GitHub Actions run and APK hash: **pending**.
- APK signature verification and physical-device rotation/usability checks: **pending**.

## Limits and next checks

Check the scene crop, keypad sizing, rotation state preservation, and screen-reader descriptions on the target phone. CI checks cannot establish phone appearance or usability. The debug build is not a release-signed Play Store package; a persistent release key is needed for seamless in-place updates across builds.
