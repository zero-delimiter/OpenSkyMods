#include "include/jni_helpers.h"
#include "include/memory_tool.h"
#include "include/menu_state.h"

#include <cstdint>
#include <cstdio>
#include <cstring>
#include <thread>

#include <pthread.h>
#include <unistd.h>

namespace android_mod {
namespace {

constexpr const char *kBootloaderModule = "libBootloader.so";
constexpr std::uintptr_t kPatchOffset580 = 0x04438B00; // qword_364580
constexpr std::uintptr_t kPatchOffset5F8 = 0x048C70D4; // qword_3645F8
constexpr std::uintptr_t kPatchOffset600 = 0x044F5310; // qword_364600

bool selfMapsContainLibrary(const char *library_name) {
    if (library_name == nullptr || library_name[0] == '\0') {
        return false;
    }

    FILE *maps = std::fopen("/proc/self/maps", "r");
    if (maps == nullptr) {
        return false;
    }

    char line[512]{};
    bool found = false;
    while (std::fgets(line, sizeof(line), maps) != nullptr) {
        if (std::strstr(line, library_name) != nullptr) {
            found = true;
            break;
        }
    }

    if (!found) {
        std::fclose(maps);
    }
    return found;
}

void runBootloaderPostLoadPatch() {
    constexpr std::uint32_t kEnabled = 1;
    std::uintptr_t base = getModuleAddress(kBootloaderModule);
    if (base == 0) {
        return;
    }
    writeBytes(base + kPatchOffset580, &kEnabled, sizeof(kEnabled));
    writeBytes(base + kPatchOffset5F8, &kEnabled, sizeof(kEnabled));
    writeBytes(base + kPatchOffset600, &kEnabled, sizeof(kEnabled));
}

void *waitForBootloaderAndPatch(void *) {
    originalLogPrint(ANDROID_LOG_INFO, "Mod_Menu", "pthread created");

    do {
        sleep(1);
    } while (!selfMapsContainLibrary(kBootloaderModule));

    markGameLibLoaded();
    originalLogPrint(ANDROID_LOG_INFO, "Mod_Menu", "%s has been loaded", kBootloaderModule);
    std::thread(runBootloaderPostLoadPatch).detach();
    return nullptr;
}

struct BootloaderInitWorkerStarter {
    BootloaderInitWorkerStarter() {
        pthread_t thread{};
        pthread_create(&thread, nullptr, waitForBootloaderAndPatch, nullptr);
    }
};

BootloaderInitWorkerStarter g_bootloader_init_worker_starter;

} // namespace
}
