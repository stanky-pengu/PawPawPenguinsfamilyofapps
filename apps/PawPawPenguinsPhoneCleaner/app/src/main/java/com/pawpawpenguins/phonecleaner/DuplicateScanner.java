package com.pawpawpenguins.phonecleaner;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.DocumentsContract;
import androidx.documentfile.provider.DocumentFile;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class DuplicateScanner {
    static final class FileItem {
        final Uri uri;
        final String name;
        final String location;
        final long size;
        final long dateAdded;
        final boolean mediaStoreItem;

        FileItem(Uri uri, String name, String location, long size, long dateAdded, boolean mediaStoreItem) {
            this.uri = uri;
            this.name = name;
            this.location = location;
            this.size = size;
            this.dateAdded = dateAdded;
            this.mediaStoreItem = mediaStoreItem;
        }
    }

    private DuplicateScanner() {}

    static List<FileItem> scanScreenshots(Context context) throws Exception {
        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String pathColumnName = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? MediaStore.Images.Media.RELATIVE_PATH : MediaStore.Images.Media.DATA;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                pathColumnName
        };
        Map<Long, List<FileItem>> sameSize = new HashMap<>();
        try (Cursor cursor = resolver.query(collection, projection, null, null,
                MediaStore.Images.Media.DATE_ADDED + " ASC")) {
            if (cursor == null) return new ArrayList<>();
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            int pathColumn = cursor.getColumnIndex(pathColumnName);
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumn);
                String path = pathColumn >= 0 ? cursor.getString(pathColumn) : "";
                if (!isScreenshot(name, path)) continue;
                long size = cursor.getLong(sizeColumn);
                if (size <= 0) continue;
                long id = cursor.getLong(idColumn);
                Uri itemUri = Uri.withAppendedPath(collection, Long.toString(id));
                FileItem item = new FileItem(itemUri, name, path, size, cursor.getLong(dateColumn), true);
                sameSize.computeIfAbsent(size, ignored -> new ArrayList<>()).add(item);
            }
        }
        return findRedundant(context, sameSize);
    }

    /** Scans direct files only; subfolders are deliberately left untouched. */
    static List<FileItem> scanSelectedFolder(Context context, Uri treeUri) throws Exception {
        DocumentFile tree = DocumentFile.fromTreeUri(context, treeUri);
        Map<Long, List<FileItem>> sameSize = new HashMap<>();
        if (tree == null || !tree.isDirectory()) return new ArrayList<>();
        for (DocumentFile file : tree.listFiles()) {
            if (!file.isFile() || file.length() <= 0) continue;
            long dateAdded = file.lastModified() / 1000L;
            FileItem item = new FileItem(file.getUri(), file.getName() == null ? "File" : file.getName(),
                    "Selected Downloads folder", file.length(), dateAdded, false);
            sameSize.computeIfAbsent(item.size, ignored -> new ArrayList<>()).add(item);
        }
        return findRedundant(context, sameSize);
    }

    private static boolean isScreenshot(String name, String path) {
        String lowerName = name == null ? "" : name.toLowerCase(Locale.ROOT);
        String lowerPath = path == null ? "" : path.toLowerCase(Locale.ROOT);
        return lowerPath.contains("screenshot") || lowerName.contains("screenshot");
    }

    private static List<FileItem> findRedundant(Context context, Map<Long, List<FileItem>> sizeGroups) throws Exception {
        List<DuplicatePolicy.Candidate> hashed = new ArrayList<>();
        Map<String, FileItem> byUri = new HashMap<>();
        for (List<FileItem> sameSize : sizeGroups.values()) {
            if (sameSize.size() < 2) continue;
            for (FileItem item : sameSize) {
                String digest = sha256(context, item.uri);
                if (digest == null) continue;
                DuplicatePolicy.Candidate candidate = new DuplicatePolicy.Candidate(
                        item.size + ":" + digest, item.uri.toString(), item.dateAdded);
                hashed.add(candidate);
                byUri.put(item.uri.toString(), item);
            }
        }
        List<FileItem> duplicates = new ArrayList<>();
        for (DuplicatePolicy.Candidate duplicate : DuplicatePolicy.redundantCopies(hashed)) {
            FileItem item = byUri.get(duplicate.uri);
            if (item != null) duplicates.add(item);
        }
        return duplicates;
    }

    private static String sha256(Context context, Uri uri) {
        try (InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) return null;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[16 * 1024];
            int count;
            while ((count = input.read(buffer)) > 0) digest.update(buffer, 0, count);
            StringBuilder out = new StringBuilder(64);
            for (byte value : digest.digest()) out.append(String.format(Locale.ROOT, "%02x", value));
            return out.toString();
        } catch (Exception ignored) {
            return null;
        }
    }
}
