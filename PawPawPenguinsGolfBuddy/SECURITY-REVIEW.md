# Security review — PawPawPenguins Golf Buddy 0.2.2

**Review type:** developer source review and reproducible CI build verification, 2026-10-16. This is not an independent penetration test or formal security certification.

## Scope and findings

Reviewed the native Android source, manifest, and build configuration for version 0.2.2. The update adds a bundled illustration of the penguin and squirrel playing golf together; both mascots hold golf clubs. The image is displayed in the existing compact app header. No scoring controls or data handling changed.

Course name, date, pars, and hole scores are stored in Android app-private preferences. Android backup is disabled. Sharing happens only when the user taps Share and invokes Android's share sheet. The manifest declares no permissions.

- Package: `com.pawpawpenguins.golfbuddy`
- Version: `0.2.2` (versionCode 4)
- Minimum SDK: 26
- Target SDK: 36
- Build: **PASS** — [GitHub Actions run 38992911588](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/38992911588), Android SDK 36, Build Tools 36.0.0, Gradle 9.6.0
- APK signature: **PASS** — apksigner verified APK Signature Scheme v2; one Android Debug signer
- APK SHA-256: `70efc25e19e92d9bda2ffd5591bf7968564e6559621e7479fd9172cbb6d96e23`
- Debug certificate SHA-256: `5125e9ffdc1e170ee20fc2f4dce806aa071e1bac4b2ec5b9ff7687ed0af0ca43`
- On-device launch and interaction testing: **NOT RUN** — no physical Android device was connected during this build

## Release status

The APK is a debug build signed with a debug key for testing. It is not a Play Store release artifact. Before publication, build a release variant with a protected, user-controlled upload key, test on supported Android devices, and review the exact release APK.
