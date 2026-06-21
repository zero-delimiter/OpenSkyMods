#include "include/game_api.h"

#include "include/feature8013_routes.h"
#include "include/game_command.h"
#include "include/game_memory_patch_table.h"
#include "include/jni_helpers.h"
#include "include/memory_tool.h"
#include "include/text_codec.h"

#include <unistd.h>
#include <zlib.h>

#include <array>
#include <cctype>
#include <cstdio>
#include <cstdint>
#include <cstring>
#include <ctime>
#include <fstream>
#include <map>
#include <sstream>
#include <sys/stat.h>
#include <algorithm>
#include <vector>

#if ENABLE_DIAGNOSTIC_LOGS
#define GAME_API_TEXT(text) text
#else
#define GAME_API_TEXT(text) ""
#endif

namespace android_mod {
namespace {

constexpr const char *kGameApiBaseUrl = "https://live-as-sky-merge.game.163.com";
constexpr const char *kWingBuffsGetPath = "/account/wing_buffs/get";
constexpr const char *kCurrencyGetPath = "/account/get_currency";
constexpr const char *kSeasonQuestsGetPath = "/account/get_season_quests";
constexpr const char *kQuestCreatePath = "/service/quest/api/v1/create";
constexpr const char *kAchievementStatsPath = "/service/status/api/v1/set_achievement_stats";
constexpr const char *kQuestUpdatePath = "/service/quest/api/v1/update_quest";
constexpr const char *kWingBuffsDepositPath = "/account/wing_buffs/deposit";
constexpr const char *kWingBuffsDropPath = "/account/wing_buffs/drop";
constexpr const char *kWingBuffsCollectPath = "/account/wing_buffs/collect";
constexpr const char *kWingBuffsConvertPath = "/account/wing_buffs/convert";
constexpr const char *kMapDefsGetPath = "/account/get_map_defs";
constexpr const char *kFriendConstellationPath = "/service/relationship/api/v1/get_friend_constellation";
constexpr const char *kHeartFireSendPath = "/service/relationship/api/v1/free_gifts/send";
constexpr const char *kHeartFirePendingPath = "/service/relationship/api/v1/free_gifts/get_pending";
constexpr const char *kHeartFireClaimPath = "/service/relationship/api/v1/free_gifts/claim";
constexpr const char *kHeartMessagePendingPath = "/account/get_pending_messages";
constexpr const char *kHeartMessageClaimPath = "/account/claim_message_gift";
constexpr const char *kWorldQuestRewardPath = "/service/quest/api/v1/claim_world_quest_reward";
constexpr const char *kUserDataPath = "/service/status/api/v1/userdata";
constexpr const char *kCollectPickupBatchPath = "/account/collect_pickup_batch";
constexpr const char *kContentTypeHeaderName = "Content-Type";
constexpr const char *kContentTypeHeaderValue = "application/json";
constexpr const char *kBootloaderModule = "libBootloader.so";
constexpr const char *kSkyModsDirectory = "/storage/emulated/0/SkyMods";

constexpr std::uintptr_t kGameApiChain0 = 0x0473D660;
constexpr std::uintptr_t kGameApiSessionChain1 = 0x7EB8;
constexpr std::uintptr_t kGameApiSessionFinal = 0x2005E8;
constexpr std::uintptr_t kGameApiHeaderChain1 = 0x7ED0;
constexpr std::uintptr_t kGameApiHeaderFinal = 0x10;
constexpr std::uintptr_t kGameApiRc4StateOffset = 0x6E168;
constexpr std::uintptr_t kGameApiUserSeedBackOffset = 0x2A;
constexpr std::uintptr_t kGameApiHeaderTextOffset = 0x6F8;
constexpr std::size_t kGameApiHeaderTextMax = 0x800;
constexpr std::uint64_t kPointerMask = 0x00FFFFFFFFFFFFFFULL;

constexpr unsigned int kPatchHomeReloadId = 0x62507247;
constexpr unsigned int kPatchHomeLevel0Id = 0xD22C87DE;
constexpr unsigned int kPatchHomeLevel1Id = 0x59FC7D97;
constexpr unsigned int kPatchCandleSpaceId = 0xD22C87DE;
constexpr std::size_t kFastRouteBatchSize = 32;
constexpr int kFastRoutePauseEveryRoutes = 15;
constexpr std::size_t kFastRouteRouteNameBytes = 48;
constexpr std::uintptr_t kFastRouteRouteNameChain0 = 0x0473D660;
constexpr std::uintptr_t kFastRouteRouteNameChain1 = 0x7D78;
constexpr std::uintptr_t kFastRouteRouteNameFinal = 0x398;
constexpr std::uintptr_t kFastRouteLiveLevelChain0 = 0x0473D660;
constexpr std::uintptr_t kFastRouteLiveLevelChain1 = 0x8520;
constexpr std::uintptr_t kFastRouteLiveLevelFinal = 0x630;

constexpr const char *kCollectPickupRequestFailed =
    "\xe2\x9d\x8c\x20\xe8\xaf\xb7\xe6\xb1\x82\xe5\xa4\xb1\xe8\xb4\xa5";
constexpr const char *kCollectPickupSucceeded =
    "\xe2\x9c\x85\x20\xe9\x87\x87\xe9\x9b\x86\xe6\x88\x90\xe5\x8a\x9f";
constexpr const char *kCollectPickupMissingSeasonPass =
    "\xe2\x9a\xa0\xef\xb8\x8f\x20\xe9\x9c\x80\xe8\xa6\x81\xe5\xad\xa3"
    "\xe5\x8d\xa1\xe6\x89\x8d\xe8\x83\xbd\xe9\xa2\x86\xe5\x8f\x96";
constexpr const char *kCollectPickupAlreadyCollected =
    "\xe2\x9a\xa0\xef\xb8\x8f\x20\xe5\xb7\xb2\xe9\x87\x87\xe9\x9b\x86"
    "\xe8\xbf\x87\xef\xbc\x8c\xe4\xb8\x8d\xe8\x83\xbd\xe9\x87\x8d"
    "\xe5\xa4\x8d\xe9\xa2\x86\xe5\x8f\x96";
constexpr const char *kCollectPickupUnknownFailure =
    "\xe2\x9d\x8c\x20\xe9\x87\x87\xe9\x9b\x86\xe5\xa4\xb1\xe8\xb4\xa5"
    "\xef\xbc\x8c\xe6\x9c\xaa\xe7\x9f\xa5\xe9\x94\x99\xe8\xaf\xaf";

constexpr const char *kWingQueryStart =
    "\xf0\x9f\x94\x8d\x20\xe6\x9f\xa5\xe8\xaf\xa2\xe5\x85\x89\xe7\xbf\xbc\xe4\xb8\xad\x2e\x2e\x2e";
constexpr const char *kCurrencyQueryStart =
    "\xf0\x9f\x94\x8d\x20\xe6\x9f\xa5\xe8\xaf\xa2\xe8\xb4\xa7\xe5\xb8\x81\xe4\xb8\xad\x2e\x2e\x2e";
constexpr const char *kWingReportHeader =
    "\x3d\x3d\x3d\x3d\x3d\x20\xe5\x85\x89\xe7\xbf\xbc\xe6\x9f\xa5\xe8\xaf\xa2\xe7\xbb\x93\xe6\x9e\x9c\x20\x3d\x3d\x3d\x3d\x3d\x0a";
constexpr const char *kCurrencyReportHeader =
    "\x3d\x3d\x3d\x3d\x3d\x20\xe8\xb4\xa7\xe5\xb8\x81\xe6\x9f\xa5\xe8\xaf\xa2\xe7\xbb\x93\xe6\x9e\x9c\x20\x3d\x3d\x3d\x3d\x3d\x0a";
constexpr const char *kQueryRequestFailed =
    "\xe2\x9d\x8c\x20\xe8\xaf\xb7\xe6\xb1\x82\xe5\xa4\xb1\xe8\xb4\xa5\x0a";
constexpr const char *kQueryDecodeFailedPrefix =
    "\xe2\x9d\x8c\x20\xe8\xa7\xa3\xe5\x8e\x8b\x2f\xe8\xa7\xa3\xe5\xaf\x86\xe5\xa4\xb1\xe8\xb4\xa5\x0a\xe5\x8e\x9f\xe5\xa7\x8b\x28";
constexpr const char *kQueryBytesSuffix =
    "\xe5\xad\x97\xe8\x8a\x82\x29\x0a";
constexpr const char *kQueryReportFooter =
    "\x0a\x3d\x3d\x3d\x3d\x3d\x20\xe7\xbb\x93\xe6\x9d\x9f\x20\x3d\x3d\x3d\x3d\x3d\x0a";
constexpr const char *kWingReportFileName =
    "\x2f\xe5\x85\x89\xe7\xbf\xbc\xe6\x9f\xa5\xe8\xaf\xa2\xe7\xbb\x93\xe6\x9e\x9c\x2e\x74\x78\x74";
constexpr const char *kCurrencyReportFileName =
    "\x2f\xe8\xb4\xa7\xe5\xb8\x81\xe6\x9f\xa5\xe8\xaf\xa2\xe7\xbb\x93\xe6\x9e\x9c\x2e\x74\x78\x74";
constexpr const char *kWingQueryDone =
    "\xe2\x9c\x85\x20\xe5\x85\x89\xe7\xbf\xbc\xe7\xbb\x93\xe6\x9e\x9c\xe5\xb7\xb2\xe4\xbf\x9d\xe5\xad\x98";
constexpr const char *kCurrencyQueryDone =
    "\xe2\x9c\x85\x20\xe8\xb4\xa7\xe5\xb8\x81\xe7\xbb\x93\xe6\x9e\x9c\xe5\xb7\xb2\xe4\xbf\x9d\xe5\xad\x98";

constexpr const char *kWingStatsStart =
    "\xe6\x9f\xa5\xe8\xaf\xa2\xe4\xb8\xad\x2e\x2e\x2e";
constexpr const char *kWingStatsRequestFailed =
    "\xe6\x9f\xa5\xe8\xaf\xa2\xe5\xa4\xb1\xe8\xb4\xa5";
constexpr const char *kWingStatsDecodeFailed =
    "\xe8\xa7\xa3\xe5\xaf\x86\xe5\xa4\xb1\xe8\xb4\xa5";
constexpr const char *kWingStatsDoneFormat =
    "\xe5\x9c\xb0\xe5\x9b\xbe\xe5\x85\x89\xe7\xbf\xbc\x3a\x20\x25\x64"
    "\x2c\x20\xe5\x85\x88\xe7\xa5\x96\x3a\x20\x25\x64";

constexpr const char *kCollectHeartFireStart =
    "\xe5\xbc\x80\xe5\xa7\x8b\xe6\x94\xb6\xe5\xbf\x83\xe7\x81\xab";
constexpr const char *kCollectHeartFireListFailed =
    "\xe8\x8e\xb7\xe5\x8f\x96\xe5\xbf\x83\xe7\x81\xab\xe5\x88\x97\xe8\xa1\xa8\xe5\xa4\xb1\xe8\xb4\xa5";
constexpr const char *kCollectHeartFireNoItems =
    "\xe6\xb2\xa1\xe6\x9c\x89\xe5\xbe\x85\xe6\x94\xb6\xe5\xbf\x83\xe7\x81\xab";
constexpr const char *kCollectHeartFireCountFormat =
    "\xe5\x85\xb1\x20%zu\x20\xe4\xb8\xaa\xe5\xbf\x83\xe7\x81\xab";
constexpr const char *kCollectHeartFireProgressFormat =
    "\xe6\x94\xb6\xe5\xbf\x83\xe7\x81\xab\x20%zu/%zu";
constexpr const char *kCollectHeartFireDoneFormat =
    "\xe6\x94\xb6\xe5\xbf\x83\xe7\x81\xab\xe5\xae\x8c\xe6\x88\x90\x3a\x20%d\x20\xe4\xb8\xaa";

constexpr const char *kCollectHeartsStart =
    "\xe5\xbc\x80\xe5\xa7\x8b\xe6\x94\xb6\xe7\x88\xb1\xe5\xbf\x83\x2e\x2e\x2e";
constexpr const char *kCollectHeartsListFailed =
    "\xe8\x8e\xb7\xe5\x8f\x96\xe6\xb6\x88\xe6\x81\xaf\xe5\xa4\xb1\xe8\xb4\xa5";
constexpr const char *kCollectHeartsNoItems =
    "\xe6\xb2\xa1\xe6\x9c\x89\xe5\x8f\xaf\xe6\x94\xb6\xe7\x9a\x84\xe7\x88\xb1\xe5\xbf\x83";
constexpr const char *kCollectHeartsCountFormat =
    "\xe5\x85\xb1\x20%zu\x20\xe4\xb8\xaa\xe7\x88\xb1\xe5\xbf\x83";
constexpr const char *kCollectHeartsProgressFormat =
    "\xe6\x94\xb6\xe7\x88\xb1\xe5\xbf\x83\x20%zu/%zu";
constexpr const char *kCollectHeartsDoneFormat =
    "\xe6\x94\xb6\xe7\x88\xb1\xe5\xbf\x83\xe5\xae\x8c\xe6\x88\x90\x3a\x20%d\x20\xe4\xb8\xaa";

constexpr const char *kSendHeartFireStart =
    "\xe5\xbc\x80\xe5\xa7\x8b\xe9\x80\x81\xe5\xbf\x83\xe7\x81\xab\x2e\x2e\x2e";
constexpr const char *kSendHeartFireListFailed =
    "\xe8\x8e\xb7\xe5\x8f\x96\xe5\xa5\xbd\xe5\x8f\x8b\xe5\x88\x97\xe8\xa1\xa8\xe5\xa4\xb1\xe8\xb4\xa5";
constexpr const char *kSendHeartFireNoFriends =
    "\xe6\xb2\xa1\xe6\x9c\x89\xe5\xa5\xbd\xe5\x8f\x8b";
constexpr const char *kSendHeartFireCountFormat =
    "\xe5\x85\xb1\x20%zu\x20\xe4\xb8\xaa\xe5\xa5\xbd\xe5\x8f\x8b";
constexpr const char *kSendHeartFireProgressFormat =
    "\xe9\x80\x81\xe5\xbf\x83\xe7\x81\xab\x20%zu/%zu";
constexpr const char *kSendHeartFireDoneFormat =
    "\xe9\x80\x81\xe5\xbf\x83\xe7\x81\xab\xe5\xae\x8c\xe6\x88\x90\x3a\x20"
    "\xe6\x88\x90\xe5\x8a\x9f\x20%d\x2c\x20\xe5\xa4\xb1\xe8\xb4\xa5\x20%d";

constexpr const char *kAncestorCandlesStart =
    "\xe5\xbc\x80\xe5\xa7\x8b\xe9\xa2\x86\xe5\x8f\x96\xe5\x85\x88\xe7\xa5\x96\xe7\x99\xbd\xe8\x9c\xa1\x2e\x2e\x2e";
constexpr const char *kAncestorCandlesProgressFormat =
    "\xe9\xa2\x86\xe5\x8f\x96\x20%d/%d";
constexpr const char *kAncestorCandlesDoneFormat =
    "\xe5\x85\x88\xe7\xa5\x96\xe7\x99\xbd\xe8\x9c\xa1\xe5\xae\x8c\xe6\x88\x90"
    "\xef\xbc\x81\xe8\x8e\xb7\xe5\xbe\x97\x20\x25\x64\x20\xe7\x99\xbd\xe8\x9c\xa1";
constexpr const char *kDailyQuestStart =
    "\xf0\x9f\x93\x8b\x20\xe5\xbc\x80\xe5\xa7\x8b\xe6\xaf\x8f\xe6\x97"
    "\xa5\xe4\xbb\xbb\xe5\x8a\xa1\x2e\x2e\x2e";
constexpr const char *kDailyQuestFetchList =
    "\xf0\x9f\x93\x8b\x20\xe8\x8e\xb7\xe5\x8f\x96\xe4\xbb\xbb\xe5\x8a"
    "\xa1\xe5\x88\x97\xe8\xa1\xa8\x2e\x2e\x2e";
constexpr const char *kDailyQuestListFailed =
    "\xe2\x9d\x8c\x20\xe8\x8e\xb7\xe5\x8f\x96\xe4\xbb\xbb\xe5\x8a\xa1"
    "\xe5\x88\x97\xe8\xa1\xa8\xe5\xa4\xb1\xe8\xb4\xa5";
constexpr const char *kDailyQuestDecodeFailed =
    "\xe2\x9d\x8c\x20\xe4\xbb\xbb\xe5\x8a\xa1\xe5\x88\x97\xe8\xa1\xa8"
    "\xe8\xa7\xa3\xe5\xaf\x86\xe5\xa4\xb1\xe8\xb4\xa5";
constexpr const char *kDailyQuestNotFound =
    "\xe2\x9d\x8c\x20\xe6\x9c\xaa\xe6\x89\xbe\xe5\x88\xb0\xe4\xbb\xbb"
    "\xe5\x8a\xa1";
constexpr const char *kDailyQuestCreatedFormat =
    "\xe4\xbb\xbb\xe5\x8a\xa1\x3a\x20%s\x20\x7c\x20\xe7\x9b\xae\xe6"
    "\xa0\x87\x3a\x20%d";
constexpr const char *kDailyQuestProgressFormat =
    "\xe4\xbb\xbb\xe5\x8a\xa1\x20%zu/%zu";
constexpr const char *kDailyQuestDone =
    "\xe2\x9c\x85\x20\xe4\xbb\xbb\xe5\x8a\xa1\xe5\x85\xa8\xe9\x83"
    "\xa8\xe5\xae\x8c\xe6\x88\x90\x21";
constexpr const char *kSacrificeWingStart =
    "\xe6\x9f\xa5\xe8\xaf\xa2\xe5\x85\x89\xe7\xbf\xbc\xe4\xb8\xad\x2e\x2e\x2e";
constexpr const char *kSacrificeWingWeeklyFull =
    "\xe6\x9c\xac\xe5\x91\xa8\xe5\xb7\xb2\xe7\x8c\xae\xe7\xa5\xad\xe6\xbb\xa1"
    "\x36\x33\xe4\xb8\xaa\xef\xbc\x8c\xe8\xb7\xb3\xe8\xbf\x87";
constexpr const char *kSacrificeWingCountFormat =
    "\xe6\x9c\xac\xe5\x91\xa8\xe5\xb7\xb2\xe7\x8c\xae\xe7\xa5\xad\x3a\x20"
    "\x25\x64\x20\x7c\x20\xe5\x8f\xaf\xe7\x8c\xae\xe7\xa5\xad\x3a\x20\x25\x7a\x75";
constexpr const char *kSacrificeWingNoCandidates =
    "\xe6\xb2\xa1\xe6\x9c\x89\xe5\x8f\xaf\xe7\x8c\xae\xe7\xa5\xad\xe7\x9a\x84"
    "\xe5\x85\x89\xe7\xbf\xbc";
constexpr const char *kSacrificeWingProgressFormat =
    "\xe7\x8c\xae\xe7\xa5\xad\x20\x25\x64\x2f\x25\x64";
constexpr const char *kSacrificeWingDropAll =
    "\xe9\x94\x80\xe6\xaf\x81\xe5\x85\xa8\xe9\x83\xa8\xe5\x85\x89\xe7\xbf\xbc\x2e\x2e\x2e";
constexpr const char *kSacrificeWingConvert =
    "\xe9\xa2\x86\xe5\x8f\x96\xe7\xba\xa2\xe8\x9c\xa1\x2e\x2e\x2e";
constexpr const char *kSacrificeWingDoneFormat =
    "\xe7\x8c\xae\xe7\xa5\xad\xe5\xae\x8c\xe6\x88\x90\x3a\x20\xe6\x88\x90\xe5\x8a\x9f"
    "\x20\x25\x64\x2c\x20\xe5\xa4\xb1\xe8\xb4\xa5\x20\x25\x64";
constexpr const char *kFastRouteDoneFormat =
    "\xe2\x9c\x85\xe8\xb7\x91\xe5\x9b\xbe\xe5\xae\x8c\xe6\x88\x90\xef\xbc\x9a"
    "\xe5\x85\xb1\xe8\x80\x97\xe6\x97\xb6%d\xe5\x88\x86%d\xe7\xa7\x92";

struct AncestorCandleGroup {
    std::uint32_t patch_id;
    int task_count;
    const char *tasks[4];
};

constexpr AncestorCandleGroup kAncestorCandleGroups[] = {
    {0x62507247u, 3, {"dawn_nothanks", "dawn_come", "dawn_point", nullptr}},
    {0x93A94B82u, 1, {"prairie_butterfly", nullptr, nullptr, nullptr}},
    {0x8C1A4650u, 4, {"prairie_thumbsup", "prairie_wave", "prairie_laugh", "prairie_yawn"}},
    {0xC169BA0Du, 1, {"prairie_wipe", nullptr, nullptr, nullptr}},
    {0x1298D15Du, 1, {"prairie_bird", nullptr, nullptr, nullptr}},
    {0x09D001F3u, 1, {"rain_cold", nullptr, nullptr, nullptr}},
    {0xA940A36Eu, 3, {"rain_shy", "rain_seek", "rain_pout", nullptr}},
    {0x80B98897u, 4, {"rain_whale", "rain_cry", "rain_sorry", "rain_ohno"}},
    {0x61A20627u, 1, {"sunset_strong", nullptr, nullptr, nullptr}},
    {0x81E94770u, 2, {"sunset_handstand", "sunset_manta", nullptr, nullptr}},
    {0x0FB5EBD3u, 4, {"sunset_backflip", "sunset_cheer", "sunset_proud", "sunset_bow"}},
    {0x44655688u, 1, {"dusk_scared", nullptr, nullptr, nullptr}},
    {0x338632B5u, 3, {"dusk_sneaky", "dusk_brave", "dusk_die", nullptr}},
    {0x9E01DB8Du, 1, {"dusk_salute", nullptr, nullptr, nullptr}},
    {0x5F319852u, 1, {"dusk_lookaround", nullptr, nullptr, nullptr}},
    {0x8C9A1101u, 4, {"night_pray", "night_love", "night_force", "night_float"}},
    {0x89891349u, 1, {"night_ghost", nullptr, nullptr, nullptr}},
};

int ancestorCandleTotalTasks() {
    int total = 0;
    for (const AncestorCandleGroup &group : kAncestorCandleGroups) {
        for (int i = 0; i < group.task_count && i < 4; ++i) {
            if (group.tasks[i] && group.tasks[i][0]) {
                ++total;
            }
        }
    }
    return total;
}

struct HeartFireGift {
    std::string from_id;
    long long gift_id = 0;
};

struct FriendEntry {
    std::string friend_id;
};

struct HeartMessage {
    long long msg_id = 0;
};

struct WingStats {
    int wing_buff = 0;
    int collectible = 0;
};

struct DailyQuestEntry {
    std::string quest_name;
    std::string stat_type;
    std::string quest_id;
    int target_value = 1;
};

struct WingBuffEntry {
    std::string name;
    long long deposit_id = 0;
    long long last_conversion = 0;
    bool collected = false;
};

struct WingDepositRequest {
    std::string name;
    long long deposit_id = 0;
};

std::uintptr_t maskedPointer(std::uint64_t value) {
    return static_cast<std::uintptr_t>(value & kPointerMask);
}

bool readU64At(std::uintptr_t address, std::uint64_t *out) {
    return out && readTyped(address, TYPE_QWORD, out, sizeof(*out));
}

bool readU32At(std::uintptr_t address, std::uint32_t *out) {
    return out && readTyped(address, TYPE_DWORD, out, sizeof(*out));
}

bool readByteAt(std::uintptr_t address, unsigned char *out) {
    return out && readTyped(address, TYPE_BYTE, out, sizeof(*out));
}

bool resolveBootloaderChain(std::uintptr_t offset0, std::uintptr_t offset1, std::uintptr_t final_offset, std::uintptr_t *out) {
    if (!out) {
        return false;
    }
    std::uintptr_t base = getModuleAddress(kBootloaderModule);
    if (base == 0) {
        return false;
    }

    std::uint64_t p0 = 0;
    std::uint64_t p1 = 0;
    if (!readU64At(base + offset0, &p0)) {
        return false;
    }
    if (!readU64At(maskedPointer(p0) + offset1, &p1)) {
        return false;
    }

    *out = maskedPointer(p1) + final_offset;
    return *out != 0;
}

bool writeFastRouteRouteNameToLiveMemory(const char *route_name, std::uintptr_t *address_out, std::size_t *written_out) {
    std::uintptr_t address = 0;
    if (!resolveBootloaderChain(
            kFastRouteRouteNameChain0,
            kFastRouteRouteNameChain1,
            kFastRouteRouteNameFinal,
            &address)) {
        if (written_out) {
            *written_out = 0;
        }
        return false;
    }

    char buffer[kFastRouteRouteNameBytes]{};
    std::strncpy(buffer, route_name ? route_name : "", kFastRouteRouteNameBytes - 1);

    bool ok = true;
    std::size_t written = 0;
    for (std::size_t i = 0; i < kFastRouteRouteNameBytes; ++i) {
        const std::uint8_t value = static_cast<std::uint8_t>(buffer[i]);
        if (writeBytes(address + i, &value, sizeof(value))) {
            ++written;
        } else {
            ok = false;
        }
    }

    if (address_out) {
        *address_out = address;
    }
    if (written_out) {
        *written_out = written;
    }
    return ok;
}

bool readFastRouteLiveLevelId(std::uint32_t *level_id, std::uintptr_t *address_out) {
    std::uintptr_t address = 0;
    if (!resolveBootloaderChain(
            kFastRouteLiveLevelChain0,
            kFastRouteLiveLevelChain1,
            kFastRouteLiveLevelFinal,
            &address)) {
        return false;
    }

    std::uint32_t value = 0;
    if (!readU32At(address, &value)) {
        return false;
    }
    if (level_id) {
        *level_id = value;
    }
    if (address_out) {
        *address_out = address;
    }
    return true;
}

std::string hexByte(unsigned char value) {
    constexpr char kHex[] = "0123456789abcdef";
    std::string out;
    out.push_back(kHex[value >> 4]);
    out.push_back(kHex[value & 0x0f]);
    return out;
}

std::string formatUuidFromBytes(const unsigned char bytes[16]) {
    std::string out;
    out.reserve(36);
    for (int i = 0; i < 16; ++i) {
        out += hexByte(bytes[i]);
        if (i == 3 || i == 5 || i == 7 || i == 9) {
            out.push_back('-');
        }
    }
    return out;
}

std::string formatHexBytes(const unsigned char *bytes, std::size_t count) {
    std::string out;
    out.reserve(count * 2);
    for (std::size_t i = 0; i < count; ++i) {
        out += hexByte(bytes[i]);
    }
    return out;
}

bool isAllZero(const unsigned char *bytes, std::size_t count) {
    for (std::size_t i = 0; i < count; ++i) {
        if (bytes[i] != 0) {
            return false;
        }
    }
    return true;
}

std::string trimAscii(std::string value) {
    std::size_t begin = 0;
    while (begin < value.size() && std::isspace(static_cast<unsigned char>(value[begin]))) {
        ++begin;
    }
    std::size_t end = value.size();
    while (end > begin && std::isspace(static_cast<unsigned char>(value[end - 1]))) {
        --end;
    }
    return value.substr(begin, end - begin);
}

bool asciiEqualsIgnoreCase(const std::string &a, const char *b) {
    if (!b) {
        return false;
    }
    const std::size_t len = std::strlen(b);
    if (a.size() != len) {
        return false;
    }
    for (std::size_t i = 0; i < len; ++i) {
        if (std::tolower(static_cast<unsigned char>(a[i])) !=
            std::tolower(static_cast<unsigned char>(b[i]))) {
            return false;
        }
    }
    return true;
}

std::map<std::string, std::string> parseGameApiHeaders(const std::string &text) {
    std::map<std::string, std::string> headers;
    std::size_t cursor = 0;
    while (cursor < text.size()) {
        std::size_t line_end = text.find('\n', cursor);
        if (line_end == std::string::npos) {
            line_end = text.size();
        }
        std::string line = text.substr(cursor, line_end - cursor);
        if (!line.empty() && line.back() == '\r') {
            line.pop_back();
        }
        std::size_t colon = line.find(':');
        if (colon != std::string::npos) {
            std::string name = trimAscii(line.substr(0, colon));
            std::string value = trimAscii(line.substr(colon + 1));
            if (!name.empty() && name != "Content-Length") {
                headers[name] = value;
            }
        }
        cursor = line_end == text.size() ? text.size() : line_end + 1;
    }
    return headers;
}

bool readRemoteCStringLimited(std::uintptr_t address, std::size_t max_len, std::string *out) {
    if (!out || address == 0 || max_len == 0) {
        return false;
    }
    out->clear();
    out->reserve(std::min<std::size_t>(max_len, 256));
    for (std::size_t i = 0; i < max_len; ++i) {
        unsigned char ch = 0;
        if (!readByteAt(address + i, &ch)) {
            return false;
        }
        if (ch == 0) {
            return true;
        }
        out->push_back(static_cast<char>(ch));
    }
    return true;
}

std::map<std::string, std::string> readGameApiHeadersFromMemory() {
    std::uintptr_t header_slot = 0;
    if (!resolveBootloaderChain(kGameApiChain0, kGameApiHeaderChain1, kGameApiHeaderFinal, &header_slot)) {
        return {};
    }

    std::uint64_t header_base_value = 0;
    if (!readU64At(header_slot, &header_base_value)) {
        return {};
    }
    const std::uintptr_t header_base = maskedPointer(header_base_value);

    std::string raw_headers;
    if (!readRemoteCStringLimited(header_base + kGameApiHeaderTextOffset, kGameApiHeaderTextMax, &raw_headers)) {
        return {};
    }

    std::map<std::string, std::string> headers = parseGameApiHeaders(raw_headers);
    if (!headers.empty()) {
        logInfo(
            "game API live headers extracted header_slot=0x%llX header_base=0x%llX offset=0x%llX count=%zu",
            static_cast<unsigned long long>(header_slot),
            static_cast<unsigned long long>(header_base),
            static_cast<unsigned long long>(kGameApiHeaderTextOffset),
            headers.size());
    }
    return headers;
}

bool tryGameApiSessionFromMemory(GameApiSession *session) {
    if (!session) {
        return false;
    }

    std::uintptr_t session_base = 0;
    if (!resolveBootloaderChain(kGameApiChain0, kGameApiSessionChain1, kGameApiSessionFinal, &session_base)) {
        return false;
    }

    unsigned char seed[32]{};
    for (std::size_t i = 0; i < sizeof(seed); ++i) {
        if (!readByteAt(session_base - kGameApiUserSeedBackOffset + i, &seed[i])) {
            return false;
        }
    }
    if (isAllZero(seed, sizeof(seed))) {
        return false;
    }

    unsigned char rc4_state[256]{};
    for (std::size_t i = 0; i < 256; ++i) {
        std::uint32_t value = 0;
        if (!readU32At(session_base + kGameApiRc4StateOffset + i * 4, &value)) {
            return false;
        }
        rc4_state[i] = static_cast<unsigned char>(value & 0xff);
    }
    if (isAllZero(rc4_state, sizeof(rc4_state))) {
        return false;
    }

    session->user = formatUuidFromBytes(seed);
    session->user_id = session->user;
    session->session = formatHexBytes(seed + 16, 16);
    session->rc4_state_hex = formatHexBytes(rc4_state, sizeof(rc4_state));
    session->headers = readGameApiHeadersFromMemory();

    logInfo(
        "game API session extracted from game memory session_base=0x%llX user_len=%zu session_len=%zu headers=%zu",
        static_cast<unsigned long long>(session_base),
        session->user.size(),
        session->session.size(),
        session->headers.size());
    return true;
}

int hexNibble(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return c - 'a' + 10;
    if (c >= 'A' && c <= 'F') return c - 'A' + 10;
    return -1;
}

bool decodeRc4StateHex(const std::string &hex, std::array<unsigned char, 256> *state) {
    if (!state) {
        return false;
    }
    std::string compact;
    compact.reserve(hex.size());
    for (unsigned char c : hex) {
        if (!std::isspace(c) && c != ':' && c != ',' && c != '-') {
            compact.push_back(static_cast<char>(c));
        }
    }
    if (compact.size() != 512) {
        return false;
    }
    for (std::size_t i = 0; i < 256; ++i) {
        int hi = hexNibble(compact[i * 2]);
        int lo = hexNibble(compact[i * 2 + 1]);
        if (hi < 0 || lo < 0) {
            return false;
        }
        (*state)[i] = static_cast<unsigned char>((hi << 4) | lo);
    }
    return true;
}

bool transformWithRc4State(const std::string &state_hex, const std::string &input, std::string *out) {
    if (!out) {
        return false;
    }
    std::array<unsigned char, 256> s{};
    if (!decodeRc4StateHex(state_hex, &s)) {
        return false;
    }
    out->resize(input.size());
    int i = 0;
    int j = 0;
    for (std::size_t n = 0; n < input.size(); ++n) {
        i = (i + 1) & 0xff;
        j = (j + s[static_cast<std::size_t>(i)]) & 0xff;
        std::swap(s[static_cast<std::size_t>(i)], s[static_cast<std::size_t>(j)]);
        unsigned char k = s[(s[static_cast<std::size_t>(i)] + s[static_cast<std::size_t>(j)]) & 0xff];
        (*out)[n] = static_cast<char>(static_cast<unsigned char>(input[n]) ^ k);
    }
    return true;
}

bool inflateGzipPayload(const std::string &input, std::string *out) {
    if (!out || input.size() < 2 ||
        static_cast<unsigned char>(input[0]) != 0x1f ||
        static_cast<unsigned char>(input[1]) != 0x8b) {
        return false;
    }

    z_stream stream{};
    if (inflateInit2(&stream, MAX_WBITS | 16) != Z_OK) {
        return false;
    }

    stream.next_in = reinterpret_cast<Bytef *>(const_cast<char *>(input.data()));
    stream.avail_in = static_cast<uInt>(input.size());
    std::string result;
    std::array<char, 4096> buffer{};
    int status = Z_OK;
    while (status == Z_OK) {
        stream.next_out = reinterpret_cast<Bytef *>(buffer.data());
        stream.avail_out = static_cast<uInt>(buffer.size());
        status = inflate(&stream, Z_NO_FLUSH);
        if (status != Z_OK && status != Z_STREAM_END) {
            inflateEnd(&stream);
            return false;
        }
        const std::size_t produced = buffer.size() - stream.avail_out;
        if (produced) {
            result.append(buffer.data(), produced);
        }
    }

    inflateEnd(&stream);
    if (status != Z_STREAM_END || result.empty()) {
        return false;
    }
    *out = std::move(result);
    return true;
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

std::string shellSingleQuote(const std::string &value) {
    std::string out;
    out.reserve(value.size() + 2);
    out.push_back('\'');
    for (char c : value) {
        if (c == '\'') {
            out += "'\\''";
        } else {
            out.push_back(c);
        }
    }
    out.push_back('\'');
    return out;
}

std::string buildCurlCommand(const std::string &url, const std::vector<std::pair<std::string, std::string>> &headers, const std::string &body) {
    std::string command = "curl -s -k -X POST ";
    for (const auto &header : headers) {
        command += "-H ";
        command += shellSingleQuote(header.first + ": " + header.second);
        command.push_back(' ');
    }
    command += "-d ";
    command += shellSingleQuote(body);
    command += " --output - -w '%{http_code}' ";
    command += shellSingleQuote(url);
    command += " 2>/dev/null";
    return command;
}

GameApiResult runCurlPost(const std::string &url, const std::vector<std::pair<std::string, std::string>> &headers, const std::string &body) {
    GameApiResult result;
    std::string command = buildCurlCommand(url, headers, body);
    FILE *pipe = popen(command.c_str(), "r");
    if (!pipe) {
        result.error = GAME_API_TEXT("popen curl failed");
        return result;
    }

    std::array<char, 4096> buffer{};
    std::string output;
    for (;;) {
        std::size_t n = fread(buffer.data(), 1, buffer.size(), pipe);
        if (n == 0) {
            break;
        }
        output.append(buffer.data(), n);
    }
    int close_status = pclose(pipe);
    if (close_status == -1) {
        result.error = GAME_API_TEXT("pclose curl failed");
        return result;
    }
    if (output.size() < 3) {
        result.error = GAME_API_TEXT("curl output missing http status");
        return result;
    }

    std::string code_text = output.substr(output.size() - 3);
    result.http_status = std::atoi(code_text.c_str());
    result.ok = result.http_status == 200;
    if (result.ok) {
        result.body = output.substr(0, output.size() - 3);
    } else {
#if ENABLE_DIAGNOSTIC_LOGS
        std::ostringstream oss;
        oss << "http status " << result.http_status;
        result.error = oss.str();
#else
        result.error.clear();
#endif
    }
    return result;
}

std::string buildDefaultUserDataRequestBody(const GameApiSession &session) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) + "\"}";
}

std::string buildClaimSeasonCandleBody(const GameApiSession &session) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"level_id\":3526133726,\"pickup_ids\":[],\"global_pickup_ids\":[4180017154],\"emitters\":[]}";
}

std::string buildCollectPickupBatchBody(
    const GameApiSession &session,
    std::uint32_t level_id,
    const std::uint64_t *pickup_ids,
    std::size_t pickup_count) {
    std::string body = "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"level_id\":" + std::to_string(level_id) +
        ",\"pickup_ids\":[";
    for (std::size_t i = 0; i < pickup_count; ++i) {
        if (i) {
            body.push_back(',');
        }
        body += std::to_string(pickup_ids[i]);
    }
    body += "],\"global_pickup_ids\":[],\"emitters\":[]}";
    return body;
}

std::string buildPendingHeartFireBody(const GameApiSession &session) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) + "\"}";
}

std::string buildFriendConstellationBody(const GameApiSession &session) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"max\":150,\"sort_ver\":1}";
}

std::string buildSendHeartFireBody(const GameApiSession &session, const FriendEntry &friend_entry) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"target\":\"" + jsonEscape(friend_entry.friend_id) +
        "\",\"gift_type\":\"gift_heart_wax\",\"return_buff\":\"false\"}";
}

std::string buildClaimHeartFireBody(const GameApiSession &session, const HeartFireGift &gift) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"from_id\":\"" + jsonEscape(gift.from_id) +
        "\",\"gift_id\":" + std::to_string(gift.gift_id) + "}";
}

std::string buildPendingHeartMessageBody(const GameApiSession &session) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"exclude_free_gifts\":true}";
}

std::string buildClaimHeartMessageBody(const GameApiSession &session, const HeartMessage &message) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"msg_id\":" + std::to_string(message.msg_id) + "}";
}

std::string buildClaimWorldQuestRewardBody(const GameApiSession &session, const char *raw_name) {
    const char *name = raw_name ? raw_name : "";
    if (name[0] == '.') {
        ++name;
    }
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"name\":\"" + jsonEscape(name) +
        "\",\"reward_event_name\":\"\",\"bonus_percent\":0}";
}

std::string buildCreateDailyQuestBody(const GameApiSession &session, const DailyQuestEntry &quest) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"quest_name\":\"" + jsonEscape(quest.quest_name) + "\"}";
}

std::string buildDailyQuestAchievementBody(const GameApiSession &session, const DailyQuestEntry &quest) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"return_buff\":\"true\",\"achievement_stats\":[{\"type\":\"" +
        jsonEscape(quest.stat_type) +
        "\",\"value\":" + std::to_string(quest.target_value) + "}]}";
}

std::string buildDailyQuestStatusBody(const GameApiSession &session, const DailyQuestEntry &quest) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"quest_id\":\"" + jsonEscape(quest.quest_id) +
        "\",\"status\":\"succeeded\"}";
}

std::string buildDailyQuestCollectBody(const GameApiSession &session, const DailyQuestEntry &quest) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"quest_id\":\"" + jsonEscape(quest.quest_id) +
        "\",\"collect\":true}";
}

std::string buildWingBuffCollectBody(const GameApiSession &session, const std::string &name) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"names\":[\"" + jsonEscape(name) +
        "\"],\"client_level_id\":0}";
}

std::string buildWingBuffDepositBody(const GameApiSession &session, const WingDepositRequest &request) {
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"name_deposit_id_pairs\":[[\"" + jsonEscape(request.name) +
        "\"," + std::to_string(request.deposit_id) + "]]}";
}

std::string buildWingBuffDropBody(const GameApiSession &session, const std::vector<std::string> &names) {
    std::string body = "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"names\":[";
    for (std::size_t i = 0; i < names.size(); ++i) {
        if (i) {
            body.push_back(',');
        }
        body += "\"" + jsonEscape(names[i]) + "\"";
    }
    body += "]}";
    return body;
}

std::string buildWrappedBody(const GameApiSession &session, const std::string &plain_body) {
    std::string encrypted_or_plain = plain_body;
    if (!session.rc4_state_hex.empty()) {
        if (!transformWithRc4State(session.rc4_state_hex, plain_body, &encrypted_or_plain)) {
            logWarn("game API RC4 state is present but invalid; posting plain body");
        }
    }
    return "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"body\":\"" + base64Encode(encrypted_or_plain) + "\"}";
}

std::vector<std::pair<std::string, std::string>> buildGameApiHeaders(const GameApiSession &session) {
    std::map<std::string, std::string> header_map;
    for (const auto &header : session.headers) {
        if (header.first.empty() || header.first == "Content-Length") {
            continue;
        }
        header_map[header.first] = header.second;
    }
    header_map[kContentTypeHeaderName] = kContentTypeHeaderValue;

    std::vector<std::pair<std::string, std::string>> headers;
    headers.reserve(header_map.size());
    for (const auto &header : header_map) {
        headers.push_back(header);
    }
    return headers;
}

std::string decodeResponseBody(const GameApiSession &session, const std::string &response) {
    std::string inflated;
    if (inflateGzipPayload(response, &inflated)) {
        return inflated;
    }
    if (session.rc4_state_hex.empty()) {
        return response;
    }
    std::string transformed;
    if (!transformWithRc4State(session.rc4_state_hex, response, &transformed)) {
        return response;
    }
    if (inflateGzipPayload(transformed, &inflated)) {
        return inflated;
    }
    return transformed;
}

bool parseGiftIdAfter(const std::string &text, std::size_t pos, long long *out, std::size_t *end_out) {
    std::size_t marker = text.find("\"gift_id\":", pos);
    if (marker == std::string::npos) {
        return false;
    }
    std::size_t cursor = marker + 10;
    while (cursor < text.size() && std::isspace(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    std::size_t begin = cursor;
    if (cursor < text.size() && text[cursor] == '-') {
        ++cursor;
    }
    while (cursor < text.size() && std::isdigit(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    if (cursor == begin || (cursor == begin + 1 && text[begin] == '-')) {
        return false;
    }
    if (out) {
        *out = std::strtoll(text.substr(begin, cursor - begin).c_str(), nullptr, 10);
    }
    if (end_out) {
        *end_out = cursor;
    }
    return true;
}

std::vector<HeartFireGift> parsePendingHeartFireList(const std::string &text) {
    std::vector<HeartFireGift> gifts;
    std::size_t cursor = 0;
    constexpr const char *kFromMarker = "\"from_id\":\"";
    constexpr std::size_t kFromMarkerLen = 11;
    while ((cursor = text.find(kFromMarker, cursor)) != std::string::npos) {
        std::size_t value_begin = cursor + kFromMarkerLen;
        std::size_t value_end = text.find('"', value_begin);
        if (value_end == std::string::npos) {
            break;
        }
        HeartFireGift gift;
        gift.from_id = text.substr(value_begin, value_end - value_begin);
        std::size_t gift_end = value_end;
        if (parseGiftIdAfter(text, value_end, &gift.gift_id, &gift_end)) {
            gifts.push_back(gift);
            cursor = gift_end;
        } else {
            cursor = value_end + 1;
        }
    }
    return gifts;
}

std::vector<FriendEntry> parseFriendConstellationList(const std::string &text) {
    std::vector<FriendEntry> friends;
    std::size_t cursor = 0;
    constexpr const char *kFriendMarker = "\"friend_id\":\"";
    constexpr std::size_t kFriendMarkerLen = 13;
    while ((cursor = text.find(kFriendMarker, cursor)) != std::string::npos) {
        std::size_t value_begin = cursor + kFriendMarkerLen;
        std::size_t value_end = text.find('"', value_begin);
        if (value_end == std::string::npos) {
            break;
        }
        FriendEntry entry;
        entry.friend_id = text.substr(value_begin, value_end - value_begin);
        if (!entry.friend_id.empty()) {
            friends.push_back(entry);
        }
        cursor = value_end + 1;
    }
    return friends;
}

bool parseMsgIdAfter(const std::string &text, std::size_t pos, long long *out, std::size_t *marker_out, std::size_t *end_out) {
    std::size_t marker = text.find("\"msg_id\":", pos);
    if (marker == std::string::npos) {
        return false;
    }
    std::size_t cursor = marker + 9;
    while (cursor < text.size() && std::isspace(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    std::size_t begin = cursor;
    if (cursor < text.size() && text[cursor] == '-') {
        ++cursor;
    }
    while (cursor < text.size() && std::isdigit(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    if (cursor == begin || (cursor == begin + 1 && text[begin] == '-')) {
        return false;
    }
    if (out) {
        *out = std::strtoll(text.substr(begin, cursor - begin).c_str(), nullptr, 10);
    }
    if (marker_out) {
        *marker_out = marker;
    }
    if (end_out) {
        *end_out = cursor;
    }
    return true;
}

bool extractLongLongFieldInWindow(
    const std::string &text,
    const char *key,
    std::size_t begin,
    std::size_t end,
    long long *out) {
    if (!key || !out || begin >= text.size() || begin >= end) {
        return false;
    }
    if (end > text.size()) {
        end = text.size();
    }
    std::string marker = std::string("\"") + key + "\"";
    std::size_t marker_pos = text.find(marker, begin);
    if (marker_pos == std::string::npos || marker_pos >= end) {
        return false;
    }
    std::size_t colon = text.find(':', marker_pos + marker.size());
    if (colon == std::string::npos || colon >= end) {
        return false;
    }
    std::size_t cursor = colon + 1;
    while (cursor < end && std::isspace(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    std::size_t value_begin = cursor;
    if (cursor < end && text[cursor] == '-') {
        ++cursor;
    }
    while (cursor < end && std::isdigit(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    if (cursor == value_begin || (cursor == value_begin + 1 && text[value_begin] == '-')) {
        return false;
    }
    *out = std::strtoll(text.substr(value_begin, cursor - value_begin).c_str(), nullptr, 10);
    return true;
}

bool extractBoolFieldInWindow(
    const std::string &text,
    const char *key,
    std::size_t begin,
    std::size_t end,
    bool *out) {
    if (!key || !out || begin >= text.size() || begin >= end) {
        return false;
    }
    if (end > text.size()) {
        end = text.size();
    }
    std::string marker = std::string("\"") + key + "\":";
    std::size_t marker_pos = text.find(marker, begin);
    if (marker_pos == std::string::npos || marker_pos >= end) {
        return false;
    }
    std::size_t cursor = marker_pos + marker.size();
    while (cursor < end && std::isspace(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    if (cursor + 4 <= end && text.compare(cursor, 4, "true") == 0) {
        *out = true;
        return true;
    }
    if (cursor + 5 <= end && text.compare(cursor, 5, "false") == 0) {
        *out = false;
        return true;
    }
    return false;
}

std::vector<HeartMessage> parsePendingHeartMessageList(const std::string &text) {
    std::vector<HeartMessage> messages;
    std::size_t cursor = 0;
    while (cursor < text.size()) {
        HeartMessage message;
        std::size_t marker = 0;
        std::size_t end = 0;
        if (!parseMsgIdAfter(text, cursor, &message.msg_id, &marker, &end)) {
            break;
        }

        // Original code checks a 50-byte window ending near the msg_id digits.
        std::size_t window_end = end;
        std::size_t window_begin = window_end > 50 ? window_end - 50 : 0;
        constexpr const char *kHeartCurrencyMarker = "\"currency_type\":\"heart\"";
        std::size_t currency_marker = text.find(kHeartCurrencyMarker, window_begin);
        if (currency_marker != std::string::npos &&
            currency_marker + std::strlen(kHeartCurrencyMarker) <= window_end) {
            messages.push_back(message);
        }
        cursor = end > marker ? end : marker + 1;
    }
    return messages;
}

std::string extractStringFieldInWindow(
    const std::string &text,
    const char *key,
    std::size_t begin,
    std::size_t end) {
    if (!key || begin >= text.size() || begin >= end) {
        return {};
    }
    if (end > text.size()) {
        end = text.size();
    }
    std::string marker = std::string("\"") + key + "\":\"";
    std::size_t marker_pos = text.find(marker, begin);
    if (marker_pos == std::string::npos || marker_pos >= end) {
        return {};
    }
    std::size_t value_begin = marker_pos + marker.size();
    if (value_begin > end) {
        return {};
    }
    std::size_t value_end = text.find('"', value_begin);
    if (value_end == std::string::npos || value_end > end) {
        return {};
    }
    return text.substr(value_begin, value_end - value_begin);
}

bool extractIntFieldInWindow(
    const std::string &text,
    const char *key,
    std::size_t begin,
    std::size_t end,
    int *out) {
    if (!key || !out || begin >= text.size() || begin >= end) {
        return false;
    }
    if (end > text.size()) {
        end = text.size();
    }
    std::string marker = std::string("\"") + key + "\"";
    std::size_t marker_pos = text.find(marker, begin);
    if (marker_pos == std::string::npos || marker_pos >= end) {
        return false;
    }
    std::size_t colon = text.find(':', marker_pos + marker.size());
    if (colon == std::string::npos || colon >= end) {
        return false;
    }
    std::size_t cursor = colon + 1;
    while (cursor < end && std::isspace(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    std::size_t value_begin = cursor;
    if (cursor < end && text[cursor] == '-') {
        ++cursor;
    }
    while (cursor < end && std::isdigit(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    if (cursor == value_begin || (cursor == value_begin + 1 && text[value_begin] == '-')) {
        return false;
    }
    *out = std::atoi(text.substr(value_begin, cursor - value_begin).c_str());
    return true;
}

std::size_t findObjectBegin(const std::string &text, std::size_t pos) {
    std::size_t begin = text.rfind('{', pos);
    return begin == std::string::npos ? pos : begin;
}

std::size_t findObjectEnd(const std::string &text, std::size_t pos) {
    std::size_t end = text.find('}', pos);
    return end == std::string::npos ? text.size() : end + 1;
}

int extractFirstTargetValue(const std::string &text, std::size_t begin) {
    std::size_t target = text.find("\"target_values\"", begin);
    if (target == std::string::npos) {
        return 0;
    }
    std::size_t limit = std::min(text.size(), target + 200);
    int value = 0;
    return extractIntFieldInWindow(text, "value", target, limit, &value) ? value : 0;
}

std::vector<DailyQuestEntry> parseDailyQuestDefinitions(const std::string &text) {
    std::vector<DailyQuestEntry> quests;
    constexpr const char *kDailyQuestMarker = "\"daily_quest_def_id\":\"";
    constexpr std::size_t kDailyQuestMarkerLen = 22;
    std::size_t cursor = 0;
    while ((cursor = text.find(kDailyQuestMarker, cursor)) != std::string::npos) {
        std::size_t value_begin = cursor + kDailyQuestMarkerLen;
        std::size_t value_end = text.find('"', value_begin);
        if (value_end == std::string::npos) {
            break;
        }

        std::size_t object_begin = findObjectBegin(text, cursor);
        std::size_t object_end = findObjectEnd(text, value_end);
        DailyQuestEntry quest;
        quest.quest_name = text.substr(value_begin, value_end - value_begin);
        quest.stat_type = extractStringFieldInWindow(text, "stat_type", object_begin, object_end);
        if (!quest.quest_name.empty() && !quest.stat_type.empty()) {
            quests.push_back(quest);
        }
        cursor = value_end + 1;
    }
    return quests;
}

bool mergeDailyQuestCreateResponse(const std::string &text, DailyQuestEntry *quest) {
    if (!quest) {
        return false;
    }
    std::string quest_id = jsonExtractString(text, "quest_id");
    if (quest_id.empty()) {
        quest_id = extractStringFieldInWindow(text, "quest_id", 0, text.size());
    }
    if (!quest_id.empty()) {
        quest->quest_id = quest_id;
    }
    int target_value = extractFirstTargetValue(text, 0);
    if (target_value != 0) {
        quest->target_value = target_value;
    }
    return !quest->quest_id.empty();
}

std::vector<WingBuffEntry> parseWingBuffEntries(const std::string &text) {
    std::vector<WingBuffEntry> entries;
    constexpr const char *kNameMarker = "\"name\":\"";
    constexpr std::size_t kNameMarkerLen = 8;
    std::size_t cursor = 0;
    while ((cursor = text.find(kNameMarker, cursor)) != std::string::npos) {
        std::size_t value_begin = cursor + kNameMarkerLen;
        std::size_t value_end = text.find('"', value_begin);
        if (value_end == std::string::npos) {
            break;
        }

        std::size_t object_begin = findObjectBegin(text, cursor);
        std::size_t object_end = findObjectEnd(text, value_end);
        WingBuffEntry entry;
        entry.name = text.substr(value_begin, value_end - value_begin);
        bool has_deposit = extractLongLongFieldInWindow(text, "deposit_id", object_begin, object_end, &entry.deposit_id);
        extractLongLongFieldInWindow(text, "last_conversion", object_begin, object_end, &entry.last_conversion);
        extractBoolFieldInWindow(text, "collected", object_begin, object_end, &entry.collected);
        if (!entry.name.empty() && has_deposit) {
            entries.push_back(entry);
        }
        cursor = value_end + 1;
    }
    return entries;
}

long long currentWeekStartSeconds() {
    std::time_t now = std::time(nullptr);
    std::tm local{};
    std::tm *tm_now = std::localtime(&now);
    if (tm_now) {
        local = *tm_now;
    }
    return static_cast<long long>(now) -
        (3600LL * local.tm_hour + 60LL * local.tm_min + local.tm_sec) -
        86400LL * local.tm_wday;
}

bool wasConvertedThisWeek(long long last_conversion, long long week_start) {
    return last_conversion >= week_start && last_conversion < week_start + 604800LL;
}

void appendUniqueName(std::vector<std::string> *names, const std::string &name) {
    if (!names || name.empty()) {
        return;
    }
    if (std::find(names->begin(), names->end(), name) == names->end()) {
        names->push_back(name);
    }
}

std::vector<std::string> parseWingBuffNames(const std::string &text) {
    std::vector<std::string> names;
    constexpr const char *kNameMarker = "\"name\":\"";
    constexpr std::size_t kNameMarkerLen = 8;
    std::size_t cursor = 0;
    while ((cursor = text.find(kNameMarker, cursor)) != std::string::npos) {
        std::size_t value_begin = cursor + kNameMarkerLen;
        std::size_t value_end = text.find('"', value_begin);
        if (value_end == std::string::npos) {
            break;
        }
        appendUniqueName(&names, text.substr(value_begin, value_end - value_begin));
        cursor = value_end + 1;
    }
    return names;
}

bool parseWingStats(const std::string &text, WingStats *stats) {
    if (!stats || text.empty()) {
        return false;
    }

    constexpr const char *kTypeMarker = "\"type\":\"";
    constexpr std::size_t kTypeMarkerLen = 8;
    bool saw_type = false;
    std::size_t cursor = 0;
    while ((cursor = text.find(kTypeMarker, cursor)) != std::string::npos) {
        std::size_t type_begin = cursor + kTypeMarkerLen;
        std::size_t type_end = text.find('"', type_begin);
        if (type_end == std::string::npos) {
            break;
        }

        std::string type = text.substr(type_begin, type_end - type_begin);
        if (type == "wing_buff") {
            ++stats->wing_buff;
            saw_type = true;
        } else if (type == "collectible") {
            std::size_t object_begin = text.rfind('{', cursor);
            if (object_begin == std::string::npos) {
                object_begin = cursor > 160 ? cursor - 160 : 0;
            }
            std::size_t object_end = text.find('}', type_end);
            if (object_end == std::string::npos) {
                object_end = std::min(text.size(), type_end + 240);
            }

            std::string name = extractStringFieldInWindow(text, "name", object_begin, object_end + 1);
            if (name != "questap20") {
                ++stats->collectible;
            }
            saw_type = true;
        }
        cursor = type_end + 1;
    }
    return saw_type;
}

bool parseCandlesValue(const std::string &text, int *out) {
    if (!out) {
        return false;
    }
    constexpr const char *kCandlesMarker = "\"candles\":";
    std::size_t marker = text.find(kCandlesMarker);
    if (marker == std::string::npos) {
        return false;
    }

    std::size_t cursor = marker + std::strlen(kCandlesMarker);
    while (cursor < text.size() && std::isspace(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    std::size_t begin = cursor;
    while (cursor < text.size() && std::isdigit(static_cast<unsigned char>(text[cursor]))) {
        ++cursor;
    }
    if (cursor == begin) {
        return false;
    }
    *out = std::atoi(text.substr(begin, cursor - begin).c_str());
    return true;
}

void sendFormattedDialog(const char *format, std::size_t a, std::size_t b, float seconds) {
    char text[128];
    if (b == static_cast<std::size_t>(-1)) {
        std::snprintf(text, sizeof(text), format, a);
    } else {
        std::snprintf(text, sizeof(text), format, a, b);
    }
    sendDialogHintTimed(text, seconds);
}

bool responseContainsDecodedOrPlain(
    const GameApiSession &session,
    const std::string &response,
    const char *needle,
    std::string *decoded_out = nullptr) {
    if (!needle || response.empty()) {
        if (decoded_out) {
            decoded_out->clear();
        }
        return false;
    }
    std::string decoded = decodeResponseBody(session, response);
    if (decoded_out) {
        *decoded_out = decoded;
    }
    if (decoded.find(needle) != std::string::npos) {
        return true;
    }
    return decoded != response && response.find(needle) != std::string::npos;
}

bool parseUserDataResponse(
    const GameApiSession &session,
    const std::string &response,
    std::string *version_raw,
    std::string *decoded_data) {
    std::string decoded_response = decodeResponseBody(session, response);
    std::string data_b64 = jsonExtractString(decoded_response, "data");
    if (data_b64.empty()) {
        return false;
    }
    if (!base64Decode(data_b64, decoded_data)) {
        return false;
    }
    if (version_raw) {
        *version_raw = jsonExtractRawValue(decoded_response, "version");
    }
    return true;
}

bool hasRequiredSession(const GameApiSession &session, std::string *missing);

GameApiResult gameApiPostPath(const GameApiSession &session, const char *path, const std::string &plain_body) {
    GameApiResult result;
    std::string missing;
    if (!hasRequiredSession(session, &missing)) {
        result.error = missing;
        return result;
    }

    const std::string url = std::string(kGameApiBaseUrl) + path;
    std::vector<std::pair<std::string, std::string>> headers = buildGameApiHeaders(session);
    std::string wrapped_body = buildWrappedBody(session, plain_body);
    return runCurlPost(url, headers, wrapped_body);
}

std::string buildQueryReport(const GameApiSession &session, const GameApiResult &result, const char *header) {
    std::string report = header ? header : "";
    if (result.body.empty()) {
        report += kQueryRequestFailed;
        return report;
    }

    std::string decoded = decodeResponseBody(session, result.body);
    if (!decoded.empty() && decoded != result.body) {
        report += decoded;
        return report;
    }

    if (session.rc4_state_hex.empty() && result.ok) {
        report += result.body;
        return report;
    }

    report += kQueryDecodeFailedPrefix;
    report += std::to_string(result.body.size());
    report += kQueryBytesSuffix;
    report.append(result.body.data(), std::min<std::size_t>(result.body.size(), 200));
    return report;
}

bool writeSkyModsReport(const char *file_name, const std::string &content, std::string *path_out) {
    mkdir(kSkyModsDirectory, 0777);

    std::string path = std::string(kSkyModsDirectory) + (file_name ? file_name : "");
    std::ofstream stream(path, std::ios::binary | std::ios::trunc);
    if (!stream) {
        if (path_out) {
            *path_out = path;
        }
        return false;
    }
    stream.write(content.data(), static_cast<std::streamsize>(content.size()));
    stream.close();
    if (path_out) {
        *path_out = path;
    }
    return stream.good();
}

GameApiResult queryReport(
    const GameApiSession &session,
    int selector,
    const char *start_prompt,
    const char *header,
    const char *file_name,
    const char *done_prompt) {
    sendDialogHintTimed(start_prompt ? start_prompt : "", 2.0f);

    GameApiResult result;
    std::string plain_body = buildDefaultUserDataRequestBody(session);
    if (selector == 1) {
        result = gameApiPostPath(session, kWingBuffsGetPath, plain_body);
    } else {
        result = gameApiPostPath(session, kCurrencyGetPath, plain_body);
    }

    std::string report = buildQueryReport(session, result, header);
    report += kQueryReportFooter;

    std::string saved_path;
    if (!writeSkyModsReport(file_name, report, &saved_path)) {
        logWarn("query report feature selector=%d failed to write %s", selector, saved_path.c_str());
    }
    sendDialogHintTimed(done_prompt ? done_prompt : "", 3.0f);
    logInfo(
        "query report selector=%d status=%d ok=%d body_len=%zu saved=%s",
        selector,
        result.http_status,
        result.ok ? 1 : 0,
        result.body.size(),
        saved_path.c_str());
    return result;
}

std::string buildUserDataUpdateBody(
    const GameApiSession &session,
    const std::string &version_raw,
    const std::string &encoded_data) {
    std::string body = "{\"user\":\"" + jsonEscape(session.user) +
        "\",\"user_id\":\"" + jsonEscape(session.user_id.empty() ? session.user : session.user_id) +
        "\",\"session\":\"" + jsonEscape(session.session) +
        "\",\"time\":" + std::to_string(static_cast<long long>(std::time(nullptr)));
    if (!version_raw.empty()) {
        body += ",\"version\":" + version_raw;
    }
    body += ",\"data\":\"" + jsonEscape(encoded_data) + "\"}";
    return body;
}

bool hasRequiredSession(const GameApiSession &session, std::string *missing) {
    if (session.user.empty() || session.session.empty()) {
#if ENABLE_DIAGNOSTIC_LOGS
        std::vector<const char *> names;
        if (session.user.empty()) names.push_back("game user");
        if (session.session.empty()) names.push_back("game session");
        std::ostringstream oss;
        oss << "missing ";
        for (std::size_t i = 0; i < names.size(); ++i) {
            if (i) oss << ", ";
            oss << names[i];
        }
        if (missing) {
            *missing = oss.str();
        }
#else
        if (missing) {
            missing->clear();
        }
#endif
        return false;
    }
    return true;
}

} // namespace

GameApiSession gameApiSessionFromMemory() {
    GameApiSession session;
    tryGameApiSessionFromMemory(&session);
    if (session.user_id.empty()) {
        session.user_id = session.user;
    }
    return session;
}

GameApiResult gameApiPostSelector1(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kWingBuffsGetPath, plain_body);
}

GameApiResult gameApiPostSelector2(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kCurrencyGetPath, plain_body);
}

GameApiResult gameApiPostSelector3(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kSeasonQuestsGetPath, plain_body);
}

GameApiResult gameApiPostSelector4(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kQuestCreatePath, plain_body);
}

GameApiResult gameApiPostSelector5(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kAchievementStatsPath, plain_body);
}

GameApiResult gameApiPostSelector6(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kQuestUpdatePath, plain_body);
}

GameApiResult gameApiPostSelector7(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kWingBuffsDepositPath, plain_body);
}

GameApiResult gameApiPostSelector8(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kWingBuffsDropPath, plain_body);
}

GameApiResult gameApiPostSelector9(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kWingBuffsCollectPath, plain_body);
}

GameApiResult gameApiPostSelector0A(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kWingBuffsConvertPath, plain_body);
}

GameApiResult gameApiPostSelector0B(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kMapDefsGetPath, plain_body);
}

GameApiResult gameApiPostSelector0C(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kFriendConstellationPath, plain_body);
}

GameApiResult gameApiPostSelector0D(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kHeartFireSendPath, plain_body);
}

GameApiResult gameApiPostSelector0E(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kHeartFirePendingPath, plain_body);
}

GameApiResult gameApiPostSelector0F(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kHeartFireClaimPath, plain_body);
}

GameApiResult gameApiPostSelector10(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kHeartMessagePendingPath, plain_body);
}

GameApiResult gameApiPostSelector11(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kHeartMessageClaimPath, plain_body);
}

GameApiResult gameApiPostSelector12(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kWorldQuestRewardPath, plain_body);
}

GameApiResult gameApiPostSelector13(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kUserDataPath, plain_body);
}

GameApiResult gameApiPostSelector14(const GameApiSession &session, const std::string &plain_body) {
    return gameApiPostPath(session, kCollectPickupBatchPath, plain_body);
}

GameApiResult queryWingLight(const GameApiSession &session) {
    return queryReport(
        session,
        1,
        kWingQueryStart,
        kWingReportHeader,
        kWingReportFileName,
        kWingQueryDone);
}

GameApiResult queryCurrency(const GameApiSession &session) {
    return queryReport(
        session,
        2,
        kCurrencyQueryStart,
        kCurrencyReportHeader,
        kCurrencyReportFileName,
        kCurrencyQueryDone);
}

GameApiResult runDailyQuest(const GameApiSession &session) {
    sendDialogHintTimed(kDailyQuestStart, 3.0f);

    sendDialogHintTimed(kDailyQuestFetchList, 3.0f);
    GameApiResult list_result = gameApiPostSelector3(session, buildDefaultUserDataRequestBody(session));
    if (!list_result.ok || list_result.body.empty()) {
        sendDialogHintTimed(kDailyQuestListFailed, 3.0f);
        return list_result;
    }

    std::string decoded = decodeResponseBody(session, list_result.body);
    if (decoded.empty()) {
        sendDialogHintTimed(kDailyQuestDecodeFailed, 3.0f);
        list_result.ok = false;
        list_result.error = GAME_API_TEXT("daily quest list decode failed");
        return list_result;
    }
    std::vector<DailyQuestEntry> quests = parseDailyQuestDefinitions(decoded);
    if (quests.empty() && decoded != list_result.body) {
        quests = parseDailyQuestDefinitions(list_result.body);
    }
    if (quests.empty()) {
        sendDialogHintTimed(kDailyQuestNotFound, 3.0f);
        list_result.ok = false;
        list_result.error = GAME_API_TEXT("parse daily quest definitions failed");
        return list_result;
    }

    std::vector<DailyQuestEntry> created_quests;
    created_quests.reserve(quests.size());
    GameApiResult last_result = list_result;
    int created = 0;
    int failed = 0;
    for (std::size_t i = 0; i < quests.size(); ++i) {
        DailyQuestEntry &quest = quests[i];
        GameApiResult create = gameApiPostSelector4(session, buildCreateDailyQuestBody(session, quest));
        last_result = create;
        if (!create.ok || create.body.empty()) {
            ++failed;
            usleep(0x493E0);
            continue;
        }

        std::string create_body = decodeResponseBody(session, create.body);
        if ((!mergeDailyQuestCreateResponse(create_body, &quest)) && create_body != create.body) {
            mergeDailyQuestCreateResponse(create.body, &quest);
        }
        if (quest.quest_id.empty()) {
            ++failed;
            usleep(0x493E0);
            continue;
        }

        char created_message[160];
        std::snprintf(created_message, sizeof(created_message), kDailyQuestCreatedFormat, quest.quest_name.c_str(), quest.target_value);
        sendDialogHintTimed(created_message, 3.0f);
        created_quests.push_back(quest);
        ++created;
        usleep(0x493E0);
    }

    if (created_quests.empty()) {
        sendDialogHintTimed(kDailyQuestNotFound, 3.0f);
        list_result.ok = false;
        list_result.error = GAME_API_TEXT("daily quest create returned no quest ids");
        return list_result;
    }

    int completed = 0;
    for (std::size_t i = 0; i < created_quests.size(); ++i) {
        DailyQuestEntry &quest = created_quests[i];
        sendFormattedDialog(kDailyQuestProgressFormat, i + 1, created_quests.size(), 2.0f);
        GameApiResult stats = gameApiPostSelector5(session, buildDailyQuestAchievementBody(session, quest));
        last_result = stats;
        usleep(0x493E0);

        GameApiResult status = gameApiPostSelector6(session, buildDailyQuestStatusBody(session, quest));
        last_result = status;
        usleep(0x7A120);

        GameApiResult collect = gameApiPostSelector6(session, buildDailyQuestCollectBody(session, quest));
        last_result = collect;
        usleep(0x7A120);

        if (stats.ok && status.ok && collect.ok) {
            ++completed;
        } else {
            ++failed;
        }
    }

    sendDialogHintTimed(kDailyQuestDone, 5.0f);
    if (failed != 0) {
        last_result.ok = false;
        if (last_result.error.empty()) {
            last_result.error = GAME_API_TEXT("daily quest completed with failed requests");
        }
    }
    logInfo(
        "runDailyQuest recovered completed quests=%zu created=%d completed=%d failed=%d last_status=%d",
        quests.size(),
        created,
        completed,
        failed,
        last_result.http_status);
    return last_result;
}

GameApiResult sacrificeWingLight(const GameApiSession &session) {
    sendDialogHintTimed(kSacrificeWingStart, 2.0f);

    GameApiResult initial = gameApiPostSelector1(session, "");
    if (!initial.ok || initial.body.empty()) {
        return initial;
    }

    std::string decoded = decodeResponseBody(session, initial.body);
    std::vector<WingBuffEntry> wings = parseWingBuffEntries(decoded);
    if (wings.empty() && decoded != initial.body) {
        wings = parseWingBuffEntries(initial.body);
    }
    if (wings.empty()) {
        initial.ok = false;
        initial.error = GAME_API_TEXT("parse wing buffs failed");
        return initial;
    }

    constexpr int kWingDepositSlotCount = 63;
    const long long week_start = currentWeekStartSeconds();
    bool seen_deposit[kWingDepositSlotCount] = {};
    std::vector<long long> converted_this_week;
    std::vector<WingDepositRequest> collect_with_deposit;
    std::vector<std::string> collected_missing_deposit;
    std::vector<std::string> collect_missing_deposit;
    std::vector<WingDepositRequest> deposit_requests;

    int already_converted_this_week = 0;
    for (const WingBuffEntry &entry : wings) {
        for (int i = 0; i < kWingDepositSlotCount; ++i) {
            if (entry.deposit_id == (1LL << i)) {
                seen_deposit[i] = true;
                break;
            }
        }
        if (wasConvertedThisWeek(entry.last_conversion, week_start)) {
            ++already_converted_this_week;
            converted_this_week.push_back(entry.deposit_id);
            continue;
        }

        if (entry.deposit_id > 0) {
            if (entry.collected) {
                deposit_requests.push_back({entry.name, entry.deposit_id});
            } else {
                collect_with_deposit.push_back({entry.name, entry.deposit_id});
            }
        } else if (entry.collected) {
            appendUniqueName(&collected_missing_deposit, entry.name);
        } else {
            appendUniqueName(&collect_missing_deposit, entry.name);
        }
    }

    if (already_converted_this_week >= kWingDepositSlotCount) {
        sendDialogHintTimed(kSacrificeWingWeeklyFull, 3.0f);
        logInfo("sacrificeWingLight recovered skipped weekly converted=%d", already_converted_this_week);
        return initial;
    }

    std::vector<long long> missing_deposit_ids;
    for (int i = 0; i < kWingDepositSlotCount; ++i) {
        long long deposit_id = 1LL << i;
        if (!seen_deposit[i] &&
            std::find(converted_this_week.begin(), converted_this_week.end(), deposit_id) == converted_this_week.end()) {
            missing_deposit_ids.push_back(deposit_id);
        }
    }

    for (const WingDepositRequest &request : collect_with_deposit) {
        if (deposit_requests.size() >= kWingDepositSlotCount) {
            break;
        }
        gameApiPostSelector9(session, buildWingBuffCollectBody(session, request.name));
        deposit_requests.push_back(request);
        usleep(0x186A0);
    }

    std::size_t missing_index = 0;
    for (const std::string &name : collected_missing_deposit) {
        if (deposit_requests.size() >= kWingDepositSlotCount || missing_index >= missing_deposit_ids.size()) {
            break;
        }
        deposit_requests.push_back({name, missing_deposit_ids[missing_index++]});
    }

    for (const std::string &name : collect_missing_deposit) {
        if (deposit_requests.size() >= kWingDepositSlotCount || missing_index >= missing_deposit_ids.size()) {
            break;
        }
        gameApiPostSelector9(session, buildWingBuffCollectBody(session, name));
        deposit_requests.push_back({name, missing_deposit_ids[missing_index++]});
        usleep(0x186A0);
    }

    std::size_t deposits_to_send = deposit_requests.size();
    if (deposits_to_send > static_cast<std::size_t>(kWingDepositSlotCount)) {
        deposits_to_send = kWingDepositSlotCount;
    }

    char count_message[128];
    std::snprintf(
        count_message,
        sizeof(count_message),
        kSacrificeWingCountFormat,
        already_converted_this_week,
        deposits_to_send);
    sendDialogHintTimed(count_message, 3.0f);

    if (deposits_to_send == 0) {
        sendDialogHintTimed(kSacrificeWingNoCandidates, 3.0f);
        return initial;
    }

    GameApiResult last_result = initial;
    int success_count = 0;
    int failure_count = 0;
    for (std::size_t i = 0; i < deposits_to_send; ++i) {
        last_result = gameApiPostSelector7(session, buildWingBuffDepositBody(session, deposit_requests[i]));
        if (responseContainsDecodedOrPlain(session, last_result.body, "\"result\":\"ok\"")) {
            ++success_count;
        } else {
            ++failure_count;
        }

        if (((i + 1) % 10) == 0 || i + 1 == deposits_to_send) {
            char progress[64];
            std::snprintf(progress, sizeof(progress), kSacrificeWingProgressFormat, static_cast<int>(i + 1), static_cast<int>(deposits_to_send));
            sendDialogHintTimed(progress, 2.0f);
        }
        usleep(0x186A0);
    }

    sendDialogHintTimed(kSacrificeWingDropAll, 2.0f);
    GameApiResult refreshed = gameApiPostSelector1(session, "");
    if (refreshed.ok && !refreshed.body.empty()) {
        std::string refreshed_body = decodeResponseBody(session, refreshed.body);
        std::vector<std::string> drop_names = parseWingBuffNames(refreshed_body);
        if (drop_names.empty() && refreshed_body != refreshed.body) {
            drop_names = parseWingBuffNames(refreshed.body);
        }
        if (!drop_names.empty()) {
            gameApiPostSelector8(session, buildWingBuffDropBody(session, drop_names));
        }
        last_result = refreshed;
    }

    sendDialogHintTimed(kSacrificeWingConvert, 2.0f);
    GameApiResult convert = gameApiPostSelector0A(session, "");
    if (!convert.ok) {
        last_result = convert;
    }

    char done[128];
    std::snprintf(done, sizeof(done), kSacrificeWingDoneFormat, success_count, failure_count);
    sendDialogHintTimed(done, 5.0f);
    logInfo(
        "sacrificeWingLight recovered completed wings=%zu collect_first=%zu collect_after_missing=%zu deposits=%zu success=%d failure=%d",
        wings.size(),
        collect_with_deposit.size(),
        collect_missing_deposit.size(),
        deposits_to_send,
        success_count,
        failure_count);
    return last_result;
}

GameApiResult dropAllWingBuffs(const GameApiSession &session) {
    sendDialogHintTimed(kSacrificeWingDropAll, 2.0f);

    GameApiResult result = gameApiPostSelector1(session, "");
    if (!result.ok || result.body.empty()) {
        return result;
    }

    std::string decoded = decodeResponseBody(session, result.body);
    std::vector<std::string> names = parseWingBuffNames(decoded);
    if (names.empty() && decoded != result.body) {
        names = parseWingBuffNames(result.body);
    }

    if (names.empty()) {
        result.ok = false;
        result.error = GAME_API_TEXT("parse wing buff names failed");
        sendDialogHintTimed(kSacrificeWingNoCandidates, 3.0f);
        return result;
    }

    char count_text[64];
    std::snprintf(count_text, sizeof(count_text), "%zu", names.size());
    sendDialogHintTimed(count_text, 2.0f);

    GameApiResult drop = gameApiPostSelector8(session, buildWingBuffDropBody(session, names));
    std::string body = drop.body.empty() ? drop.body : decodeResponseBody(session, drop.body);
    if (body.find("\"result\":\"ok\"") == std::string::npos &&
        (body == drop.body || drop.body.find("\"result\":\"ok\"") == std::string::npos)) {
        drop.ok = false;
        if (drop.error.empty()) {
            drop.error = GAME_API_TEXT("wing buff drop response missing ok");
        }
        sendDialogHintTimed(kCollectPickupUnknownFailure, 3.0f);
    } else {
        sendDialogHintTimed(kCollectPickupSucceeded, 3.0f);
    }

    logInfo(
        "dropAllWingBuffs recovered sub_195B04 selector1->8 names=%zu ok=%d status=%d body_len=%zu",
        names.size(),
        drop.ok ? 1 : 0,
        drop.http_status,
        drop.body.size());
    return drop;
}

GameApiResult queryWingStats(const GameApiSession &session) {
    sendDialogHintTimed(kWingStatsStart, 2.0f);

    GameApiResult result = gameApiPostSelector0B(session, buildDefaultUserDataRequestBody(session));
    if (!result.ok || result.body.empty()) {
        sendDialogHintTimed(kWingStatsRequestFailed, 3.0f);
        return result;
    }

    std::string decoded = decodeResponseBody(session, result.body);
    if (decoded.empty()) {
        result.ok = false;
        result.error = GAME_API_TEXT("decode wing stats failed");
        sendDialogHintTimed(kWingStatsDecodeFailed, 3.0f);
        return result;
    }

    WingStats stats;
    if (!parseWingStats(decoded, &stats) && decoded != result.body) {
        parseWingStats(result.body, &stats);
    }

    char done[128];
    std::snprintf(done, sizeof(done), kWingStatsDoneFormat, stats.wing_buff, stats.collectible);
    sendDialogHintTimed(done, 5.0f);
    logInfo(
        "queryWingStats recovered selector=0x0B path=%s wing_buff=%d collectible=%d body_len=%zu",
        kMapDefsGetPath,
        stats.wing_buff,
        stats.collectible,
        result.body.size());
    return result;
}

GameApiResult sendHeartFireToFriends(const GameApiSession &session) {
    sendDialogHintTimed(kSendHeartFireStart, 2.0f);

    GameApiResult result = gameApiPostSelector0C(session, buildFriendConstellationBody(session));
    if (!result.ok || result.body.empty()) {
        sendDialogHintTimed(kSendHeartFireListFailed, 3.0f);
        return result;
    }

    std::string decoded = decodeResponseBody(session, result.body);
    std::vector<FriendEntry> friends = parseFriendConstellationList(decoded);
    if (friends.empty() && decoded != result.body) {
        friends = parseFriendConstellationList(result.body);
    }
    if (friends.empty()) {
        sendDialogHintTimed(kSendHeartFireNoFriends, 3.0f);
        return result;
    }

    sendFormattedDialog(kSendHeartFireCountFormat, friends.size(), static_cast<std::size_t>(-1), 2.0f);

    int success_count = 0;
    int failure_count = 0;
    for (std::size_t i = 0; i < friends.size(); ++i) {
        GameApiResult send = gameApiPostSelector0D(session, buildSendHeartFireBody(session, friends[i]));
        if (responseContainsDecodedOrPlain(session, send.body, "\"sent_free_gift\"")) {
            ++success_count;
        } else {
            ++failure_count;
        }

        if (((i + 1) % 10) == 0 || i + 1 == friends.size()) {
            sendFormattedDialog(kSendHeartFireProgressFormat, i + 1, friends.size(), 1.0f);
        }
        usleep(0x493E0);
    }

    char done[128];
    std::snprintf(done, sizeof(done), kSendHeartFireDoneFormat, success_count, failure_count);
    sendDialogHintTimed(done, 3.0f);
    logInfo("sendHeartFireToFriends recovered completed total=%zu success=%d failure=%d", friends.size(), success_count, failure_count);
    return result;
}

GameApiResult collectHeartFire(const GameApiSession &session) {
    sendDialogHintTimed(kCollectHeartFireStart, 2.0f);

    GameApiResult result = gameApiPostSelector0E(session, buildPendingHeartFireBody(session));
    if (!result.ok || result.body.empty()) {
        sendDialogHintTimed(kCollectHeartFireListFailed, 3.0f);
        return result;
    }

    std::string decoded = decodeResponseBody(session, result.body);
    std::vector<HeartFireGift> gifts = parsePendingHeartFireList(decoded);
    if (gifts.empty() && decoded != result.body) {
        gifts = parsePendingHeartFireList(result.body);
    }
    if (gifts.empty()) {
        sendDialogHintTimed(kCollectHeartFireNoItems, 3.0f);
        return result;
    }

    sendFormattedDialog(kCollectHeartFireCountFormat, gifts.size(), static_cast<std::size_t>(-1), 2.0f);

    int success_count = 0;
    for (std::size_t i = 0; i < gifts.size(); ++i) {
        GameApiResult claim = gameApiPostSelector0F(session, buildClaimHeartFireBody(session, gifts[i]));
        std::string claim_body = claim.body.empty() ? claim.body : decodeResponseBody(session, claim.body);
        if (claim_body.find("\"result\":\"ok\"") != std::string::npos) {
            ++success_count;
        }

        if (((i + 1) % 10) == 0 || i + 1 == gifts.size()) {
            sendFormattedDialog(kCollectHeartFireProgressFormat, i + 1, gifts.size(), 1.0f);
        }
        usleep(0x186A0);
    }

    char done[128];
    std::snprintf(done, sizeof(done), kCollectHeartFireDoneFormat, success_count);
    sendDialogHintTimed(done, 3.0f);
    logInfo("collectHeartFire recovered completed total=%zu success=%d", gifts.size(), success_count);
    return result;
}

GameApiResult collectHearts(const GameApiSession &session) {
    sendDialogHintTimed(kCollectHeartsStart, 2.0f);

    GameApiResult result = gameApiPostSelector10(session, buildPendingHeartMessageBody(session));
    if (!result.ok || result.body.empty()) {
        sendDialogHintTimed(kCollectHeartsListFailed, 3.0f);
        return result;
    }

    std::string decoded = decodeResponseBody(session, result.body);
    std::vector<HeartMessage> messages = parsePendingHeartMessageList(decoded);
    if (messages.empty() && decoded != result.body) {
        messages = parsePendingHeartMessageList(result.body);
    }
    if (messages.empty()) {
        sendDialogHintTimed(kCollectHeartsNoItems, 3.0f);
        return result;
    }

    sendFormattedDialog(kCollectHeartsCountFormat, messages.size(), static_cast<std::size_t>(-1), 2.0f);

    int success_count = 0;
    for (std::size_t i = 0; i < messages.size(); ++i) {
        GameApiResult claim = gameApiPostSelector11(session, buildClaimHeartMessageBody(session, messages[i]));
        std::string claim_body = claim.body.empty() ? claim.body : decodeResponseBody(session, claim.body);
        if (claim_body.find("\"result\":\"ok\"") != std::string::npos) {
            ++success_count;
        }

        if (((i + 1) % 10) == 0 || i + 1 == messages.size()) {
            sendFormattedDialog(kCollectHeartsProgressFormat, i + 1, messages.size(), 1.0f);
        }
        usleep(0x186A0);
    }

    char done[128];
    std::snprintf(done, sizeof(done), kCollectHeartsDoneFormat, success_count);
    sendDialogHintTimed(done, 3.0f);
    logInfo("collectHearts recovered completed total=%zu success=%d", messages.size(), success_count);
    return result;
}

GameApiResult claimAncestorCandles(const GameApiSession &session) {
    sendDialogHintTimed(kAncestorCandlesStart, 2.0f);

    GameApiResult initial = gameApiPostSelector2(session, buildDefaultUserDataRequestBody(session));
    int initial_candles = 0;
    if (!initial.body.empty()) {
        std::string initial_body = decodeResponseBody(session, initial.body);
        if (!parseCandlesValue(initial_body, &initial_candles) && initial_body != initial.body) {
            parseCandlesValue(initial.body, &initial_candles);
        }
    }

    int processed = 0;
    int attempted = 0;
    const int total_tasks = ancestorCandleTotalTasks();
    std::uint32_t current_patch_id = 0;
    for (const AncestorCandleGroup &group : kAncestorCandleGroups) {
        if (group.patch_id != 0 && current_patch_id != group.patch_id) {
            recordGamePatchId(group.patch_id);
            current_patch_id = group.patch_id;
            usleep(0x4C4B40);
        }

        for (int i = 0; i < group.task_count && i < 4; ++i) {
            const char *task_name = group.tasks[i];
            if (!task_name || !task_name[0]) {
                continue;
            }

            ++attempted;
            GameApiResult claim = gameApiPostSelector12(session, buildClaimWorldQuestRewardBody(session, task_name));
            if (claim.ok) {
                ++processed;
            }

            char progress[64];
            std::snprintf(progress, sizeof(progress), kAncestorCandlesProgressFormat, attempted, total_tasks);
            sendDialogHintTimed(progress, 1.0f);
            usleep(0x186A0);
        }
    }

    recordGamePatchId(kPatchCandleSpaceId);
    usleep(0x2DC6C0);

    GameApiResult final_result = gameApiPostSelector2(session, buildDefaultUserDataRequestBody(session));
    if (!final_result.body.empty()) {
        final_result.body = decodeResponseBody(session, final_result.body);
    }
    int final_candles = initial_candles;
    if (!final_result.body.empty()) {
        parseCandlesValue(final_result.body, &final_candles);
    }

    char done[128];
    std::snprintf(done, sizeof(done), kAncestorCandlesDoneFormat, final_candles - initial_candles);
    sendDialogHintTimed(done, 5.0f);
    logInfo(
        "claimAncestorCandles recovered completed attempted=%d successful_http=%d candles_before=%d candles_after=%d",
        attempted,
        processed,
        initial_candles,
        final_candles);
    return final_result.ok ? final_result : initial;
}

GameApiResult setPreferredHomeLevel(int preferred_home_level, const GameApiSession &session) {
    GameApiResult result;
    if (preferred_home_level != 0 && preferred_home_level != 1) {
        result.error = GAME_API_TEXT("preferredHomeLevel must be 0 or 1");
        return result;
    }

    std::string fetch_plain = buildDefaultUserDataRequestBody(session);
    GameApiResult fetch = gameApiPostSelector13(session, fetch_plain);
    if (!fetch.ok) {
        return fetch;
    }

    std::string version_raw;
    std::string decoded_data;
    if (!parseUserDataResponse(session, fetch.body, &version_raw, &decoded_data)) {
        result.error = GAME_API_TEXT("parse userdata response failed");
        result.http_status = fetch.http_status;
        result.body = fetch.body;
        return result;
    }

    if (!jsonReplaceOrInsertInt(&decoded_data, "preferredHomeLevel", preferred_home_level)) {
        result.error = GAME_API_TEXT("patch preferredHomeLevel failed");
        result.http_status = fetch.http_status;
        result.body = decoded_data;
        return result;
    }

    std::string encoded_data = base64Encode(decoded_data);
    std::string update_plain = buildUserDataUpdateBody(session, version_raw, encoded_data);
    GameApiResult update = gameApiPostSelector13(session, update_plain);
    if (!update.ok) {
        return update;
    }

    usleep(0x2DC6C0);
    recordGamePatchId(kPatchHomeReloadId);
    usleep(0x5B8D80);
    recordGamePatchId(
        preferred_home_level == 0 ? kPatchHomeLevel0Id : kPatchHomeLevel1Id);
    usleep(0x2DC6C0);

    return update;
}

GameApiResult claimSeasonCandle(const GameApiSession &session) {
    GameApiResult result = gameApiPostSelector14(session, buildClaimSeasonCandleBody(session));
    if (!result.body.empty()) {
        result.body = decodeResponseBody(session, result.body);
    }

    if (!result.ok) {
        return result;
    }

    if (result.body.empty()) {
        result.ok = false;
        result.error = GAME_API_TEXT("request failed");
        sendDialogHintTimed(kCollectPickupRequestFailed, 3.0f);
        return result;
    }

    if (result.body.find("\"r\":\"ok\"") != std::string::npos) {
        sendDialogHintTimed(kCollectPickupSucceeded, 4.0f);
        logInfo("claimSeasonCandle recovered prompt: collect succeeded");
        return result;
    }

    result.ok = false;
    if (result.body.find("\"r\":\"missing_season_pass\"") != std::string::npos) {
        result.error = "missing_season_pass";
        sendDialogHintTimed(kCollectPickupMissingSeasonPass, 4.0f);
        logWarn("claimSeasonCandle recovered prompt: missing season pass");
    } else if (result.body.find("\"r\":\"already_collected\"") != std::string::npos) {
        result.error = "already_collected";
        sendDialogHintTimed(kCollectPickupAlreadyCollected, 4.0f);
        logWarn("claimSeasonCandle recovered prompt: already collected");
    } else {
        result.error = GAME_API_TEXT("unknown collect_pickup_batch response");
        sendDialogHintTimed(kCollectPickupUnknownFailure, 4.0f);
        logWarn("claimSeasonCandle recovered prompt: unknown error");
    }
    return result;
}

GameApiResult runFastMapRoute(const GameApiSession &session) {
    const time_t start_time = std::time(nullptr);
    GameApiResult last_result;
    int failed_requests = 0;
    int live_route_name_write_failures = 0;
    int live_level_read_failures = 0;
    int submitted_batches = 0;
    std::size_t submitted_pickups = 0;

    const Feature8013Route *routes = feature8013Routes();
    const std::size_t route_count = feature8013RouteCount();
    for (std::size_t route_index = 0; route_index < route_count; ++route_index) {
        const Feature8013Route &route = routes[route_index];
        std::uint32_t level_id = 0;

        std::uintptr_t route_name_address = 0;
        std::size_t route_name_bytes = 0;
        const bool route_name_written = writeFastRouteRouteNameToLiveMemory(
            route.level_name,
            &route_name_address,
            &route_name_bytes);
        if (!route_name_written) {
            ++live_route_name_write_failures;
            logWarn(
                "runFastMapRoute live route-name write failed route=%s address=0x%llX written=%zu/48 chain=unk_364188 source=sub_19F16C",
                route.level_name ? route.level_name : "",
                static_cast<unsigned long long>(route_name_address),
                route_name_bytes);
        }
        usleep(0x7A120);

        std::uintptr_t live_level_address = 0;
        if (!readFastRouteLiveLevelId(&level_id, &live_level_address)) {
            ++live_level_read_failures;
            level_id = 0;
            logWarn(
                "runFastMapRoute live level_id read failed route=%s level_id=0 chain=qword_3640B8 helper=sub_16C56C",
                route.level_name ? route.level_name : "");
        } else {
            logInfo(
                "runFastMapRoute live level_id route=%s id=0x%08X address=0x%llX route_name_address=0x%llX source=unk_364188/qword_3640B8 helper=sub_16D388/sub_16C56C",
                route.level_name ? route.level_name : "",
                level_id,
                static_cast<unsigned long long>(live_level_address),
                static_cast<unsigned long long>(route_name_address));
        }

        for (std::size_t offset = 0; offset < route.pickup_count; offset += kFastRouteBatchSize) {
            const std::size_t batch_count = std::min(kFastRouteBatchSize, route.pickup_count - offset);
            last_result = gameApiPostSelector14(
                session,
                buildCollectPickupBatchBody(session, level_id, route.pickup_ids + offset, batch_count));
            ++submitted_batches;
            submitted_pickups += batch_count;
            if (!last_result.ok) {
                ++failed_requests;
            }
        }

        if (((route_index + 1) % kFastRoutePauseEveryRoutes) == 0) {
            usleep(0x4C4B40);
        }
    }

    recordGamePatchId(kPatchCandleSpaceId);
    usleep(0x4C4B40);

    const time_t elapsed = std::time(nullptr) - start_time;
    char done[128];
    std::snprintf(
        done,
        sizeof(done),
        kFastRouteDoneFormat,
        static_cast<int>(elapsed / 60),
        static_cast<int>(elapsed % 60));
    sendDialogHintTimed(done, 6.0f);

    if (failed_requests) {
        last_result.ok = false;
        if (last_result.error.empty()) {
            last_result.error = GAME_API_TEXT("runFastMapRoute completed with failed requests");
        }
    }
    logInfo(
        "runFastMapRoute recovered completed routes=%zu batches=%d pickups=%zu failed=%d live_route_name_write_failures=%d live_level_read_failures=%d",
        route_count,
        submitted_batches,
        submitted_pickups,
        failed_requests,
        live_route_name_write_failures,
        live_level_read_failures);
    return last_result;
}

void recordGamePatchId(unsigned int id) {
    GameMemoryPatchResult result = applyGameMemoryPatchById(id);
    if (!result.ok) {
        logWarn(
            "game_memory_patch_table recovered id=0x%08X but replay failed: %s",
            id,
            result.error ? result.error : "unknown");
    }
}

}
