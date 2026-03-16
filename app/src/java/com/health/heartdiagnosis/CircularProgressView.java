package com.health.heartdiagnosis;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class CircularProgressView extends View {

    private Paint trackPaint;
    private Paint progressPaint;
    private Paint glowPaint;
    private RectF oval;

    private float progress = 0f;      // 0–100
    private float animatedProgress = 0f;
    private int progressColor = Color.parseColor("#C0392B");

    private static final float STROKE = 14f;
    private static final float START_ANGLE = 135f;
    private static final float SWEEP_RANGE = 270f;

    public CircularProgressView(Context context) { super(context); init(); }
    public CircularProgressView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public CircularProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        float strokePx = STROKE * density;

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(strokePx);
        trackPaint.setColor(Color.parseColor("#21262D"));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokePx);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(strokePx * 2.5f);
        glowPaint.setColor(progressColor);
        glowPaint.setAlpha(30);
        glowPaint.setStrokeCap(Paint.Cap.ROUND);

        oval = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float density = getResources().getDisplayMetrics().density;
        float strokePx = STROKE * density;
        float padding = strokePx * 1.5f;

        oval.set(padding, padding, getWidth() - padding, getHeight() - padding);

        // Track
        canvas.drawArc(oval, START_ANGLE, SWEEP_RANGE, false, trackPaint);

        // Glow
        float sweep = (animatedProgress / 100f) * SWEEP_RANGE;
        if (sweep > 0) {
            canvas.drawArc(oval, START_ANGLE, sweep, false, glowPaint);
        }

        // Progress arc
        canvas.drawArc(oval, START_ANGLE, sweep, false, progressPaint);
    }

    public void setProgress(float progress, int colorRes) {
        this.progress = progress;
        this.progressColor = getContext().getResources().getColor(colorRes);
        progressPaint.setColor(this.progressColor);
        glowPaint.setColor(this.progressColor);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, progress);
        animator.setDuration(1400);
        animator.setInterpolator(new DecelerateInterpolator(1.5f));
        animator.addUpdateListener(animation -> {
            animatedProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }
}
