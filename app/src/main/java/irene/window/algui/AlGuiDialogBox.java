package irene.window.algui;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import irene.window.algui.CustomizeView.vLinearLayout;
import irene.window.algui.Tools.AppPermissionTool;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import irene.window.algui.Tools.ImageTool;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/07 03:16
 * @Describe Algui对话框 
 */
public class AlGuiDialogBox {

    public static final String TAG = "AlGuiDialogBox";

    //显示一个可以添加自定义视图的对话框
    public static AlertDialog showDiaLog(Context tContext, int backColor, float radius, View... view) {
        if (tContext == null) {
            return null;
        }

        //创建AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(tContext);
        // builder.setCancelable(false); // 禁止按下返回键关闭对话框


        //根布局
        GradientDrawable Background1 = new GradientDrawable();
        Background1.setCornerRadius(radius);// 圆角幅度
        Background1.setColor(backColor);//背景颜色


        vLinearLayout linearLayouts = new vLinearLayout(tContext);
        linearLayouts.setOrientation(1);
        linearLayouts.setLayoutParams(new LinearLayout.LayoutParams(
                                          LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 100f));
        linearLayouts.setBackground(Background1); 



        linearLayouts.setPadding(40, 40, 40, 40);
        //linearLayouts.setGravity(Gravity.CENTER);
        //将传入的所有视图挨个加进折叠菜单内部布局中
        for (View aView : view) {
            if (aView != null) {
                //如果是小按钮
                if (aView.getId() == AlGuiData.AlguiView.SmallButton.getId()) {
                    //Log.d("折叠菜单添加一个控件", "小按钮");
                }
                linearLayouts.addView(aView);
            }
        }

        //设置对话框的自定义视图
        builder.setView(linearLayouts);

        // 创建对话框
        final AlertDialog dialog = builder.create();
        //设置对话框背景透明
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(AlGuiData.getLiveStreamFlags());
        //dialog.setCanceledOnTouchOutside(false); // 禁止点击对话框外部关闭对话框
        //设置窗口类型
        /*
         if(aContext instanceof Activity){
         //应用级窗口 (免悬浮权限 但是上下文必须是Activity 且 只能让悬浮窗显示在这一个Activity上方)
         ballParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//悬浮球
         menuParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//悬浮菜单
         return;
         }
         */
        //检查当前应用程序清单文件中是否声明了悬浮窗权限代号 android.permission.SYSTEM_ALERT_WINDOW
        if (AppPermissionTool.isAndroidManifestPermissionExist(tContext, "android.permission.SYSTEM_ALERT_WINDOW")) {
            if (AppPermissionTool.checkOverlayPermission(tContext)) {
                //系统级窗口 (需要悬浮窗权限)
                dialog.getWindow().setType((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY: WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                //显示对话框
                dialog.show();
            }
        } else {
            Toast.makeText(tContext, "开发者未在AndroidManifest.xml文件中声明悬浮窗权限因此无法显示对话框，只有声明了此权限才能显示对话框！你也可以手动去安装包此文件中声明android.permission.SYSTEM_ALERT_WINDOW", Toast.LENGTH_LONG).show();

        } 

        return dialog;
    }


    private static AlertDialog dialog;
    //显示艾琳预制的文本对话框
    public static AlertDialog showTextDiaLog(final Context context, CharSequence title, CharSequence content, CharSequence buttonText) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //dialog = builder.create();
        if(context==null){
            return null;
        }
        dialog = showDiaLog
        (context, 0xCEFAFAFA, 50f,
         AlGui.GUI(context).addLinearLayout
         (//这一对括号中是线性布局的空间
             //线性布局属性配置🛠️
             Gravity.CENTER,//子视图对齐方式
             LinearLayout.VERTICAL,//线性布局方向
             LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
             LinearLayout.LayoutParams.WRAP_CONTENT,//线性布局高
             AlGui.GUI(context).addTextView(title, 18, 0xFF212121, Typeface.create(Typeface.DEFAULT, Typeface.BOLD)),
             //AlGui.GUI(context).addLine(1f, 0xFF424242, true),//增加一条线 参数：线的厚度，线的颜色，线的方向(true=横向 false=竖向)
             AlGui.GUI(context).addTextView(content, 15f, 0xFF424242, null)
         ),
         //添加一个普通按钮
         AlGui.GUI(context).addButton
         (
             //普通按钮属性配置🛠️
             buttonText, 15, 0xFFFFFFFF, null,//按钮文本，文本大小，文本颜色，文本字体
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
         );
        return dialog;
    }



}
