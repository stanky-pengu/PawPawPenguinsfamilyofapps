# PawPawPenguins Golf Buddy

Native Android golf scorecard app, version 0.2.0. The android/ folder is an Android Studio/Gradle project. The earlier index.html prototype remains as a lightweight preview.

## App features

- 9-hole or 18-hole scorecards
- Hole-by-hole scores and editable par values from 3 to 6
- Running stroke total, course par, and score relative to par
- Saved rounds stored locally on the phone
- Share scorecard text with Android's share sheet
- Custom penguin and golf-club artwork in the app and launcher icon
- Offline operation; no account, network calls, location, or other runtime permissions

## Open and build

Open the android/ directory in Android Studio, let it sync the Gradle project, then build the debug variant. From a machine with Android SDK Platform 36 and Gradle network access, run:

    ./gradlew assembleDebug

The debug APK is for testing. It is signed with a debug key and cannot be published to Google Play. A release must use a user-controlled upload key, a release build, store listing assets, privacy disclosures, and device testing.

The current downloadable test APK is PawPawPenguinsGolfBuddy-0.2.0-debug.apk. It was built from the native source with Android SDK Platform 36 and Build Tools 36.0.0, signed with a temporary debug key, and signature-verified.

Package: com.pawpawpenguins.golfbuddy
Minimum Android version: Android 8.0 (API 26)
Target Android version: API 36

