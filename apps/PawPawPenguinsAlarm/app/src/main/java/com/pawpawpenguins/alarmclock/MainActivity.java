package com.pawpawpenguins.alarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Typeface;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int PICK_AUDIO = 101;
    private static final int INK = Color.rgb(28, 55, 78), MUTED = Color.rgb(111, 132, 150), BLUE = Color.rgb(38, 119, 185), BG = Color.rgb(232, 246, 255);
    private static final String[] DAYS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private AlarmStore store;
    private LinearLayout root, weekList;
    private TextView batteryView, ringtoneNameView, statusView;
    private Ringtone preview;
    private MediaPlayer previewPlayer;
    private final Handler handler = new Handler();
    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) { updateBattery(intent); }
    };

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG); getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        store = new AlarmStore(this);
        AutoTestLog.removeLegacyLogs(this);
        buildScreen();
        refreshRingtoneName();
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS") != android.content.pm.PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 12);
    }
    @Override protected void onResume() {
        super.onResume();
        Intent sticky = registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); updateBattery(sticky);
        if (AlarmScheduler.canSchedule(this)) { AlarmScheduler.scheduleAll(this); renderDays(); statusView.setText("Alarms are scheduled by Android. Battery and ringtone stay on this device."); }
        else { statusView.setText("Allow Alarms & reminders for dependable wake-ups. Turn on a day to open Android settings."); }
    }
    @Override protected void onPause() { try { unregisterReceiver(batteryReceiver); } catch (Exception ignored) { } stopPreview(); super.onPause(); }

    private void buildScreen() {
        ScrollView scroller = new ScrollView(this); scroller.setFillViewport(true); scroller.setBackgroundColor(BG);
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(18), dp(14), dp(18), dp(26)); scroller.addView(root);
        FrameLayout screen = new FrameLayout(this);
        screen.addView(scroller, new FrameLayout.LayoutParams(-1, -1));
        setContentView(screen);
        PenguinLoadingView loading = new PenguinLoadingView(this);
        loading.setTag("penguin_loading");
        loading.setOnAnimationEndListener(() -> loading.animate().alpha(0f).setDuration(220).withEndAction(() -> screen.removeView(loading)));
        screen.addView(loading, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout titleRow = new LinearLayout(this); titleRow.setGravity(Gravity.CENTER_VERTICAL); root.addView(titleRow);
        TextView title = text("Your weekly calendar", 27, INK, true); title.setTag("calendar_title");
        titleRow.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        TextView friendSeal = friendMadeStamp();
        LinearLayout.LayoutParams sealParams = new LinearLayout.LayoutParams(dp(40), dp(36)); sealParams.leftMargin = dp(8);
        titleRow.addView(friendSeal, sealParams);
        TextView subtitle = text("Tap a time to change it. Turn on only the days you need.", 15, MUTED, false); marginTop(subtitle, 4); root.addView(subtitle);
        ImageView bedScene = new ImageView(this);
        bedScene.setImageResource(R.drawable.tinkerbell_alarm_scene);
        bedScene.setContentDescription("Tinkerbell sleeping under a penguin blanket while a penguin wakes beside an alarm clock");
        bedScene.setTag("tinkerbell_bed_scene");
        bedScene.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bedScene.setBackground(round(Color.WHITE, 18));
        bedScene.setClipToOutline(true);
        LinearLayout.LayoutParams sceneParams = new LinearLayout.LayoutParams(-1, dp(156));
        sceneParams.topMargin = dp(12);
        root.addView(bedScene, sceneParams);
        batteryView = text("🔋 Battery --%", 14, INK, true); batteryView.setTag("battery_meter"); batteryView.setPadding(dp(12), dp(11), dp(12), dp(11)); batteryView.setBackground(round(0xfff7fcff, 16)); marginTop(batteryView, 14); root.addView(batteryView);
        weekList = new LinearLayout(this); weekList.setOrientation(LinearLayout.VERTICAL); marginTop(weekList, 8); root.addView(weekList); renderDays();

        LinearLayout ringCard = new LinearLayout(this); ringCard.setOrientation(LinearLayout.VERTICAL); ringCard.setPadding(dp(18), dp(18), dp(18), dp(18)); ringCard.setBackground(round(Color.WHITE, 24)); marginTop(ringCard, 18); root.addView(ringCard);
        ringCard.addView(text("Your wake-up ringtone", 21, INK, true));
        ringtoneNameView = text(store.ringtoneName(), 14, MUTED, false); ringtoneNameView.setTag("ringtone_name"); marginTop(ringtoneNameView, 8); ringCard.addView(ringtoneNameView);
        LinearLayout buttons = new LinearLayout(this); buttons.setOrientation(LinearLayout.HORIZONTAL); buttons.setGravity(Gravity.CENTER_VERTICAL); marginTop(buttons, 14); ringCard.addView(buttons);
        Button choose = button("Choose my ringtone", BLUE); choose.setTag("choose_ringtone"); choose.setOnClickListener(v -> chooseRingtone()); buttons.addView(choose, new LinearLayout.LayoutParams(0, dp(48), 1));
        Button previewButton = button("Preview", 0xff609bb8); previewButton.setTag("preview_ringtone"); previewButton.setOnClickListener(v -> previewRingtone()); LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, dp(48), 1); bp.leftMargin = dp(10); buttons.addView(previewButton, bp);
        TextView note = text("Preview uses media volume. Wake-up alarms use alarm volume.", 13, MUTED, false); marginTop(note, 13); ringCard.addView(note);
        Button defaultTone = button("Use phone's default alarm sound", 0xffebf7ff); defaultTone.setTag("default_ringtone"); defaultTone.setOnClickListener(v -> clearRingtone()); marginTop(defaultTone, 8); ringCard.addView(defaultTone);

        statusView = text("Alarms are scheduled by Android. Allow Alarms & reminders and notifications for dependable wake-ups.", 13, MUTED, false); statusView.setGravity(Gravity.CENTER); statusView.setTag("alarm_help"); marginTop(statusView, 20); root.addView(statusView);
        TextView version = text("PawPawPenguins Alarm Clock · v" + BuildConfig.VERSION_NAME, 12, MUTED, false); version.setGravity(Gravity.CENTER); version.setTag("version_label"); marginTop(version, 12); root.addView(version);
        Button diagnostics = button("Run checks & share test log", 0xff367f9c); diagnostics.setTag("test_log_button"); diagnostics.setOnClickListener(v -> shareTestLog()); marginTop(diagnostics, 12); root.addView(diagnostics);
        Button privacy = button("Privacy Policy", 0xff367f9c); privacy.setTag("privacy_policy"); privacy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyPolicyActivity.class))); marginTop(privacy, 8); root.addView(privacy);
    }
    private TextView friendMadeStamp() {
        TextView seal = text("FRIEND\nMADE", 7, 0xfff5b94c, true);
        seal.setGravity(Gravity.CENTER); seal.setLineSpacing(0, 0.88f);
        seal.setContentDescription("Gold FRIEND MADE PawPawPenguins seal"); seal.setTag("friend_made_stamp");
        GradientDrawable badge = new GradientDrawable(); badge.setShape(GradientDrawable.OVAL); badge.setColor(INK); badge.setStroke(dp(1), 0xfff5b94c);
        seal.setBackground(badge); return seal;
    }

    private void renderDays() {
        if (weekList == null) return; weekList.removeAllViews();
        for (int day = 1; day <= 7; day++) {
            final int d = day;
            LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.HORIZONTAL); card.setGravity(Gravity.CENTER_VERTICAL); card.setPadding(dp(16), dp(12), dp(13), dp(12)); card.setBackground(round(Color.WHITE, 23)); LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, dp(92)); cp.topMargin = dp(8); weekList.addView(card, cp);
            LinearLayout labels = new LinearLayout(this); labels.setOrientation(LinearLayout.VERTICAL); card.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
            TextView name = text(DAYS[day - 1], 18, INK, true); name.setTag("day_name_" + day); labels.addView(name);
            TextView summary = text(alarmSummary(day), 13, MUTED, false); summary.setTag("day_status_" + day); labels.addView(summary);
            Button time = button(formatTime(store.hour(day), store.minute(day)), 0xffebf7ff); time.setTextColor(0xff326d8a); time.setTag("time_" + day); LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(dp(122), dp(50)); card.addView(time, tp); time.setOnClickListener(v -> new TimePickerDialog(this, (view, hour, minute) -> { store.setTime(d, hour, minute); AlarmScheduler.scheduleDay(this, store, d); renderDays(); }, store.hour(d), store.minute(d), false).show());
            Switch enabled = new Switch(this); enabled.setTag("enabled_" + day); enabled.setChecked(store.enabled(day)); LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-2, -2); sp.leftMargin = dp(9); card.addView(enabled, sp);
            enabled.setOnCheckedChangeListener((button, checked) -> { store.setEnabled(d, checked); if (checked && !AlarmScheduler.canSchedule(this)) { button.setChecked(false); store.setEnabled(d, false); promptExactAlarm(); return; } AlarmScheduler.scheduleDay(this, store, d); renderDays(); });
        }
    }
    private String alarmSummary(int day) { return store.enabled(day) ? "Rings at " + formatTime(store.hour(day), store.minute(day)) : "Alarm off"; }
    private String formatTime(int hour, int minute) { Calendar c = Calendar.getInstance(); c.set(Calendar.HOUR_OF_DAY, hour); c.set(Calendar.MINUTE, minute); return android.text.format.DateFormat.getTimeFormat(this).format(c.getTime()); }
    private void promptExactAlarm() { showToast("Allow Alarms & reminders, then turn the day on again."); if (Build.VERSION.SDK_INT >= 31) try { startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName()))); } catch (Exception ignored) { } }
    private void chooseRingtone() { Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT); picker.addCategory(Intent.CATEGORY_OPENABLE); picker.setType("audio/*"); picker.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION); try { startActivityForResult(picker, PICK_AUDIO); } catch (Exception e) { showToast("Could not open the ringtone picker."); } }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_AUDIO || resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        String previousRingtone = store.ringtoneUri();
        try { getContentResolver().takePersistableUriPermission(uri, data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch (Exception ignored) { }
        String name = uri.getLastPathSegment() == null ? "Selected audio" : uri.getLastPathSegment();
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) { if (cursor != null && cursor.moveToFirst()) { int col = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME); if (col >= 0) name = cursor.getString(col); } } catch (Exception ignored) { }
        store.setRingtone(uri.toString(), name);
        if (previousRingtone != null && !previousRingtone.equals(uri.toString())) releaseRingtoneAccess(previousRingtone);
        refreshRingtoneName(); showToast("Ringtone chosen.");
    }
    private void refreshRingtoneName() { if (ringtoneNameView != null) ringtoneNameView.setText(store.ringtoneName()); }
    private void clearRingtone() {
        stopPreview();
        String previous = store.ringtoneUri();
        store.clearRingtone();
        if (previous != null) releaseRingtoneAccess(previous);
        refreshRingtoneName();
        showToast("Using the phone's default alarm sound.");
    }
    private void releaseRingtoneAccess(String uri) {
        try { getContentResolver().releasePersistableUriPermission(Uri.parse(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch (Exception ignored) { }
    }
    private void previewRingtone() {
        stopPreview();
        String picked = store.ringtoneUri();
        if (picked != null) {
            try {
                previewPlayer = new MediaPlayer();
                // Previews follow media volume; actual alarms use alarm volume.
                previewPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
                previewPlayer.setDataSource(this, Uri.parse(picked));
                previewPlayer.setOnPreparedListener(player -> { player.start(); handler.postDelayed(this::stopPreview, 8000); });
                previewPlayer.setOnCompletionListener(player -> stopPreview());
                previewPlayer.setOnErrorListener((player, what, extra) -> { stopPreview(); showToast("Could not play that ringtone. Choose another audio file."); return true; });
                previewPlayer.prepareAsync();
            } catch (Exception e) { stopPreview(); showToast("Could not play that ringtone. Choose another audio file."); }
            return;
        }
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            preview = RingtoneManager.getRingtone(this, uri);
            if (preview == null) throw new IllegalStateException("The selected audio could not be opened.");
            if (Build.VERSION.SDK_INT >= 21) preview.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
            if (Build.VERSION.SDK_INT >= 28) preview.setLooping(false);
            preview.play();
            handler.postDelayed(this::stopPreview, 8000);
        } catch (Exception e) { stopPreview(); showToast("Could not play that ringtone. Choose another audio file."); }
    }
    private void stopPreview() { handler.removeCallbacksAndMessages(null); if (preview != null) { try { preview.stop(); } catch (Exception ignored) { } preview = null; } if (previewPlayer != null) { try { if (previewPlayer.isPlaying()) previewPlayer.stop(); } catch (Exception ignored) { } try { previewPlayer.release(); } catch (Exception ignored) { } previewPlayer = null; } }
    private void updateBattery(Intent intent) { int percent = batteryPercent(intent); batteryView.setText(percent < 0 ? "🔋 Battery unavailable" : "🔋 Battery " + percent + "%" + (intent != null && intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING ? " · charging" : "")); }
    private int batteryPercent(Intent intent) { if (intent == null) return -1; int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1); return level < 0 || scale <= 0 ? -1 : Math.round(level * 100f / scale); }
    private void shareTestLog() {
        Intent sticky = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        String body = AutoTestLog.run(this, batteryPercent(sticky));
        Intent send = new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_SUBJECT, "PawPawPenguins test log").putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(send, "Share test log"));
    }
    private void showToast(String text) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show(); }
    private TextView text(String value, int size, int color, boolean bold) { TextView v = new TextView(this); v.setText(value); v.setTextSize(size); v.setTextColor(color); if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return v; }
    private Button button(String label, int color) { Button b = new Button(this); b.setText(label); b.setTextSize(14); b.setAllCaps(false); if (color == BLUE || color == 0xff609bb8 || color == 0xff367f9c) { b.setTextColor(Color.WHITE); b.setBackground(round(color, 10)); } else { b.setTextColor(INK); b.setBackground(round(color, 10)); } return b; }
    private android.graphics.drawable.GradientDrawable round(int color, int radius) { android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private void marginTop(View view, int dp) { ViewGroup.LayoutParams raw = view.getLayoutParams(); if (raw instanceof ViewGroup.MarginLayoutParams) ((ViewGroup.MarginLayoutParams) raw).topMargin = dp(dp); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
