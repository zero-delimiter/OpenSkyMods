#pragma once

#include <jni.h>

namespace android_mod {

struct MenuGlobalRefs {
    jobject scroll_view = nullptr;      // Original qword_37C018.
    jobject linear_layout_1 = nullptr;  // Original qword_37C020.
    jobject linear_layout_2 = nullptr;  // Original qword_37C028.
    jobject linear_layout_3 = nullptr;  // Original qword_37C030.
};

MenuGlobalRefs currentMenuGlobalRefs();
void storeMenuGlobalRefs(
    JNIEnv *env,
    jobject scroll_view,
    jobject linear_layout_1,
    jobject linear_layout_2,
    jobject linear_layout_3);
void markGameLibLoaded();
void setFeature10000LightwingRouteEnabled(bool enabled);
bool feature10000LightwingRouteEnabled();

}
