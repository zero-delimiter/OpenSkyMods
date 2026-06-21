package irene.window.algui.Tools;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.view.View;
import irene.window.algui.CustomizeView.GradientTextView;
import android.view.ViewGroup;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/01 16:02
 * @Describe 视图工具
 */
public class ViewTool {

    public static final String TAG = "ViewTool";



    public static int getByteCount(byte b) {
        
        if ((b & 0b10000000) == 0) {
            return 1; // 单字节字符
        } else if ((b & 0b11100000) == 0b11000000) {
            return 2; // 双字节字符
        } else if ((b & 0b11110000) == 0b11100000) {
            return 3; // 三字节字符
        } else if ((b & 0b11111000) == 0b11110000) {
            return 4; // 四字节字符
        } else {
            return 1; // 非法字符，按一个字节处理
        }
    }

    //限制字符串一行最大字符数量的工具
    /**
     * 
     *
     * @param input 要处理的文本
     * @param maxCharsPerLine 一行最大字符数量
     * @return
     */
    public static CharSequence wrapText(CharSequence input, int maxCharsPerLine) {
        if(input==null){
            return input;
        }
        // 创建 StringBuilder 对象用于拼接生成的字符串
        StringBuilder output = new StringBuilder();
        // 获取输入字符串的长度
        int length = input.length();
        // 定义当前行已经占用的字符数
        float count = 0;

        // 按照每行最大字符数进行遍历
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);

            // 中文字符占用1个字符位置
            if ((c >= '\u4e00' && c <= '\u9fa5') || c == '\u3007') {
                // 如果当前行已占用的字符数加上中文字符超过最大值，则添加换行符并重置计数器
                if (count + 1 > maxCharsPerLine) {
                    output.append("\n");
                    count = 0;
                }
                output.append(c);
                count += 1;
            } else {
                // 数字、英文字母、符号占用0.5个字符位置
                // 如果当前行已占用的字符数加上数字、英文字母、符号字符超过最大值，则添加换行符并重置计数器
                if (count + 0.5 > maxCharsPerLine) {
                    output.append("\n");
                    count = 0;
                }
                output.append(c);
                count += 0.5;
            }
        }

        // 将 StringBuilder 对象转换为字符串并返回
        return output.toString();
    }

    //让颜色更有层次感的工具
    public static int createLayeredColor(int baseColor, float alphaFactor) {
        // 提取RGB分量和透明度
        int red = Color.red(baseColor);
        int green = Color.green(baseColor);
        int blue = Color.blue(baseColor);
        int alpha = Color.alpha(baseColor);

        // 计算新的透明度
        int newAlpha = (int) (alpha * alphaFactor);

        // 创建新的颜色并返回
        return Color.argb(newAlpha, red, green, blue);
    }

    //计算颜色的反色的工具 比如白色的反色是黑色 参数：颜色
    public static int calculateColorInverse(int color) {
        int red = 255 - Color.red(color);
        int green = 255 - Color.green(color);
        int blue = 255 - Color.blue(color);

        return Color.rgb(red, green, blue);
    }

    //颜色加深工具 参数：颜色 加深比例0-1
    static public int darkenColor(int color, float ratio) {
        // 获取原始颜色的ARGB分量
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        // 对红、绿、蓝三个分量进行加深处理
        red = (int) (red * ratio);
        green = (int) (green * ratio);
        blue = (int) (blue * ratio);

        // 修正分量值超出范围的情况
        red = Math.min(red, 255);
        green = Math.min(green, 255);
        blue = Math.min(blue, 255);

        // 合成新的颜色值
        return Color.argb(alpha, red, green, blue);
    }


    //颜色变亮工具
    public static int brightenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] * 1.2f; // 饱和度增加20%
        hsv[2] = hsv[2] * 1.2f; // 明度增加20%
        return Color.HSVToColor(hsv);
    }




    //将dp屏幕单位值转换为固定单位像素值 的转换器 用于实现在不同机型上 显示的大小一致 例如悬浮球 布局 文字之类的
    static public int convertDpToPx(Context context, float dp) {
        if(context==null){
            return (int)dp;
        }
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if(context==null){
            return (int)dpValue;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        if(context==null){
            return (int)pxValue;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //设置文本视图阴影 参数：文本视图，阴影半径，阴影水平偏移量，阴影垂直偏移量，阴影颜色
    public static TextView setTextViewShadow(TextView textView, float radius, float xOffset, float yOffset, int shadowColor) {
        if (textView == null) {
            return textView;
        }
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 开启软件加速，以支持setShadowLayer()
        textView.setShadowLayer(radius, xOffset, yOffset, shadowColor); //设置文本阴影 接受四个参数：阴影的模糊半径、阴影的水平偏移量、阴影的垂直偏移量和阴影的颜色
        return textView;
    }

    //设置视图渐变背景  参数：视图对象，渐变颜色数组，渐变类型
    public static Drawable setGradientBackground(ViewGroup view, int[] colors,int gradientType) {
        if(view==null||colors==null){
            return null;
        }
        
        Drawable b = view.getBackground();

        if (b == null) {
            GradientDrawable back=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
            // 设置渐变类型为线性渐变
            back.setGradientType(gradientType);
            view.setBackground(back);
            return back;
        } else {
            if (b instanceof GradientDrawable) {
                GradientDrawable gd=(GradientDrawable)b;
                //设置渐变方向为 从左到右
                gd.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                //设置渐变颜色数组
                gd.setColors(colors);
                // 设置渐变类型为线性渐变
                gd.setGradientType(gradientType);
                return gd;
            }
        }
        return b;

    }

}
