package irene.window.algui;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.GregorianCalendar;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import irene.window.algui.AlGui;
import irene.window.algui.AlGuiBubbleNotification;
import irene.window.algui.Tools.VariousTools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author 𝘽𝙮·艾琳 - ［Copyright © 2023 艾琳 版权所有］游戏逆向交流群730967224 - 作者QQ3353484607
 * @Date 2024/02/27 02:12
 * @Describe 网络验证
 */
public class AlguiNetworkVerification {

    public static final String TAG = "AlguiNetworkVerification";
    private Context context;
    public Context getContext() {return context;}

    //单例模式
    private static AlguiNetworkVerification object;
    public static AlguiNetworkVerification getObject(Context c) {if (object == null) {object = new AlguiNetworkVerification(c);}return object;};
    private AlguiNetworkVerification(Context c) {context = c;}

    //网络验证配置 (微验)
    private final boolean IO=false;//网络验证总开关(调试用)
    public boolean getIO() {return IO;}
    private final int xtTime=20;//心跳验证间隔时间

    private final String NetworkVerification = "https://wy.llua.cn";//网络验证api接口
    private final String appID="49612";//应用ID
    private final String VersionNo="1.0";//应用版本号(用于更新)
    private final int successStatusCode=465;//成功状态码
    private final String appKey="46JnVb3RXV8Jyuz";//APPkey密钥
    private final String rc4_2 = "Zmg52cUeGKJcwk6";//RC4-2密钥

    //存储
    private String fileName="NetworkVerification";//本地存储网络验证数据的文件名称
    public String getFinlName() {return fileName;}




    //MD5哈希函数
    private String encodeMD5(String str) {
        if (str == null) {
            return "null";
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            byte messageDigest[] = md5.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", b));
            }
            return hexString.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    //获取机器码
    private String getMachineCode() {
        return  android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }
    //Post请求
    private String UrlPost(String ur, String byteString) {
        String str="";      
        try {
            URL url=new URL(ur);
            HttpURLConnection HttpURLConnection=(HttpURLConnection) url.openConnection();
            HttpURLConnection.setReadTimeout(9000);
            HttpURLConnection.setRequestMethod("POST");
            OutputStream outputStream = HttpURLConnection.getOutputStream();
            outputStream.write(byteString.getBytes());
            BufferedReader BufferedReader=new BufferedReader(new InputStreamReader(HttpURLConnection.getInputStream()));
            String String="";
            StringBuffer StringBuffer=new StringBuffer();
            while ((String = BufferedReader.readLine()) != null) {
                StringBuffer.append(String);
            }
            str = StringBuffer.toString();
        } catch (IOException e) {}
        return str;
    }

    //启动
    public void start() {
        update();//检测更新
        captionAnnouncement();//加载字幕公告

    }

    //检测更新
    private void update() {

        //根布局背景
        GradientDrawable rootLayoutB = new GradientDrawable();
        //rootLayoutB.setCornerRadius(ViewTool.convertDpToPx(context, 5));
        rootLayoutB.setColor(0xCE3F51B5);
        rootLayoutB.setStroke(0, 0xC1FFFFFF);

        //Algui滚动列表增加根布局
        final LinearLayout layout =AlGui.GUI(context).addLinearLayout
        (
            //线性布局属性配置🛠️
            AlGui.GUI(context).getMenuScrollingListLayout(),//父布局
            Gravity.CENTER,//子视图对齐方式
            LinearLayout.VERTICAL,//线性布局方向
            LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
            LinearLayout.LayoutParams.WRAP_CONTENT//线性布局高
        );
        layout.setBackground(rootLayoutB);
        layout.setPadding(50, 50, 50, 50);

        //根布局增加标题布局
        final LinearLayout textLayout =AlGui.GUI(context).addLinearLayout
        (
            //线性布局属性配置🛠️
            layout,//父布局
            Gravity.CENTER,//子视图对齐方式
            LinearLayout.HORIZONTAL,//线性布局方向
            LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
            LinearLayout.LayoutParams.WRAP_CONTENT//线性布局高
        );

        //标题布局增加一个线 {参数：父布局，线的厚度，线的颜色，线的方向(true=横向 false=竖向)}
        AlGui.GUI(context).addLine(textLayout, 1f, 0xC1FFFFFF, true);
        //标题布局增加一个标题文本 {参数：父布局，文本，文本大小，文本颜色，字体}
        final TextView title = AlGui.GUI(context).addTextView(textLayout, "服务器连接失败…", 15, 0xFFFAFAFA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        //标题布局增加一个线 {参数：父布局，线的厚度，线的颜色，线的方向(true=横向 false=竖向)}
        AlGui.GUI(context).addLine(textLayout, 1f, 0xC1FFFFFF, true);

        //根布局增加一个内容文本 {参数：父布局，文本，文本大小，文本颜色，字体}
        final TextView content = AlGui.GUI(context).addTextView(layout, "Algui网络验证系统连接失败！\n\n可能触发此警报的原因：\n➤ 未正确连接网络\n➤ 开发人员未正确配置网络验证\n➤ 网络验证配置失效\n➤ 验证服务商跑路\n请尝试重进，或等待程序更新…", 12, 0xFFE0E0E0, null);

        AlguiNetworkObtain task = new AlguiNetworkObtain(context, new AlguiNetworkObtain.NetworkCallback() {
                @Override
                public void onResult(String result) {
                    if (result == null) {
                        return;
                    }
                    String data=null;
                    try {
                        data = RC4Util.decryRC4(result, rc4_2, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        content.setText(e.getMessage());
                        return;
                    }
                    if (data == null) {
                        return;
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        // int code = jsonObject.getInt("code");//状态码

                        JSONObject msgObject = jsonObject.optJSONObject("msg");
                        String version = msgObject.optString("version");//版本号
                        String updateContent = msgObject.optString("app_update_show");//更新内容
                        final String updateUrl = msgObject.optString("app_update_url");//更新地址
                        String isUpdate = msgObject.optString("app_update_must");//是否强制更新


                        if (version != null) {
                            if (version.equals(VersionNo)) {
                                AlGui.GUI(context).getMenuScrollingListLayout().removeView(layout);
                                kamiInspect();
                                return;
                            } else {
                                if (updateContent == null || isUpdate == null) {
                                    return;
                                }

                                //设置更新标题
                                title.setText(Html.fromHtml("发现新版本 " + "<font color='#009688'>" + version + "</font>"));
                                //设置更新内容
                                content.setText(updateContent);
                                //根布局增加一个普通按钮
                                AlGui.GUI(context).addButton
                                (
                                    //普通按钮属性配置🛠️
                                    layout,//父布局
                                    "更新", 12, 0xFFFFFFFF, null,//按钮文本，文本大小，文本颜色，文本字体
                                    0,//按钮圆角半径
                                    0xCE536DFE,//按钮背景颜色
                                    0, 0xC1FFFFFF,//按钮描边大小，描边颜色
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,//按钮宽度，按钮高度
                                    new  AlGui.T_ButtonOnChangeListener(){
                                        @Override
                                        public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                            //按钮点击时执行的内容
                                            if (updateUrl != null) {
                                                VariousTools.gotoWeb(context, updateUrl);
                                            }
                                        }
                                    }
                                );


                                if (!isUpdate.equals("y")) {
                                    //根布局增加一个普通按钮
                                    AlGui.GUI(context).addButton
                                    (
                                        //普通按钮属性配置🛠️
                                        layout,//父布局
                                        "取消", 12, 0xFFFFFFFF, null,//按钮文本，文本大小，文本颜色，文本字体
                                        0,//按钮圆角半径
                                        0xCE536DFE,//按钮背景颜色
                                        0, 0xC1FFFFFF,//按钮描边大小，描边颜色
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,//按钮宽度，按钮高度
                                        new  AlGui.T_ButtonOnChangeListener(){
                                            @Override
                                            public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                                                //按钮点击时执行的内容
                                                AlGui.GUI(context).getMenuScrollingListLayout().removeView(layout);
                                                kamiInspect();
                                            }
                                        }
                                    );

                                }
                            }

                        } else {
                            return;
                        }



                    } catch (JSONException e) {

                        content.setText(e.getMessage());
                        return;
                    }
                }
            }
        );
        task.execute(NetworkVerification + "/api/?id=ini&app=" + appID, "GET");


    }





    //字幕公告
    private void captionAnnouncement() {
        AlguiNetworkObtain task = new AlguiNetworkObtain(context, new AlguiNetworkObtain.NetworkCallback() {
                @Override
                public void onResult(String result) {
                    if (result == null) {
                        return;
                    }
                    String data=null;
                    try {
                        data = RC4Util.decryRC4(result, rc4_2, "UTF-8");
                    } catch (UnsupportedEncodingException e) {

                        return;
                    }
                    if (data == null) {
                        return;
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        //int code = jsonObject.getInt("code");//状态码

                        JSONObject msgObject = jsonObject.optJSONObject("msg");
                        String msg = msgObject.optString("app_gg");//公告内容
                        if (msg == null) {
                            return;
                        }

                        AlGui.GUI(context).getMenuExplanation().setText("官方公告：" + msg);//设置滚动字幕
                    } catch (JSONException e) {

                        return;
                    }
                }
            }
        );
        task.execute(NetworkVerification + "/api/?id=notice&app=" + appID, "GET");
    }


    private  boolean b=false;

    //卡密登录
    private void kamiInspect() {

        //根布局背景
        GradientDrawable rootLayoutB = new GradientDrawable();
        //rootLayoutB.setCornerRadius(ViewTool.convertDpToPx(context, 5));
        rootLayoutB.setColor(0xCE3F51B5);
        rootLayoutB.setStroke(0, 0xC1FFFFFF);

        //Algui滚动列表增加根布局
        final LinearLayout layout =AlGui.GUI(context).addLinearLayout
        (
            //线性布局属性配置🛠️
            AlGui.GUI(context).getMenuScrollingListLayout(),//父布局
            Gravity.RIGHT,//子视图对齐方式
            LinearLayout.VERTICAL,//线性布局方向
            LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
            LinearLayout.LayoutParams.WRAP_CONTENT//线性布局高
        );
        layout.setBackground(rootLayoutB);
        layout.setPadding(50, 50, 50, 50);


        //根布局增加标题布局
        final LinearLayout textLayout =AlGui.GUI(context).addLinearLayout
        (
            //线性布局属性配置🛠️
            layout,//父布局
            Gravity.CENTER,//子视图对齐方式
            LinearLayout.HORIZONTAL,//线性布局方向
            LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
            LinearLayout.LayoutParams.WRAP_CONTENT//线性布局高
        );

        //标题布局增加一个线 {参数：父布局，线的厚度，线的颜色，线的方向(true=横向 false=竖向)}
        AlGui.GUI(context).addLine(textLayout, 1f, 0xC1FFFFFF, true);
        //标题布局增加一个标题文本 {参数：父布局，文本，文本大小，文本颜色，字体}
        final TextView title = AlGui.GUI(context).addTextView(textLayout, "登录程序", 15, 0xFFFAFAFA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        //标题布局增加一个线 {参数：父布局，线的厚度，线的颜色，线的方向(true=横向 false=竖向)}
        AlGui.GUI(context).addLine(textLayout, 1f, 0xC1FFFFFF, true);


        //根布局增加一个输入框
        final EditText editText = AlGui.GUI(context).addEditText
        (
            //输入框的属性配置🛠
            layout,//父布局
            9, null,//输入框文本大小，输入框文本字体(null代表跟随系统字体)
            0xFFCECECE, "请输入卡密", //输入框提示内容颜色，输入框提示内容
            0xC200FF00, context.getSharedPreferences(fileName, context.MODE_PRIVATE).getString("kami", ""), //输入框输入内容颜色，输入框默认输入内容
            0, 0xFF424242,  0.5f, 0xCEFAFAFA,//输入框圆角半径, 输入框背景色, 输入框描边厚度, 输入框描边颜色

            //输入框事件监听器
            new AlGui.T_EditTextOnChangeListener(){
                @Override
                public void beforeTextChanged(EditText edit, CharSequence charSequence, int start, int count, int after) {
                    // 在文本变化之前执行的内容
                }

                @Override
                public void onTextChanged(EditText edit, CharSequence charSequence, int start, int before, int count) {
                    // 在文本变化时执行的内容
                }

                @Override
                public void afterTextChanged(EditText edit, Editable editable) {
                    // 在文本变化之后执行的内容
                }

                //输入框按钮点击事件
                @Override
                public  void buttonOnClick(EditText edit, View button, TextView buttonText, boolean isChecked) {
                    //这样来获取输入框当前输入的内容
                    String editText=edit.getText().toString();
                }
            }
        );

        //根布局增加一个存放文本的右对齐布局
        LinearLayout textLayout2 = AlGui.GUI(context).addLinearLayout
        (
            //线性布局属性配置🛠️
            layout,//父布局
            Gravity.RIGHT,//子视图对齐方式
            LinearLayout.VERTICAL,//线性布局方向
            LinearLayout.LayoutParams.MATCH_PARENT,//线性布局宽
            LinearLayout.LayoutParams.WRAP_CONTENT//线性布局高
        );
        //文本布局增加一个文本
        AlGui.GUI(context).addTextView(textLayout2, Html.fromHtml("没有卡密？<a href='https://qm.qq.com/q/3U1H3JMf5Y' color=\"#E8EAF6\">点我购买</a>"), 11, 0xFFE0E0E0, null);



        //根布局增加一个普通按钮
        AlGui.GUI(context).addButton
        (
            //普通按钮属性配置🛠️
            layout,//父布局
            "登录", 12, 0xFFFFFFFF, null,//按钮文本，文本大小，文本颜色，文本字体
            0,//按钮圆角半径
            0xCE536DFE,//按钮背景颜色
            0, 0xC1FFFFFF,//按钮描边大小，描边颜色
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,//按钮宽度，按钮高度
            new  AlGui.T_ButtonOnChangeListener(){
                String info="null";//存储信息
                Long dqTime=-999l;//存储到期时间戳
                boolean p=false;
                @Override
                public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                    info = "null";
                    dqTime = -999l;
                    p = false;
                    //按钮点击时执行的内容
                    Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String sign = NetworkVerification + "/api/?id=kmlogon"; // 卡密登录
                                String content;
                                String random;

                                String kami=editText.getText().toString();//获取输入的卡密
                                String id=getMachineCode();//获取当前机器码
                                Long time = System.currentTimeMillis() / 1000;//获取当前时间戳

                                String signs = encodeMD5("kami=" + kami + "&markcode=" + id + "&t=" + time + "&" + appKey);
                                String body="&app=" + appID + "&kami=" + kami + "&markcode=" + id + "&t=" + time + "&sign=" + signs;     
                                String init=sign + body;

                                random = UUID.randomUUID().toString().replace("-", "") + appKey + id;
                                try {
                                    String data = "data=" + RC4Util.encryRC4String(body, rc4_2, "UTF-8");
                                    content = RC4Util.decryRC4(UrlPost(init + "&app=" + appID, data + "&value=" + random), rc4_2, "UTF-8");

                                    JSONObject jsonObject = new JSONObject(content);
                                    int code=jsonObject.getInt("code");//是否登录成功
                                    String Message=jsonObject.getString("msg");
                                    String check=jsonObject.optString("check");//校验密钥
                                    Long timee=jsonObject.optLong("time");//服务器时间戳
                                    if (check.equals(encodeMD5(timee.toString() + appKey + id))) {
                                        info = "非法操作";
                                    } else if (timee - time > 30) {                
                                        info = "数据过期";
                                    } else if (timee - time < -30) {           
                                        info = "数据过期";
                                    } else if (code == successStatusCode) {//登录成功
                                        JSONObject json = new JSONObject(Message);                      
                                        Long vip=json.optLong("vip");
                                        dqTime = vip;
                                        //保存卡密
                                        SharedPreferences sp = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
                                        SharedPreferences.Editor sped=sp.edit();
                                        sped.putString("kami", kami);
                                        sped.apply();

                                        p = true;
                                    } else {
                                        //其他情况
                                        info = Message;
                                    }

                                } catch (JSONException e) {
                                    //异常情况
                                    info = "错误：" + e.getMessage();
                                } catch (Exception e) {
                                    //异常情况
                                    info = "错误：" + e.getMessage();
                                }
                            }



                        });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        info = "错误：" + e.getMessage();
                    }
                    if (p) {
                        AlGui.GUI(context).getMenuScrollingListLayout().removeView(layout);
                        Loading.initMenu();

                        //格式时间戳
                        GregorianCalendar gc=new GregorianCalendar();
                        gc.setTimeInMillis(dqTime * 1000);
                        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");//到期时间格式：yyyy-MM-dd(年月日) HH:mm:ss(时分秒) EEEE(星期几) EE(周几)
                        String str = df.format(gc.getTime());
                        AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "登录成功", "到期时间：" + str, 5000);

                        //心跳验证
                        b = true;
                        final Handler handler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message inputMessage) {
                                AlguiNetworkObtain task = new AlguiNetworkObtain(context, new AlguiNetworkObtain.NetworkCallback() {
                                        @Override
                                        public void onResult(String result) {
                                            if (result == null) {
                                                return;
                                            }

                                            try {
                                                String jsonContent = result.substring(result.indexOf("{"));
                                                JSONObject jsonObject = new JSONObject(jsonContent);
                                                Long currentTimestamp = jsonObject.getLong("t");//服务器时间戳
                                                // AlGuiBubbleNotification.Inform(context).showMessageNotification_Exquisite(null, "心跳验证测试", "时间戳：" + currentTimestamp + "|" + dqTime, 5000);

                                                //检查服务器时间戳是否大于到期时间戳
                                                if (currentTimestamp > dqTime) {
                                                    b = false;
                                                    AlGuiBubbleNotification.Inform(context).showMessageNotification_Exquisite(null, "卡密到期提醒", "你的卡密已到期，重新购买卡密才能继续使用哦！", 5000);

                                                    AlGui.GUI(context).getMenuScrollingListLayout().removeAllViews();//清除所有视图
                                                    AlGui.GUI(context).getMenuScrollingListLayout().removeAllViewsInLayout();//清除所有视图的布局
                                                    start();//重新网络验证

                                                }

                                            } catch (JSONException e) {

                                                return;
                                            }
                                        }
                                    }
                                );
                                task.execute("https://vv.video.qq.com/checktime?otype=json", "GET");
                            }
                        };
                        new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (b) {
                                        handler.sendEmptyMessage(0);
                                        try {
                                            Thread.sleep(xtTime * 1000); 
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();


                    } else {
                        AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "登录失败", info, 5000);
                    }
                }
            }
        );

        //根布局增加一个普通按钮
        AlGui.GUI(context).addButton
        (
            //普通按钮属性配置🛠️
            layout,//父布局
            "解绑", 12, 0xFFFFFFFF, null,//按钮文本，文本大小，文本颜色，文本字体
            0,//按钮圆角半径
            0xCE536DFE,//按钮背景颜色
            0, 0xC1FFFFFF,//按钮描边大小，描边颜色
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,//按钮宽度，按钮高度
            new  AlGui.T_ButtonOnChangeListener(){
                int num=-999;
                boolean p=false;
                String info="null";
                @Override
                public void onClick(View button, GradientDrawable back, TextView buttonText, boolean isChecked) {
                    num = -999;
                    p = false;
                    info = "null";
                    //按钮点击时执行的内容
                    Thread thread = new Thread(new Runnable() {         
                            @Override
                            public void run() {
                                String sign = NetworkVerification + "/api/?id=kmdismiss"; // 卡密解绑
                                String content;


                                String kami=editText.getText().toString();//获取输入的卡密
                                String id=getMachineCode();//获取当前机器码
                                Long time = System.currentTimeMillis() / 1000;//获取当前时间戳

                                String random = UUID.randomUUID().toString().replace("-", "") + appKey + id;          

                                String signs = encodeMD5("kami=" + kami + "&markcode=" + id + "&t=" + time + "&" + appKey);
                                String body="&app=" + appID + "&kami=" + kami + "&markcode=" + id + "&t=" + time + "&sign=" + signs;     
                                String init=sign + body;

                                try {
                                    String data = "data=" + RC4Util.encryRC4String(body, rc4_2, "UTF-8");
                                    content = RC4Util.decryRC4(UrlPost(init + "&app=" + appID, data + "&value=" + random), rc4_2, "UTF-8");
                                    JSONObject jsonObject = new JSONObject(content);
                                    String code=jsonObject.getString("code");//是否解绑成功
                                    String Message=jsonObject.getString("msg");

                                    if (code.equals("200")) {//解绑成功
                                        JSONObject json = new JSONObject(Message);                      
                                        num = json.getInt("num");
                                        p = true;
                                    } else {
                                        info = Message;
                                    }
                                } catch (JSONException e) {
                                    info = "错误：" + e.getMessage();

                                } catch (Exception e) {
                                    info = "错误：" + e.getMessage();
                                }
                            }


                        });

                    thread.start();

                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        info = "错误：" + e.getMessage();
                    }
                    if (p) {
                        AlGuiBubbleNotification.Inform(context).showSuccessNotification_Simplicity(null, "解绑成功", "还剩" + num + "次解绑机会", 5000);
                    } else {
                        AlGuiBubbleNotification.Inform(context).showMistakeNotification_Simplicity(null, "解绑失败", info, 5000);
                    }
                }
            }
        );


    }





}
