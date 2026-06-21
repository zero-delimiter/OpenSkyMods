package com.android.support;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DynamicLoader {
    private static final long MIN_VALID_SIZE = 1024L;
    private static final String SO_FILENAME = "libAndroidMod.so";
    private static final String SO_URL = "https://wpan.cdndns.site/down/libAndroidMod";
    private static volatile boolean sFallbackToBuiltin = false;

    public static void startWithDownload(final Context context, final Runnable callback) {
        if (sFallbackToBuiltin) {
            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(callback);
            }
            return;
        }

        final File soFile = new File(context.getDir("airen", 0), SO_FILENAME);
        final Handler uiHandler = new Handler(Looper.getMainLooper());

        if (soFile.exists() && soFile.length() >= MIN_VALID_SIZE) {
            uiHandler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.load(soFile.getAbsolutePath());
                                if (callback != null) {
                                    callback.run();
                                }
                            } catch (Throwable t) {
                                soFile.delete();
                                sFallbackToBuiltin = true;
                                Toast.makeText(
                                                context,
                                                "加载失败，使用内置: "
                                                        + t.toString(),
                                                Toast.LENGTH_LONG)
                                        .show();
                                if (callback != null) {
                                    callback.run();
                                }
                            }
                        }
                    });
            return;
        }

        if (soFile.exists()) {
            soFile.delete();
        }

        new Thread(new DownloadTask(context, soFile, uiHandler, callback)).start();
    }

    private static class DownloadTask implements Runnable {
        private final Context context;
        private final File soFile;
        private final Handler uiHandler;
        private final Runnable callback;

        DownloadTask(Context context, File soFile, Handler uiHandler, Runnable callback) {
            this.context = context;
            this.soFile = soFile;
            this.uiHandler = uiHandler;
            this.callback = callback;
        }

        @Override
        public void run() {
            final String builtinSuffix = "，使用内置";
            InputStream input = null;
            FileOutputStream output = null;
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(SO_URL).openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                int contentLength = connection.getContentLength();
                if (responseCode != HttpURLConnection.HTTP_OK || contentLength <= 0) {
                    finishWithError(
                            "无法获取文件大小 "
                                    + responseCode
                                    + builtinSuffix,
                            false);
                    return;
                }

                input = connection.getInputStream();
                File parent = soFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                output = new FileOutputStream(soFile);
                byte[] buffer = new byte[8192];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();

                long size = soFile.length();
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                String sizeText = String.format("%.2f MB", size / 1048576.0);
                                Toast.makeText(
                                                context,
                                                "下载完成: " + sizeText,
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });

                if (size < MIN_VALID_SIZE) {
                    soFile.delete();
                    finishWithError(
                            "下载无效，使用内置",
                            false);
                    return;
                }

                try {
                    Runtime.getRuntime().exec("chmod 777 " + soFile.getAbsolutePath());
                } catch (Exception ignored) {
                }

                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    System.load(soFile.getAbsolutePath());
                                    Toast.makeText(
                                                    context,
                                                    "热更新加载成功",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    finishSuccess();
                                } catch (Throwable t) {
                                    soFile.delete();
                                    sFallbackToBuiltin = true;
                                    Toast.makeText(
                                                    context,
                                                    "加载失败，使用内置: "
                                                            + t.toString(),
                                                    Toast.LENGTH_LONG)
                                            .show();
                                    finishWithError(null, false);
                                }
                            }
                        });
            } catch (Exception e) {
                finishWithError(
                        "下载异常: "
                                + e.getClass().getSimpleName()
                                + builtinSuffix,
                        false);
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (Exception ignored) {
                }
            }
        }

        private void finishSuccess() {
            finishCommon(true);
        }

        private void finishWithError(final String errorMessage, boolean suppressToast) {
            if (!suppressToast && errorMessage != null) {
                uiHandler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
            }
            finishCommon(false);
        }

        private void finishCommon(boolean success) {
            if (!success) {
                sFallbackToBuiltin = true;
            }
            if (callback != null) {
                uiHandler.post(callback);
            }
        }
    }
}
