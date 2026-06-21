package com.android.support.CustomizeView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
    public static final String TAG = "MarqueeTextView";
    private Context mContext;
    Animation marqueeAnimation;

    public MarqueeTextView(Context context) {
        super(context);
        mContext = context;
        setMarqueeAnimation();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setMarqueeAnimation();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setMarqueeAnimation();
    }

    private void setMarqueeAnimation() {
        super.setEllipsize((TextUtils.TruncateAt) null);
        super.setSelected(true);
        super.setGravity(Gravity.CENTER);
        marqueeAnimation =
                new TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT,
                        1.0f,
                        Animation.RELATIVE_TO_PARENT,
                        -1.0f,
                        Animation.RELATIVE_TO_PARENT,
                        0,
                        Animation.RELATIVE_TO_PARENT,
                        0);
        marqueeAnimation.setDuration(8000);
        marqueeAnimation.setRepeatCount(Animation.INFINITE);
        marqueeAnimation.setInterpolator(new LinearInterpolator());
        super.startAnimation(marqueeAnimation);
    }

    public Animation getMarqueeAnimation() {
        return marqueeAnimation;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        super.startAnimation(marqueeAnimation);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        super.clearAnimation();
    }
}
