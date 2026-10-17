#!/usr/bin/env python3
"""License-free CI source checks with a machine-readable JSONL test log."""

import json
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MAIN = ROOT / "app/src/main/java/com/pawpawpenguins/alarmclock"
checks = []


def check(name, condition, detail):
    row = {"test": name, "result": "PASS" if condition else "FAIL", "detail": detail}
    checks.append(row)
    print(json.dumps(row, ensure_ascii=False))


manifest_path = ROOT / "app/src/main/AndroidManifest.xml"
try:
    manifest = ET.parse(manifest_path).getroot()
    android = "{http://schemas.android.com/apk/res/android}"
    names = {node.attrib.get(android + "name", "") for node in manifest.iter()}
    app = manifest.find("application")
    check("manifest_xml", True, "Android manifest parses")
    check("alarm_permissions", "android.permission.SCHEDULE_EXACT_ALARM" in names and "android.permission.RECEIVE_BOOT_COMPLETED" in names, "exact alarms and reboot rescheduling are declared")
    check("alarm_service_declared", any(node.tag.endswith("service") and node.attrib.get(android + "name", "").endswith("AlarmService") for node in manifest.iter()), "alarm playback service is declared")
    check("no_internet_permission", "android.permission.INTERNET" not in names, "app declares no Internet permission")
    check("backup_disabled", app is not None and app.attrib.get(android + "allowBackup") == "false", "app data backup is disabled")
    check("privacy_screen_declared", any(node.tag.endswith("activity") and node.attrib.get(android + "name") == ".PrivacyPolicyActivity" for node in manifest.iter()), "in-app privacy screen is declared")
except Exception as exc:
    check("manifest_xml", False, f"{type(exc).__name__}: {exc}")

gradle = (ROOT / "app/build.gradle").read_text()
check("version_configured", "versionName '1.3.1'" in gradle and "versionCode 6" in gradle and "targetSdk 36" in gradle, "version 1.3.1 targets API 36")
check("unit_test_source", (ROOT / "app/src/test/java/com/pawpawpenguins/alarmclock/ScheduleTest.java").is_file(), "weekly schedule JUnit tests are present")
check("instrumentation_test_source", (ROOT / "app/src/androidTest/java/com/pawpawpenguins/alarmclock/MainActivityTest.java").is_file(), "Android UI instrumentation tests are present")
check("privacy_policy_asset", (ROOT / "app/src/main/assets/privacy-policy.txt").is_file(), "privacy policy is available offline in the app")
check("play_store_checklist", (ROOT / "docs/PLAY-STORE-RELEASE-CHECKLIST.md").is_file(), "release checklist is present")

main = (MAIN / "MainActivity.java").read_text()
service = (MAIN / "AlarmService.java").read_text()
store = (MAIN / "AlarmStore.java").read_text()
autotest = (MAIN / "AutoTestLog.java").read_text()
check("ringtone_picker", "ACTION_OPEN_DOCUMENT" in main and "takePersistableUriPermission" in main, "user-selected audio access is persisted")
check("ringtone_preview_media_volume", "AudioAttributes.USAGE_MEDIA" in main, "preview uses media audio channel")
check("alarm_uses_alarm_volume", "AudioAttributes.USAGE_ALARM" in service, "scheduled playback uses alarm audio channel")
check("ringtone_stored", "ringtone_uri" in store and "ringtone_name" in store, "selected URI and display name are stored locally")
check("ringtone_permission_released", "releasePersistableUriPermission" in main and "clearRingtone" in main, "replaced or cleared ringtone access is released")
check("battery_meter", 'setTag("battery_meter")' in main, "battery meter is present")
check("version_label", 'setTag("version_label")' in main, "version label is present")
check("friend_made_seal", 'setTag("friend_made_stamp")' in main and "FRIEND" in main and "MADE" in main and "0xfff5b94c" in main, "gold FRIEND MADE seal is present")
check("tinkerbell_scene_asset", (ROOT / "app/src/main/res/drawable-nodpi/tinkerbell_alarm_scene.jpg").is_file() and 'setImageResource(R.drawable.tinkerbell_alarm_scene)' in main, "Tinkerbell bed scene is bundled and shown on the main screen")
check("tinkerbell_scene_accessibility", 'setContentDescription("Tinkerbell sleeping under a penguin blanket while a penguin wakes beside an alarm clock")' in main and 'setTag("tinkerbell_bed_scene")' in main, "Tinkerbell scene has an accessibility description and test tag")
check("device_audio_diagnostics", '"alarm_volume"' in autotest and '"selected_ringtone_access"' in autotest, "on-demand logs check alarm volume and saved file access")
check("no_persistent_diagnostics", "FileWriter" not in autotest and main.count("AutoTestLog.run(this") == 1, "diagnostics run only on request and are not written to app storage")
check("penguin_loading_animation", (MAIN / "PenguinLoadingView.java").is_file() and 'setTag("penguin_loading")' in main, "animated mascot splash is present")

failed = sum(row["result"] == "FAIL" for row in checks)
print(json.dumps({"summary": "PASS" if failed == 0 else "FAIL", "passed": len(checks) - failed, "failed": failed}))
sys.exit(1 if failed else 0)
