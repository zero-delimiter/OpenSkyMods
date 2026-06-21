package irene.window.algui.Tools;
import android.content.Context;

/**
 * @Author 𝘽𝙮·艾琳 - ［Copyright © 2023 艾琳 版权所有］游戏逆向交流群730967224 - 作者QQ3353484607
 * @Date 2023/12/30 18:53
 * @Describe 外挂工具
 */
public class HackerTool {

    public static final String TAG = "HackerTool";

    // 执行shell命令
    public static void shell(String shell) {
        try {
            Runtime.getRuntime().exec(shell, null, null);
        } catch (Exception e) {
            //DeBug.showDebugToast(mContext, "功能执行失败 请重试！", "可能游戏没有加载库 重试无效则重进");
            e.printStackTrace();
        }
    }

    // 64位和32位通用调用单个二进制方法 二进制文件名
    public static void linuxHackerFile(Context mContext, String name) {

        //if (arm == 64) {
        //arm64位模式
        // Tools.writeWriteAssets(mContext, mContext.getCacheDir() + "/", name);
        shell("chmod 777 " + mContext.getApplicationInfo().nativeLibraryDir + "/" + name);
        shell(mContext.getApplicationInfo().nativeLibraryDir + "/" + name);
        shell("chmod 777 " + mContext.getCacheDir() + "/" + name);
        shell(mContext.getCacheDir() + "/" + name);
        //} else {
        //arm32位模式
        shell("chmod 777 " +  "/data/data/" + mContext.getPackageName() + "/lib/" + name);
        shell("/data/data/" + mContext.getPackageName() + "/lib/" + name);
        //}
    }

    // 64和32位通用开关不同状态调用不同二进制方法 开启功能二进制文件名 关闭功能二进制文件名 开关状态标识
    public static void linuxHackerFile(Context mContext, String onSoFile, String offSoFile, boolean isChecked) {
        //if (arm == 64) {
        //arm64位模式 
        //Tools.writeWriteAssets(mContext, mContext.getCacheDir() + "/", isChecked ?onSoFile: offSoFile);
        shell("chmod 777 " + mContext.getApplicationInfo().nativeLibraryDir + "/" + (isChecked ?onSoFile: offSoFile));
        shell(mContext.getApplicationInfo().nativeLibraryDir + "/" + (isChecked ?onSoFile: offSoFile));
        shell("chmod 777 " + mContext.getCacheDir() + "/" + (isChecked ?onSoFile: offSoFile));
        shell(mContext.getCacheDir() + "/" + (isChecked ?onSoFile: offSoFile));
        //} else {
        //arm32位模式
        shell("chmod 777 " +  "/data/data/" + mContext.getPackageName() + "/lib/" + (isChecked ?onSoFile: offSoFile));
        shell("/data/data/" + mContext.getPackageName() + "/lib/" + (isChecked ?onSoFile: offSoFile));
        // }
    }

}
