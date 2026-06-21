package irene.window.algui.CustomizeView;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.RemoteViews.RemoteView;
import android.view.View;
//霓虹灯文本
public class GradientTextView extends TextView {
public static final String TAG = "GradientTextView";
    //渐变矩阵
    private Matrix mMatrix;
    private float mTranslate;
    //渐变动画速度
    private float colorSpeed;
    //渐变动画范围
    private float colorSpace;
    //渐变颜色数组
    //全部颜色
    //private int[] colors = {0xFFFF2B22, 0xFFFF7F22, 0xFFEDFF22, 0xFF22FF22, 0xFF22F4FF, 0xFF2239FF, 0xFF5400F7};
    //多彩糖果
    private int[] colors = { 0xFFff00cc, 0xFFffcc00, 0xFF00ffcc, 0xFFff0066};
    //紫色渐变
    //private int[] colors = {0xFF750075, 0xFF930093, 0xFFae00ae, 0xFFd200d2, 0xFFe800e8, 0xFFff00ff, 0xFFff44ff};
    private LinearGradient mLinearGradient;
    private static DisplayMetrics sDisplayMetrics;
    
    private boolean isDynamic=true;//动态颜色启动状态

    public GradientTextView(Context context) {
        this(context, null);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 开启软件加速，以支持setShadowLayer()
        setShadowLayer(10, 0, 0, 0xFF000000); //设置文本阴影以实现发光效果 接受四个参数：阴影的模糊半径、阴影的水平偏移量、阴影的垂直偏移量和阴影的颜色
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

    public int dp2px(float f) {
        return Math.round(f * getDisplayMetrics().density);
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

    //设置动态颜色是否启用
    public void setDynamicEnabled(boolean b){
        isDynamic=b;
        postInvalidateOnAnimation();
    }
    
    //获取动态颜色启用状态
    public boolean getDynamicEnabled(){
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
        if(isDynamic){
            //重新绘制(递归 执行onDraw)
            postInvalidateOnAnimation();
        }
        
    }
}
