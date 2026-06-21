package irene.window.algui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * @Author 𝘽𝙮·艾琳 - ［Copyright © 2023 艾琳 版权所有］游戏逆向交流群730967224 - 作者QQ3353484607
 * @Date 2023/12/30 19:31
 * @Describe 异步获取网络数据
 */
public class AlguiNetworkObtain extends AsyncTask<String, Void, String> {
    private Context aContext=null;
    //private Activity aActivity=null;
    private NetworkCallback aCallback;
    //回调接口
    public static interface NetworkCallback {
        void onResult(String result);
    }

    public AlguiNetworkObtain(Context context, NetworkCallback callback) {
        if (context == null || callback == null) {
            return;
        }
        aContext = context;
        // aActivity = (Activity)context;
        aCallback = callback;

    }

    /*doInBackground 方法：
     doInBackground 方法是在 AsyncTask 执行时被调用的
     具体地说，当你调用 execute 方法来启动 AsyncTask 时，它会在后台线程中执行 doInBackground 方法
     在这个方法中，用于执行耗时的网络请求操作，获取 网络 数据，并将结果返回给 onPostExecute 方法
     */
    @Override
    protected String doInBackground(String... params) {
        String apiUrl = params[0];//获取传入的api接口
        HttpURLConnection connection = null;

        BufferedReader reader = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(params[1]);//设置网络请求类型 GET/POST
            connection.setUseCaches(false);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                //正常情况 返回获取到的json数据
                return response.toString();
            } else {
                //处理请求失败的情况
                return "Algui 请求失败！Error response code: " + responseCode;
            }
        } catch (Exception e) {
            //处理异常情况
            return "Algui  Error: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("Algui ", "Error closing BufferedReader", e);
                }
            }
        }
    }

    /*
     onPostExecute 方法：
     当 doInBackground 方法执行完毕后将自动调用
     接收从 doInBackground 方法中传递回来的 网络 数据，并对其进行处理
     通常，这个方法用于更新应用程序中的 UI 界面或者进行其他相关操作
     */
    @Override
    protected void onPostExecute(String result) {
        // Log.d("Algui  EeturnData", result);
        if (aCallback != null) {
            aCallback.onResult(result);
        }

    }




}
