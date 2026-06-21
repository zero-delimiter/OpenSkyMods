package com.android.support;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;

public class NetView extends Drawable {
    private Paint bgPaint;
    private Paint dotPaint;
    private Handler handler;
    private Paint linePaint;
    private int numDots;
    private PointF[] points;
    private float[] sizes;
    private long startTime;
    private PointF[] velocities;

    public NetView() {
        numDots = 70;
        handler = new Handler();
        startTime = System.currentTimeMillis();
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setShadowLayer(10.0f, 0.0f, 0.0f, Color.CYAN);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(2.0f);
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#0B0F1A"));
        points = new PointF[numDots];
        velocities = new PointF[numDots];
        sizes = new float[numDots];
        for (int i = 0; i < numDots; i++) {
            points[i] = new PointF((float) (Math.random() * 1000), (float) (Math.random() * 1000));
            velocities[i] =
                    new PointF((float) ((Math.random() - 0.5f) * 1.5f), (float) ((Math.random() - 0.5f) * 1.5f));
            sizes[i] = (float) (Math.random() * 3) + 2.0f;
        }
        startAnimationLoop();
    }

    private void startAnimationLoop() {
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        updatePositions();
                        invalidateSelf();
                        handler.postDelayed(this, 8L);
                    }
                },
                8L);
    }

    private void updatePositions() {
        Rect bounds = getBounds();
        float centerX = bounds.centerX();
        float centerY = bounds.centerY();
        float elapsed = (System.currentTimeMillis() - startTime) / 1000.0f;
        for (int i = 0; i < numDots; i++) {
            PointF point = points[i];
            PointF velocity = velocities[i];
            point.x += velocity.x + ((float) Math.sin(i + elapsed)) * 0.5f;
            point.y += velocity.y + ((float) Math.cos(i + elapsed)) * 0.5f;
            point.x += (centerX - point.x) * 0.0007f;
            point.y += (centerY - point.y) * 0.0007f;
            if (point.x < 0 || point.x > bounds.width()) {
                velocity.x = -velocity.x;
            }
            if (point.y < 0 || point.y > bounds.height()) {
                velocity.y = -velocity.y;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#0B0F1A"));
        canvas.drawRoundRect(new RectF(bounds), 20.0f, 20.0f, paint);
        float elapsed = (System.currentTimeMillis() - startTime) / 1000.0f;
        int color = Color.HSVToColor(new float[]{(40.0f * elapsed) % 360.0f, 0.8f, 1.0f});
        dotPaint.setColor(color);
        dotPaint.setShadowLayer(10.0f, 0.0f, 0.0f, color);
        for (int i = 0; i < numDots; i++) {
            float scale = (float) (Math.sin(3 * elapsed + i) * 0.5 + 1);
            canvas.drawCircle(points[i].x, points[i].y, sizes[i] * scale, dotPaint);
        }
        for (int i = 0; i < numDots; i++) {
            for (int j = i + 1; j < numDots; j++) {
                float dx = points[i].x - points[j].x;
                float dy = points[i].y - points[j].y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance < 160.0f) {
                    linePaint.setColor(
                            Color.argb(
                                    (int) ((1 - distance / 160.0f) * 200),
                                    Color.red(color),
                                    Color.green(color),
                                    Color.blue(color)));
                    canvas.drawLine(points[i].x, points[i].y, points[j].x, points[j].y, linePaint);
                }
            }
        }
        Paint border = new Paint();
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(8.0f);
        border.setColor(Color.WHITE);
        canvas.drawRoundRect(new RectF(bounds), 20.0f, 20.0f, border);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
