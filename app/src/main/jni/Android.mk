LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Keep the original ndk-build/AIDE-style packaging, but emit the target library name.
LOCAL_MODULE := AndroidMod

LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)

LOCAL_CFLAGS := -w -s -Wno-error=format-security -fvisibility=hidden -fexceptions
LOCAL_CPPFLAGS := -w -s -Wno-error=format-security -fvisibility=hidden -std=c++17 -fexceptions
LOCAL_CPPFLAGS += -Wno-error=c++11-narrowing -Wno-unused-parameter
LOCAL_LDFLAGS += -Wl,--gc-sections,--strip-all
LOCAL_LDFLAGS += -Wl,-z,max-page-size=16384 -Wl,-z,common-page-size=16384
LOCAL_LDLIBS := -llog -lz

LOCAL_SRC_FILES := \
    src/bootloader_init_worker.cpp \
    src/candle_routes.cpp \
    src/feature8013_routes.cpp \
    src/generated_switch_cases.cpp \
    src/game_api.cpp \
    src/game_command.cpp \
    src/game_memory_patch_table.cpp \
    src/high_value_actions.cpp \
    src/jni_helpers.cpp \
    src/jni_onload.cpp \
    src/memory_tool.cpp \
    src/menu_assets.cpp \
    src/menu_features.cpp \
    src/menu_features_data.cpp \
    src/network_auth.cpp \
    src/outfit_tables.cpp \
    src/player_ride.cpp \
    src/preferences_changes.cpp \
    src/text_codec.cpp

include $(BUILD_SHARED_LIBRARY)
