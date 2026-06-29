#include "include/high_value_actions.h"

#include "include/game_api.h"
#include "include/game_command.h"
#include "include/game_memory_patch_table.h"
#include "include/jni_helpers.h"
#include "include/memory_tool.h"

#include <array>
#include <atomic>
#include <cstddef>
#include <cstdio>
#include <cstdint>
#include <cmath>
#include <mutex>
#include <string>
#include <thread>
#include <vector>

#include <unistd.h>

namespace android_mod {
namespace {

struct HighValueCaseSpec {
    int feature_num;
};

std::atomic_bool g_dragon_esp_enabled{false};
std::mutex g_dragon_esp_lock;

struct Vec3 {
    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;
};

struct DragonEspRecord {
    float x = 0.0f;
    float y = 0.0f;
    float half_width = 0.0f;
    float height = 0.0f;
    float distance = 0.0f;
    bool visible = false;
    int index = 0;
};

struct ProjectionMatrix {
    float clip0_x = 0.0f;
    float clip1_x = 0.0f;
    float w_x = 0.0f;
    float clip0_y = 0.0f;
    float clip1_y = 0.0f;
    float w_y = 0.0f;
    float clip0_z = 0.0f;
    float clip1_z = 0.0f;
    float w_z = 0.0f;
    float clip0_w = 0.0f;
    float clip1_w = 0.0f;
    float w_w = 0.0f;
};

constexpr int kDragonCandidateCount = 5;
constexpr std::size_t kDragonRecordStride = 0x1C;
static_assert(sizeof(DragonEspRecord) == kDragonRecordStride, "Dragon ESP record stride must match dword_3C8B10");
static_assert(offsetof(DragonEspRecord, visible) == 0x14, "Dragon ESP visible flag offset must match dword_3C8B10+0x14");
static_assert(offsetof(DragonEspRecord, index) == 0x18, "Dragon ESP candidate index offset must match dword_3C8B10+0x18");

std::array<DragonEspRecord, kDragonCandidateCount> g_dragon_records{};
std::size_t g_dragon_record_count = 0;

struct DragonEspCache {
    std::array<DragonEspRecord, kDragonCandidateCount> records{};
    std::size_t count = 0;
};

constexpr std::uint32_t kPatchCandleSpaceId = 0xD22C87DEu;
constexpr const char *kDragonEspModule = "libBootloader.so";
constexpr std::uintptr_t kDragonLivePositionChain[] = {
    0x0473E660,
    0x7F18,
    0x1C68,
    0x0,
};
constexpr std::uintptr_t kDragonCandidateChain[] = {
    0x0473E660,
    0x8158,
    0xB0,
};
constexpr std::uint64_t kRemotePointerMask = 0x00FFFFFFFFFFFFFFULL;
constexpr std::uintptr_t kDragonViewPointerOffset = 0x04A1AAE0;
constexpr std::uintptr_t kDragonProjectionBaseOffset = 0x23A0;
constexpr float kDragonNearClip = 0.001f;
constexpr float kDragonScreen0Scale = 1080.0f;
constexpr float kDragonScreen1Scale = 2340.0f;
constexpr float kDragonScreenMin = -100.0f;
constexpr float kDragonScreen0Max = 1180.0f;
constexpr float kDragonScreen1Max = 2440.0f;
constexpr float kDragonMinBoxSize = 15.0f;
constexpr float kDragonMaxBoxSize = 250.0f;
constexpr const char kDragonEspEnabledToast[] =
    "\xF0\x9F\x90\x89 \xE9\xBE\x99" "ESP\xE5\xB7\xB2\xE5\xBC\x80\xE5\x90\xAF";
constexpr const char kDragonEspDisabledToast[] =
    "\xE2\x9D\x8C \xE9\xBE\x99" "ESP\xE5\xB7\xB2\xE5\x85\xB3\xE9\x97\xAD";
constexpr const char kDragonEspFormatToast[] =
    "\xF0\x9F\x90\x89 \xE9\xBE\x99: \xE8\xB7\x9D\xE7\xA6\xBB%.0fm "
    "\xE5\xB1\x8F\xE5\xB9\x95(%.0f,%.0f)";
constexpr const char *kTaskTestModule = "libBootloader.so";
constexpr std::uintptr_t kTaskTestPointerChain[] = {
    0x0474A2C0,
    0x0,
    0x7D0,
    0x1C8,
    0x0,
};
constexpr int kTaskTestQuestCount = 4;
constexpr std::uintptr_t kTaskTestQuestStride = 0x18;
constexpr std::size_t kTaskTestMaxQuestName = 50;
constexpr std::uint64_t kTaskTestPointerMask = 0x00FFFFFFFFFFFFFFULL;
constexpr useconds_t kTaskTestStartDelayUsec = 0x61A80;
constexpr useconds_t kTaskTestStageDelayUsec = 0x7A120;
constexpr useconds_t kTaskTestIncrementDelayUsec = 0xC350;
constexpr useconds_t kTaskTestCollectStageDelayUsec = 0x1E8480;
constexpr int kTaskTestIncrementRounds = 30;
constexpr const char *kTaskTestStartFormat =
    "local e = game:eventBarn():AddEventByMetaName(\"TryStartQuest\") if e then e:questName(\"%s\") e:Start(0) end";
constexpr const char *kTaskTestIncrementFormat =
    "local e = game:eventBarn():AddEventByMetaName(\"TryCompleteQuestOrIncrementStat\") if e then e:questName(\"%s\") e:Start(0) end";
constexpr const char *kTaskTestCollectFormat =
    "local e = game:eventBarn():AddEventByMetaName(\"TryCollectQuest\") if e then e:questName(\"%s\") e:Start(0) end";

constexpr HighValueCaseSpec kHighValueCases[] = {
    {8001},
    {8002},
    {8003},
    {8004},
    {8006},
    {8007},
    {8008},
    {8009},
    {8010},
    {8011},
    {8012},
    {8013},
    {8014},
    {9999},
};

const HighValueCaseSpec *findHighValueSpec(int feature_num) {
    for (const HighValueCaseSpec &spec : kHighValueCases) {
        if (spec.feature_num == feature_num) {
            return &spec;
        }
    }
    return nullptr;
}

bool readRemoteByte(std::uintptr_t address, unsigned char *out) {
    return out != nullptr && readTyped(address, TYPE_BYTE, out, sizeof(*out));
}

bool readRemoteDword(std::uintptr_t address, std::uint32_t *out) {
    return out != nullptr && readTyped(address, TYPE_DWORD, out, sizeof(*out));
}

bool readRemoteQword(std::uintptr_t address, std::uint64_t *out) {
    return out != nullptr && readTyped(address, TYPE_QWORD, out, sizeof(*out));
}

bool readRemoteFloat(std::uintptr_t address, float *out) {
    return out != nullptr && readTyped(address, TYPE_FLOAT, out, sizeof(*out));
}

std::uintptr_t readMaskedPointer(std::uintptr_t address) {
    return static_cast<std::uintptr_t>(readLong(address) & kRemotePointerMask);
}

bool readMaskedPointerAt(std::uintptr_t address, std::uintptr_t *out) {
    std::uint64_t value = 0;
    if (out == nullptr || !readRemoteQword(address, &value)) {
        return false;
    }
    *out = static_cast<std::uintptr_t>(value & kRemotePointerMask);
    return true;
}

std::uintptr_t resolveBootloaderPointerChain(const std::uintptr_t *chain, std::size_t count) {
    if (chain == nullptr || count == 0) {
        return 0;
    }

    std::uintptr_t current = getModuleAddress(kDragonEspModule);
    if (current == 0) {
        logWarn("bootloader pointer chain target module not loaded: %s", kDragonEspModule);
        return 0;
    }

    for (std::size_t i = 0; i + 1 < count; ++i) {
        current = readMaskedPointer(current + chain[i]);
        if (current == 0) {
            logWarn("bootloader pointer chain failed at index=%zu offset=0x%llX",
                    i,
                    static_cast<unsigned long long>(chain[i]));
            return 0;
        }
    }

    return current + chain[count - 1];
}

std::uintptr_t resolveDragonLivePosition() {
    return resolveBootloaderPointerChain(
        kDragonLivePositionChain,
        sizeof(kDragonLivePositionChain) / sizeof(kDragonLivePositionChain[0]));
}

std::uintptr_t resolveDragonViewMatrixBase() {
    std::uintptr_t bootloader = getModuleAddress(kDragonEspModule);
    std::uintptr_t pointer = 0;
    if (bootloader == 0 || !readMaskedPointerAt(bootloader + kDragonViewPointerOffset, &pointer)) {
        return 0;
    }
    return pointer;
}

bool readVec3(std::uintptr_t address, Vec3 *out) {
    if (out == nullptr) {
        return false;
    }
    return readRemoteFloat(address, &out->x) &&
           readRemoteFloat(address + 4, &out->y) &&
           readRemoteFloat(address + 8, &out->z);
}

bool sendDragonDialogHintTimed(const std::string &text, float seconds) {
    char command[512];
    std::snprintf(
        command,
        sizeof(command),
        "local e = game:eventBarn():AddEventByMetaName(\"DialogHintTimed\") "
        "if e then e:text(\"%s\") e:lifeTime(%f) e:Start(0) end",
        text.c_str(),
        static_cast<double>(seconds));
    return sendGameCommand(command);
}

bool readDragonProjectionMatrix(ProjectionMatrix *out) {
    if (out == nullptr) {
        return false;
    }

    std::uintptr_t base = resolveDragonViewMatrixBase();
    if (base == 0) {
        return false;
    }

    float values[16]{};
    for (int i = 0; i < 16; ++i) {
        if (!readRemoteFloat(base + kDragonProjectionBaseOffset + static_cast<std::uintptr_t>(i) * 4, &values[i])) {
            return false;
        }
    }

    out->clip0_x = values[1];
    out->clip1_x = values[0];
    out->w_x = values[3];
    out->clip0_y = values[5];
    out->clip1_y = values[4];
    out->w_y = values[7];
    out->clip0_z = values[9];
    out->clip1_z = values[8];
    out->w_z = values[11];
    out->clip0_w = values[13];
    out->clip1_w = values[12];
    out->w_w = values[15];
    return true;
}

bool buildProjectedRecord(const Vec3 &pos, const Vec3 &player_position, const ProjectionMatrix *matrix, int index, DragonEspRecord *out) {
    if (out == nullptr) {
        return false;
    }

    float dx = pos.x - player_position.x;
    float dy = pos.y - player_position.y;
    float dz = pos.z - player_position.z;
    float distance = std::sqrt(dx * dx + dy * dy + dz * dz);
    if (!(distance >= 0.0f)) {
        return false;
    }

    float screen_x = 0.0f;
    float screen_y = 0.0f;
    float perspective_w = distance;
    bool visible = false;
    if (matrix != nullptr) {
        perspective_w = pos.x * matrix->w_x + pos.y * matrix->w_y + pos.z * matrix->w_z + matrix->w_w;
        if (perspective_w >= kDragonNearClip) {
            float clip0 =
                (pos.x * matrix->clip0_x +
                 pos.y * matrix->clip0_y +
                 pos.z * matrix->clip0_z +
                 matrix->clip0_w) /
                perspective_w;
            float clip1 =
                (pos.x * matrix->clip1_x +
                 pos.y * matrix->clip1_y +
                 pos.z * matrix->clip1_z +
                 matrix->clip1_w) /
                perspective_w;
            screen_x = (clip1 + 1.0f) * 0.5f * kDragonScreen0Scale;
            screen_y = (1.0f - clip0) * 0.5f * kDragonScreen1Scale;
            visible =
                screen_x >= kDragonScreenMin &&
                screen_x <= kDragonScreen0Max &&
                screen_y >= kDragonScreenMin &&
                screen_y <= kDragonScreen1Max;
        }
    }

    float height = kDragonScreen1Scale / std::fmax(perspective_w * 1.5f, 1.0f);
    height = std::fmin(std::fmax(height, kDragonMinBoxSize), kDragonMaxBoxSize);
    float half_width = height * 0.5f;

    out->x = screen_x - half_width * 0.5f;
    out->y = screen_y - height;
    out->half_width = half_width;
    out->height = height;
    out->distance = distance;
    out->visible = visible;
    out->index = index;
    return true;
}

DragonEspCache buildDragonEspRecords(const Vec3 &player_position) {
    DragonEspCache cache;

    ProjectionMatrix matrix;
    ProjectionMatrix *matrix_ptr = readDragonProjectionMatrix(&matrix) ? &matrix : nullptr;
    for (int i = 0; i < kDragonCandidateCount; ++i) {
        std::uintptr_t base = resolveBootloaderPointerChain(
            kDragonCandidateChain,
            sizeof(kDragonCandidateChain) / sizeof(kDragonCandidateChain[0]));
        if (base == 0) {
            continue;
        }
        std::uintptr_t candidate = base + 8 + 0x2D0 * static_cast<std::uintptr_t>(i);
        Vec3 pos;
        if (!readVec3(candidate - 8, &pos)) {
            continue;
        }
        if (pos.x == 0.0f && pos.y == 0.0f && pos.z == 0.0f) {
            continue;
        }

        DragonEspRecord record;
        if (buildProjectedRecord(pos, player_position, matrix_ptr, i, &record) &&
            cache.count < cache.records.size()) {
            cache.records[cache.count++] = record;
        }
    }

    return cache;
}

void refreshDragonEspCacheOnce() {
    std::uintptr_t live_position = resolveDragonLivePosition();
    Vec3 player_position;
    if (live_position == 0 || !readVec3(live_position, &player_position)) {
        logWarn("dragon ESP live position resolve/read failed addr=0x%llX",
                static_cast<unsigned long long>(live_position));
    }

    DragonEspCache cache = buildDragonEspRecords(player_position);

    std::size_t records_size = 0;
    {
        std::lock_guard<std::mutex> guard(g_dragon_esp_lock);
        g_dragon_records = cache.records;
        g_dragon_record_count = cache.count;
        records_size = g_dragon_record_count;
    }

    logInfo("dragon ESP records refreshed live_position=0x%llX records=%zu cache=dword_3C8B10 count=dword_38570C stride=0x1C",
            static_cast<unsigned long long>(live_position),
            records_size);
}

std::uintptr_t resolveTaskTestQuestArray() {
    std::uintptr_t current = getModuleAddress(kTaskTestModule);
    if (current == 0) {
        logWarn("task-test harness target module not loaded: %s", kTaskTestModule);
        return 0;
    }

    for (std::size_t i = 0; i + 1 < sizeof(kTaskTestPointerChain) / sizeof(kTaskTestPointerChain[0]); ++i) {
        current = readMaskedPointer(current + kTaskTestPointerChain[i]);
        if (current == 0) {
            logWarn("task-test pointer chain failed at index=%zu", i);
            return 0;
        }
    }

    return current + kTaskTestPointerChain[(sizeof(kTaskTestPointerChain) / sizeof(kTaskTestPointerChain[0])) - 1];
}

std::string readRemoteCString(std::uintptr_t address) {
    std::string out;
    out.reserve(kTaskTestMaxQuestName);
    for (std::size_t i = 0; i < kTaskTestMaxQuestName; ++i) {
        unsigned char ch = 0;
        if (!readRemoteByte(address + i, &ch) || ch == 0) {
            break;
        }
        out.push_back(static_cast<char>(ch));
    }
    return out;
}

std::string readRemoteLibcxxString(std::uintptr_t address) {
    std::uint32_t first_word = 0;
    if (!readRemoteDword(address, &first_word)) {
        return {};
    }

    const bool long_string = first_word >= 20 && first_word <= 50;
    if (long_string) {
        std::uintptr_t data = readMaskedPointer(address + 0x10);
        return data == 0 ? std::string() : readRemoteCString(data);
    }

    return readRemoteCString(address + 1);
}

std::vector<std::string> readTaskTestQuestNames() {
    std::vector<std::string> names;
    std::uintptr_t quest_array = resolveTaskTestQuestArray();
    if (quest_array == 0) {
        return names;
    }

    for (int i = 0; i < kTaskTestQuestCount; ++i) {
        std::string name = readRemoteLibcxxString(quest_array + static_cast<std::uintptr_t>(i) * kTaskTestQuestStride);
        if (!name.empty()) {
            names.push_back(name);
        }
    }

    logInfo("task-test harness resolved quest array=0x%llX names=%zu",
            static_cast<unsigned long long>(quest_array),
            names.size());
    return names;
}

bool sendFormattedTaskTestCommand(const char *format, const std::string &quest_name) {
    char command[1024];
    std::snprintf(command, sizeof(command), format, quest_name.c_str());
    return sendGameCommand(command);
}

void runTaskTest() {
    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }
    logInfo("task-test harness target pid=%d mem=/proc/%d/mem", pid, pid);

    std::vector<std::string> quest_names = readTaskTestQuestNames();
    if (quest_names.empty()) {
        logWarn("task-test harness has no quest names");
        return;
    }

    int sent = 0;
    int failed = 0;
    for (const std::string &quest_name : quest_names) {
        if (sendFormattedTaskTestCommand(kTaskTestStartFormat, quest_name)) {
            ++sent;
        } else {
            ++failed;
        }
        usleep(kTaskTestStartDelayUsec);
    }

    usleep(kTaskTestStageDelayUsec);
    for (int round = 0; round < kTaskTestIncrementRounds; ++round) {
        for (const std::string &quest_name : quest_names) {
            if (sendFormattedTaskTestCommand(kTaskTestIncrementFormat, quest_name)) {
                ++sent;
            } else {
                ++failed;
            }
            usleep(kTaskTestIncrementDelayUsec);
        }
    }

    usleep(kTaskTestCollectStageDelayUsec);
    for (const std::string &quest_name : quest_names) {
        if (sendFormattedTaskTestCommand(kTaskTestCollectFormat, quest_name)) {
            ++sent;
        } else {
            ++failed;
        }
        usleep(kTaskTestStartDelayUsec);
    }

    logInfo("task-test harness recovered completed quests=%zu sent=%d failed=%d",
            quest_names.size(),
            sent,
            failed);
}

void runHighValueWorker(HighValueCaseSpec spec, int int_value, std::string string_value) {
    logInfo(
        "high-value worker start feature=%d int=%d str='%s'",
        spec.feature_num,
        int_value,
        string_value.c_str());

    if (spec.feature_num == 9999) {
        runTaskTest();
        return;
    }

    if (spec.feature_num == 8001 || spec.feature_num == 8002) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = spec.feature_num == 8001
            ? queryWingLight(session)
            : queryCurrency(session);
        if (!result.ok) {
            logWarn(
                "high-value query failed feature=%d status=%d error=%s body_len=%zu",
                spec.feature_num,
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "high-value query succeeded feature=%d status=%d body_len=%zu",
            spec.feature_num,
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8003) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = runDailyQuest(session);
        if (!result.ok) {
            logWarn(
                "runDailyQuest recovered request failed status=%d error=%s body_len=%zu",
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "runDailyQuest recovered request completed status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8006) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = queryWingStats(session);
        if (!result.ok) {
            logWarn(
                "queryWingStats recovered request failed status=%d error=%s body_len=%zu",
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "queryWingStats recovered request completed status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8004) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = sacrificeWingLight(session);
        if (!result.ok) {
            logWarn(
                "sacrificeWingLight recovered request failed status=%d error=%s body_len=%zu",
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "sacrificeWingLight recovered request completed status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8007) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = sendHeartFireToFriends(session);
        if (!result.ok) {
            logWarn(
                "sendHeartFireToFriends recovered request failed status=%d error=%s body_len=%zu",
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "sendHeartFireToFriends recovered request completed status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8011 || spec.feature_num == 8012) {
        int preferred_home_level = spec.feature_num == 8011 ? 0 : 1;
        logInfo("preferredHomeLevel target value=%d", preferred_home_level);
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = setPreferredHomeLevel(preferred_home_level, session);
        if (!result.ok) {
            logWarn(
                "preferredHomeLevel recovered request failed feature=%d status=%d error=%s body_len=%zu",
                spec.feature_num,
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "preferredHomeLevel recovered request succeeded feature=%d status=%d body_len=%zu",
            spec.feature_num,
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8014) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = claimSeasonCandle(session);
        if (!result.ok) {
            logWarn(
                "claimSeasonCandle recovered request failed status=%d error=%s body_len=%zu",
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "claimSeasonCandle recovered request succeeded status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8008) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = collectHeartFire(session);
        if (!result.ok) {
            logWarn(
                "collectHeartFire recovered request failed status=%d error=%s body_len=%zu",
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "collectHeartFire recovered request completed status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8009) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = collectHearts(session);
        if (!result.ok) {
            logWarn(
                "collectHearts recovered request failed status=%d error=%s body_len=%zu",
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "collectHearts recovered request completed status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8010) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = claimAncestorCandles(session);
        if (!result.ok) {
            logWarn(
                "claimAncestorCandles recovered request failed status=%d error=%s body_len=%zu",
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "claimAncestorCandles recovered request completed status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    if (spec.feature_num == 8013) {
        GameApiSession session = gameApiSessionFromMemory();
        GameApiResult result = runFastMapRoute(session);
        if (!result.ok) {
            logWarn(
                "runFastMapRoute recovered request failed feature=%d status=%d error=%s body_len=%zu",
                spec.feature_num,
                result.http_status,
                result.error.c_str(),
                result.body.size());
            return;
        }
        logInfo(
            "runFastMapRoute recovered request completed status=%d body_len=%zu",
            result.http_status,
            result.body.size());
        return;
    }

    return;
}

void startHighValueWorker(const HighValueCaseSpec &spec, int int_value, const std::string &string_value) {
    std::thread(runHighValueWorker, spec, int_value, string_value).detach();
}

HighValueActionResult setDragonEsp(bool enabled) {
    g_dragon_esp_enabled = enabled;
    if (!enabled) {
        {
            std::lock_guard<std::mutex> guard(g_dragon_esp_lock);
            g_dragon_record_count = 0;
        }
        logInfo("dragon ESP disabled; original branch loc_1C5780 resets dword_38570C");
        sendDragonDialogHintTimed(kDragonEspDisabledToast, 2.0f);
        return {true, false};
    }

    {
        std::lock_guard<std::mutex> guard(g_dragon_esp_lock);
        g_dragon_record_count = 0;
    }

    refreshDragonEspCacheOnce();

    std::size_t record_count = 0;
    bool first_visible = false;
    DragonEspRecord first_record;
    {
        std::lock_guard<std::mutex> guard(g_dragon_esp_lock);
        record_count = g_dragon_record_count;
        if (g_dragon_record_count > 0 && g_dragon_records[0].visible) {
            first_visible = true;
            first_record = g_dragon_records[0];
        }
    }

    if (first_visible) {
        char text[128];
        std::snprintf(
            text,
            sizeof(text),
            kDragonEspFormatToast,
            static_cast<double>(first_record.distance),
            static_cast<double>(first_record.x),
            static_cast<double>(first_record.y));
        logInfo("dragon ESP first visible record: %s", text);
        sendDragonDialogHintTimed(text, 1.0f);
    }

    logInfo(
        "dragon ESP enabled; original feature=9010 target=0x1C09AC live_chain={473E660,7F18,1C68,0} candidate_chain={473E660,8158,B0} matrix_ptr=bootloader+4A1AAE0 record_cache=dword_3C8B10 count=dword_38570C stride=0x1C records=%zu",
        record_count);
    sendDragonDialogHintTimed(kDragonEspEnabledToast, 2.0f);
    return {true, true};
}

} // namespace

HighValueActionResult executeHighValueAction(
    int feature_num,
    int int_value,
    bool bool_value,
    const std::string &string_value) {
    if (feature_num == 8005) {
        logInfo("high-value feature=8005 is a target no-op at 0x1CC564");
        return {true, false};
    }

    if (feature_num == 9010) {
        return setDragonEsp(bool_value);
    }

    const HighValueCaseSpec *spec = findHighValueSpec(feature_num);
    if (spec == nullptr) {
        return {};
    }

    startHighValueWorker(*spec, int_value, string_value);
    return {true, true};
}

bool isDragonEspEnabled() {
    return g_dragon_esp_enabled.load();
}

}
