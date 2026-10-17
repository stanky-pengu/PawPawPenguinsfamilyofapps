# Security Evaluation — PawPawPenguins Phone Cleaner 1.0.0

**Review scope:** Source review and change-focused checks for this initial build; not a penetration test or formal security certification.

## Change summary

Branded as PawPawPenguins Phone Cleaner with a bundled penguin-and-polar-bear illustration and a gold FRIEND MADE seal. The app scans exact screenshot duplicates through Android MediaStore and exact duplicates in a user-selected Downloads folder through the Storage Access Framework. The oldest copy is retained; subfolders are not traversed.

## Privacy and security

- No Internet, analytics, advertising, account, location, Bluetooth, or all-files-access permission is declared.
- Screenshot reading uses Android's image permissions; Android 14+ selected-photo access is supported and may limit the scan to selected images.
- Downloads access is restricted to the folder explicitly selected by the user and persisted by Android.
- Files are compared locally by size and SHA-256; no file content or hash leaves the device.
- Screenshot deletion on Android 11+ is handed to Android's system delete-approval flow. Downloads copies are deleted only after the user reviews results and presses the app's delete button.
- The oldest exact match is kept. The app does not enter selected-folder subdirectories.
- App backup is disabled.

## Verification status

- Host-side duplicate policy tests: **PENDING CI**.
- Android source checks: **PENDING CI**.
- Debug APK assembly: **PENDING CI**.
- Physical-device scan/delete and appearance checks: **PENDING**.
- Play Store release signing: **not configured**; this project produces a debug APK.

This evaluation will be updated with the CI run and artifact SHA-256 before a downloadable build is delivered.
