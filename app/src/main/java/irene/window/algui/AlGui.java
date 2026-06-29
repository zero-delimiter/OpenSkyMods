package irene.window.algui;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import irene.window.algui.CustomizeView.MarqueeTextView;
import irene.window.algui.CustomizeView.vFrameLayout;
import irene.window.algui.CustomizeView.vLinearLayout;
import irene.window.algui.Tools.AppPermissionTool;
import irene.window.algui.Tools.AppTool;
import irene.window.algui.Tools.ViewTool;
import java.io.IOException;
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.graphics.drawable.AnimatedImageDrawable;
import irene.window.algui.Tools.ImageTool;
import android.app.AlertDialog;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import irene.window.algui.CustomizeView.GradientTextView;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/01 14:25
 * @Describe 
 */
public class AlGui {
    public static final String TAG = "AlGui";
    
    //特殊
    private AlGuiPrefabricatedMenu_LB prefabricatedMenu;//拥有各种已写好的折叠菜单 例如系统属性折叠菜单...
 
    private static AlGui algui;// 单例模式
    private Context aContext; //上下文


    private int[] viewWBJ = new int[]{8,8,8,8};//滚动条内的所有视图的外边距
    // 悬浮球窗口
    private boolean isBallView = false;//窗口是否添加了布局的标识
    private WindowManager ballManager;//悬浮球窗口
    private WindowManager.LayoutParams ballParams;//窗口参数
    private vLinearLayout ballLayout;//悬浮球布局

    // 悬浮菜单窗口
    private boolean isMenuView=false;//窗口是否添加了布局的标识
    private WindowManager menuManager;//悬浮菜单窗口
    private WindowManager.LayoutParams menuParams;//窗口参数

    private vFrameLayout menuLayout;//菜单根布局
    private vLinearLayout mainLayout;//主要布局

    private vLinearLayout lineTouchLayout;//触摸移动的布局
    private vLinearLayout touchMoveLine;//触摸移动的线条

    private vLinearLayout titleLiveStreamRootLayout;//标题和直播模式的根布局
    private vLinearLayout titleLayout;//标题布局
    private TextView title;//主标题
    private TextView edition;//子标题
    private vLinearLayout liveStreamLayout;//直播模式布局
    private ImageView liveStreamIcon;//直播模式图标

    private vLinearLayout subTitleLayout;//说明布局
    private TextView subTitle;//说明

    private ScrollView scroll;//滚动列表
    private vLinearLayout scrollLayout;//滚动列表布局 - 功能列表

    private RelativeLayout relativeLayout;//下边栏相对布局
    private Button leftButton;//左下角按钮
    private Button rightButton;//右下角按钮

    private View zoomTriangleView;//悬浮窗右下角的三角触摸视图 用来缩放悬浮窗
    private Path trianglePath;//三角形路径
    private Paint trianglePaint;//三角形画笔

    //👽@@@@@@@@@@特殊@@@@@@@@@@👽
    //获取上下文
    public Context getContext() {
        return aContext;
    }
    //获取特殊折叠菜单 拥有各种已写好的折叠菜单 例如系统属性折叠菜单...
    public AlGuiPrefabricatedMenu_LB PrefabricatedMenu() {
        if (prefabricatedMenu == null) {
            prefabricatedMenu = new AlGuiPrefabricatedMenu_LB(aContext,algui);//初始化特殊折叠菜单
        }
        return prefabricatedMenu;
    }
    
   

    //😛@@@@@@@@@@悬浮球相关@@@@@@@@@@😛
    //获取悬浮球窗口
    public WindowManager getBallWindow() {
        return ballManager;
    }
    //获取悬浮球窗口参数
    public WindowManager.LayoutParams getBallWindowParams() {
        return ballParams;
    }
    //获取悬浮窗布局
    public vLinearLayout getBallLayout() {
        return ballLayout;
    }






    //😛@@@@@@@@@@悬浮菜单相关@@@@@@@@@@😛
    //获取悬浮菜单窗口
    public WindowManager getMenuWindow() {
        return menuManager;
    }
    //获取悬浮菜单窗口参数
    public WindowManager.LayoutParams getMenuWindowParams() {
        return menuParams;
    }
    //获取悬浮菜单根布局
    public vFrameLayout getMenuRootLayout() {
        return menuLayout;
    }
    //获取悬浮菜单主布局
    public vLinearLayout getMenuMainLayout() {
        return mainLayout;
    }



    //获取悬浮菜单顶部的线条布局
    public vLinearLayout getMenuTopLayout() {
        return lineTouchLayout;
    }
    //获取悬浮菜单顶部的线条
    public vLinearLayout getMenuTopLine() {
        return touchMoveLine;
    }



    //获取悬浮菜单标题和直播模式的根布局
    public vLinearLayout getMenuTitleLiveStreamRootLayout() {
        return titleLiveStreamRootLayout;
    }
    //获取悬浮菜单主标题布局
    public vLinearLayout getMenuTitleLayout() {
        return titleLayout;
    }
    //获取悬浮菜单主标题
    public TextView getMenuMainTitle() {
        return title;
    }
    //获取悬浮菜单子标题
    public TextView getMenuSubTitle() {
        return edition;
    }
    //获取直播模式布局
    public vLinearLayout getMenuLiveStreamLayout() {
        return liveStreamLayout;
    }
    //获取直播模式图标
    public ImageView getMenuLiveStreamIcon() {
        return liveStreamIcon;
    }



    //获取悬浮菜单说明布局
    public vLinearLayout getMenuExplanationLayout() {
        return subTitleLayout;
    }
    //获取悬浮菜单说明
    public TextView getMenuExplanation() {
        return subTitle;
    }



    //获取悬浮菜单滚动列表
    public ScrollView getMenuScrollingList() {
        return scroll;
    }
    //获取悬浮菜单滚动列表布局
    public vLinearLayout getMenuScrollingListLayout() {
        return scrollLayout;
    }



    //获取悬浮菜单底部布局
    public RelativeLayout getMenuBottomLayout() {
        return relativeLayout;
    }
    //获取悬浮菜单左下角按钮
    public Button getMenuBottomLeftButton() {
        return leftButton;
    }
    //获取悬浮菜单右下角按钮
    public Button getMenuBottomRightButton() {
        return rightButton;
    }



    //获取悬浮菜单右下角三角视图
    public View getMenuBottomRightTriangleView() {
        return zoomTriangleView;
    }
    //获取悬浮菜单右下角三角形路径
    public Path getMenuBottomRightTrianglePath() {
        return trianglePath;
    }
    //获取悬浮菜单右下角三角形画笔
    public Paint getMenuBottomRightTrianglePaint() {
        return trianglePaint;
    }




    //单例模式 - 禁止多开窗口
    public static AlGui GUI(Context context) {
        if (algui == null) {
            algui = new AlGui(context);
        } 
        return algui;
    }

    //构造函数
    private AlGui(Context context) {
        if(context==null){
          //  return;
            throw new IllegalArgumentException("实例化时传入的Context上下文对象为空！null");
            
        }
        aContext = context;
        //初始化对话框
        //dialog=new AlGuiDialogBox();
        //初始化气泡通知
        //bubbleNotification=new AlGuiBubbleNotification(aContext);
       
      
        
        initBallWindow();//初始化悬浮球窗口配置
        initBall();//初始化悬浮球

        initMenuWindow();//初始化悬浮菜单窗口配置
        initMenu();//初始化悬浮菜单
        updateMenuAppearance();//加载悬浮菜单外观

        //ALGUI 初始默认配置
        //设置悬浮窗滚动菜单内所有视图的外边距 参数：分别是 (左 上 右 下) 的外边距
        setAllViewMargins(8, 8, 8, 8);

        //设置悬浮球图片 参数：Assets文件夹图片名(null代表不使用图片) 悬浮球宽高
        setBallImage(null, 50, 50);
     
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (AppPermissionTool.isAndroidManifestPermissionExist(aContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
             //AppPermissionTool.floatingWindowPermission(aContext);//申请悬浮窗权限
            //AppTool.showNotificationBar(aContext,"ALGUI悬浮窗口 已加载","请勿尝试逆向此程序，系统损坏我们不负责");
                
                if(!AppPermissionTool.checkOverlayPermission(aContext)){
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        //安卓8.0以下才执行
                        Toast.makeText(aContext, "授予悬浮窗权限后重进才能显示悬浮窗！", Toast.LENGTH_LONG).show();
                        
                    }else{
                        //安卓8.0以上才执行
                        Toast.makeText(aContext, "请授予悬浮窗权限，才能显示窗口", Toast.LENGTH_LONG).show();
                        
                    }
                }
       
        }else{
            Toast.makeText(aContext, "开发者未在AndroidManifest.xml文件中声明悬浮窗权限因此无法显示悬浮窗，只有声明了此权限才能授予悬浮窗权限！你也可以手动去安装包此文件中声明android.permission.SYSTEM_ALERT_WINDOW", Toast.LENGTH_LONG).show();
        }
        
       

    }




    //设置悬浮球图片 (参数1 传null代表使用默认无图片MOD悬浮球)
    public Drawable setBallImage(String iconName, float iconWidth, float iconHeight) {
        if (ballLayout.getChildCount() > 0) {
            //如果悬浮球根布局中已经包含了子视图就清空视图
            ballLayout.removeAllViews();
        }

        Drawable drawable=null;
        if (iconName != null) {
            try {
                drawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource(aContext.getAssets(), iconName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        if (drawable != null) {
            //图片存在则设置图片作为悬浮球
            ImageView controlView = new ImageView(aContext);
            controlView.setBackground(drawable);
            ballLayout.addView(controlView, ViewTool.convertDpToPx(aContext, iconWidth), ViewTool.convertDpToPx(aContext, iconHeight));
            //如果是gif动图则开始启动动画
            if (drawable instanceof AnimatedImageDrawable) {
                AnimatedImageDrawable ad =  ((AnimatedImageDrawable) drawable);
                ad.start();
                return ad;
            }
            return drawable;
        } else {
            //图片为空 则使用默认无图片MOD悬浮球
            int strokeSize=ViewTool.convertDpToPx(aContext,1.5f);
            GradientDrawable mainBackground = new GradientDrawable();
            mainBackground.setShape(GradientDrawable.OVAL);
            mainBackground.setColor(0xFF303030);//背景颜色
            mainBackground.setStroke(strokeSize, 0xFF424242);//边框厚度与描边颜色

            LinearLayout titleLayout = new LinearLayout(aContext);
            titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                            ViewTool.convertDpToPx(aContext, iconWidth), 
                                            ViewTool.convertDpToPx(aContext, iconWidth)));
            titleLayout.setGravity(Gravity.CENTER);
            titleLayout.setBackground(mainBackground);
            titleLayout.setPadding(strokeSize, strokeSize, strokeSize, strokeSize);

          
            TextView text=new TextView(aContext);
            //设置文本视图阴影 参数：文本视图，阴影半径，阴影水平偏移量，阴影垂直偏移量，阴影颜色
           // ViewTool.setTextViewShadow(text,10,0,0,0xFF000000);
           // text.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
            text.setGravity(Gravity.CENTER);
            text.setText("MOD");
            text.setTextColor(0xFFFFFFFF);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 15f));
            //text.setColors(new int[]{0xFFf902ff,0xff00dbde});
           //ptext.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 15f));
           /*
             第一个参数是最小的文字大小（以 sp 为单位）。
             第二个参数是最大的文字大小（以 sp 为单位）。
             第三个参数是文字大小的步长（以 sp 为单位）。
             最后一个参数指定了单位，这里使用的是 TypedValue.COMPLEX_UNIT_SP
           */
           //安卓8.0以下有影响
           //text.setAutoSizeTextTypeUniformWithConfiguration(2, ViewTool.convertDpToPx(aContext, iconWidth), 1, TypedValue.COMPLEX_UNIT_SP);
            titleLayout.addView(text);
            ballLayout.addView(titleLayout);
            return null;
        }
    }

    //设置菜单宽高 参数：宽 高
    /*public void setMenuSize(int w, int h) {
        //只需设置滚动列表的宽高即可 因为悬浮窗布局的宽高是自适应子布局
        if(scroll!=null){
            if(scroll.getLayoutParams()==null){
                scroll.setLayoutParams(new ScrollView.LayoutParams(ViewTool.convertDpToPx(aContext, w),
                                                                   ViewTool.convertDpToPx(aContext, h)));
            }
            
            scroll.getLayoutParams().width = w;
            scroll.getLayoutParams().height = h;          
        }
        
        updateMenu();
    }*/

    //设置所有视图外边距
    public void setAllViewMargins(int left, int top, int right, int bottom) {
        viewWBJ = new int[]{left,top,right,bottom};
    }



    //初始化菜单外观 (不要更改其中数据！这是自动化的)
    public void updateMenuAppearance() {
        //根布局
        menuLayout.setFilletRadiu(ViewTool.convertDpToPx(aContext, AlGuiData.getMenuAttributeSP(aContext).getFloat((String)AlGuiData.getMenuAttributeData().get("根布局圆角半径键名"), (float)AlGuiData.getMenuAttributeData().get("根布局圆角半径默认数据"))));//圆角半径
        menuLayout.setBorder(ViewTool.convertDpToPx(aContext, AlGuiData.getMenuAttributeSP(aContext).getFloat((String)AlGuiData.getMenuAttributeData().get("根布局描边宽度键名"), (float)AlGuiData.getMenuAttributeData().get("根布局描边宽度默认数据"))), AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("根布局描边颜色键名"), (int)AlGuiData.getMenuColorData().get("根布局描边颜色默认数据")));//描边厚度以及描边颜色
        menuLayout.setBackColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("根布局背景颜色键名"), (int)AlGuiData.getMenuColorData().get("根布局背景颜色默认数据")));//背景颜色
        menuLayout.setAlpha(AlGuiData.getMenuAttributeSP(aContext).getFloat((String)AlGuiData.getMenuAttributeData().get("菜单透明度键名"), (float)AlGuiData.getMenuAttributeData().get("菜单透明度默认数据")));//设置菜单透明度 透明度介于0和1之间

      
        //顶部状态线条
        touchMoveLine.setBackColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单顶部线条颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单顶部线条颜色默认数据")));//线条颜色
        touchMoveLine.setFilletRadiu(ViewTool.convertDpToPx(aContext, AlGuiData.getMenuAttributeSP(aContext).getFloat((String)AlGuiData.getMenuAttributeData().get("菜单顶部线条圆角半径键名"),(float)AlGuiData.getMenuAttributeData().get("菜单顶部线条圆角半径默认数据"))));//圆角半径

        //主标题
        title.setTextColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单主标题文本颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单主标题文本颜色默认数据")));//文本颜色
        //子标题
        edition.setTextColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单副标题文本颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单副标题文本颜色默认数据")));//文本颜色

        //直播模式图标颜色
        liveStreamIcon.setImageTintList(ColorStateList.valueOf(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单直播模式图标颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单直播模式图标颜色默认数据"))));
        
        
        //说明
        subTitleLayout.setBackgroundColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单说明背景颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单说明背景颜色默认数据")));
        subTitle.setTextColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单说明文本颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单说明文本颜色默认数据")));//文本颜色

        //滚动列表
        scroll.setBackgroundColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单滚动列表背景颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单滚动列表背景颜色默认数据")));//背景颜色
        scroll.getLayoutParams().width =  (int)AlGuiData.getMenuAttributeSP(aContext).getFloat((String)AlGuiData.getMenuAttributeData().get("菜单滚动列表宽度键名"),(float)AlGuiData.getMenuAttributeData().get("菜单滚动列表宽度默认数据"));//宽度
        scroll.getLayoutParams().height = (int)AlGuiData.getMenuAttributeSP(aContext).getFloat((String)AlGuiData.getMenuAttributeData().get("菜单滚动列表高度键名"),(float)AlGuiData.getMenuAttributeData().get("菜单滚动列表高度默认数据"));//高度
      
        //底部左下角按钮
        leftButton.setTextColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单左下角按钮文本颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单左下角按钮文本颜色默认数据")));//文本颜色
        //底部右下角按钮
        rightButton.setTextColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单右下角按钮文本颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单右下角按钮文本颜色默认数据")));//文本颜色

        //菜单右下三角形视图颜色
        trianglePaint.setColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单右下角三角形颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单右下角三角形颜色默认数据")));
        zoomTriangleView.invalidate();//重绘三角形
        
        //窗口自适应布局
        menuParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        menuParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        //更新窗口
        updateMenu();
    }


    //初始化悬浮菜单的方法 
    private void initMenu() {

        //根布局 (作为窗口布局 所以他的布局参数就是悬浮窗的布局参数 要更改此布局的参数就去改悬浮窗布局参数)
        menuLayout = new vFrameLayout(aContext);
        
        menuLayout.setClipChildren(true);
        menuLayout. setLayerType(View.LAYER_TYPE_HARDWARE, null) ;//开启硬件加速，因为之后调整悬浮窗大小功能会进行复杂计算
        
     
        //主要布局        
        mainLayout = new vLinearLayout(aContext);
        mainLayout.setBackgroundColor(Color.TRANSPARENT);//透明
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                       LinearLayout.LayoutParams.MATCH_PARENT, 
                                       LinearLayout.LayoutParams.WRAP_CONTENT));
        mainLayout.setOrientation(LinearLayout.VERTICAL);





        //窗口顶部线条的布局 
        lineTouchLayout = new vLinearLayout(aContext);
        lineTouchLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT, 
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
        lineTouchLayout.setOrientation(LinearLayout.VERTICAL);
        lineTouchLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        //顶部线条线条 (用来显示触摸移动状态 高亮则代表正在移动悬浮窗)
        
        touchMoveLine = new vLinearLayout(aContext);
        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
            ViewTool.convertDpToPx(aContext, 50), 
            ViewTool.convertDpToPx(aContext, 3));
        childLayoutParams.setMargins(0, 16, 0, 16);//外边距
        touchMoveLine.setLayoutParams(childLayoutParams);


        //标题和直播模式根布局
        titleLiveStreamRootLayout = new vLinearLayout(aContext);
        titleLiveStreamRootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, 
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
        titleLiveStreamRootLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLiveStreamRootLayout.setGravity(Gravity.CENTER_VERTICAL);
        //titleRootLayout.setPadding(20, 20, 20, 20);
        
        //标题布局
        titleLayout = new vLinearLayout(aContext);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, 
                                        LinearLayout.LayoutParams.WRAP_CONTENT,1f));
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setPadding(20, 20, 20, 20);
        titleLayout.setGravity(Gravity.LEFT);
        //主标题
        title = new TextView(aContext);
        //设置文本视图阴影 参数：文本视图，阴影半径，阴影水平偏移量，阴影垂直偏移量，阴影颜色
        //ViewTool.setTextViewShadow(title,10,0,0,0xFF000000);
       // title.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        title.setText("ALGUI");
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 15f));
        //子标题
        edition = new TextView(aContext);
        //edition.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        edition.setText("版本：2.0");
        edition.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 8f));

        
        //直播模式布局
        liveStreamLayout = new vLinearLayout(aContext);
        liveStreamLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, 
                                        LinearLayout.LayoutParams.WRAP_CONTENT,1f));
        liveStreamLayout.setOrientation(LinearLayout.VERTICAL);
        liveStreamLayout.setPadding(20, 20, 50, 40);
        liveStreamLayout.setGravity(Gravity.RIGHT);
        //直播模式图标
        liveStreamIcon = new ImageView(aContext);
        //videoIcon.setPadding(10, 10, 10, 10);
        liveStreamIcon.setLayoutParams(new ViewGroup.LayoutParams(
                                      ViewTool.convertDpToPx(aContext,30),
                                      ViewTool.convertDpToPx(aContext,30)
                                  ));
                                 
         //设置初始图标为直播开始图标
        liveStreamIcon.setImageBitmap(ImageTool.getBase64Image(AlGuiData.getVideo_Icon_LiveEnd()));
        



        //说明布局 
        subTitleLayout = new vLinearLayout(aContext);
        subTitleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                           LinearLayout.LayoutParams.MATCH_PARENT, 
                                           LinearLayout.LayoutParams.WRAP_CONTENT));
        subTitleLayout.setOrientation(LinearLayout.HORIZONTAL);
        subTitleLayout.setPadding(20, 5, 20, 5);
        //说明
        subTitle = new TextView(aContext);
        subTitle.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        subTitle.setText("作者：艾琳⼁仅供用于学习交流请勿用于违法用途⼁如果您有任何疑问请进游戏逆向交流群：730967224进行交流讨论");
        subTitle.setGravity(Gravity.LEFT);
        subTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 8f));
        //使说明文本水平超出布局时 进行滚动字幕效果
        subTitle.setSelected(true);//设置获取焦点时自动滚动
        subTitle.setHorizontallyScrolling(true);//水平滚动
        subTitle.setSingleLine(true);//只显示一行
        subTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);//启用省略号，并且在文本过长时进行滚动显示
        subTitle.setMarqueeRepeatLimit(-1);//滚动次数为无限次
        subTitle.setFocusable(true);//设置可获取焦点
        subTitle.setFocusableInTouchMode(true);//设置触摸模式下可获取焦点
        subTitle.requestFocus();//获取焦点
        subTitle.requestFocusFromTouch();//从触摸状态下获取焦点


        //滚动列表
        scroll = new ScrollView(aContext);
        scroll.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT,
                                                           ScrollView.LayoutParams.WRAP_CONTENT));
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setVerticalScrollBarEnabled(false);
        
        scroll. setLayerType(View.LAYER_TYPE_HARDWARE, null) ;//开启硬件加速，因为之后调整悬浮窗大小功能会进行复杂计算
        
       //滚动布局 - 功能列表
        scrollLayout = new vLinearLayout(aContext);
        scrollLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                         LinearLayout.LayoutParams.MATCH_PARENT, 
                                         LinearLayout.LayoutParams.MATCH_PARENT));
        scrollLayout.setOrientation(LinearLayout.VERTICAL);
        scrollLayout.setPadding(20, 20, 20, 20);//内边距
        scrollLayout.setVerticalGravity(16);
        // scrollLayout.setGravity(Gravity.CENTER);//子布局在我的中心位置
        

        

        //下边栏相对布局
        relativeLayout = new RelativeLayout(aContext);//创建相对布局
        relativeLayout.setPadding(10, 3, 10, 3);//设置内边距
        relativeLayout.setVerticalGravity(Gravity.CENTER);//设置垂直方向居中对齐
     
        
        //隐藏悬浮窗口(单击隐藏) 或 退出悬浮菜单的按钮(长按退出)
        RelativeLayout.LayoutParams lParamsHideBtn = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lParamsHideBtn.addRule(9);
        leftButton = new Button(aContext);
        //leftButton.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        leftButton.setLayoutParams(lParamsHideBtn);
        leftButton.setBackgroundColor(Color.TRANSPARENT);
        leftButton.setText("隐藏/退出");
        leftButton.setOnClickListener(new View.OnClickListener() {
            
            AlertDialog dialog;
                //隐藏悬浮球的方法
                public void hideBall(){
                    
                    clearMenu();//隐藏悬浮菜单
                    showBall();//显示悬浮球
                    ballLayout.setAlpha(1);//设置不透明
                    ballLayout.setBackgroundColor(0xFFF44336);

                    //倒计时5秒
                    CountDownTimer timer = new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            //倒计时期间，可以更新悬浮球中的提示信息

                        }

                        @Override
                        public void onFinish() {
                            // 倒计时结束后，隐藏悬浮球
                            ballLayout.setBackgroundColor(0);//清除背景色
                            ballLayout.setAlpha(0);//透明悬浮球
                            updateBall();//更新悬浮球
                            AlGuiBubbleNotification.Inform(aContext).showMessageNotification_Exquisite(null, "悬浮球已隐藏", "再次拖动悬浮球以恢复", 5000);
                            
                        }
                    };
                    timer.start();
                }
                
                
                
                public void onClick(View view) {
                    //单击时
                   
                    //检查这个弹窗不再提示的本地的标识 如果标识为true代表不再提示那么直接进第一个分支 否则进第二个分支弹窗提示形式
                    if(AlGuiData.getDiaLogFlagSP(aContext).getBoolean((String)AlGuiData.getDiaLogFlagData().get("悬浮窗隐藏弹窗不再提示键名"),(boolean)AlGuiData.getDiaLogFlagData().get("悬浮窗隐藏弹窗不再提示默认数据"))){
                        hideBall();//隐藏悬浮球
                    }else{
                        
                        dialog =AlGuiDialogBox.showDiaLog
                        (aContext, 0xCEFAFAFA, 50f,
                         AlGui.GUI(aContext).addLinearLayout
                         (//这一对括号中是线性布局的空间
                             //线性布局属性配置🛠️
                             Gravity.CENTER,//子视图对齐方式
                             LinearLayout.VERTICAL,//线性布局方向
                             LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                             LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
                             AlGui.GUI(aContext).addTextView("隐藏悬浮球", 18, 0xFF212121, Typeface.create(Typeface.DEFAULT, Typeface.BOLD)),
                             //AlGui.GUI(context).addLine(1f, 0xFF424242, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
                             AlGui.GUI(aContext).addTextView("你确定要隐藏悬浮球吗？\n\n如果确定隐藏那么我们会高亮悬浮球五秒\n\n请在这段时间记住它的位置以便后面恢复\n\n再次拖动悬浮球将恢复并显示\n", 12f, 0xFF424242, null)
                         ),
                         AlGui.GUI(aContext).addLinearLayout
                         (//这一对括号中是线性布局的空间
                             //线性布局属性配置🛠️
                             Gravity.CENTER,//子视图对齐方式
                             LinearLayout.HORIZONTAL,//线性布局方向
                             LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                             LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
                         //添加一个普通按钮
                         AlGui.GUI(aContext).addButton
                         (
                             //普通按钮属性配置🛠️
                                 "确定", 15, 0xCEFAFAFA, null,//按钮文本，文本大小，文本颜色，文本字体
                             50,//按钮圆角半径
                             0xFF3F51B5,//按钮背景颜色
                             0, 0xff000000,//按钮描边大小，描边颜色
                             //按钮宽度，按钮高度
                             LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                             //按钮事件监听器
                             new  AlGui.T_ButtonOnChangeListener(){
                                 @Override//事件中你可以获取到：button=按钮对象，back=按钮背景对象，buttonText=按钮文本对象，isChecked=按钮开关状态
                                 public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                     //按钮点击时执行的内容
                                     hideBall();//隐藏悬浮球
                                     dialog.dismiss();
                                     
                                 }
                             }
                         )
                         ,
                         //添加一个普通按钮
                         AlGui.GUI(aContext).addButton
                         (
                             //普通按钮属性配置🛠️
                                 "不再提示", 15, 0xCEFAFAFA, null,//按钮文本，文本大小，文本颜色，文本字体
                             50,//按钮圆角半径
                             0xFF3F51B5,//按钮背景颜色
                             0, 0xff000000,//按钮描边大小，描边颜色
                             //按钮宽度，按钮高度
                             LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                             //按钮事件监听器
                             new  AlGui.T_ButtonOnChangeListener(){
                                 @Override//事件中你可以获取到：button=按钮对象，back=按钮背景对象，buttonText=按钮文本对象，isChecked=按钮开关状态
                                 public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                     //按钮点击时执行的内容
                                     //设置本地标识 下次将不再提示此弹窗
                                     AlGuiData.getDiaLogFlagSPED(aContext).putBoolean((String)AlGuiData.getDiaLogFlagData().get("悬浮窗隐藏弹窗不再提示键名"), true);
                                     //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                     //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                     AlGuiData.getDiaLogFlagSPED(aContext).apply();
                                     hideBall();//隐藏悬浮球
                                     dialog.dismiss();

                                 }
                             }
                         ),
                        //添加一个普通按钮
                        AlGui.GUI(aContext).addButton
                        (
                            //普通按钮属性配置🛠️
                                 "取消", 15, 0xCEFAFAFA, null,//按钮文本，文本大小，文本颜色，文本字体
                            50,//按钮圆角半径
                            0xFF3F51B5,//按钮背景颜色
                            0, 0xff000000,//按钮描边大小，描边颜色
                            //按钮宽度，按钮高度
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                            //按钮事件监听器
                            new  AlGui.T_ButtonOnChangeListener(){
                                @Override//事件中你可以获取到：button=按钮对象，back=按钮背景对象，buttonText=按钮文本对象，isChecked=按钮开关状态
                                public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                    //按钮点击时执行的内容
                                    dialog.dismiss();

                                }
                            }
                        )
                        )
                         );
                     
                    }
                }
            });
            
            
        leftButton.setOnLongClickListener(new View.OnLongClickListener() {
            AlertDialog dialog;
                 //退出菜单的方法
                public void exitMenu(){
                    AlGuiBubbleNotification.Inform(aContext).clearW();//清除右下角通知窗口
                    clearMenu();//清除悬浮菜单
                    clearBall();//清除悬浮球
                }
                public boolean onLongClick(View view) {
                    //检查这个弹窗不再提示的本地的标识 如果标识为true代表不再提示那么直接进第一个分支 否则进第二个分支弹窗提示形式
                    if(AlGuiData.getDiaLogFlagSP(aContext).getBoolean((String)AlGuiData.getDiaLogFlagData().get("悬浮窗退出弹窗不再提示键名"),(boolean)AlGuiData.getDiaLogFlagData().get("悬浮窗退出弹窗不再提示默认数据"))){
                        exitMenu();//退出菜单
                    }else{
                        dialog =AlGuiDialogBox.showDiaLog
                        (aContext, 0xCEFAFAFA, 50f,
                         AlGui.GUI(aContext).addLinearLayout
                         (//这一对括号中是线性布局的空间
                             //线性布局属性配置🛠️
                             Gravity.CENTER,//子视图对齐方式
                             LinearLayout.VERTICAL,//线性布局方向
                             LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                             LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
                             AlGui.GUI(aContext).addTextView("退出悬浮窗", 18, 0xFF212121, Typeface.create(Typeface.DEFAULT, Typeface.BOLD)),
                             //AlGui.GUI(context).addLine(1f, 0xFF424242, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
                             AlGui.GUI(aContext).addTextView("你确定要退出悬浮窗吗？此操作不可恢复", 15f, 0xFF424242, null)
                         ),
                         AlGui.GUI(aContext).addLinearLayout
                         (//这一对括号中是线性布局的空间
                             //线性布局属性配置🛠️
                             Gravity.CENTER,//子视图对齐方式
                             LinearLayout.HORIZONTAL,//线性布局方向
                             LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                             LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
                             //添加一个普通按钮
                             AlGui.GUI(aContext).addButton
                             (
                                 //普通按钮属性配置🛠️
                                 "确定", 15, 0xCEFAFAFA, null,//按钮文本，文本大小，文本颜色，文本字体
                                 50,//按钮圆角半径
                                 0xFF3F51B5,//按钮背景颜色
                                 0, 0xff000000,//按钮描边大小，描边颜色
                                 //按钮宽度，按钮高度
                                 LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                                 //按钮事件监听器
                                 new  AlGui.T_ButtonOnChangeListener(){
                                     @Override//事件中你可以获取到：button=按钮对象，back=按钮背景对象，buttonText=按钮文本对象，isChecked=按钮开关状态
                                     public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                         //按钮点击时执行的内容
                                         exitMenu();//退出菜单
                                         dialog.dismiss();

                                     }
                                 }
                             )
                             ,
                             //添加一个普通按钮
                             AlGui.GUI(aContext).addButton
                             (
                                 //普通按钮属性配置🛠️
                                 "不再提示", 15, 0xCEFAFAFA, null,//按钮文本，文本大小，文本颜色，文本字体
                                 50,//按钮圆角半径
                                 0xFF3F51B5,//按钮背景颜色
                                 0, 0xff000000,//按钮描边大小，描边颜色
                                 //按钮宽度，按钮高度
                                 LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                                 //按钮事件监听器
                                 new  AlGui.T_ButtonOnChangeListener(){
                                     @Override//事件中你可以获取到：button=按钮对象，back=按钮背景对象，buttonText=按钮文本对象，isChecked=按钮开关状态
                                     public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                         //按钮点击时执行的内容
                                         //设置本地标识 下次将不再提示此弹窗
                                         AlGuiData.getDiaLogFlagSPED(aContext).putBoolean((String)AlGuiData.getDiaLogFlagData().get("悬浮窗退出弹窗不再提示键名"), true);
                                         //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                         //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                         AlGuiData.getDiaLogFlagSPED(aContext).apply();
                                         exitMenu();//退出菜单
                                         dialog.dismiss();

                                     }
                                 }
                             ),
                             //添加一个普通按钮
                             AlGui.GUI(aContext).addButton
                             (
                                 //普通按钮属性配置🛠️
                                 "取消", 15, 0xCEFAFAFA, null,//按钮文本，文本大小，文本颜色，文本字体
                                 50,//按钮圆角半径
                                 0xFF3F51B5,//按钮背景颜色
                                 0, 0xff000000,//按钮描边大小，描边颜色
                                 //按钮宽度，按钮高度
                                 LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                                 //按钮事件监听器
                                 new  AlGui.T_ButtonOnChangeListener(){
                                     @Override//事件中你可以获取到：button=按钮对象，back=按钮背景对象，buttonText=按钮文本对象，isChecked=按钮开关状态
                                     public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                         //按钮点击时执行的内容
                                         dialog.dismiss();

                                     }
                                 }
                             )
                         )
                         );
                       
                     }
                    //长按时
                    return false;
                }
            });
        //最小化 按钮
        RelativeLayout.LayoutParams lParamsCloseBtn = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams. WRAP_CONTENT);
        lParamsCloseBtn.addRule(11);
        rightButton = new Button(aContext);
        //rightButton.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        rightButton.setLayoutParams(lParamsCloseBtn);
        rightButton.setBackgroundColor(Color.TRANSPARENT);
        rightButton.setText("最小化");
        rightButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    clearMenu();//隐藏悬浮菜单
                    showBall();//显示悬浮球

                }
            });





        // 创建触摸视图(用于拉拽悬浮窗右下角动态调整悬浮窗大小)
        //三角形画笔
        trianglePaint = new Paint();
        trianglePaint.setStyle(Paint.Style.FILL);
        zoomTriangleView = new View(aContext) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                // 绘制三角形
                trianglePath = new Path();
                trianglePath.moveTo(0, getHeight());
                trianglePath.lineTo(getWidth(), getHeight());
                trianglePath.lineTo(getWidth(), getHeight() - ViewTool.convertDpToPx(aContext, 19));
                trianglePath.close();

                canvas.drawPath(trianglePath, trianglePaint);

            }
        };
        zoomTriangleView.setBackgroundColor(Color.TRANSPARENT);
        zoomTriangleView.setOnTouchListener(new ResizeViewTouchListener());


        //悬浮窗布局框架结构

        //😋####### 主要布局 #######😋
        //顶边布局 - 用来触摸移动的布局
        lineTouchLayout.addView(touchMoveLine);//顶边触摸布局添加线条
        mainLayout.addView(lineTouchLayout);//主要布局添加顶边触摸布局

        //标题和直播模式布局
        titleLayout.addView(title);//标题布局添加主标题
        titleLayout.addView(edition);//标题布局添加副标题
        titleLiveStreamRootLayout.addView(titleLayout);//标题直播根布局添加标题布局
        liveStreamLayout.addView(liveStreamIcon);//直播模式布局添加直播模式图标
        titleLiveStreamRootLayout.addView(liveStreamLayout);//标题直播根布局添加直播模式布局
        mainLayout.addView(titleLiveStreamRootLayout);//主要布局添加标题直播根布局
        
        

        //说明布局
        subTitleLayout.addView(subTitle);//说明布局添加说明文本
        mainLayout.addView(subTitleLayout);//主要布局添加说明布局

        //滚动布局
        scroll.addView(scrollLayout);//滚动布局添加功能布局
        mainLayout.addView(scroll);//主要布局添加滚动布局

        //下边栏布局
        relativeLayout.addView(leftButton);//底边布局添加隐藏退出按钮
        relativeLayout.addView(rightButton);//底边布局添加最小化按钮
        mainLayout.addView(relativeLayout);//主要布局添加底边布局


        //😋####### 根布局 #######😋
        menuLayout.addView(mainLayout);//根布局中添加主要布局
        //根布局中添加触摸缩放三角视图
        menuLayout.addView(zoomTriangleView, new FrameLayout.LayoutParams(ViewTool.convertDpToPx(aContext, 25),
                                                                          ViewTool.convertDpToPx(aContext, 25), Gravity.END | Gravity.BOTTOM));


        //直播模式图标点击事件                                          
        liveStreamIcon.setOnClickListener(new View.OnClickListener() {
            AlertDialog dialog;
            boolean isOn=true;//开关切换状态
            GradientTextView w=null;
                @Override
                public void onClick(View v) {
                    dialog =AlGuiDialogBox.showDiaLog
                    (aContext, 0xCEFAFAFA, 50f,
                     AlGui.GUI(aContext).addLinearLayout
                     (//这一对括号中是线性布局的空间
                         //线性布局属性配置🛠️
                         Gravity.CENTER,//子视图对齐方式
                         LinearLayout.VERTICAL,//线性布局方向
                         LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                         LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
                         AlGui.GUI(aContext).addTextView("直播模式", 18, 0xFF212121, Typeface.create(Typeface.DEFAULT, Typeface.BOLD)),
                         //AlGui.GUI(context).addLine(1f, 0xFF424242, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
                         AlGui.GUI(aContext).addTextView("启动直播模式后 直播或录屏画面将无法看见所有窗口\n\n只有在现实中的你自己可见", 12f, 0xFF424242, null)
                     ),
                     AlGui.GUI(aContext).addLinearLayout
                     (//这一对括号中是线性布局的空间
                         //线性布局属性配置🛠️
                         Gravity.CENTER,//子视图对齐方式
                         LinearLayout.HORIZONTAL,//线性布局方向
                         LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                         LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
                         //添加一个普通按钮
                         AlGui.GUI(aContext).addButton
                         (
                             //普通按钮属性配置🛠️
                             isOn?"开始直播":"结束直播", 15, 0xCEFAFAFA, null,//按钮文本，文本大小，文本颜色，文本字体
                             50,//按钮圆角半径
                             isOn?0xFF3F51B5:0xFFEF5350,//按钮背景颜色
                             0, 0xff000000,//按钮描边大小，描边颜色
                             //按钮宽度，按钮高度
                             LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                             //按钮事件监听器
                             new  AlGui.T_ButtonOnChangeListener(){
                                 @Override//事件中你可以获取到：button=按钮对象，back=按钮背景对象，buttonText=按钮文本对象，isChecked=按钮开关状态
                                 public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                     //按钮点击时执行的内容
                                     if(isOn){
                                         //直播模式开启
                                         AlGuiData.setIsLiveStream(aContext,true);
                                         com.android.support.Menu.setLiveStreamMode(true);
                                         //设置直播模式图标为直播开始图标
                                         liveStreamIcon.setImageBitmap(ImageTool.getBase64Image(AlGuiData.getVideo_Icon_LiveStart()));
                                         //liveStreamIcon.setImageTintList(ColorStateList.valueOf(0xFFBDBDBD)); //设置图标颜色
                                         AlGuiBubbleNotification.Inform(aContext).showMessageNotification_Exquisite(null, "开始直播", "安全系统已加载，实时保证您的安全，现在您可以录屏或直播了", 5000);
                                         if(w==null){
                                             //屏幕上悬浮显示一个霓虹灯文本
                                             w = AlGuiWindowView.showNeonLightText(
                                                 aContext,//上下文
                                                 "正在直播中…",//文本
                                                 new int[] { 0xFFff00cc, 0xFFffcc00, 0xFF00ffcc, 0xFFff0066}, //文本渐变色颜色数组
                                                 11f, //文本大小
                                                 null,//文本字体 (null代表跟随系统字体)
                                                 Gravity.START | Gravity.BOTTOM,//位置 (这里显示在屏幕右上角)
                                                 30, 30//xy位置偏移
                                             );
                                         }
                                         w.setText("正在直播中…");
                                         
                                     }else{
                                         //直播模式结束
                                         AlGuiData.setIsLiveStream(aContext,false);
                                         com.android.support.Menu.setLiveStreamMode(false);
                                         //设置直播模式图标为直播结束图标
                                         liveStreamIcon.setImageBitmap(ImageTool.getBase64Image(AlGuiData.getVideo_Icon_LiveEnd()));
                                         //liveStreamIcon.setImageTintList(ColorStateList.valueOf(0xFFBDBDBD)); //设置图标颜色
                                         AlGuiBubbleNotification.Inform(aContext).showMessageNotification_Exquisite(null, "结束直播", "安全系统已销毁", 5000);
                                         if(w!=null){
                                             w.setText(" ");
                                             
                                         }
                                     }
                                     isOn=!isOn;
                                     dialog.dismiss();

                                 }
                             }
                         )
                         ,
                         //添加一个普通按钮
                         AlGui.GUI(aContext).addButton
                         (
                             //普通按钮属性配置🛠️
                             "取消", 15, 0xCEFAFAFA, null,//按钮文本，文本大小，文本颜色，文本字体
                             50,//按钮圆角半径
                             0xff616161,//按钮背景颜色
                             0, 0xff000000,//按钮描边大小，描边颜色
                             //按钮宽度，按钮高度
                             LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                             //按钮事件监听器
                             new  AlGui.T_ButtonOnChangeListener(){
                                 @Override//事件中你可以获取到：button=按钮对象，back=按钮背景对象，buttonText=按钮文本对象，isChecked=按钮开关状态
                                 public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                     //按钮点击时执行的内容
                                     dialog.dismiss();

                                 }
                             }
                         )
                     )
                     );
                    
                }
            });


        //(旧方案)顶部触摸布局的触摸事件 - 移动悬浮窗口
        //(新方案)根布局全局触摸时的触摸事件 - 移动悬浮窗口
        menuLayout.setOnTouchListener(new OnTouchListener() {
               
                
                private int signX;

                private int signY;

                private float downX;

                private float downY;

                private float moveX;

                private float moveY;

                boolean isOne=true;//第一次移动时设置透明度标识
                boolean isMove=false;//当前是否在移动
                int moveThreshold=20;//手指移动的阀值 (灵敏度) 改小更容易触发移动 太小可能导致误判打不开悬浮窗

                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    switch (event.getActionMasked()) {

                            //点击了跟布局之外区域时触发 也就相当于点击了window之外的区域
                        case MotionEvent.ACTION_OUTSIDE:

                            clearMenu();//隐藏悬浮菜单
                            showBall();//显示悬浮球
                            return true;
                            //手指按下时触发
                        case MotionEvent.ACTION_DOWN:            
                            touchMoveLine.setBackColor(0xFF2196F3);//更改线条颜色

                            isOne = true;
                            isMove = false;
                            signX = menuParams.x;//记录视图初始位置的横向坐标
                            signY = menuParams.y;//记录视图初始位置的竖向坐标
                            downX = event.getRawX();//记录手指按下时的绝对横向坐标
                            downY = event.getRawY();//记录手指按下时的绝对竖向坐标
                            return true;



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
                                    //进行移动触觉振动反馈
                                    lineTouchLayout.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                                    float alpha=AlGuiData.getMenuAttributeSP(aContext).getFloat((String)AlGuiData.getMenuAttributeData().get("菜单透明度键名"), (float)AlGuiData.getMenuAttributeData().get("菜单透明度默认数据"));
                                   
                          
                                    menuLayout.setAlpha(alpha/2);//设置菜单透明度 透明度介于0和1之间
                                    
                                    
                                    isOne = false;//不是第一次移动了
                                }
                                menuParams.x = signX + (int) (event.getRawX() - downX);//根据手指移动的距离计算视图新的横向坐标
                                menuParams.y = signY + (int) (event.getRawY() - downY);//根据手指移动的距离计算视图新的竖向坐标
                                updateMenu();//更新视图位置
                            }

                            return true;


                            //手指抬起时触发
                        case MotionEvent.ACTION_UP:
                            //不是移动状态 抬起的 那么执行这些内容
                            if (!isMove) {

                            }
                            menuLayout.setAlpha(AlGuiData.getMenuAttributeSP(aContext).getFloat((String)AlGuiData.getMenuAttributeData().get("菜单透明度键名"), (float)AlGuiData.getMenuAttributeData().get("菜单透明度默认数据")));//设置菜单透明度 透明度介于0和1之间
                            touchMoveLine.setBackColor(AlGuiData.getMenuColorSP(aContext).getInt((String)AlGuiData.getMenuColorData().get("菜单顶部线条颜色键名"), (int)AlGuiData.getMenuColorData().get("菜单顶部线条颜色默认数据")));//恢复线条颜色

                            return true;
                    }
                    return false;
                }
            });
    }






    //初始化悬浮球的方法
    private void initBall() {
        //创建悬浮球根布局
        ballLayout = new vLinearLayout(aContext);
        ballLayout.setGravity(Gravity.CENTER);
        //悬浮球点击事件 (如果你有特殊需求)
        ballLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                }
            });

        //触摸监听器
        ballLayout.setOnTouchListener(new OnTouchListener() {
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
                                updateBall();//更新视图位置
                            }

                            break;


                            //手指抬起时触发
                        case MotionEvent.ACTION_UP:
                            //不是移动状态 抬起的 那么执行这些内容
                            if (!isMove) {
                                //播放悬浮球开启音频
                                /*if (Resource.audio != null) {
                                 Resource.audio.playSoundEffect("OpenMenu.ogg");
                                 }   
                                 //显示悬浮窗
                                 GameMenu.getMenu(mContext).showView();*/
                                //执行传入的悬浮球点击事件
                                showMenu();//显示悬浮菜单
                                clearBall();//隐藏悬浮球
                                //隐藏悬浮球
                                //clearView();
                            }
                            //移动状态抬起的就执行
                            if(isMove){
                                ballLayout.setAlpha(1);//设置悬浮球透明度 透明度介于0和1之间
                            }

                            
                            break;
                    }
                    return false;
                }
            });
    }








    //悬浮球窗口的配置
    private void initBallWindow() {
        ballManager = (WindowManager) aContext.getSystemService(Context.WINDOW_SERVICE);
        ballParams = new WindowManager.LayoutParams();
        ballParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ballParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ballParams.gravity = Gravity.TOP | Gravity.LEFT;
        ballParams.format = PixelFormat.RGBA_8888;
        ballParams.windowAnimations = android.R.style.Animation_Toast;
        ballParams.flags = 
         AlGuiData.getLiveStreamFlags() |
        //WindowManager.LayoutParams.FLAG_SECURE |//防截屏
        //WindowManager.LayoutParams.FLAG_DITHER | //抖动(防录屏)
       WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |//硬件加速
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        
        
        ;
        /*
         if(aContext instanceof Activity){
         //应用级窗口 (免悬浮权限 但是上下文必须是Activity 且 只能让悬浮窗显示在这一个Activity上方)
         ballParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//悬浮球
         menuParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//悬浮菜单
         return;
         }
         */
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (AppPermissionTool.isAndroidManifestPermissionExist(aContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            //系统级窗口 (需要悬浮窗权限)
            //悬浮球
            ballParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            
        }

    }
    //显示悬浮球
    public void showBall() {
         if(!AppPermissionTool.isAndroidManifestPermissionExist(aContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            return;
         }
         
         if(!AppPermissionTool.checkOverlayPermission(aContext)){
             //没有允许悬浮窗权限就直接结束
             return;
         }
        if (!isBallView) {
            isBallView = true;
            ballManager.addView(ballLayout, ballParams);
        }
    }
    //更新悬浮球
    public void updateBall() {    
        if (isBallView) {
            ballManager.updateViewLayout(ballLayout, ballParams);
        }
    }
    //清除悬浮球
    public void clearBall() {
        if (ballManager != null) {
            if (isBallView) {
                isBallView = false;
                ballManager.removeView(ballLayout);
            }

        }
    }








    //悬浮菜单窗口的配置
    private void initMenuWindow() {
        menuManager = (WindowManager) aContext.getSystemService(Context.WINDOW_SERVICE);
        menuParams = new WindowManager.LayoutParams();
        menuParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        menuParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        menuParams.gravity = Gravity.TOP | Gravity.LEFT;
        menuParams.format = PixelFormat.RGBA_8888;
        menuParams.windowAnimations = android.R.style.Animation_Toast;
        menuParams.flags = 
          AlGuiData.getLiveStreamFlags() |
            //WindowManager.LayoutParams.FLAG_SECURE |//防截屏
            //WindowManager.LayoutParams.FLAG_DITHER | //抖动(防录屏)
           WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |//硬件加速
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | 
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        /*
         if(aContext instanceof Activity){
         //应用级窗口 (免悬浮权限 但是上下文必须是Activity 且 只能让悬浮窗显示在这一个Activity上方)
         ballParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//悬浮球
         menuParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//悬浮菜单
         return;
         }
         */
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (AppPermissionTool.isAndroidManifestPermissionExist(aContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            //系统级窗口 (需要悬浮窗权限)
            //悬浮菜单
            menuParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            
        }
    }
    //显示悬浮菜单
    public void showMenu() {        
        if(!AppPermissionTool.isAndroidManifestPermissionExist(aContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            return;
        }

        if(!AppPermissionTool.checkOverlayPermission(aContext)){
            //没有允许悬浮窗权限就直接结束
            return;
        }
        if (!isMenuView) {
            isMenuView = true;
            menuManager.addView(menuLayout, menuParams);
        }
    }
    //更新悬浮菜单
    public void updateMenu() {
        if(!AppPermissionTool.isAndroidManifestPermissionExist(aContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            return;
        }
        
        if (isMenuView) {
            menuManager.updateViewLayout(menuLayout, menuParams);    
        }  
    }
    //清除悬浮菜单
    public void clearMenu() {
        if (menuManager != null) {
            if (isMenuView) {
                isMenuView = false;
                menuManager.removeView(menuLayout);
            }
        }
    }







    //拉拽悬浮窗右下角 动态调整悬浮窗大小的算法
    private class ResizeViewTouchListener implements View.OnTouchListener {
        private int dx, dy;
        private int initialX,initialY;
        private int menuLayoutWidth,menuLayoutHeight;//存储整个悬浮窗布局之前的宽高
        private int scrollWidth,scrollHeight;//存储滚动列表之前的宽高
     
      
        //boolean isOne=true;//第一次移动时的标识
        boolean isMove=false;//当前是否在移动
        int moveThreshold=10;//手指移动的阀值 (灵敏度) 改小更容易触发移动 太小可能导致误判打不开悬浮窗
        private float downX, downY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //isOne = true;
                    isMove = false;
                    downX = event.getRawX();//记录手指按下时的绝对横向坐标
                    downY = event.getRawY();//记录手指按下时的绝对竖向坐标

                    //记录触摸点的初始位置
                    initialX = menuParams.x;
                    initialY = menuParams.y;
                    
                    dx = (int) (event.getRawX() - menuParams.x);
                    dy = (int) (event.getRawY() - menuParams.y);
                    
                    //保存整个悬浮窗布局当前的宽高 用于后面进行计算新的宽高
                    menuLayoutWidth = menuLayout.getWidth();
                    menuLayoutHeight = menuLayout.getHeight();
                    
                    //保存滚动列表当前的宽高 用于后面进行计算新的宽高
                    scrollWidth=scroll.getWidth();
                    scrollHeight=scroll.getHeight();
                    
                   
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float moveDistanceX = Math.abs(event.getRawX() - downX);
                    float moveDistanceY = Math.abs(event.getRawY() - downY);
                    if (moveDistanceX > moveThreshold || moveDistanceY > moveThreshold) {
                        isMove = true;//当前是移动
                    }
                    //第一次移动执行的内容
                    /*if (isOne) {
                        //scroll.getLayoutParams().height = ScrollView.LayoutParams.WRAP_CONTENT;
                        isOne = false;//不是第一次移动了

                    }*/
                    if (isMove) {
                       
                       
                        //正在移动 动态执行的代码
                            // 计算触摸点相对于悬浮窗左上角的偏移量
                            int offsetX = (int) (event.getRawX() - dx - initialX);
                            int offsetY = (int) (event.getRawY() - dy - initialY);

                            // 根据偏移量计算整个悬浮窗布局新的宽高 
                            //int mWidth = Math.max(minWidthPx, menuLayoutWidth + offsetX);
                            //int mHeight = Math.max(minHeightPx, menuLayoutHeight + offsetY);
                         
                            
                          
                            //根据偏移量计算滚动列表新的宽高  
                            int sWidth = Math.max(350, scrollWidth + offsetX);//参数1最小宽度
                            int sHeight = Math.max(0, scrollHeight + offsetY);//参数1最小高度
                        //根据偏移量计算整个悬浮窗布局新的宽高 
                        int mWidth = Math.max(350, menuLayoutWidth + offsetX);//参数1最小宽度
                        int mHeight = Math.max(0, menuLayoutHeight + offsetY);//参数1最小高度
                            //为滚动列表设置新的宽高
                            if(scroll!=null){
                                scroll.getLayoutParams().width =sWidth;
                                
                                    scroll.getLayoutParams().height =sHeight;
                                
                                    
                                
                            }
                      
                            if(menuParams!=null){
                                //设置整个悬浮窗布局新的宽高
                                menuParams.width = mWidth;
                                if(scroll.getHeight()>0){
                                    //滚动列表高度大于0时才设置悬浮窗高
                                    menuParams.height = mHeight;

                                }
                            }
                           
                            
                            
                         
                            //更新窗口 以生效
                            updateMenu();
                        
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    //手指抬起触发
                   //是移动状态 抬起的就执行
                   if(isMove){
                       //保存数据  (这里保存时注意要直接保存当前进度 不要进行转单位 因为保存后下一次进时设置的时候获取到这个值后会转单位的 所以不影响)
                       AlGuiData.getMenuAttributeSPED(aContext).putFloat((String)AlGuiData.getMenuAttributeData().get("菜单滚动列表宽度键名"), scroll.getWidth());
                       AlGuiData.getMenuAttributeSPED(aContext).putFloat((String)AlGuiData.getMenuAttributeData().get("菜单滚动列表高度键名"), scroll.getHeight());
                       //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                       //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                       AlGuiData.getMenuAttributeSPED(aContext).apply();
                       //窗口自适应子视图
                       menuParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                       menuParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                       updateMenu();
                   }
                  



                    
                    return true;
            }
            return false;
        }
    }












//容器
    
    //增加一个折叠菜单
    public LinearLayout addCollapse(
        ViewGroup parentLayout,//父布局
        final CharSequence text, int textSize, int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        float CircleRadius ,//圆角半径
        int backColor,//背景颜色
        float borderSize, int borderColor,//描边大小，描边颜色
        final boolean isUnfold//默认是否展开
        ) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        params.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        vLinearLayout collapse = new vLinearLayout(aContext);
        collapse.setLayoutParams(params);
        collapse.setBackColor(backColor);
        collapse.setFilletRadiu(ViewTool.convertDpToPx(aContext, CircleRadius));
        collapse.setBorder(ViewTool.convertDpToPx(aContext, borderSize), borderColor);
        collapse.setPadding(10, 10, 10, 10);
        collapse.setClipChildren(true);
        collapse.setVerticalGravity(16);
        collapse.setOrientation(LinearLayout.VERTICAL);
        collapse.setId(AlGuiData.AlguiView.Collapse.getId());//设置ID



        LinearLayout.LayoutParams subparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout collapseSub = new LinearLayout(aContext);
        collapseSub.setLayoutParams(subparams);
        collapseSub.setVerticalGravity(16);
        collapseSub.setPadding(20, 20, 20, 20);
        collapseSub.setOrientation(LinearLayout.VERTICAL);
        collapseSub.setBackgroundColor(0);
        // collapseSub.setGravity(Gravity.CENTER);//子布局在我的中心位置


        final LinearLayout textLayout = new LinearLayout(aContext);
        textLayout. setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textLayout.setOrientation(LinearLayout.HORIZONTAL);
        textLayout.setGravity(Gravity.CENTER_VERTICAL);
        textLayout.setBackgroundColor(0);

        final TextView textView = new TextView(aContext);
        // textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setBackgroundColor(0);
        textView.setTextColor(textColor);

        if (textTF != null) {
            textView.setTypeface(textTF);
        }

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(textSize+5, 0,0,0);
        final TextView title = new TextView(aContext);
        // title.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        title.setLayoutParams(titleParams);
        title.setBackgroundColor(0);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));

            if(text!=null){
                title.setText(text);
            }

        title.setTextColor(textColor);
        if (textTF != null) {
            title.setTypeface(textTF);
        }

        textLayout.addView(textView);
        textLayout.addView(title);


        if (isUnfold) {
            collapseSub.setVisibility(View.VISIBLE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 11));
            textView.setText("▼");
        } else {
            collapseSub.setVisibility(View.GONE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 8));
            textView.setText("▶");
        }

        textLayout.setOnClickListener(new View.OnClickListener() {
                boolean isChecked = isUnfold;

                @Override
                public void onClick(View v) {
                    isChecked = !isChecked;
                    if (isChecked) {
                        AlGuiSoundEffect.getAudio(aContext).playSoundEffect(AlGuiSoundEffect.COLLAPSEMENU_OPEN);
                        collapseSub.setVisibility(View.VISIBLE);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 11));
                        textView.setText("▼");
                        return;
                    }
                    AlGuiSoundEffect.getAudio(aContext).playSoundEffect(AlGuiSoundEffect.COLLAPSEMENU_SHUT);
                    collapseSub.setVisibility(View.GONE);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 8));
                    textView.setText("▶");
                }
            });

        collapse.addView(textLayout);
        collapse.addView(collapseSub);
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(collapse);
        }
        
        //返回折叠菜单
            return collapseSub;
    }
    

    //增加一个折叠菜单
    public vLinearLayout addCollapse(
        final CharSequence text, int textSize, int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        float CircleRadius ,//圆角半径
        int backColor,//背景颜色
        float borderSize, int borderColor,//描边大小，描边颜色
        final boolean isUnfold,//默认是否展开
        View... view) {//折叠菜单内的视图

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        params.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        vLinearLayout collapse = new vLinearLayout(aContext);
        collapse.setLayoutParams(params);
        collapse.setBackColor(backColor);
        collapse.setFilletRadiu(ViewTool.convertDpToPx(aContext, CircleRadius));
        collapse.setBorder(ViewTool.convertDpToPx(aContext, borderSize), borderColor);
        collapse.setPadding(10, 10, 10, 10);
        collapse.setClipChildren(true);
        collapse.setVerticalGravity(16);
        collapse.setOrientation(LinearLayout.VERTICAL);
        collapse.setId(AlGuiData.AlguiView.Collapse.getId());//设置ID



        LinearLayout.LayoutParams subparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout collapseSub = new LinearLayout(aContext);
        collapseSub.setLayoutParams(subparams);
        collapseSub.setVerticalGravity(16);
        collapseSub.setPadding(20, 20, 20, 20);
        collapseSub.setOrientation(LinearLayout.VERTICAL);
        collapseSub.setBackgroundColor(0);
        // collapseSub.setGravity(Gravity.CENTER);//子布局在我的中心位置


        final LinearLayout textLayout = new LinearLayout(aContext);
        textLayout. setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textLayout.setOrientation(LinearLayout.HORIZONTAL);
        textLayout.setGravity(Gravity.CENTER_VERTICAL);
        textLayout.setBackgroundColor(0);

        final TextView textView = new TextView(aContext);
       // textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setBackgroundColor(0);
        textView.setTextColor(textColor);

        if (textTF != null) {
            textView.setTypeface(textTF);
        }

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(textSize+5, 0,0,0);
        final TextView title = new TextView(aContext);
       // title.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        title.setLayoutParams(titleParams);
        title.setBackgroundColor(0);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
   
        if(text!=null){
            title.setText(text);
        }
        
    
        
        title.setTextColor(textColor);
        if (textTF != null) {
            title.setTypeface(textTF);
        }

        textLayout.addView(textView);
        textLayout.addView(title);


        if (isUnfold) {
            collapseSub.setVisibility(View.VISIBLE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 11));
            textView.setText("▼");
        } else {
            collapseSub.setVisibility(View.GONE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 8));
            textView.setText("▶");
        }

        textLayout.setOnClickListener(new View.OnClickListener() {
                boolean isChecked = isUnfold;

                @Override
                public void onClick(View v) {
                    isChecked = !isChecked;
                    if (isChecked) {
                        AlGuiSoundEffect.getAudio(aContext).playSoundEffect(AlGuiSoundEffect.COLLAPSEMENU_OPEN);
                        collapseSub.setVisibility(View.VISIBLE);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 11));
                        textView.setText("▼");
                        return;
                    }
                    AlGuiSoundEffect.getAudio(aContext).playSoundEffect(AlGuiSoundEffect.COLLAPSEMENU_SHUT);
                    collapseSub.setVisibility(View.GONE);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, 8));
                    textView.setText("▶");
                }
            });

        collapse.addView(textLayout);
        collapse.addView(collapseSub);

        //将传入的所有视图挨个加进折叠菜单内部布局中
        for (View aView : view) {
            if (aView != null) {

                //如果是小按钮
                if (aView.getId() == AlGuiData.AlguiView.SmallButton.getId()) {
                    //Log.d("折叠菜单添加一个控件", "小按钮");
                }
                collapseSub.addView(aView);
            }
        }
        //返回折叠菜单
        return collapse;
    }
 



    //增加一个线性布局
    public LinearLayout addLinearLayout(
        ViewGroup parentLayout,//父布局
        int gravity,//子视图对齐方式
        int direction,//线性布局方向
        int width,//线性布局宽
        int height//线性布局高
        ) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            (width != LinearLayout.LayoutParams.WRAP_CONTENT
            && width != LinearLayout.LayoutParams.MATCH_PARENT
            && width != LinearLayout.LayoutParams.FILL_PARENT)
            ?
            ViewTool.convertDpToPx(aContext, width)
            :
            width, 

            (height != LinearLayout.LayoutParams.WRAP_CONTENT
            && height != LinearLayout.LayoutParams.MATCH_PARENT
            && height != LinearLayout.LayoutParams.FILL_PARENT)
            ?
            ViewTool.convertDpToPx(aContext, height)
            :
            height,100f);
        //根布局
        /*LinearLayout Layout = new LinearLayout(aContext);
        Layout.setGravity(gravity);
        Layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Layout.setOrientation(direction);*/

        //主要布局
        LinearLayout Layout2 = new LinearLayout(aContext);
        Layout2.setGravity(gravity);
        Layout2.setLayoutParams(params);
        Layout2.setOrientation(direction);
        //Layout.addView(Layout2);


     
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(Layout2);
        }
        //return Layout;
        return Layout2;
    }


    //增加一个线性布局
    public LinearLayout addLinearLayout(
        int gravity,//子视图对齐方式
        int direction,//线性布局方向
        int width,//线性布局宽
        int height,//线性布局高
        View... view) {//线性布局内的视图
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            (width != LinearLayout.LayoutParams.WRAP_CONTENT
            && width != LinearLayout.LayoutParams.MATCH_PARENT
            && width != LinearLayout.LayoutParams.FILL_PARENT)
            ?
            ViewTool.convertDpToPx(aContext, width)
            :
            width, 

            (height != LinearLayout.LayoutParams.WRAP_CONTENT
            && height != LinearLayout.LayoutParams.MATCH_PARENT
            && height != LinearLayout.LayoutParams.FILL_PARENT)
            ?
            ViewTool.convertDpToPx(aContext, height)
            :
            height,100f);
        //根布局
       /* LinearLayout Layout = new LinearLayout(aContext);
        Layout.setGravity(gravity);
        Layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Layout.setOrientation(direction);
        */
        //主要布局
        LinearLayout Layout2 = new LinearLayout(aContext);
        Layout2.setGravity(gravity);
        Layout2.setLayoutParams(params);
        Layout2.setOrientation(direction);
        //Layout.addView(Layout2);
      

        //将传入的所有视图挨个加进主要布局中
        for (View aView : view) {
            if (aView != null) {
                //如果是小按钮
                if (aView.getId() == AlGuiData.AlguiView.SmallButton.getId()) {
                    //Log.d("自定义线性布局添加一个控件", "小按钮");
                }
                Layout2.addView(aView);
            }
        }
        //return Layout;
        return Layout2;
    }















//视图

    //增加一个开关按钮
    public Switch addSwitch(
        ViewGroup parentLayout,//父布局
        CharSequence switchText, float switchTextSize, int switchTextColor, Typeface switchTextTF,//开关文本，文本大小，文本颜色，文本字体
        CharSequence describeText, float describeTextSize, int describeTextColor, Typeface describeTextTF,//开关描述文本，文本大小，文本颜色，文本字体
        final int roundOnColor, final int railOnColor,//开关开启时圆的颜色和轨迹的颜色
        final int roundOffColor, final int railOffColor,//开关关闭时圆的颜色和轨迹的颜色
        final T_SwitchOnChangeListener fun) {//事件监听器
        //根布局
        LinearLayout switchLayout = new LinearLayout(aContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        params.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        switchLayout.setLayoutParams(params);
        switchLayout.setOrientation(LinearLayout.VERTICAL);
        switchLayout.setId(AlGuiData.AlguiView.Switch.getId());


        /*LinearLayout sLayout = new LinearLayout(aContext);
         LinearLayout.LayoutParams sParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
         sLayout.setLayoutParams(sParams);
         sLayout.setOrientation(LinearLayout.HORIZONTAL);*/

        //开关
        final Switch aSwitch=new Switch(aContext);
        //aSwitch.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        if(switchText!=null){
            aSwitch.setText(switchText);
        }
        
        aSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, switchTextSize));
        aSwitch.setTextColor(switchTextColor);
        setSwitchColor(aSwitch, roundOffColor, railOffColor);//设置开关颜色
        if (switchTextTF != null) {
            aSwitch.setTypeface(switchTextTF);
        }



        //描述
        final TextView desc = new TextView(aContext);
        desc.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoParams.setMargins(0, -10, 0, 0);
        desc.setLayoutParams(infoParams);
        if(describeText!=null){
            desc.setText(describeText);
        }
        
        desc.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, describeTextSize));
        desc.setTextColor(describeTextColor);
        if (describeTextTF != null) {
            desc.setTypeface(describeTextTF);
        }

        //开关按钮点击事件
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //SoundEffectPlayer.getAudio(aContext).playSoundEffect(isChecked ? "On.ogg" : "Off.ogg");//根据开关不同状态播放不同音效
                    setSwitchColor(aSwitch, aSwitch.isChecked() ?roundOnColor: roundOffColor, aSwitch.isChecked() ?railOnColor: railOffColor);//切换开关颜色
                    if (fun != null) {
                        fun.onClick(buttonView, desc, aSwitch.isChecked());//执行外部传入的事件监听器中的点击方法
                    }
                    
                }
            });
        //sLayout.addView(aSwitch);
        switchLayout.addView(aSwitch);
        switchLayout.addView(desc);
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(switchLayout);
        }
        return aSwitch;
    }
    
    //增加一个开关按钮
    public LinearLayout addSwitch(
        CharSequence switchText, float switchTextSize, int switchTextColor, Typeface switchTextTF,//开关文本，文本大小，文本颜色，文本字体
        CharSequence describeText, float describeTextSize, int describeTextColor, Typeface describeTextTF,//开关描述文本，文本大小，文本颜色，文本字体
        final int roundOnColor, final int railOnColor,//开关开启时圆的颜色和轨迹的颜色
        final int roundOffColor, final int railOffColor,//开关关闭时圆的颜色和轨迹的颜色
        final T_SwitchOnChangeListener fun) {//事件监听器
        //根布局
        LinearLayout switchLayout = new LinearLayout(aContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        params.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        switchLayout.setLayoutParams(params);
        switchLayout.setOrientation(LinearLayout.VERTICAL);
        switchLayout.setId(AlGuiData.AlguiView.Switch.getId());


        /*LinearLayout sLayout = new LinearLayout(aContext);
         LinearLayout.LayoutParams sParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
         sLayout.setLayoutParams(sParams);
         sLayout.setOrientation(LinearLayout.HORIZONTAL);*/

        //开关
        final Switch aSwitch=new Switch(aContext);
        //aSwitch.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        if(switchText!=null){
            aSwitch.setText(switchText);
        }
        aSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, switchTextSize));
        aSwitch.setTextColor(switchTextColor);
        setSwitchColor(aSwitch, roundOffColor, railOffColor);//设置开关颜色
        if (switchTextTF != null) {
            aSwitch.setTypeface(switchTextTF);
        }
      

        //描述
        final TextView desc = new TextView(aContext);
        desc.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoParams.setMargins(0, -10, 0, 0);
        desc.setLayoutParams(infoParams);
        if(describeText!=null){
            desc.setText(describeText);
        }
        desc.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, describeTextSize));
        desc.setTextColor(describeTextColor);
        if (describeTextTF != null) {
            desc.setTypeface(describeTextTF);
        }

        //开关按钮点击事件
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //SoundEffectPlayer.getAudio(aContext).playSoundEffect(isChecked ? "On.ogg" : "Off.ogg");//根据开关不同状态播放不同音效
                    setSwitchColor(aSwitch, aSwitch.isChecked() ?roundOnColor: roundOffColor, aSwitch.isChecked() ?railOnColor: railOffColor);//切换开关颜色
                    if (fun != null) {
                        fun.onClick(buttonView, desc, aSwitch.isChecked());//执行外部传入的事件监听器中的点击方法
                    }
                    
                }
            });
        //sLayout.addView(aSwitch);
        switchLayout.addView(aSwitch);
        switchLayout.addView(desc);

        return switchLayout;
    }
    // [@] 切换设置开关按钮颜色的方法 参数1要设置的按钮 参数2滑块颜色 参数3滑条颜色
    private boolean setSwitchColor(Switch aSwitch, int thumbColor, int trackColor) {
        if (aSwitch != null) {
            if (aSwitch.getThumbDrawable() != null && aSwitch.getTrackDrawable() != null) {
                aSwitch.getThumbDrawable().setColorFilter(thumbColor, PorterDuff.Mode.MULTIPLY);
                aSwitch.getTrackDrawable().setColorFilter(trackColor, PorterDuff.Mode.MULTIPLY);
                return true;
            } else {
                // 设置失败时执行的内容
                return false;
            }
        } else {
            return false;
        }

    }
    //开关按钮事件监听器
    public static interface T_SwitchOnChangeListener {
        //点击
        public abstract void onClick(CompoundButton aSwitch, TextView desc, boolean isChecked);
    }





    //增加一个小按钮
    public vLinearLayout addSmallButton(
        ViewGroup parentLayout,//父布局
        final CharSequence text, float textSize, final int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        float CircleRadius ,//圆角半径
        final int backColor,//背景颜色
        float borderSize, int borderColor,//描边大小，描边颜色
        final T_ButtonOnChangeListener fun) { //事件监听器

        final GradientDrawable back = new GradientDrawable();
        back.setColor(backColor);
        back.setCornerRadius(ViewTool.convertDpToPx(aContext, CircleRadius));
        back.setStroke(ViewTool.convertDpToPx(aContext, borderSize), borderColor);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        final vLinearLayout button=new vLinearLayout(aContext);
        button.setLayoutParams(layoutParams);
        button.setBackground(back);
        button.setPadding(10, 10, 10, 10);
        button.setClipChildren(true);
        button.setId(AlGuiData.AlguiView.SmallButton.getId());
        button.setGravity(Gravity.CENTER);

        final TextView buttonText=new TextView(aContext);
        // buttonText.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        if(text!=null){
            buttonText.setText(text);
        }
       
      
        
        buttonText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        buttonText.setTextColor(textColor);
        buttonText.setGravity(Gravity.CENTER);
        if (textTF != null) {
            buttonText.setTypeface(textTF);
        }
        button.setOnClickListener(new View.OnClickListener() {
                boolean isChecked =true;//点击开关状态
              
                boolean b=true;//防止连点
                @Override
                public void onClick(final View v) {
                    if(b==false){
                        return;
                    }
                    b=false;
                    //SoundEffectPlayer.getAudio(aContext).playSoundEffect("mcDanJi.ogg");//播放点击音效

                    /*RippleDrawable rippleDrawable = new RippleDrawable(
                     ColorStateList.valueOf(ViewTool.darkenColor(backColor, 0.6f)), // 点击时水波纹的颜色
                     back, // 按钮原始的背景
                     null); // 水波纹边界，默认为按钮的边界

                     button.setBackground(rippleDrawable);*/
                    if (fun != null) {
                        //执行外部传入的监听
                        fun.onClick(v, back, buttonText, isChecked);
                        isChecked = !isChecked;
                    }
                    final int color = back.getColor().getDefaultColor();
                    
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
                    back.setColor(ViewTool.darkenColor(color, 0.7f));// 变动按钮背景颜色(加深)

                    // 设置监听器，在动画结束时恢复按钮的原始状态
                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { 
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                button.clearAnimation();
                                back.setColor(color);// 恢复按钮背景颜色
                                b=true;
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });
                    // 启动动画
                    button.startAnimation(animationSet);
                    
                    
                }
            });


        button.addView(buttonText);
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(button);
        }
        return button;
    }



    //增加一个小按钮
    public vLinearLayout addSmallButton(
        final CharSequence text, float textSize, final int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        float CircleRadius ,//圆角半径
        final int backColor,//背景颜色
        float borderSize, int borderColor,//描边大小，描边颜色
        final T_ButtonOnChangeListener fun) { //事件监听器

        final GradientDrawable back = new GradientDrawable();
        back.setColor(backColor);
        back.setCornerRadius(ViewTool.convertDpToPx(aContext, CircleRadius));
        back.setStroke(ViewTool.convertDpToPx(aContext, borderSize), borderColor);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        final vLinearLayout button=new vLinearLayout(aContext);
        button.setLayoutParams(layoutParams);
        button.setBackground(back);
        button.setPadding(10, 10, 10, 10);
        button.setClipChildren(true);
        button.setId(AlGuiData.AlguiView.SmallButton.getId());
        button.setGravity(Gravity.CENTER);

        final TextView buttonText=new TextView(aContext);
       // buttonText.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        buttonText.setText(text != null ?text: "按钮");
        buttonText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        buttonText.setTextColor(textColor);
        buttonText.setGravity(Gravity.CENTER);
        if (textTF != null) {
            buttonText.setTypeface(textTF);
        }
        button.setOnClickListener(new View.OnClickListener() {
                boolean isChecked =true;//点击开关状态
               
                boolean b=true;//防止连点
                @Override
                public void onClick(final View v) {
                    if(b==false){
                        return;
                    }
                    b=false;
                    
                    //SoundEffectPlayer.getAudio(aContext).playSoundEffect("mcDanJi.ogg");//播放点击音效

                    /*RippleDrawable rippleDrawable = new RippleDrawable(
                     ColorStateList.valueOf(ViewTool.darkenColor(backColor, 0.6f)), // 点击时水波纹的颜色
                     back, // 按钮原始的背景
                     null); // 水波纹边界，默认为按钮的边界

                     button.setBackground(rippleDrawable);*/

                    if (fun != null) {
                        //执行外部传入的监听
                        fun.onClick(v, back, buttonText, isChecked);
                        isChecked = !isChecked;
                    }
                    final int color = back.getColor().getDefaultColor();
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
                    back.setColor(ViewTool.darkenColor(color, 0.7f));// 变动按钮背景颜色(加深)

                    // 设置监听器，在动画结束时恢复按钮的原始状态
                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { 
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                button.clearAnimation();
                                back.setColor(color);// 恢复按钮背景颜色
                                b=true;
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


    //增加一个普通按钮
    public vLinearLayout addButton(
        ViewGroup parentLayout,//父布局
        final CharSequence text, float textSize, final int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        float CircleRadius ,//圆角半径
        final int backColor,//背景颜色
        float borderSize, int borderColor,//描边大小，描边颜色
        int width, int height,//按钮宽度，按钮高度
        final T_ButtonOnChangeListener fun) { //事件监听器

        final GradientDrawable back = new GradientDrawable();
        back.setColor(backColor);
        back.setCornerRadius(ViewTool.convertDpToPx(aContext, CircleRadius));
        back.setStroke(ViewTool.convertDpToPx(aContext, borderSize), borderColor);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            (width != LinearLayout.LayoutParams.WRAP_CONTENT
            && width != LinearLayout.LayoutParams.MATCH_PARENT
            && width != LinearLayout.LayoutParams.FILL_PARENT)
            ?
            ViewTool.convertDpToPx(aContext, width)
            :
            width, 

            (height != LinearLayout.LayoutParams.WRAP_CONTENT
            && height != LinearLayout.LayoutParams.MATCH_PARENT
            && height != LinearLayout.LayoutParams.FILL_PARENT)
            ?
            ViewTool.convertDpToPx(aContext, height)
            :
            height,100f);

        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        final vLinearLayout button=new vLinearLayout(aContext);
        button.setLayoutParams(layoutParams);
        button.setBackground(back);
        button.setPadding(10, 10, 10, 10);
        button.setClipChildren(true);
        button.setId(AlGuiData.AlguiView.Button.getId());
        button.setGravity(Gravity.CENTER);


        final TextView buttonText=new TextView(aContext);
        //buttonText.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        if(text!=null){
            buttonText.setText(text);
        }
        
        buttonText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        buttonText.setTextColor(textColor);
        buttonText.setGravity(Gravity.CENTER);
        if (textTF != null) {
            buttonText.setTypeface(textTF);
        }
        button.setOnClickListener(new View.OnClickListener() {
                boolean isChecked =true;//点击开关状态
                boolean b=true;//防止连点
                @Override
                public void onClick(final View v) {
                    if(b==false){
                        return;
                    }
                    b=false;
                    //SoundEffectPlayer.getAudio(aContext).playSoundEffect("mcDanJi.ogg");//播放点击音效

                    /*RippleDrawable rippleDrawable = new RippleDrawable(
                     ColorStateList.valueOf(ViewTool.darkenColor(backColor, 0.6f)), // 点击时水波纹的颜色
                     back, // 按钮原始的背景
                     null); // 水波纹边界，默认为按钮的边界

                     button.setBackground(rippleDrawable);*/
                    if (fun != null) {
                        //执行外部传入的监听
                        fun.onClick(v, back, buttonText, isChecked);
                        isChecked = !isChecked;
                    }
                    final int color = back.getColor().getDefaultColor();

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
                    back.setColor(ViewTool.darkenColor(color, 0.7f));// 变动按钮背景颜色(加深)

                    // 设置监听器，在动画结束时恢复按钮的原始状态
                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { 
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                button.clearAnimation();
                                back.setColor(color);// 恢复按钮背景颜色
                                b=true;
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });
                    // 启动动画
                    button.startAnimation(animationSet);
                    
                    
                }
            });


        button.addView(buttonText);
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(button);
        }
        return button;
    }
    
    //增加一个普通按钮
    public vLinearLayout addButton(
        final CharSequence text, float textSize, final int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        float CircleRadius ,//圆角半径
        final int backColor,//背景颜色
        float borderSize, int borderColor,//描边大小，描边颜色
        int width, int height,//按钮宽度，按钮高度
        final T_ButtonOnChangeListener fun) { //事件监听器

        final GradientDrawable back = new GradientDrawable();
        back.setColor(backColor);
        back.setCornerRadius(ViewTool.convertDpToPx(aContext, CircleRadius));
        back.setStroke(ViewTool.convertDpToPx(aContext, borderSize), borderColor);
        
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            (width != LinearLayout.LayoutParams.WRAP_CONTENT
            && width != LinearLayout.LayoutParams.MATCH_PARENT
            && width != LinearLayout.LayoutParams.FILL_PARENT)
            ?
            ViewTool.convertDpToPx(aContext, width)
            :
            width, 

            (height != LinearLayout.LayoutParams.WRAP_CONTENT
            && height != LinearLayout.LayoutParams.MATCH_PARENT
            && height != LinearLayout.LayoutParams.FILL_PARENT)
            ?
            ViewTool.convertDpToPx(aContext, height)
            :
            height,100f);
        
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        final vLinearLayout button=new vLinearLayout(aContext);
        button.setLayoutParams(layoutParams);
        button.setBackground(back);
        button.setPadding(10, 10, 10, 10);
        button.setClipChildren(true);
        button.setId(AlGuiData.AlguiView.Button.getId());
        button.setGravity(Gravity.CENTER);
      

        final TextView buttonText=new TextView(aContext);
        //buttonText.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        if(text!=null){
            buttonText.setText(text);
        }
        buttonText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        buttonText.setTextColor(textColor);
        buttonText.setGravity(Gravity.CENTER);
        if (textTF != null) {
            buttonText.setTypeface(textTF);
        }
        button.setOnClickListener(new View.OnClickListener() {
                boolean isChecked =true;//点击开关状态
                boolean b=true;//防止连点
                @Override
                public void onClick(final View v) {
                    if(b==false){
                        return;
                    }
                    b=false;
                    //SoundEffectPlayer.getAudio(aContext).playSoundEffect("mcDanJi.ogg");//播放点击音效

                    /*RippleDrawable rippleDrawable = new RippleDrawable(
                     ColorStateList.valueOf(ViewTool.darkenColor(backColor, 0.6f)), // 点击时水波纹的颜色
                     back, // 按钮原始的背景
                     null); // 水波纹边界，默认为按钮的边界

                     button.setBackground(rippleDrawable);*/
                     
                    if (fun != null) {
                        //执行外部传入的监听
                        fun.onClick(v, back, buttonText, isChecked);
                        isChecked = !isChecked;
                    }
                    final int color = back.getColor().getDefaultColor();
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
                    back.setColor(ViewTool.darkenColor(color, 0.7f));// 变动按钮背景颜色(加深)

                    // 设置监听器，在动画结束时恢复按钮的原始状态
                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { 
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                button.clearAnimation();
                                back.setColor(color);// 恢复按钮背景颜色
                                b=true;
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
    
    


    //增加一个不带按钮的输入框
    public EditText addEditText(
        ViewGroup parentLayout,//父布局
        float textSize, Typeface textTF,//输入框和按钮的文本大小，输入框和按钮的字体
        //输入框提示内容颜色，输入框提示内容, 输入框输入内容颜色, 输入框默认输入内容, 输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色
        int editHintColor, CharSequence editHint, int editContentColor, CharSequence editContent, float editCircleRadius, int editBackColor, float editBorderSize, int editBorderColor,
         final T_EditTextOnChangeListener fun) {//输入框事件监听器

        //输入框
        LinearLayout.LayoutParams editLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                   LinearLayout.LayoutParams.WRAP_CONTENT, 100f);
         editLayoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        GradientDrawable Backgroundss = new GradientDrawable();
        Backgroundss.setCornerRadius(ViewTool.convertDpToPx(aContext, editCircleRadius));
        Backgroundss.setColor(editBackColor);
        Backgroundss.setStroke(ViewTool.convertDpToPx(aContext, editBorderSize), editBorderColor);

        final EditText editext = new EditText(aContext);
        // editext.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        editext.setTextIsSelectable(true);//启用输入框文本选择功能
        //设置内容过多时光标拖动时查看之前输入的内容是横向的
        editext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE); // 支持多行输入
        editext.setLayoutParams(editLayoutParams);
        if(editHint!=null){
            editext.setHint(editHint);
        }
        editext.setHintTextColor(editHintColor);
        if(editContent != null){
            editext.setText(editContent);
        }

        editext.setTextColor(editContentColor);
        editext.setPadding(20, 10, 20, 10);
        editext.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        editext.setBackground(Backgroundss);
        if(textTF!=null){
            editext.setTypeface(textTF);
        }

         editext.setId(AlGuiData.AlguiView.EditText.getId());

// 设置长按监听器
        editext.setOnLongClickListener(new View.OnLongClickListener() {
                PopupMenu popupMenu;

                @Override
                public boolean onLongClick(View v) {
                    if(popupMenu==null){
                        // 创建菜单
                        popupMenu = new PopupMenu(aContext, v);
                        Menu menu = popupMenu.getMenu();

                        menu.add(Menu.NONE, 95, Menu.NONE, "全选"); // 添加全选选项
                        menu.add(Menu.NONE, 96, Menu.NONE, "复制"); // 添加复制选项
                        menu.add(Menu.NONE, 97, Menu.NONE, "粘贴"); // 添加粘贴选项
                        menu.add(Menu.NONE, 98, Menu.NONE, "剪切"); // 添加剪切选项


                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                ClipboardManager clipboardManager = (ClipboardManager) aContext.getSystemService(Context.CLIPBOARD_SERVICE);

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    ClipData clipData = ClipData.newPlainText("text", editext.getText());

                                    switch (item.getItemId()) {
                                        case 95:
                                            //Log.d("艾琳测试","全选");
                                            // 执行全选操作
                                            editext.selectAll();

                                            return true;
                                        case 96:
                                            //Log.d("艾琳测试","复制");
                                            // 执行复制操作
                                            clipboardManager.setPrimaryClip(clipData);


                                            return true;
                                        case 97:
                                            //Log.d("艾琳测试","粘贴");
                                            // 执行粘贴操作

                                            // 获取剪贴板中的文本
                                            if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                                editext.setText(""); // 清空 EditText 中的文本
                                                ClipData.Item cItem = clipboardManager.getPrimaryClip().getItemAt(0);
                                                String pasteText = cItem.getText().toString();
                                                // 将粘贴的文本插入到 EditText 中
                                                editext.getText().insert(editext.getSelectionStart(), pasteText);
                                            }

                                            return true;
                                        case 98:
                                            //Log.d("艾琳测试","剪切");
                                            // 执行剪切操作
                                            clipboardManager.setPrimaryClip(clipData);
                                            editext.setText(""); // 清空 EditText 中的文本


                                            return true;

                                        default:
                                            return false;
                                    }

                                }
                            });

                    }
                    popupMenu.show();




                    return false;
                }
            });


        //输入框输入文本监听器
        editext.addTextChangedListener
        (
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    // 在文本变化之前执行的操作
                    if(fun!=null){
                        fun.beforeTextChanged(editext,charSequence,start,count,after);
                    }

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // 在文本变化时执行的操作
                    if(fun!=null){
                        fun.onTextChanged(editext,charSequence,start,before,count);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // 在文本变化之后执行的操作              
                    if(fun!=null){
                        fun.afterTextChanged(editext,editable);
                    }

                }
            }
        );
       
        
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(editext);
        }
        return editext;
    }
    
    
    //增加一个不带按钮的输入框
    public EditText addEditText(
        float textSize, Typeface textTF,//输入框和按钮的文本大小，输入框和按钮的字体
        //输入框提示内容颜色，输入框提示内容, 输入框输入内容颜色, 输入框默认输入内容, 输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色
        int editHintColor, CharSequence editHint, int editContentColor, CharSequence editContent, float editCircleRadius, int editBackColor, float editBorderSize, int editBorderColor,
        final T_EditTextOnChangeListener fun) {//输入框事件监听器

        //输入框
        LinearLayout.LayoutParams editLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                   LinearLayout.LayoutParams.WRAP_CONTENT, 100f);
        editLayoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        GradientDrawable Backgroundss = new GradientDrawable();
        Backgroundss.setCornerRadius(ViewTool.convertDpToPx(aContext, editCircleRadius));
        Backgroundss.setColor(editBackColor);
        Backgroundss.setStroke(ViewTool.convertDpToPx(aContext, editBorderSize), editBorderColor);

        final EditText editext = new EditText(aContext);
        // editext.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        editext.setTextIsSelectable(true);//启用输入框文本选择功能
        //设置内容过多时光标拖动时查看之前输入的内容是横向的
        editext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE); // 支持多行输入
        editext.setLayoutParams(editLayoutParams);
        if(editHint!=null){
            editext.setHint(editHint);
        }
        editext.setHintTextColor(editHintColor);
        if(editContent != null){
            editext.setText(editContent);
        }

        editext.setTextColor(editContentColor);
        editext.setPadding(20, 10, 20, 10);
        editext.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        editext.setBackground(Backgroundss);
        if(textTF!=null){
            editext.setTypeface(textTF);
        }

        editext.setId(AlGuiData.AlguiView.EditText.getId());

// 设置长按监听器
        editext.setOnLongClickListener(new View.OnLongClickListener() {
                PopupMenu popupMenu;

                @Override
                public boolean onLongClick(View v) {
                    if(popupMenu==null){
                        // 创建菜单
                        popupMenu = new PopupMenu(aContext, v);
                        Menu menu = popupMenu.getMenu();

                        menu.add(Menu.NONE, 95, Menu.NONE, "全选"); // 添加全选选项
                        menu.add(Menu.NONE, 96, Menu.NONE, "复制"); // 添加复制选项
                        menu.add(Menu.NONE, 97, Menu.NONE, "粘贴"); // 添加粘贴选项
                        menu.add(Menu.NONE, 98, Menu.NONE, "剪切"); // 添加剪切选项


                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                ClipboardManager clipboardManager = (ClipboardManager) aContext.getSystemService(Context.CLIPBOARD_SERVICE);

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    ClipData clipData = ClipData.newPlainText("text", editext.getText());

                                    switch (item.getItemId()) {
                                        case 95:
                                            //Log.d("艾琳测试","全选");
                                            // 执行全选操作
                                            editext.selectAll();

                                            return true;
                                        case 96:
                                            //Log.d("艾琳测试","复制");
                                            // 执行复制操作
                                            clipboardManager.setPrimaryClip(clipData);


                                            return true;
                                        case 97:
                                            //Log.d("艾琳测试","粘贴");
                                            // 执行粘贴操作

                                            // 获取剪贴板中的文本
                                            if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                                editext.setText(""); // 清空 EditText 中的文本
                                                ClipData.Item cItem = clipboardManager.getPrimaryClip().getItemAt(0);
                                                String pasteText = cItem.getText().toString();
                                                // 将粘贴的文本插入到 EditText 中
                                                editext.getText().insert(editext.getSelectionStart(), pasteText);
                                            }

                                            return true;
                                        case 98:
                                            //Log.d("艾琳测试","剪切");
                                            // 执行剪切操作
                                            clipboardManager.setPrimaryClip(clipData);
                                            editext.setText(""); // 清空 EditText 中的文本


                                            return true;

                                        default:
                                            return false;
                                    }

                                }
                            });

                    }
                    popupMenu.show();




                    return false;
                }
            });


        //输入框输入文本监听器
        editext.addTextChangedListener
        (
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    // 在文本变化之前执行的操作
                    if(fun!=null){
                        fun.beforeTextChanged(editext,charSequence,start,count,after);
                    }

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // 在文本变化时执行的操作
                    if(fun!=null){
                        fun.onTextChanged(editext,charSequence,start,before,count);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // 在文本变化之后执行的操作              
                    if(fun!=null){
                        fun.afterTextChanged(editext,editable);
                    }

                }
            }
        );

        return editext;
    }

    //增加一个带按钮的输入框
    public EditText addEditText(
    ViewGroup parentLayout,//父布局
        float textSize, Typeface textTF,//输入框和按钮的文本大小，输入框和按钮的字体
        //输入框提示内容颜色，输入框提示内容, 输入框输入内容颜色, 输入框默认输入内容, 输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色
        int editHintColor, CharSequence editHint, int editContentColor, CharSequence editContent, float editCircleRadius, int editBackColor, float editBorderSize, int editBorderColor,
        //按钮文本颜色，按钮文本，按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色
        int buttonTextColor, CharSequence buttonText, float buttonCircleRadius, int buttonBackColor, float buttonBorderSize, int buttonBorderColor,
        final T_EditTextOnChangeListener fun) {//输入框事件监听器

        //根布局
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        LinearLayout layout=new LinearLayout(aContext);
        layout.setLayoutParams(layoutParams);
        layout.setClipChildren(true);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setId(AlGuiData.AlguiView.EditText.getId());
       



        //输入框
        LinearLayout.LayoutParams editLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                   LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        editLayoutParams.setMargins(0, 0, 8, 0);
        GradientDrawable Backgroundss = new GradientDrawable();
        Backgroundss.setCornerRadius(ViewTool.convertDpToPx(aContext, editCircleRadius));
        Backgroundss.setColor(editBackColor);
        Backgroundss.setStroke(ViewTool.convertDpToPx(aContext, editBorderSize), editBorderColor);
        
        final EditText editext = new EditText(aContext);
        // editext.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        editext.setTextIsSelectable(true);//启用输入框文本选择功能
        //设置内容过多时光标拖动时查看之前输入的内容是横向的
        editext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE); // 支持多行输入
        editext.setLayoutParams(editLayoutParams);
        if(editHint!=null){
            editext.setHint(editHint);
        }
        editext.setHintTextColor(editHintColor);
        if(editContent != null){
            editext.setText(editContent);
        }
        
        editext.setTextColor(editContentColor);
        editext.setPadding(20, 10, 20, 10);
        editext.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        editext.setBackground(Backgroundss);
        if(textTF!=null){
            editext.setTypeface(textTF);
        }
        

// 设置长按监听器
        editext.setOnLongClickListener(new View.OnLongClickListener() {
                PopupMenu popupMenu;

                @Override
                public boolean onLongClick(View v) {
                    if(popupMenu==null){
                        // 创建菜单
                        popupMenu = new PopupMenu(aContext, v);
                        Menu menu = popupMenu.getMenu();

                        menu.add(Menu.NONE, 95, Menu.NONE, "全选"); // 添加全选选项
                        menu.add(Menu.NONE, 96, Menu.NONE, "复制"); // 添加复制选项
                        menu.add(Menu.NONE, 97, Menu.NONE, "粘贴"); // 添加粘贴选项
                        menu.add(Menu.NONE, 98, Menu.NONE, "剪切"); // 添加剪切选项


                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                ClipboardManager clipboardManager = (ClipboardManager) aContext.getSystemService(Context.CLIPBOARD_SERVICE);

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    ClipData clipData = ClipData.newPlainText("text", editext.getText());

                                    switch (item.getItemId()) {
                                        case 95:
                                            //Log.d("艾琳测试","全选");
                                            // 执行全选操作
                                            editext.selectAll();

                                            return true;
                                        case 96:
                                            //Log.d("艾琳测试","复制");
                                            // 执行复制操作
                                            clipboardManager.setPrimaryClip(clipData);


                                            return true;
                                        case 97:
                                            //Log.d("艾琳测试","粘贴");
                                            // 执行粘贴操作

                                            // 获取剪贴板中的文本
                                            if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                                editext.setText(""); // 清空 EditText 中的文本
                                                ClipData.Item cItem = clipboardManager.getPrimaryClip().getItemAt(0);
                                                String pasteText = cItem.getText().toString();
                                                // 将粘贴的文本插入到 EditText 中
                                                editext.getText().insert(editext.getSelectionStart(), pasteText);
                                            }

                                            return true;
                                        case 98:
                                            //Log.d("艾琳测试","剪切");
                                            // 执行剪切操作
                                            clipboardManager.setPrimaryClip(clipData);
                                            editext.setText(""); // 清空 EditText 中的文本


                                            return true;

                                        default:
                                            return false;
                                    }

                                }
                            });

                    }
                    popupMenu.show();




                    return false;
                }
            });


        //输入框输入文本监听器

        editext.addTextChangedListener
        (
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    // 在文本变化之前执行的操作
                    if(fun!=null){
                        fun.beforeTextChanged(editext,charSequence,start,count,after);
                    }

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // 在文本变化时执行的操作
                    if(fun!=null){
                        fun.onTextChanged(editext,charSequence,start,before,count);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // 在文本变化之后执行的操作              
                    if(fun!=null){
                        fun.afterTextChanged(editext,editable);
                    }

                }
            }
        );



        //小按钮
        final LinearLayout button = addSmallButton
        (

            buttonText, textSize, buttonTextColor, textTF,//普通按钮文本，文本大小，文本颜色
            buttonCircleRadius,//普通按钮圆角半径
            buttonBackColor,//普通按钮颜色
            buttonBorderSize, buttonBorderColor,//普通按钮描边大小，描边颜色
            //普通按钮点击事件
            new AlGui.T_ButtonOnChangeListener(){
                @Override// button=普通按钮对象 back=按钮外观对象 buttonText=按钮文本视图对象 isChecked=开关状态
                public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                    if (fun != null) {
                        fun.buttonOnClick(editext, button, buttonText, isChecked);
                    }

                }
            }
        );
        //重制小按钮布局参数确保与输入框位置一致
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(buttonLayoutParams);


        layout.addView(editext);
        layout.addView(button);
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(layout);
        }
        return editext;
    }
    
    
    
    
    
    

    //增加一个带按钮的输入框
    public LinearLayout addEditText(
        float textSize, Typeface textTF,//输入框和按钮的文本大小，输入框和按钮的字体
        //输入框提示内容颜色，输入框提示内容, 输入框输入内容颜色, 输入框默认输入内容, 输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色
        int editHintColor, CharSequence editHint, int editContentColor, CharSequence editContent, float editCircleRadius, int editBackColor, float editBorderSize, int editBorderColor,
        //按钮文本颜色，按钮文本，按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色
        int buttonTextColor, CharSequence buttonText, float buttonCircleRadius, int buttonBackColor, float buttonBorderSize, int buttonBorderColor,
        final T_EditTextOnChangeListener fun) {//输入框事件监听器

        //根布局
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        LinearLayout layout=new LinearLayout(aContext);
        layout.setLayoutParams(layoutParams);
        layout.setClipChildren(true);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setId(AlGuiData.AlguiView.EditText.getId());
        
     


        //输入框
        LinearLayout.LayoutParams editLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                   LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        editLayoutParams.setMargins(0, 0,8, 0);
        GradientDrawable Backgroundss = new GradientDrawable();
        Backgroundss.setCornerRadius(ViewTool.convertDpToPx(aContext, editCircleRadius));
        Backgroundss.setColor(editBackColor);
        Backgroundss.setStroke(ViewTool.convertDpToPx(aContext, editBorderSize), editBorderColor);
        final EditText editext = new EditText(aContext);
      // editext.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        editext.setTextIsSelectable(true);//启用输入框文本选择功能
        //设置内容过多时光标拖动时查看之前输入的内容是横向的
        editext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE); // 支持多行输入
        editext.setLayoutParams(editLayoutParams);
        if(editHint!=null){
            editext.setHint(editHint);
        }
        editext.setHintTextColor(editHintColor);
        if(editContent != null){
            editext.setText(editContent);
        }
        editext.setTextColor(editContentColor);
        editext.setPadding(20, 10, 20, 10);
        editext.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        editext.setBackground(Backgroundss);
        if(editext!=null){
            editext.setTypeface(textTF);
        }
         
        
// 设置长按监听器
       editext.setOnLongClickListener(new View.OnLongClickListener() {
           PopupMenu popupMenu;
                
                @Override
                public boolean onLongClick(View v) {
                    if(popupMenu==null){
                        // 创建菜单
                        popupMenu = new PopupMenu(aContext, v);
                        Menu menu = popupMenu.getMenu();
                        
                        menu.add(Menu.NONE, 95, Menu.NONE, "全选"); // 添加全选选项
                        menu.add(Menu.NONE, 96, Menu.NONE, "复制"); // 添加复制选项
                        menu.add(Menu.NONE, 97, Menu.NONE, "粘贴"); // 添加复制选项
                        menu.add(Menu.NONE, 98, Menu.NONE, "剪切"); // 添加剪切选项
                    
                        
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                ClipboardManager clipboardManager = (ClipboardManager) aContext.getSystemService(Context.CLIPBOARD_SERVICE);

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    ClipData clipData = ClipData.newPlainText("text", editext.getText());

                                    switch (item.getItemId()) {
                                        case 95:
                                            //Log.d("艾琳测试","全选");
                                            // 执行全选操作
                                            editext.selectAll();
                                           
                                            return true;
                                        case 96:
                                            //Log.d("艾琳测试","复制");
                                            // 执行复制操作
                                            clipboardManager.setPrimaryClip(clipData);
                                         
                                            
                                            return true;
                                        case 97:
                                            //Log.d("艾琳测试","粘贴");
                                            // 执行粘贴操作
                                            
                                            // 获取剪贴板中的文本
                                            if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                                editext.setText(""); // 清空 EditText 中的文本
                                                ClipData.Item cItem = clipboardManager.getPrimaryClip().getItemAt(0);
                                                String pasteText = cItem.getText().toString();
                                                // 将粘贴的文本插入到 EditText 中
                                                editext.getText().insert(editext.getSelectionStart(), pasteText);
                                            }
                                            
                                            return true;
                                        case 98:
                                            //Log.d("艾琳测试","剪切");
                                            // 执行剪切操作
                                            clipboardManager.setPrimaryClip(clipData);
                                            editext.setText(""); // 清空 EditText 中的文本
                                          
                                            
                                            return true;

                                        default:
                                            return false;
                                    }

                                }
                            });
                            
                    }
                        popupMenu.show();
                        
                 
                    
                    
                    return false;
                }
           });
                

        //输入框输入文本监听器
        
            editext.addTextChangedListener
            (
            new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作
                        if(fun!=null){
                            fun.beforeTextChanged(editext,charSequence,start,count,after);
                        }
                        
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作
                        if(fun!=null){
                            fun.onTextChanged(editext,charSequence,start,before,count);
                        }
                        
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // 在文本变化之后执行的操作              
                        if(fun!=null){
                            fun.afterTextChanged(editext,editable);
                        }

                    }
                    }
             );
        


        //小按钮
        final LinearLayout button = addSmallButton
        (

            buttonText, textSize, buttonTextColor, textTF,//普通按钮文本，文本大小，文本颜色
            buttonCircleRadius,//普通按钮圆角半径
            buttonBackColor,//普通按钮颜色
            buttonBorderSize, buttonBorderColor,//普通按钮描边大小，描边颜色
            //普通按钮点击事件
            new AlGui.T_ButtonOnChangeListener(){
                @Override// button=普通按钮对象 back=按钮外观对象 buttonText=按钮文本视图对象 isChecked=开关状态
                public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                    if (fun != null) {
                        fun.buttonOnClick(editext, button, buttonText, isChecked);
                    }

                }
            }
        );
        //重制小按钮布局参数确保与输入框位置一致
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(buttonLayoutParams);


        layout.addView(editext);
        layout.addView(button);
        return layout;
    }
    //输入框事件监听器
    public static interface T_EditTextOnChangeListener {
        //在文本变化之前执行
        public abstract void beforeTextChanged(EditText editText ,CharSequence charSequence, int start, int count, int after);
        //在文本变化时执行
        public abstract void onTextChanged(EditText editText ,CharSequence charSequence, int start, int before, int count);
        //在文本变化之后执行的操作
        public abstract void afterTextChanged(EditText editText,Editable editable) ;
        //点击按钮就执行
        public abstract void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked);
        
        
    }



    //增加一个小数拖动条 (拖动float值)
    public SeekBar addSeekBarFloat(
    ViewGroup parentLayout,//父布局
        final CharSequence text, float textSize,  int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        float minValue, float  defaultValue, float maxValue,//最小进度，初始进度，最大进度
        int roundColor, int offProgressColor, int onProgressColor,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
        final T_SeekBarFloatOnChangeListener fun) {//事件监听器
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);

        //根布局
        LinearLayout layout = new LinearLayout(aContext);
        layout.setLayoutParams(layoutParams);
        layout.setClipChildren(true);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setId(AlGuiData.AlguiView.SeekBarFloat.getId());
        

        // 创建拖动条
        SeekBar seekBar = new SeekBar(aContext);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                              LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        // 将最小值、默认值和最大值都放大10倍以便支持小数
        float min =  minValue * 10;
        float initProgress =  defaultValue * 10;
        float max =  maxValue * 10;

        /* 已踩坑😆
         SeekBar 拖动条设置最小进度和设置最大进度的书写顺序很重要！坑了我半天
         如果你先设置最小进度再设置最大进度，将舍去一位
         比如设置最小进度为 1000 再设置最大进度为 5000
         之后你会发现拖动条最小进度是 100 舍去了一位数
         如果先设置最大进度再设置最小进度就是正常的
         */
        seekBar.setMax((int)max); // 设置最大进度
        seekBar.setMin((int)min); // 设置最小进度

        if (initProgress < min) {
            //初始进度比最小值还小则拉回设置初始进度为最小值
            seekBar.setProgress((int)min); // 设置初始进度
        } else if (initProgress > max) {
            //初始进度比最大值还大则拉回设置初始进度为最大值
            seekBar.setProgress((int)max); // 设置初始进度
        } else {
            //否则没有超出范围 设置为传入的初始进度
            seekBar.setProgress((int)initProgress); // 设置初始进度
        }
        seekBar.setProgress((int)initProgress); // 设置初始进度


        // 设置拖动条圆的颜色
        seekBar.setThumbTintList(ColorStateList.valueOf(roundColor));
        // 设置拖动条未拖动的进度颜色
        seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(offProgressColor));
        // 设置拖动条拖动后的进度颜色
        seekBar.setProgressTintList(ColorStateList.valueOf(onProgressColor));


        //拖动条文本
        final TextView textView = new TextView(aContext);
        //  textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                               LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        textView.setTextColor(textColor);
        
        if(text!=null){
            textView.setText("•" + text  + "：" + (seekBar.getProgress()/10.0f));
        }

        if (textTF != null) {
            textView.setTypeface(textTF);
        }





        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 当拖动条进度改变时调用
                    // 可以在这里执行相应的操作
                    // 计算当前的小数值
                    float value = progress  / 10.0f;
                    if(text!=null){
                        textView.setText("•" + text  + "：" + value);
                    }
                    if (fun != null) {
                        fun.onProgressChanged(textView, seekBar, value, fromUser);
                    }



                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // 当开始拖动拖动条时调用
                    if (fun != null) {
                        fun.onStartTrackingTouch(textView, seekBar,seekBar.getProgress()/10.0f);
                    }

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // 当停止拖动拖动条时调用
                    if (fun != null) {
                        fun.onStopTrackingTouch(textView, seekBar,seekBar.getProgress()/10.0f);
                    }

                }
            });


        layout.addView(textView);
        layout.addView(seekBar);

        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(layout);
        }
        return seekBar;
    }


    //增加一个小数拖动条 (拖动float值)
    public LinearLayout addSeekBarFloat(
        final CharSequence text, float textSize,  int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
       float minValue, float  defaultValue, float maxValue,//最小进度，初始进度，最大进度
        int roundColor, int offProgressColor, int onProgressColor,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
        final T_SeekBarFloatOnChangeListener fun) {//事件监听器
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);

        //根布局
        LinearLayout layout = new LinearLayout(aContext);
        layout.setLayoutParams(layoutParams);
        layout.setClipChildren(true);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setId(AlGuiData.AlguiView.SeekBarFloat.getId());
        

        // 创建拖动条
        SeekBar seekBar = new SeekBar(aContext);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                              LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
      
        // 将最小值、默认值和最大值都放大10倍以便支持小数
        float min =  minValue * 10;
        float initProgress =  defaultValue * 10;
        float max =  maxValue * 10;
        
        /* 已踩坑😆
         SeekBar 拖动条设置最小进度和设置最大进度的书写顺序很重要！坑了我半天
         如果你先设置最小进度再设置最大进度，将舍去一位
         比如设置最小进度为 1000 再设置最大进度为 5000
         之后你会发现拖动条最小进度是 100 舍去了一位数
         如果先设置最大进度再设置最小进度就是正常的
         */
        seekBar.setMax((int)max); // 设置最大进度
        seekBar.setMin((int)min); // 设置最小进度

        if (initProgress < min) {
            //初始进度比最小值还小则拉回设置初始进度为最小值
            seekBar.setProgress((int)min); // 设置初始进度
        } else if (initProgress > max) {
            //初始进度比最大值还大则拉回设置初始进度为最大值
            seekBar.setProgress((int)max); // 设置初始进度
        } else {
            //否则没有超出范围 设置为传入的初始进度
            seekBar.setProgress((int)initProgress); // 设置初始进度
        }
        seekBar.setProgress((int)initProgress); // 设置初始进度
        

        // 设置拖动条圆的颜色
        seekBar.setThumbTintList(ColorStateList.valueOf(roundColor));
        // 设置拖动条未拖动的进度颜色
        seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(offProgressColor));
        // 设置拖动条拖动后的进度颜色
        seekBar.setProgressTintList(ColorStateList.valueOf(onProgressColor));


        //拖动条文本
        final TextView textView = new TextView(aContext);
      //  textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                               LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        textView.setTextColor(textColor);
        
        if(text!=null){
            textView.setText("•" + text  + "：" + (seekBar.getProgress()/10.0f));
        }
        
        if (textTF != null) {
            textView.setTypeface(textTF);
        }





        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 当拖动条进度改变时调用
                    // 可以在这里执行相应的操作
                    // 计算当前的小数值
                    float value = progress  / 10.0f;
                    if(text!=null){
                        textView.setText("•" + text  + "：" + value);
                    }
                    if (fun != null) {
                    fun.onProgressChanged(textView, seekBar, value, fromUser);
                    }
                    


                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // 当开始拖动拖动条时调用
                    if (fun != null) {
                        fun.onStartTrackingTouch(textView, seekBar,seekBar.getProgress()/10.0f);
                    }

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // 当停止拖动拖动条时调用
                    if (fun != null) {
                        fun.onStopTrackingTouch(textView, seekBar,seekBar.getProgress()/10.0f);
                    }

                }
            });


        layout.addView(textView);
        layout.addView(seekBar);


        return layout;
    }
    //小数拖动条事件监听器
    public static interface T_SeekBarFloatOnChangeListener {
        //当拖动条进度改变时调用
        public abstract void onProgressChanged(TextView textView, SeekBar seekBar, float progress, boolean fromUser);
        //当开始拖动拖动条时调用
        public abstract void onStartTrackingTouch(TextView textView, SeekBar seekBar,float progress);
        //当停止拖动拖动条时调用
        public abstract void onStopTrackingTouch(TextView textView, SeekBar seekBar,float progress);
    }



    
    
    //增加一个整数拖动条 (拖动int值)
    public SeekBar addSeekBarInt(
    ViewGroup parentLayout,//父布局
        final CharSequence text, float textSize,  int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        int min, int  initProgress,  int max,//最小进度，初始进度，最大进度
        int roundColor, int offProgressColor, int onProgressColor,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
        final T_SeekBarIntOnChangeListener fun) {//事件监听器
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);

        //根布局
        LinearLayout layout = new LinearLayout(aContext);
        layout.setLayoutParams(layoutParams);
        layout.setClipChildren(true);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setId(AlGuiData.AlguiView.SeekBarInt.getId());
        

        // 创建拖动条
        SeekBar seekBar = new SeekBar(aContext);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                              LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        /* 已踩坑😆
         SeekBar 拖动条设置最小进度和设置最大进度的书写顺序很重要！坑了我半天
         如果你先设置最小进度再设置最大进度，将舍去一位
         比如设置最小进度为 1000 再设置最大进度为 5000
         之后你会发现拖动条最小进度是 100 舍去了一位数
         如果先设置最大进度再设置最小进度就是正常的
         */
        seekBar.setMax(max); // 设置最大进度
        seekBar.setMin(min); // 设置最小进度

        if (initProgress < min) {
            //初始进度比最小值还小则拉回设置初始进度为最小值
            seekBar.setProgress(min); // 设置初始进度
        } else if (initProgress > max) {
            //初始进度比最大值还大则拉回设置初始进度为最大值
            seekBar.setProgress(max); // 设置初始进度
        } else {
            //否则没有超出范围 设置为传入的初始进度
            seekBar.setProgress(initProgress); // 设置初始进度
        }

        // 设置拖动条圆的颜色
        seekBar.setThumbTintList(ColorStateList.valueOf(roundColor));
        // 设置拖动条未拖动的进度颜色
        seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(offProgressColor));
        // 设置拖动条拖动后的进度颜色
        seekBar.setProgressTintList(ColorStateList.valueOf(onProgressColor));


        //拖动条文本
        final TextView textView = new TextView(aContext);
        //textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                               LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        textView.setTextColor(textColor);
        
        if(text!=null){
            textView.setText("•" + text  + "：" + seekBar.getProgress());
        }
        if (textTF != null) {
            textView.setTypeface(textTF);
        }





        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 当拖动条进度改变时调用
                    // 可以在这里执行相应的操作
                    if(text!=null){
                        textView.setText("•" + text  + "：" + progress);
                    }

                    if (fun != null) {
                        fun.onProgressChanged(textView, seekBar, progress, fromUser);
                    }


                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // 当开始拖动拖动条时调用
                    if (fun != null) {
                        fun.onStartTrackingTouch(textView, seekBar, seekBar.getProgress());
                    }

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // 当停止拖动拖动条时调用
                    if (fun != null) {
                        fun.onStopTrackingTouch(textView, seekBar,seekBar.getProgress());
                    }

                }
            });


        layout.addView(textView);
        layout.addView(seekBar);

        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(layout);
        }
        return seekBar;
    }
    
    
    //增加一个整数拖动条 (拖动int值)
    public LinearLayout addSeekBarInt(
        final CharSequence text, float textSize,  int textColor, Typeface textTF,//文本，文本大小，文本颜色，文本字体
        int min, int  initProgress,  int max,//最小进度，初始进度，最大进度
        int roundColor, int offProgressColor, int onProgressColor,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
        final T_SeekBarIntOnChangeListener fun) {//事件监听器
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);

        //根布局
        LinearLayout layout = new LinearLayout(aContext);
        layout.setLayoutParams(layoutParams);
        layout.setClipChildren(true);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setId(AlGuiData.AlguiView.SeekBarInt.getId());
        

        // 创建拖动条
        SeekBar seekBar = new SeekBar(aContext);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                              LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        
        /* 已踩坑😆
         SeekBar 拖动条设置最小进度和设置最大进度的书写顺序很重要！坑了我半天
         如果你先设置最小进度再设置最大进度，将舍去一位
         比如设置最小进度为 1000 再设置最大进度为 5000
         之后你会发现拖动条最小进度是 100 舍去了一位数
         如果先设置最大进度再设置最小进度就是正常的
        */
        seekBar.setMax(max); // 设置最大进度
        seekBar.setMin(min); // 设置最小进度

        if (initProgress < min) {
            //初始进度比最小值还小则拉回设置初始进度为最小值
            seekBar.setProgress(min); // 设置初始进度
        } else if (initProgress > max) {
            //初始进度比最大值还大则拉回设置初始进度为最大值
            seekBar.setProgress(max); // 设置初始进度
        } else {
            //否则没有超出范围 设置为传入的初始进度
            seekBar.setProgress(initProgress); // 设置初始进度
        }

        // 设置拖动条圆的颜色
        seekBar.setThumbTintList(ColorStateList.valueOf(roundColor));
        // 设置拖动条未拖动的进度颜色
        seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(offProgressColor));
        // 设置拖动条拖动后的进度颜色
        seekBar.setProgressTintList(ColorStateList.valueOf(onProgressColor));


        //拖动条文本
        final TextView textView = new TextView(aContext);
        //textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                               LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        textView.setTextColor(textColor);
        
        if(text!=null){
            textView.setText("•" + text  + "：" + seekBar.getProgress());
        }
        if (textTF != null) {
            textView.setTypeface(textTF);
        }





        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 当拖动条进度改变时调用
                    // 可以在这里执行相应的操作
                    if(text!=null){
                        textView.setText("•" + text  + "：" + progress);
                    }
                    
                    if (fun != null) {
                        fun.onProgressChanged(textView, seekBar, progress, fromUser);
                    }


                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // 当开始拖动拖动条时调用
                    if (fun != null) {
                        fun.onStartTrackingTouch(textView, seekBar, seekBar.getProgress());
                    }

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // 当停止拖动拖动条时调用
                    if (fun != null) {
                        fun.onStopTrackingTouch(textView, seekBar,seekBar.getProgress());
                    }

                }
            });


        layout.addView(textView);
        layout.addView(seekBar);


        return layout;
    }
    //整数拖动条事件监听器
    public static interface T_SeekBarIntOnChangeListener {
        //当拖动条进度改变时调用
        public abstract void onProgressChanged(TextView textView, SeekBar seekBar, int progress, boolean fromUser);
        //当开始拖动拖动条时调用
        public abstract void onStartTrackingTouch(TextView textView, SeekBar seekBar, int progress);
        //当停止拖动拖动条时调用
        public abstract void onStopTrackingTouch(TextView textView, SeekBar seekBar, int progress);
    }




    //增加一个复选框
    public CheckBox addCheckBox(
    ViewGroup parentLayout,//父布局
        final CharSequence text, float textSize,  int textColor, Typeface textTF,//文本，文本大小，文本颜色
        int checkBoxColor,//复选框颜色
        final T_CheckBoxOnChangeListener fun) {//复选框监听事件
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        CheckBox checkBox = new CheckBox(aContext);
        //checkBox.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        checkBox.setLayoutParams(layoutParams);
        checkBox.setId(AlGuiData.AlguiView.CheckBox.getId());
        
        if (textTF != null) {
            checkBox.setTypeface(textTF);
        }
        if(text!=null){
            checkBox.setText(text);
        }


        checkBox.setTextColor(textColor);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        if (checkBoxColor != 0) {
            checkBox.setButtonTintList(ColorStateList.valueOf(checkBoxColor));  // 修改复选框按钮颜色
        }
        //设置复选框的选中状态监听器
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (fun != null) {
                        fun.onClick(buttonView, isChecked);
                    }

                }
            });
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(checkBox);
        }
        return checkBox;
    }

    //增加一个复选框
    public CheckBox addCheckBox(
        final CharSequence text, float textSize,  int textColor, Typeface textTF,//文本，文本大小，文本颜色
        int checkBoxColor,//复选框颜色
        final T_CheckBoxOnChangeListener fun) {//复选框监听事件
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        CheckBox checkBox = new CheckBox(aContext);
        //checkBox.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        checkBox.setLayoutParams(layoutParams);
        checkBox.setId(AlGuiData.AlguiView.CheckBox.getId());
       
        if (textTF != null) {
            checkBox.setTypeface(textTF);
        }
        if(text!=null){
            checkBox.setText(text);
        }
        
        
        checkBox.setTextColor(textColor);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        if (checkBoxColor != 0) {
            checkBox.setButtonTintList(ColorStateList.valueOf(checkBoxColor));  // 修改复选框按钮颜色
        }
        //设置复选框的选中状态监听器
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (fun != null) {
                        fun.onClick(buttonView, isChecked);
                    }

                }
            });
        return checkBox;
    }
    //复选框事件监听器
    public static interface T_CheckBoxOnChangeListener {
        //点击
        public abstract void onClick(CompoundButton buttonView, boolean isChecked);
    }


    //增加一个web自定义网络视图 (具有完全的html支持)
    public WebView addWebView(
    ViewGroup parentLayout,//父布局
        String text//网络代码
    ) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
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
        wView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    /* AlGuiBubbleNotification.Inform(aContext).showMistakeNotification_Simplicity(null, "无法加载", "我们将自动跳转到第三方游览器来加载！", 5000);
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
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(wView);
        }
        return wView;
    }
    
    //增加一个web自定义网络视图 (具有完全的html支持)
    public WebView addWebView(
        String text//网络代码
    ) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
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
        wView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                   /* AlGuiBubbleNotification.Inform(aContext).showMistakeNotification_Simplicity(null, "无法加载", "我们将自动跳转到第三方游览器来加载！", 5000);
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
    
    //增加一个web自定义网络视图 (具有完全的html支持) 重载&
    public WebView addWebView(
    ViewGroup parentLayout,//父布局
        String text,//网络代码
        float width,//视图宽度
        float height,//视图高度
        int backColor//背景颜色
    ) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewTool.convertDpToPx(aContext, width),
                                                                               ViewTool.convertDpToPx(aContext, height),100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        WebView wView = new WebView(aContext);
        wView.setId(AlGuiData.AlguiView.WebView.getId());
        wView.setLayoutParams(layoutParams);
        wView.setBackgroundColor(backColor); //设置背景颜色

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
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(wView);
        }
        return wView;
    }
    
    //增加一个web自定义网络视图 (具有完全的html支持) 重载&
    public WebView addWebView(
        String text,//网络代码
        float width,//视图宽度
        float height,//视图高度
        int backColor//背景颜色
    ) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewTool.convertDpToPx(aContext, width),
                                                                               ViewTool.convertDpToPx(aContext, height),100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        WebView wView = new WebView(aContext);
        wView.setId(AlGuiData.AlguiView.WebView.getId());
        wView.setLayoutParams(layoutParams);
        wView.setBackgroundColor(backColor); //设置背景颜色
    
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
    
    //增加一个web网站视图
    public WebView addWebSite(
    ViewGroup parentLayout,//父布局
        String url//网站链接
    ) {
        WebView webSite=addWebView(null);
        webSite.setId(AlGuiData.AlguiView.WebSite.getId());
        if (url == null) {
            return webSite;
        }

        webSite.loadUrl(url);
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(webSite);
        }
        return  webSite;
    }
   
    //增加一个web网站视图
    public WebView addWebSite(
        String url//网站链接
    ) {
        WebView webSite=addWebView(null);
        webSite.setId(AlGuiData.AlguiView.WebSite.getId());
        if (url == null) {
            return webSite;
        }
        
        webSite.loadUrl(url);
       
        return  webSite;
    }
    
    //增加一个文本视图
    public TextView addTextView(
    ViewGroup parentLayout,//父布局
        
        CharSequence text, //文本
        float textSize, //文本大小
        int textColor, //文本颜色
        Typeface tf) { //文本字体
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        TextView textView=new TextView(aContext);
        textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setLayoutParams(layoutParams);
        if(text!=null){
            textView.setText(text);
        }
       
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        textView.setTextColor(textColor);
        if (tf != null) {
            textView.setTypeface(tf);
        }
       
        textView.setId(AlGuiData.AlguiView.TextView.getId());
        //textView.setGravity(Gravity.CENTER);
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(textView);
        }
        return textView;
    }

    //增加一个文本视图
    public TextView addTextView(
        
        CharSequence text, //文本
        float textSize, //文本大小
        int textColor, //文本颜色
        Typeface tf) { //文本字体
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        TextView textView=new TextView(aContext);
        textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setLayoutParams(layoutParams);
        if(text!=null){
            textView.setText(text);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        textView.setTextColor(textColor);
        if (tf != null) {
            textView.setTypeface(tf);
        }
       
        textView.setId(AlGuiData.AlguiView.TextView.getId());
        textView.setGravity(Gravity.CENTER);
      
        return textView;
    }
    
    //增加一个可以滚动的字幕文本视图
    public MarqueeTextView addMarqueeTextView
    (
    ViewGroup parentLayout,//父布局
        int gravity, //对齐方式
        CharSequence text, //文本
        float textSize, //文本大小
        int textColor, //文本颜色
        Typeface tf,//文本字体
        long speed,//滚动一次的时间 单位毫秒
        int RepeatNum,//重复次数
        boolean isSingleLine//是否只显示一行
    ) { 
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);

        HorizontalScrollView layout = new HorizontalScrollView(aContext);
        layout.setLayoutParams(layoutParams);
        layout.setHorizontalScrollBarEnabled(false);
        layout.setVerticalScrollBarEnabled(false);
        layout.setBackgroundColor(0);



        MarqueeTextView textView=new MarqueeTextView(aContext);
        textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setLayoutParams(layoutParams);
        if(text!=null){
            textView.setText(text);
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        textView.setTextColor(textColor);
        if (tf != null) {
            textView.setTypeface(tf);
        }


        textView.setGravity(gravity);
        textView.setId(AlGuiData.AlguiView.MarqueeTextView.getId());

        textView.setSingleLine(isSingleLine);//是否只显示一行
        textView.getAnimation().setDuration(speed); // 设置滚动一次的时间 单位毫秒
        textView.getAnimation().setRepeatCount(RepeatNum); // 设置重复次数，这里设置为无限
        textView.getAnimation().setInterpolator(new LinearInterpolator()); // 设置动画插值器，使滚动平滑
        textView.startAnimation(textView.getAnimation());//启动动画

        layout.addView(textView);
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(layout);
        }
        return textView;
    }

    //增加一个可以滚动的字幕文本视图
    public HorizontalScrollView addMarqueeTextView
    (
        int gravity, //对齐方式
        CharSequence text, //文本
        float textSize, //文本大小
        int textColor, //文本颜色
        Typeface tf,//文本字体
        long speed,//滚动一次的时间 单位毫秒
        int RepeatNum,//重复次数
        boolean isSingleLine//是否只显示一行
    ) { 
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                               LinearLayout.LayoutParams.WRAP_CONTENT,100f);
        layoutParams.setMargins(viewWBJ[0], viewWBJ[1], viewWBJ[2], viewWBJ[3]);
        
        HorizontalScrollView layout = new HorizontalScrollView(aContext);
        layout.setLayoutParams(layoutParams);
        layout.setHorizontalScrollBarEnabled(false);
        layout.setVerticalScrollBarEnabled(false);
        layout.setBackgroundColor(0);
        
    
        
        MarqueeTextView textView=new MarqueeTextView(aContext);
        textView.setMovementMethod(LinkMovementMethod.getInstance());//启动文本点击链接可响应
        textView.setLayoutParams(layoutParams);
        if(text!=null){
            textView.setText(text);
        }
        
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(aContext, textSize));
        textView.setTextColor(textColor);
        if (tf != null) {
            textView.setTypeface(tf);
        }
      

        textView.setGravity(gravity);
        textView.setId(AlGuiData.AlguiView.MarqueeTextView.getId());

        textView.setSingleLine(isSingleLine);//是否只显示一行
        textView.getAnimation().setDuration(speed); // 设置滚动一次的时间 单位毫秒
        textView.getAnimation().setRepeatCount(RepeatNum); // 设置重复次数，这里设置为无限
        textView.getAnimation().setInterpolator(new LinearInterpolator()); // 设置动画插值器，使滚动平滑
        textView.startAnimation(textView.getAnimation());//启动动画

        layout.addView(textView);
        return layout;
    }

    
    
    
    //增加一条线
    public View addLine(
    ViewGroup parentLayout,//父布局
        float Thickness,//线的厚度
        int color, //线的颜色
        boolean isTransverse) {//线的方向(true=横向 false=竖向)
        View line = new View(aContext);
        line.setBackgroundColor(color);
        // 设置线条的宽度和高度
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
        (
            isTransverse ? ViewGroup.LayoutParams.MATCH_PARENT: ViewTool.convertDpToPx(aContext, Thickness)
            ,
            isTransverse ? ViewTool.convertDpToPx(aContext, Thickness): ViewGroup.LayoutParams.MATCH_PARENT
            ,
            isTransverse ? 100f:0
        ); 
        line.setLayoutParams(layoutParams);
        line.setId(AlGuiData.AlguiView.Line.getId());
        //父布局添加此视图
        if(parentLayout!=null){
            parentLayout.addView(line);
        }
        return line;
    }
    
    
    //增加一条线
    public View addLine(
        float Thickness,//线的厚度
        int color, //线的颜色
        boolean isTransverse) {//线的方向(true=横向 false=竖向)
        View line = new View(aContext);
        line.setBackgroundColor(color);
        // 设置线条的宽度和高度
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
        (
            isTransverse ? ViewGroup.LayoutParams.MATCH_PARENT: ViewTool.convertDpToPx(aContext, Thickness)
            ,
            isTransverse ? ViewTool.convertDpToPx(aContext, Thickness): ViewGroup.LayoutParams.MATCH_PARENT
            ,
            isTransverse ? 100f:0
        ); 
        line.setLayoutParams(layoutParams);
        line.setId(AlGuiData.AlguiView.Line.getId());
        
        return line;
    }

}
