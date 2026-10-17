# Security review — PawPawPenguins Golf Buddy 0.2.3

**Review type:** developer source review and reproducible CI build verification, 2026-10-16. This is not an independent penetration test or formal security certification.

## Scope and findings

Reviewed the native Android source, manifest, and build configuration for version 0.2.3. This patch adds a small gold “FRIEND MADE” seal beside the PawPawPenguins label in the app header. Scoring controls, local storage, sharing, and permissions are unchanged.

Course name, date, pars, and hole scores are stored in Android app-private preferences. Android backup is disabled. Sharing happens only when the user taps Share and invokes Android's share sheet. The manifest declares no permissions.

- Package: `com.pawpawpenguins.golfbuddy`
- Version: `0.2.3` (versionCode 5)
- Minimum SDK: 26
- Target SDK: 36
- Build and signature for version 0.2.3: **PENDING CI**.
- Previous version 0.2.2 verification: [GitHub Actions run 38992911588](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/38992911588) passed build and APK signature checks.
- Previous 0.2.2 APK SHA-256: `70efc25e19e92d9bda2ffd5591bf7968564e6559621e7479fd9172cbb6d96e23`.
- On-device launch and interaction testing: **NOT RUN** — no physical Android device was connected during this build

## Release status

The APK is a debug build signed with a debug key for testing. It is not a Play Store release artifact. Before publication, build a release variant with a protected, user-controlled upload key, test on supported Android devices, and review the exact release APK.
