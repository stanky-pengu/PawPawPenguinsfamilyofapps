# PawPawPenguins Phone Cleaner

Android app for finding exact duplicate screenshots and exact duplicate files directly inside a Downloads folder selected by the user.

## Duplicate handling

- Files are grouped only when their sizes and SHA-256 hashes match.
- The oldest matching file is kept; only the other copies are listed for cleanup.
- Screenshot scanning is limited to image entries identified as screenshots by their folder or filename.
- Downloads scanning inspects files immediately inside the user-selected folder. It does not walk into subfolders.
- The user reviews the duplicate list and presses **Delete duplicate copies**. Android displays its own delete approval for screenshots on Android 11 and newer.

## Privacy

All scanning and hashing run locally. The app does not request Internet access and does not upload file names, images, hashes, or scan results. Photo access is requested only for screenshot scanning. The Downloads folder is accessed only after the user selects it in Android's folder picker.

## Build

```bash
gradle test assembleDebug
```

Version: 1.0.0 (version code 1). Package: `com.pawpawpenguins.phonecleaner`.
