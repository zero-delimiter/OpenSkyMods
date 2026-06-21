package irene.window.algui;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import irene.window.algui.CustomizeView.GradientTextView;
import irene.window.algui.Tools.AppPermissionTool;
import irene.window.algui.Tools.ViewTool;

/**
 * @Author 𝘽𝙮·艾琳 - ［Copyright © 2023 艾琳 版权所有］游戏逆向交流群730967224 - 作者QQ3353484607
 * @Date 2024/01/28 03:06
 * @Describe 窗口视图
 */
public class AlGuiWindowView {

    public static final String TAG = "AlGuiHoverView";

    //屏幕悬浮显示一个霓虹灯文本
    public static GradientTextView showNeonLightText(
        Context hContext,//上下文
        CharSequence text,//文本
        int[] textColors, //文本颜色
        float textSize, //文本大小
        Typeface textTF,//文本字体
        int gravity,//重力
        int x, int y //xy位置偏移
    ) {
        if (hContext == null) {
            return null;
        }
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (!AppPermissionTool.isAndroidManifestPermissionExist(hContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            Toast.makeText(hContext, "开发者未在AndroidManifest.xml文件中声明悬浮窗权限因此无法显示悬浮视图，只有声明了此权限才能显示悬浮视图！你也可以手动去安装包此文件中声明android.permission.SYSTEM_ALERT_WINDOW", Toast.LENGTH_LONG).show();
            return null;
        }
        if(!AppPermissionTool.checkOverlayPermission(hContext)){
            //没有允许悬浮窗权限就直接结束
            return null;
        }


        WindowManager wManager = (WindowManager) hContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wParams = new WindowManager.LayoutParams();

        wParams.flags =  AlGuiData.getLiveStreamFlags() |
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|//硬件加速
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | //设置窗口在接收触摸事件时不会拦截其他窗口的触摸事件。其他窗口仍然可以接收触摸事件。
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | //设置窗口不接收焦点（即无法获取键盘输入焦点）。这意味着，如果有其他可获得焦点的窗口存在，焦点将传递给该窗口。
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ; //设置窗口不接收触摸事件。这意味着用户无法通过触摸来与该窗口进行交互。
           // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | //指定窗口布局时，考虑窗口所占据的整个屏幕空间。即使窗口没有覆盖整个屏幕，也会根据屏幕的大小和方向进行布局。
            // WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | //指定窗口布局时，不受限制地占据整个屏幕空间。窗口可以延伸到状态栏、导航栏等系统UI的区域。
           // WindowManager.LayoutParams.FLAG_FULLSCREEN;//将窗口设置为全屏模式。窗口将覆盖整个屏幕，并隐藏状态栏和导航栏。

        wParams.gravity = gravity;
        wParams.x = x;
        wParams.y = y;
        wParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wParams.format = PixelFormat.RGBA_8888;
        wParams.windowAnimations = android.R.style.Animation_Toast;

        //系统级窗口 (需要悬浮窗权限)
        wParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);


        final GradientTextView WaterMark = new GradientTextView(hContext);
        WaterMark.setColors(textColors);
        //设置文本视图阴影 参数：文本视图，阴影半径，阴影水平偏移量，阴影垂直偏移量，阴影颜色
        //ViewTool.setTextViewShadow(WaterMark, 10, 0, 0, 0xFF000000);
        WaterMark.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        WaterMark.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(hContext, textSize));
        if (textTF != null) {
            WaterMark.setTypeface(textTF);
        }
        if (text != null) {
            WaterMark.setText(text);
        }
        wManager.addView(WaterMark, wParams);

        return WaterMark;

	}







    //屏幕悬浮显示一个文本
    public static TextView showText(
        Context hContext,//上下文
        CharSequence text,//文本
        int textColor, //文本颜色
        float textSize, //文本大小
        Typeface textTF,//文本字体
        int gravity,//重力
        int x, int y //xy位置偏移
    ) {
        if (hContext == null) {
            return null;
        }
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (!AppPermissionTool.isAndroidManifestPermissionExist(hContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            Toast.makeText(hContext, "开发者未在AndroidManifest.xml文件中声明悬浮窗权限因此无法显示悬浮视图，只有声明了此权限才能显示悬浮视图！你也可以手动去安装包此文件中声明android.permission.SYSTEM_ALERT_WINDOW", Toast.LENGTH_LONG).show();
            return null;
        }
        if(!AppPermissionTool.checkOverlayPermission(hContext)){
            //没有允许悬浮窗权限就直接结束
            return null;
        }

        WindowManager wManager = (WindowManager) hContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wParams = new WindowManager.LayoutParams();

        wParams.flags = AlGuiData.getLiveStreamFlags() |
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|//硬件加速
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | //设置窗口在接收触摸事件时不会拦截其他窗口的触摸事件。其他窗口仍然可以接收触摸事件。
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | //设置窗口不接收焦点（即无法获取键盘输入焦点）。这意味着，如果有其他可获得焦点的窗口存在，焦点将传递给该窗口。
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ; //设置窗口不接收触摸事件。这意味着用户无法通过触摸来与该窗口进行交互。
            //WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;  //指定窗口布局时，考虑窗口所占据的整个屏幕空间。即使窗口没有覆盖整个屏幕，也会根据屏幕的大小和方向进行布局。
        // WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | //指定窗口布局时，不受限制地占据整个屏幕空间。窗口可以延伸到状态栏、导航栏等系统UI的区域。
        //WindowManager.LayoutParams.FLAG_FULLSCREEN;//将窗口设置为全屏模式。窗口将覆盖整个屏幕，并隐藏状态栏和导航栏。

        wParams.gravity = gravity;
        wParams.x = x;
        wParams.y = y;
        wParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wParams.format = PixelFormat.RGBA_8888;
        wParams.windowAnimations = android.R.style.Animation_Toast;
        //系统级窗口 (需要悬浮窗权限)
        wParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);


        final TextView WaterMark = new TextView(hContext);
        WaterMark.setTextColor(textColor);
        //设置文本视图阴影 参数：文本视图，阴影半径，阴影水平偏移量，阴影垂直偏移量，阴影颜色
        //ViewTool.setTextViewShadow(WaterMark, ViewTool.convertDpToPx(hContext, textSize), 0, 0, 0xFF000000);
        WaterMark.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        WaterMark.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(hContext, textSize));
        if (textTF != null) {
            WaterMark.setTypeface(textTF);
        }
        if (text != null) {
            WaterMark.setText(text);
        }

        wManager.addView(WaterMark, wParams);
        return WaterMark;

	}








    //悬浮显示网络视图
    public static void showWebView(
        Context mContext,//上下文
        CharSequence title,//标题(用于悬浮球标题)
        String html//网络代码
    ) {
        if (mContext == null || html == null) {
            return;
        }
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (!AppPermissionTool.isAndroidManifestPermissionExist(mContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            Toast.makeText(mContext, "开发者未在AndroidManifest.xml文件中声明悬浮窗权限因此无法显示悬浮视图，只有声明了此权限才能显示悬浮视图！你也可以手动去安装包此文件中声明android.permission.SYSTEM_ALERT_WINDOW", Toast.LENGTH_LONG).show();
            return;
        }
        if(!AppPermissionTool.checkOverlayPermission(mContext)){
            //没有允许悬浮窗权限就直接结束
            return;
        }

        //悬浮内容窗口
        final WindowManager windowManager = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
        // 创建 WindowManager.LayoutParams 对象，用于设置 View 的位置和大小等参数
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.windowAnimations = android.R.style.Animation_Toast;
        layoutParams.flags = AlGuiData.getLiveStreamFlags() |
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|//硬件加速
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |//非模态标志。当设置此标志时，窗口将不会把触摸事件传递给下层的窗口。
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH ;//标志表示悬浮窗口将监视外部触摸事件，并在触摸事件发生时接收通知。
            //WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN//指定窗口布局时，考虑窗口所占据的整个屏幕空间。即使窗口没有覆盖整个屏幕，也会根据屏幕的大小和方向进行布局。
            //WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |//锁屏显示标志。当设置此标志时，窗口将在锁屏时显示
            //WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |//保持屏幕开启标志。当设置此标志时，系统将保持屏幕处于高亮状态，直到你清除此标志或关闭窗口
            // WindowManager.LayoutParams.FLAG_FULLSCREEN //全屏标志。当设置此标志时，窗口将占据整个屏幕，覆盖状态栏和导航栏。
            //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |//标志表示悬浮窗口无法获取焦点，这样点击悬浮窗口外部区域时事件不会传递给下面的窗口。
           
        //悬浮内容设置系统级窗口 (需要悬浮窗权限)
        layoutParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        //悬浮球窗口
        final WindowManager ballManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams ballParams = new WindowManager.LayoutParams();
        ballParams.flags =AlGuiData.getLiveStreamFlags() |
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|//硬件加速
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        ballParams.gravity = Gravity.CENTER;
        ballParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ballParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ballParams.format = PixelFormat.RGBA_8888;
        ballParams.windowAnimations = android.R.style.Animation_Toast;
        //悬浮球设置系统级窗口 (需要悬浮窗权限)
        ballParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //创建悬浮球根布局
        final FrameLayout ballLayout = new FrameLayout(mContext);

        //悬浮球
        final LinearLayout suspendedBall=addRoundButton(
            mContext,//上下文
            title, 0xFFFFFFFF, //文本，文本颜色
            //按钮大小，背景颜色，描边大小，描边颜色
            40, 0xCE009688, 1.5f, 0xCEFFFFFF
        );
        ballLayout.addView(suspendedBall);


        //根布局 (作为窗口布局 所以他的布局参数就是悬浮窗的布局参数 要更改此布局的参数就去改悬浮窗布局参数)
        final FrameLayout rootLayout = new FrameLayout(mContext);
        rootLayout.setClipChildren(true);
        //悬浮布局
        final LinearLayout Layout = new LinearLayout(mContext);
        LinearLayout.LayoutParams  Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams .WRAP_CONTENT, LinearLayout.LayoutParams .WRAP_CONTENT);
        Params.setMargins(80, 80, 80, 80);//外边距
        Layout.setLayoutParams(Params);
        Layout.setClipChildren(true);
        // rootLayout.setGravity(Gravity.CENTER);
        Layout.setOrientation(LinearLayout.VERTICAL);
        //背景
        int sSize=ViewTool.convertDpToPx(mContext, 1);//描边宽度
        GradientDrawable mainBackground = new GradientDrawable();
        mainBackground.setShape(GradientDrawable.RECTANGLE);
        mainBackground.setColor(0xa2303030);//背景颜色
        mainBackground.setStroke(sSize, 0xCEFFFFFF);//边框厚度与描边颜色
        Layout.setBackground(mainBackground);

        //网络视图
        final WebView view= addWebView(mContext, html);
        LinearLayout.LayoutParams  viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams .WRAP_CONTENT, LinearLayout.LayoutParams .WRAP_CONTENT);
        viewParams.setMargins(30, 30, 30, 30);//外边距
        view.setLayoutParams(viewParams);

        //按钮布局
        LinearLayout buttonLayout = new LinearLayout(mContext);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                         LinearLayout.LayoutParams.WRAP_CONTENT, 
                                         LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        // buttonLayout.setGravity(Gravity.CENTER);
        // buttonLayout.setPadding(20, 20, 20, 20);
        //关闭按钮
        LinearLayout shutLayout=addSquareButton(
            mContext,//上下文
            "关闭", 0xFFFFFFFF, //文本，文本颜色
            //按钮大小，背景颜色，描边大小，描边颜色
            35, 0xCEee5350, 0, 0xCEFFFFFF
        );
        //设置关闭按钮点击事件
        shutLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    windowManager.removeView(rootLayout);
                }
            });


        //隐藏按钮
        LinearLayout hideLayout=addSquareButton(
            mContext,//上下文
            "隐藏", 0xFFFFFFFF, //文本，文本颜色
            //按钮大小，背景颜色，描边大小，描边颜色
            35, 0xCE009688, 0, 0xCEFFFFFF
        );
        //设置隐藏按钮点击事件
        hideLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    windowManager.removeView(rootLayout);//删除悬浮内容窗口
                    ballManager.addView(ballLayout, ballParams);//显示悬浮球窗口

                }
            });



        //刷新按钮
        LinearLayout refreshLayout=addSquareButton(
            mContext,//上下文
            "刷新", 0xFFFFFFFF, //文本，文本颜色
            //按钮大小，背景颜色，描边大小，描边颜色
            35, 0xCE009688, 0, 0xCEFFFFFF
        );
        //设置刷新按钮点击事件
        refreshLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //view.loadData(html, "text/html", "utf-8");
                    view.reload();//刷新页面
                    windowManager.updateViewLayout(rootLayout, layoutParams);//更新窗口
                }
            });




        buttonLayout.addView(shutLayout);//按钮布局添加关闭按钮视图
        buttonLayout.addView(hideLayout);//按钮布局添加隐藏按钮视图
        buttonLayout.addView(refreshLayout);//按钮布局添加刷新按钮视图

        Layout.addView(buttonLayout);//悬浮布局添加按钮布局
        Layout.addView(view);//悬浮布局添加网络视图

        rootLayout.addView(Layout);//根布局添加悬浮布局

        windowManager.addView(rootLayout, layoutParams);//悬浮内容窗口添加根布局


        rootLayout.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    switch (event.getActionMasked()) {
                            //点击了内容窗口布局之外区域时触发 也就相当于点击了window之外的区域
                        case MotionEvent.ACTION_OUTSIDE:
                            windowManager.removeView(rootLayout);//删除悬浮内容窗口
                            ballManager.addView(ballLayout, ballParams);//显示悬浮球窗口
                            return true;
                    }
                    return false;
                }
            });



        //设置悬浮球点击事件
        suspendedBall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                }
            });
        //悬浮球触摸监听器
        suspendedBall.setOnTouchListener(new OnTouchListener() {
                private int signX;
                private int signY;
                private float downX;
                private float downY;
                boolean isOne=true;//第一次移动时设置透明度标识
                boolean isMove=false;//当前是否在移动
                int moveThreshold=20;//手指移动的阀值(灵敏度) 改小更容易触发移动 太小可能导致误判打不开悬浮窗

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {


                            //手指按下时触发
                        case MotionEvent.ACTION_DOWN:
                            isOne = true;
                            isMove = false;
                            signX = ballParams.x;//记录视图初始位置的横向坐标
                            signY = ballParams.y;//记录视图初始位置的竖向坐标
                            downX = event.getRawX();//记录手指按下时的绝对横向坐标
                            downY = event.getRawY();//记录手指按下时的绝对竖向坐标
                            break;


                            //手指移动时触发
                        case MotionEvent.ACTION_MOVE:

                            float moveDistanceX = Math.abs(event.getRawX() - downX);
                            float moveDistanceY = Math.abs(event.getRawY() - downY);
                            if (moveDistanceX > moveThreshold || moveDistanceY > moveThreshold) {
                                isMove = true;//当前是移动

                            }





                            if (isMove) {
                                //第一次移动执行的内容
                                if (isOne) {
                                    ballLayout.setAlpha(0.3f);//设置悬浮球透明度 透明度介于0和1之间
                                    isOne = false;//不是第一次移动了
                                }
                                ballParams.x = signX + (int) (event.getRawX() - downX);//根据手指移动的距离计算视图新的横向坐标
                                ballParams.y = signY + (int) (event.getRawY() - downY);//根据手指移动的距离计算视图新的竖向坐标
                                ballManager.updateViewLayout(ballLayout, ballParams);//更新视图位置
                            }

                            break;


                            //手指抬起时触发
                        case MotionEvent.ACTION_UP:
                            //不是移动状态 抬起的 那么执行这些内容
                            if (!isMove) {
                                ballManager.removeView(ballLayout);//删除悬浮球窗口
                                windowManager.addView(rootLayout, layoutParams);//显示悬浮内容窗口
                            }
                            //移动状态抬起的就执行
                            if (isMove) {
                                ballLayout.setAlpha(1);//设置悬浮球透明度 透明度介于0和1之间
                            }

                            break;
                    }
                    return false;
                }
            });
        //倒计时5毫秒后刷新web页面
        CountDownTimer timer = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                //倒计时期间，可以更新提示信息
            }

            @Override
            public void onFinish() {
                //倒计时结束后，刷新页面
                view.reload();
            }
        };
        timer.start();


    }





    //悬浮显示网站视图
    public static void showWebSite(
        Context mContext,//上下文
        CharSequence title,//标题(用于悬浮球标题)
        final String html//网络代码
    ) {
        if (mContext == null || html == null) {
            return;
        }
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (!AppPermissionTool.isAndroidManifestPermissionExist(mContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            Toast.makeText(mContext, "开发者未在AndroidManifest.xml文件中声明悬浮窗权限因此无法显示悬浮视图，只有声明了此权限才能显示悬浮视图！你也可以手动去安装包此文件中声明android.permission.SYSTEM_ALERT_WINDOW", Toast.LENGTH_LONG).show();
            return;
        }
        if(!AppPermissionTool.checkOverlayPermission(mContext)){
            //没有允许悬浮窗权限就直接结束
            return;
        }

        //悬浮内容窗口
        final WindowManager windowManager = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
        // 创建 WindowManager.LayoutParams 对象，用于设置 View 的位置和大小等参数
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.windowAnimations = android.R.style.Animation_Toast;
        layoutParams.flags = AlGuiData.getLiveStreamFlags() |
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|//硬件加速
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |//非模态标志。当设置此标志时，窗口将不会把触摸事件传递给下层的窗口。
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH ;//标志表示悬浮窗口将监视外部触摸事件，并在触摸事件发生时接收通知。
            //WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN//指定窗口布局时，考虑窗口所占据的整个屏幕空间。即使窗口没有覆盖整个屏幕，也会根据屏幕的大小和方向进行布局。
            //WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |//锁屏显示标志。当设置此标志时，窗口将在锁屏时显示
            //WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |//保持屏幕开启标志。当设置此标志时，系统将保持屏幕处于高亮状态，直到你清除此标志或关闭窗口
            // WindowManager.LayoutParams.FLAG_FULLSCREEN //全屏标志。当设置此标志时，窗口将占据整个屏幕，覆盖状态栏和导航栏。
            //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |//标志表示悬浮窗口无法获取焦点，这样点击悬浮窗口外部区域时事件不会传递给下面的窗口。
            
        //悬浮内容设置系统级窗口 (需要悬浮窗权限)
        layoutParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        //悬浮球窗口
        final WindowManager ballManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams ballParams = new WindowManager.LayoutParams();
        ballParams.flags =AlGuiData.getLiveStreamFlags() |
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|//硬件加速
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        ballParams.gravity = Gravity.CENTER;
        ballParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ballParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ballParams.format = PixelFormat.RGBA_8888;
        ballParams.windowAnimations = android.R.style.Animation_Toast;
        //悬浮球设置系统级窗口 (需要悬浮窗权限)
        ballParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //创建悬浮球根布局
        final FrameLayout ballLayout = new FrameLayout(mContext);

        //悬浮球
        final LinearLayout suspendedBall=addRoundButton(
            mContext,//上下文
            title, 0xFFFFFFFF, //文本，文本颜色
            //按钮大小，背景颜色，描边大小，描边颜色
            40, 0xCE009688, 1.5f, 0xCEFFFFFF
        );
        ballLayout.addView(suspendedBall);


        //根布局 (作为窗口布局 所以他的布局参数就是悬浮窗的布局参数 要更改此布局的参数就去改悬浮窗布局参数)
        final FrameLayout rootLayout = new FrameLayout(mContext);
        rootLayout.setClipChildren(true);
        //悬浮布局
        final LinearLayout Layout = new LinearLayout(mContext);
        LinearLayout.LayoutParams  Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams .MATCH_PARENT, LinearLayout.LayoutParams .MATCH_PARENT);
        //  Params.setMargins(80, 80, 80, 80);//外边距
        Layout.setLayoutParams(Params);
        Layout.setClipChildren(true);
        // rootLayout.setGravity(Gravity.CENTER);
        Layout.setOrientation(LinearLayout.VERTICAL);
        //背景
        int sSize=ViewTool.convertDpToPx(mContext, 1);//描边宽度
        GradientDrawable mainBackground = new GradientDrawable();
        mainBackground.setShape(GradientDrawable.RECTANGLE);
        mainBackground.setColor(0xa2303030);//背景颜色
        mainBackground.setStroke(sSize, 0xCEFFFFFF);//边框厚度与描边颜色
        Layout.setBackground(mainBackground);

        //网站视图
        final WebView view= addWebSite(mContext, html);
        LinearLayout.LayoutParams  viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams .MATCH_PARENT, LinearLayout.LayoutParams .MATCH_PARENT);
        viewParams.setMargins(30, 30, 30, 30);//外边距
        view.setLayoutParams(viewParams);

        //按钮布局
        LinearLayout buttonLayout = new LinearLayout(mContext);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                         LinearLayout.LayoutParams.WRAP_CONTENT, 
                                         LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        // buttonLayout.setGravity(Gravity.CENTER);
        // buttonLayout.setPadding(20, 20, 20, 20);
        //关闭按钮
        LinearLayout shutLayout=addSquareButton(
            mContext,//上下文
            "关闭", 0xFFFFFFFF, //文本，文本颜色
            //按钮大小，背景颜色，描边大小，描边颜色
            35, 0xCEee5350, 0, 0xCEFFFFFF
        );
        //设置关闭按钮点击事件
        shutLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    windowManager.removeView(rootLayout);
                }
            });


        //隐藏按钮
        LinearLayout hideLayout=addSquareButton(
            mContext,//上下文
            "隐藏", 0xFFFFFFFF, //文本，文本颜色
            //按钮大小，背景颜色，描边大小，描边颜色
            35, 0xCE009688, 0, 0xCEFFFFFF
        );
        //设置隐藏按钮点击事件
        hideLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    windowManager.removeView(rootLayout);//删除悬浮内容窗口
                    ballManager.addView(ballLayout, ballParams);//显示悬浮球窗口

                }
            });



        //刷新按钮
        LinearLayout refreshLayout=addSquareButton(
            mContext,//上下文
            "刷新", 0xFFFFFFFF, //文本，文本颜色
            //按钮大小，背景颜色，描边大小，描边颜色
            35, 0xCE009688, 0, 0xCEFFFFFF
        );
        //设置刷新按钮点击事件
        refreshLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //view.loadData(html, "text/html", "utf-8");
                    view.reload();//刷新页面
                    windowManager.updateViewLayout(rootLayout, layoutParams);//更新窗口
                }
            });




        buttonLayout.addView(shutLayout);//按钮布局添加关闭按钮视图
        buttonLayout.addView(hideLayout);//按钮布局添加隐藏按钮视图
        buttonLayout.addView(refreshLayout);//按钮布局添加刷新按钮视图


        Layout.addView(buttonLayout);//悬浮布局添加按钮布局
        Layout.addView(view);//悬浮布局添加网络视图

        rootLayout.addView(Layout);//根布局添加悬浮布局

        windowManager.addView(rootLayout, layoutParams);//悬浮内容窗口添加根布局


        rootLayout.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    switch (event.getActionMasked()) {
                            //点击了内容窗口布局之外区域时触发 也就相当于点击了window之外的区域
                        case MotionEvent.ACTION_OUTSIDE:
                            windowManager.removeView(rootLayout);//删除悬浮内容窗口
                            ballManager.addView(ballLayout, ballParams);//显示悬浮球窗口
                            return true;
                    }
                    return false;
                }
            });



        //设置悬浮球点击事件
        suspendedBall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                }
            });
        //悬浮球触摸监听器
        suspendedBall.setOnTouchListener(new OnTouchListener() {
                private int signX;
                private int signY;
                private float downX;
                private float downY;
                boolean isOne=true;//第一次移动时设置透明度标识
                boolean isMove=false;//当前是否在移动
                int moveThreshold=20;//手指移动的阀值(灵敏度) 改小更容易触发移动 太小可能导致误判打不开悬浮窗

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {


                            //手指按下时触发
                        case MotionEvent.ACTION_DOWN:
                            isOne = true;
                            isMove = false;
                            signX = ballParams.x;//记录视图初始位置的横向坐标
                            signY = ballParams.y;//记录视图初始位置的竖向坐标
                            downX = event.getRawX();//记录手指按下时的绝对横向坐标
                            downY = event.getRawY();//记录手指按下时的绝对竖向坐标
                            break;


                            //手指移动时触发
                        case MotionEvent.ACTION_MOVE:

                            float moveDistanceX = Math.abs(event.getRawX() - downX);
                            float moveDistanceY = Math.abs(event.getRawY() - downY);
                            if (moveDistanceX > moveThreshold || moveDistanceY > moveThreshold) {
                                isMove = true;//当前是移动

                            }





                            if (isMove) {
                                //第一次移动执行的内容
                                if (isOne) {
                                    ballLayout.setAlpha(0.3f);//设置悬浮球透明度 透明度介于0和1之间
                                    isOne = false;//不是第一次移动了
                                }
                                ballParams.x = signX + (int) (event.getRawX() - downX);//根据手指移动的距离计算视图新的横向坐标
                                ballParams.y = signY + (int) (event.getRawY() - downY);//根据手指移动的距离计算视图新的竖向坐标
                                ballManager.updateViewLayout(ballLayout, ballParams);//更新视图位置
                            }

                            break;


                            //手指抬起时触发
                        case MotionEvent.ACTION_UP:
                            //不是移动状态 抬起的 那么执行这些内容
                            if (!isMove) {
                                ballManager.removeView(ballLayout);//删除悬浮球窗口
                                windowManager.addView(rootLayout, layoutParams);//显示悬浮内容窗口
                            }
                            //移动状态抬起的就执行
                            if (isMove) {
                                ballLayout.setAlpha(1);//设置悬浮球透明度 透明度介于0和1之间
                            }

                            break;
                    }
                    return false;
                }
            });
        //倒计时5毫秒后刷新web页面
        CountDownTimer timer = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                //倒计时期间，可以更新提示信息
            }

            @Override
            public void onFinish() {
                //倒计时结束后，刷新页面
                view.reload();
            }
        };
        timer.start();


    }






    //增加一个圆形按钮
    private static LinearLayout addRoundButton(
        Context mContext,//上下文
        CharSequence text, int textColor, //文本，文本颜色
        //按钮大小，背景颜色，描边大小，描边颜色
        float buttonSize, int backColor, float strokeSize, int strokeColor
    ) {
        if (mContext == null) {
            return null;
        }
        //背景
        int sSize=ViewTool.convertDpToPx(mContext, strokeSize);
        GradientDrawable mainBackground = new GradientDrawable();
        mainBackground.setShape(GradientDrawable.OVAL);
        mainBackground.setColor(backColor);//背景颜色
        mainBackground.setStroke(sSize, strokeColor);//边框厚度与描边颜色

        //布局
        final LinearLayout shutLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(
            ViewTool.convertDpToPx(mContext, buttonSize), 
            ViewTool.convertDpToPx(mContext, buttonSize));
        p.setMargins(5, 5, 5, 5);
        shutLayout.setLayoutParams(p);
        shutLayout.setGravity(Gravity.CENTER);
        shutLayout.setBackground(mainBackground);
        shutLayout.setPadding(sSize, sSize, sSize, sSize);

        //设置渐变色
        //ViewTool.setGradientBackground(shutLayout,new int[]{0xFF6025f5,0xFFff5555},GradientDrawable.LINEAR_GRADIENT);
        final TextView textview=new TextView(mContext);
        textview.setGravity(Gravity.CENTER);
        if (text != null) {
            textview.setText(text);
        }

        textview.setTextColor(textColor);
        shutLayout.addView(textview);
        textview.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(mContext, 10f));
        /*
         第一个参数是最小的文字大小（以 sp 为单位）。
         第二个参数是最大的文字大小（以 sp 为单位）。
         第三个参数是文字大小的步长（以 sp 为单位）。
         最后一个参数指定了单位，这里使用的是 TypedValue.COMPLEX_UNIT_SP
         */
         //安卓8.0以下有影响
        //textview.setAutoSizeTextTypeUniformWithConfiguration(2, ViewTool.convertDpToPx(mContext, buttonSize), 1, TypedValue.COMPLEX_UNIT_SP);
        return shutLayout;
    }

    //增加一个方形按钮
    private static LinearLayout addSquareButton(
        Context mContext,//上下文
        CharSequence text, int textColor, //文本，文本颜色
        //按钮大小，背景颜色，描边大小，描边颜色
        float buttonSize, int backColor, float strokeSize, int strokeColor
    ) {
        if (mContext == null) {
            return null;
        }
        //背景
        int sSize=ViewTool.convertDpToPx(mContext, strokeSize);
        GradientDrawable mainBackground = new GradientDrawable();
        mainBackground.setShape(GradientDrawable.RECTANGLE);
        mainBackground.setColor(backColor);//背景颜色
        mainBackground.setStroke(sSize, strokeColor);//边框厚度与描边颜色

        //布局
        final LinearLayout shutLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(
            ViewTool.convertDpToPx(mContext, buttonSize), 
            ViewTool.convertDpToPx(mContext, buttonSize));
        p.setMargins(4, 4, 4, 4);
        shutLayout.setLayoutParams(p);
        shutLayout.setGravity(Gravity.CENTER);
        shutLayout.setBackground(mainBackground);
        shutLayout.setPadding(sSize, sSize, sSize, sSize);
      
        //设置渐变色
        //ViewTool.setGradientBackground(shutLayout,new int[]{0xFF6025f5,0xFFff5555},GradientDrawable.LINEAR_GRADIENT);
        final TextView textview=new TextView(mContext);
        textview.setGravity(Gravity.CENTER);
        if (text != null) {
            textview.setText(text);
        }

        textview.setTextColor(textColor);
        shutLayout.addView(textview);
        //ptext.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 15f));
        /*
         第一个参数是最小的文字大小（以 sp 为单位）。
         第二个参数是最大的文字大小（以 sp 为单位）。
         第三个参数是文字大小的步长（以 sp 为单位）。
         最后一个参数指定了单位，这里使用的是 TypedValue.COMPLEX_UNIT_SP
         */
        textview.setAutoSizeTextTypeUniformWithConfiguration(2, ViewTool.convertDpToPx(mContext, buttonSize), 1, TypedValue.COMPLEX_UNIT_SP);
        return shutLayout;
    }

    //增加一个web自定义网络视图 (具有完全的html支持)
    private static WebView addWebView(
        final Context aContext,
        String text//网络代码
    ) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT);
        //layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        WebView wView = new WebView(aContext);
        wView.setId(AlGuiData.AlguiView.WebView.getId());
        wView.setLayoutParams(layoutParams);
        wView.setBackgroundColor(0x00000000); //设置背景颜色
     
        /*
         用于禁用 WebView 对应用程序缓存的使用
         调用该方法可以避免 WebView 在加载网页时缓存文件，从而减少应用程序的内存占用
         如果你不想让 WebView 使用应用程序缓存，可以通过该方法来禁用它
         */
        wView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wView.getSettings().setUseWideViewPort(true);//页面自适应屏幕宽度
        wView.getSettings().setLoadWithOverviewMode(true);//图片自适应屏幕大小
        wView.getSettings().setJavaScriptEnabled(true);// 允许在 WebView 中执行 JavaScript 代码，
        wView.getSettings().setLoadsImagesAutomatically(true);//自动加载图片。
        wView.getSettings().setBlockNetworkImage(false);//是否阻塞网络图片加载。
        //setCacheMode(int mode)：设置缓存模式，如 LOAD_DEFAULT、LOAD_CACHE_ELSE_NETWORK、LOAD_NO_CACHE 等。
        wView.getSettings().setDomStorageEnabled(true);//启用或禁用 DOM Storage API。
        wView.getSettings().setAllowFileAccess(true);//是否允许 WebView 访问文件。
        wView.getSettings().setGeolocationEnabled(true);//启用或禁用地理位置定位。
        wView.getSettings().setDatabaseEnabled(true);//启用或禁用数据库。
        wView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置 JavaScript 是否可以自动打开窗口。
        wView.getSettings().setDefaultTextEncodingName("utf-8");//设置默认的文本编码名称。
        wView.getSettings().setDomStorageEnabled(true);//启用对DOM存储的支持
        wView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    /*AlGuiBubbleNotification.Inform(aContext).showMistakeNotification_Simplicity(null, "无法加载", "我们将自动跳转到第三方游览器来加载！", 5000);
                    // 在加载出错时，跳转到系统默认的浏览器
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(failingUrl));
                    ((Activity)aContext).startActivity(intent);*/
                }
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //使其网站中的所有超链接直接跳转而不是打开游览器跳转
                    super.shouldOverrideUrlLoading(view, url);
                    view.loadUrl(url);
                    return true;
                }

            });

        if (text == null) {
            return wView;
        }


        //否则加载自定义HTML网页
        /*
         该方法用于在 WebView 控件中加载一段 HTML 文本
         第一个参数 text 是要加载的 HTML 文本
         第二个参数 "text/html" 表示文本的 MIME 类型
         第三个参数 "utf-8" 表示文本的编码格式
         通过调用该方法，可以在 WebView 中显示自定义的 HTML 内容。
         */
        wView.loadData(text, "text/html", "utf-8");

        return wView;
    }

    //增加一个网站
    private static WebView addWebSite(
        Context context,
        String url//网站链接
    ) {
        WebView webSite=addWebView(context, null);
        if (url == null) {
            return webSite;
        }
        webSite.loadUrl(url);


        return  webSite;
    }


}
