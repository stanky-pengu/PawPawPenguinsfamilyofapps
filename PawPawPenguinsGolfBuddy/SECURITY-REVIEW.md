# Security review — PawPawPenguins Golf Buddy 0.2.1

**Review type:** developer source review and reproducible CI build verification, 2026-10-16. This is not an independent penetration test or formal security certification.

## Scope and findings

Reviewed the native Android source, manifest, and build configuration for version 0.2.1. The update uses a bundled mascot image matched to the PawPawPenguins Alarm Clock penguin. It adds no network, account, analytics, or third-party runtime behavior.

Course name, date, pars, and hole scores are stored in Android app-private preferences. Android backup is disabled. Sharing happens only when the user taps Share and invokes Android's share sheet. The manifest declares no permissions.

- Package: `com.pawpawpenguins.golfbuddy`
- Version: `0.2.1` (versionCode 3)
- Minimum SDK: 26
- Target SDK: 36
- Build: **PASS** — GitHub Actions, Android SDK 36, Build Tools 36.0.0, Gradle 9.6.0
- APK signature: **PASS** — apksigner verified APK Signature Scheme v2; one Android Debug signer
- APK SHA-256: `3d515da89e20e9312ae6e70a75d77a83d59e06ccecc9afe7b99c371d4ff9ef37`
- On-device launch and interaction testing: **NOT RUN** — no physical Android device was connected during this build

## Release status

The APK is a debug build signed with a debug key for testing. It is not a Play Store release artifact. Before publication, build a release variant with a protected, user-controlled upload key, test on supported Android devices, and review the exact release APK.
