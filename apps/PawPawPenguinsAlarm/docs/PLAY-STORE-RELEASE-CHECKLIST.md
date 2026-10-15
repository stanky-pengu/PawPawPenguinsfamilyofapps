# Play Store readiness — PawPawPenguins Alarm Clock

## Implemented in the 1.2.0 preparation source

- Target and compile SDK: Android 16 / API 36.
- Privacy policy available inside the app and in this public repository.
- No Internet permission, ads, analytics, or automatic server transmission.
- Diagnostics run only after the user taps the share button; the report is held in memory and passed to Android Sharesheet.
- Android Auto Backup disabled.
- Clear ringtone control releases saved URI access when permitted by Android.
- CI configured for source checks, JVM tests, emulator tests, debug APK, and release AAB build.

## Remaining before Google Play submission

1. **Play Console setup:** complete the Data safety form, Ads declaration, content rating, target audience/app access, and developer details. Base answers on the final production binary, including the user-initiated diagnostic share.
2. **Signing:** configure Play App Signing and use an upload key. CI builds an unsigned release AAB. Never commit a private keystore or key password.
3. **Store listing:** prepare the final title, short/full descriptions, high-resolution icon, screenshots, feature graphic, and contact details.
4. **Physical-device checks:** test an alarm while locked, notification permission allowed/denied, exact-alarm access allowed/denied, reboot rescheduling, ringtone preview and audible alarm playback, and the share-log flow. An emulator cannot confirm audible output from a phone speaker.
5. **Release validation:** review Play Console's pre-launch report and policy warnings, install the release-signed artifact, and compare its version and hash with the submitted bundle.

## Data Safety notes

The app has no account, server, ads, analytics, or automatic data transmission. It stores alarm settings and the selected ringtone reference locally for app function; it reads battery percentage for display and alarm volume for on-demand diagnostics. A user may explicitly share a diagnostic report through Android Sharesheet. Confirm how that intentional share maps to Play Console before submission. Update these notes if code, dependencies, or data flows change.
