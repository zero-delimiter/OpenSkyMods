#include "include/game_memory_patch_table.h"

#include "include/jni_helpers.h"
#include "include/memory_tool.h"

#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>

#include <cerrno>
#include <cstdio>
#include <cstring>
#include <string>

namespace android_mod {
namespace {

#if ENABLE_DIAGNOSTIC_LOGS
#define PATCH_ERROR(text) text
#else
#define PATCH_ERROR(text) nullptr
#endif

constexpr const char *kPatchModule = "libBootloader.so";
constexpr std::uintptr_t kPatchChain0 = 0x0473E660;
constexpr std::uintptr_t kPatchChain1 = 0x8208;
constexpr std::uintptr_t kPatchChain2 = 0x1AC;
constexpr std::uintptr_t kPointerMask = 0x00FFFFFFFFFFFFFFULL;

constexpr std::uintptr_t kEnabledOffset = 96;
constexpr std::uintptr_t kCapacityOffset = 44;
constexpr std::uintptr_t kLengthOffset = 52;
constexpr std::uintptr_t kTextPointerOffset = 60;
constexpr std::uintptr_t kColorTextOffset = 68;
constexpr std::uintptr_t kTextStorageOffset = 16648;
constexpr std::uintptr_t kScaleOffset0 = 21740;
constexpr std::uintptr_t kScaleOffset1 = 21744;
constexpr std::uintptr_t kScaleOffset2 = 21748;
constexpr std::uintptr_t kScaleOffset3 = 21752;

constexpr unsigned char kBlackColorBytes[] = {
    0x0A, 'B', 'l', 'a', 'c', 'k',
};

struct PatchEntry {
    int index;
    std::uint32_t id;
    const char *payload;
    bool has_id;
};

constexpr PatchEntry kPatchEntries[] = {
    {0, 0x00000000u, "UILogo", false},
    {1, 0xD22C87DEu, "CandleSpace", true},
    {2, 0x831F3D06u, "Nintendo_CandleSpace", true},
    {3, 0x59FC7D97u, "MainStreet", true},
    {4, 0xF111B437u, "MainStreet_ShopSpells", true},
    {5, 0x5D3718ECu, "MainStreet_ShopOutfits", true},
    {6, 0x690574FEu, "MainStreetFlyingIntro", true},
    {7, 0x18E3BC73u, "Event_Cinema", true},
    {8, 0xE8C2DDB6u, "MainStreet_Apartment", true},
    {9, 0x163244CAu, "MainStreet_ShopProps", true},
    {10, 0x561E2EF5u, "MainStreet_Cafe", true},
    {11, 0xC8CD0396u, "MainStreet_Soundbath", true},
    {12, 0x41D95A10u, "MainStreet_Cafe_Wonderland", true},
    {13, 0x5CF5D209u, "MainStreet_ConcertHall", true},
    {14, 0x62507247u, "Dawn", true},
    {15, 0x2CA073A2u, "DawnCave", true},
    {16, 0xE7835080u, "Dawn_TrialsWater", true},
    {17, 0x70847CA7u, "Dawn_TrialsEarth", true},
    {18, 0x7A318007u, "Dawn_TrialsAir", true},
    {19, 0x49FCFCA9u, "Dawn_TrialsFire", true},
    {20, 0x93A94B82u, "Prairie_ButterflyFields", true},
    {21, 0x8C1A4650u, "Prairie_Village", true},
    {22, 0xC169BA0Du, "Prairie_Cave", true},
    {23, 0x1298D15Du, "Prairie_NestAndKeeper", true},
    {24, 0x8EBC83D1u, "DayHubCave", true},
    {25, 0x46FCCD42u, "DayEnd", true},
    {26, 0xEA269379u, "StormEvent_VoidSpace", true},
    {27, 0xB63B168Du, "Prairie_Island", true},
    {28, 0xCC6441C6u, "Prairie_WildLifePark", true},
    {29, 0x09D001F3u, "Rain", true},
    {30, 0xA940A36Eu, "RainForest", true},
    {31, 0xA22A76B4u, "RainShelter", true},
    {32, 0xF661AA51u, "Rain_Cave", true},
    {33, 0x80B98897u, "RainMid", true},
    {34, 0x07AE02A0u, "RainEnd", true},
    {35, 0xC5B96248u, "Rain_BaseCamp", true},
    {36, 0x7C1544FDu, "Skyway", true},
    {37, 0xDE576E50u, "Rain_BlueBirdTheater", true},
    {38, 0x61A20627u, "Sunset", true},
    {39, 0x81E94770u, "Sunset_Citadel", true},
    {40, 0x6DF0D2FCu, "Sunset_FlyRace", true},
    {41, 0x2213C32Au, "SunsetRace", true},
    {42, 0x8CAF7B94u, "SunsetEnd", true},
    {43, 0x0FB5EBD3u, "SunsetColosseum", true},
    {44, 0x1E3FA652u, "SunsetEnd2", true},
    {45, 0x4EE6C975u, "SunsetVillage_MusicShop", true},
    {46, 0x68DAF011u, "SunsetVillage", true},
    {47, 0x26B08055u, "Sunset_YetiPark", true},
    {48, 0x60174DA1u, "Sunset_Theater", true},
    {49, 0x02CD87B8u, "Event_Arr_EyesOfAChild", true},
    {50, 0xEF18E1DFu, "Event_Arr_Runaway", true},
    {51, 0x7CCCA10Au, "Event_Arr_TheSeed", true},
    {52, 0xD267F5AEu, "Event_Arr_Warrior", true},
    {53, 0x63024C80u, "Event_Arr_SoftInside", true},
    {54, 0xA5AF55BAu, "Event_Arr_ExhaleInhale", true},
    {55, 0x30B82314u, "DuskStart", true},
    {56, 0x44655688u, "Dusk", true},
    {57, 0x338632B5u, "DuskGraveyard", true},
    {58, 0x9E01DB8Du, "Dusk_CrabField", true},
    {59, 0x5F319852u, "DuskMid", true},
    {60, 0xF7E4A46Du, "DuskEnd", true},
    {61, 0x0A2C01CDu, "DuskOasis", true},
    {62, 0xF298F995u, "Dusk_Triangle", true},
    {63, 0xD192A2FAu, "Dusk_TriangleEnd", true},
    {64, 0x3950995Fu, "DuskMid_PastMarket", true},
    {65, 0x44B453B8u, "Dusk_HiddenTemple", true},
    {66, 0x5D0FD61Bu, "DuskMid_Past", true},
    {67, 0x161F650Eu, "DuskEnd_Past", true},
    {68, 0x8C9A1101u, "Night", true},
    {69, 0x00266E49u, "NightArchive", true},
    {70, 0x89891349u, "Night2", true},
    {71, 0x87228186u, "NightEnd", true},
    {72, 0x11A1CEC9u, "TGCOffice", true},
    {73, 0x1FA3D86Fu, "Night_IPHallway", true},
    {74, 0xB7BF177Eu, "Event_DaysOfMischief", true},
    {75, 0x7ACC60B8u, "NightDesert", true},
    {76, 0xB5E27B08u, "NightDesert_Beach", true},
    {77, 0xC23FD00Eu, "Night_JarCave", true},
    {78, 0x0CC28627u, "Night_InfiniteDesert", true},
    {79, 0xA85E9640u, "NightDesert_Planets", true},
    {80, 0xACD74BA3u, "Night_Shelter", true},
    {81, 0x65050DA5u, "Night_PaintedWorld", true},
    {82, 0x46AC7A80u, "Night_ValleyHouseSky", true},
    {83, 0xDA969973u, "Night_StoryBook", true},
    {84, 0x6B5FFB30u, "Night_ValleyForest", true},
    {85, 0x0CF316EBu, "Night_ValleyHouse", true},
    {86, 0xB969D8B6u, "StormStart", true},
    {87, 0x65A32136u, "Storm", true},
    {88, 0xCF695453u, "StormEnd", true},
    {89, 0xCCDE869Bu, "OrbitMid", true},
    {90, 0x21DAC95Cu, "OrbitEnd", true},
    {91, 0x862FE09Bu, "CandleSpaceEnd", true},
    {92, 0x0F9ADE75u, "Credits", true},
    {93, 0xDA969973u, "Season24Void1", true},
    {94, 0x58645BF0u, "VoidSharedSpace", true},
    {95, 0x70847CA7u, "WorldEmpty", true},
    {96, 0x0E6EF3FFu, "ECWaxTest", true},
};

const PatchEntry *findEntryById(std::uint32_t id) {
    for (const PatchEntry &entry : kPatchEntries) {
        if (entry.has_id && entry.id == id) {
            return &entry;
        }
    }
    return nullptr;
}

const PatchEntry *findEntryByIndex(int index) {
    for (const PatchEntry &entry : kPatchEntries) {
        if (entry.index == index) {
            return &entry;
        }
    }
    return nullptr;
}

const PatchEntry *findEntryByPayload(const char *payload) {
    if (!payload) {
        return nullptr;
    }
    for (const PatchEntry &entry : kPatchEntries) {
        if (entry.payload && std::strcmp(entry.payload, payload) == 0) {
            return &entry;
        }
    }
    return nullptr;
}

int targetPid() {
    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }
    return pid;
}

std::string procMemPath(int pid) {
    char path[64];
    std::snprintf(path, sizeof(path), "/proc/%d/mem", pid);
    return path;
}

std::uintptr_t moduleBase(int pid, const char *module) {
    for (const MapRange &range : readMaps(pid)) {
        if (range.pathname.find(module) != std::string::npos) {
            return range.start;
        }
    }
    return 0;
}

class ProcMem {
public:
    ProcMem(int pid, int flags) {
        path_ = procMemPath(pid);
        fd_ = open(path_.c_str(), flags);
        if (fd_ < 1) {
            if (fd_ >= 0) {
                close(fd_);
            }
            fd_ = -1;
        }
    }

    ~ProcMem() {
        if (fd_ >= 0) {
            close(fd_);
        }
    }

    bool valid() const {
        return fd_ >= 0;
    }

    const std::string &path() const {
        return path_;
    }

    bool read(std::uintptr_t address, void *out, std::size_t size) {
        if (fd_ < 0 || !out) {
            return false;
        }
        ssize_t n = pread64(fd_, out, size, static_cast<off64_t>(address));
        return n == static_cast<ssize_t>(size);
    }

    bool write(std::uintptr_t address, const void *data, std::size_t size) {
        if (fd_ < 0 || !data) {
            return false;
        }
        bestEffortMprotect(address, PROT_READ | PROT_WRITE | PROT_EXEC);
        ssize_t n = pwrite64(fd_, data, size, static_cast<off64_t>(address));
        bestEffortMprotect(address, PROT_WRITE | PROT_EXEC);
        return n == static_cast<ssize_t>(size);
    }

private:
    void bestEffortMprotect(std::uintptr_t address, int prot) {
        long page_size = sysconf(_SC_PAGESIZE);
        if (page_size <= 0) {
            return;
        }
        std::uintptr_t page = address & ~(static_cast<std::uintptr_t>(page_size) - 1);
        mprotect(reinterpret_cast<void *>(page), static_cast<std::size_t>(page_size), prot);
    }

    int fd_ = -1;
    std::string path_;
};

bool readMaskedPointer(ProcMem *mem, std::uintptr_t address, std::uintptr_t *out) {
    std::uint64_t value = 0;
    if (!mem || !out || !mem->read(address, &value, sizeof(value))) {
        return false;
    }
    *out = static_cast<std::uintptr_t>(value & kPointerMask);
    return true;
}

bool resolvePatchBase(int pid, std::uintptr_t *base, const char **error) {
    if (!base) {
        if (error) *error = PATCH_ERROR("missing output base");
        return false;
    }

    std::uintptr_t bootloader = moduleBase(pid, kPatchModule);
    if (bootloader == 0) {
        if (error) *error = PATCH_ERROR("libBootloader.so base not found");
        return false;
    }

    ProcMem mem(pid, O_RDONLY);
    if (!mem.valid()) {
        if (error) *error = PATCH_ERROR("open proc mem for pointer chain failed");
        return false;
    }

    std::uintptr_t p0 = 0;
    std::uintptr_t p1 = 0;
    if (!readMaskedPointer(&mem, bootloader + kPatchChain0, &p0)) {
        if (error) *error = PATCH_ERROR("read patch chain[0] failed");
        return false;
    }
    if (!readMaskedPointer(&mem, p0 + kPatchChain1, &p1)) {
        if (error) *error = PATCH_ERROR("read patch chain[1] failed");
        return false;
    }

    *base = p1 + kPatchChain2;
    return true;
}

template <typename T>
bool writeScalar(ProcMem *mem, std::uintptr_t address, T value) {
    return mem && mem->write(address, &value, sizeof(value));
}

bool writePatchPayload(ProcMem *mem, std::uintptr_t base, const char *payload) {
    char payload_bytes[48]{};
    std::strncpy(payload_bytes, payload, sizeof(payload_bytes));

    const std::uint32_t one = 1;
    const std::uint64_t capacity = 65;
    const std::uint64_t length = static_cast<std::uint64_t>(std::strlen(payload) + 1);
    const std::uint64_t text_ptr = static_cast<std::uint64_t>(base + kTextStorageOffset);
    const std::uint32_t ten = 10;
    const float far_value = 80000.0f;

    if (!writeScalar(mem, base + kEnabledOffset, one)) return false;
    if (!writeScalar(mem, base + kCapacityOffset, capacity)) return false;
    if (!writeScalar(mem, base + kLengthOffset, length)) return false;
    if (!writeScalar(mem, base + kTextPointerOffset, text_ptr)) return false;
    if (!writeScalar(mem, base + kScaleOffset0, ten)) return false;
    if (!writeScalar(mem, base + kScaleOffset1, ten)) return false;
    if (!writeScalar(mem, base + kScaleOffset2, ten)) return false;
    if (!writeScalar(mem, base + kScaleOffset3, ten)) return false;

    for (std::size_t i = 0; i < sizeof(payload_bytes); ++i) {
        if (!writeScalar(mem, base + kTextStorageOffset + i, static_cast<std::uint8_t>(payload_bytes[i]))) {
            return false;
        }
    }

    for (std::size_t i = 0; i < sizeof(kBlackColorBytes); ++i) {
        if (!writeScalar(mem, base + kColorTextOffset + i, static_cast<std::uint8_t>(kBlackColorBytes[i]))) {
            return false;
        }
    }

    if (!writeScalar(mem, base - 20, far_value)) return false;
    if (!writeScalar(mem, base - 40, far_value)) return false;
    if (!writeScalar(mem, base - 60, far_value)) return false;
    return true;
}

GameMemoryPatchResult applyEntry(const PatchEntry &entry) {
    GameMemoryPatchResult result;
    result.index = entry.index;
    result.id = entry.id;
    result.payload = entry.payload;

    int pid = targetPid();
    const char *resolve_error = nullptr;
    if (!resolvePatchBase(pid, &result.base, &resolve_error)) {
        result.error = resolve_error ? resolve_error : PATCH_ERROR("resolve patch base failed");
        logWarn(
            "game_memory_patch_table replay failed id=0x%08X index=%d payload=%s error=%s",
            entry.id,
            entry.index,
            entry.payload,
            result.error);
        return result;
    }

    ProcMem mem(pid, O_WRONLY);
    if (!mem.valid()) {
        result.error = PATCH_ERROR("open proc mem for write failed");
        logWarn(
            "game_memory_patch_table replay failed id=0x%08X index=%d payload=%s base=0x%llX error=%s",
            entry.id,
            entry.index,
            entry.payload,
            static_cast<unsigned long long>(result.base),
            result.error);
        return result;
    }

    if (!writePatchPayload(&mem, result.base, entry.payload)) {
        result.error = PATCH_ERROR("write patch payload failed");
        logWarn(
            "game_memory_patch_table replay failed id=0x%08X index=%d payload=%s base=0x%llX error=%s",
            entry.id,
            entry.index,
            entry.payload,
            static_cast<unsigned long long>(result.base),
            result.error);
        return result;
    }

    result.ok = true;
    logInfo(
        "game_memory_patch_table replayed id=0x%08X index=%d payload=%s base=0x%llX",
        entry.id,
        entry.index,
        entry.payload,
        static_cast<unsigned long long>(result.base));
    return result;
}

} // namespace

const char *findGamePatchPayloadById(std::uint32_t id, int *index) {
    const PatchEntry *entry = findEntryById(id);
    if (!entry) {
        if (index) {
            *index = -1;
        }
        return nullptr;
    }
    if (index) {
        *index = entry->index;
    }
    return entry->payload;
}

const char *findGamePatchPayloadByIndex(int index, std::uint32_t *id) {
    const PatchEntry *entry = findEntryByIndex(index);
    if (!entry) {
        if (id) {
            *id = 0;
        }
        return nullptr;
    }
    if (id) {
        *id = entry->id;
    }
    return entry->payload;
}

bool findGamePatchIdByPayload(const char *payload, std::uint32_t *id, int *index) {
    const PatchEntry *entry = findEntryByPayload(payload);
    if (!entry || !entry->has_id) {
        if (id) {
            *id = 0;
        }
        if (index) {
            *index = -1;
        }
        return false;
    }
    if (id) {
        *id = entry->id;
    }
    if (index) {
        *index = entry->index;
    }
    return true;
}

GameMemoryPatchResult applyGameMemoryPatchById(std::uint32_t id) {
    const PatchEntry *entry = findEntryById(id);
    if (!entry) {
        GameMemoryPatchResult result;
        result.id = id;
        logWarn("game_memory_patch_table replay failed id=0x%08X error=%s", id, result.error);
        return result;
    }
    return applyEntry(*entry);
}

GameMemoryPatchResult applyGameMemoryPatchByIndex(int index) {
    const PatchEntry *entry = findEntryByIndex(index);
    if (!entry) {
        GameMemoryPatchResult result;
        result.index = index;
        logWarn("game_memory_patch_table replay failed index=%d error=%s", index, result.error);
        return result;
    }
    return applyEntry(*entry);
}

}
