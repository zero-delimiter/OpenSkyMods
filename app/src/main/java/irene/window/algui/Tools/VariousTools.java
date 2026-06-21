package irene.window.algui.Tools;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import android.content.ClipboardManager;
import android.content.ClipData;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/28 12:46
 * @Describe 各种工具
 */
public class VariousTools {

    public static final String TAG = "VariousTools";



  
    //复制文本
    public static boolean copyToClipboard(Context context, String text) {
        if(context==null||text==null){
            return false;
        }
        
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        return true;
    }


    //跳转网站
    public static boolean gotoWeb(Context context, String web) {
        if (context == null) {
            return false;
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web != null ?web: "https://cn.bing.com/")));
        return true;
	}




    /****************
     *
     * 发起添加群流程。群号：游戏逆向爱好者(730967224) 的 key 为： qeey7hum96m64eaHkKKjHC6micNY9_oI
     * 调用 joinQQGroup(qeey7hum96m64eaHkKKjHC6micNY9_oI) 即可发起手Q客户端申请加群 游戏逆向爱好者(730967224)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public static boolean joinQQGroup(Context context, String key) {
        if (context == null) {
            return false;
        }
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    /**
     * 将文本转换为语音输出
     * @param context 上下文环境
     * @param text 需要转换为语音的文本
     * @param language 语言
     */
    private static TextToSpeech textToSpeech;//tts引擎
    public static boolean convertTextToSpeech(final Context tContext, final String text, final Locale language) {
        if (tContext == null || text==null ) {
            return false;
        }
        // 检查设备是否安装了 TTS 引擎
        PackageManager pm = tContext.getPackageManager();
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        if (pm.resolveActivity(checkIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            //设备安装了TTS引擎
            // 创建一个 TextToSpeech 对象并初始化
            textToSpeech = new TextToSpeech(tContext, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        // 初始化成功
                        if (status == TextToSpeech.SUCCESS) {

                            // 获取用户当前正在使用的 TTS 引擎的包名
                            String currentEngine = textToSpeech.getDefaultEngine();
                            // 设置 TextToSpeech 实例为用户当前正在使用的 TTS 引擎
                            textToSpeech.setEngineByPackageName(currentEngine);

                            Locale locale=null;
                            if (language != null) {
                                locale = language;
                            } else {
                                //使用当前系统默认的语言
                                locale = Locale.getDefault();
                            }


                            // 检查所需语言是否可用
                            if (textToSpeech.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                                // 设置语言
                                textToSpeech.setLanguage(locale);
                                // 将文本转换为语音并朗读出来
                                textToSpeech.speak(text , TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                Toast.makeText(tContext, "该语言无法识别此内容", Toast.LENGTH_LONG).show();   

                            }

                        }
                    }
                });
            // 关闭 TextToSpeech 引擎
            textToSpeech.shutdown();
            return true;
        } else {
            //设备未安装TTS引擎
            Toast.makeText(tContext, "未安装系统自带的TTS引擎", Toast.LENGTH_LONG).show();   
            return false;
        }
    }
}
