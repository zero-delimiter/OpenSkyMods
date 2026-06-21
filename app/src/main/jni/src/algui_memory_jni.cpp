#include "include/jni_helpers.h"
#include "include/memory_tool.h"

namespace {

using namespace android_mod;

void native_clearResult(JNIEnv *, jclass) {
    clearResult();
}

void native_setRange(JNIEnv *, jclass, jint range) {
    setRange(range);
}

void native_setPackageName(JNIEnv *env, jclass, jstring package_name) {
    setPackageName(toString(env, package_name));
}

void native_RangeMemorySearch(JNIEnv *env, jclass, jstring value, jint type) {
    rangeMemorySearch(toString(env, value), type);
}

void native_MemoryOffset(JNIEnv *env, jclass, jstring value, jint type, jlong offset) {
    memoryOffset(toString(env, value), type, offset);
}

void native_MemoryWrite(JNIEnv *env, jclass, jstring value, jint type, jlong offset) {
    memoryWrite(toString(env, value), type, offset);
}

jint native_getPackageNamePid(JNIEnv *env, jclass, jstring package_name) {
    return getPackageNamePid(toString(env, package_name));
}

jint native_getResultCount(JNIEnv *, jclass) {
    return getResultCount();
}

void native_startFreeze(JNIEnv *, jclass) {
    startFreeze();
}

void native_stopFreeze(JNIEnv *, jclass) {
    stopFreeze();
}

jlong native_getModuleAddress(JNIEnv *env, jclass, jstring name) {
    return static_cast<jlong>(getModuleAddress(toString(env, name)));
}

jlong native_readLong(JNIEnv *, jclass, jlong address) {
    return static_cast<jlong>(readLong(static_cast<std::uintptr_t>(address)));
}

void native_setValue(JNIEnv *env, jclass, jstring value, jlong address, jint type) {
    writeValue(toString(env, value), static_cast<std::uintptr_t>(address), type);
}

} // namespace

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_clearResult(JNIEnv *env, jclass cls) {
    native_clearResult(env, cls);
}

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_setRange(JNIEnv *env, jclass cls, jint range) {
    native_setRange(env, cls, range);
}

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_setPackageName(JNIEnv *env, jclass cls, jstring package_name) {
    native_setPackageName(env, cls, package_name);
}

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_RangeMemorySearch(JNIEnv *env, jclass cls, jstring value, jint type) {
    native_RangeMemorySearch(env, cls, value, type);
}

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_MemoryOffset(JNIEnv *env, jclass cls, jstring value, jint type, jlong offset) {
    native_MemoryOffset(env, cls, value, type, offset);
}

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_MemoryWrite(JNIEnv *env, jclass cls, jstring value, jint type, jlong offset) {
    native_MemoryWrite(env, cls, value, type, offset);
}

extern "C" JNIEXPORT jint JNICALL Java_irene_window_algui_AlguiMemory_getPackageNamePid(JNIEnv *env, jclass cls, jstring package_name) {
    return native_getPackageNamePid(env, cls, package_name);
}

extern "C" JNIEXPORT jint JNICALL Java_irene_window_algui_AlguiMemory_getResultCount(JNIEnv *env, jclass cls) {
    return native_getResultCount(env, cls);
}

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_startFreeze(JNIEnv *env, jclass cls) {
    native_startFreeze(env, cls);
}

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_stopFreeze(JNIEnv *env, jclass cls) {
    native_stopFreeze(env, cls);
}

extern "C" JNIEXPORT jlong JNICALL Java_irene_window_algui_AlguiMemory_getModuleAddress(JNIEnv *env, jclass cls, jstring name) {
    return native_getModuleAddress(env, cls, name);
}

extern "C" JNIEXPORT jlong JNICALL Java_irene_window_algui_AlguiMemory_readLong(JNIEnv *env, jclass cls, jlong address) {
    return native_readLong(env, cls, address);
}

extern "C" JNIEXPORT void JNICALL Java_irene_window_algui_AlguiMemory_setValue(JNIEnv *env, jclass cls, jstring value, jlong address, jint type) {
    native_setValue(env, cls, value, address, type);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_clearResult(JNIEnv *env, jclass cls) {
    native_clearResult(env, cls);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_setRange(JNIEnv *env, jclass cls, jint range) {
    native_setRange(env, cls, range);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_setPackageName(JNIEnv *env, jclass cls, jstring package_name) {
    native_setPackageName(env, cls, package_name);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_RangeMemorySearch(JNIEnv *env, jclass cls, jstring value, jint type) {
    native_RangeMemorySearch(env, cls, value, type);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_MemoryOffset(JNIEnv *env, jclass cls, jstring value, jint type, jlong offset) {
    native_MemoryOffset(env, cls, value, type, offset);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_MemoryWrite(JNIEnv *env, jclass cls, jstring value, jint type, jlong offset) {
    native_MemoryWrite(env, cls, value, type, offset);
}

extern "C" JNIEXPORT jint JNICALL Java_com_irene_algui_AlguiMemory_getPackageNamePid(JNIEnv *env, jclass cls, jstring package_name) {
    return native_getPackageNamePid(env, cls, package_name);
}

extern "C" JNIEXPORT jint JNICALL Java_com_irene_algui_AlguiMemory_getResultCount(JNIEnv *env, jclass cls) {
    return native_getResultCount(env, cls);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_startFreeze(JNIEnv *env, jclass cls) {
    native_startFreeze(env, cls);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_stopFreeze(JNIEnv *env, jclass cls) {
    native_stopFreeze(env, cls);
}

extern "C" JNIEXPORT jlong JNICALL Java_com_irene_algui_AlguiMemory_getModuleAddress(JNIEnv *env, jclass cls, jstring name) {
    return native_getModuleAddress(env, cls, name);
}

extern "C" JNIEXPORT jlong JNICALL Java_com_irene_algui_AlguiMemory_readLong(JNIEnv *env, jclass cls, jlong address) {
    return native_readLong(env, cls, address);
}

extern "C" JNIEXPORT void JNICALL Java_com_irene_algui_AlguiMemory_setValue(JNIEnv *env, jclass cls, jstring value, jlong address, jint type) {
    native_setValue(env, cls, value, address, type);
}
