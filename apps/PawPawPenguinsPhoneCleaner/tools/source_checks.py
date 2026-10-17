from pathlib import Path
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
manifest = ET.parse(ROOT / "app/src/main/AndroidManifest.xml").getroot()
android = "{http://schemas.android.com/apk/res/android}"
permissions = {item.get(android + "name") for item in manifest.findall("uses-permission")}
assert "android.permission.INTERNET" not in permissions
assert "android.permission.MANAGE_EXTERNAL_STORAGE" not in permissions
assert "android.permission.READ_MEDIA_IMAGES" in permissions
assert "android.permission.READ_MEDIA_VIDEO" in permissions
assert "android.permission.READ_MEDIA_VISUAL_USER_SELECTED" in permissions
assert "android.permission.READ_EXTERNAL_STORAGE" in permissions
assert "android.permission.WRITE_EXTERNAL_STORAGE" in permissions

scanner = (ROOT / "app/src/main/java/com/pawpawpenguins/phonecleaner/DuplicateScanner.java").read_text()
activity = (ROOT / "app/src/main/java/com/pawpawpenguins/phonecleaner/MainActivity.java").read_text()
policy = (ROOT / "app/src/main/java/com/pawpawpenguins/phonecleaner/DuplicatePolicy.java").read_text()
assert "MessageDigest.getInstance(\"SHA-256\")" in scanner
assert "MediaStore.Video.Media.EXTERNAL_CONTENT_URI" in scanner
assert "scanDevice(Context context, Uri downloadsTree)" in scanner
assert "oldest item in each exact-hash group is kept" in policy
assert "MediaStore.createDeleteRequest" in activity
assert "ACTION_OPEN_DOCUMENT_TREE" in activity
assert "for (DocumentFile file : tree.listFiles())" in scanner
assert "if (!file.isFile()" in scanner
assert "android:allowBackup=\"false\"" in (ROOT / "app/src/main/AndroidManifest.xml").read_text()
assert "FRIEND\\nMADE" in activity
assert "phone_cleaner_friends.jpg.base64" in (ROOT / "app/build.gradle").read_text()
assert "phone_cleaner_icon.jpg.base64" in (ROOT / "app/build.gradle").read_text()
assert "PawPawPenguins Phone Cleaner" in (ROOT / "README.md").read_text()
print("Source checks passed: permissions, local-only handling, exact hashing, duplicate policy, SAF, system media deletion, scene, seal, and app identity.")
