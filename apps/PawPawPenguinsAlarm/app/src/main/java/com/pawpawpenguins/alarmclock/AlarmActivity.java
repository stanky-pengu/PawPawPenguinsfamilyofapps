package com.pawpawpenguins.alarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LinearLayout box = new LinearLayout(this); box.setGravity(Gravity.CENTER); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(28, 36, 28, 36); box.setBackgroundColor(0xffe8f5ff);
        TextView mascot = new TextView(this); mascot.setText("🐧⏰🐧"); mascot.setTextSize(48); mascot.setGravity(Gravity.CENTER);
        TextView title = new TextView(this); title.setText("Time to wake up!"); title.setTextSize(28); title.setTextColor(0xff153354); title.setGravity(Gravity.CENTER); title.setPadding(0, 18, 0, 28);
        Button dismiss = new Button(this); dismiss.setText("Dismiss"); dismiss.setOnClickListener(v -> { stopAlarm(); finish(); });
        Button snooze = new Button(this); snooze.setText("Snooze 5 min"); snooze.setOnClickListener(v -> { stopAlarm(); scheduleSnooze(); finish(); });
        box.addView(mascot); box.addView(title); box.addView(snooze); box.addView(dismiss); setContentView(box);
    }
    private void stopAlarm() { stopService(new Intent(this, AlarmService.class).setAction(AlarmService.ACTION_STOP)); }
    private void scheduleSnooze() {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class).putExtra("day", -1);
        PendingIntent pi = PendingIntent.getBroadcast(this, 999, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long at = System.currentTimeMillis() + 5 * 60 * 1000L;
        if (Build.VERSION.SDK_INT >= 23) manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi); else manager.setExact(AlarmManager.RTC_WAKEUP, at, pi);
    }
}
