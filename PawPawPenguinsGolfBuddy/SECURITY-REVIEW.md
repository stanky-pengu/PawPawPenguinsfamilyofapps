# Security review — PawPawPenguins Golf Buddy 0.2.3

**Review type:** developer source review and reproducible CI build verification, 2026-10-16. This is not an independent penetration test or formal security certification.

## Scope and findings

Reviewed the native Android source, manifest, and build configuration for version 0.2.3. This patch adds a small gold “FRIEND MADE” seal beside the PawPawPenguins label in the app header. Scoring controls, local storage, sharing, and permissions are unchanged.

Course name, date, pars, and hole scores are stored in Android app-private preferences. Android backup is disabled. Sharing happens only when the user taps Share and invokes Android's share sheet. The manifest declares no permissions.

- Package: `com.pawpawpenguins.golfbuddy`
- Version: `0.2.3` (versionCode 5)
- Minimum SDK: 26
- Target SDK: 36
- Build: **PASS** — [GitHub Actions run 39030319000](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/39030319000), Android SDK 36, Build Tools 36.0.0, Gradle 9.6.0.
- APK signature: **PASS** — apksigner verified APK Signature Scheme v2; one Android Debug signer.
- APK SHA-256: `fa31503e13c4c3f678b47596bc427f0fd81d6a99396c924873acb0884765cb8f`.
- Debug certificate SHA-256: `5b3771015aa0cbb51a0cfa2bbb5df21634517e886150a52e87c52d0578e2f398`.
- On-device launch and interaction testing: **NOT RUN** — no physical Android device was connected during this build

## Release status

The APK is a debug build signed with a debug key for testing. It is not a Play Store release artifact. Before publication, build a release variant with a protected, user-controlled upload key, test on supported Android devices, and review the exact release APK.
