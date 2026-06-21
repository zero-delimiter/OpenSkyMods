package irene.window.algui.Tools;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.view.Choreographer;
import android.widget.TextView;
import java.sql.Date;
import java.util.Locale;

/**
 * @Author 𝘽𝙮·艾琳 - ［Copyright © 2023 艾琳 版权所有］游戏逆向交流群730967224 - 作者QQ3353484607
 * @Date 2024/01/22 00:11
 * @Describe 实时数据文本工具
 */
public class RealTimeDataTextTool {

    public static final String TAG = "RealTimeDataTextTool";

    private static String ordinaryDataColor = "#9C27B0";//普通实时数据颜色
    public static String getOrdinaryDataColor_RGB() {
        return ordinaryDataColor;
    }

    //设置普通实时数据颜色 (只支持RGB格式)
    public static void setOrdinaryDataColor_RGB(String rgb) {
        if (rgb == null || !rgb.matches("^#[0-9a-fA-F]{6}$")) {
            return;
        }
        ordinaryDataColor = rgb;
    }






    //文本视图添加手机实时电量
    public static TextView textAddPower(Context context, TextView textView) {
        if (context == null || textView == null) {
            return textView;
        }
        final Context context_A=context;
        final TextView textView_A=textView;
        final String text=(textView.getText().toString() != null) ?textView.getText().toString(): "";
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                int batteryLevel =SystemTool.getBatteryLevel(context_A);
                textView_A.setText(Html.fromHtml(text +  "<font color='" + ordinaryDataColor + "'>" + String.format(Locale.getDefault(), "%d%%", batteryLevel) + "</font>"));
            }
        };

        // 启动线程
        new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        handler.sendEmptyMessage(0);
                        try {
                            Thread.sleep(1000); // 每隔一秒更新一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        return textView_A;
    }







    // 文本视图添加实时可用内存 参数上下文 是否格式化 写入哪个文本视图中
    public static TextView textAddAvailableMemory(Context context, boolean isFormat,  TextView textView) {
        if (context == null || textView == null) {
            return textView;
        }
        final Context context_A=context;
        final boolean isFormat_A=isFormat;
        final TextView textView_A=textView;
        final String text=(textView.getText().toString() != null) ?textView.getText().toString(): "";
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                String formattedMemoryUsage =SystemTool.getMemoryUsage(context_A, isFormat_A);
                textView_A.setText(Html.fromHtml(text + "<font color='" + ordinaryDataColor + "'>" + String.format(Locale.getDefault(), "%s", formattedMemoryUsage) + "</font>"));
            }
        };

        // 启动线程
        new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        handler.sendEmptyMessage(0);
                        try {
                            Thread.sleep(1000); // 每隔一秒更新一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        return textView_A;
    }








    // 文本视图添加实时可用存储 参数 是否格式化 写入哪个文本视图中
    public static TextView textAddAvailableStorage(boolean isFormat, TextView textView) {
        if (textView == null) {
            return textView;
        }
        final boolean isFormat_A=isFormat;
        final TextView textView_A=textView;
        final String text=(textView.getText().toString() != null) ?textView.getText().toString(): "";
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                String availableStorage =SystemTool.getAvailableStorage(isFormat_A);
                textView_A.setText(Html.fromHtml(text + "<font color='" + ordinaryDataColor + "'>" + String.format(Locale.getDefault(), "%s", availableStorage) + "</font>"));
            }
        };

        // 启动线程
        new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        handler.sendEmptyMessage(0);
                        try {
                            Thread.sleep(1000); // 每隔一秒更新一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        return textView_A;
    }







    /**
     * 文本视图添加实时时间
     * 
     * @param timeFormat 时间格式 yyyy/MM/dd(年月日) HH:mm:ss(时分秒) EEEE(星期几) EE(周几)
     * @param textView 放在哪个文本视图上
     * @return 
     */
    public static TextView textAddTime(String timeFormat, TextView textView) {
        if (textView == null) {
            return textView;
        }
        final String timeFormat_A=timeFormat;
        final TextView textView_A=textView;
        final String text=(textView.getText().toString() != null) ?textView.getText().toString(): "";
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                long currentTime = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat(timeFormat_A != null ?timeFormat_A: "yyyy年MM月dd日 HH:mm:ss EE", Locale.getDefault());
                String timeString = sdf.format(new Date(currentTime));
                textView_A.setText(Html.fromHtml(text +   "<font color='" + ordinaryDataColor + "'>" + timeString + "</font>"));
            }
        };

        // 启动线程
        new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        handler.sendEmptyMessage(0);
                        try {
                            Thread.sleep(1000); // 每隔一秒更新一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        return textView_A;
    }








    //文本视图添加手机实时帧率 参数：要加进哪个文本视图当中
    public static TextView textAddFps(TextView fpsTextView) {
        if (fpsTextView == null) {
            return fpsTextView;
        }
        final TextView textView_A=fpsTextView;
        final String text= (fpsTextView.getText().toString() != null) ?fpsTextView.getText().toString(): "";
        final Choreographer choreographer = Choreographer.getInstance();
        Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
            long lastFrameTimeNanos = 0;

            @Override
            public void doFrame(long frameTimeNanos) {
                // 计算上一帧和当前帧之间的时间差
                long frameIntervalNanos = frameTimeNanos - lastFrameTimeNanos;
                lastFrameTimeNanos = frameTimeNanos;

                // 计算当前帧的帧率
                double fps = 1_000_000_000.0 / frameIntervalNanos;

                // 显示帧率
                textView_A.setText(Html.fromHtml(text +  "<font color='" + ordinaryDataColor + "'>" + String.format(Locale.getDefault(), "%.2f FPS", fps) + "</font>"));

                // 请求下一帧
                choreographer.postFrameCallback(this);
            }
        };

        choreographer.postFrameCallback(frameCallback);
        return textView_A;
    }







    //文本视图添加实时检测SELinux状态(强制模式/宽容模式) 参数：要加进哪个文本视图当中
    public static TextView textAddSELinuxMode(TextView textView) {
        if (textView == null) {
            return textView;
        }
        final TextView textView_A=textView;
        final String text=(textView.getText().toString() != null) ?textView.getText().toString(): "";
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                //检测selinux状态
                textView_A.setText(
                    Html.fromHtml(
                        text + 
                        (
                        SystemTool.isSELinuxPermissive() 
                        ?
                        "<font color='#EF5350'>环境危险！SELinux处于-宽容模式</font>"
                        :
                        "<font color='#3F51B5'>环境安全！SELinux处于-强制模式</font>"
                        )
                    )
                );


            }
        };

        // 启动线程更新时间
        new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        handler.sendEmptyMessage(0);
                        try {
                            Thread.sleep(1000); // 每隔一秒更新一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        return textView_A;
    }





    //文本视图添加实时检测当前应用ROOT授予状态
    public static TextView textAddDetectAppRooted(TextView textView) {
        if (textView == null) {
            return textView;
        }
        final TextView textView_A=textView;
        final String text=(textView.getText().toString() != null) ?textView.getText().toString(): "";
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                //检测当前应用APP授予ROOT的状态 显示不同文本
                textView_A.setText(
                    Html.fromHtml(
                        text + 
                        (
                        AppTool.isRootEnabled()
                        ?
                        "<font color='#009688'>当前应用已授予ROOT权限</font>"
                        :
                        "<font color='#795548'>当前应用未授予ROOT权限</font>"
                        )
                    )
                );

            }
        };

        // 启动线程更新时间
        new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        handler.sendEmptyMessage(0);
                        try {
                            Thread.sleep(1000); // 每隔一秒更新一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        return textView_A;
    }
}
