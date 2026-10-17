package com.pawpawpenguins.calculator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class MainActivity extends Activity {
    private final CalculatorEngine calculator = new CalculatorEngine();
    private TextView display;
    private static final int BG = Color.rgb(8, 19, 31);
    private static final int PANEL = Color.rgb(15, 32, 48);
    private static final int KEY = Color.rgb(29, 49, 66);
    private static final int GOLD = Color.rgb(245, 185, 76);
    private static final int WHITE = Color.rgb(247, 249, 251);
    private static final int MUTED = Color.rgb(165, 184, 198);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        buildScreen();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(12), dp(18), dp(12));
        root.setBackgroundColor(BG);
        final int baseLeft = dp(18);
        final int baseTop = dp(12);
        final int baseRight = dp(18);
        final int baseBottom = dp(12);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets safeInsets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(baseLeft + safeInsets.left, baseTop + safeInsets.top,
                    baseRight + safeInsets.right, baseBottom + safeInsets.bottom);
            return windowInsets;
        });

        TextView brand = new TextView(this);
        brand.setText("🐧  PAWPAWPENGUINS");
        brand.setTextColor(GOLD);
        brand.setTextSize(15);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.setLetterSpacing(0.08f);
        root.addView(brand, new LinearLayout.LayoutParams(-1, dp(32)));

        TextView title = new TextView(this);
        title.setText("Penguin Calculator");
        title.setTextColor(WHITE);
        title.setTextSize(22);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(38)));

        LinearLayout mascotRow = new LinearLayout(this);
        mascotRow.setOrientation(LinearLayout.HORIZONTAL);
        mascotRow.setGravity(Gravity.CENTER);
        mascotRow.setBackgroundColor(Color.WHITE);

        ImageView mascot = new ImageView(this);
        mascot.setImageResource(R.drawable.penguin_calculator_mascot);
        mascot.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mascot.setContentDescription("Cute penguin holding a calculator");
        mascot.setBackgroundColor(Color.WHITE);
        mascot.setPadding(dp(4), dp(2), dp(4), dp(2));
        mascotRow.addView(mascot, new LinearLayout.LayoutParams(dp(94), dp(94)));

        ImageView sealBuddy = new ImageView(this);
        sealBuddy.setImageResource(R.drawable.seal_buddy);
        sealBuddy.setScaleType(ImageView.ScaleType.FIT_CENTER);
        sealBuddy.setContentDescription("Cute baby seal buddy");
        sealBuddy.setPadding(dp(2), dp(2), dp(2), dp(2));
        LinearLayout.LayoutParams sealParams = new LinearLayout.LayoutParams(dp(82), dp(94));
        sealParams.setMargins(dp(6), 0, 0, 0);
        mascotRow.addView(sealBuddy, sealParams);

        LinearLayout.LayoutParams mascotRowParams = new LinearLayout.LayoutParams(-1, dp(94));
        mascotRowParams.gravity = Gravity.CENTER_HORIZONTAL;
        root.addView(mascotRow, mascotRowParams);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.BOTTOM | Gravity.END);
        screen.setPadding(dp(18), dp(14), dp(18), dp(18));
        screen.setBackgroundColor(PANEL);
        LinearLayout.LayoutParams screenParams = new LinearLayout.LayoutParams(-1, 0, 1.15f);
        screenParams.setMargins(0, dp(4), 0, dp(14));
        root.addView(screen, screenParams);

        TextView hint = new TextView(this);
        hint.setText("BIG, CLEAR, READY");
        hint.setTextColor(MUTED);
        hint.setTextSize(12);
        hint.setLetterSpacing(0.1f);
        hint.setGravity(Gravity.RIGHT);
        screen.addView(hint, new LinearLayout.LayoutParams(-1, dp(26)));

        display = new TextView(this);
        display.setText(calculator.display());
        display.setTextColor(WHITE);
        display.setTextSize(54);
        display.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        display.setSingleLine(true);
        display.setHorizontallyScrolling(true);
        display.setFocusable(true);
        display.setContentDescription("Calculator display, 0");
        screen.addView(display, new LinearLayout.LayoutParams(-1, dp(82)));

        GridLayout keys = new GridLayout(this);
        keys.setColumnCount(4);
        keys.setRowCount(5);
        String[] labels = {"AC", "±", "%", "÷", "7", "8", "9", "×",
                "4", "5", "6", "−", "1", "2", "3", "+",
                "⌫", "0", ".", "="};
        for (String label : labels) {
            Button button = makeButton(label);
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = 0;
            p.height = 0;
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.setMargins(dp(4), dp(4), dp(4), dp(4));
            keys.addView(button, p);
        }
        root.addView(keys, new LinearLayout.LayoutParams(-1, 0, 4.7f));
        setContentView(root);
        ViewCompat.requestApplyInsets(root);
    }

    private Button makeButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(label.length() > 1 ? 24 : 29);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(WHITE);
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                isUtility(label) ? PANEL : isOperator(label) ? GOLD : KEY));
        if (isOperator(label)) button.setTextColor(Color.rgb(8, 19, 31));
        button.setMinHeight(dp(66));
        button.setStateListAnimator(null);
        button.setContentDescription(accessibilityName(label));
        button.setOnClickListener(v -> onKey(label));
        return button;
    }

    private void onKey(String key) {
        switch (key) {
            case "AC": calculator.clear(); break;
            case "⌫": calculator.backspace(); break;
            case "±": calculator.toggleSign(); break;
            case "%": calculator.percent(); break;
            case ".": calculator.decimalPoint(); break;
            case "=": calculator.equals(); break;
            case "÷": calculator.operator('/'); break;
            case "×": calculator.operator('*'); break;
            case "−": calculator.operator('-'); break;
            case "+": calculator.operator('+'); break;
            default:
                if (key.length() == 1) calculator.digit(key.charAt(0));
        }
        display.setText(calculator.display());
        display.setContentDescription("Calculator display, " + calculator.display());
        display.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    private boolean isOperator(String key) {
        return key.equals("÷") || key.equals("×") || key.equals("−") || key.equals("+") || key.equals("=");
    }

    private boolean isUtility(String key) {
        return key.equals("AC") || key.equals("⌫") || key.equals("±") || key.equals("%");
    }

    private String accessibilityName(String key) {
        switch (key) {
            case "AC": return "Clear";
            case "⌫": return "Backspace";
            case "±": return "Change sign";
            case "%": return "Percent";
            case "÷": return "Divide";
            case "×": return "Multiply";
            case "−": return "Subtract";
            case "+": return "Add";
            case "=": return "Equals";
            case ".": return "Decimal point";
            default: return key;
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}

