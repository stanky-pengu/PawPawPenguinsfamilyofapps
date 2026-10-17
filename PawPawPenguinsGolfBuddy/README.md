# PawPawPenguins Golf Buddy

Native Android golf scorecard app, version 0.2.2. The `android/` folder is the Android Studio/Gradle project. The earlier `index.html` prototype remains as a lightweight preview.

## App features

- 9-hole or 18-hole scorecards
- Hole-by-hole scores and editable par values from 3 to 6
- Running stroke total, course par, and score relative to par
- Saved rounds stored locally on the phone
- Share scorecard text with Android's share sheet
- Offline operation; no account, network calls, location, or runtime permissions
- Paired penguin and squirrel golf scene in the app header; both mascots hold golf clubs

## Open and build

Open the `android/` directory in Android Studio, let it sync the Gradle project, then build the debug variant. From a machine with Android SDK Platform 36 and Gradle network access, run:

    ./gradlew assembleDebug

The debug APK is for testing and uses a debug signing key. It cannot be published to Google Play. A release needs a user-controlled upload key, a release build, store listing assets, privacy disclosures, and device testing.

Package: `com.pawpawpenguins.golfbuddy`  
Minimum Android version: Android 8.0 (API 26)  
Target Android version: API 36  
Version: `0.2.2` (versionCode 4)
