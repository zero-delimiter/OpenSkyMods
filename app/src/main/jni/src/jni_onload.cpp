#include "include/jni_helpers.h"

#include <jni.h>

#include <string>

// Feedback-email credential getters — statically-linked JNI exports defined in
// menu_features.cpp (global scope, outside android_mod). The target registers these in
// the Menu RegisterNatives table too (sub_1D9C34, 21 entries), so we mirror that here.
extern "C" JNIEXPORT jstring JNICALL Java_com_android_support_Menu_getNativeSenderEmail(JNIEnv *, jobject);
extern "C" JNIEXPORT jstring JNICALL Java_com_android_support_Menu_getNativeAuthCode(JNIEnv *, jobject);
extern "C" JNIEXPORT jstring JNICALL Java_com_android_support_Menu_getNativeReceiverEmail(JNIEnv *, jobject);

namespace android_mod {

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_GetFeatureList2(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_GetFeatureList3(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_GetFeatureList4(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_GetFeatureList5(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_GetFeatureList6(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_GetFeatureList7(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_GetFeatureList8(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_GetFeatureList9(JNIEnv *, jobject);
extern "C" jstring JNICALL native_Menu_Icon(JNIEnv *, jobject);
extern "C" jstring JNICALL native_Menu_IconWebViewData(JNIEnv *, jobject);
extern "C" jstring JNICALL native_Menu_EYE(JNIEnv *, jobject);
extern "C" jstring JNICALL native_Menu_EYECLOSE(JNIEnv *, jobject);
extern "C" void JNICALL native_Menu_Init(JNIEnv *, jobject, jobject, jobject, jobject);
extern "C" jboolean JNICALL native_Menu_IsGameLibLoaded(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_SetMloinList(JNIEnv *, jobject);
extern "C" jobjectArray JNICALL native_Menu_SettingsList(JNIEnv *, jobject);
extern "C" void JNICALL native_Menu_getLinearLayout(JNIEnv *, jobject, jobject, jobject, jobject, jobject);
extern "C" void JNICALL native_Preferences_Changes(JNIEnv *, jclass, jobject, jint, jstring, jint, jboolean, jstring);

void setAndroidId(const std::string &android_id);

namespace {

void native_Main_CheckOverlayPermission(JNIEnv *env, jclass, jobject context) {
    startOverlayPermissionOrMenu(env, context);
}

int registerNatives(JNIEnv *env, const char *class_name, const JNINativeMethod *methods, int count) {
    jclass cls = env->FindClass(class_name);
    if (!cls) {
        return JNI_ERR;
    }
    return env->RegisterNatives(cls, methods, count);
}

JNINativeMethod kMenuMethods[] = {
    {"Icon", "()Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_Icon)},
    {"EYE", "()Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_EYE)},
    {"EYECLOSE", "()Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_EYECLOSE)},
    {"IconWebViewData", "()Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_IconWebViewData)},
    {"IsGameLibLoaded", "()Z", reinterpret_cast<void *>(native_Menu_IsGameLibLoaded)},
    {"Init", "(Landroid/content/Context;Landroid/widget/TextView;Landroid/widget/TextView;)V", reinterpret_cast<void *>(native_Menu_Init)},
    {"SettingsList", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_SettingsList)},
    {"SetMloinList", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_SetMloinList)},
    {"GetFeatureList", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList)},
    {"GetFeatureList2", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList2)},
    {"GetFeatureList3", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList3)},
    {"GetFeatureList4", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList4)},
    {"GetFeatureList5", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList5)},
    {"GetFeatureList6", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList6)},
    {"GetFeatureList7", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList7)},
    {"GetFeatureList8", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList8)},
    {"GetFeatureList9", "()[Ljava/lang/String;", reinterpret_cast<void *>(native_Menu_GetFeatureList9)},
    {"getLinearLayout", "(Landroid/widget/ScrollView;Landroid/widget/LinearLayout;Landroid/widget/LinearLayout;Landroid/widget/LinearLayout;)V", reinterpret_cast<void *>(native_Menu_getLinearLayout)},
    {"getNativeSenderEmail", "()Ljava/lang/String;", reinterpret_cast<void *>(Java_com_android_support_Menu_getNativeSenderEmail)},
    {"getNativeAuthCode", "()Ljava/lang/String;", reinterpret_cast<void *>(Java_com_android_support_Menu_getNativeAuthCode)},
    {"getNativeReceiverEmail", "()Ljava/lang/String;", reinterpret_cast<void *>(Java_com_android_support_Menu_getNativeReceiverEmail)},
};

JNINativeMethod kPreferenceMethods[] = {
    {"Changes", "(Landroid/content/Context;ILjava/lang/String;IZLjava/lang/String;)V", reinterpret_cast<void *>(native_Preferences_Changes)},
};

JNINativeMethod kMainMethods[] = {
    {"CheckOverlayPermission", "(Landroid/content/Context;)V", reinterpret_cast<void *>(native_Main_CheckOverlayPermission)},
};

} // namespace

}

extern "C" JNIEXPORT void JNICALL Java_com_android_support_Main_Get_1AndroidID(
    JNIEnv *env,
    jclass,
    jstring android_id) {
    if (!android_id) {
        return;
    }
    android_mod::setAndroidId(android_mod::toString(env, android_id));
}

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK || !env) {
        return JNI_ERR;
    }
    using namespace android_mod;
    if (registerNatives(env, "com/android/support/Menu", kMenuMethods, sizeof(kMenuMethods) / sizeof(kMenuMethods[0])) != JNI_OK) {
        return JNI_ERR;
    }
    if (registerNatives(env, "com/android/support/Preferences", kPreferenceMethods, sizeof(kPreferenceMethods) / sizeof(kPreferenceMethods[0])) != JNI_OK) {
        return JNI_ERR;
    }
    if (registerNatives(env, "com/android/support/Main", kMainMethods, sizeof(kMainMethods) / sizeof(kMainMethods[0])) != JNI_OK) {
        return JNI_ERR;
    }
    logInfo("JNI_OnLoad registered Menu, Preferences, Main");
    return JNI_VERSION_1_6;
}
