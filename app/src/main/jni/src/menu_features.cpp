#include "include/jni_helpers.h"
#include "include/menu_state.h"
#include "include/menu_assets.h"
#include "include/menu_features_data.h"

#include <atomic>
#include <mutex>
#include <string>
#include <vector>

namespace android_mod {

bool isDeveloperUnlocked();

namespace {

std::atomic_bool g_icon_requested{false}; // Original byte_37C004.
std::atomic_bool g_game_lib_loaded{false};

std::mutex g_menu_refs_lock;
MenuGlobalRefs g_menu_refs;
jobject g_sound_effect_player = nullptr;   // Original qword_37C038.

const std::vector<std::string> &defaultSettingsList() {
    static const std::vector<std::string> kItems{
        u8"Category_开发选项",
        u8"-901_InputText_请输入开发密钥",
        u8"-902_Button_登录",
        u8"Category_日志",
        u8"RichTextView_技术支持：<font color='green'>可多樂、清风、恓惶、真君、零霙、十夏、孤鹜、懒柚、SanSysn</font><br />Mod制作：<font color='red'>矮人</font><br />TG群</b>：<u>Skyairen</u><br />打开：<font color='green'>Telegram</font> 加入我们吧！",
    };
    return kItems;
}

const std::vector<std::string> &developerSettingsList() {
    static const std::vector<std::string> kItems{
        u8"Category_开发功能",
        u8"925_InputText_自定义代码",
        u8"8001_Button_查询光翼",
        u8"8002_Button_查询货币",
        u8"8006_Button_查询翼统计",
        u8"8003_Button_每日任务",
        u8"8004_Button_献祭光翼",
        u8"8007_Button_送心火",
        u8"8008_Button_收心火",
        u8"8009_Button_收爱心",
        u8"8010_Button_先祖白蜡",
        u8"8011_Button_设置出生点-遇境",
        u8"8012_Button_设置出生点-空巢",
        u8"8014_Button_领取季蜡",
        u8"9010_Toggle_ESP方框",
        u8"9999_Button_任务测试",
        u8"1009_Toggle_循环换盾",
        u8"Category_烟花文本",
        u8"1021_InputText_自定义烟花图案",
        u8"Category_物品生成器",
        u8"934_InputText_物品代码",
        u8"935_Button_生成可拷取放置物",
    };
    return kItems;
}

jobjectArray featureArray(JNIEnv *env, int group) {
    return toJStringArray(env, menuFeatureGroup(group));
}

jobject htmlFromString(JNIEnv *env, const char *html) {
    if (!env || !html) {
        return nullptr;
    }

    jclass html_cls = findClass(env, "android/text/Html");
    if (!html_cls) {
        return nullptr;
    }

    jmethodID from_html = getStaticMethod(
        env,
        html_cls,
        "fromHtml",
        "(Ljava/lang/String;)Landroid/text/Spanned;");
    if (!from_html) {
        env->DeleteLocalRef(html_cls);
        return nullptr;
    }

    jstring source = env->NewStringUTF(html);
    jobject spanned = env->CallStaticObjectMethod(html_cls, from_html, source);
    clearPendingException(env, "Html.fromHtml");
    env->DeleteLocalRef(source);
    env->DeleteLocalRef(html_cls);
    return spanned;
}

void setTextHtml(JNIEnv *env, jobject text_view, const char *html) {
    if (!env || !text_view || !html) {
        return;
    }

    jobject spanned = htmlFromString(env, html);
    if (!spanned) {
        return;
    }

    jclass text_view_cls = env->GetObjectClass(text_view);
    jmethodID set_text = getMethod(env, text_view_cls, "setText", "(Ljava/lang/CharSequence;)V");
    if (set_text) {
        env->CallVoidMethod(text_view, set_text, spanned);
        clearPendingException(env, "TextView.setText");
    }

    if (text_view_cls) {
        env->DeleteLocalRef(text_view_cls);
    }
    env->DeleteLocalRef(spanned);
}

void playSoundEffect(JNIEnv *env, jobject context, const char *effect_name) {
    if (!env || !context || !effect_name) {
        return;
    }

    if (!g_sound_effect_player) {
        jclass player_cls = findClass(env, "com/android/support/SoundEffectPlayer");
        if (!player_cls) {
            originalLogPrint(ANDROID_LOG_ERROR, "Mod_Menu", "SoundEffectPlayer class not found");
            return;
        }
        jmethodID ctor = getMethod(env, player_cls, "<init>", "(Landroid/content/Context;)V");
        if (!ctor) {
            originalLogPrint(ANDROID_LOG_ERROR, "Mod_Menu", "Constructor not found");
            env->DeleteLocalRef(player_cls);
            return;
        }
        jobject local_player = env->NewObject(player_cls, ctor, context);
        clearPendingException(env, "new SoundEffectPlayer");
        if (local_player) {
            g_sound_effect_player = env->NewGlobalRef(local_player);
            env->DeleteLocalRef(local_player);
        } else {
            originalLogPrint(ANDROID_LOG_ERROR, "Mod_Menu", "Failed to create SoundEffectPlayer instance");
        }
        env->DeleteLocalRef(player_cls);
    }

    if (!g_sound_effect_player) {
        return;
    }

    jclass player_cls = env->GetObjectClass(g_sound_effect_player);
    jmethodID play = getMethod(env, player_cls, "playSoundEffect", "(Ljava/lang/String;)V");
    if (play) {
        jstring effect = env->NewStringUTF(effect_name);
        env->CallVoidMethod(g_sound_effect_player, play, effect);
        clearPendingException(env, "SoundEffectPlayer.playSoundEffect");
        env->DeleteLocalRef(effect);
    } else {
        originalLogPrint(ANDROID_LOG_ERROR, "Mod_Menu", "playSoundEffect method not found");
        env->DeleteGlobalRef(g_sound_effect_player);
        g_sound_effect_player = nullptr;
    }
    if (player_cls) {
        env->DeleteLocalRef(player_cls);
    }
}

void showHtmlToast(JNIEnv *env, jobject context, const char *html, bool long_duration) {
    if (!env || !context || !html) {
        return;
    }

    jobject spanned = htmlFromString(env, html);
    if (!spanned) {
        return;
    }

    jclass toast_cls = findClass(env, "android/widget/Toast");
    if (!toast_cls) {
        env->DeleteLocalRef(spanned);
        return;
    }

    jmethodID make_text = getStaticMethod(
        env,
        toast_cls,
        "makeText",
        "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    jmethodID show = getMethod(env, toast_cls, "show", "()V");
    if (make_text && show) {
        jobject toast = env->CallStaticObjectMethod(
            toast_cls,
            make_text,
            context,
            spanned,
            long_duration ? 1 : 0);
        clearPendingException(env, "Toast.makeText");
        if (toast) {
            env->CallVoidMethod(toast, show);
            clearPendingException(env, "Toast.show");
            env->DeleteLocalRef(toast);
        }
    }

    env->DeleteLocalRef(toast_cls);
    env->DeleteLocalRef(spanned);
}

} // namespace

MenuGlobalRefs currentMenuGlobalRefs() {
    std::lock_guard<std::mutex> lock(g_menu_refs_lock);
    return g_menu_refs;
}

void storeMenuGlobalRefs(
    JNIEnv *env,
    jobject scroll_view,
    jobject linear_layout_1,
    jobject linear_layout_2,
    jobject linear_layout_3) {
    if (!env) {
        return;
    }

    std::lock_guard<std::mutex> lock(g_menu_refs_lock);
    if (g_menu_refs.scroll_view) {
        env->DeleteGlobalRef(g_menu_refs.scroll_view);
    }
    if (g_menu_refs.linear_layout_1) {
        env->DeleteGlobalRef(g_menu_refs.linear_layout_1);
    }
    if (g_menu_refs.linear_layout_2) {
        env->DeleteGlobalRef(g_menu_refs.linear_layout_2);
    }
    if (g_menu_refs.linear_layout_3) {
        env->DeleteGlobalRef(g_menu_refs.linear_layout_3);
    }

    g_menu_refs.scroll_view = scroll_view ? env->NewGlobalRef(scroll_view) : nullptr;
    g_menu_refs.linear_layout_1 = linear_layout_1 ? env->NewGlobalRef(linear_layout_1) : nullptr;
    g_menu_refs.linear_layout_2 = linear_layout_2 ? env->NewGlobalRef(linear_layout_2) : nullptr;
    g_menu_refs.linear_layout_3 = linear_layout_3 ? env->NewGlobalRef(linear_layout_3) : nullptr;
}

void markGameLibLoaded() {
    g_game_lib_loaded.store(true);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList(JNIEnv *env, jobject) {
    return featureArray(env, 0);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList2(JNIEnv *env, jobject) {
    return featureArray(env, 1);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList3(JNIEnv *env, jobject) {
    return featureArray(env, 2);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList4(JNIEnv *env, jobject) {
    return featureArray(env, 3);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList5(JNIEnv *env, jobject) {
    return featureArray(env, 4);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList6(JNIEnv *env, jobject) {
    return featureArray(env, 5);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList7(JNIEnv *env, jobject) {
    return featureArray(env, 6);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList8(JNIEnv *env, jobject) {
    return featureArray(env, 7);
}

extern "C" jobjectArray JNICALL native_Menu_GetFeatureList9(JNIEnv *env, jobject) {
    return featureArray(env, 8);
}

extern "C" jstring JNICALL native_Menu_Icon(JNIEnv *env, jobject) {
    g_icon_requested.store(true);
    return toJString(env, originalMenuIconBase64());
}

extern "C" jstring JNICALL native_Menu_IconWebViewData(JNIEnv *, jobject) {
    g_icon_requested.store(true);
    return nullptr;
}

extern "C" jstring JNICALL native_Menu_EYE(JNIEnv *env, jobject) {
    return toJString(env, originalMenuEyeBase64());
}

extern "C" jstring JNICALL native_Menu_EYECLOSE(JNIEnv *env, jobject) {
    return toJString(env, originalMenuEyeCloseBase64());
}

extern "C" void JNICALL native_Menu_Init(JNIEnv *env, jobject, jobject context, jobject title, jobject subtitle) {
    (void)subtitle;
    setTextHtml(env, title, u8"<b> 𝙎𝙆𝙔 <font color='red'>𝙈𝙊𝘿𝙎</font></b>");
    playSoundEffect(env, context, "Inform_Message_GTA.ogg");
    showHtmlToast(env, context, u8"光遇 <font color='#00BFFF'>𝙈𝙊𝘿𝙎</font> 已启动", true);
}

extern "C" jboolean JNICALL native_Menu_IsGameLibLoaded(JNIEnv *, jobject) {
    return g_game_lib_loaded.load() ? JNI_TRUE : JNI_FALSE;
}

extern "C" jobjectArray JNICALL native_Menu_SetMloinList(JNIEnv *env, jobject) {
    return toJStringArray(env, {
        u8"-10_InputText_请输入卡密",
        u8"-11_Button_登录",
        u8"-12_Button_解绑",
        u8"ButtonLink_获取卡密_https://68n.cn/aikVP",
    });
}

extern "C" jobjectArray JNICALL native_Menu_SettingsList(JNIEnv *env, jobject) {
    return toJStringArray(env, isDeveloperUnlocked() ? developerSettingsList() : defaultSettingsList());
}

extern "C" void JNICALL native_Menu_getLinearLayout(
    JNIEnv *env,
    jobject,
    jobject scroll_view,
    jobject linear_layout_1,
    jobject linear_layout_2,
    jobject linear_layout_3) {
    storeMenuGlobalRefs(env, scroll_view, linear_layout_1, linear_layout_2, linear_layout_3);
}

}
