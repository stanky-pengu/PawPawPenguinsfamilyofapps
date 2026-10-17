package com.pawpawpenguins.phonecleaner;

import android.Manifest;
import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.documentfile.provider.DocumentFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainActivity extends Activity {
    private static final int REQUEST_MEDIA_PERMISSION = 301;
    private static final int REQUEST_FOLDER = 302;
    private static final int REQUEST_DELETE = 303;
    private static final int REQUEST_LEGACY_WRITE = 304;
    private static final String PREFS = "cleaner_settings";
    private static final String KEY_DOWNLOADS_TREE = "downloads_tree";
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private LinearLayout resultList;
    private TextView status;
    private Button deleteButton;
    private List<DuplicateScanner.FileItem> currentDuplicates = new ArrayList<>();
    private boolean currentIsScreenshots;
    private Uri pendingFolderUri;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        buildScreen();
        showWelcome();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(232, 246, 252));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(16), dp(18), dp(28));
        scroll.addView(page);

        FrameLayout bannerFrame = new FrameLayout(this);
        ImageView banner = new ImageView(this);
        banner.setImageResource(R.drawable.phone_cleaner_friends);
        banner.setScaleType(ImageView.ScaleType.CENTER_CROP);
        banner.setContentDescription(getString(R.string.scene_accessibility));
        bannerFrame.addView(banner, new FrameLayout.LayoutParams(-1, dp(205)));
        GradientDrawable clip = new GradientDrawable();
        clip.setColor(Color.WHITE);
        clip.setCornerRadius(dp(24));
        bannerFrame.setBackground(clip);
        bannerFrame.setClipToOutline(true);
        TextView seal = new TextView(this);
        seal.setText("FRIEND\nMADE");
        seal.setGravity(Gravity.CENTER);
        seal.setTextColor(Color.rgb(255, 211, 105));
        seal.setTextSize(9);
        seal.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable sealShape = new GradientDrawable();
        sealShape.setShape(GradientDrawable.OVAL);
        sealShape.setColor(Color.rgb(42, 49, 57));
        sealShape.setStroke(dp(2), Color.rgb(218, 166, 55));
        seal.setBackground(sealShape);
        FrameLayout.LayoutParams sealParams = new FrameLayout.LayoutParams(dp(58), dp(58), Gravity.TOP | Gravity.END);
        sealParams.setMargins(0, dp(10), dp(10), 0);
        bannerFrame.addView(seal, sealParams);
        page.addView(bannerFrame, new LinearLayout.LayoutParams(-1, dp(205)));

        TextView title = text("PawPawPenguins Phone Cleaner", 26, Color.rgb(35, 62, 80), true);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dp(18);
        page.addView(title, titleParams);
        page.addView(text("A cozy way to tidy up exact duplicate files.", 15, Color.rgb(91, 117, 133), false));

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.VERTICAL);
        Button screenshots = makeButton("Find duplicate screenshots");
        screenshots.setOnClickListener(v -> requestScreenshotScan());
        Button downloads = makeButton("Choose Downloads folder");
        downloads.setOnClickListener(v -> chooseDownloadsFolder());
        Button scanDownloads = makeButton("Scan Downloads duplicates");
        scanDownloads.setOnClickListener(v -> scanDownloads());
        buttons.addView(screenshots, rowParams());
        buttons.addView(downloads, rowParams());
        buttons.addView(scanDownloads, rowParams());
        LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams(-1, -2);
        buttonsParams.topMargin = dp(16);
        page.addView(buttons, buttonsParams);

        status = text("Choose a scan to get started.", 15, Color.rgb(55, 82, 99), false);
        status.setPadding(dp(14), dp(14), dp(14), dp(14));
        status.setBackground(cardBackground());
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, -2);
        statusParams.topMargin = dp(12);
        page.addView(status, statusParams);

        TextView resultsHeading = text("Duplicates to review", 19, Color.rgb(35, 62, 80), true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dp(18);
        page.addView(resultsHeading, headingParams);
        resultList = new LinearLayout(this);
        resultList.setOrientation(LinearLayout.VERTICAL);
        page.addView(resultList, new LinearLayout.LayoutParams(-1, -2));

        deleteButton = makeButton("Delete duplicate copies");
        deleteButton.setEnabled(false);
        deleteButton.setAlpha(0.55f);
        deleteButton.setOnClickListener(v -> deleteCurrentDuplicates());
        LinearLayout.LayoutParams deleteParams = rowParams();
        deleteParams.topMargin = dp(10);
        page.addView(deleteButton, deleteParams);
        TextView footer = text("Exact matches only. One oldest copy is kept. Android confirms screenshot deletion.",
                12, Color.rgb(91, 117, 133), false);
        footer.setPadding(dp(6), dp(12), dp(6), 0);
        page.addView(footer);
        setContentView(scroll);
    }

    private void showWelcome() {
        Uri saved = savedDownloadsUri();
        if (saved != null) status.setText("Downloads folder remembered. Scan it whenever you’re ready.");
    }

    private void requestScreenshotScan() {
        if (!hasImagePermission()) {
            if (Build.VERSION.SDK_INT >= 34) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, REQUEST_MEDIA_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= 33) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_MEDIA_PERMISSION);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA_PERMISSION);
            }
            return;
        }
        runScan(true, null);
    }

    private boolean hasImagePermission() {
        if (Build.VERSION.SDK_INT >= 34) {
            return checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED;
        }
        if (Build.VERSION.SDK_INT >= 33) return checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void chooseDownloadsFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_FOLDER);
    }

    private Uri savedDownloadsUri() {
        String value = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_DOWNLOADS_TREE, null);
        return value == null ? null : Uri.parse(value);
    }

    private void scanDownloads() {
        Uri tree = savedDownloadsUri();
        if (tree == null) {
            status.setText("Choose the Downloads folder first. I’ll only inspect files directly inside it; subfolders stay untouched.");
            return;
        }
        runScan(false, tree);
    }

    private void runScan(boolean screenshots, Uri folder) {
        currentDuplicates = new ArrayList<>();
        deleteButton.setEnabled(false);
        deleteButton.setAlpha(0.55f);
        currentIsScreenshots = screenshots;
        clearRows();
        status.setText(screenshots ? "Checking screenshot files for exact matches…" : "Checking selected Downloads files…");
        worker.execute(() -> {
            try {
                List<DuplicateScanner.FileItem> found = screenshots
                        ? DuplicateScanner.scanScreenshots(this)
                        : DuplicateScanner.scanSelectedFolder(this, folder);
                runOnUiThread(() -> showResults(found, screenshots));
            } catch (Exception error) {
                runOnUiThread(() -> status.setText("I couldn’t read that location. Check its access and try again."));
            }
        });
    }

    private void showResults(List<DuplicateScanner.FileItem> found, boolean screenshots) {
        currentDuplicates = found;
        if (found.isEmpty()) {
            status.setText(screenshots
                    ? "No byte-for-byte duplicate screenshots found in the images this app can access."
                    : "No byte-for-byte duplicate files found directly in the selected folder.");
            return;
        }
        String area = screenshots ? "screenshot" : "file";
        status.setText(found.size() + " duplicate " + area + (found.size() == 1 ? " copy" : " copies")
                + " found. The oldest matching copy is kept.");
        for (DuplicateScanner.FileItem item : found) addResult(item);
        deleteButton.setEnabled(true);
        deleteButton.setAlpha(1f);
    }

    private void addResult(DuplicateScanner.FileItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackground(cardBackground());
        TextView name = text(item.name, 15, Color.rgb(35, 62, 80), true);
        TextView details = text(item.location + " • " + readableSize(item.size), 12, Color.rgb(91, 117, 133), false);
        card.addView(name);
        card.addView(details);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(7);
        resultList.addView(card, params);
    }

    private void deleteCurrentDuplicates() {
        if (currentDuplicates.isEmpty()) return;
        if (currentIsScreenshots && Build.VERSION.SDK_INT >= 30) {
            ArrayList<Uri> uris = new ArrayList<>();
            for (DuplicateScanner.FileItem item : currentDuplicates) uris.add(item.uri);
            try {
                startIntentSenderForResult(MediaStore.createDeleteRequest(getContentResolver(), uris).getIntentSender(),
                        REQUEST_DELETE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException error) {
                status.setText("Android couldn’t open its delete confirmation. Nothing was deleted.");
            }
            return;
        }
        if (!currentIsScreenshots) {
            int deleted = 0;
            for (DuplicateScanner.FileItem item : new ArrayList<>(currentDuplicates)) {
                try {
                    if (DocumentsContract.deleteDocument(getContentResolver(), item.uri)) deleted++;
                } catch (Exception ignored) { }
            }
            status.setText(deleted + " duplicate file(s) removed from the selected Downloads folder.");
            scanDownloads();
            return;
        }
        if (Build.VERSION.SDK_INT <= 28 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_LEGACY_WRITE);
            return;
        }
        int deleted = 0;
        for (DuplicateScanner.FileItem item : new ArrayList<>(currentDuplicates)) {
            try { deleted += getContentResolver().delete(item.uri, null, null); }
            catch (RecoverableSecurityException recoverable) {
                try {
                    startIntentSenderForResult(recoverable.getUserAction().getActionIntent().getIntentSender(),
                            REQUEST_DELETE, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException ignored) { }
                break;
            } catch (SecurityException ignored) { }
        }
        status.setText(deleted + " duplicate screenshot(s) removed.");
        requestScreenshotScan();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (hasImagePermission()) requestScreenshotScan();
            else status.setText("Photo access is off, so screenshots weren’t scanned.");
        } else if (requestCode == REQUEST_LEGACY_WRITE) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) deleteCurrentDuplicates();
            else status.setText("Storage write access was declined. Nothing was deleted.");
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOLDER && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri tree = data.getData();
            DocumentFile selected = DocumentFile.fromTreeUri(this, tree);
            String selectedName = selected == null || selected.getName() == null ? "" : selected.getName().toLowerCase(java.util.Locale.ROOT);
            if (!selectedName.equals("download") && !selectedName.equals("downloads")) {
                status.setText("Please choose the phone’s Download folder. I won’t scan a different folder by mistake.");
                return;
            }
            int flags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(tree, flags);
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_DOWNLOADS_TREE, tree.toString()).apply();
            status.setText("Downloads folder saved. I’ll scan only files directly in that folder; subfolders are left alone.");
        } else if (requestCode == REQUEST_DELETE && resultCode == RESULT_OK) {
            status.setText("Android approved the screenshot cleanup. Refreshing the list…");
            requestScreenshotScan();
        } else if (requestCode == REQUEST_DELETE) {
            status.setText("Deletion cancelled. Your screenshots were left in place.");
        }
    }

    private void clearRows() { resultList.removeAllViews(); }

    private Button makeButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.rgb(8, 137, 126));
        shape.setCornerRadius(dp(18));
        button.setBackground(shape);
        button.setMinHeight(dp(52));
        return button;
    }

    private LinearLayout.LayoutParams rowParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(8);
        return params;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private GradientDrawable cardBackground() {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.WHITE);
        shape.setCornerRadius(dp(20));
        return shape;
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    private String readableSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024L * 1024) return String.format(java.util.Locale.ROOT, "%.1f KB", size / 1024f);
        return String.format(java.util.Locale.ROOT, "%.1f MB", size / (1024f * 1024f));
    }

    @Override protected void onDestroy() {
        worker.shutdownNow();
        super.onDestroy();
    }
}
