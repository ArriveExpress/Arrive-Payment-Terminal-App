package com.example.card_payment;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;

public class CircularGradientDrawable extends Drawable {

    private float gradientRadius = 1f;
    private Paint gradientPaint;
    private final int startColor = Color.parseColor("#3EC65D");
    private final int endColor = Color.parseColor("#00000000");

    public CircularGradientDrawable() {
        gradientPaint = new Paint();
        gradientPaint.setAntiAlias(true);
        gradientPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        float width = getBounds().width();
        float height = getBounds().height();
        float centerX = width / 2;
        float centerY = height / 2;

         gradientRadius = Math.max(gradientRadius, Math.min(width, height) * 0.2f);

        RadialGradient gradient = new RadialGradient(
                centerX, centerY, gradientRadius,
                startColor, endColor, Shader.TileMode.CLAMP
        );

        gradientPaint.setShader(gradient);
        canvas.drawRect(0f, 0f, width, height, gradientPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        gradientPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        gradientPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setGradientRadius(float radius) {
        this.gradientRadius = radius;
        invalidateSelf();
    }

    public void animateGradientRadius(final CircularGradientDrawable drawable, final View view) {
        final float width = view.getWidth();
        final float height = view.getHeight();

        if (width == 0 || height == 0) {
            return;
        }

        final float minRadius = Math.max(Math.min(width, height) * 0.3f, 1f);
        final float maxRadius = Math.min(width, height) * 0.5f;

        ValueAnimator animator = ValueAnimator.ofFloat(minRadius, maxRadius);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float radius = (Float) animation.getAnimatedValue();
                drawable.setGradientRadius(radius);
            }
        });

        animator.start();
    }
}
