package com.pawpawpenguins.alarmclock;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Test public void screenShowsVersionBatteryAndRingtoneControls() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                android.view.View decor = activity.getWindow().getDecorView();
                assertNotNull(decor.findViewWithTag("version_label"));
                assertTrue(((android.widget.TextView) decor.findViewWithTag("version_label")).getText().toString().contains(BuildConfig.VERSION_NAME));
                assertNotNull(decor.findViewWithTag("battery_meter"));
                assertNotNull(decor.findViewWithTag("preview_ringtone"));
                assertNotNull(decor.findViewWithTag("choose_ringtone"));
                assertNotNull(decor.findViewWithTag("default_ringtone"));
                assertNotNull(decor.findViewWithTag("privacy_policy"));
            });
        }
    }

    @Test public void diagnosticsAreGeneratedInMemoryOnlyOnRequest() {
        android.content.Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        AutoTestLog.removeLegacyLogs(context);
        String log = AutoTestLog.run(context, 50);
        assertTrue(log.contains("\"test\":\"app_version\""));
        assertTrue(log.contains("\"test\":\"battery_reading\""));
        assertFalse(new File(context.getFilesDir(), "test-logs").exists());
    }
}
