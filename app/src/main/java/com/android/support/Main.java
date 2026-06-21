package com.android.support;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

public class Main {

    // Load lib
    static {
        System.loadLibrary("AndroidMod");
    }

    private static native void CheckOverlayPermission(Context context);

    static native void Get_AndroidID(String androidId);

    public static void StartWithoutPermission(Context context) {
        CrashHandler.init(context, true);
        if (context instanceof Activity) {
            // Check if context is an Activity.
            Menu menu = new Menu(context);
            menu.SetWindowManagerActivity();
            menu.ShowMenu();
        } else {
            // Anything else, ask for permission
            CheckOverlayPermission(context);
        }
    }

    public static void Start(Context context) {
        CrashHandler.init(context, false);
        Get_AndroidID(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

        new Handler(Looper.getMainLooper())
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                CheckOverlayPermission(context);
                            }
                        },
                        10000L);
    }
}
