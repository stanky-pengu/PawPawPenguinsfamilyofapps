package com.pawpawpenguins.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        int day = intent.getIntExtra("day", -1);
        if (day >= 1 && day <= 7) AlarmScheduler.scheduleNext(context, day);
        Intent service = new Intent(context, AlarmService.class).setAction(AlarmService.ACTION_START);
        if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(service); else context.startService(service);
    }
}
