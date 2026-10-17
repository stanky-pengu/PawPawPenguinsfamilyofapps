# Security Evaluation — PawPawPenguins Phone Cleaner 1.1.0

**Review scope:** Source review and change-focused checks for the automatic duplicate scan; not a penetration test or formal security certification.

## Change summary

The app now starts scanning when opened. It compares photos and videos available through Android MediaStore and, when configured, direct files in the user-selected Downloads folder. It hashes locally, keeps the oldest exact match, and presents redundant copies for review. Android 14 selected-photo access can limit photo coverage to selected items. The app does not delete anything until the user invokes cleanup; Android confirms media deletion on Android 11 and newer.

## Privacy and security

- No Internet, analytics, advertising, account, location, Bluetooth, or all-files-access permission is declared.
- Photo/video reads use Android media permissions. If the user grants only selected-photo access or only one media type, scans cover only what Android exposes.
- Downloads access is limited to the folder explicitly selected by the user and persisted by Android; only direct children are scanned.
- Files are compared locally by size and SHA-256. File content, names, hashes, and results are not uploaded.
- The user reviews results and initiates deletion. Android system approval is used for MediaStore items on Android 11 and newer; selected Downloads files are deleted only after the user presses the delete button.
- App backup is disabled.

## Verification status

- Host-side duplicate policy tests: **PENDING**.
- Android source checks: **PENDING**.
- Debug APK assembly: **PENDING**.
- Physical-device scan/delete and appearance checks: **PENDING**.
- Play Store release signing: **not configured**; this project produces a debug APK.
