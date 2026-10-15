package com.pawpawpenguins.alarmclock;

import java.util.Calendar;
import java.util.TimeZone;

/** Date calculations kept pure so they can be tested without an Android device. */
public final class Schedule {
    private Schedule() {}

    /** Returns the next local occurrence for Calendar day (Sunday=1 ... Saturday=7). */
    public static long nextOccurrence(int calendarDay, int hour, int minute, long nowMillis, TimeZone zone) {
        if (calendarDay < Calendar.SUNDAY || calendarDay > Calendar.SATURDAY) throw new IllegalArgumentException("day out of range");
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) throw new IllegalArgumentException("time out of range");
        Calendar next = Calendar.getInstance(zone);
        next.setTimeInMillis(nowMillis);
        next.set(Calendar.DAY_OF_WEEK, calendarDay);
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (next.getTimeInMillis() <= nowMillis) next.add(Calendar.WEEK_OF_YEAR, 1);
        return next.getTimeInMillis();
    }
}
