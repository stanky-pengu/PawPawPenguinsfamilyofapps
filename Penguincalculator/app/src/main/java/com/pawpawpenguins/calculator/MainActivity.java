package com.pawpawpenguins.calculator;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
    private boolean radians = false;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        buildScreen();
    }

    private void buildScreen() {
        boolean landscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(landscape ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        int inset = landscape ? 8 : 18;
        root.setPadding(dp(inset), dp(landscape ? 6 : 12), dp(inset), dp(landscape ? 6 : 12));
        root.setBackgroundColor(BG);
        final int baseLeft = dp(inset);
        final int baseTop = dp(landscape ? 6 : 12);
        final int baseRight = dp(inset);
        final int baseBottom = dp(landscape ? 6 : 12);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets safeInsets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(baseLeft + safeInsets.left, baseTop + safeInsets.top,
                    baseRight + safeInsets.right, baseBottom + safeInsets.bottom);
            return windowInsets;
        });

        LinearLayout left = landscape ? new LinearLayout(this) : root;
        if (landscape) {
            left.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0, -1, 1.1f);
            leftParams.setMargins(0, 0, dp(8), 0);
            root.addView(left, leftParams);
        }

        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setOrientation(LinearLayout.HORIZONTAL);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);
        left.addView(brandRow, new LinearLayout.LayoutParams(-1, dp(landscape ? 26 : 36)));

        TextView brand = new TextView(this);
        brand.setText("🐧  PAWPAWPENGUINS");
        brand.setTextColor(GOLD);
        brand.setTextSize(landscape ? 13 : 15);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.setLetterSpacing(0.08f);
        brandRow.addView(brand, new LinearLayout.LayoutParams(0, -1, 1f));

        TextView makerSeal = new TextView(this);
        makerSeal.setText("FRIEND\nMADE");
        makerSeal.setTextColor(GOLD);
        makerSeal.setTextSize(7);
        makerSeal.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        makerSeal.setGravity(Gravity.CENTER);
        makerSeal.setLineSpacing(0, 0.88f);
        makerSeal.setContentDescription("Friend-made gold seal");
        GradientDrawable sealShape = new GradientDrawable();
        sealShape.setShape(GradientDrawable.OVAL);
        sealShape.setColor(BG);
        sealShape.setStroke(dp(1), GOLD);
        makerSeal.setBackground(sealShape);
        LinearLayout.LayoutParams sealParams = new LinearLayout.LayoutParams(
                dp(landscape ? 30 : 36), dp(landscape ? 24 : 32));
        sealParams.setMargins(dp(4), 0, dp(2), 0);
        brandRow.addView(makerSeal, sealParams);

        TextView title = new TextView(this);
        title.setText("PawPawPenguins Calculator");
        title.setTextColor(WHITE);
        title.setTextSize(landscape ? 15 : 20);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setSingleLine(true);
        left.addView(title, new LinearLayout.LayoutParams(-1, dp(landscape ? 30 : 38)));

        ImageView lessonScene = new ImageView(this);
        lessonScene.setImageResource(R.drawable.math_classroom_scene);
        lessonScene.setScaleType(ImageView.ScaleType.CENTER_CROP);
        lessonScene.setContentDescription(
                "A baby seal teaches the penguin math beside a board showing two plus three equals five");
        LinearLayout.LayoutParams sceneParams = new LinearLayout.LayoutParams(-1, dp(landscape ? 82 : 96));
        sceneParams.setMargins(0, 0, 0, dp(4));
        left.addView(lessonScene, sceneParams);

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.BOTTOM | Gravity.END);
        screen.setPadding(dp(landscape ? 10 : 18), dp(landscape ? 4 : 14),
                dp(landscape ? 10 : 18), dp(landscape ? 6 : 18));
        screen.setBackgroundColor(PANEL);
        LinearLayout.LayoutParams screenParams = landscape
                ? new LinearLayout.LayoutParams(-1, 0, 1f)
                : new LinearLayout.LayoutParams(-1, 0, 1.15f);
        screenParams.setMargins(0, dp(4), 0, dp(landscape ? 0 : 14));
        left.addView(screen, screenParams);

        TextView hint = new TextView(this);
        hint.setText(landscape ? (radians ? "SCIENTIFIC · RAD" : "SCIENTIFIC · DEG") : "BIG, CLEAR, READY");
        hint.setTextColor(MUTED);
        hint.setTextSize(12);
        hint.setLetterSpacing(0.1f);
        hint.setGravity(Gravity.RIGHT);
        screen.addView(hint, new LinearLayout.LayoutParams(-1, dp(landscape ? 18 : 26)));

        display = new TextView(this);
        display.setText(calculator.display());
        display.setTextColor(WHITE);
        display.setTextSize(landscape ? 38 : 54);
        display.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        display.setSingleLine(true);
        display.setHorizontallyScrolling(true);
        display.setFocusable(true);
        display.setContentDescription("Calculator display, 0");
        screen.addView(display, new LinearLayout.LayoutParams(-1, dp(landscape ? 0 : 82),
                landscape ? 1f : 0f));

        GridLayout keys = new GridLayout(this);
        keys.setColumnCount(landscape ? 6 : 4);
        keys.setRowCount(landscape ? 5 : 5);
        String[] labels = landscape
                ? new String[]{"AC", radians ? "RAD" : "DEG", "π", "√", "x²", "÷",
                    "sin", "cos", "tan", "ln", "log", "%",
                    "7", "8", "9", "⌫", "×", "1/x",
                    "4", "5", "6", "±", "−", "0",
                    "1", "2", "3", ".", "+", "="}
                : new String[]{"AC", "±", "%", "÷", "7", "8", "9", "×",
                    "4", "5", "6", "−", "1", "2", "3", "+",
                    "⌫", "0", ".", "="};
        for (String label : labels) {
            Button button = makeButton(label, landscape);
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = 0;
            p.height = 0;
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            int gap = landscape ? 2 : 4;
            p.setMargins(dp(gap), dp(gap), dp(gap), dp(gap));
            keys.addView(button, p);
        }
        if (landscape) {
            root.addView(keys, new LinearLayout.LayoutParams(0, -1, 2f));
        } else {
            root.addView(keys, new LinearLayout.LayoutParams(-1, 0, 4.7f));
        }
        setContentView(root);
        ViewCompat.requestApplyInsets(root);
    }

    private Button makeButton(String label, boolean compact) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(compact ? (label.length() > 2 ? 15 : 19) : (label.length() > 1 ? 24 : 29));
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(WHITE);
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                isUtility(label) ? PANEL : isOperator(label) ? GOLD : KEY));
        if (isOperator(label)) button.setTextColor(Color.rgb(8, 19, 31));
        button.setMinHeight(compact ? 0 : dp(66));
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
            case "DEG": radians = true; buildScreen(); return;
            case "RAD": radians = false; buildScreen(); return;
            case "π": calculator.constant(Math.PI); break;
            case "√": calculator.scientific("sqrt", radians); break;
            case "x²": calculator.scientific("square", radians); break;
            case "1/x": calculator.scientific("reciprocal", radians); break;
            case "sin": calculator.scientific("sin", radians); break;
            case "cos": calculator.scientific("cos", radians); break;
            case "tan": calculator.scientific("tan", radians); break;
            case "ln": calculator.scientific("ln", radians); break;
            case "log": calculator.scientific("log", radians); break;
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
        return key.equals("AC") || key.equals("⌫") || key.equals("±") || key.equals("%")
                || key.equals("DEG") || key.equals("RAD");
    }

    private String accessibilityName(String key) {
        switch (key) {
            case "AC": return "Clear";
            case "⌫": return "Backspace";
            case "±": return "Change sign";
            case "%": return "Percent";
            case "DEG": return "Switch to radians";
            case "RAD": return "Switch to degrees";
            case "π": return "Pi";
            case "√": return "Square root";
            case "x²": return "Square";
            case "1/x": return "Reciprocal";
            case "sin": return radians ? "Sine in radians" : "Sine in degrees";
            case "cos": return radians ? "Cosine in radians" : "Cosine in degrees";
            case "tan": return radians ? "Tangent in radians" : "Tangent in degrees";
            case "ln": return "Natural logarithm";
            case "log": return "Base 10 logarithm";
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

