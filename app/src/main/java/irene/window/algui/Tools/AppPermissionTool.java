package irene.window.algui.Tools;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 𝘽𝙮·艾琳 - ［Copyright © 2023 艾琳 版权所有］游戏逆向交流群730967224 - 作者QQ3353484607
 * @Date 2023/12/29 14:02
 * @Describe APP权限工具
 */
public class AppPermissionTool {

    public static final String TAG = "AppPermissionTool";
    //检查并申请所有权限 
    public static void initPermission(Context mContext, Activity mActivity) {

        boolean isGranted = true;

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (mContext.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (mContext.checkSelfPermission(Manifest.permission.RESTART_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (mContext.checkSelfPermission(Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (mContext.checkSelfPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (mContext.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }

            if (!isGranted) {
                mActivity.requestPermissions(new String[]{
                                                 Manifest.permission.READ_EXTERNAL_STORAGE,
                                                 Manifest.permission.ACCESS_COARSE_LOCATION,
                                                 Manifest.permission.RECORD_AUDIO,
                                                 Manifest.permission.CAMERA,
                                                 Manifest.permission.READ_CALENDAR,
                                                 Manifest.permission.CALL_PHONE,
                                                 Manifest.permission.SEND_SMS,
                                                 Manifest.permission.READ_CONTACTS,
                                                 Manifest.permission.BODY_SENSORS,
                                                 Manifest.permission.READ_CALL_LOG,
                                             }, 0);
                mActivity.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                        .ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.RESTART_PACKAGES,
                        Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND,
                        Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    102);




            }
        }

        if (!Settings.canDrawOverlays(mContext)) {
            mActivity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName())), 0);
        }
    }
    //申请储存权限
    public static void storePermission(Context context, Activity ac) {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (!isGranted) {
                ac.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            }
        }
    }
    //获取清单文件所有权限
    public static String[] getAllPermissionsFromManifest(Context context) {
        List<String> permissionsList = new ArrayList<>();

        try {
            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(((Activity)context).getComponentName(), PackageManager.GET_META_DATA);
            if (activityInfo != null && activityInfo.applicationInfo != null) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(activityInfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] permissions = packageInfo.requestedPermissions;
                if (permissions != null && permissions.length > 0) {
                    for (String permission : permissions) {
                        permissionsList.add(permission);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return permissionsList.toArray(new String[0]);
    }


    //批量检查申请权限
    private static int index=0;//权限码
    public static void batchApplyPermission(Context mContext, String... PermissionName) {
        /*Log.d("长度",""+PermissionName.length);

         for(int i=0;i<PermissionName.length;i++){
         Log.d("正在批量申请",PermissionName[i]);
         }*/
        Activity mActivity = (Activity)mContext;

        //检查应用是否具有 android.Manifest.permission.REQUEST_INSTALL_PACKAGES 权限。
        //如果没有该权限，您的应用将无法请求其他权限，这里如果没有则申请
        if (mContext.checkSelfPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            mActivity.requestPermissions(
                new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                index);
        }

        for (String p : PermissionName) {
            if (p != null) {
                //检查是否未授予
                if (mContext.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    index++;
                    //如果是申请悬浮窗权限则使用特殊方式
                    if (p.equals("android.permission.SYSTEM_ALERT_WINDOW")) {
                        floatingWindowPermission(mContext);

                    } else {
                        //普通权限直接进行申请
                        mActivity.requestPermissions(
                            new String[]{p},
                            index);
                    }
                }
            }
        }
        index++;
    }

    //申请悬浮窗权限
    public static void floatingWindowPermission(Context mContext) {
        if(mContext==null){
            return;
        }
        
        if (!Settings.canDrawOverlays(mContext)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mContext.getPackageName()));
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                mContext.startActivity(intent);
            }
        }
    }
    
    //检查是否授予了悬浮窗权限
    public static boolean checkOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查是否拥有悬浮窗权限
            if (!Settings.canDrawOverlays(context)) {
                // 悬浮窗权限未被授予，需要请求权限
                return false;
            }
        }
        // 悬浮窗权限已被授予
        return true;
    }


    //检查当前应用程序AndroidManifest.xml安卓清单文件中 是否存在某个权限  分别传入上下文和权限代号
    //实例：
    /*if (AppUtilsTool.isAndroidManifestAuthority(context, "android.permission.SYSTEM_ALERT_WINDOW")) {
     Log.d("艾琳debug","清单文件中存在悬浮窗权限");
     }else{
     Log.d("艾琳debug","清单文件中不存在悬浮窗权限");
     }*/
    public static boolean isAndroidManifestPermissionExist(Context context, String Authority) {
        String permission = Authority;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null) {
                for (String p : permissions) {
                    if (p.equals(permission)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
