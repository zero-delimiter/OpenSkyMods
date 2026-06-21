package irene.window.algui;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * @Author 𝘽𝙮·艾琳 - 手游逆向交流群101640882
 * @Date 2023/010/11 15:38
 * @Describe ALGUI服务
 */
public class AlguiService extends Service {

    private Context mContext;

    //这个方法用于绑定Service组件和其他组件之间的交互，在这里返回null表示不支持绑定
    @Override
    public IBinder onBind(Intent Intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Loading.start(mContext);//启动

    }

    //在Service销毁时调用此方法
    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}
