PawPawPenguins Alarm Clock Privacy Policy
Last updated: October 15, 2026

## Who this policy covers
This policy describes PawPawPenguins Alarm Clock, an offline Android alarm clock.

Developer/privacy contact: penguindefensive@gmail.com

## Information the app uses
The app does not require an account and does not send information to a developer server. It has no Internet permission, advertising, analytics, or crash-reporting SDK.

To provide alarm features, the app stores the alarm days, times, on/off choices, and the selected ringtone's display name and document URI in app-private Android preferences. This information remains on the device so alarms can be scheduled and restored after a reboot.

The app reads the phone's battery percentage to display the battery meter. When you choose “Run checks & share test log,” it also reads the current alarm-volume level and checks whether the selected ringtone file can be opened. The diagnostic report includes the time of the check, app version, battery percentage, alarm-volume level, ringtone-access result, and a weekly schedule calculation. Diagnostics are not run automatically and are not saved to app storage. They are created in memory only when you tap the button.

If you choose to share a diagnostic report, Android opens its Sharesheet so you can choose another app. The app does not send the report by itself. The recipient app may store or transmit what you chose to share under its own policy.

## Permissions and why they are used
- **Alarms & reminders / exact alarms:** schedules alarms at the times and days you choose. Android may ask you to allow exact alarms in system settings.
- **Notifications:** shows alarm controls and status while an alarm is sounding. Android 13 and newer ask for notification permission.
- **Receive boot completed:** restores your chosen alarm schedule after the phone restarts.
- **Foreground service and media playback:** keeps alarm audio playing while the app is in the background or the screen is off.
- **Selected audio file read access:** lets the app preview and play the ringtone file you choose. Access is read-only. Replacing the tone or choosing the phone's default releases the old saved access where Android permits.

The app does not request location, contacts, camera, microphone, or Internet access.

## Storage, retention, and deletion
Alarm settings and ringtone selection stay in app-private storage until you change them, clear the app's storage, or uninstall the app. Android Auto Backup is disabled for this app. Device-transfer behavior may vary by Android version or manufacturer.

Diagnostic reports exist only in memory until the share flow ends. If you choose to share one, the app you choose may keep its copy. Contact that app or delete its copy there.

To remove saved alarms and settings, use Android Settings to clear the app's storage or uninstall the app. To stop using a selected ringtone while keeping your other alarms, tap “Use phone's default alarm sound.” The app has no accounts or server-held user data.

## Changes
If the app's data handling changes, this policy and the Play Store Data safety information will be updated before the changed version is released.
