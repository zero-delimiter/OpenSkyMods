#include "include/player_ride.h"

#include "include/game_command.h"
#include "include/jni_helpers.h"
#include "include/memory_tool.h"

#include <atomic>
#include <chrono>
#include <cstdint>
#include <cstdio>
#include <string>
#include <thread>

namespace android_mod {
namespace {

constexpr const char *kBootloaderModule = "libBootloader.so";
constexpr std::uintptr_t kPlayerChain0 = 0x0473D660;
constexpr std::uintptr_t kPlayerChain1 = 0x8450;
constexpr std::uintptr_t kPlayerBaseBias = 0x9FC0;
constexpr std::uintptr_t kPlayerStride = 0x1260;

constexpr std::uintptr_t kRideScanChain0 = 0x044C2710;
constexpr std::uintptr_t kRideScanChain1 = 0x10;
constexpr std::uintptr_t kRideScanCenter = 0x40;
constexpr std::uintptr_t kRideSentinelOffset = 0x2C4;
constexpr std::uintptr_t kRideTargetOffset = 0x248;
constexpr std::uint64_t kPointerMask = 0x00FFFFFFFFFFFFFFULL;

constexpr std::uintptr_t kPatchOffset0 = 0x01320050;
constexpr std::uintptr_t kPatchOffset1 = 0x01320054;
constexpr std::uintptr_t kPatchOffset2 = 0x01319BF8;
constexpr std::uint32_t kPatchValue0 = 0x14000013;
constexpr std::uint32_t kPatchValue1 = 0xD503201F;
constexpr std::uint32_t kPatchValue2 = 0x52800020;

constexpr const char *kRideTargetInvalidText =
    "\xe2\x9d\x8c\x20\xe9\xaa\x91\xe5\xb0\x8f\xe9\xbb\x91\xe5\x9c\xb0"
    "\xe5\x9d\x80\xe6\x97\xa0\xe6\x95\x88";
constexpr const char *kRideNotRefreshedFormat =
    "\xe2\x9a\xa0\xef\xb8\x8f\x20\xe7\x8e\xa9\xe5\xae\xb6%d\xe6\x9a\x82"
    "\xe6\x9c\xaa\xe5\x88\xb7\xe6\x96\xb0\xef\xbc\x8c\xe8\xaf\xb7\xe7"
    "\xa8\x8d\xe5\x90\x8e\xe5\x86\x8d\xe8\xaf\x95";
constexpr const char *kRideSuccessFormat =
    "\xe2\x9c\x85\x20\xe9\xaa\x91\xe7\x8e\xa9\xe5\xae\xb6%d\xef\xbc\x9a"
    "ID: %d";
constexpr const char *kRideCancelText =
    "\xe2\x9c\x85\x20\xe5\xb7\xb2\xe5\x8f\x96\xe6\xb6\x88\xe9\xaa\x91"
    "\xe5\xb0\x8f\xe9\xbb\x91";
constexpr const char *kRideNoActiveText =
    "\xe5\xbd\x93\xe5\x89\x8d\xe6\xb2\xa1\xe6\x9c\x89\xe9\xaa\x91\xe4"
    "\xb9\x98\xe4\xbb\xbb\xe4\xbd\x95\xe7\x8e\xa9\xe5\xae\xb6";

std::atomic_bool g_ride_active{false};
std::atomic_bool g_ride_worker_running{false};
std::atomic_int g_ride_player_index{-1};
std::uintptr_t g_ride_target = 0;
std::uintptr_t g_player_base = 0;

std::uintptr_t maskedPointer(std::uint64_t value) {
    return static_cast<std::uintptr_t>(value & kPointerMask);
}

bool readU64(std::uintptr_t address, std::uint64_t *out) {
    return out && readTyped(address, TYPE_QWORD, out, sizeof(*out));
}

bool readU32(std::uintptr_t address, std::uint32_t *out) {
    return out && readTyped(address, TYPE_DWORD, out, sizeof(*out));
}

bool readMaskedPointer(std::uintptr_t address, std::uintptr_t *out) {
    std::uint64_t value = 0;
    if (!readU64(address, &value) || out == nullptr) {
        return false;
    }
    *out = maskedPointer(value);
    return true;
}

bool writeDword(std::uintptr_t address, std::uint32_t value) {
    return writeValue(std::to_string(value), address, TYPE_DWORD);
}

bool sendDialogHintTimed(const std::string &text, float seconds) {
    char command[1024];
    std::snprintf(
        command,
        sizeof(command),
        "local e = game:eventBarn():AddEventByMetaName(\"DialogHintTimed\") "
        "if e then e:text(\"%s\") e:lifeTime(%f) e:Start(0) end",
        text.c_str(),
        seconds);
    return sendGameCommand(command);
}

bool applyRideCodePatches(std::uintptr_t bootloader) {
    bool ok = true;
    ok = writeDword(bootloader + kPatchOffset0, kPatchValue0) && ok;
    ok = writeDword(bootloader + kPatchOffset1, kPatchValue1) && ok;
    ok = writeDword(bootloader + kPatchOffset2, kPatchValue2) && ok;
    if (!ok) {
        logWarn("player ride code patch write failed bootloader=0x%llX", static_cast<unsigned long long>(bootloader));
    }
    return ok;
}

bool resolvePlayerBase(std::uintptr_t bootloader, std::uintptr_t *out) {
    std::uintptr_t p0 = 0;
    std::uintptr_t p1 = 0;
    if (!readMaskedPointer(bootloader + kPlayerChain0, &p0)) {
        return false;
    }
    if (!readMaskedPointer(p0 + kPlayerChain1, &p1)) {
        return false;
    }
    if (p1 < kPlayerBaseBias) {
        return false;
    }
    *out = p1 - kPlayerBaseBias;
    return true;
}

bool checkRideCandidate(std::uintptr_t slot_address, std::uintptr_t *out) {
    std::uintptr_t slot = 0;
    std::uintptr_t candidate = 0;
    std::uint32_t sentinel = 0;
    if (!readMaskedPointer(slot_address, &slot) || slot == 0) {
        return false;
    }
    if (!readMaskedPointer(slot, &candidate) || candidate == 0) {
        return false;
    }
    if (!readU32(candidate + kRideSentinelOffset, &sentinel)) {
        return false;
    }
    if (sentinel != 0xFFFFFFFFu) {
        return false;
    }
    *out = candidate + kRideTargetOffset;
    return true;
}

bool resolveRideTarget(std::uintptr_t bootloader, std::uintptr_t *out) {
    std::uintptr_t p0 = 0;
    std::uintptr_t p1 = 0;
    if (!readMaskedPointer(bootloader + kRideScanChain0, &p0)) {
        return false;
    }
    if (!readMaskedPointer(p0 + kRideScanChain1, &p1)) {
        return false;
    }

    const std::uintptr_t center = p1 + kRideScanCenter;
    for (int i = 0; i < 11; ++i) {
        if (checkRideCandidate(center + static_cast<std::uintptr_t>(i) * 8, out)) {
            return true;
        }
        if (i != 0 && checkRideCandidate(center - static_cast<std::uintptr_t>(i) * 8, out)) {
            return true;
        }
    }
    return false;
}

bool refreshRidePointers() {
    std::uintptr_t bootloader = getModuleAddress(kBootloaderModule);
    if (bootloader == 0) {
        logWarn("player ride target module not loaded: %s", kBootloaderModule);
        return false;
    }

    std::uintptr_t player_base = 0;
    std::uintptr_t ride_target = 0;
    bool ok = resolvePlayerBase(bootloader, &player_base) &&
              resolveRideTarget(bootloader, &ride_target);
    if (!ok || player_base == 0 || ride_target == 0) {
        g_player_base = 0;
        g_ride_target = 0;
        return false;
    }

    g_player_base = player_base;
    g_ride_target = ride_target;
    applyRideCodePatches(bootloader);
    return true;
}

std::uint32_t playerIdForIndex(int player_index) {
    if (g_player_base == 0 || player_index < 1 || player_index > 7) {
        return 0;
    }
    std::uint32_t player_id = 0;
    if (!readU32(g_player_base + kPlayerStride * static_cast<std::uintptr_t>(player_index), &player_id)) {
        return 0;
    }
    return player_id;
}

void rideWorker() {
    while (g_ride_active.load()) {
        while (g_ride_active.load() && g_ride_target == 0) {
        }

        const int player_index = g_ride_player_index.load();
        if (g_ride_active.load() && g_ride_target != 0 && g_player_base != 0 && player_index >= 1 && player_index <= 7) {
            writeDword(g_ride_target, playerIdForIndex(player_index));
        }
    }

    if (g_ride_target != 0) {
        writeDword(g_ride_target, 0);
    }
    g_ride_worker_running.store(false);
}

void ensureRideWorker() {
    bool already_running = g_ride_worker_running.exchange(true);
    if (!already_running) {
        std::thread(rideWorker).detach();
    }
}

bool stopRide() {
    const bool was_active = g_ride_active.load() || g_ride_player_index.load() != -1;
    g_ride_active.store(false);
    g_ride_player_index.store(-1);
    if (g_ride_target != 0) {
        writeDword(g_ride_target, 0);
    }
    sendDialogHintTimed(was_active ? kRideCancelText : kRideNoActiveText, 2.0f);
    return true;
}

} // namespace

bool handlePlayerRideFeature1029(int item_id) {
    if (item_id < 1 || item_id > 8) {
        logWarn("player ride invalid item=%d", item_id);
        return true;
    }

    if (item_id == 8) {
        return stopRide();
    }

    if (g_ride_active.load()) {
        g_ride_active.store(false);
        std::this_thread::sleep_for(std::chrono::milliseconds(100));
    }

    if (!refreshRidePointers()) {
        sendDialogHintTimed(kRideTargetInvalidText, 3.0f);
        return true;
    }

    const std::uint32_t player_id = playerIdForIndex(item_id);
    if (player_id == 0) {
        char text[128];
        std::snprintf(text, sizeof(text), kRideNotRefreshedFormat, item_id);
        sendDialogHintTimed(text, 4.0f);
        return true;
    }

    g_ride_player_index.store(item_id);
    g_ride_active.store(true);
    ensureRideWorker();

    char text[128];
    std::snprintf(text, sizeof(text), kRideSuccessFormat, item_id, static_cast<int>(player_id));
    sendDialogHintTimed(text, 3.0f);
    logInfo(
        "player ride enabled item=%d player_id=%u player_base=0x%llX target=0x%llX",
        item_id,
        player_id,
        static_cast<unsigned long long>(g_player_base),
        static_cast<unsigned long long>(g_ride_target));
    return true;
}

}
