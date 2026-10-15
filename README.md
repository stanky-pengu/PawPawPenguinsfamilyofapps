# PawPawPenguins Family of Apps

Public source and release home for PawPawPenguins apps.

## PawPawPenguins Alarm Clock

The Android alarm clock source is in [apps/PawPawPenguinsAlarm](apps/PawPawPenguinsAlarm/).

- **Current source version:** 1.2.0
- **Privacy policy:** [Read the policy](apps/PawPawPenguinsAlarm/PRIVACY_POLICY.md)
- **Installable public release:** none posted yet. A signed build will be added to GitHub Releases when it is ready for people to install. CI artifacts are test builds.

The app supports seven weekly alarms, ringtone selection and preview, alarm playback, a battery meter, and an animated penguin loading screen. It works offline. Alarm settings and ringtone selection stay on the device. Diagnostics run only when the user taps the share button; the app does not automatically save or transmit them.

### Build and test

Requires JDK 17, Gradle 8.13, Android SDK platform 36, and Android Build Tools 36.0.0.

From the repository root:

```sh
cd apps/PawPawPenguinsAlarm
gradle testDebugUnitTest assembleDebug assembleDebugAndroidTest bundleRelease
```

GitHub Actions also runs source checks, tests, and Android emulator instrumentation. The current CI release bundle is unsigned and is not a consumer installation package.
