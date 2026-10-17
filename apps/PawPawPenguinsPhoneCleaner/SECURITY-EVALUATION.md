# Security Evaluation — PawPawPenguins Phone Cleaner 1.0.0

**Review date:** 2026-10-17  
**Version:** 1.0.0 (version code 1)  
**Package:** `com.pawpawpenguins.phonecleaner`  
**Review type:** Source review and change-focused build checks; not a penetration test or formal certification.

## Change summary

Rebuilt and branded the cleaner as PawPawPenguins Phone Cleaner. The app includes a bundled penguin-and-polar-bear photo-sorting scene, a gold FRIEND MADE seal, exact duplicate screenshot scanning, and exact duplicate file scanning in a user-selected Downloads folder.

## Privacy and security findings

- No Internet, account, analytics, advertising, location, Bluetooth, or all-files-access permission is declared.
- Screenshot scanning uses Android's photo access and hashes files locally with SHA-256. Results are limited to images Android lets the app read.
- Android 14+ selected-photo access is declared. When the user grants access only to selected pictures, the scan can only include those pictures.
- Downloads access is restricted to the folder selected through Android's system folder picker. The app scans only direct files and leaves subfolders untouched.
- Duplicate candidates must match byte-for-byte (same size and SHA-256). The oldest matching copy is kept.
- Screenshot deletion on Android 11+ uses Android's system approval dialog. Download duplicates are deleted only after the user reviews results and presses the delete button.
- The app does not send file names, images, hashes, or results off-device. App backup is disabled.

## Verification

- GitHub Actions [run 39034566907](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/actions/runs/39034566907): **PASS** — Gradle unit tests, debug APK assembly, and source checks.
- Source commit: [9c686c2](https://github.com/stanky-pengu/PawPawPenguinsfamilyofapps/commit/9c686c2d8f8a3ce82f88091dae54912477b4afe7).
- APK ZIP integrity: **PASS**.
- APK SHA-256: `85ea40ae8ca9f671d92ecdee3410d4ba7fd161caee2d0d4e03d0cc8ddb5b2e56`.
- CI artifact ZIP SHA-256: `2b686738d73ae50b48efcc0492b2dd0fe7b9a1c4a508cd8af36eefaf130e694b`.
- Physical-device scanning/deletion and appearance checks: **not performed**. Android's selection and deletion dialogs have not been exercised on the user's phone.
- Release signing: **not configured**. This is a debug APK, not a Play Store release.

## Limits

Android 14+ partial photo access can exclude screenshots not selected by the user. The Downloads folder must be selected explicitly. The clean-up list is built from the device's current accessible files; scan again after deletion to confirm the remaining duplicates.
