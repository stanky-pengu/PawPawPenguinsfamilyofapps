package com.pawpawpenguins.alarmclock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public final class PenguinLoadingView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress;
    private ValueAnimator animator;
    private Runnable animationEndListener;

    public PenguinLoadingView(Context context) {
        super(context);
        setBackgroundColor(0xffeaf6ff);
        setContentDescription("Animated PawPawPenguins mascot sleeps beside a ringing alarm, then jumps awake.");
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    public void setOnAnimationEndListener(Runnable listener) {
        animationEndListener = listener;
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(value -> {
            progress = (float) value.getAnimatedValue();
            invalidate();
        });
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                if (animationEndListener != null) animationEndListener.run();
            }
        });
        animator.start();
    }

    @Override protected void onDetachedFromWindow() {
        if (animator != null) animator.cancel();
        super.onDetachedFromWindow();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float sx = getWidth() / 360f;
        float sy = getHeight() / 520f;
        canvas.save();
        canvas.scale(sx, sy);
        canvas.drawColor(0xffeaf6ff);
        fill(canvas, 0xffd8efff);
        canvas.drawCircle(315, 70, 42, paint);

        text(canvas, "PawPawPenguins Alarm", 25, 0xff1c374e, true, 180, 82);
        text(canvas, "Loading your alarms…", 15, 0xff6f8496, false, 180, 112);

        // Nightstand, lamp, and the clock that starts the surprise.
        rounded(canvas, 250, 247, 333, 339, 0xffb57857, 9);
        rounded(canvas, 261, 235, 322, 250, 0xff8d573f, 7);
        rounded(canvas, 280, 184, 303, 227, 0xffffdf9a, 12);
        canvas.drawRect(290, 221, 294, 236, paintColor(0xffaf8060));
        rounded(canvas, 267, 153, 316, 168, 0xffd6b17a, 5);
        alarmClock(canvas, 286, 212);

        // Bed frame, mattress, pillow, and blue quilt.
        rounded(canvas, 30, 303, 261, 397, 0xff618fb0, 22);
        rounded(canvas, 39, 281, 255, 352, 0xffffffff, 30);
        rounded(canvas, 37, 343, 270, 380, 0xff739fbb, 14);
        rounded(canvas, 48, 368, 59, 421, 0xff526e7f, 5);
        rounded(canvas, 244, 368, 255, 421, 0xff526e7f, 5);
        rounded(canvas, 70, 280, 145, 322, 0xffdcecf6, 18);

        float jump = 0;
        boolean awake = progress >= 0.55f;
        if (progress >= 0.55f && progress < 1f) {
            float phase = (progress - 0.55f) / 0.45f;
            jump = -112f * (float) Math.sin(Math.PI * phase);
        }
        penguin(canvas, 142, 280 + jump, awake, progress);
        quilt(canvas, progress);
        if (progress > 0.48f) alarmRings(canvas, 286, 212, progress);

        text(canvas, progress < 0.58f ? "Shhh… cozy dreams" : "Surprise! Good morning!", 17, 0xff326d8a, true, 180, 460);
        canvas.restore();
    }

    private void penguin(Canvas c, float x, float y, boolean awake, float t) {
        c.save();
        c.translate(x, y);
        if (awake) {
            float spread = Math.min(1f, (t - 0.55f) / 0.18f);
            c.save();
            c.rotate(-38f * spread, -36, -9);
            oval(c, -62, -27, -24, 13, 0xff222f3a);
            c.restore();
            c.save();
            c.rotate(38f * spread, 36, -9);
            oval(c, 24, -27, 62, 13, 0xff222f3a);
            c.restore();
        } else {
            oval(c, -58, -19, -26, 10, 0xff222f3a);
            oval(c, 26, -19, 58, 10, 0xff222f3a);
        }
        oval(c, -43, -28, 43, 53, 0xff263642);
        oval(c, -30, -15, 30, 48, 0xfff9fbfc);
        oval(c, -26, -43, 26, 7, 0xff263642);
        if (awake) {
            oval(c, -17, -31, -6, -15, 0xffffffff);
            oval(c, 6, -31, 17, -15, 0xffffffff);
            oval(c, -12, -27, -7, -18, 0xff17232a);
            oval(c, 7, -27, 12, -18, 0xff17232a);
            triangle(c, -8, -9, 8, -9, 0, 2, 0xffff9b39);
            oval(c, -26, 50, -4, 60, 0xffff9b39);
            oval(c, 4, 50, 26, 60, 0xffff9b39);
            if (t > 0.62f && t < 0.91f) {
                paint.setColor(0xff8fd3f1);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                c.drawCircle(36, -31, 3, paint);
                paint.setStyle(Paint.Style.FILL);
            }
        } else {
            paint.setColor(0xff273943);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            c.drawArc(new RectF(-18, -27, -4, -16), 10, 160, false, paint);
            c.drawArc(new RectF(4, -27, 18, -16), 10, 160, false, paint);
            paint.setStyle(Paint.Style.FILL);
            triangle(c, -7, -11, 7, -11, 0, -4, 0xffff9b39);
        }
        c.restore();
    }

    private void quilt(Canvas c, float t) {
        float wobble = t > 0.55f && t < 0.75f ? 8f * (float) Math.sin((t - 0.55f) * 24f) : 0f;
        Path p = new Path();
        p.moveTo(45, 323);
        p.cubicTo(95, 307 + wobble, 166, 324 - wobble, 260, 316);
        p.lineTo(260, 364);
        p.cubicTo(200, 383, 108, 380, 45, 365);
        p.close();
        paint.setColor(0xff88c6e7);
        c.drawPath(p, paint);
        paint.setColor(0xffb8e1f5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        c.drawArc(new RectF(73, 320, 238, 372), 8, 150, false, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void alarmClock(Canvas c, float x, float y) {
        oval(c, x - 24, y - 24, x + 24, y + 24, 0xffffc857);
        oval(c, x - 19, y - 19, x + 19, y + 19, 0xfffff7df);
        paint.setColor(0xff4b5358);
        paint.setStrokeWidth(3);
        c.drawLine(x, y, x, y - 11, paint);
        c.drawLine(x, y, x + 9, y + 4, paint);
        oval(c, x - 19, y - 30, x - 8, y - 24, 0xffed9a4d);
        oval(c, x + 8, y - 30, x + 19, y - 24, 0xffed9a4d);
        triangle(c, x - 16, y + 21, x - 8, y + 21, x - 18, y + 31, 0xff9a6a49);
        triangle(c, x + 16, y + 21, x + 8, y + 21, x + 18, y + 31, 0xff9a6a49);
    }

    private void alarmRings(Canvas c, float x, float y, float t) {
        float pulse = 3f + 3f * (float) Math.sin(t * 38f);
        paint.setColor(0xffff9b39);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        c.drawArc(new RectF(x - 39 - pulse, y - 36, x - 17, y - 10), 220, 100, false, paint);
        c.drawArc(new RectF(x + 17, y - 36, x + 39 + pulse, y - 10), 220, 100, false, paint);
        c.drawLine(x - 40, y - 47 - pulse, x - 47, y - 54, paint);
        c.drawLine(x + 40, y - 47 - pulse, x + 47, y - 54, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void triangle(Canvas c, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        Path path = new Path();
        path.moveTo(x1, y1); path.lineTo(x2, y2); path.lineTo(x3, y3); path.close();
        paint.setColor(color); c.drawPath(path, paint);
    }

    private void oval(Canvas c, float l, float t, float r, float b, int color) {
        paint.setColor(color); c.drawOval(l, t, r, b, paint);
    }

    private void rounded(Canvas c, float l, float t, float r, float b, int color, float radius) {
        paint.setColor(color); c.drawRoundRect(l, t, r, b, radius, radius, paint);
    }

    private Paint paintColor(int color) { paint.setColor(color); return paint; }

    private void fill(Canvas c, int color) { paint.setColor(color); c.drawCircle(315, 70, 35, paint); }

    private void text(Canvas c, String value, float size, int color, boolean bold, float x, float y) {
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(android.graphics.Typeface.create("sans-serif-rounded", bold ? 1 : 0));
        c.drawText(value, x, y, paint);
    }
}
