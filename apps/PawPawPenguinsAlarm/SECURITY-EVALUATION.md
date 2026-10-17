# Security Evaluation — PawPawPenguins Alarm Clock 1.3.1

**Review date:** 2026-10-17  
**Version:** 1.3.1 (version code 6)  
**Package:** `com.pawpawpenguins.alarmclock`  
**Review type:** Source review and change-focused checks; not a penetration test or formal certification.

## Change summary

Restores the full Tinkerbell sleeping-bed scene from 1.3.0 and keeps the small gold FRIEND MADE seal. The scene is a bundled local image with an accessibility description. Version 1.3.1 can update 1.3.0 using the same app package.

## Security and privacy findings

- The manifest declares no Internet permission.
- App data backup is disabled.
- Alarm schedule, ringtone selection, and test data remain on device; no account, analytics, advertising, or cloud integration was found in the reviewed source.
- The app requests notification permission on Android 13+ and declares exact-alarm and reboot permissions needed for scheduled alarms.
- The Tinkerbell scene is bundled in the APK and makes no network requests.

## Verification

- Source checks: **PASS — 25 checks**, including package permissions, version, seal, scene asset, accessibility label, and existing diagnostics constraints.
- Gradle JVM tests, debug APK assembly, and release bundle assembly: **PASS**.
- GitHub Actions run [39031993350](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/39031993350), run 74.
- Android emulator installation/UI test: **NOT COMPLETED**. The hosted emulator failed before installation with `cmd: Can't find service: package`; this is an emulator environment failure, not a passed app UI test.
- APK archive integrity: **PASS**.
- APK SHA-256: `9cd903ce9d75e9847a49248fe5dbaeba0e6dc259b9aa33249882135e6b30d1fe`.
- CI artifact ZIP SHA-256: `61162fe597204d42de852ce0894387cde6267da070762453ebb0a2ac2b199ff5`.
- Signature verification and physical-device appearance check: **not independently completed**. This is a debug build, not a release-signed Play Store package.

## Limits

The emulator failure leaves the scene’s on-device crop and layout unverified in this run. Review the screen on a physical device when convenient. Alarm scheduling behavior was not changed by this scene restoration.
