# PawPawPenguins Phone Cleaner

Android app for finding exact duplicate photos, videos, and files in a user-selected Downloads folder.

## Duplicate handling

- The app starts a duplicate scan when opened and can be rescanned with **Scan phone for duplicates**.
- The scan compares photos and videos Android allows the app to access. On Android 14+, selected-photo access may limit results to the photos the user chose.
- If the user selected the Downloads folder, the automatic scan includes files directly inside it. Subfolders are not traversed.
- Files are grouped only when their sizes and SHA-256 hashes match. The oldest matching copy is kept.
- The user reviews results and presses **Delete duplicate copies**. Android's system approval is used for media deletion on Android 11 and newer.
- Deletion never happens automatically.

## Privacy

All scanning and hashing run locally. The app does not request Internet or all-files-access permission and does not upload file names, media, hashes, or scan results. Media permissions are requested for photo and video scanning. Downloads access is limited to the folder explicitly selected in Android's folder picker.

## Build

```bash
gradle test assembleDebug
```

Version: 1.2.0 (version code 3). Package: `com.pawpawpenguins.phonecleaner`.
