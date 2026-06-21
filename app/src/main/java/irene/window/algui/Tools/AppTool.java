package irene.window.algui.Tools;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import irene.window.algui.AlGuiData;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/17 06:17
 * @Describe APP工具
 */
public class AppTool {

    public static final String TAG = "AppTool";

    // 检测当前是否有网络连接
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        //检查当前应用程序清单文件中是否声明了查看网络连接的权限代号 android.permission.ACCESS_NETWORK_STATE
        if (AppPermissionTool.isAndroidManifestPermissionExist(context, "android.permission.ACCESS_NETWORK_STATE")) {
        } else {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    //返回桌面
    public static void backToDesktop(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                             | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    };

    //在通知栏上显示一个通知
    private static int index=335;//用于通知的ID，每个通知ID必须不同，否则相同ID的通知将无法共存
    public static void showNotificationBar(Context tContext, String title, String content) {
        String channelId = "艾琳QQ3353484607";
        Notification notification = new Notification.Builder(tContext, channelId)
            .setContentTitle(title != null ?title: "通知")//设置通知标题
            .setContentText(content != null ?content: "内容")//设置通知内容
            //.setAutoCancel(false)//设置点击后是否清除通知
            //.setWhen(1)//设置时间
            .setPriority(2) // 设置通知优先级 -2-2 越大越高

            // .SetContentIntent(null);//设置点击后的跳转
            .setWhen(System.currentTimeMillis())//设置通知的时间戳
            .setSmallIcon(android.R.drawable.sym_def_app_icon)//设置小图标
            //设置大图标
            .setLargeIcon(ImageTool.getBase64Image(AlGuiData.getOther_Icon_Hacker2()))   //设置大图标
            .build();

        NotificationManager notificationManager = (NotificationManager)tContext.getSystemService(tContext.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelId, "游戏外挂稳定中...", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(index, notification);
        index++;//ID+1
    }



    //检查当前应用程序是否处于前台
    public static boolean isAppInForeground() {
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        return runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }

    //检测当前应用程序是否已授予ROOT权限
    public static boolean isRootEnabled() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            OutputStream outputStream = process.getOutputStream();
            outputStream.write("echo \"test\" >/dev/null\n".getBytes());
            outputStream.flush();
            outputStream.close();
            int exitCode = process.waitFor();
            return (exitCode == 0);
        } catch (Exception e) {
            // 忽略异常
        } finally {
            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception e) {
                    // 忽略异常
                }
            }
        }
        return false;
    }


    /**
     * 获得root权限
     * @param context 上下文
     * @return
     */
    public static boolean getRootPermission(Context context) {
        String packageCodePath = context.getPackageCodePath();
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + packageCodePath;
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * 检测APP是否安装
     *
     * @param packageName
     * @return
     */
    public boolean isInstalled(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo info : installedPackages) {
                if (info.packageName.equals(packageName))
                    return true;
            }
        }
        return false;
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取版本名字
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    /**
     * 获取apk的名称
     * @param context 上下文
     * @return String app名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager e = context.getPackageManager();
            PackageInfo packageInfo = e.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
            return "unKnown";
        }
    }

    /**
     * 获取应用图标
     * @param context 上下文
     * @param packageName 包名
     * @return
     */
    public static Drawable getAppIcon(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Drawable appIcon = null;
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            appIcon = applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appIcon;
    }

    // 获取应用包名
    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    // 判断应用是否为系统应用
    public static boolean isSystemApp(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    // 获取应用的签名信息
    public static String getAppSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            if (signatures.length > 0) {
                Signature signature = signatures[0];
                return signature.toCharsString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "null";
    }



    //获取当前应用的UID
    public static int getAppUid(Context context) {
        return context.getApplicationInfo().uid;
    }

    //检测当前设备的哪些应用与自己当前应用的UID相同，并返回应用名称列表
    public static List<String> getSameUidAppNames(Context context) {
        int myUid = getAppUid(context);
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> sameUidAppNames = new ArrayList<>();

        for (ApplicationInfo appInfo : appList) {
            if (appInfo.uid == myUid && !appInfo.packageName.equals(context.getPackageName())) {
                String appName = appInfo.loadLabel(packageManager).toString();
                sameUidAppNames.add(appName);
            }
        }

        return sameUidAppNames;
    }

    //获取当前应用的入口Activity名称
    public static String getLauncherActivityName(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) {
            return null;
        }
        ComponentName cn = intent.getComponent();
        return cn.getClassName();
    }

    /**
     * 服务是否在运行
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo si : servicesList) {
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }


    //检测调试模式
    public static boolean isDebugMode(Context context) {
        return true;
    }




}
