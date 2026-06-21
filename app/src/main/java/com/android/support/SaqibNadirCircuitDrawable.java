package com.android.support;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import java.util.Random;

class SaqibNadirCircuitDrawable extends Drawable {
    private static final float CORNER_RADIUS = 18.0f;
    private static final int FRAME_RATE = 120;
    private static final int PATH_COUNT = 25;
    private static final int SEGMENTS = 6;
    private static final float SPEED = 1.0f;

    private final Paint backgroundPaint;
    private final float[][] baseCoords;
    private final Paint borderPaint;
    private final Handler handler;
    private final Paint paint;
    private final float[][] pathOffsets;
    private final Path[] paths;
    private final Random random;
    private float sn;

    public SaqibNadirCircuitDrawable() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handler = new Handler();
        random = new Random();
        paths = new Path[PATH_COUNT];
        pathOffsets = new float[PATH_COUNT][2];
        baseCoords = new float[PATH_COUNT][2];
        sn = 0.0f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0f);
        paint.setShadowLayer(12.0f, 0.0f, 0.0f, Color.CYAN);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8.0f);
        borderPaint.setColor(Color.WHITE);
        backgroundPaint.setColor(Color.parseColor("#0A0F1A"));
        initPaths();
        animate();
    }

    private void animate() {
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        updatePaths();
                        invalidateSelf();
                        handler.postDelayed(this, FRAME_RATE);
                    }
                },
                FRAME_RATE);
    }

    private Path generatePath(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        for (int i = 0; i < SEGMENTS; i++) {
            x += random.nextInt(60) - 30;
            y += random.nextInt(60) - 30;
            path.lineTo(x, y);
        }
        return path;
    }

    private void initPaths() {
        for (int i = 0; i < PATH_COUNT; i++) {
            baseCoords[i][0] = random.nextInt(1000);
            baseCoords[i][1] = random.nextInt(1000);
            pathOffsets[i][0] = randomDirection();
            pathOffsets[i][1] = randomDirection();
            paths[i] = generatePath(baseCoords[i][0], baseCoords[i][1]);
        }
    }

    private float randomDirection() {
        return (random.nextFloat() - 0.5f) * SPEED * 2;
    }

    private void updatePaths() {
        Rect bounds = getBounds();
        for (int i = 0; i < PATH_COUNT; i++) {
            baseCoords[i][0] += pathOffsets[i][0];
            baseCoords[i][1] += pathOffsets[i][1];
            if (baseCoords[i][0] < 0 || baseCoords[i][0] > bounds.width()) {
                pathOffsets[i][0] *= -1;
            }
            if (baseCoords[i][1] < 0 || baseCoords[i][1] > bounds.height()) {
                pathOffsets[i][1] *= -1;
            }
            paths[i] = generatePath(baseCoords[i][0], baseCoords[i][1]);
        }
        sn += 1.0f;
        if (sn > 360.0f) {
            sn = 0.0f;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.drawRoundRect(new RectF(bounds), CORNER_RADIUS, CORNER_RADIUS, backgroundPaint);
        int color = Color.HSVToColor(new float[]{sn, 1.0f, 1.0f});
        paint.setColor(color);
        paint.setShadowLayer(12.0f, 0.0f, 0.0f, color);
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }
        canvas.drawRoundRect(new RectF(bounds), CORNER_RADIUS, CORNER_RADIUS, borderPaint);
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
