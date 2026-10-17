# Security Evaluation — PawPawPenguins Phone Cleaner 1.2.0

**Review scope:** Source review and change-focused checks for the penguin phone app icon; not a penetration test or formal security certification.

## Change summary

The launcher icon now shows the penguin mascot holding a phone. The automatic duplicate scan behavior from 1.1.0 is unchanged: it compares accessible photos and videos and, when configured, direct files in the selected Downloads folder. Android 14 selected-photo access can limit photo coverage. The user reviews matches and initiates deletion; Android confirms media deletion on Android 11 and newer.

## Privacy and security

- No Internet, analytics, advertising, account, location, Bluetooth, or all-files-access permission is declared.
- Photo/video reads use Android media permissions. If the user grants only selected-photo access or only one media type, scans cover only what Android exposes.
- Downloads access is limited to the folder explicitly selected by the user and persisted by Android; only direct children are scanned.
- Files are compared locally by size and SHA-256. File content, names, hashes, and results are not uploaded.
- The user reviews results and initiates deletion. Android system approval is used for MediaStore items on Android 11 and newer; selected Downloads files are deleted only after the user presses the delete button.
- App backup is disabled.

## Verification status

- Host-side duplicate policy tests: **PASS** in GitHub Actions run 39039288638.
- Android source checks: **PASS** in GitHub Actions run 39039288638.
- Debug APK assembly: **PENDING CI**.
- Physical-device scan/delete and appearance checks: **not performed** in this build environment.
- Play Store release signing: **not configured**; this project produces a debug APK.
