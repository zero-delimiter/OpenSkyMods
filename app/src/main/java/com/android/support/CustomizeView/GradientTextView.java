package com.android.support.CustomizeView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class GradientTextView extends TextView {
    public static final String TAG = "GradientTextView";
    private static DisplayMetrics sDisplayMetrics;
    private float colorSpace;
    private float colorSpeed;
    private int[] colors = {0xFFFF00CC, 0xFFFFCC00, 0xFF00FFCC, 0xFFFF0066};
    private boolean isDynamic = true;
    private LinearGradient mLinearGradient;
    private Matrix mMatrix;
    private float mTranslate;

    public GradientTextView(Context context) {
        this(context, null);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        setShadowLayer(10.0f, 0.0f, 0.0f, 0xFF000000);
        colorSpace = dp2px(100);
        colorSpeed = dp2px(1);
        mMatrix = new Matrix();
        initPaint();
    }

    private void initPaint() {
        if (colors != null && colors.length > 0) {
            mLinearGradient = new LinearGradient(0, 0, colorSpace, 0, colors, null, Shader.TileMode.MIRROR);
            getPaint().setShader(mLinearGradient);
        }
    }

    public int dp2px(float value) {
        return Math.round(value * getDisplayMetrics().density);
    }

    public DisplayMetrics getDisplayMetrics() {
        if (sDisplayMetrics == null) {
            sDisplayMetrics = Resources.getSystem().getDisplayMetrics();
        }
        return sDisplayMetrics;
    }

    public int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public void setColors(int... colors) {
        if (colors != null && colors.length > 0) {
            this.colors = colors;
            initPaint();
        }
    }

    public void setDynamicEnabled(boolean enabled) {
        isDynamic = enabled;
        postInvalidateOnAnimation();
    }

    public boolean getDynamicEnabled() {
        return isDynamic;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMatrix == null) {
            mMatrix = new Matrix();
        }
        mTranslate += colorSpeed;
        mMatrix.setTranslate(mTranslate, 0);
        if (mLinearGradient != null) {
            mLinearGradient.setLocalMatrix(mMatrix);
        }
        super.onDraw(canvas);
        if (isDynamic) {
            postInvalidateOnAnimation();
        }
    }
}
