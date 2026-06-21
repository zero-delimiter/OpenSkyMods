package com.android.support;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

public class Launcher extends Service {

    private static final String CHANNEL_ID = "floating_menu_channel";
    private static final String CHANNEL_NAME = "悬浮菜单";
    private static final int NOTIFICATION_ID = 1001;

    Menu menu;

    // When this Class is called the code in this function will be executed
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
        }
        startForeground(NOTIFICATION_ID, createNotification());

        menu = new Menu(this);
        menu.SetWindowManagerWindowService();
        menu.ShowMenu();

        // Create a handler for this Class
        final Handler handler = new Handler();
        handler.post(
                new Runnable() {
                    public void run() {
                        Thread();
                        handler.postDelayed(this, 1000);
                    }
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            notificationManager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW));
        }
    }

    private Notification createNotification() {
        Notification.Builder builder =
                Build.VERSION.SDK_INT >= 26
                        ? new Notification.Builder(this, CHANNEL_ID)
                        : new Notification.Builder(this);
        builder
                .setContentTitle("Sky_Mod菜单")
                .setContentText("正在运行中...")
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setPriority(Notification.PRIORITY_MIN)
                .setOngoing(true)
                .setVisibility(Notification.VISIBILITY_SECRET);
        if (Build.VERSION.SDK_INT >= 26) {
            builder.setChannelId(CHANNEL_ID);
        }
        return builder.build();
    }

    // Check if we are still in the game. If now our menu and menu button will dissapear
    private boolean isNotInGame() {
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        return runningAppProcessInfo.importance != 100;
    }

    private void Thread() {
        if (isNotInGame()) {
            menu.setVisibility(View.VISIBLE);
        } else {
            menu.setVisibility(View.VISIBLE);
        }
    }

    // Destroy our View
    public void onDestroy() {
        super.onDestroy();
        menu.onDestroy();
    }

    // Same as above so it wont crash in the background and therefore use alot of Battery life
    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    // Override our Start Command so the Service doesnt try to recreate itself when the App is closed
    public int onStartCommand(Intent intent, int i, int i2) {
        return Service.START_STICKY;
    }
}
