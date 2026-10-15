package com.pawpawpenguins.alarmclock;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class PrivacyPolicyActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(232, 246, 255));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(14), dp(18), dp(18));
        page.setBackgroundColor(Color.rgb(232, 246, 255));
        TextView title = new TextView(this);
        title.setText("Privacy Policy");
        title.setTextColor(Color.rgb(28, 55, 78));
        title.setTextSize(26);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        page.addView(title);
        ScrollView scroll = new ScrollView(this);
        TextView policy = new TextView(this);
        policy.setText(readPolicy());
        policy.setTextColor(Color.rgb(34, 50, 64));
        policy.setTextSize(16);
        policy.setLineSpacing(dp(3), 1.05f);
        policy.setPadding(0, dp(12), 0, dp(18));
        scroll.addView(policy);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        Button close = new Button(this);
        close.setText("Done");
        close.setOnClickListener(v -> finish());
        page.addView(close, new LinearLayout.LayoutParams(-1, dp(50)));
        setContentView(page);
    }

    private String readPolicy() {
        try (InputStream input = getAssets().open("privacy-policy.txt");
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            return output.toString(StandardCharsets.UTF_8.name());
        } catch (Exception error) {
            return "The privacy policy could not be loaded. No policy information is sent over the network.";
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
