package com.pawpawpenguins.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;
import java.util.TimeZone;

final class AlarmScheduler {
    private AlarmScheduler() {}
    static boolean canSchedule(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        return Build.VERSION.SDK_INT < 31 || manager.canScheduleExactAlarms();
    }
    static void scheduleAll(Context context) {
        if (!canSchedule(context)) return;
        AlarmStore store = new AlarmStore(context);
        for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) scheduleDay(context, store, day);
    }
    static void scheduleDay(Context context, AlarmStore store, int day) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarm = alarmPendingIntent(context, day);
        if (!store.enabled(day)) { manager.cancel(alarm); return; }
        long when = Schedule.nextOccurrence(day, store.hour(day), store.minute(day), System.currentTimeMillis(), TimeZone.getDefault());
        Intent show = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent showIntent = PendingIntent.getActivity(context, 700 + day, show, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        manager.setAlarmClock(new AlarmManager.AlarmClockInfo(when, showIntent), alarm);
    }
    static void scheduleNext(Context context, int day) { scheduleDay(context, new AlarmStore(context), day); }
    static PendingIntent alarmPendingIntent(Context context, int day) {
        Intent intent = new Intent(context, AlarmReceiver.class).putExtra("day", day);
        return PendingIntent.getBroadcast(context, 100 + day, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
