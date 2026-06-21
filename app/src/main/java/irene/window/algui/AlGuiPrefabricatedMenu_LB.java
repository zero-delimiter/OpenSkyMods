package irene.window.algui;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import irene.window.algui.CustomizeView.MarqueeTextView;
import irene.window.algui.CustomizeView.vLinearLayout;
import irene.window.algui.Tools.AppPermissionTool;
import irene.window.algui.Tools.AppTool;
import irene.window.algui.Tools.RealTimeDataTextTool;
import irene.window.algui.Tools.SystemTool;
import irene.window.algui.Tools.VariousTools;
import irene.window.algui.Tools.ViewTool;
import java.util.Map;
import android.content.res.ColorStateList;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/24 08:45
 * @Describe UI预制菜单 (ALGUI内部组件)
 */
public class AlGuiPrefabricatedMenu_LB {

    public static final String TAG = "AlGuiPrefabricatedMenu_LB";
    private Context context;
    private AlGui algui;


    protected AlGuiPrefabricatedMenu_LB(Context mContext, AlGui aui) {
        if (mContext == null||aui==null) {
            return;
        }
      
        context = mContext;
        algui = aui;
    }
    
    
   
    //说明
    public vLinearLayout addExplanation(ViewGroup larentLayout) {
        if (context == null||algui==null) {
            return null;
        }
        //在悬浮窗内部滚动布局中增加一个折叠菜单
        vLinearLayout sam = algui.addCollapse
        (   //折叠菜单属性
            "使用说明", 10, 0xFF000000, null,//折叠菜单文本，文本大小，文本颜色，文本字体(null代表默认)
            3,//折叠菜单圆角半径
            0xFFFFFFFF,//折叠菜单背景颜色
            0, 0xFFC5CAE9,//折叠菜单描边大小，描边颜色
            true,//折叠菜单默认是否展开，true=默认展开，false=默认不展开
            //↓折叠菜单中的视图↓
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.VERTICAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
                algui.addLine(0.5f, 0xFFE0E0E0, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
                algui.addTextView( "使用说明", 11, 0xFF000000, Typeface.create(Typeface.DEFAULT, Typeface.BOLD)),
                algui.addLine(0.5f, 0xFFE0E0E0, true),
                algui.addTextView(Html.fromHtml( "本项目由 <font color='#EF5350'>麻省理工学院许可证</font> 进行发布和分发"), 11, 0xFF000000, null)
            ),
            
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml( "开发者：<font color='#424242'>艾琳 (唯一作者)</font>"), 11, 0xFF000000, null),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("开发者QQ：<a href=\"https://qm.qq.com/q/3U1H3JMf5Y\">3353484607</a>"), 11, 0xFF000000, null),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("交流群QQ：<a color='#3F51B5' href=\"mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3Dqeey7hum96m64eaHkKKjHC6micNY9_oI\">730967224</a>"), 11, 0xFF000000, null),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml( "当前版本：<font color='#424242'>v2.0 (正式版)</font>"), 11, 0xFF000000, null),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            
    
                
                algui.addButton//增加一个小按钮
                (
                    "使用文档", 11, 0xFFFFFFFF, null,//文本，文本大小，文本颜色，文本字体
                    5,//圆角半径
                    0xFF3F51B5,//背景颜色
                    0, 0xff000000,//描边大小，描边颜色
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,//按钮宽度，按钮高度
                    new  AlGui.T_ButtonOnChangeListener(){
                        @Override
                        public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked){
                            VariousTools.gotoWeb(context,"https://gitee.com/ByteAL/ALGUI");
                        }
                    }
                ),
                
            algui.addButton//增加一个小按钮
            (
                "更新GUI", 11, 0xFFFFFFFF, null,//文本，文本大小，文本颜色，文本字体
                5,//圆角半径
                0xFF3F51B5,//背景颜色
                0, 0xff000000,//描边大小，描边颜色
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,//按钮宽度，按钮高度
                new  AlGui.T_ButtonOnChangeListener(){
                    @Override
                    public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked){
                        VariousTools.gotoWeb(context,"https://www.123pan.com/s/RMOtVv-G8ijh.html");
                    }
                }
            ),
                
                algui.addButton//增加一个小按钮
                (
                    "作者B站", 11, 0xFFFFFFFF, null,//文本，文本大小，文本颜色，文本字体
                    5,//圆角半径
                    0xFF3F51B5,//背景颜色
                    0, 0xff000000,//描边大小，描边颜色
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,//按钮宽度，按钮高度
                    new  AlGui.T_ButtonOnChangeListener(){
                        @Override
                        public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked){
                            AlGuiBubbleNotification.Inform(context).showMessageNotification_Simplicity_Button
                            (
                                null,//图标(null代表默认)
                                "请选择打开方式",//标题(null代表无标题)
                                "你希望在内部直接打开，还是在外部游览器打开",//内容(null代表无内容)
                                "内部", //消极按钮文本 (null代表没有这个按钮)
                                //这是消极按钮的点击事件 (null代表默认)
                                new AlGuiBubbleNotification.T_ButtonOnChangeListener(){
                                    @Override// button=小按钮对象 back=小按钮外观对象 buttonText=小按钮文本视图对象 isChecked=小按钮开关状态
                                    public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                        if(AppPermissionTool.isAndroidManifestPermissionExist(context, "android.permission.INTERNET")){
                                            AlGuiWindowView.showWebSite(context,"B站","https://b23.tv/NoEaEXW");
                                        }else{
                                            AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "内部无法加载", "可能没有网络权限，我们将自动跳转到第三方游览器加载！", 5000);

                                            VariousTools.gotoWeb(context,"https://b23.tv/NoEaEXW");
                                        }
                                        
                                    }
                                },
                                "外部",//正极按钮的文本 (null代表没有某个按钮)
                                //这是正极按钮的点击事件 (null代表默认)
                                new AlGuiBubbleNotification.T_ButtonOnChangeListener(){
                                    @Override// button=小按钮对象 back=小按钮外观对象 buttonText=小按钮文本视图对象 isChecked=小按钮开关状态
                                    public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                        VariousTools.gotoWeb(context,"https://b23.tv/NoEaEXW");

                                    }
                                }
                            );

                        }
                    }
                ),
            
            algui.addButton//增加一个小按钮
            (
                "赞助作者(加入开发)", 11, 0xFFFFFFFF, null,//文本，文本大小，文本颜色，文本字体
                5,//圆角半径
                0xFF3F51B5,//背景颜色
                0, 0xff000000,//描边大小，描边颜色
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,//按钮宽度，按钮高度
                new  AlGui.T_ButtonOnChangeListener(){
                    @Override
                    public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked){
                        if(AppPermissionTool.isAndroidManifestPermissionExist(context, "android.permission.INTERNET")){
                            AlGuiWindowView.showWebView(context,
                                                        "赞助",
                                                        "<html><body><img src=\"https://img2.imgtp.com/2024/02/14/aOxtoFpY.png\"></img></body></html>");
                            
                        }else{
                            VariousTools.gotoWeb(context,"https://img2.imgtp.com/2024/02/14/aOxtoFpY.png");
                        }
                        
                    }
                }
            ),
            algui.addButton//增加一个小按钮
            (
                "交流社区(Bug反馈)", 11, 0xFFFFFFFF, null,//文本，文本大小，文本颜色，文本字体
                5,//圆角半径
                0xFF3F51B5,//背景颜色
                0, 0xff000000,//描边大小，描边颜色
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,//按钮宽度，按钮高度
                new  AlGui.T_ButtonOnChangeListener(){
                    @Override
                    public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked){

                        AlGuiBubbleNotification.Inform(context).showMessageNotification_Simplicity_Button
                        (
                            null,//图标(null代表默认)
                            "请选择打开方式",//标题(null代表无标题)
                            "你希望在内部直接打开，还是在外部游览器打开",//内容(null代表无内容)
                            "内部", //消极按钮文本 (null代表没有这个按钮)
                            //这是消极按钮的点击事件 (null代表默认)
                            new AlGuiBubbleNotification.T_ButtonOnChangeListener(){
                                @Override// button=小按钮对象 back=小按钮外观对象 buttonText=小按钮文本视图对象 isChecked=小按钮开关状态
                                public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                    if(AppPermissionTool.isAndroidManifestPermissionExist(context, "android.permission.INTERNET")){
                                        AlGuiWindowView.showWebSite(context,"社区","https://txc.qq.com/products/634245");
                                    }else{
                                        AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "内部无法加载", "可能没有网络权限，我们将自动跳转到第三方游览器加载！", 5000);
                                        
                                        VariousTools.gotoWeb(context,"https://txc.qq.com/products/634245");
                                    }
                                    
                                }
                            },
                            "外部",//正极按钮的文本 (null代表没有某个按钮)
                            //这是正极按钮的点击事件 (null代表默认)
                            new AlGuiBubbleNotification.T_ButtonOnChangeListener(){
                                @Override// button=小按钮对象 back=小按钮外观对象 buttonText=小按钮文本视图对象 isChecked=小按钮开关状态
                                public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                    VariousTools.gotoWeb(context,"https://txc.qq.com/products/634245");

                                }
                            }
                        );
                    }
                }
            ),
            // addWebView("<img src=\"" + "https://img2.imgtp.com/2024/01/28/3YREVoNy.png" + "\" width=48dp/>"),

            //addWebSite("https://cn.anotepad.com/"),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.HORIZONTAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView("Copyright © 2023 艾琳 版权所有", 9, 0xFF9E9E9E, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            )

        );

        if (larentLayout != null) {
            larentLayout.addView(sam);
        } 
        return sam;
    }
    

    //属性状态菜单专用的实时检测SELinux状态(强制模式/宽容模式) 因为属性菜单显示此实时数据时使用的字幕文本视图 类型不同因此我们为他单独封装了一个函数
    private HorizontalScrollView textAddSELinuxMode(HorizontalScrollView textView) {
        if (textView == null) {
            return textView;
        }
        final MarqueeTextView textView_A=textView.findViewById(AlGuiData.AlguiView.MarqueeTextView.getId());
        final String text=(textView_A.getText().toString() != null) ?textView_A.getText().toString(): "";
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
        return textView;
    }

    //显示属性状态折叠菜单 
    //参数：父布局
    public  vLinearLayout addAttributeStatusMenu(ViewGroup larentLayout) {
        if (context == null||algui==null) {
            return null;
        }
        //在悬浮窗内部滚动布局中增加一个折叠菜单
        vLinearLayout sam = algui.addCollapse
        (   //折叠菜单属性
            "属性状态", 10, 0xFF000000, null,//折叠菜单文本，文本大小，文本颜色，文本字体(null代表默认)
            3,//折叠菜单圆角半径
            0xFFFFFFFF,//折叠菜单背景颜色
            0, 0xFFC5CAE9,//折叠菜单描边大小，描边颜色
            false,//折叠菜单默认是否展开，true=默认展开，false=默认不展开

            //↓折叠菜单中的视图↓

            algui.addLine(0.5f, 0xFFE0E0E0, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.HORIZONTAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView("系统状态实时监测", 11, 0xFF000000, Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            ),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            RealTimeDataTextTool.textAddTime("yyyy年MM月dd日 HH:mm:ss EE", algui.addTextView("系统当前时间：", 11, 0xFF000000, null)),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            RealTimeDataTextTool.textAddPower(context, algui.addTextView( "系统实时电量：", 11, 0xFF000000, null)),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            RealTimeDataTextTool.textAddFps(algui.addTextView("系统实时帧率：", 11, 0xFF000000, null)),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml("<hr>系统总共内存：<font color='" + RealTimeDataTextTool.getOrdinaryDataColor_RGB() + "'>" + SystemTool.getTotalMemory(context, true) + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            RealTimeDataTextTool.textAddAvailableMemory(context, true, algui.addTextView("系统可用内存：", 11, 0xFF000000, null)),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml("系统总共存储：<font color='" + RealTimeDataTextTool.getOrdinaryDataColor_RGB() + "'>" + SystemTool.getTotalStorage(true) + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            RealTimeDataTextTool.textAddAvailableStorage(true, algui.addTextView("系统可用存储：", 11, 0xFF000000, null)),




            algui.addLine(0.5f, 0xFFE0E0E0, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.HORIZONTAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView( "设备信息", 11, 0xFF000000, Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            ),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml("品牌名：<font color='#757575'>" + Build.BRAND + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("系统型号：<font color='#757575'>" + Build.MODEL + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml("系统分辨率：<font color='#757575'>" + SystemTool.getScreenResolution(context) + "</font>"), 11, 0xFF000000, null),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("系统真实DP：<font color='#757575'>" + SystemTool.getRealScreenDP(context) + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml("系统CPU位数：<font color='#757575'>" + Build.CPU_ABI + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("设备系统：<font color='#757575'>" + (SystemTool.isHarmonyOs()?"HarmonyOs [鸿蒙系统]":"Android [安卓系统]") + "</font>"), 11, 0xFF000000, null),
        
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml("系统版本：<font color='#757575'>" + (SystemTool.isHarmonyOs()?"HarmonyOs "+SystemTool.getHarmonyVersion():"Android "+Build.VERSION.RELEASE) + "</font>"), 11, 0xFF000000, null),
            

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("SDK版本号：<font color='#757575'>" + Build.VERSION.SDK + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("系统ID：<font color='#757575'>" + Build.ID + "</font>") , 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("Root权限：" + (SystemTool.inspectRootPermission() ?"<font color='#009688'>当前设备拥有ROOT权限</font>": "<font color='#795548'>当前设备没有ROOT权限</font>") ), 11, 0xFF000000, null),




            algui.addLine(0.5f, 0xFFE0E0E0, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.HORIZONTAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView( "应用信息", 11, 0xFF000000, Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            ),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("应用名称：<font color='#757575'>" + AppTool.getAppName(context) + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("版本名称：<font color='#757575'>" + AppTool.getVersion(context) + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml("版本号：<font color='#757575'>" + AppTool.getVersionCode(context) + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView(Html.fromHtml("包名：<font color='#757575'>" + AppTool.getAppPackageName(context) + "</font>"), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("系统应用：" + (AppTool.isSystemApp(context) ?"<font color='#009688'>是</font>": "<font color='#795548'>否</font>")), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("调试模式：" + (AppTool.isDebugMode(context) ?"<font color='#009688'>true</font>": "<font color='#795548'>false</font>")), 11, 0xFF000000, null),

           //此功能在algui1.0版本中已废除 因为如果手机具有ROOT权限 它会弹出对话框让用户授予ROOT权限来检测软件是否授予ROOT权限 这种方法非常蠢 因此我们决定废除此功能
           // algui.addLine(0.5f, 0xFFE0E0E0, true),
           // algui.addTextView(Gravity.LEFT, Html.fromHtml("Root权限：" + (AppTool.isRootEnabled()?"<font color='#009688'>当前应用已授予ROOT权限</font>":"<font color='#795548'>当前应用未授予ROOT权限</font>")), 11, 0xFF000000, null),

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("当前应用uid：<font color='#3F51B5'>" + AppTool.getAppUid(context) + "</font>"), 11, 0xFF000000, null),
            AppTool.getSameUidAppNames(context).size() == 0
            ?
            algui.addTextView( Html.fromHtml("uid跨进程：<font color='#757575'>" + "没有可以跨进程的应用，因为没有检测到与当前应用uid相同的应用" + "</font>"), 11, 0xFF000000, null)
            :
            algui.addTextView( Html.fromHtml("uid跨进程：<font color='#BA68C8'>" + String.join(", ", AppTool.getSameUidAppNames(context)) + "与当前应用uid相同, 它们可以跨进程"  + "</font>"), 11, 0xFF000000, null)
            ,

            algui.addLine(0.5f, 0xFFE0E0E0, true),
            algui.addTextView( Html.fromHtml("入口Activity：<font color='#757575'>" + AppTool.getLauncherActivityName(context) + "</font>"), 11, 0xFF000000, null),





            algui.addLine(0.5f, 0xFFE0E0E0, true),
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.HORIZONTAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView( "环境检测机制", 11, 0xFF000000, Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            ),
            algui.addLine(0.5f, 0xFFE0E0E0, true),
            //实时检测SELinux模式
            textAddSELinuxMode(algui.addMarqueeTextView(Gravity.LEFT, "", 11, 0xFF3F51B5, null, 8000, Animation.INFINITE, true)),




            algui.addLine(0.5f, 0xFFE0E0E0, true),
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.HORIZONTAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView( "Copyright © 2023 艾琳 版权所有", 9, 0xFF9E9E9E, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            )
        );
        if (larentLayout != null) {
            larentLayout.addView(sam);
        } 
        return sam;

    }

    //显示外观设置菜单 参数：父布局
    public vLinearLayout addAppearanceSettingsMenu(ViewGroup larentLayout) {

        if (context == null||algui==null) {
            return null;
        }

        //在悬浮窗内部滚动布局中增加一个折叠菜单
        vLinearLayout as= algui.addCollapse
        (   //折叠菜单属性
            "菜单设置", 10, 0xFF000000, null,//折叠菜单文本，文本大小，文本颜色，文本字体(null代表默认)
            3,//折叠菜单圆角半径
            0xFFFFFFFF,//折叠菜单背景颜色
            0, 0xFFC5CAE9,//折叠菜单描边大小，描边颜色
            false,//折叠菜单默认是否展开，true=默认展开，false=默认不展开

            algui.addLine(0.5f, 0xFFE0E0E0, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.VERTICAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView( "全局颜色设置", 11, 0xFF000000, Typeface.create(Typeface.DEFAULT, Typeface.BOLD)),
            algui.addTextView( "支持的颜色格式 ARGB格式(0xFF4DB6AC)  HEX格式(#4DB6AC)", 8, 0xFF000000, null),
            algui.addTextView( "设置完成后将自动保存配置", 8, 0xFF000000, null)
            ),
            //↓折叠菜单中的视图↓
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单上下边栏背景颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000,  0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){

                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作


                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                        //最大输入10位
                        /*if (editable.toString().length() > 10) {
                         // 输入超过最大长度，进行截断操作
                         String trimmedInput = editable.toString().substring(0, 10);
                         edit.setText(trimmedInput);
                         edit.setSelection(10);
                         }*/

                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                algui.getMenuRootLayout().setBackColor(color);// 设置颜色




                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("根布局背景颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);

                            }
                        } else {
                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }
                    }
                }
            ),
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单说明布局背景颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000,  0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuExplanationLayout().setBackgroundColor(color);



                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单说明背景颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {
                           AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            ),
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单滚动列表背景颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000,  0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作     
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }

                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuScrollingList().setBackgroundColor(color);



                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单滚动列表背景颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }


                    }
                }
            ),

            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单描边边框绘制颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000,  0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {

                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuRootLayout().setBorderColor(color);




                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("根布局描边颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                               AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            )
            ,
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单右下三角绘制颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000,  0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuBottomRightTrianglePaint().setColor(color);
                                algui.getMenuBottomRightTriangleView().invalidate();//重绘




                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单右下角三角形颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                          AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            )
            ,
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单顶部线条绘制颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuTopLine().setBackColor(color);





                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单顶部线条颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            )
            ,

            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单主要标题文本颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuMainTitle().setTextColor(color);




                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单主标题文本颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                           AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            )
            ,
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单小副标题文本颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuSubTitle().setTextColor(color);




                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单副标题文本颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                               AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            )
            ,
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单说明内容文本颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuExplanation().setTextColor(color);




                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单说明文本颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                           AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            )
            ,
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单左下按钮文本颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuBottomLeftButton().setTextColor(color);




                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单左下角按钮文本颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                               AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            ),
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单右下按钮文本颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                                algui.getMenuBottomRightButton().setTextColor(color);




                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单右下角按钮文本颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                              AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            ),
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "菜单直播模式图标颜色", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                // 设置颜色
                               
                                algui.getMenuLiveStreamIcon().setImageTintList(ColorStateList.valueOf(color));
                                



                                //保存颜色
                                AlGuiData.getMenuColorSPED(context).putInt((String)AlGuiData.getMenuColorData().get("菜单直播模式图标颜色键名"), color);
                                //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                AlGuiData.getMenuColorSPED(context).apply();
                                AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存", 5000);

                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
                ),
            //增加一个普通按钮
            algui.addButton
            (
                //按钮属性
                "恢复默认颜色", 11, 0xFFFFFFFF, null,//按钮文本，文本大小，文本颜色，文本字体(null代表默认)
                5,//按钮圆角半径
                0xFF3F51B5,//按钮颜色
                0, 0xFFFFFFFF,//按钮描边大小，描边颜色
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,//按钮宽度，按钮高度
                //小按钮点击事件
                new AlGui.T_ButtonOnChangeListener(){
                    @Override// button=按钮对象 back=按钮外观对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                        AlGuiData.getMenuColorSPED(context).clear();//删除所有保存的颜色数据
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getMenuColorSPED(context).apply();
                        //使用菜单默认颜色配置
                        algui.updateMenuAppearance();
                        AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "恢复成功", "已恢复所有颜色并清除保存的颜色配置", 5000);

                    }
                }
            ),




            //algui.addLine(0.5f, 0xFFE0E0E0, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
            algui.addTextView(" ", 10, 0, null),
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.VERTICAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView("全局属性设置", 11, 0xFF000000, Typeface.create(Typeface.DEFAULT, Typeface.BOLD)),
            algui.addTextView( "设置完成后将自动保存配置", 8, 0xFF000000, null)
            ),
            //增加一个小数拖动条 
            algui.addSeekBarFloat
            (
                //拖动条属性
                "菜单圆角半径", 11, 0xFF000000, null,//文本，文本大小，文本颜色，文本字体(null代表默认)
                0f, AlGuiData.getMenuAttributeSP(context).getFloat((String)AlGuiData.getMenuAttributeData().get("根布局圆角半径键名"), (float)AlGuiData.getMenuAttributeData().get("根布局圆角半径默认数据")), 50f,//最小进度，初始进度，最大进度
                0xFF5492E5, 0xFF5492E5, 0xFF5492E5,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
                //拖动条监听事件
                new AlGui.T_SeekBarFloatOnChangeListener()
                {
                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度 fromUser=是否为用户手动拖动改变的进度的而不是代码来拖动改变的进度
                    public void onProgressChanged(TextView textView, SeekBar seekBar, float progress, boolean fromUser) {
                        // 当拖动条进度改变时调用
                        //设置根布局圆角半径
                        algui.getMenuRootLayout().setFilletRadiu(ViewTool.convertDpToPx(context, progress));

                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStartTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当开始拖动拖动条时调用
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStopTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当停止拖动拖动条时调用

                        //保存数据  (这里保存时注意要直接保存当前进度 不要进行转单位 因为保存后下一次进时设置的时候获取到这个值后会转单位的 所以不影响)
                        AlGuiData.getMenuAttributeSPED(context).putFloat((String)AlGuiData.getMenuAttributeData().get("根布局圆角半径键名"), progress);
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getMenuAttributeSPED(context).apply();
                    }
                }
            ),
            //增加一个小数拖动条 
            algui.addSeekBarFloat
            (
                //拖动条属性
                "菜单描边宽度", 11, 0xFF000000, null,//文本，文本大小，文本颜色，文本字体(null代表默认)
                0f, AlGuiData.getMenuAttributeSP(context).getFloat((String)AlGuiData.getMenuAttributeData().get("根布局描边宽度键名"), (float)AlGuiData.getMenuAttributeData().get("根布局描边宽度默认数据")), 10f,//最小进度，初始进度，最大进度
                0xFF5492E5, 0xFF5492E5, 0xFF5492E5,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
                //拖动条监听事件
                new AlGui.T_SeekBarFloatOnChangeListener()
                {
                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度 fromUser=是否为用户手动拖动改变的进度的而不是代码来拖动改变的进度
                    public void onProgressChanged(TextView textView, SeekBar seekBar, float progress, boolean fromUser) {
                        // 当拖动条进度改变时调用
                        //设置根布局描边宽度
                        algui.getMenuRootLayout().setBorderSize(ViewTool.convertDpToPx(context, progress));//描边宽度以及描边颜色
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStartTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当开始拖动拖动条时调用
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStopTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当停止拖动拖动条时调用
                        //保存数据  (这里保存时注意要直接保存当前进度 不要进行转单位 因为保存后下一次进时设置的时候获取到这个值后会转单位的 所以不影响)
                        AlGuiData.getMenuAttributeSPED(context).putFloat((String)AlGuiData.getMenuAttributeData().get("根布局描边宽度键名"), progress);
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getMenuAttributeSPED(context).apply();
                    }
                }
            ),
            //增加一个小数拖动条 
            algui.addSeekBarFloat
            (
                //拖动条属性
                "顶部线条圆角", 11, 0xFF000000, null,//文本，文本大小，文本颜色，文本字体(null代表默认)
                0f, AlGuiData.getMenuAttributeSP(context).getFloat((String)AlGuiData.getMenuAttributeData().get("菜单顶部线条圆角半径键名"), (float)AlGuiData.getMenuAttributeData().get("菜单顶部线条圆角半径默认数据")), 20f,//最小进度，初始进度，最大进度
                0xFF5492E5, 0xFF5492E5, 0xFF5492E5,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
                //拖动条监听事件
                new AlGui.T_SeekBarFloatOnChangeListener()
                {
                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度 fromUser=是否为用户手动拖动改变的进度的而不是代码来拖动改变的进度
                    public void onProgressChanged(TextView textView, SeekBar seekBar, float progress, boolean fromUser) {
                        // 当拖动条进度改变时调用
                        //设置根布局描边宽度
                        algui.getMenuTopLine().setFilletRadiu(ViewTool.convertDpToPx(context, progress));
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStartTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当开始拖动拖动条时调用
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStopTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当停止拖动拖动条时调用
                        //保存数据  (这里保存时注意要直接保存当前进度 不要进行转单位 因为保存后下一次进时设置的时候获取到这个值后会转单位的 所以不影响)
                        AlGuiData.getMenuAttributeSPED(context).putFloat((String)AlGuiData.getMenuAttributeData().get("菜单顶部线条圆角半径键名"), progress);
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getMenuAttributeSPED(context).apply();
                    }
                }
            ),

            //增加一个小数拖动条 
            algui.addSeekBarFloat
            (
                //拖动条属性
                "悬浮窗透明度", 11, 0xFF000000, null,//文本，文本大小，文本颜色，文本字体(null代表默认)
                0.1f, AlGuiData.getMenuAttributeSP(context).getFloat((String)AlGuiData.getMenuAttributeData().get("菜单透明度键名"), (float)AlGuiData.getMenuAttributeData().get("菜单透明度默认数据")), 1f,//最小进度，初始进度，最大进度
                0xFF5492E5, 0xFF5492E5, 0xFF5492E5,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
                //拖动条监听事件
                new AlGui.T_SeekBarFloatOnChangeListener()
                {
                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度 fromUser=是否为用户手动拖动改变的进度的而不是代码来拖动改变的进度
                    public void onProgressChanged(TextView textView, SeekBar seekBar, float progress, boolean fromUser) {
                        // 当拖动条进度改变时调用
                        algui.getMenuRootLayout().setAlpha(progress);
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStartTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当开始拖动拖动条时调用
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStopTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当停止拖动拖动条时调用
                        //保存数据  (这里保存时注意要直接保存当前进度 不要进行转单位 因为保存后下一次进时设置的时候获取到这个值后会转单位的 所以不影响)
                        AlGuiData.getMenuAttributeSPED(context).putFloat((String)AlGuiData.getMenuAttributeData().get("菜单透明度键名"), progress);
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getMenuAttributeSPED(context).apply();
                    }
                }
            ),

            //增加一个普通按钮
            algui.addButton
            (
                //按钮属性
                "恢复默认属性", 11, 0xFFFFFFFF, null,//按钮文本，文本大小，文本颜色，文本字体(null代表默认)
                5,//按钮圆角半径
                0xFF3F51B5,//按钮颜色
                0, 0xFFFFFFFF,//按钮描边大小，描边颜色
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,//按钮宽度，按钮高度
                //小按钮点击事件
                new AlGui.T_ButtonOnChangeListener(){
                    @Override// button=按钮对象 back=按钮外观对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                        //清除 除了悬浮窗宽高以外的所有数据
                        Map<String, ?> allPrefs = AlGuiData.getMenuAttributeSP(context).getAll();
                        for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                            String key = entry.getKey();
                            if (!key.equals((String)AlGuiData.getMenuAttributeData().get("菜单滚动列表宽度键名")) && !key.equals((String)AlGuiData.getMenuAttributeData().get("菜单滚动列表高度键名"))) {
                                AlGuiData.getMenuAttributeSPED(context).remove(key);
                            }
                        }

                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getMenuAttributeSPED(context).apply();
                        //重新加载菜单外观
                        algui.updateMenuAppearance();
                        AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "恢复成功", "已恢复所有属性并清除保存的属性配置", 5000);

                    }
                }
            ),



            //algui.addLine(0.5f, 0xFFE0E0E0, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.HORIZONTAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView( "Copyright © 2023 艾琳 版权所有", 9, 0xFF9E9E9E, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            )
        );
        if (larentLayout != null) {
            larentLayout.addView(as);
        }
        return as;

    }
    
    
    
    
    
    
    //游戏准星助手折叠菜单 
    //参数：父布局
    private TextView WaterMark=null;//准星文本
    private WindowManager wManager=null;//准星的窗口
    private WindowManager.LayoutParams wParams=null;//准星窗口参数
  
    public  vLinearLayout addGameFrontSightMenu(ViewGroup larentLayout) {
        if (context == null||algui==null) {
            return null;
        }
        
        initFrontSight();//初始化游戏准星
        
        //在悬浮窗内部滚动布局中增加一个折叠菜单
        vLinearLayout sam = algui.addCollapse
        (   //折叠菜单属性
            "游戏准星", 10, 0xFF000000, null,//折叠菜单文本，文本大小，文本颜色，文本字体(null代表默认)
            3,//折叠菜单圆角半径
            0xFFFFFFFF,//折叠菜单背景颜色
            0, 0xFFC5CAE9,//折叠菜单描边大小，描边颜色
            false,//折叠菜单默认是否展开，true=默认展开，false=默认不展开

            //↓折叠菜单中的视图↓

            algui.addLine(0.5f, 0xFFE0E0E0, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
            //添加一个开关按钮
            algui.addSwitch
            (
                //开关按钮属性⚙️
                //开关文本，文本大小，文本颜色，文本字体(null代表默认)
                "游戏准星", 11, 0xFF000000, null,
                //开关描述文本，文本大小，文本颜色，文本字体(null代表默认)
                "在游戏内绘制一个准星 - 提升精准度", 8, 0xFF9E9E9E, null,
                //开关圆圈开启时的颜色 和 开关轨迹开启时的颜色
                0xFF4CAF50, 0xFF66BB6A,
                //开关圆圈关闭时的颜色 和 开关轨迹关闭时的颜色
                0xFFF44336, 0xFFEF5350,
                //开关点击事件
                new AlGui.T_SwitchOnChangeListener(){
                    @Override//事件中你可以获取到： aSwitch=开关对象 desc=描述信息对象 isChecked=开关状态
                    public void onClick(CompoundButton aSwitch, TextView desc, boolean isChecked) {
                        if(isChecked){
                            if(WaterMark==null||wManager==null||wParams==null){
                                //初始化
                                if(initFrontSight()){
                                    //初始化成功
                                    AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "初始化成功", "已加载游戏准星", 5000);
                                    
                                }else{
                                    //初始化失败
                                    aSwitch.setChecked(false);
                                    AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "准星初始化失败", "可能没有悬浮窗权限或空指针异常", 5000);          
                                    
                                    return;
                                }
                                
                            }
                            WaterMark.setVisibility(View.VISIBLE);//视图可见
                            AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "开启成功", "已加载游戏准星", 5000);
                            
                        }else{
                            if(WaterMark==null||wManager==null||wParams==null){
                             return;
                            }
                            WaterMark.setVisibility(View.GONE);//视图不可见
                            AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "关闭成功", "已关闭游戏准星", 5000);
                            
                        }
                        
                    }
                }
            ),
            //algui.addTextView(Gravity.LEFT, "准星参数设置", 11, 0xFF000000, Typeface.create(Typeface.DEFAULT, Typeface.BOLD)),
           // algui.addTextView(Gravity.LEFT, "准星样式", 9, 0xFF000000, null),
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "准星样式 例如：⊙☉⊕·✛╋☩ 等等", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                      
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {    
                        if(WaterMark==null||wManager==null||wParams==null){
                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "游戏准星未初始化，无法设置！", 5000);          
                            return;
                        }
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            
                                    WaterMark.setText(editText);
                                    //保存数据
                                    AlGuiData.getGameFrontSightSPED(context).putString((String)AlGuiData.getGameFrontSightData().get("游戏准星样式键名"), editText);
                                    //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                    //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                    AlGuiData.getGameFrontSightSPED(context).apply();
                                    AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存配置", 5000);
                                    
                               
                            
                        } else {
                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入准星样式符号 例如：⊙☉⊕·✛╋☩ 等等", 5000);
                        }

                    }
                }
            ),
          //  algui.addTextView(Gravity.LEFT, "准星颜色", 9, 0xFF000000, null),
            //增加一个输入框
            algui.addEditText
            (
                //通用属性
                9, null,//输入框和按钮的文本大小，输入框和按钮的文本字体(null代表默认)

                //输入框属性
                0xFFCECECE, "准星颜色 例如：#009688 或 0xFF009688等等", //输入框提示内容颜色，输入框提示内容
                0xC200FF00, "", //输入框输入内容颜色，输入框默认输入内容
                5, 0xA8000000, 0, 0,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

                //按钮属性
                0xFFFFFFFF, "应用", //按钮文本颜色，按钮文本
                5, 0xFF5492E5, 0, 0xFFFFFFFF,//按钮圆角半径，按钮背景色，按钮描边厚度，按钮描边颜色

                //输入框监听事件
                new AlGui.T_EditTextOnChangeListener(){
                    @Override
                    public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                        // 在文本变化之前执行的操作

                    }

                    @Override
                    public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                        // 在文本变化时执行的操作

                    }

                    @Override
                    public void afterTextChanged(EditText edit, Editable editable) {
                        // 在文本变化之后执行的操作              
                        //进行检测 只支持输入字母、数字和 # 号
                        //检测非法字符 如果检测到则清除           
                        if (!editable.toString().matches("[a-zA-Z0-9#]*")) {
                            // 输入包含非法字符，进行清除操作
                            String cleanedInput = editable.toString().replaceAll("[^a-zA-Z0-9#]", "");
                            edit.setText(cleanedInput);
                            edit.setSelection(cleanedInput.length());
                        }
                    }

                    //输入框按钮点击事件
                    @Override// edit=输入框对象 button=普通按钮对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                        if(WaterMark==null||wManager==null||wParams==null){
                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "游戏准星未初始化，无法设置！", 5000);          
                            return;
                        }
                        //获取输入框当前输入的内容
                        String editText=edit.getText().toString() != null ?edit.getText().toString(): "";
                        //输入框内容不为空才进
                        if (!editText.isEmpty()) {
                            try {
                                int color;
                                if (editText.startsWith("0x") && editText.length() == 10) {
                                    // 支持十六进制的 ARGB 格式，例如 0xFFFFFFFF
                                    color = (int) Long.parseLong(editText.substring(2), 16);
                                } else {
                                    // 支持常规的颜色字符串格式，例如 #RRGGBB 或 #AARRGGBB
                                    color = Color.parseColor(editText);
                                }
                                
                                        // 设置颜色
                                        WaterMark.setTextColor(color);
                                        //保存数据
                                        AlGuiData.getGameFrontSightSPED(context).putInt((String)AlGuiData.getGameFrontSightData().get("游戏准星颜色键名"), color);
                                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                                        AlGuiData.getGameFrontSightSPED(context).apply();
                                        AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "应用成功", "已自动保存配置", 5000);
                                    
                              
                            } catch (IllegalArgumentException e) {
                                // 如果输入的颜色值不合法，可以在这里进行错误处理
                                AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "应用失败", "无效的颜色值", 5000);
                            }    
                        } else {

                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "请输入颜色值 例如：#009688 或 0xFF009688", 5000);


                        }

                    }
                }
            ),
            //增加一个小数拖动条 
            algui.addSeekBarFloat
            (
                //拖动条属性
                "准星大小", 11, 0xFF000000, null,//文本，文本大小，文本颜色，文本字体(null代表默认)
                0, AlGuiData.getGameFrontSightSP(context).getFloat((String)AlGuiData.getGameFrontSightData().get("游戏准星大小键名"), (float)AlGuiData.getGameFrontSightData().get("游戏准星大小默认数据")), 100,//最小进度，初始进度，最大进度
                0xFF5492E5, 0xFF5492E5, 0xFF5492E5,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
                //拖动条监听事件
                new AlGui.T_SeekBarFloatOnChangeListener()
                {

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度 fromUser=是否为用户手动拖动改变的进度的而不是代码来拖动改变的进度
                    public void onProgressChanged(TextView textView, SeekBar seekBar, float progress, boolean fromUser) {
                        // 当拖动条进度改变时调用
                        if(WaterMark==null||wManager==null||wParams==null){
                            return;
                        }
                        WaterMark.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(context,progress));

                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStartTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当开始拖动拖动条时调用
                        if(WaterMark==null||wManager==null||wParams==null){
                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "游戏准星未初始化，无法设置！", 5000);          
                            return;
                        }
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStopTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        if(WaterMark==null||wManager==null||wParams==null){
                            return;
                        }
                        // 当停止拖动拖动条时调用
                        //保存数据  (这里保存时注意要直接保存当前进度 不要进行转单位 因为保存后下一次进时设置的时候获取到这个值后会转单位的 所以不影响)
                        AlGuiData.getGameFrontSightSPED(context).putFloat((String)AlGuiData.getGameFrontSightData().get("游戏准星大小键名"), progress);
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getGameFrontSightSPED(context).apply();
                    }
                }
            ),
            //增加一个小数拖动条 
            algui.addSeekBarFloat
            (
                //拖动条属性
                "准星透明", 11, 0xFF000000, null,//文本，文本大小，文本颜色，文本字体(null代表默认)
                0, AlGuiData.getGameFrontSightSP(context).getFloat((String)AlGuiData.getGameFrontSightData().get("游戏准星透明度键名"), (float)AlGuiData.getGameFrontSightData().get("游戏准星透明度默认数据")), 1,//最小进度，初始进度，最大进度
                0xFF5492E5, 0xFF5492E5, 0xFF5492E5,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
                //拖动条监听事件
                new AlGui.T_SeekBarFloatOnChangeListener()
                {

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度 fromUser=是否为用户手动拖动改变的进度的而不是代码来拖动改变的进度
                    public void onProgressChanged(TextView textView, SeekBar seekBar, float progress, boolean fromUser) {
                        // 当拖动条进度改变时调用
                        if(WaterMark==null||wManager==null||wParams==null){
                            return;
                        }
                        WaterMark.setAlpha(progress);
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStartTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        // 当开始拖动拖动条时调用
                        if(WaterMark==null||wManager==null||wParams==null){
                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "游戏准星未初始化，无法设置！", 5000);          
                            return;
                        }
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStopTrackingTouch(TextView textView, SeekBar seekBar, float progress) {
                        if(WaterMark==null||wManager==null||wParams==null){
                            return;
                        }
                        // 当停止拖动拖动条时调用
                        AlGuiData.getGameFrontSightSPED(context).putFloat((String)AlGuiData.getGameFrontSightData().get("游戏准星透明度键名"), progress);
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getGameFrontSightSPED(context).apply();
                    }
                }
            ),
            //增加一个整数拖动条 
            algui.addSeekBarInt
            (
                //拖动条属性
                "X偏移(相对中心点)", 11, 0xFF000000, null,//文本，文本大小，文本颜色，文本字体(null代表默认)
                -1000, AlGuiData.getGameFrontSightSP(context).getInt((String)AlGuiData.getGameFrontSightData().get("游戏准星X偏移键名"), (int)AlGuiData.getGameFrontSightData().get("游戏准星X偏移默认数据")), 1000,//最小进度，初始进度，最大进度
                0xFF5492E5, 0xFF5492E5, 0xFF5492E5,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
                //拖动条监听事件
                new AlGui.T_SeekBarIntOnChangeListener()
                {

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度 fromUser=是否为用户手动拖动改变的进度的而不是代码来拖动改变的进度
                    public void onProgressChanged(TextView textView, SeekBar seekBar, int progress, boolean fromUser) {
                        // 当拖动条进度改变时调用
                        if(WaterMark==null||wManager==null||wParams==null){
                            return;
                        }
                        wParams.x = progress;
                        
                        wManager.updateViewLayout(WaterMark,wParams);
                     

                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStartTrackingTouch(TextView textView, SeekBar seekBar, int progress) {
                        // 当开始拖动拖动条时调用
                        if(WaterMark==null||wManager==null||wParams==null){
                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "游戏准星未初始化，无法设置！", 5000);          
                            return;
                        }
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStopTrackingTouch(TextView textView, SeekBar seekBar,int progress) {
                        if(WaterMark==null||wManager==null||wParams==null){
                            return;
                        }
                        // 当停止拖动拖动条时调用
                        //保存数据  (这里保存时注意要直接保存当前进度 不要进行转单位 因为保存后下一次进时设置的时候获取到这个值后会转单位的 所以不影响)
                        AlGuiData.getGameFrontSightSPED(context).putInt((String)AlGuiData.getGameFrontSightData().get("游戏准星X偏移键名"), seekBar.getProgress());
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getGameFrontSightSPED(context).apply();


                    }
                }
            ),
            //增加一个整数拖动条 
            algui.addSeekBarInt
            (
                //拖动条属性
                "Y偏移(相对中心点)", 11, 0xFF000000, null,//文本，文本大小，文本颜色，文本字体(null代表默认)
                -1000, AlGuiData.getGameFrontSightSP(context).getInt((String)AlGuiData.getGameFrontSightData().get("游戏准星Y偏移键名"), (int)AlGuiData.getGameFrontSightData().get("游戏准星Y偏移默认数据")), 1000,//最小进度，初始进度，最大进度
                0xFF5492E5, 0xFF5492E5, 0xFF5492E5,//进度圆颜色，进度条未拖动时的颜色，进度条拖动后的颜色
                //拖动条监听事件
                new AlGui.T_SeekBarIntOnChangeListener()
                {

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度 fromUser=是否为用户手动拖动改变的进度的而不是代码来拖动改变的进度
                    public void onProgressChanged(TextView textView, SeekBar seekBar, int progress, boolean fromUser) {
                        // 当拖动条进度改变时调用
                        if(WaterMark==null||wManager==null||wParams==null){
                            return;
                        }
                        wParams.y = progress;

                        wManager.updateViewLayout(WaterMark,wParams);


                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStartTrackingTouch(TextView textView, SeekBar seekBar,int progress) {
                        // 当开始拖动拖动条时调用
                        if(WaterMark==null||wManager==null||wParams==null){
                            AlGuiBubbleNotification.Inform(context).showAlertNotification_Simplicity(null, "应用失败", "游戏准星未初始化，无法设置！", 5000);          
                            return;
                        }
                    }

                    @Override//textView=拖动条文本对象 seekBar=拖动条对象 progress=拖动条当前进度
                    public void onStopTrackingTouch(TextView textView, SeekBar seekBar,int progress) {
                        if(WaterMark==null||wManager==null||wParams==null){
                            return;
                        }
                        // 当停止拖动拖动条时调用
                        //保存数据  (这里保存时注意要直接保存当前进度 不要进行转单位 因为保存后下一次进时设置的时候获取到这个值后会转单位的 所以不影响)
                        AlGuiData.getGameFrontSightSPED(context).putInt((String)AlGuiData.getGameFrontSightData().get("游戏准星Y偏移键名"), seekBar.getProgress());
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getGameFrontSightSPED(context).apply();


                    }
                }
            ),
            //增加一个普通按钮
            algui.addButton
            (
                //按钮属性
                "恢复默认配置", 11, 0xFFFFFFFF, null,//按钮文本，文本大小，文本颜色，文本字体(null代表默认)
                5,//按钮圆角半径
                0xFF3F51B5,//按钮颜色
                0, 0xFFFFFFFF,//按钮描边大小，描边颜色
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ,//按钮宽度，按钮高度
                //小按钮点击事件
                new AlGui.T_ButtonOnChangeListener(){
                    @Override// button=按钮对象 back=按钮外观对象 buttonText=按钮文本视图对象 isChecked=按钮开关状态
                    public  void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                        if(WaterMark==null||wManager==null||wParams==null){
                            AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "恢复失败", "游戏准星未初始化，无法恢复！", 5000);
                            
                            return;
                        }
                        AlGuiData.getGameFrontSightSPED(context).clear();//删除所有保存的数据
                        //提交保存编辑操作，使修改生效。注意，这里使用 apply() 方法而不是 commit() 方法，
                        //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
                        AlGuiData.getGameFrontSightSPED(context).apply();
                        //更新配置
                        updateFrontSightCF();
                        wManager.updateViewLayout(WaterMark,wParams);
                        AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "恢复成功", "已重置准星所有配置", 5000);
                        
                    }
                }
            ),
            //添加一个线性布局，主要用来排版一些比较小的UI视图让它们更加规整，例如小按钮和复选框，使他们能够横向添加多个而不是一味的垂直添加
            AlGui.GUI(context).addLinearLayout
            (//这一对括号中是线性布局的空间
                //线性布局属性配置🛠️
                Gravity.CENTER,//子视图对齐方式
                LinearLayout.HORIZONTAL,//线性布局方向
                LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
                LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
            algui.addTextView("Copyright © 2023 艾琳 版权所有", 9, 0xFF9E9E9E, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            )
        );
        if (larentLayout != null) {
            larentLayout.addView(sam);
        } 
        return sam;

    }
    
    //初始化游戏准星
    private boolean initFrontSight(){
        if (context == null) {
            return false;
        }
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (!AppPermissionTool.isAndroidManifestPermissionExist(context, "android.permission.SYSTEM_ALERT_WINDOW")) {
            Toast.makeText(context, "开发者未在AndroidManifest.xml文件中声明悬浮窗权限因此无法显示准星，只有声明了此权限才能显示准星！你也可以手动去安装包此文件中声明android.permission.SYSTEM_ALERT_WINDOW", Toast.LENGTH_LONG).show();
            
            return false;
        }

        wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wParams = new WindowManager.LayoutParams();

        wParams.flags = 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | //设置窗口在接收触摸事件时不会拦截其他窗口的触摸事件。其他窗口仍然可以接收触摸事件。
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | //设置窗口不接收焦点（即无法获取键盘输入焦点）。这意味着，如果有其他可获得焦点的窗口存在，焦点将传递给该窗口。
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | //设置窗口不接收触摸事件。这意味着用户无法通过触摸来与该窗口进行交互。
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | //指定窗口布局时，考虑窗口所占据的整个屏幕空间。即使窗口没有覆盖整个屏幕，也会根据屏幕的大小和方向进行布局。
            // WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | //指定窗口布局时，不受限制地占据整个屏幕空间。窗口可以延伸到状态栏、导航栏等系统UI的区域。
            WindowManager.LayoutParams.FLAG_FULLSCREEN;//将窗口设置为全屏模式。窗口将覆盖整个屏幕，并隐藏状态栏和导航栏。
        wParams.gravity = Gravity.CENTER;
        
        wParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wParams.format = PixelFormat.RGBA_8888;
        wParams.windowAnimations = android.R.style.Animation_Toast;

        //系统级窗口 (需要悬浮窗权限)
        wParams.type =  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);


        WaterMark = new TextView(context);
        //设置文本视图阴影 参数：文本视图，阴影半径，阴影水平偏移量，阴影垂直偏移量，阴影颜色
        //ViewTool.setTextViewShadow(WaterMark, 5, 0, 0, 0xff4CAF50);
        WaterMark.setVisibility(View.GONE);//默认不可见
        updateFrontSightCF();//更新配置
        wManager.addView(WaterMark, wParams);
        
        return true;
    }
    //更新游戏准星配置
    private void updateFrontSightCF(){
        if(wParams==null||WaterMark==null||wManager==null){
            return;
        }
        wParams.x = AlGuiData.getGameFrontSightSP(context).getInt((String)AlGuiData.getGameFrontSightData().get("游戏准星X偏移键名"), (int)AlGuiData.getGameFrontSightData().get("游戏准星X偏移默认数据"));
        wParams.y = AlGuiData.getGameFrontSightSP(context).getInt((String)AlGuiData.getGameFrontSightData().get("游戏准星Y偏移键名"), (int)AlGuiData.getGameFrontSightData().get("游戏准星Y偏移默认数据"));
        WaterMark.setTextSize(TypedValue.COMPLEX_UNIT_PX, ViewTool.convertDpToPx(context,AlGuiData.getGameFrontSightSP(context).getFloat((String)AlGuiData.getGameFrontSightData().get("游戏准星大小键名"), (float)AlGuiData.getGameFrontSightData().get("游戏准星大小默认数据"))));
        WaterMark.setTextColor(AlGuiData.getGameFrontSightSP(context).getInt((String)AlGuiData.getGameFrontSightData().get("游戏准星颜色键名"), (int)AlGuiData.getGameFrontSightData().get("游戏准星颜色默认数据")));
        WaterMark.setText(AlGuiData.getGameFrontSightSP(context).getString((String)AlGuiData.getGameFrontSightData().get("游戏准星样式键名"), (String)AlGuiData.getGameFrontSightData().get("游戏准星样式默认数据")));
        WaterMark.setAlpha(AlGuiData.getGameFrontSightSP(context).getFloat((String)AlGuiData.getGameFrontSightData().get("游戏准星透明度键名"), (float)AlGuiData.getGameFrontSightData().get("游戏准星透明度默认数据")));
        
    }
     
    
        
        

	

}
