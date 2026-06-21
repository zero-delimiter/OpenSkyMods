package irene.window.algui;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import irene.window.algui.CustomizeView.vFrameLayout;
import irene.window.algui.CustomizeView.vLinearLayout;
import irene.window.algui.Tools.AppPermissionTool;
import irene.window.algui.Tools.ImageTool;
import irene.window.algui.Tools.ViewTool;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/26 13:48
 * @Describe 右下角气泡通知 (ALGUI内部组件)
 */
public class AlGuiBubbleNotification {

    public static final String TAG = "AlGuiBubbleNotification";
    static private AlGuiBubbleNotification bn;
    private Context context;
    private boolean isWLayout = false;//窗口是否添加了布局的标识
    private WindowManager Manager;
    private WindowManager.LayoutParams wParams;
    private vFrameLayout rootLayout;
    private vLinearLayout mainLayout;
    
    //获取窗口管理器
    public WindowManager getWindowManager(){
        return Manager;
    }
    
    //获取窗口布局参数
    public WindowManager.LayoutParams getWindowManageLayoutParams(){
        return wParams;
    }
    
    //获取窗口根布局
    public vFrameLayout getRootLayout(){
        return rootLayout;
    }
    
    //获取窗口主布局
    public vLinearLayout getMainLayout(){
        return mainLayout;
    }




    //初始化窗口
    private void initWindow() {
        Manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wParams = new WindowManager.LayoutParams();
        wParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wParams.gravity = Gravity.BOTTOM | Gravity.END;//右下角
        wParams.format = PixelFormat.RGBA_8888;
        wParams.windowAnimations = android.R.style.Animation_Toast;
        wParams.flags =
         AlGuiData.getLiveStreamFlags() |
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED //硬件加速
            //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE//不可触摸
            ;
        wParams.y = dpToPx(16); // 设置相对屏幕底部的偏移量
        wParams.x = dpToPx(16); // 设置相对屏幕右侧的偏移量

        /*
         if(aContext instanceof Activity){
         //应用级窗口 (免悬浮权限 但是上下文必须是Activity 且 只能让悬浮窗显示在这一个Activity上方)
         ballParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//悬浮球
         menuParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//悬浮菜单
         return;
         }
         */
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (AppPermissionTool.isAndroidManifestPermissionExist(context, "android.permission.SYSTEM_ALERT_WINDOW")) {
            //系统级窗口 (需要悬浮窗权限)
            wParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    //显示窗口
    public void showW() { 
        if (context == null) {
            return;
        }
        if (!AppPermissionTool.isAndroidManifestPermissionExist(context, "android.permission.SYSTEM_ALERT_WINDOW")) {
            return;
        }
        if(!AppPermissionTool.checkOverlayPermission(context)){
            //没有允许悬浮窗权限就直接结束
            return;
        }
        if (!isWLayout) {
            isWLayout = true;
            Manager.addView(rootLayout, wParams);
        }
    }
    //更新窗口
    public void updateW() {
        if (context == null) {
            return;
        }
        if (isWLayout) {
            Manager.updateViewLayout(rootLayout, wParams);    
        }  
    }
    //清除窗口
    public void clearW() {
        if (context == null) {
            return;
        }
        if (Manager != null) {
            if (isWLayout) {
                isWLayout = false;
                Manager.removeView(rootLayout);
            }
        }
    }


    //单例模式
    public static AlGuiBubbleNotification Inform(Context context) {
        if (bn == null) {
            bn = new AlGuiBubbleNotification(context);
        }
        return bn;
    }

    private AlGuiBubbleNotification(Context context) {
        if (context == null) {
            return;
        }
        this.context = context;
        initWindow();//初始化窗口
        initLayout();//初始化布局
        showW();//显示窗口
    }



    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private void initLayout() {
        //根布局
        rootLayout = new vFrameLayout(context);

        //主要布局
        mainLayout = new vLinearLayout(context);
        mainLayout.setBackgroundColor(Color.TRANSPARENT);//透明
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                       LinearLayout.LayoutParams.WRAP_CONTENT, 
                                       LinearLayout.LayoutParams.WRAP_CONTENT));
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        //默认主要布局加一个空视图 确保主要布局不会在后面添加通知时出现通知消失不全的bug
        TextView textView=new TextView(context);
        textView.setText(" ");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(context, 1f));
        mainLayout.addView((textView));

        rootLayout.addView(mainLayout);

    }

    //增加一个小按钮
    private vLinearLayout addSmallButton(
        final CharSequence text, float textSize, final int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        float CircleRadius ,//圆角半径
        final int backColor,//背景颜色
        float borderSize, int borderColor,//描边大小，描边颜色
        final  T_ButtonOnChangeListener fun) { //事件监听器

        final GradientDrawable back = new GradientDrawable();
        back.setColor(backColor);
        back.setCornerRadius(ViewTool.convertDpToPx(context, CircleRadius));
        back.setStroke(ViewTool.convertDpToPx(context, borderSize), borderColor);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 8, 8, 8);
        final vLinearLayout button=new vLinearLayout(context);
        button.setLayoutParams(layoutParams);
        button.setBackground(back);
        button.setPadding(20, 10, 20, 10);
        button.setClipChildren(true);
        button.setId(AlGuiData.AlguiView.SmallButton.getId());
        button.setGravity(Gravity.CENTER);
     
        final TextView buttonText=new TextView(context);
        //buttonText.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        if(text!=null){
            buttonText.setText(text);
        }
        
        buttonText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(context, textSize));
        buttonText.setTextColor(textColor);
        //buttonText.setGravity(Gravity.CENTER);
        if (textTF != null) {
            buttonText.setTypeface(textTF);
        }
        button.setOnClickListener(new View.OnClickListener() {
                boolean isChecked =true;//点击开关状态
                boolean isOne=true;//确保外部监听器每次执行只执行一次
                @Override
                public void onClick(final View v) {
                    isOne=true;
                    //SoundEffectPlayer.getAudio(aContext).playSoundEffect("mcDanJi.ogg");//播放点击音效

                    /*RippleDrawable rippleDrawable = new RippleDrawable(
                     ColorStateList.valueOf(ViewTool.darkenColor(backColor, 0.6f)), // 点击时水波纹的颜色
                     back, // 按钮原始的背景
                     null); // 水波纹边界，默认为按钮的边界

                     button.setBackground(rippleDrawable);*/

                    // 创建缩放动画，将按钮从原始大小缩小到90%大小再放大到原始大小
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setRepeatCount(1);
                    scaleAnimation.setRepeatMode(Animation.REVERSE);

                    // 创建透明度动画，将按钮的透明度从1变为0.5再变为1
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.5f);
                    alphaAnimation.setDuration(200);
                    alphaAnimation.setRepeatCount(1);
                    alphaAnimation.setRepeatMode(Animation.REVERSE);

                    // 创建动画集合，并将缩放动画和透明度动画添加到动画集合中
                    AnimationSet animationSet = new AnimationSet(true);
                    animationSet.addAnimation(scaleAnimation);
                    animationSet.addAnimation(alphaAnimation);
                    // buttonText.setTextColor(0xFF00FF00); // 变动按钮的文字颜色
                    back.setColor(ViewTool.darkenColor(backColor, 0.7f));// 变动按钮背景颜色(加深)

                    // 设置监听器，在动画结束时恢复按钮的原始状态
                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { 
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                button.clearAnimation();
                                back.setColor(backColor);// 恢复按钮背景颜色
                                if (isOne&&fun != null) {
                                    //执行外部传入的监听
                                    fun.onClick(v, back, buttonText, isChecked);
                                    isChecked = !isChecked;
                                    isOne=false;
                                }
                                
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });
                    // 启动动画
                    button.startAnimation(animationSet);
                    

                    
                }
            });


        button.addView(buttonText);
        return button;
    }
    //普通按钮事件监听器
    public static interface T_ButtonOnChangeListener {
        //点击
        public abstract void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked);
    }


    //显示一个自定义带按钮的通知
    public void showCustomizeButtonNotification(
        int backColor, //通知背景颜色
        Bitmap NotificationIcon, int iconColor,//通知图标，图标颜色(0代表不设置颜色)
        CharSequence titleText, int titleColor,//通知标题，标题颜色
        CharSequence contentText, int contentColor,//通知内容，内容颜色
        //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色， 描边颜色，关闭按钮点击事件
        CharSequence shutButtonText, int shutButtonTextColor, int shutButtonBackColor, int shutStrokeColor, final T_ButtonOnChangeListener cancelFun,
        //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色，确认按钮点击事件
        CharSequence confirmButtonText, int confirmButtonTextColor, int confirmButtonBackColor, int confirmStrokeColor, final T_ButtonOnChangeListener confirmFun
    ) {
        //SoundEffectPlayer.getAudio(context).playSoundEffect("BubbleInform.ogg");//播放音效

        if (context == null) {
            return;
        }
        //通知根布局
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);
        final vLinearLayout layout = new vLinearLayout(context);
        layout.setBackColor(backColor);
        layout.setFilletRadiu(dpToPx(11));
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(20, 20, 20, 20);
        layout.setGravity(Gravity.CENTER);
        layout.setId(AlGuiData.AlguiNotification.ButtonNotification.getId());
        layout.setAlpha(1f);
     

        //通知图标
        final ImageView imageView = new ImageView(context);
        imageView.setPadding(10, 10, 10, 10);
        if (iconColor != 0) {
            imageView.setImageTintList(ColorStateList.valueOf(iconColor)); // 设置图标颜色

        }
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                      dpToPx(30),
                                      dpToPx(30)
                                  ));
        if (NotificationIcon != null) {
            imageView.setImageBitmap(NotificationIcon);
        } else {
            imageView.setImageBitmap(ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Message()));
        }
      
        //设置图标圆形背景
        /*GradientDrawable gradientDrawable = new GradientDrawable();
         gradientDrawable.setShape(GradientDrawable.OVAL);
         gradientDrawable.setColor(ViewTool.createLayeredColor(backColor,0.5f));  // 设置背景颜色*/
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
         imageView.setBackground(gradientDrawable);
         } else {
         imageView.setBackgroundDrawable(gradientDrawable);
         }*/


        //通知文本布局
        LinearLayout.LayoutParams textLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT);
        textLParams.setMargins(10, 0, 0, 0);
        final vLinearLayout textLayout = new vLinearLayout(context);
        textLayout.setBackColor(0);
        textLayout.setLayoutParams(textLParams);
        textLayout.setOrientation(LinearLayout.VERTICAL);

        //通知标题
        TextView title=new TextView(context);
        title.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        if(titleText!=null){
            title.setText(titleText);
        }
       
        // title.setText(ViewTool.wrapText(titleText != null ?titleText: "通知", 10));

        title.setSingleLine(false);//允许自动换行
        title.setMaxEms(11);//一行最大文本宽度(这里用来限制一行数量) 超过将自动换行
        title.setTextColor(titleColor);
        title.setGravity(Gravity.LEFT);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(11));
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));



        //通知内容
        TextView content=new TextView(context);
        content.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        if(contentText!=null){
            content.setText(contentText);
        }
        
        //content.setText(ViewTool.wrapText(contentText != null ?contentText: "这是一个气泡通知", 15));

        content.setSingleLine(false);//允许自动换行
        content.setMaxEms(11);//一行最大文本宽度(这里用来限制一行数量) 超过将自动换行
        content.setTextColor(contentColor);
        content.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(8));
        content.setGravity(Gravity.LEFT);

        LinearLayout.LayoutParams buttonLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLParams.setMargins(0, 0, 30, 0);
        //通知按钮布局
        final vLinearLayout buttonLayout = new vLinearLayout(context);
        buttonLayout.setBackColor(0);
        buttonLayout.setLayoutParams(buttonLParams);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.RIGHT);

        //关闭按钮
        final LinearLayout shutButton = addSmallButton
        (

            shutButtonText,  8, shutButtonTextColor, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),//普通按钮文本，文本大小，文本颜色
            2,//普通按钮圆角半径
            shutButtonBackColor,//普通按钮颜色
            confirmStrokeColor == 0 ?0: 0.7f, shutStrokeColor,//普通按钮描边大小，描边颜色
            //普通按钮点击事件
            new T_ButtonOnChangeListener(){

                @Override// button=普通按钮对象 back=按钮外观对象 buttonText=按钮文本视图对象 isChecked=开关状态
                public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                    //执行外部传递的事件
                    if (cancelFun != null) {
                        cancelFun.onClick(button, back, buttonText, isChecked);
                    }
                    if (layout != null) {
                        // 创建一个Alpha动画对象，设置渐隐效果
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setDuration(2000); // 设置动画持续时间（单位：毫秒）
                        // 设置动画监听器
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    // 动画开始时执行的代码
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    // 动画结束时执行的代码
                                    if (mainLayout.indexOfChild(layout) != -1) {
                                        layout.setVisibility(View.GONE);//不再可见
                                        mainLayout.removeView(layout);
                                    }
                                    if (mainLayout.findViewById(AlGuiData.AlguiNotification.ButtonNotification.getId()) == null) {
                                        //悬浮窗当前已经不存在可交互按钮通知了就将窗口标志设置为不可触摸
                                        wParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | AlGuiData.getLiveStreamFlags();
                                        //更新窗口
                                        updateW();
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    // 动画重复时执行的代码
                                }
                            });
                        layout.startAnimation(fadeOut);
                    }
                }
            }
        );

        //确定按钮
        final LinearLayout confirmButton = addSmallButton
        (
            confirmButtonText,  8, confirmButtonTextColor, Typeface.create(Typeface.DEFAULT, Typeface.BOLD),//普通按钮文本，文本大小，文本颜色
            2,//普通按钮圆角半径
            confirmButtonBackColor,//普通按钮颜色
            confirmStrokeColor == 0 ?0: 0.7f, confirmStrokeColor,//普通按钮描边大小，描边颜色
            //普通按钮点击事件
            new  T_ButtonOnChangeListener(){

                @Override// button=普通按钮对象 back=按钮外观对象 buttonText=按钮文本视图对象 isChecked=开关状态
                public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                    //执行外部传递的事件
                    if (confirmFun != null) {
                        confirmFun.onClick(button, back, buttonText, isChecked);
                    }


                    if (layout != null) {
                        // 创建一个Alpha动画对象，设置渐隐效果
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setDuration(2000); // 设置动画持续时间（单位：毫秒）
                        // 设置动画监听器
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    // 动画开始时执行的代码
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    // 动画结束时执行的代码
                                    if (mainLayout.indexOfChild(layout) != -1) {
                                        layout.setVisibility(View.GONE);//不再可见
                                        mainLayout.removeView(layout);
                                    }
                                    if (mainLayout.findViewById(AlGuiData.AlguiNotification.ButtonNotification.getId()) == null) {
                                        //悬浮窗当前已经不存在可交互按钮通知了就将窗口标志设置为不可触摸
                                        wParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | AlGuiData.getLiveStreamFlags();
                                        //更新窗口
                                        updateW();
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    // 动画重复时执行的代码
                                }
                            });
                        layout.startAnimation(fadeOut);
                    }


                }
            }
        );




        boolean bu1=true;
        boolean bu2=true;
        boolean t1=true;
        boolean t2=true;
        //通知视图结构
        layout.addView(imageView);
        //标题
        if (titleText == null) {
            t1 = false;
        } else {
            textLayout.addView(title);
        }
        //内容
        if (contentText == null) {
            t2 = false;
        } else {
            textLayout.addView(content);
        }
        //取消按钮
        if (shutButtonText == null) {
            bu1 = false;
        } else {
            buttonLayout.addView(shutButton);
        }
        //确定按钮
        if (confirmButtonText == null) {
            bu2 = false;
        } else {
            buttonLayout.addView(confirmButton);
        }
        //按钮布局
        if (bu1 || bu2) {
            textLayout.addView(buttonLayout);
        }
        //文本布局
        if (t1 || t2) {
            layout.addView(textLayout);
        }

        mainLayout.addView(layout);


        //1分钟后清除当前通知
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (layout != null) {
                        // 创建一个Alpha动画对象，设置渐隐效果
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setDuration(2000); // 设置动画持续时间（单位：毫秒）
                        // 设置动画监听器
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    // 动画开始时执行的代码
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    // 动画结束时执行的代码
                                    if (mainLayout.indexOfChild(layout) != -1) {
                                        layout.setVisibility(View.GONE);//不再可见
                                        mainLayout.removeView(layout);
                                    }
                                    if (mainLayout.findViewById(AlGuiData.AlguiNotification.ButtonNotification.getId()) == null) {
                                        //悬浮窗当前已经不存在可交互按钮通知了就将窗口标志设置为不可触摸
                                        wParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | AlGuiData.getLiveStreamFlags();
                                        //更新窗口
                                        updateW();
                                    }

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    // 动画重复时执行的代码
                                }
                            });
                        layout.startAnimation(fadeOut);
                    }

                }
            }, 60000);

        // 创建一个Alpha动画对象，设置渐显效果
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000); // 设置动画持续时间（单位：毫秒）
        layout.startAnimation(fadeIn);

        //将窗口标志重置，使其可触摸
        wParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | AlGuiData.getLiveStreamFlags();
        //更新窗口
        updateW();

    }




    //显示一个自定义通知
    public void showCustomizeNotification(
        int backColor, //通知背景颜色
        Bitmap NotificationIcon, int iconColor,//通知图标，图标颜色(0代表不设置颜色)
        CharSequence titleText, int titleColor,//通知标题，标题颜色
        CharSequence contentText, int contentColor,//通知内容，内容颜色
        long notificationTime//通知时间 (单位毫秒)

    ) {
        if (context == null) {
            return;
        }
        //SoundEffectPlayer.getAudio(context).playSoundEffect("BubbleInform.ogg");//播放音效
        //通知根布局
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);
        final vLinearLayout layout = new vLinearLayout(context);
        layout.setBackColor(backColor);
        layout.setFilletRadiu(dpToPx(11));
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(20, 20, 20, 20);
        layout.setGravity(Gravity.CENTER);
        layout.setId(AlGuiData.AlguiNotification.MessageNotification.getId());
        layout.setAlpha(1f);


        //通知图标
        final ImageView imageView = new ImageView(context);
        imageView.setPadding(10, 10, 10, 10);
        if (iconColor != 0) {
            imageView.setImageTintList(ColorStateList.valueOf(iconColor)); // 设置图标颜色

        }
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                      dpToPx(30),
                                      dpToPx(30)
                                  ));
        if (NotificationIcon != null) {
            imageView.setImageBitmap(NotificationIcon);
        } else {
            imageView.setImageBitmap(ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Message()));
        }
     
        //设置图标圆形背景
        /*GradientDrawable gradientDrawable = new GradientDrawable();
         gradientDrawable.setShape(GradientDrawable.OVAL);
         gradientDrawable.setColor(ViewTool.createLayeredColor(backColor,0.5f));  // 设置背景颜色*/
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
         imageView.setBackground(gradientDrawable);
         } else {
         imageView.setBackgroundDrawable(gradientDrawable);
         }*/



        //通知文本布局
        LinearLayout.LayoutParams textLParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT);
        textLParams.setMargins(10, 0, 0, 0);
        final vLinearLayout textLayout = new vLinearLayout(context);
        textLayout.setBackColor(0);
        textLayout.setLayoutParams(textLParams);
        textLayout.setOrientation(LinearLayout.VERTICAL);

        //通知标题
        TextView title=new TextView(context);
        title.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        //title.setText(ViewTool.wrapText(titleText != null ?titleText: "通知", 10));
        if(title!=null){
            title.setText(titleText);
        }
       

        title.setSingleLine(false);//允许自动换行
        title.setMaxEms(11);//一行最大文本宽度(这里用来限制一行数量) 超过将自动换行
        title.setTextColor(titleColor);
        title.setGravity(Gravity.LEFT);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(11));
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));


        //通知内容
        TextView content=new TextView(context);
        content.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        //content.setText(ViewTool.wrapText(contentText != null ?contentText: "这是一个气泡通知", 15));
        if(content!=null){
            content.setText(contentText);
        }
      

        content.setSingleLine(false);//允许自动换行
        content.setMaxEms(11);//一行最大文本宽度(这里用来限制一行数量) 超过将自动换行
        content.setTextColor(contentColor);
        content.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(8));
        content.setGravity(Gravity.LEFT);

        boolean b=true;
        boolean b2=true;
        //通知视图结构
        layout.addView(imageView);
        if (titleText == null) {
            b = false;
        } else {
            textLayout.addView(title);
        }

        if (contentText == null) {
            b2 = false;
        } else {
            textLayout.addView(content);
        }

        if (b || b2) {
            layout.addView(textLayout);
        }
        /*textLayout.addView(title);
         textLayout.addView(content);
         layout.addView(textLayout);*/
        mainLayout.addView(layout);

        //定时n毫秒后清除当前通知
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (layout != null) {
                        // 创建一个Alpha动画对象，设置渐隐效果
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setDuration(2000); // 设置动画持续时间（单位：毫秒）
                        // 设置动画监听器
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    // 动画开始时执行的代码
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    // 动画结束时执行的代码
                                    if (mainLayout.indexOfChild(layout) != -1) {
                                        layout.setVisibility(View.GONE);//不再可见
                                        mainLayout.removeView(layout);
                                    }

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    // 动画重复时执行的代码
                                }
                            });
                        layout.startAnimation(fadeOut);
                    }

                }
            }, notificationTime);

        // 创建一个Alpha动画对象，设置渐显效果
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000); // 设置动画持续时间（单位：毫秒）
        layout.startAnimation(fadeIn);

        //检查悬浮窗当前是否存在可交互的按钮通知
        if (mainLayout.findViewById(AlGuiData.AlguiNotification.ButtonNotification.getId()) != null) {
            //存在才将窗口标志重置设置为可触摸
            wParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | AlGuiData.getLiveStreamFlags();
        } else {
            //否则将窗口标志设置为不可触摸
            wParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | AlGuiData.getLiveStreamFlags();
        }
        //更新窗口
        updateW();
    }




















    //艾琳模板预制通知

    //精美通知

    //显示一个精美的消息通知
    public void showMessageNotification_Exquisite(Bitmap icon, CharSequence title, CharSequence content, long notificationTime) {
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_MESSAGE_GTA);//播放音效
        showCustomizeNotification
        (
            //通知背景颜色
            0xFFEBF2EB,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getExquisiteNotice_Icon_Message()), 0xFF000000,
            //通知标题，标题颜色
            title, 0xFF000000,
            //通知内容，内容颜色
            content, 0xFF424242,
            //通知时间(单位毫秒)
            notificationTime
        );
    }
    //显示一个精美的消息通知(带按钮)
    public void showMessageNotification_Exquisite_Button(
        Bitmap icon,//图标
        CharSequence title, CharSequence content,//标题，内容
        CharSequence cancelText, T_ButtonOnChangeListener cancelFun, //取消按钮文本，取消按钮点击事件
        CharSequence confirmText, T_ButtonOnChangeListener confirmFun) {//确定按钮文本，确定按钮点击事件
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_MESSAGE_GTA);//播放音效

        showCustomizeButtonNotification
        (
            //通知背景颜色
            0xFFEBF2EB,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getExquisiteNotice_Icon_Message()), 0xFF000000,
            //通知标题，标题颜色
            title, 0xFF000000,
            //通知内容，内容颜色
            content, 0xFF424242,
            //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色，描边颜色
            cancelText, 0xFF000000, 0, 0xFF9E9E9E,
            //关闭按钮点击事件
            cancelFun,
            //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色
            confirmText, 0xFF1976D2, 0, 0xFF9E9E9E,
            //确定按钮点击事件
            confirmFun
        );
    }




    //显示一个精美的成功通知
    public void showSuccessNotification_Exquisite(Bitmap icon, CharSequence title, CharSequence content, long notificationTime) {
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_SUCCESS);//播放音效

        showCustomizeNotification
        (
            //通知背景颜色
            0xFFADEDB1,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getExquisiteNotice_Icon_Success()), 0xFF1DA366,
            //通知标题，标题颜色
            title, 0xFF000000,
            //通知内容，内容颜色
            content, 0xFF424242,
            //通知时间(单位毫秒)
            notificationTime
        );
    }
    //显示一个精美的成功通知(带按钮)
    public void showSuccessNotification_Exquisite_Button(
        Bitmap icon,//图标
        CharSequence title, CharSequence content,//标题，内容
        CharSequence cancelText, T_ButtonOnChangeListener cancelFun, //取消按钮文本，取消按钮点击事件
        CharSequence confirmText, T_ButtonOnChangeListener confirmFun) {//确定按钮文本，确定按钮点击事件
        if (context == null) {
            return;
        }

        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_SUCCESS);//播放音效

        showCustomizeButtonNotification
        (
            //通知背景颜色
            0xFFADEDB1,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getExquisiteNotice_Icon_Success()), 0xFF1DA366,
            //通知标题，标题颜色
            title, 0xFF000000,
            //通知内容，内容颜色
            content, 0xFF424242,
            //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色，描边颜色
            cancelText, 0xFF2E7D32, 0, 0xFF2E7D32,
            //关闭按钮点击事件
            cancelFun,
            //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色
            confirmText, 0xFF2E7D32, 0, 0xFF2E7D32,
            //确定按钮点击事件
            confirmFun
        );
    }

    //显示一个精美的错误通知
    public void showMistakeNotification_Exquisite(Bitmap icon, CharSequence title, CharSequence content, long notificationTime) {
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_MISTAKE);//播放音效

        showCustomizeNotification
        (
            //通知背景颜色
            0xFFDF8E7D,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getExquisiteNotice_Icon_Mistake()), 0xFFB24D44,
            //通知标题，标题颜色
            title, 0xFF000000,
            //通知内容，内容颜色
            content, 0xFF424242,
            //通知时间(单位毫秒)
            notificationTime
        );
    }
    //显示一个精美的错误通知(带按钮)
    public void showMistakeNotification_Exquisite_Button(
        Bitmap icon,//图标
        CharSequence title, CharSequence content,//标题，内容
        CharSequence cancelText, T_ButtonOnChangeListener cancelFun, //取消按钮文本，取消按钮点击事件
        CharSequence confirmText, T_ButtonOnChangeListener confirmFun) {//确定按钮文本，确定按钮点击事件
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_MISTAKE);//播放音效

        showCustomizeButtonNotification
        (
            //通知背景颜色
            0xFFDF8E7D,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getExquisiteNotice_Icon_Mistake()), 0xFFB24D44,
            //通知标题，标题颜色
            title, 0xFF000000,
            //通知内容，内容颜色
            content, 0xFF424242,
            //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色，描边颜色
            cancelText, 0xFFB24D44, 0, 0xFFB24D44,
            //关闭按钮点击事件
            cancelFun,
            //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色
            confirmText, 0xFFB24D44, 0, 0xFFB24D44,
            //确定按钮点击事件
            confirmFun
        );
    }


    //显示一个精美的警告通知
    public void showAlertNotification_Exquisite(Bitmap icon, CharSequence title, CharSequence content, long notificationTime) {
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_ALERT);//播放音效

        showCustomizeNotification
        (
            //通知背景颜色
            0xFFF0D77B,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getExquisiteNotice_Icon_Alert()), 0xFFC3853F,
            //通知标题，标题颜色
            title, 0xFF000000,
            //通知内容，内容颜色
            content, 0xFF424242,
            //通知时间(单位毫秒)
            notificationTime
        );
    }
    //显示一个精美的警告通知(带按钮)
    public void showAlertNotification_Exquisite_Button(
        Bitmap icon,//图标
        CharSequence title, CharSequence content,//标题，内容
        CharSequence cancelText, T_ButtonOnChangeListener cancelFun, //取消按钮文本，取消按钮点击事件
        CharSequence confirmText, T_ButtonOnChangeListener confirmFun) {//确定按钮文本，确定按钮点击事件
        if (context == null) {
            return;
        }

        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_ALERT);//播放音效

        showCustomizeButtonNotification
        (
            //通知背景颜色
            0xFFF0D77B,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getExquisiteNotice_Icon_Alert()), 0xFFC3853F,
            //通知标题，标题颜色
            title, 0xFF000000,
            //通知内容，内容颜色
            content, 0xFF424242,
            //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色，描边颜色
            cancelText, 0xFFC3853F, 0, 0xFFC3853F,
            //关闭按钮点击事件
            cancelFun,
            //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色
            confirmText, 0xFFC3853F, 0, 0xFFC3853F,
            //确定按钮点击事件
            confirmFun
        );
    }



    //简约通知

    //显示一个简约的消息通知
    public void showMessageNotification_Simplicity(Bitmap icon, CharSequence title, CharSequence content, long notificationTime) {
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_MESSAGE_GTA);//播放音效
        showCustomizeNotification
        (
            //通知背景颜色
            0xFF303030,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Message()), 0,
            //通知标题，标题颜色
            title, 0xFFFFFFFF,
            //通知内容，内容颜色
            content, 0x60FFFFFF,
            //通知时间(单位毫秒)
            notificationTime
        );
    }
    //显示一个简约的消息通知(带按钮)
    public void showMessageNotification_Simplicity_Button(
        Bitmap icon,//图标
        CharSequence title, CharSequence content,//标题，内容
        CharSequence cancelText, T_ButtonOnChangeListener cancelFun, //取消按钮文本，取消按钮点击事件
        CharSequence confirmText, T_ButtonOnChangeListener confirmFun) {//确定按钮文本，确定按钮点击事件
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_MESSAGE_GTA);//播放音效

        showCustomizeButtonNotification
        (
            //通知背景颜色
            0xFF303030,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Message()), 0,
            //通知标题，标题颜色
            title, 0xFFFFFFFF,
            //通知内容，内容颜色
            content, 0x60FFFFFF,
            //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色，描边颜色
            cancelText, 0xFF303030, 0xFFFFFFFF, 0,
            //关闭按钮点击事件
            cancelFun,
            //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色
            confirmText, 0xFFFFFFFF, 0xFF11AA67, 0,
            //确定按钮点击事件
            confirmFun
        );
    }




    //显示一个简约的成功通知
    public void showSuccessNotification_Simplicity(Bitmap icon, CharSequence title, CharSequence content, long notificationTime) {
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_SUCCESS);//播放音效

        showCustomizeNotification
        (
            //通知背景颜色
            0xFF303030,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Success()), 0,
            //通知标题，标题颜色
            title, 0xFFFFFFFF,
            //通知内容，内容颜色
            content, 0x60FFFFFF,
            //通知时间(单位毫秒)
            notificationTime
        );
    }
    //显示一个简约的成功通知(带按钮)
    public void showSuccessNotification_Simplicity_Button(
        Bitmap icon,//图标
        CharSequence title, CharSequence content,//标题，内容
        CharSequence cancelText, T_ButtonOnChangeListener cancelFun, //取消按钮文本，取消按钮点击事件
        CharSequence confirmText, T_ButtonOnChangeListener confirmFun) {//确定按钮文本，确定按钮点击事件
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_SUCCESS);//播放音效

        showCustomizeButtonNotification
        (
            //通知背景颜色
            0xFF303030,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Success()), 0,
            //通知标题，标题颜色
            title, 0xFFFFFFFF,
            //通知内容，内容颜色
            content, 0x60FFFFFF,
            //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色，描边颜色
            cancelText, 0xFF303030, 0xFFFFFFFF, 0,
            //关闭按钮点击事件
            cancelFun,
            //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色
            confirmText, 0xFFFFFFFF, 0xFF11AA67, 0,
            //确定按钮点击事件
            confirmFun
        );
    }


    //显示一个简约的错误通知
    public void showMistakeNotification_Simplicity(Bitmap icon, CharSequence title, CharSequence content, long notificationTime) {
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_MISTAKE);//播放音效

        showCustomizeNotification
        (
            //通知背景颜色
            0xFF303030,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Mistake()), 0,
            //通知标题，标题颜色
            title, 0xFFFFFFFF,
            //通知内容，内容颜色
            content, 0x60FFFFFF,
            //通知时间(单位毫秒)
            notificationTime
        );
    }
    //显示一个简约的错误通知(带按钮)
    public void showMistakeNotification_Simplicity_Button(
        Bitmap icon,//图标
        CharSequence title, CharSequence content,//标题，内容
        CharSequence cancelText, T_ButtonOnChangeListener cancelFun, //取消按钮文本，取消按钮点击事件
        CharSequence confirmText, T_ButtonOnChangeListener confirmFun) {//确定按钮文本，确定按钮点击事件
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_MISTAKE);//播放音效

        showCustomizeButtonNotification
        (
            //通知背景颜色
            0xFF303030,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Mistake()), 0,
            //通知标题，标题颜色
            title, 0xFFFFFFFF,
            //通知内容，内容颜色
            content, 0x60FFFFFF,
            //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色，描边颜色
            cancelText, 0xFF303030, 0xFFFFFFFF, 0,
            //关闭按钮点击事件
            cancelFun,
            //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色
            confirmText, 0xFFFFFFFF, 0xFF11AA67, 0,
            //确定按钮点击事件
            confirmFun
        );
    }



    //显示一个简约的警告通知
    public void showAlertNotification_Simplicity(Bitmap icon, CharSequence title, CharSequence content, long notificationTime) {
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_ALERT);//播放音效

        showCustomizeNotification
        (
            //通知背景颜色
            0xFF303030,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Alert()), 0,
            //通知标题，标题颜色
            title, 0xFFFFFFFF,
            //通知内容，内容颜色
            content, 0x60FFFFFF,
            //通知时间(单位毫秒)
            notificationTime
        );
    }
    //显示一个简约的警告通知(带按钮)
    public void showAlertNotification_Simplicity_Button(
        Bitmap icon,//图标
        CharSequence title, CharSequence content,//标题，内容
        CharSequence cancelText, T_ButtonOnChangeListener cancelFun, //取消按钮文本，取消按钮点击事件
        CharSequence confirmText, T_ButtonOnChangeListener confirmFun) {//确定按钮文本，确定按钮点击事件
        if (context == null) {
            return;
        }
        AlGuiSoundEffect.getAudio(context).playSoundEffect(AlGuiSoundEffect.INFORM_ALERT);//播放音效

        showCustomizeButtonNotification
        (
            //通知背景颜色
            0xFF303030,
            //通知图标，图标颜色(0代表不设置颜色)
            icon != null ?icon: ImageTool.getBase64Image(AlGuiData.getSimplicityNotice_Icon_Alert()), 0,
            //通知标题，标题颜色
            title, 0xFFFFFFFF,
            //通知内容，内容颜色
            content, 0x60FFFFFF,
            //关闭按钮文本，关闭按钮文本颜色，关闭按钮背景颜色，描边颜色
            cancelText, 0xFF303030, 0xFFFFFFFF, 0,
            //关闭按钮点击事件
            cancelFun,
            //确认按钮文本，确认按钮文本颜色，确认按钮背景颜色，描边颜色
            confirmText, 0xFFFFFFFF, 0xFF11AA67, 0,
            //确定按钮点击事件
            confirmFun
        );
    }


}
