package com.pawpawpenguins.alarmclock;

import android.content.Context;
import android.content.SharedPreferences;

final class AlarmStore {
    static final String PREFS = "pawpawpenguins_alarms";
    private final SharedPreferences prefs;
    AlarmStore(Context context) { prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    boolean enabled(int day) { return prefs.getBoolean("enabled_" + day, day == 1 || day == 2 || day == 3 || day == 6 || day == 7); }
    int hour(int day) { return prefs.getInt("hour_" + day, (day == 4 || day == 5) ? 7 : 5); }
    int minute(int day) { return prefs.getInt("minute_" + day, 0); }
    void setEnabled(int day, boolean value) { prefs.edit().putBoolean("enabled_" + day, value).apply(); }
    void setTime(int day, int hour, int minute) { prefs.edit().putInt("hour_" + day, hour).putInt("minute_" + day, minute).apply(); }
    String ringtoneUri() { return prefs.getString("ringtone_uri", null); }
    String ringtoneName() { return prefs.getString("ringtone_name", "Android default alarm tone"); }
    void setRingtone(String uri, String name) { prefs.edit().putString("ringtone_uri", uri).putString("ringtone_name", name).apply(); }
    void clearRingtone() { prefs.edit().remove("ringtone_uri").remove("ringtone_name").apply(); }
}
