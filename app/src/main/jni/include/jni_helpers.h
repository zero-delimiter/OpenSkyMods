#pragma once

#include <jni.h>

#include <cstdint>
#include <string>
#include <vector>

#include <android/log.h>

#ifndef ENABLE_ORIGINAL_LOGS
#define ENABLE_ORIGINAL_LOGS 1
#endif

namespace android_mod {

#ifndef ENABLE_DIAGNOSTIC_LOGS
#define ENABLE_DIAGNOSTIC_LOGS 0
#endif

#if ENABLE_DIAGNOSTIC_LOGS
constexpr const char *kLogTag = "AndroidMod";
void logInfo(const char *fmt, ...);
void logWarn(const char *fmt, ...);
void logError(const char *fmt, ...);
#else
#define logInfo(...) ((void)0)
#define logWarn(...) ((void)0)
#define logError(...) ((void)0)
#endif

#if ENABLE_ORIGINAL_LOGS
template <typename... Args>
inline int originalLogPrint(int priority, const char *tag, const char *fmt, Args... args) {
    return __android_log_print(priority, tag, fmt, args...);
}
#else
template <typename... Args>
inline int originalLogPrint(int, const char *, const char *, Args...) {
    return 0;
}
#endif

std::string toString(JNIEnv *env, jstring value);
jstring toJString(JNIEnv *env, const std::string &value);
jobjectArray toJStringArray(JNIEnv *env, const std::vector<std::string> &items);

bool clearPendingException(JNIEnv *env, const char *where);
jclass findClass(JNIEnv *env, const char *name);
jmethodID getMethod(JNIEnv *env, jclass cls, const char *name, const char *sig);
jmethodID getStaticMethod(JNIEnv *env, jclass cls, const char *name, const char *sig);

void showToast(JNIEnv *env, jobject context, const std::string &text, bool long_duration = false);
void callMenuReloadFeatures(JNIEnv *env);
void setMenuLoggedIn(JNIEnv *env, bool logged_in);

std::string getPackageName(JNIEnv *env, jobject context);
void startOverlayPermissionOrMenu(JNIEnv *env, jobject context);

}
