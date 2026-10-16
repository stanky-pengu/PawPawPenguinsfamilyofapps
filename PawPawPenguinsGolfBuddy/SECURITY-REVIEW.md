# Security review — PawPawPenguins Golf Buddy 0.2.1

**Review type:** developer source and configuration review, 2026-10-16. This is not an independent penetration test or a formal security certification.

## Scope and findings

Reviewed the native Android source, manifest, and app build configuration for version 0.2.1. The update replaces the hand-drawn mascot with a local bundled image matching the Alarm Clock penguin. It adds no network, account, analytics, or third-party runtime behavior.

The app stores course name, date, pars, and hole scores in Android app-private preferences. Android backup is disabled. Sharing happens only when the user taps Share and invokes Android's share sheet. The manifest declares no permissions.

Package: `com.pawpawpenguins.golfbuddy`  
Version: `0.2.1` (versionCode 3)  
Minimum SDK: 26  
Target SDK: 36

## Checks

- Source review for network access and permission declarations: **PASS**
- Android manifest permission inspection: **PASS** — none declared
- Local-only mascot asset and version configuration review: **PASS**
- Android build, resource linking, APK signature, and SHA-256 verification: **NOT RUN** — this workspace does not have Gradle or Android SDK build tools installed
- On-device launch and interaction testing: **NOT RUN** — no Android device or emulator is connected

## Release status

No 0.2.1 APK is certified by this review. Build and verify the debug APK on an Android SDK-equipped machine, then repeat device testing. Before Play Store publication, create a release build with a protected, user-controlled upload key and review the exact release APK.
