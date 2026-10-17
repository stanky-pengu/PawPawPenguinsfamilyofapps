# Security Evaluation — PawPawPenguins Alarm Clock 1.2.1

**Review date:** 2026-10-17  
**Version:** 1.2.1 (version code 4)  
**Package:** `com.pawpawpenguins.alarmclock`  
**Review type:** Change-focused developer source review. This is not an independent penetration test or a formal security certification.

## Change summary

This patch adds a small gold “FRIEND MADE” seal beside the weekly-calendar heading. The seal is rendered locally in the existing screen. Alarm scheduling, ringtone handling, preferences, and app permissions are unchanged.

## Security and privacy

- The manifest requests exact-alarm scheduling, notifications, boot completion, and foreground media playback permissions for the alarm features.
- Alarm data and ringtone selection remain on the device, in app-private storage.
- No network permission, analytics, advertising, or external account integration is added by this patch.
- The seal uses native text and shapes; it adds no image download or third-party library.

## Verification status

- Source review of the seal change: **PASS**.
- Instrumentation test asserts the gold seal is present alongside existing version, battery, and ringtone controls.
- Gradle tests, APK build, and emulator instrumentation: **PENDING CI**.
- APK SHA-256 and signing verification: **PENDING CI**.
- Physical-device appearance and alarm behavior: **NOT TESTED** by this change review.

## Limits

CI and source review cannot confirm the seal's appearance on the target phone. The debug APK is for testing and is not a Play Store release-signed package.
