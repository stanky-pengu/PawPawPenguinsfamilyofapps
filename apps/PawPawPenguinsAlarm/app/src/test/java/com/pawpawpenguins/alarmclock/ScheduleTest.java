package com.pawpawpenguins.alarmclock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Test;

public class ScheduleTest {
    private final TimeZone utc = TimeZone.getTimeZone("UTC");

    @Test public void upcomingAlarmTodayUsesToday() {
        Calendar now = Calendar.getInstance(utc);
        now.set(2025, Calendar.MAY, 4, 4, 30, 0); now.set(Calendar.MILLISECOND, 0);
        long result = Schedule.nextOccurrence(Calendar.SUNDAY, 5, 0, now.getTimeInMillis(), utc);
        Calendar alarm = Calendar.getInstance(utc); alarm.setTimeInMillis(result);
        assertEquals(Calendar.SUNDAY, alarm.get(Calendar.DAY_OF_WEEK));
        assertEquals(5, alarm.get(Calendar.HOUR_OF_DAY));
        assertEquals(4, alarm.get(Calendar.DAY_OF_MONTH));
        assertTrue(result > now.getTimeInMillis());
    }

    @Test public void elapsedAlarmMovesToNextWeek() {
        Calendar now = Calendar.getInstance(utc);
        now.set(2025, Calendar.MAY, 4, 5, 0, 0); now.set(Calendar.MILLISECOND, 0);
        long result = Schedule.nextOccurrence(Calendar.SUNDAY, 5, 0, now.getTimeInMillis(), utc);
        Calendar alarm = Calendar.getInstance(utc); alarm.setTimeInMillis(result);
        assertEquals(11, alarm.get(Calendar.DAY_OF_MONTH));
        assertEquals(5, alarm.get(Calendar.HOUR_OF_DAY));
    }

    @Test public void rejectsInvalidTime() {
        try { Schedule.nextOccurrence(Calendar.MONDAY, 24, 0, 0, utc); throw new AssertionError("Expected invalid hour"); }
        catch (IllegalArgumentException expected) { }
    }
}
