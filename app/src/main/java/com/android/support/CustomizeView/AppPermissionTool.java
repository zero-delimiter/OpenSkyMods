package com.android.support.CustomizeView;

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

public class AppPermissionTool {
    public static final String TAG = "AppPermissionTool";
    private static int index = 0;

    public static void initPermission(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean isGranted = true;
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (context.checkSelfPermission(Manifest.permission.RESTART_PACKAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (context.checkSelfPermission(Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND)
                    != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (context.checkSelfPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (context.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (!isGranted) {
                activity.requestPermissions(
                        new String[]{
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
                        },
                        0);
                activity.requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.RESTART_PACKAGES,
                                Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND,
                                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                Manifest.permission.SYSTEM_ALERT_WINDOW,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        102);
            }
        }

        if (!Settings.canDrawOverlays(context)) {
            activity.startActivityForResult(
                    new Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + context.getPackageName())),
                    0);
        }
    }

    public static void storePermission(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean isGranted =
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED;
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (!isGranted) {
                activity.requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        102);
            }
        }
    }

    public static String[] getAllPermissionsFromManifest(Context context) {
        List<String> permissionsList = new ArrayList<>();
        try {
            ActivityInfo activityInfo =
                    context
                            .getPackageManager()
                            .getActivityInfo(((Activity) context).getComponentName(), PackageManager.GET_META_DATA);
            if (activityInfo != null && activityInfo.applicationInfo != null) {
                PackageInfo packageInfo =
                        context
                                .getPackageManager()
                                .getPackageInfo(activityInfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] permissions = packageInfo.requestedPermissions;
                if (permissions != null) {
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

    public static void batchApplyPermission(Context context, String... permissionNames) {
        Activity activity = (Activity) context;
        if (context.checkSelfPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, index);
        }
        for (String permission : permissionNames) {
            if (permission != null
                    && context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                index++;
                if ("android.permission.SYSTEM_ALERT_WINDOW".equals(permission)) {
                    floatingWindowPermission(context);
                } else {
                    activity.requestPermissions(new String[]{permission}, index);
                }
            }
        }
        index++;
    }

    public static void floatingWindowPermission(Context context) {
        if (context == null) {
            return;
        }
        if (!Settings.canDrawOverlays(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.startActivity(
                        new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.getPackageName())));
            } else {
                context.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            }
        }
    }

    public static boolean checkOverlayPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    public static boolean isAndroidManifestPermissionExist(Context context, String permission) {
        try {
            PackageInfo packageInfo =
                    context
                            .getPackageManager()
                            .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null) {
                for (String item : permissions) {
                    if (item.equals(permission)) {
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
