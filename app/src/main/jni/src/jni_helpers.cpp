#include "include/jni_helpers.h"

#if ENABLE_DIAGNOSTIC_LOGS
#include <android/log.h>
#endif

#include <cstdarg>
#include <cstdlib>
#include <thread>
#include <unistd.h>

namespace android_mod {

#if ENABLE_DIAGNOSTIC_LOGS

namespace {

void vlog(int priority, const char *fmt, va_list ap) {
    __android_log_vprint(priority, kLogTag, fmt, ap);
}

} // namespace

void logInfo(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vlog(ANDROID_LOG_INFO, fmt, ap);
    va_end(ap);
}

void logWarn(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vlog(ANDROID_LOG_WARN, fmt, ap);
    va_end(ap);
}

void logError(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vlog(ANDROID_LOG_ERROR, fmt, ap);
    va_end(ap);
}

#endif

std::string toString(JNIEnv *env, jstring value) {
    if (!env || !value) {
        return {};
    }
    const char *chars = env->GetStringUTFChars(value, nullptr);
    if (!chars) {
        return {};
    }
    std::string out(chars);
    env->ReleaseStringUTFChars(value, chars);
    return out;
}

jstring toJString(JNIEnv *env, const std::string &value) {
    return env->NewStringUTF(value.c_str());
}

jobjectArray toJStringArray(JNIEnv *env, const std::vector<std::string> &items) {
    jclass string_cls = env->FindClass("java/lang/String");
    if (!string_cls) {
        clearPendingException(env, "FindClass(java/lang/String)");
        return nullptr;
    }
    jobjectArray array = env->NewObjectArray(static_cast<jsize>(items.size()), string_cls, nullptr);
    for (jsize i = 0; i < static_cast<jsize>(items.size()); ++i) {
        jstring item = env->NewStringUTF(items[static_cast<std::size_t>(i)].c_str());
        env->SetObjectArrayElement(array, i, item);
        env->DeleteLocalRef(item);
    }
    env->DeleteLocalRef(string_cls);
    return array;
}

bool clearPendingException(JNIEnv *env, const char *where) {
    if (!env || !env->ExceptionCheck()) {
        return false;
    }
    logWarn("clearing JNI exception at %s", where ? where : "unknown");
    env->ExceptionDescribe();
    env->ExceptionClear();
    return true;
}

jclass findClass(JNIEnv *env, const char *name) {
    jclass cls = env->FindClass(name);
    if (!cls) {
        clearPendingException(env, name);
    }
    return cls;
}

jmethodID getMethod(JNIEnv *env, jclass cls, const char *name, const char *sig) {
    jmethodID method = cls ? env->GetMethodID(cls, name, sig) : nullptr;
    if (!method) {
        clearPendingException(env, name);
    }
    return method;
}

jmethodID getStaticMethod(JNIEnv *env, jclass cls, const char *name, const char *sig) {
    jmethodID method = cls ? env->GetStaticMethodID(cls, name, sig) : nullptr;
    if (!method) {
        clearPendingException(env, name);
    }
    return method;
}

void showToast(JNIEnv *env, jobject context, const std::string &text, bool long_duration) {
    if (!env || !context) {
        return;
    }
    jclass toast_cls = findClass(env, "android/widget/Toast");
    if (!toast_cls) {
        return;
    }
    jmethodID make_text = getStaticMethod(
        env,
        toast_cls,
        "makeText",
        "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    jmethodID show = getMethod(env, toast_cls, "show", "()V");
    if (!make_text || !show) {
        env->DeleteLocalRef(toast_cls);
        return;
    }
    jstring msg = env->NewStringUTF(text.c_str());
    jobject toast = env->CallStaticObjectMethod(toast_cls, make_text, context, msg, long_duration ? 1 : 0);
    clearPendingException(env, "Toast.makeText");
    if (toast) {
        env->CallVoidMethod(toast, show);
        clearPendingException(env, "Toast.show");
        env->DeleteLocalRef(toast);
    }
    env->DeleteLocalRef(msg);
    env->DeleteLocalRef(toast_cls);
}

void callMenuReloadFeatures(JNIEnv *env) {
    jclass menu_cls = findClass(env, "com/android/support/Menu");
    if (!menu_cls) {
        return;
    }
    jmethodID reload = getStaticMethod(env, menu_cls, "ReloadFeatures", "()V");
    if (reload) {
        env->CallStaticVoidMethod(menu_cls, reload);
        clearPendingException(env, "Menu.ReloadFeatures");
    }
    env->DeleteLocalRef(menu_cls);
}

void setMenuLoggedIn(JNIEnv *env, bool logged_in) {
    jclass menu_cls = env ? env->FindClass("com/android/support/Menu") : nullptr;
    if (!menu_cls) {
        clearPendingException(env, "com/android/support/Menu");
        originalLogPrint(ANDROID_LOG_ERROR, "Mod_Menu", "Cannot find Menu class");
        return;
    }

    jfieldID is_logged_in = env->GetStaticFieldID(menu_cls, "isLoggedIn", "Z");
    if (!is_logged_in) {
        clearPendingException(env, "isLoggedIn");
        originalLogPrint(ANDROID_LOG_ERROR, "Mod_Menu", "Cannot find isLoggedIn field");
        env->DeleteLocalRef(menu_cls);
        return;
    }

    env->SetStaticBooleanField(menu_cls, is_logged_in, logged_in ? JNI_TRUE : JNI_FALSE);
    clearPendingException(env, "Menu.isLoggedIn");
    originalLogPrint(ANDROID_LOG_DEBUG, "Mod_Menu", "Login status set to: %d", logged_in ? 1 : 0);
    env->DeleteLocalRef(menu_cls);
}

std::string getPackageName(JNIEnv *env, jobject context) {
    if (!env || !context) {
        return {};
    }
    jclass context_cls = env->GetObjectClass(context);
    jmethodID get_package_name = getMethod(env, context_cls, "getPackageName", "()Ljava/lang/String;");
    std::string package_name;
    if (get_package_name) {
        auto package = static_cast<jstring>(env->CallObjectMethod(context, get_package_name));
        clearPendingException(env, "Context.getPackageName");
        package_name = toString(env, package);
        if (package) {
            env->DeleteLocalRef(package);
        }
    }
    if (context_cls) {
        env->DeleteLocalRef(context_cls);
    }
    return package_name;
}

namespace {

int getAndroidSdkInt(JNIEnv *env) {
    jclass version_cls = findClass(env, "android/os/Build$VERSION");
    if (!version_cls) {
        return 0;
    }
    jfieldID sdk_int = env->GetStaticFieldID(version_cls, "SDK_INT", "I");
    int value = sdk_int ? env->GetStaticIntField(version_cls, sdk_int) : 0;
    clearPendingException(env, "Build.VERSION.SDK_INT");
    env->DeleteLocalRef(version_cls);
    return value;
}

void startLauncherService(JNIEnv *env, jobject context) {
    jclass context_cls = env->GetObjectClass(context);
    jclass intent_cls = findClass(env, "android/content/Intent");
    jclass launcher_cls = findClass(env, "com/android/support/Launcher");
    if (!context_cls || !intent_cls || !launcher_cls) {
        if (context_cls) env->DeleteLocalRef(context_cls);
        if (intent_cls) env->DeleteLocalRef(intent_cls);
        if (launcher_cls) env->DeleteLocalRef(launcher_cls);
        return;
    }

    jmethodID intent_ctor = getMethod(env, intent_cls, "<init>", "(Landroid/content/Context;Ljava/lang/Class;)V");
    jobject intent = intent_ctor ? env->NewObject(intent_cls, intent_ctor, context, launcher_cls) : nullptr;
    clearPendingException(env, "new Launcher Intent");
    if (intent) {
        const char *method_name = getAndroidSdkInt(env) <= 25 ? "startService" : "startForegroundService";
        jmethodID start_service = getMethod(
            env,
            context_cls,
            method_name,
            "(Landroid/content/Intent;)Landroid/content/ComponentName;");
        if (start_service) {
            env->CallObjectMethod(context, start_service, intent);
            clearPendingException(env, method_name);
        }
        env->DeleteLocalRef(intent);
    }

    env->DeleteLocalRef(launcher_cls);
    env->DeleteLocalRef(intent_cls);
    env->DeleteLocalRef(context_cls);
}

void exitAfterOverlayPermissionDelay() {
    std::thread([] {
        usleep(5000000);
        std::exit(0);
    }).detach();
}

} // namespace

void startOverlayPermissionOrMenu(JNIEnv *env, jobject context) {
    if (!env || !context) {
        return;
    }

    originalLogPrint(ANDROID_LOG_INFO, "Mod_Menu", "Check overlay permission");

    bool can_draw = true;
    if (getAndroidSdkInt(env) >= 23) {
        jclass settings_cls = findClass(env, "android/provider/Settings");
        can_draw = false;
        if (settings_cls) {
        jmethodID can_draw_overlays = env->GetStaticMethodID(
            settings_cls,
            "canDrawOverlays",
            "(Landroid/content/Context;)Z");
        if (can_draw_overlays) {
            can_draw = env->CallStaticBooleanMethod(settings_cls, can_draw_overlays, context);
#if ENABLE_DIAGNOSTIC_LOGS
            clearPendingException(env, "Settings.canDrawOverlays");
#else
            clearPendingException(env, nullptr);
#endif
        } else {
#if ENABLE_DIAGNOSTIC_LOGS
            clearPendingException(env, "Settings.canDrawOverlays missing");
#else
            clearPendingException(env, nullptr);
#endif
        }
        env->DeleteLocalRef(settings_cls);
        }
    }

    if (can_draw) {
        originalLogPrint(ANDROID_LOG_INFO, "Mod_Menu", "Start service");
        startLauncherService(env, context);
        return;
    }

    showToast(env, context, u8"请开启显示在其他应用上层权限", true);

    std::string package_name = getPackageName(env, context);
    jclass uri_cls = findClass(env, "android/net/Uri");
    jclass intent_cls = findClass(env, "android/content/Intent");
    if (!uri_cls || !intent_cls) {
        if (uri_cls) env->DeleteLocalRef(uri_cls);
        if (intent_cls) env->DeleteLocalRef(intent_cls);
        return;
    }

    jmethodID parse = getStaticMethod(env, uri_cls, "parse", "(Ljava/lang/String;)Landroid/net/Uri;");
    jmethodID intent_ctor = getMethod(env, intent_cls, "<init>", "(Ljava/lang/String;Landroid/net/Uri;)V");
    jmethodID add_flags = getMethod(env, intent_cls, "addFlags", "(I)Landroid/content/Intent;");
    jclass context_cls = env->GetObjectClass(context);
    jmethodID start_activity = getMethod(env, context_cls, "startActivity", "(Landroid/content/Intent;)V");

    if (parse && intent_ctor && add_flags && start_activity) {
        std::string uri_text = "package:" + package_name;
        jstring action = env->NewStringUTF("android.settings.action.MANAGE_OVERLAY_PERMISSION");
        jstring uri_string = env->NewStringUTF(uri_text.c_str());
        jobject uri = env->CallStaticObjectMethod(uri_cls, parse, uri_string);
        jobject intent = env->NewObject(intent_cls, intent_ctor, action, uri);
        clearPendingException(env, "new overlay Intent");
        if (intent) {
            env->CallObjectMethod(intent, add_flags, 0x10000000);
            clearPendingException(env, "Intent.addFlags");
            env->CallVoidMethod(context, start_activity, intent);
            clearPendingException(env, "Context.startActivity");
            env->DeleteLocalRef(intent);
        }
        if (uri) env->DeleteLocalRef(uri);
        env->DeleteLocalRef(uri_string);
        env->DeleteLocalRef(action);
    }

    if (context_cls) env->DeleteLocalRef(context_cls);
    env->DeleteLocalRef(uri_cls);
    env->DeleteLocalRef(intent_cls);

    exitAfterOverlayPermissionDelay();
}

}
