package com.pawpawpenguins.alarmclock;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Builds diagnostics in memory only; no test log is written to app storage. */
final class AutoTestLog {
    private AutoTestLog() {}

    static String run(Context context, int batteryPercent) {
        StringBuilder log = new StringBuilder();
        check(log, "app_version", !BuildConfig.VERSION_NAME.isEmpty(), BuildConfig.VERSION_NAME);
        check(log, "battery_reading", batteryPercent >= 0 && batteryPercent <= 100, batteryPercent + "%");
        check(log, "alarm_scheduler", context.getSystemService(Context.ALARM_SERVICE) != null, "AlarmManager available");
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int alarmVolume = audio == null ? -1 : audio.getStreamVolume(AudioManager.STREAM_ALARM);
        int maxAlarmVolume = audio == null ? 0 : audio.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        check(log, "alarm_volume", alarmVolume > 0, alarmVolume + "/" + maxAlarmVolume + " (alarm stream)");
        String picked = new AlarmStore(context).ringtoneUri();
        if (picked != null) {
            boolean readable = false;
            String detail = "saved ringtone URI is not readable";
            try (android.os.ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(Uri.parse(picked), "r")) {
                readable = fd != null;
                detail = readable ? "saved ringtone file can be opened" : detail;
            } catch (Exception e) {
                detail = e.getClass().getSimpleName();
            }
            check(log, "selected_ringtone_access", readable, detail);
        } else {
            check(log, "selected_ringtone_access", true, "using Android default alarm tone");
        }
        long next = Schedule.nextOccurrence(java.util.Calendar.SUNDAY, 5, 0, System.currentTimeMillis(), TimeZone.getDefault());
        check(log, "weekly_schedule_calculation", next > System.currentTimeMillis(), "next Sunday 05:00 calculated");
        return log.toString();
    }

    /** Removes diagnostic files left by versions before the privacy update. */
    static void removeLegacyLogs(Context context) {
        File dir = new File(context.getFilesDir(), "test-logs");
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile() && file.getName().startsWith("startup-") && file.getName().endsWith(".jsonl")) {
                file.delete();
            }
        }
        String[] remaining = dir.list();
        if (remaining != null && remaining.length == 0) dir.delete();
    }

    private static void check(StringBuilder writer, String name, boolean pass, String detail) {
        String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(new Date());
        writer.append("{\"time\":").append(quote(time))
                .append(",\"test\":").append(quote(name))
                .append(",\"result\":").append(quote(pass ? "PASS" : "FAIL"))
                .append(",\"detail\":").append(quote(detail)).append("}\n");
    }

    private static String quote(String value) {
        StringBuilder escaped = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\': escaped.append("\\\\"); break;
                case '\"': escaped.append("\\\""); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if (c < 0x20) escaped.append(String.format(Locale.US, "\\u%04x", (int) c));
                    else escaped.append(c);
            }
        }
        return escaped.append('\"').toString();
    }
}
