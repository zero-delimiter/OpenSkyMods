package irene.window.algui.CustomizeView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import irene.window.algui.Tools.ViewTool;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/04 14:55
 * @Describe 自定义线性布局 实现子布局自适应它的圆角 将多出来的部分进行裁剪
 */
public class vLinearLayout extends LinearLayout {
    
    public static final String TAG = "vLinearLayout";


    private int mRadius = 0;//存储圆角半径
    GradientDrawable mainBackground;
    private int tBorderSize, tBorderColor;//存储描边宽度，描边颜色
    public vLinearLayout(Context context) {
        super(context);
        tBorderSize = ViewTool.convertDpToPx(context, 1.2f);
        tBorderColor = 0xFFFFFFFF;
        mainBackground = new GradientDrawable();
        setBackground(mainBackground);
    }

    public vLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        tBorderSize = ViewTool.convertDpToPx(context, 1.2f);
        tBorderColor = 0xFFFFFFFF;
        mainBackground = new GradientDrawable();
        setBackground(mainBackground);
    }

    public vLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tBorderSize = ViewTool.convertDpToPx(context, 1.2f);
        tBorderColor = 0xFFFFFFFF;
        mainBackground = new GradientDrawable();
        setBackground(mainBackground);
        //or in here
    }

    //设置圆角半径
    public void setFilletRadiu(int radiu) {
        mRadius = radiu;
        mainBackground.setCornerRadius(radiu);
    }
    //设置背景颜色
    public void setBackColor(int color) {
        mainBackground.setColor(color);
    }
    //设置描边
    public void setBorder(int borderSize, int borderColor) {
        tBorderSize = borderSize;
        tBorderColor = borderColor;
        mainBackground.setStroke(tBorderSize, tBorderColor);
        this.setPadding(tBorderSize, tBorderSize, tBorderSize, tBorderSize);//设置内边距 以便描边可见
    }
    //设置描边宽度
    public void setBorderSize(int borderSize) {
        tBorderSize = borderSize;
        mainBackground.setStroke(tBorderSize, tBorderColor);
        this.setPadding(tBorderSize, tBorderSize, tBorderSize, tBorderSize);//设置内边距 以便描边可见
    }
    //设置描边颜色
    public void setBorderColor(int borderColor) {
        tBorderColor = borderColor;
        mainBackground.setStroke(tBorderSize, tBorderColor);
    }
    //获取背景
    public GradientDrawable getBack() {
        return mainBackground;
    }

    //子布局绘制时将调用
    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 裁剪子布局超出圆角的部分
        Path path = new Path();
        RectF rectF = new RectF();
        rectF.set(0, 0, getWidth(), getHeight());
        path.addRoundRect(rectF, mRadius, mRadius, Path.Direction.CW);
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }


}
