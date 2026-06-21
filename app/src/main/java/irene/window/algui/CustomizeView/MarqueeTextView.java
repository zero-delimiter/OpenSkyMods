package irene.window.algui.CustomizeView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.view.Gravity;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/17 20:19
 * @Describe 字幕滚动文本视图
 */
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

        //super.setSingleLine(true);//只显示一行
        super.setEllipsize(null);//取消省略号
        super.setSelected(true); // 设置TextView获取焦点，以便跑马灯效果生效
        super.setGravity(Gravity.CENTER);
        //super.setHorizontallyScrolling(true);//水平滚动
        //super.setEllipsize(TextUtils.TruncateAt.MARQUEE); //启用省略号，并且在文本过长时进行滚动显示。
        //super.setSelected(true);//设置textview为选中状态
        marqueeAnimation = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0,
            Animation.RELATIVE_TO_PARENT, 0
        );

        marqueeAnimation.setDuration(8000); // 设置滚动一次的时间 单位毫秒
        marqueeAnimation.setRepeatCount(Animation.INFINITE); // 设置重复次数，这里设置为无限
        marqueeAnimation.setInterpolator(new LinearInterpolator()); // 设置动画插值器，使滚动平滑
        super.startAnimation(marqueeAnimation);//启动动画
    }

    //获取字幕动画
    public Animation getMarqueeAnimation() {
        return marqueeAnimation;
    }

    //当前在窗口中则调用
    @Override
    protected void onAttachedToWindow() {
        //Log.d("艾琳debug","在窗口中");
        super.onAttachedToWindow();
        //启动动画
        super.startAnimation(marqueeAnimation);
    }

    //不在窗口时调用
    @Override
    protected void onDetachedFromWindow() {
        //Log.d("艾琳debug","离开窗口");
        super.onDetachedFromWindow();
        //清除动画
        super.clearAnimation();
    }


}
