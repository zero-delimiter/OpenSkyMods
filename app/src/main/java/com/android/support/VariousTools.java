package com.android.support;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class VariousTools {
    private static CountDownLatch initLatch;
    private static TextToSpeech textToSpeech;
    private static boolean ttsReady = false;

    public static boolean copyToClipboard(Context context, String text) {
        if (context == null || text == null) {
            return false;
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Copied Text", text));
        return true;
    }

    public static boolean gotoWeb(Context context, String web) {
        if (context == null) {
            return false;
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web == null ? "https://cn.bing.com/" : web)));
        return true;
    }

    public static boolean joinQQGroup(Context context, String key) {
        if (context == null) {
            return false;
        }
        Intent intent = new Intent();
        intent.setData(
                Uri.parse(
                        "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D"
                                + key));
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean convertTextToSpeech(final Context context, final String text, final Locale language) {
        if (context == null || text == null) {
            return false;
        }
        if (ttsReady && textToSpeech != null) {
            speakNow(text, language);
            return true;
        }
        if (initLatch != null) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                initLatch.await();
                                if (ttsReady) {
                                    speakNow(text, language);
                                } else {
                                    Toast.makeText(context, "TTS引擎初始化失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .start();
            return true;
        }
        initLatch = new CountDownLatch(1);
        textToSpeech =
                new TextToSpeech(
                        context,
                        new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status == TextToSpeech.SUCCESS) {
                                    ttsReady = true;
                                    speakNow(text, language);
                                } else {
                                    Toast.makeText(context, "TTS引擎初始化失败，请检查系统设置中的文字转语音输出", Toast.LENGTH_LONG)
                                            .show();
                                }
                                initLatch.countDown();
                            }
                        });
        return true;
    }

    public static void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            ttsReady = false;
        }
        initLatch = null;
    }

    private static void speakNow(String text, Locale language) {
        if (textToSpeech == null || !ttsReady) {
            return;
        }
        Locale target = language == null ? Locale.getDefault() : language;
        int available = textToSpeech.isLanguageAvailable(target);
        if (available == TextToSpeech.LANG_AVAILABLE || available == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            textToSpeech.setLanguage(target);
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, (Bundle) null, null);
        }
    }
}
