package irene.window.algui;
import android.content.Context;
import android.content.Intent;
import irene.window.algui.Tools.AppPermissionTool;


/**
 * @Author 𝘽𝙮·艾琳 - ［Copyright © 2023 艾琳 版权所有］游戏逆向交流群730967224 - 作者QQ3353484607
 * @Date 2023/12/30 16:31
 * @Describe ALGUI加载
 */
public class AlguiLoading {

    public static final String TAG = "Main";
    //全局上下文
    public static Context oContext;

    public static void start(Context context) {
        if (context == null) {
            return;
        }
        oContext = context;//获取全局上下文
        AppPermissionTool.floatingWindowPermission(context);//申请悬浮窗权限
        context.startService(new Intent(context, AlguiService.class));//启动窗口服务
    }


}
