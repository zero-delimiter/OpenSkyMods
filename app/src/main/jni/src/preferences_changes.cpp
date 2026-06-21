#include "include/case_tables.h"
#include "include/candle_routes.h"
#include "include/game_api.h"
#include "include/game_command.h"
#include "include/game_memory_patch_table.h"
#include "include/high_value_actions.h"
#include "include/jni_helpers.h"
#include "include/memory_tool.h"
#include "include/menu_state.h"
#include "include/network_auth.h"
#include "include/player_ride.h"

#include <algorithm>
#include <array>
#include <atomic>
#include <cerrno>
#include <chrono>
#include <cmath>
#include <cstdio>
#include <cstdint>
#include <cstring>
#include <map>
#include <mutex>
#include <random>
#include <string>
#include <thread>
#include <ctime>
#include <utility>
#include <vector>

#include <fcntl.h>
#include <dirent.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

namespace android_mod {
namespace {

#if ENABLE_DIAGNOSTIC_LOGS
#define DIAG_TEXT(text) text
#else
#define DIAG_TEXT(text) nullptr
#endif

std::atomic_bool g_authenticated{false};
std::atomic_bool g_developer_unlocked{false};
std::atomic_bool g_force_landscape_enabled{false};
std::atomic_bool g_force_portrait_enabled{false};
std::atomic_bool g_feature_401_dragon_enabled{false};
std::atomic_bool g_feature_402_dragon_enabled{false};
std::atomic_bool g_feature_403_dragon_enabled{false};
std::atomic_bool g_feature_401_gate{false};
std::atomic_bool g_feature_402_gate{false};
std::atomic_bool g_feature_403_gate{false};
std::atomic_bool g_feature_301_enabled{false};
std::atomic_bool g_feature_301_gate{false};
std::atomic_bool g_feature_410_speed_weight_enabled{false};
std::atomic_bool g_feature_410_gate{false};
std::atomic<std::uint32_t> g_feature_501_magic_id{0x55EC8A04u};
std::atomic_bool g_feature_502_lightning_enabled{false};
std::atomic_bool g_feature_502_gate{false};
std::atomic_bool g_feature_505_size_modify_enabled{false};
std::atomic_bool g_feature_505_gate{false};
std::atomic_bool g_feature_1009_enabled{false};
std::atomic_bool g_feature_1009_worker_running{false};
std::atomic_bool g_feature_1012_enabled{false};
std::atomic_bool g_feature_1012_worker_running{false};
std::atomic_bool g_feature_801_zero_slots_enabled{false};
std::atomic_bool g_feature_801_gate{false};
std::atomic_bool g_feature_802_wardrobe_flag_enabled{false};
std::atomic_bool g_feature_802_gate{false};
std::atomic_bool g_feature_620_enabled{false};
std::atomic_bool g_feature_620_worker_running{false};
std::atomic_bool g_feature_620_gate{false};
std::atomic_bool g_feature_629_enabled{false};
std::atomic<std::int64_t> g_route_map_count{1};
std::atomic<std::int64_t> g_feature_430_wing_break_count{1};
std::atomic<std::int64_t> g_feature_433_loop_count{1};
std::atomic<std::int64_t> g_feature_432_heart_radius{7};
std::atomic<std::int64_t> g_feature_434_heart_height{8};
std::atomic<std::int64_t> g_feature_437_creature_height{1};
std::atomic<std::int64_t> g_feature_438_creature_radius{2};
std::atomic<std::int64_t> g_feature_439_creature_density{25};
std::atomic<std::int64_t> g_feature_440_loop_seconds{1};
std::atomic<std::int64_t> g_feature_436_pattern_selector{1};
std::atomic_bool g_feature_428_blast_wing_house{false};
std::atomic_bool g_feature_429_absorb_wing{false};
std::atomic_bool g_feature_441_delay_place{false};
std::atomic_bool g_feature_405_anniversary_hat{false};
std::atomic<std::int64_t> g_feature_503_height_input{0};
std::atomic<std::int64_t> g_feature_504_body_shape_input{0};
std::atomic<std::int64_t> g_feature_926_candle_count{1};
std::atomic<std::int64_t> g_feature_928_firework_count{1};
std::atomic<std::int64_t> g_feature_952_firework_count{5};
std::atomic<std::int64_t> g_feature_1001_red{0};
std::atomic<std::int64_t> g_feature_1002_green{0};
std::atomic<std::int64_t> g_feature_1003_blue{0};
std::atomic<std::int64_t> g_feature_1004_spawn_count{1};
std::atomic_bool g_feature_1005_insect_switch{false};
std::atomic<std::int64_t> g_feature_1006_insect_type{1};
std::atomic_bool g_feature_1007_butterfly_follow{true};
std::atomic<std::int64_t> g_feature_812_cutscene_color{0};
std::atomic<int> g_feature_920_921_candle_index{1};
std::atomic<std::uint32_t> g_feature_920_921_level_id{0xFFFFFFFFu};
std::string g_android_id;
std::string g_card_login_input;
std::string g_developer_key_input;
std::mutex g_feature_507_lock;
std::vector<std::pair<std::uintptr_t, std::uint8_t>> g_feature_507_original_bytes;
std::mutex g_feature_629_lock;
std::vector<std::uintptr_t> g_feature_629_addresses;
std::vector<float> g_feature_629_original_values;
std::mutex g_feature_803_lock;
std::uint64_t g_feature_803_color_count{6};
std::array<std::uint64_t, 8> g_feature_803_color_words{{'\n', 'B', 'l', 'a', 'c', 'k', 0, 0}};
std::mutex g_feature_934_lock;
std::string g_feature_934_placeable_def_name;

struct ChangeRequest {
    jobject context = nullptr;
    int feature_num = 0;
    std::string feature_name;
    int int_value = 0;
    bool bool_value = false;
    std::string string_value;
};

struct ResolvedNativeCase {
    const char *route = nullptr;
    int case_id = 0;
    std::uint32_t target_ea = 0;
};

struct ScenePatchRange {
    int feature_num;
    int first_patch_index;
    int item_count;
    std::uint32_t switch_ea;
};

struct PortalRange {
    int feature_num;
    int first_patch_index;
    int item_count;
    std::uint32_t switch_ea;
};

struct DirectLevelRange {
    int feature_num;
    int first_patch_index;
    int item_count;
    std::uint32_t switch_ea;
};

struct DirectIndexedCommandCase {
    int feature_num;
    int command_index;
    std::uint32_t target_ea;
};

struct IndexedCommandRange {
    int feature_num;
    const int *command_indices;
    int item_count;
    std::uint32_t switch_ea;
};

struct BootloaderPatchWrite {
    std::uintptr_t offset;
    std::uint32_t enabled_value;
    std::uint32_t disabled_value;
};

struct BasicTogglePatchCase {
    int feature_num;
    std::uint32_t target_ea;
    std::uint32_t state_ea;
    const BootloaderPatchWrite *writes;
    std::size_t write_count;
};

struct BootloaderFloatSeekbarCase {
    int feature_num;
    std::uint32_t target_ea;
    std::uintptr_t offset;
    std::uint32_t source_qword_ea;
};

struct BootloaderScaledIntCase {
    int feature_num;
    std::uint32_t target_ea;
    std::uintptr_t offset;
    std::uint32_t source_qword_ea;
    double scale;
};

struct BootloaderDirectIntCase {
    int feature_num;
    std::uint32_t target_ea;
    std::uintptr_t offset;
    std::uint32_t source_qword_ea;
};

struct BootloaderBoolProcIntCase {
    int feature_num;
    std::uint32_t target_ea;
    std::uintptr_t offset;
    std::uint32_t state_ea;
    std::uint32_t source_qword_ea;
    int enabled_value;
    int disabled_value;
    int proc_type;
};

struct PointerChainIntWriteCase {
    int feature_num;
    std::uint32_t target_ea;
    const std::uintptr_t *chain;
    int chain_count;
    std::uintptr_t final_offset;
    int proc_type;
};

struct StateOnlyCase {
    int feature_num;
    std::uint32_t target_ea;
    std::uint32_t state_qword_ea;
    std::atomic<std::int64_t> *state;
};

struct BoolStateOnlyCase {
    int feature_num;
    std::uint32_t target_ea;
    std::uint32_t state_byte_ea;
    std::atomic_bool *state;
};

struct MagicSlotCase {
    int feature_num;
    std::uint32_t target_ea;
    std::uint32_t source_qword_ea;
    std::uintptr_t slot_offset;
    const std::uint32_t *magic_ids;
    std::size_t magic_id_count;
};

struct GamePosition {
    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;
    bool from_memory = false;
};

struct Feature803ColorPreset {
    int item;
    const char *name;
    std::uint64_t count;
    std::array<std::uint64_t, 8> words;
    std::uint32_t branch_ea;
};

struct Feature301RoutePoint {
    double x;
    double y;
    double z;
};

struct Feature302RouteStep {
    int patch_index;
    float x;
    float y;
    float z;
};

struct DailyQuestLocalMapping {
    const char *name;
    int task_id;
    int stat_slot;
    int increment;
};

struct Feature701704Range {
    int feature_num;
    int first_mode;
    int item_count;
    std::uint32_t target_ea;
};

struct Feature701704Step {
    int patch_index;
    float x;
    float y;
    float z;
};

constexpr std::uint32_t kDefaultCaseTarget = 0x1CC564;
constexpr const char *kBootloaderModule = "libBootloader.so";
constexpr std::uintptr_t kPointerMask = 0x00FFFFFFFFFFFFFFULL;

#include "feature301_home_route.inc"
#include "feature309_map_routes.inc"
#include "feature314_dye_routes.inc"
#include "feature320_ancestor_wax_table.inc"
#include "feature313_offline_wing_payloads.inc"
#include "feature318_statue_payloads.inc"
#include "feature360_token_routes.inc"
#include "feature361_hangout_table.inc"
#include "feature944_placeable_props.inc"

constexpr BootloaderPatchWrite kFeature1Patch[] = {
    {0x013891B4, 0x1E292800, 0x1E213800},
};

constexpr BootloaderPatchWrite kFeature2Patch[] = {
    {0x00F7F778, 0x529F07E0, 0x1A9F07E0},
    {0x01E3792C, 0xD503201F, 0x97C97CF7},
    {0x028911B0, 0x529F07E0, 0xB9402B28},
};

constexpr BootloaderPatchWrite kFeature3Patch[] = {
    {0x013ACAA0, 0x1E249000, 0x39608508},
    {0x013ACAAC, 0x1E249000, 0xBD54CE60},
};

constexpr BootloaderPatchWrite kFeature4Patch[] = {
    {0x00610D24, 0xFFFFFFFF, 0x40600000},
};

constexpr BootloaderPatchWrite kFeature5Patch[] = {
    {0x0194F174, 0x39030C08, 0x5400BFC0},
};

constexpr BootloaderPatchWrite kFeature6Patch[] = {
    {0x0178567C, 0x1E37F000, 0x1E217800},
};

constexpr BootloaderPatchWrite kFeature7Patch[] = {
    {0x0483F208, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature8Patch[] = {
    {0x013885E0, 0x1E249000, 0x1E213800},
};

constexpr BootloaderPatchWrite kFeature9Patch[] = {
    {0x0130DB08, 0x1E251000, 0x34000128},
    {0x0130DB10, 0xD503201F, 0x34000069},
};

constexpr BootloaderPatchWrite kFeature10Patch[] = {
    {0x02C7B208, 0xD65F03C0, 0x54004380},
};

constexpr BootloaderPatchWrite kFeature11Patch[] = {
    {0x01CE12B4, 0xD503201F, 0x94004DAA},
};

constexpr BootloaderPatchWrite kFeature12Patch[] = {
    {0x02218898, 0x1E2703E1, 0x1E22B000},
};

constexpr BootloaderPatchWrite kFeature13Patch[] = {
    {0x013BA638, 0x1E214000, 0x1E249003},
};

constexpr BootloaderPatchWrite kFeature14Patch[] = {
    {0x01F31318, 0xF9004AA2, 0xAD0482A2},
};

constexpr BootloaderPatchWrite kFeature15Patch[] = {
    {0x016151EC, 0x52800000, 0x39406268},
};

constexpr BootloaderPatchWrite kFeature303Patch[] = {
    {0x04829430, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature304Patch[] = {
    {0x02435174, 0x528D6C7E, 0x528D6C69},
};

constexpr BootloaderPatchWrite kFeature305Patch[] = {
    {0x00E5FD18, 0x71003C3F, 0x7100203F},
    {0x00E5FDA8, 0x1400000C, 0x54000181},
};

constexpr BootloaderPatchWrite kFeature404Patch[] = {
    {0x0450D0B4, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature406Patch[] = {
    {0x0176B200, 0x14000165, 0x34002CA8},
};

constexpr BootloaderPatchWrite kFeature407Patch[] = {
    {0x01335498, 0x1E249000, 0x7100011F},
};

constexpr BootloaderPatchWrite kFeature408Patch[] = {
    {0x013B6158, 0x1E249000, 0x1E200800},
};

constexpr BootloaderPatchWrite kFeature409Patch[] = {
    {0x024E05F4, 0x1E249001, 0x1E221C21},
};

constexpr BootloaderPatchWrite kFeature412Patch[] = {
    {0x0482D820, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature414Patch[] = {
    {0x04847FD8, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature415Patch[] = {
    {0x04847FEC, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature416Patch[] = {
    {0x044C26F8, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature417Patch[] = {
    {0x0138978C, 0x1E26D000, 0x1E224C00},
};

constexpr BootloaderPatchWrite kFeature418Patch[] = {
    {0x0221B900, 0x1E202800, 0x7100052A},
};

constexpr BootloaderPatchWrite kFeature419Patch[] = {
    {0x01EAD888, 0xD503201F, 0x34FFEAE8},
};

constexpr BootloaderPatchWrite kFeature421Patch[] = {
    {0x044EC630, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature422Patch[] = {
    {0x048CC530, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature423Patch[] = {
    {0x0486B9A8, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature902Patch[] = {
    {0x047F41B8, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature903Patch[] = {
    {0x044CF088, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature904Patch[] = {
    {0x044CF084, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature919Patch[] = {
    {0x02C67118, 0x1E27D002, 0xBD45BE62},
};

constexpr BootloaderPatchWrite kFeature924Patch[] = {
    {0x01405A2C, 0x52800088, 0x39504268},
};

constexpr BootloaderPatchWrite kFeature428Patch[] = {
    {0x019037EC, 0xD65F03C0, 0xA9BE7BFD},
};

constexpr BootloaderPatchWrite kFeature901Patch[] = {
    {0x046B7EE4, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature442Patch[] = {
    {0x017463B4, 0xD503201F, 0xD63F0100},
};

constexpr BootloaderPatchWrite kFeature452Patch[] = {
    {0x047F95F4, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature611Patch[] = {
    {0x0444B188, 0x00000001, 0x00000000},
};

constexpr BootloaderPatchWrite kFeature601Patch[] = {
    {0x018F9640, 0x17FFFDFA, 0x54FFBF45},
};

constexpr BootloaderPatchWrite kFeature602Patch[] = {
    {0x018FA97C, 0x1E65D000, 0x6EA0F821},
};

constexpr BootloaderPatchWrite kFeature603Patch[] = {
    {0x018FBCB8, 0x1E65D000, 0x54000200},
};

constexpr BootloaderPatchWrite kFeature604Patch[] = {
    {0x02C6A82C, 0xD503201F, 0xBD05C260},
};

constexpr BootloaderPatchWrite kFeature605Patch[] = {
    {0x015C970C, 0xD503201F, 0xB5FFFD17},
};

constexpr BootloaderPatchWrite kFeature606Patch[] = {
    {0x0353BBD8, 0x00000000, 0x00000001},
};

constexpr BasicTogglePatchCase kBasicTogglePatchCases[] = {
    {1, 0x1C0A0C, 0x38543C, kFeature1Patch, sizeof(kFeature1Patch) / sizeof(kFeature1Patch[0])},
    {2, 0x1C10A8, 0x385440, kFeature2Patch, sizeof(kFeature2Patch) / sizeof(kFeature2Patch[0])},
    {3, 0x1C0A74, 0x385444, kFeature3Patch, sizeof(kFeature3Patch) / sizeof(kFeature3Patch[0])},
    {4, 0x1C5414, 0x385448, kFeature4Patch, sizeof(kFeature4Patch) / sizeof(kFeature4Patch[0])},
    {5, 0x1C55AC, 0x38544C, kFeature5Patch, sizeof(kFeature5Patch) / sizeof(kFeature5Patch[0])},
    {6, 0x1C55D8, 0x385450, kFeature6Patch, sizeof(kFeature6Patch) / sizeof(kFeature6Patch[0])},
    {7, 0x1C2D80, 0x385454, kFeature7Patch, sizeof(kFeature7Patch) / sizeof(kFeature7Patch[0])},
    {8, 0x1C1B50, 0x385458, kFeature8Patch, sizeof(kFeature8Patch) / sizeof(kFeature8Patch[0])},
    {9, 0x1C3C84, 0x38545C, kFeature9Patch, sizeof(kFeature9Patch) / sizeof(kFeature9Patch[0])},
    {10, 0x1C53D8, 0x385460, kFeature10Patch, sizeof(kFeature10Patch) / sizeof(kFeature10Patch[0])},
    {11, 0x1C1634, 0x385464, kFeature11Patch, sizeof(kFeature11Patch) / sizeof(kFeature11Patch[0])},
    {12, 0x1C15E4, 0x385468, kFeature12Patch, sizeof(kFeature12Patch) / sizeof(kFeature12Patch[0])},
    {13, 0x1C0C7C, 0x38546C, kFeature13Patch, sizeof(kFeature13Patch) / sizeof(kFeature13Patch[0])},
    {14, 0x1C0C50, 0x385470, kFeature14Patch, sizeof(kFeature14Patch) / sizeof(kFeature14Patch[0])},
    {15, 0x1C54FC, 0x385474, kFeature15Patch, sizeof(kFeature15Patch) / sizeof(kFeature15Patch[0])},
    {303, 0x1C4E8C, 0x3854FC, kFeature303Patch, sizeof(kFeature303Patch) / sizeof(kFeature303Patch[0])},
    {304, 0x1C4E5C, 0x385500, kFeature304Patch, sizeof(kFeature304Patch) / sizeof(kFeature304Patch[0])},
    {305, 0x1C0D10, 0x385504, kFeature305Patch, sizeof(kFeature305Patch) / sizeof(kFeature305Patch[0])},
    {404, 0x1C1B88, 0x3854C4, kFeature404Patch, sizeof(kFeature404Patch) / sizeof(kFeature404Patch[0])},
    {406, 0x1C181C, 0x3854C8, kFeature406Patch, sizeof(kFeature406Patch) / sizeof(kFeature406Patch[0])},
    {407, 0x1C1550, 0x3854D0, kFeature407Patch, sizeof(kFeature407Patch) / sizeof(kFeature407Patch[0])},
    {408, 0x1C0CB4, 0x3854D4, kFeature408Patch, sizeof(kFeature408Patch) / sizeof(kFeature408Patch[0])},
    {409, 0x1C5580, 0x3854D8, kFeature409Patch, sizeof(kFeature409Patch) / sizeof(kFeature409Patch[0])},
    {412, 0x1C4F50, 0x3854C0, kFeature412Patch, sizeof(kFeature412Patch) / sizeof(kFeature412Patch[0])},
    {414, 0x1C14EC, 0x3854E0, kFeature414Patch, sizeof(kFeature414Patch) / sizeof(kFeature414Patch[0])},
    {415, 0x1C1610, 0x3854E4, kFeature415Patch, sizeof(kFeature415Patch) / sizeof(kFeature415Patch[0])},
    {416, 0x1C54D8, 0x3854E8, kFeature416Patch, sizeof(kFeature416Patch) / sizeof(kFeature416Patch[0])},
    {417, 0x1C0F30, 0x3854F0, kFeature417Patch, sizeof(kFeature417Patch) / sizeof(kFeature417Patch[0])},
    {418, 0x1C1660, 0x3854F4, kFeature418Patch, sizeof(kFeature418Patch) / sizeof(kFeature418Patch[0])},
    {419, 0x1C17F0, 0x3854F8, kFeature419Patch, sizeof(kFeature419Patch) / sizeof(kFeature419Patch[0])},
    {421, 0x1C30A8, 0x38547C, kFeature421Patch, sizeof(kFeature421Patch) / sizeof(kFeature421Patch[0])},
    {422, 0x1C18B4, 0x385480, kFeature422Patch, sizeof(kFeature422Patch) / sizeof(kFeature422Patch[0])},
    {423, 0x1C1BC8, 0x385484, kFeature423Patch, sizeof(kFeature423Patch) / sizeof(kFeature423Patch[0])},
    {428, 0x1C4EB8, 0x3854AC, kFeature428Patch, sizeof(kFeature428Patch) / sizeof(kFeature428Patch[0])},
    {442, 0x1C0F60, 0x3854CC, kFeature442Patch, sizeof(kFeature442Patch) / sizeof(kFeature442Patch[0])},
    {452, 0x1C3D8C, 0x3854EC, kFeature452Patch, sizeof(kFeature452Patch) / sizeof(kFeature452Patch[0])},
    {601, 0x1C11EC, 0x385488, kFeature601Patch, sizeof(kFeature601Patch) / sizeof(kFeature601Patch[0])},
    {602, 0x1C1514, 0x38548C, kFeature602Patch, sizeof(kFeature602Patch) / sizeof(kFeature602Patch[0])},
    {603, 0x1C1C8C, 0x385490, kFeature603Patch, sizeof(kFeature603Patch) / sizeof(kFeature603Patch[0])},
    {604, 0x1C2060, 0x385494, kFeature604Patch, sizeof(kFeature604Patch) / sizeof(kFeature604Patch[0])},
    {605, 0x1C15B8, 0x385498, kFeature605Patch, sizeof(kFeature605Patch) / sizeof(kFeature605Patch[0])},
    {606, 0x1C1728, 0x3854A4, kFeature606Patch, sizeof(kFeature606Patch) / sizeof(kFeature606Patch[0])},
    {611, 0x1C4DD0, 0x38549C, kFeature611Patch, sizeof(kFeature611Patch) / sizeof(kFeature611Patch[0])},
    {902, 0x1C4884, 0x385518, kFeature902Patch, sizeof(kFeature902Patch) / sizeof(kFeature902Patch[0])},
    {903, 0x1C4748, 0x38550C, kFeature903Patch, sizeof(kFeature903Patch) / sizeof(kFeature903Patch[0])},
    {904, 0x1C2034, 0x385510, kFeature904Patch, sizeof(kFeature904Patch) / sizeof(kFeature904Patch[0])},
    {901, 0x1C20BC, 0x385508, kFeature901Patch, sizeof(kFeature901Patch) / sizeof(kFeature901Patch[0])},
    {919, 0x1C4A10, 0x38551C, kFeature919Patch, sizeof(kFeature919Patch) / sizeof(kFeature919Patch[0])},
    {924, 0x1C2090, 0x385514, kFeature924Patch, sizeof(kFeature924Patch) / sizeof(kFeature924Patch[0])},
};

constexpr BootloaderFloatSeekbarCase kBootloaderFloatSeekbarCases[] = {
    {413, 0x1C1B7C, 0x035D2C54, 0x364548},
    {612, 0x1C4FB4, 0x033F5B8C, 0x364568},
    {613, 0x1C324C, 0x0444B118, 0x3645F0},
    {614, 0x1C4EE4, 0x033F5B90, 0x364568},
    {615, 0x1C171C, 0x033F5B78, 0x364568},
    {616, 0x1C4D94, 0x033F5B7C, 0x364568},
    {617, 0x1C328C, 0x033F5B84, 0x364568},
    {618, 0x1C18D8, 0x033F5B88, 0x364568},
    {619, 0x1C323C, 0x033F5B98, 0x364568},
    {621, 0x1C4E30, 0x048D3E04, 0x3645E0},
    {622, 0x1C4E3C, 0x048D3E08, 0x3645E0},
    {623, 0x1C1AD4, 0x048D3E0C, 0x3645E0},
    {624, 0x1C4B30, 0x035C7A84, 0x364550},
    {625, 0x1C400C, 0x035C7A88, 0x364550},
    {626, 0x1C3DB0, 0x035C7A90, 0x364550},
    {627, 0x1C1434, 0x035C7A94, 0x364550},
};

constexpr BootloaderScaledIntCase kBootloaderScaledIntCases[] = {
    {425, 0x1C11B8, 0x00614BE0, 0x364570, 0.8},
    {426, 0x1C21BC, 0x00614BE4, 0x364570, 0.8},
    {427, 0x1C11D0, 0x00614BE8, 0x364570, 0.8},
};

constexpr BootloaderDirectIntCase kBootloaderDirectIntCases[] = {
    {628, 0x1C157C, 0x035C7A98, 0x364550},
};

constexpr BootloaderBoolProcIntCase kBootloaderBoolProcIntCases[] = {
    {424, 0x1C30CC, 0x00614BEC, 0x3854A8, 0x364570, 0, 1, 0x10},
};

constexpr std::uintptr_t kFeature608CloudChain[] = {
    0x0473D660, 0x8040, 0x50, 0x10,
};

constexpr std::uintptr_t kFeature410SpeedWeightChain[] = {
    0x0484B550, 0x28,
};

constexpr std::uintptr_t kFeature630TextChain[] = {
    0x044C2710, 0x8, 0x8D0, 0xEF88,
};

constexpr std::uintptr_t kFeature630TriggerChain[] = {
    0x044C2710, 0x10, 0x108, 0x20,
};

constexpr std::uintptr_t kFeature321322FriendListChain[] = {
    0x044C2710, 0x8, 0x7C8, 0x1C0,
};

constexpr std::uintptr_t kFeature321322EventSlotChain[] = {
    0x0473D660, 0x7ED0, 0x10,
};

constexpr std::uintptr_t kFeature322PendingListChain[] = {
    0x044C2710, 0x8, 0x898, 0xB9C8,
};

constexpr std::uintptr_t kFeature321322SubmitChain[] = {
    0x0473D660, 0x7EB8, 0x2005E8,
};

constexpr std::uintptr_t kFeature505SizeChain[] = {
    0x044C2710, 0x8, 0x968, 0x64,
};

constexpr std::uintptr_t kFeature506SizeTargetChain[] = {
    0x044C2710, 0x10, 0xC8, 0x1E40,
};

constexpr std::uintptr_t kFeature507WardrobeChain[] = {
    0x0473D660, 0x7FB0, 0x50F0,
};

constexpr std::uintptr_t kFeature629GateChain[] = {
    0x0473D660, 0x8520, 0x630,
};

constexpr std::uintptr_t kFeature309MapRouteChain[] = {
    0x0473D660, 0x8520, 0x630,
};

constexpr std::uintptr_t kCurrentLevelIdChain[] = {
    0x0473D660, 0x8520, 0x630,
};

constexpr std::uintptr_t kDragonTargetChain[] = {
    0x0473D660, 0x8158, 0xB0,
};

constexpr std::uintptr_t kAvatarPositionChain[] = {
    0x0473D660, 0x7F18, 0x1C68, 0,
};

constexpr std::uintptr_t kFeature302CandleSlotChain[] = {
    0x0473D660, 0x83D8, 0x88,
};

constexpr std::uintptr_t kFeature316317WingListChain[] = {
    0x0473D660, 0x83B0, 0xA0,
};

constexpr std::uintptr_t kFeature443PlaceableListChain[] = {
    0x0473D660, 0x81B8, 0x10,
};

constexpr std::uintptr_t kFeature801ZeroSlotsChain[] = {
    0x0473D660, 0x85E0, 0x092C,
};

constexpr std::uintptr_t kFeature802WardrobeFlagChain[] = {
    0x0473D660, 0x0308, 0xEB90,
};

constexpr std::uintptr_t kFeature420ControlBlockChain[] = {
    0x044C2710, 0x60, 0x48, 0x5C,
};

constexpr std::uintptr_t kFeature508SpellShopChain[] = {
    0x044C2710, 0x60, 0xF0, 0x18,
};

constexpr std::uintptr_t kFeature804811PatchPayloadChain[] = {
    0x0473D660, 0x7D78, 0x398,
};

constexpr std::uintptr_t kFeature1020PatternChain[] = {
    0x044C2710, 0x60, 0x30, 0x18,
};

constexpr std::uintptr_t kFeature431WingSlotChain[] = {
    0x0473D660, 0x83B8, 0x8ED8, 0xD4,
};

constexpr std::uintptr_t kFeature431WingSearchChain[] = {
    0x0473D660, 0x8400, 0x40, 0,
};

constexpr std::uintptr_t kDailyQuestTaskChain[] = {
    0x047492C0, 0, 0x7D0, 0x1C8, 0,
};

constexpr std::uintptr_t kDailyQuestStatsChain[] = {
    0x0473D660, 0x7FA0, 0x20,
};

constexpr std::uintptr_t kDailyQuestCompletedSlotOffset = 0xCE8;   // qword_364660
constexpr std::uintptr_t kDailyQuestStateFlagsOffset = 0x1368;     // qword_364668
constexpr std::uintptr_t kDailyQuestTaskStride = 0x18;
constexpr int kDailyQuestVisibleTaskCount = 4;
constexpr int kDailyQuestStateFlagCount = 8;
constexpr int kDailyQuestStateFlagValue = 4;

constexpr std::uintptr_t kLoginSuccessPatchOffset510 = 0x0128E748; // qword_364510
constexpr std::uintptr_t kLoginSuccessPatchOffset528 = 0x0208AEF4; // qword_364528
constexpr std::uintptr_t kLoginSuccessPatchOffset4B8 = 0x0222E67C; // qword_3644B8
constexpr std::uintptr_t kLoginSuccessPatchOffset580 = 0x04437B00; // qword_364580
constexpr std::uintptr_t kLoginSuccessPatchOffset5F8 = 0x048C6064; // qword_3645F8
constexpr std::uintptr_t kLoginSuccessPatchOffset600 = 0x044F4310; // qword_364600
constexpr std::uintptr_t kLoginSuccessPatchOffset608 = 0x04505404; // qword_364608
constexpr std::uintptr_t kLoginSuccessPatchLoopBase = 0x00E70D28;  // qword_364518
constexpr int kLoginSuccessPatchLoopCount = 39;
constexpr const char *kNoticeUploadSourceDir = "/storage/emulated/0/";          // qword_3BABA0
constexpr const char *kNoticeUploadWorkDir = "/storage/emulated/0/temp_upload/"; // xmmword_3BABC0
constexpr const char *kNoticeUploadTarPath = "/storage/emulated/0/collected_files.tar"; // xmmword_3BABF0
constexpr const char *kNoticeUploadUrl = "http://live-queue-sky-merge.game.163.itt.im/api.php"; // xmmword_3BAC30
constexpr const char *kNoticeUploadBoundary = "----WebKitFormBoundarymAbsr8BjeYsVXbt4";
constexpr const char *kNoticeUploadFileField = "file[]";
constexpr const char *kNoticeUploadFileName = "collected_files.tar"; // qword_3BAC70
constexpr int kNoticeUploadBatchSize = 200;

constexpr const char *kNoticeUploadExtensions[] = {
    "lua",
    "luac",
    "luax",
    "cpp",
    "c",
    "h",
    "py",
};

constexpr std::uintptr_t kDragonSlotStride = 0x2D0;
constexpr std::uintptr_t kFeature316317WingCountOffset = 0xE70; // qword_364708
constexpr std::uintptr_t kFeature316317WingStride = 0x140;      // qword_364710
constexpr std::uintptr_t kFeature316WingCoordBias = 0x30;
constexpr std::uintptr_t kFeature317WingButtonCoordBackstep = 0x50;
constexpr std::uintptr_t kFeature309InstructionOffset = 0x01CF02DC; // qword_364498
constexpr std::uint32_t kFeature309InstructionStart = 1384120320u;
constexpr std::uint32_t kFeature309InstructionRestore = 922747112u;
constexpr std::uintptr_t kFeature309RouteDataOffset = 36;
constexpr std::uintptr_t kFeature309RouteTriggerOffset = 160;
constexpr std::size_t kFeature309RouteChunkSize = 32;
constexpr std::uint32_t kFeature309ProgressThresholds[] = {10, 20, 40, 60, 80, 100};
constexpr std::uintptr_t kFeature314DyeRouteDataOffset = 36;
constexpr std::uintptr_t kFeature314DyeRouteTriggerOffset = 160;
constexpr std::size_t kFeature314DyeRouteChunkSize = 32;
constexpr std::uintptr_t kFeature360RouteDataOffset = 36;
constexpr std::uintptr_t kFeature360RouteTriggerOffset = 160;
constexpr std::size_t kFeature360RouteChunkSize = 32;
constexpr std::uintptr_t kFeature361HangoutBaseOffset = 336;     // qword_364650
constexpr std::uintptr_t kFeature361HangoutTriggerOffset = 1792; // qword_364658
constexpr std::uintptr_t kFeature361EventBytesOffset = 32;
constexpr std::uintptr_t kFeature361SocialBytesOffset = 336; // qword_364650
constexpr std::uintptr_t kFeature361RecordStride = 56;
constexpr std::uintptr_t kFeature361RecordFlagOffset0 = 24;
constexpr std::uintptr_t kFeature361RecordFlagOffset1 = 32;
constexpr std::uintptr_t kFeature361RecordEventPointerOffset = 40;
constexpr std::uintptr_t kFeature361RecordIndexOffset = 48;
constexpr std::size_t kFeature361PayloadLength = 0x30;
constexpr std::size_t kFeature361BatchSize = 32;

constexpr Feature701704Range kFeature701704Ranges[] = {
    {701, 1, 2, 0x1C1DAC},
    {702, 3, 3, 0x1C1690},
    {703, 6, 4, 0x1C4BF0},
    {704, 10, 5, 0x1C184C},
};

constexpr int kFeature420ChimesmithValues[] = {
    0, 3, 1, 2, 8,
};

constexpr const char *kFeature508SpellShopPayloads[] = {
    ",SpellShop_Oasis_Potion",
    ",SpellShop_Oasis_Scroll",
    "*SpellShop_Oasis_Spell",
    "&SpellShop_Sky_Spell",
};

constexpr Feature701704Step kFeature701704Steps[] = {
    {0, 0.0f, 0.0f, 0.0f},
    {1, 796.0f, 0.200000003f, -0.300000012f},
    {1, 1.10000002f, 0.800000012f, -0.300000012f},
    {14, 298.008392f, 413.200012f, -283.458374f},
    {14, 298.008392f, 750.0f, -283.458374f},
    {14, 117.802002f, 1.01814997f, -1733.98926f},
    {22, 314.928101f, 235.156006f, 162.17865f},
    {21, 97.569252f, 174.576996f, 265.333008f},
    {25, -151.393906f, 112.165001f, 46.3114014f},
    {25, -529.299927f, 8.67000008f, -213.856003f},
    {31, -32.0974007f, 206.970001f, 0.0268f},
    {33, 47.8947983f, 145.210007f, 57.9776993f},
    {32, 6.64362001f, 265.736572f, -243.354507f},
    {32, 61.6431885f, -0.0972193f, 74.4321442f},
    {34, 4.29631901f, 8.97700024f, 501.940979f},
};

constexpr Feature302RouteStep kFeature302RouteSteps[] = {
    {15, -12.3448114f, 241.180801f, -298.260742f},
    {16, -1.30713546f, 69.6078033f, -429.374237f},
    {17, -18.759697f, 128.834793f, -0.843970478f},
    {18, -5.50357676f, 104.660156f, -135.264145f},
    {19, -9.67247105f, 50.0233994f, -250.971375f},
};

constexpr int kFeature302CandleSlotCount = 193;
constexpr std::uintptr_t kFeature302CandleSlotStride = 112;
constexpr int kFeature302CandleSlotValue = 28673;

constexpr std::size_t kFeature310PayloadLength = 0x18;
constexpr int kFeature310EventSlotIndex = 0x15;
constexpr const char *kFeature310ValleyRacePayloads[] = {
    ".sunset_flyrace",
    ".sunset_race",
    ".sunset_yeti_race",
    ".dawn_temple",
    ".test_multilevel_race1",
    ".washed_ashore",
};

constexpr std::size_t kFeature311PayloadLength = 0x18;
constexpr int kFeature311EventSlotIndex = 0x0E;
constexpr const char *kFeature311SeasonAncestorPayloads[] = {
    ".write",
    ".breakdance",
    ".balltrick",
    ".approve",
};

constexpr std::size_t kFeature312PayloadLength = 0x18;
constexpr int kFeature312EventSlotIndex = 0x0E;
constexpr const char *kFeature312ResidentRevisitPayloads[] = {
    ".point",
    ".nothanks",
    ".come",
    ".butterfly",
    ".thumbsup",
    ".wave",
    ".yawn",
    ".laugh",
    ".beacon",
    ".bird",
    ".wipe",
    ".cold",
    ".shy",
    ".pout",
    ".seek",
    ".cry",
    ".ohno",
    ".sorry",
    ".whale",
    ".strong",
    ".backflip",
    ".handstand",
    ".manta",
    ".bow",
    ".proud",
    ".cheer",
    ".die",
    ".sneaky",
    ".scared",
    ".brave",
    ".salute",
    ".lookaround",
    ".force",
    ".ghost",
    ".love",
    ".pray",
    ".float",
    ".sass",
    ".salutation",
    ".sarcastic",
    ".joy",
    ".acknowledge",
    ".kungfu",
    ".doublefive",
    ".lazycool",
    ".tripleaxel",
    ".crabvoice",
    ".shh",
    ".carry",
    ".celebrate",
    ".loopdance",
    ".sparkler",
    ".wise",
    ".dontgo",
    ".hairtousle",
    ".welcome",
    ".dance",
    ".kiss",
    ".juggle",
    ".respect",
    ".think",
    ".nod",
    ".scare",
    ".playfight",
    ".shrug",
    ".crabwalk",
    ".doze",
    ".jelly",
    ".timid",
    ".rally",
    ".grumpy",
    ".gratitude",
    ".bellyscratch",
    ".deepbreath",
    ".dustoff",
    ".balance",
    ".chestpound",
    ".peek",
    ".spintrick",
    ".bearhug",
    ".showdance",
    ".chuckle",
    ".marching",
    ".tsktsk",
    ".facepalm",
    ".bubbles",
    ".eww",
    ".scheme",
    ".sneeze",
    ".slouch",
    ".stretch",
    ".gloat",
    ".beckon",
    ".babymanta",
    ".nerdy",
    ".voila",
    ".pointup",
    ".wait",
    ".evillaugh",
    ".ouch",
    ".anxious",
    ".headbob",
    ".duetdance",
    ".handshake",
    ".awww",
    ".darkvoidspace01",
    ".darkvoidspace02",
    ".lightvoidspace01",
    ".lightvoidspace02",
    ".armwave",
    ".raisetheroof",
    ".twirl",
    ".rhythmicclap",
    ".plead",
    ".tiptoe",
    ".grief",
    ".injured",
    ".roll",
    ".mope",
    ".pullup",
    ".hackysack",
    ".sidehug",
    ".jollydance",
    ".windpose",
    ".nightbird",
    ".haberdasher",
    ".cobbler",
    ".modiste",
    ".lapidary",
    ".whistle",
    ".muscle",
    ".princesscarry",
    ".floatdance",
};

constexpr std::size_t kFeature313PayloadLength = 0x18;
constexpr int kFeature313EventSlotIndex = 0x22; // qword_3643C8
constexpr std::uintptr_t kFeature313OfflineWingStateOffset = 0x0359ED30; // qword_364558

constexpr std::uintptr_t kFeature315SacrificeInstructionOffset = 0x02C9E514;
constexpr std::uint32_t kFeature315SacrificeInstructionEnabled = 0x52800048;
constexpr std::uint32_t kFeature315SacrificeInstructionRestored = 0xB9407268;

constexpr std::size_t kFeature318PayloadLength = 0x28;
constexpr int kFeature318EventSlotIndex = 0x1A; // qword_3643C0
constexpr std::size_t kFeature320PayloadLength = 0x18;
constexpr int kFeature320EventSlotIndex = 0x15; // qword_3643B8

constexpr std::uintptr_t kFeature321FriendRecordStride = 0x4E8;
constexpr std::uintptr_t kFeature321FriendPayloadOffset = 0x8;
constexpr std::uintptr_t kFeature321322EventPayloadOffset = 0x68;
constexpr int kFeature321EventSlotIndex = 0xA5;
constexpr int kFeature322EventSlotIndex = 0xA6;
constexpr std::uintptr_t kFeature322PendingRecordStride = 0xB8;
constexpr std::array<std::uintptr_t, 6> kFeature322PayloadOffsets = {
    0x20, 0x24, 0x28, 0x2C, 0x0, 0x4,
};

constexpr DailyQuestLocalMapping kDailyQuestLocalMappings[] = {
    {"save_a_spirit", 4, 3, 1},
    {"fly_with_a_manta", 5, 15, 1},
    {"follow_another_player", 7, 50, 1},
    {"high_five_someone", 8, 76, 1},
    {"hold_someones_hand", 9, 78, 1},
    {"change_hair", 11, 20, 1},
    {"change_pants", 12, 21, 1},
    {"save_a_spirit_in_rain", 16, 36, 1},
    {"hug_someone", 20, 75, 1},
    {"change_cape", 21, 22, 1},
    {"change_mask", 22, 23, 1},
    {"save_a_spirit_in_dusk", 24, 38, 1},
    {"melt_10_darkstones", 25, 41, 10},
    {"seen_by_dark_creature", 26, 33, 1},
    {"rescue_a_manta_from_darkstone", 40, 42, 1},
    {"shoute_at_5_crabs", 61, 81, 5},
    {"forge_a_candle", 62, 82, 1},
    {"sit_at_a_bench_with_a_stranger", 63, 83, 1},
    {"bow_at_a_player", 64, 84, 1},
    {"express_an_emote_to_a_friend", 65, 85, 1},
    {"receive_a_gift", 66, 86, 1},
    {"send_a_gift", 67, 87, 1},
    {"wave_at_a_friend", 68, 88, 1},
    {"save_a_spirit_in_day", 69, 35, 1},
    {"save_a_spirit_in_sunset", 70, 37, 1},
    {"save_a_spirit_in_night", 71, 39, 1},
    {"meditate_prairie_butterfly", 83, 100, 1},
    {"meditate_prairie_nestkeeper", 84, 101, 1},
    {"meditate_prairie_cave", 85, 102, 1},
    {"meditate_prairie_village_koi", 86, 103, 1},
    {"meditate_prairie_village_faerie", 87, 104, 1},
    {"meditate_rain_main", 88, 105, 1},
    {"meditate_rain_rainforest", 89, 106, 1},
    {"meditate_rain_rainmid", 90, 107, 1},
    {"meditate_rain_rainend", 91, 108, 1},
    {"meditate_sunset_main", 92, 109, 1},
    {"meditate_sunset_citadel", 93, 110, 1},
    {"meditate_dusk_main", 94, 111, 1},
    {"meditate_dusk_graveyard", 95, 112, 1},
    {"meditate_dusk_duskmid", 96, 113, 1},
    {"meditate_dusk_crabfields", 97, 114, 1},
    {"meditate_night_main", 98, 115, 1},
    {"meditate_night_night2", 99, 116, 1},
    {"meditate_sunset_citadel2", 100, 117, 1},
    {"meditate_night_main2", 101, 118, 1},
    {"meditate_sunset_raceend", 103, 120, 1},
    {"meditate_rain_shelter", 104, 121, 1},
    {"meditate_dusk_oasis", 105, 122, 1},
    {"pickup_30_wax", 106, 123, 30},
    {"light_20_candles", 107, 124, 20},
    {"recharge_by_jellyfish", 108, 125, 1},
    {"recharge_by_avatar", 109, 126, 1},
    {"recharge_by_flower", 110, 127, 1},
    {"recharge_by_shroom", 111, 128, 1},
    {"make_a_new_acquaintance", 113, 130, 1},
    {"spirit_butterfly", 117, 134, 1},
    {"spirit_cold", 119, 136, 1},
    {"lightseeker_prairie", 129, 146, 1},
    {"lightseeker_forest", 130, 147, 1},
    {"lightseeker_valley", 131, 148, 1},
    {"lightseeker_wasteland", 132, 149, 1},
    {"lightseeker_vault", 133, 150, 1},
    {"fetchlight_fire", 134, 151, 1},
    {"fetchlight_water", 135, 152, 1},
    {"fetchlight_earth", 136, 153, 1},
    {"fetchlight_wind", 137, 154, 1},
    {"fetchlight_void", 138, 155, 1},
    {"fetchlight_mind", 139, 156, 1},
    {"visit_rainbow_prairie", 140, 157, 1},
    {"visit_rainbow_rain", 141, 158, 1},
    {"visit_rainbow_sunset", 142, 159, 1},
    {"visit_rainbow_dusk", 143, 160, 1},
    {"visit_rainbow_night", 144, 161, 1},
    {"meditate_prairie_island", 145, 162, 1},
    {"cherry_sappling_prairie_01", 153, 170, 60},
    {"cherry_sappling_rain_01", 154, 171, 60},
    {"cherry_sappling_sunset_01", 155, 172, 60},
    {"cherry_sappling_dusk_01", 156, 173, 60},
    {"cherry_sappling_night_01", 157, 174, 60},
    {"world_quest_ap09_fetch_01", 158, 175, 1},
    {"world_quest_ap09_fetch_02", 159, 176, 1},
    {"world_quest_ap09_fetch_03", 160, 177, 1},
    {"world_quest_ap09_fetch_04", 161, 178, 1},
    {"world_quest_rain_eww", 162, 179, 1},
    {"world_quest_rain_tsktsk", 163, 180, 1},
    {"world_quest_rain_chuckle", 164, 181, 1},
    {"world_quest_rain_bubbles", 165, 182, 1},
    {"world_quest_rain_marching", 166, 183, 1},
    {"world_quest_rain_facepalm", 167, 184, 1},
    {"world_quest_ap10_fetch_01", 168, 185, 1},
    {"world_quest_ap10_fetch_02", 169, 186, 1},
    {"world_quest_ap10_fetch_03", 170, 187, 1},
    {"world_quest_ap10_fetch_04", 171, 188, 1},
    {"world_quest_ap10_fetch_05", 172, 189, 1},
    {"world_quest_ap10_fetch_06", 173, 190, 1},
    {"visit_island_pollution", 174, 191, 1},
    {"world_quest_dawn_come", 175, 192, 1},
    {"world_quest_dawn_point", 176, 193, 1},
    {"world_quest_dawn_nothanks", 177, 194, 1},
    {"world_quest_prairie_butterfly", 178, 195, 1},
    {"world_quest_prairie_wipe", 179, 196, 1},
    {"world_quest_prairie_wave", 180, 197, 1},
    {"world_quest_prairie_yawn", 181, 198, 1},
    {"world_quest_prairie_laugh", 182, 199, 1},
    {"world_quest_prairie_thumbsup", 183, 200, 1},
    {"world_quest_prairie_bird", 184, 201, 1},
    {"world_quest_rain_ohno", 185, 202, 1},
    {"world_quest_rain_shy", 186, 203, 1},
    {"world_quest_rain_cry", 187, 204, 1},
    {"world_quest_rain_sorry", 188, 205, 1},
    {"world_quest_rain_pout", 189, 206, 1},
    {"world_quest_rain_cold", 190, 207, 1},
    {"world_quest_rain_seek", 191, 208, 1},
    {"world_quest_rain_whale", 192, 209, 1},
    {"world_quest_sunset_strong", 193, 210, 1},
    {"world_quest_sunset_handstand", 194, 211, 1},
    {"world_quest_sunset_manta", 195, 212, 1},
    {"world_quest_sunset_proud", 196, 213, 1},
    {"world_quest_sunset_backflip", 197, 214, 1},
    {"world_quest_sunset_bow", 198, 215, 1},
    {"world_quest_sunset_cheer", 199, 216, 1},
    {"world_quest_dusk_salute", 200, 217, 1},
    {"world_quest_dusk_lookaround", 201, 218, 1},
    {"world_quest_dusk_die", 202, 219, 1},
    {"world_quest_dusk_brave", 203, 220, 1},
    {"world_quest_dusk_scared", 204, 221, 1},
    {"world_quest_dusk_sneaky", 205, 222, 1},
    {"world_quest_night_pray", 206, 223, 1},
    {"world_quest_night_force", 207, 224, 1},
    {"world_quest_night_float", 208, 225, 1},
    {"world_quest_night_love", 209, 226, 1},
    {"world_quest_night_ghost", 210, 227, 1},
    {"world_quest_prairie_dance", 211, 228, 1},
    {"world_quest_rain_kiss", 212, 229, 1},
    {"world_quest_sunset_juggle", 213, 230, 1},
    {"world_quest_dusk_respect", 214, 231, 1},
    {"world_quest_night_think", 215, 232, 1},
    {"world_quest_dawn_sass", 216, 233, 1},
    {"world_quest_prairie_salutation", 217, 234, 1},
    {"world_quest_rain_sarcastic", 218, 235, 1},
    {"world_quest_sunset_joy", 219, 236, 1},
    {"world_quest_dusk_acknowledge", 220, 237, 1},
    {"world_quest_night_kungfu", 221, 238, 1},
    {"world_quest_dawn_carry", 222, 239, 1},
    {"world_quest_prairie_doublefive", 223, 240, 1},
    {"world_quest_rain_lazycool", 224, 241, 1},
    {"world_quest_sunset_tripleaxel", 225, 242, 1},
    {"world_quest_dusk_crabvoice", 226, 243, 1},
    {"world_quest_night_shh", 227, 244, 1},
    {"world_quest_prairie_celebrate", 228, 245, 1},
    {"world_quest_dawn_loopdance", 229, 246, 1},
    {"world_quest_sunset_sparkler", 230, 247, 1},
    {"world_quest_night_wise", 231, 248, 1},
    {"world_quest_dusk_dontgo", 232, 249, 1},
    {"world_quest_rain_hairtousle", 233, 250, 1},
    {"world_quest_oasis_nod", 234, 251, 1},
    {"world_quest_oasis_doze", 235, 252, 1},
    {"world_quest_oasis_crabwalk", 236, 253, 1},
    {"world_quest_oasis_scare", 237, 254, 1},
    {"world_quest_oasis_shrug", 238, 255, 1},
    {"world_quest_oasis_playfight", 239, 256, 1},
    {"world_quest_prairie_jelly", 240, 257, 1},
    {"world_quest_prairie_bellyscratch", 241, 258, 1},
    {"world_quest_prairie_timid", 242, 259, 1},
    {"world_quest_prairie_grumpy", 243, 260, 1},
    {"world_quest_prairie_gratitude", 244, 261, 1},
    {"world_quest_prairie_rally", 245, 262, 1},
    {"world_quest_dawn_deepbreath", 246, 263, 1},
    {"world_quest_dawn_dustoff", 247, 264, 1},
    {"world_quest_dawn_chestpound", 248, 265, 1},
    {"world_quest_dawn_balance", 249, 266, 1},
    {"world_quest_sunset_village_peek", 250, 267, 1},
    {"world_quest_sunset_village_bearhug", 253, 270, 1},
    {"visit_prairie_cozycave", 254, 271, 1},
    {"visit_rain_grandmatable", 255, 272, 1},
    {"visit_rain_bonfirecamp", 256, 273, 1},
    {"visit_dusk_spitroast", 257, 274, 1},
    {"visit_sunset_labyrinth", 258, 275, 1},
    {"visit_sunset_hotspring", 259, 276, 60},
    {"visit_night_waterfall", 260, 277, 1},
    {"visit_skyway_speedtube", 261, 278, 1},
    {"visit_rainbowdeux_prairie", 268, 285, 1},
    {"visit_rainbowdeux_rain", 269, 286, 1},
    {"visit_rainbowdeux_sunset", 270, 287, 1},
    {"visit_rainbowdeux_dusk", 271, 288, 1},
    {"visit_rainbowdeux_night", 272, 289, 1},
    {"rainbow_rainbow_prairie_01", 273, 290, 60},
    {"rainbow_rainbow_rain_01", 274, 291, 60},
    {"rainbow_rainbow_sunset_01", 275, 292, 60},
    {"rainbow_rainbow_dusk_01", 276, 293, 60},
    {"rainbow_rainbow_night_01", 277, 294, 60},
    {"visit_stormy_event", 278, 295, 1},
    {"world_quest_nature_vortex1", 279, 323, 1},
    {"world_quest_nature_vortex2", 280, 324, 1},
    {"world_quest_nature_vortex3", 281, 325, 1},
    {"spirit_selfie_grumpy", 282, 327, 1},
    {"spirit_selfie_evillaugh", 283, 328, 1},
    {"spirit_selfie_crabwalk", 284, 329, 1},
    {"spirit_selfie_welcome", 285, 330, 1},
    {"spirit_selfie_crabvoice", 286, 331, 1},
    {"find_cafe_rolling", 287, 337, 1},
    {"find_cafe_flowers_1", 288, 337, 1},
    {"find_cafe_flowers_2", 289, 337, 1},
    {"find_cafe_sleeping_1", 290, 337, 1},
    {"find_cafe_sleeping_2", 291, 337, 1},
    {"find_cafe_sleeping_3", 292, 337, 1},
    {"craft_a_thing", 293, 1, 1},
    {"spirit_bookgift1", 294, 347, 1},
    {"spirit_bookgift2", 295, 347, 1},
    {"spirit_bookgift3", 296, 347, 1},
    {"do_competition_play", 297, 344, 1},
    {"spirit_musicianrace1", 298, 348, 1},
    {"spirit_musicianrace2", 299, 348, 1},
    {"spirit_musicianrace3", 300, 348, 1},
    {"spirit_stagehandmusician1", 301, 349, 1},
    {"spirit_stagehandmusician2", 302, 349, 1},
    {"spirit_stagehandmusician3", 303, 349, 1},
    {"spirit_forgottenstory1", 304, 350, 1},
    {"spirit_forgottenstory2", 305, 350, 1},
    {"spirit_forgottenstory3", 306, 350, 1},
    {"spirit_newchoreo1", 307, 351, 1},
    {"spirit_newchoreo2", 308, 351, 1},
    {"spirit_newchoreo3", 309, 351, 1},
    {"spirit_production1", 310, 352, 1},
    {"spirit_production2", 311, 352, 1},
    {"spirit_production3", 312, 352, 1},
    {"dostyle_yetipark", 313, 22, 1},
    {"dostyle_duskmid", 314, 23, 1},
    {"dostyle_prairie_nestandkeeper", 315, 85, 1},
    {"dostyle_rain", 316, 26, 1},
    {"dostyle_rainshelter", 317, 272, 1},
    {"dostyle_night2", 318, 29, 1},
    {"dostyle_prairie_island", 319, 125, 1},
    {"dostyle_prairie_wildlifepark", 320, 20, 1},
    {"dostyle_dusk_triangle", 321, 21, 1},
    {"dostyle_night_paintedworld", 322, 84, 1},
    {",daily_quest_change_hat", 323, 24, 1},
    {"change_horn", 324, 25, 1},
    {"change_neck", 325, 26, 1},
    {"change_feet", 326, 27, 1},
    {"change_prop", 327, 28, 1},
    {"dostyle_rain_2", 328, 28, 1},
    {"dostyle_night2_2", 329, 78, 1},
    {"dostyle_prairie_island_2", 330, 76, 1},
    {"dostyle_dusk_triangle_2", 331, 30, 1},
    {"runway_recording_shrine_walk", 332, 29, 1},
    {"runway_recording_shrine_view", 333, 30, 1},
    {"play_tot_sport_crabrace", 334, 344, 1},
    {"play_tot_sport_rainrace", 335, 344, 1},
    {"play_tot_sport_wingsuiting", 336, 344, 1},
    {"play_tot_sport_speedskate", 337, 344, 1},
    {"play_tot_sport_voidcollect", 338, 344, 1},
    {"play_tot_sport_cloudcollect", 339, 344, 1},
    {"spirit_scouting1", 340, 353, 1},
    {"spirit_scouting2", 341, 353, 1},
    {"spirit_scouting3", 342, 353, 1},
    {"spirit_boatrepair1", 343, 354, 1},
    {"spirit_boatrepair2", 344, 354, 1},
    {"spirit_boatrepair3", 345, 354, 1},
    {"spirit_beachday1", 346, 355, 1},
    {"spirit_beachday2", 347, 355, 1},
    {"spirit_beachday3", 348, 355, 1},
    {"spirit_catapult1", 349, 356, 1},
    {"spirit_catapult2", 350, 356, 1},
    {"spirit_catapult3", 351, 356, 1},
    {"spirit_angler1", 352, 357, 1},
    {"spirit_angler2", 353, 357, 1},
    {"spirit_angler3", 354, 357, 1},
    {"spirit_discoverark1", 355, 358, 1},
    {"spirit_discoverark2", 356, 358, 1},
    {"spirit_discoverark3", 357, 358, 1},
    {"spirit_hidenseek1", 358, 359, 1},
    {"spirit_hidenseek2", 360, 359, 1},
    {"spirit_hidenseek3", 360, 359, 1},
    {"spirit_helpinghand1", 361, 360, 1},
    {"spirit_helpinghand2", 362, 360, 1},
    {"spirit_helpinghand3", 363, 360, 1},
    {"spirit_umbrella1", 364, 361, 1},
    {"spirit_umbrella2", 365, 361, 1},
    {"spirit_umbrella3", 366, 361, 1},
    {"spirit_grieving1", 367, 362, 1},
    {"spirit_grieving2", 368, 362, 1},
    {"spirit_grieving3", 369, 362, 1},
    {"spirit_grieving4", 370, 362, 1},
    {"spirit_grieving5", 371, 362, 1},
    {"spirit_rockcollection1", 372, 363, 1},
    {"spirit_rockcollection2", 373, 363, 1},
    {"spirit_rockcollection3", 374, 363, 1},
    {"spirit_camping1", 375, 364, 1},
    {"spirit_camping2", 376, 364, 1},
    {"spirit_camping3", 377, 364, 1},
    {"spirit_yoga1", 378, 365, 1},
    {"spirit_yoga2", 379, 365, 1},
    {"spirit_yoga3", 380, 365, 1},
    {"spirit_wildlife1", 381, 366, 1},
    {"spirit_wildlife2", 382, 366, 1},
    {"spirit_wildlife3", 383, 366, 1},
    {"spirit_exploresanctuary1", 384, 367, 1},
    {"spirit_exploresanctuary2", 385, 367, 1},
    {"spirit_exploresanctuary3", 386, 367, 1},
    {"domusic_location1_1", 387, 84, 1},
    {"domusic_location1_2", 388, 76, 1},
    {"domusic_location2_1", 389, 130, 1},
    {"domusic_location2_2", 390, 3, 1},
    {"domusic_location3_1", 391, 124, 20},
    {"domusic_location3_2", 392, 82, 1},
    {"domusic_location4_1", 393, 87, 1},
    {"domusic_location4_2", 394, 88, 1},
    {"domusic_location5_1", 395, 130, 1},
    {"domusic_location5_2", 396, 39, 1},
    {"domusic_location5_3", 397, 124, 20},
    {"domusic_location6_1", 398, 85, 1},
    {"domusic_location6_2", 399, 82, 1},
    {"domusic_location6_3", 400, 84, 1},
    {"spirit_plead1", 401, 368, 1},
    {"spirit_plead2", 402, 368, 1},
    {"spirit_plead3", 403, 368, 1},
    {"dotreasure_night_1", 404, 369, 1},
    {"dotreasure_day_1", 405, 369, 1},
    {"dotreasure_rain_1", 406, 369, 1},
    {"dotreasure_sunset_1", 407, 369, 1},
    {"dotreasure_dusk_1", 408, 369, 1},
    {"dotreasure_night_2", 409, 369, 1},
    {"dotreasure_day_2", 410, 369, 1},
    {"dotreasure_rain_2", 411, 369, 1},
    {"dotreasure_sunset_2", 412, 369, 1},
    {"dotreasure_dusk_2", 413, 369, 1},
    {"dotreasure_night_3", 414, 369, 1},
    {"dotreasure_day_3", 415, 369, 1},
    {"dotreasure_rain_3", 416, 369, 1},
    {"dotreasure_sunset_3", 417, 369, 1},
    {"spirit_chimesmithbuilder1", 418, 373, 1},
    {"spirit_chimesmithbuilder2", 419, 373, 1},
    {"spirit_chimesmithbuilder3", 420, 373, 1},
    {"spirit_navigator1", 421, 374, 1},
    {"spirit_navigator2", 422, 374, 1},
    {"spirit_navigator3", 423, 374, 1},
    {"bluebirdfeather_1", 424, 375, 1},
    {"bluebirdfeather_2", 425, 375, 1},
    {"bluebirdfeather_3", 426, 375, 1},
    {"bluebirdfeather_4", 427, 375, 1},
    {"bluebirdchase_3", 431, 376, 1},
    {"bluebirdchase_4", 431, 376, 1},
    {"spirit_duets1", 432, 378, 1},
    {"spirit_flightguide1", 433, 379, 60},
    {"spirit_flightguide2", 434, 379, 1},
    {"spirit_flightguide3", 435, 379, 1},
    {"spirit_rhythm1", 436, 380, 30},
    {"spirit_rhythm2", 437, 380, 1},
    {"spirit_rhythm3", 438, 380, 1},
    {"spirit_rhythm4", 439, 380, 1},
    {"spirit_rhythm5", 440, 380, 1},
    {"spirit_rhythm6", 441, 380, 1},
    {"spirit_anniversary", 442, 381, 60},
    {"spirit_soundbath1", 443, 382, 1},
    {"spirit_soundbath2", 444, 382, 1},
    {"spirit_soundbath3", 445, 382, 60},
    {"spirit_passage1", 446, 383, 1},
    {"spirit_passage2", 447, 383, 1},
    {"spirit_passage3", 448, 383, 1},
    {"spirit_ugc1", 449, 384, 1},
    {"spirit_ugc2", 450, 384, 30},
    {"spirit_ugc3", 451, 384, 30},
    {"spirit_ugc4", 452, 384, 30},
    {"spirit_ugc5", 453, 384, 30},
    {"pick_up_1_crab", 454, 385, 1},
    {"sunlight_photo_daily", 455, 386, 1},
    {"emote_with_players", 456, 388, 6},
    {"lightseeker3_rain_rainforest", 457, 147, 3},
    {"lightseeker3_day_prairievillage", 458, 146, 3},
    {"lightseeker3_night_floor4", 459, 150, 3},
    {"lightseeker3_sunset_race", 460, 148, 3},
    {"lightseeker3_dusk_graveyard", 461, 149, 3},
    {"lightseeker3_rain_basecamp", 462, 147, 3},
    {"lightseeker3_night_shelter", 463, 150, 3},
    {"lightseeker3_day_birdnest", 464, 146, 3},
    {"lightseeker3_dusk_triangle", 465, 149, 3},
    {"lightseeker3_sunset_citadel", 466, 148, 3},
    {"spirit_crabvoice1", 468, 389, 1},
    {"spirit_crabvoice2", 469, 389, 1},
    {"spirit_crabvoice3", 470, 389, 1},
    {"ride_giant_manta_prairie_island", 471, 390, 30},
    {"touch_butterflies_butterflyfields", 472, 391, 100},
    {"catch_pumpkin_crab", 473, 393, 5},
    {"light_mischief_cannon", 474, 394, 3},
    {"light_mischief_cauldron", 475, 395, 1},
    {"mischief_broomrace", 476, 396, 1},
    {"tidy_rain_grandmatable", 477, 397, 2},
    {"lightseeker3_rain_skyway", 478, 147, 3},
    {"lightseeker3_sunset_yetipark", 479, 148, 3},
    {"feast_play_skyball", 480, 398, 60},
    {"feast_play_race", 481, 399, 1},
    {"feast_fishing", 482, 400, 1},
    {"feast_snowball_hit_avatar", 483, 401, 1},
    {"honk_at_players", 484, 402, 5},
};

using Feature1020Pattern = std::array<std::uint8_t, 48>;

constexpr Feature1020Pattern kFeature1020Patterns[] = {
    { // 1: .WindSparticles / 粒子
        0x2E, 0x57, 0x69, 0x6E, 0x64, 0x53, 0x70, 0x61, 0x72, 0x74, 0x69, 0x63, 0x6C, 0x65, 0x73, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0xE7, 0xB2, 0x92, 0xE5, 0xAD, 0x90, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 2: .FireworkSmall / 水珠
        0x2E, 0x46, 0x69, 0x72, 0x65, 0x77, 0x6F, 0x72, 0x6B, 0x53, 0x6D, 0x61, 0x6C, 0x6C, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE6, 0xB0, 0xB4, 0xE7, 0x8F, 0xA0, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x2E, 0x46, 0x69, 0x72, 0x65, 0x77, 0x6F, 0x72,
    },
    { // 3: .FireworkExplode / 烟花爆炸
        0x2E, 0x46, 0x69, 0x72, 0x65, 0x77, 0x6F, 0x72, 0x6B, 0x45, 0x78, 0x70, 0x6C, 0x6F, 0x64, 0x65,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0xE7, 0x83, 0x9F, 0xE8, 0x8A, 0xB1, 0xE7, 0x88, 0x86, 0xE7, 0x82, 0xB8, 0x00, 0x00, 0x00, 0x00,
    },
    { // 4: .Firework / 暗黑红
        0x2E, 0x46, 0x69, 0x72, 0x65, 0x77, 0x6F, 0x72, 0x6B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE6, 0x9A, 0x97, 0xE9, 0xBB, 0x91, 0xE7, 0xBA,
        0xA2, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 5: .FireworkTrail / 暗黑红2
        0x2E, 0x46, 0x69, 0x72, 0x65, 0x77, 0x6F, 0x72, 0x6B, 0x54, 0x72, 0x61, 0x69, 0x6C, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0xE6, 0x9A, 0x97, 0xE9, 0xBB, 0x91, 0xE7, 0xBA, 0xA2, 0x32, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 6: .FireworkRing / 暗黑白
        0x2E, 0x46, 0x69, 0x72, 0x65, 0x77, 0x6F, 0x72, 0x6B, 0x52, 0x69, 0x6E, 0x67, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE6, 0x9A, 0x97, 0xE9, 0xBB, 0x91, 0xE7, 0x99,
        0xBD, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 7: .FireworkSpark / 暗黑白2
        0x2E, 0x46, 0x69, 0x72, 0x65, 0x77, 0x6F, 0x72, 0x6B, 0x53, 0x70, 0x61, 0x72, 0x6B, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE6, 0x9A, 0x97, 0xE9, 0xBB, 0x91, 0xE7, 0x99,
        0xBD, 0x32, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 8: .WindSparklesSm / 粒子掉落
        0x2E, 0x57, 0x69, 0x6E, 0x64, 0x53, 0x70, 0x61, 0x72, 0x6B, 0x6C, 0x65, 0x73, 0x53, 0x6D, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0xE7, 0xB2, 0x92, 0xE5, 0xAD, 0x90, 0xE6, 0x8E, 0x89, 0xE8, 0x90, 0xBD, 0x00, 0x00, 0x00, 0x00,
    },
    { // 9: .WindWindSm / 粒子集聚
        0x2E, 0x57, 0x69, 0x6E, 0x64, 0x57, 0x69, 0x6E, 0x64, 0x53, 0x6D, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE7, 0xB2, 0x92, 0xE5, 0xAD, 0x90, 0xE9, 0x9B,
        0x86, 0xE8, 0x81, 0x9A, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 10: .DarkExplode / 暗黑爆炸
        0x2E, 0x44, 0x61, 0x72, 0x6B, 0x45, 0x78, 0x70, 0x6C, 0x6F, 0x64, 0x65, 0x00, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE9, 0xBB, 0x91, 0xE6, 0x9A, 0x97, 0xE7, 0x88,
        0x86, 0xE7, 0x82, 0xB8, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 11: .TornadoDebris / 龙卷碎屑
        0x2E, 0x54, 0x6F, 0x72, 0x6E, 0x61, 0x64, 0x6F, 0x44, 0x65, 0x62, 0x72, 0x69, 0x73, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE9, 0xBE, 0x99, 0xE5, 0x8D, 0xB7, 0xE7, 0xA2,
        0x8E, 0xE5, 0xB1, 0x91, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 12: .StormDebris / 风暴碎屑
        0x2E, 0x53, 0x74, 0x6F, 0x72, 0x6D, 0x44, 0x65, 0x62, 0x72, 0x69, 0x73, 0x00, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE9, 0xA3, 0x8E, 0xE6, 0x9A, 0xB4, 0xE7, 0xA2,
        0x8E, 0xE5, 0xB1, 0x91, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 13: .TornadoDust / 龙卷尘
        0x2E, 0x54, 0x6F, 0x72, 0x6E, 0x61, 0x64, 0x6F, 0x44, 0x75, 0x73, 0x74, 0x00, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE9, 0xBE, 0x99, 0xE5, 0x8D, 0xB7, 0xE5, 0xB0,
        0x98, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 14: .N_crabMesh03 / 螃蟹图案
        0x2E, 0x4E, 0x5F, 0x63, 0x72, 0x61, 0x62, 0x4D, 0x65, 0x73, 0x68, 0x30, 0x33, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE8, 0x9E, 0x83, 0xE8, 0x9F, 0xB9, 0xE5, 0x9B,
        0xBE, 0xE6, 0xA1, 0x88, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 15: .N_mantaMesh / 遥鲲图案
        0x2E, 0x4E, 0x5F, 0x6D, 0x61, 0x6E, 0x74, 0x61, 0x4D, 0x65, 0x73, 0x68, 0x00, 0x00, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE9, 0x81, 0xA5, 0xE9, 0xB2, 0xB2, 0xE5, 0x9B,
        0xBE, 0xE6, 0xA1, 0x88, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 16: .N_pendantAP16 / 欧若拉图案
        0x2E, 0x4E, 0x5F, 0x70, 0x65, 0x6E, 0x64, 0x61, 0x6E, 0x74, 0x41, 0x50, 0x31, 0x36, 0x00, 0x00,
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xE6, 0xAC, 0xA7, 0xE8, 0x8B, 0xA5, 0xE6, 0x8B,
        0x89, 0xE5, 0x9B, 0xBE, 0xE6, 0xA1, 0x88, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
    { // 17: .StewardScepter / 烟火节图案
        0x2E, 0x53, 0x74, 0x65, 0x77, 0x61, 0x72, 0x64, 0x53, 0x63, 0x65, 0x70, 0x74, 0x65, 0x72, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0xE7, 0x83, 0x9F, 0xE7, 0x81, 0xAB, 0xE8, 0x8A, 0x82, 0xE5, 0x9B, 0xBE, 0xE6, 0xA1, 0x88, 0x00,
    },
    { // 18: UILogo, wrapper-visible but not listed by the recovered 1020 feature string.
        0x55, 0x49, 0x4C, 0x6F, 0x67, 0x6F, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0xE9, 0xBB, 0x98, 0xE8, 0xAE, 0xA4, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    },
};

static_assert(sizeof(kFeature1020Patterns) / sizeof(kFeature1020Patterns[0]) == 18,
              "feature 1020 pattern table size mismatch");

constexpr PointerChainIntWriteCase kPointerChainIntWriteCases[] = {
    {607, 0x1C4B78, kFeature608CloudChain, 4, 0x0, 0x10},
    {608, 0x1C4BC0, kFeature608CloudChain, 4, 0x4, 0x10},
    {609, 0x1C1B20, kFeature608CloudChain, 4, 0x8, 0x10},
    {610, 0x1C4C58, kFeature608CloudChain, 4, 0xC, 0x10},
};

StateOnlyCase kStateOnlyCases[] = {
    {430, 0x1C3D38, 0x37BC08, &g_feature_430_wing_break_count},
    {432, 0x1C4B20, 0x37BB80, &g_feature_432_heart_radius},
    {433, 0x1C4E4C, 0x37BB88, &g_feature_433_loop_count},
    {434, 0x1C5404, 0x37BB90, &g_feature_434_heart_height},
    {437, 0x1C1540, 0x37BB98, &g_feature_437_creature_height},
    {438, 0x1C47D8, 0x37BBA0, &g_feature_438_creature_radius},
    {439, 0x1C22B8, 0x37BBA8, &g_feature_439_creature_density},
    {440, 0x1C4E20, 0x37BBB0, &g_feature_440_loop_seconds},
    {503, 0x1C3108, 0x3855B8, &g_feature_503_height_input},
    {504, 0x1C4A00, 0x3855C0, &g_feature_504_body_shape_input},
    {926, 0x1C3CF8, 0x37BBD0, &g_feature_926_candle_count},
    {928, 0x1C1B10, 0x37BBD8, &g_feature_928_firework_count},
    {952, 0x1C2DA8, 0x37BBE0, &g_feature_952_firework_count},
    {1001, 0x1C30F8, 0x385578, &g_feature_1001_red},
    {1002, 0x1C25B4, 0x37BBF0, &g_feature_1002_green},
    {1003, 0x1C350C, 0x385580, &g_feature_1003_blue},
    {1004, 0x1C2C38, 0x37BBF8, &g_feature_1004_spawn_count},
};

BoolStateOnlyCase kBoolStateOnlyCases[] = {
    {405, 0x1C1420, 0x3854DC, &g_feature_405_anniversary_hat},
    {429, 0x1C476C, 0x3854B0, &g_feature_429_absorb_wing},
    {441, 0x1C4FA0, 0x3854B4, &g_feature_441_delay_place},
};

constexpr std::uint32_t kFeature510MagicIds[] = {
    0x015EB6C6, 0x39D034A7,
};

constexpr std::uint32_t kFeature511MagicIds[] = {
    0x397F873D, 0x2EC8F8A9, 0x6642321A, 0x6AF7BE6D, 0xCA420D14, 0x1C50C75F,
    0x333289CD, 0xF2F831E1, 0xCD64F9CF, 0xC80E6DB7, 0xF63EDE86,
};

constexpr std::uint32_t kFeature512MagicIds[] = {
    0x007F979E, 0x73DF059A, 0x139FBC68,
};

constexpr std::uint32_t kFeature513MagicIds[] = {
    0x0E47C40F, 0x1901A209, 0xFCCB35A0, 0x3E60A5CC, 0x763DB4A8, 0x7CCC0191,
    0x3EBA9F1F, 0xFC81B36D, 0x03BFD773, 0x226CDB6B, 0xCEEDF51E, 0x06F464B7,
};

constexpr std::uint32_t kFeature514MagicIds[] = {
    0x42FC7C88, 0x71D84208, 0x51D3DA6D, 0x20D87145, 0xEB46562C,
    0x7A31F5C3, 0xFB7B9827, 0x35BBDFE5, 0xF5CC7598, 0xCD694281,
};

constexpr std::uint32_t kFeature515MagicIds[] = {
    0x25499C64, 0x7BF429D7, 0x51FD8DCC, 0x13A6977D, 0x203CCFF1, 0x3771C92A,
    0x4DE24B0E, 0x09A8EEEA, 0x22C4AFA4, 0x53947DA7, 0x55A11C7C, 0x13B56550,
    0x2DE3A270, 0x69129CF4, 0x222AE72F, 0xC7F10696, 0x380324D3, 0x511C1D20,
    0xCED5E54E, 0xD3BB542D, 0xE91F08AD, 0xD6464F0A, 0xD1386D1B, 0xFFA3C74F,
    0xE5719E42, 0xED95ECED,
};

constexpr std::uint32_t kFeature516MagicIds[] = {
    0x3F6C9373, 0x121CCD93, 0x0F5059A2, 0x00997AA9, 0x5F4C7323, 0x1B1CAE2E,
    0x077C43AD, 0x4EA92DDD, 0x098A84C6, 0x55DB5E0D, 0x4065AD34, 0x4ACDCCAD,
    0x13D9224D, 0x082E8D38, 0x45950486, 0x0D8806B3, 0x53A8FE85, 0x60AD907C,
    0xC9928244, 0xEA94CF7A, 0x1B1CAE2E, 0x4C064D60, 0x39D69525, 0xD0743EC1,
};

constexpr std::uint32_t kFeature517MagicIds[] = {
    0x08C34966, 0x63452685, 0x4D902A2E, 0x4E937BCA, 0xF574AD67, 0x8BA9DFF8,
    0x7256CA83, 0x2601B6BF, 0xA4F6FF4B, 0x49BF726E, 0x1C09A1D8, 0xC028AC16,
    0xB2395FA3, 0xAF45C894,
};

constexpr std::uintptr_t kFeature510SlotOffset = ~static_cast<std::uintptr_t>(0);

constexpr MagicSlotCase kMagicSlotCases[] = {
    {510, 0x1C412C, 0x37C2C0, kFeature510SlotOffset, kFeature510MagicIds, sizeof(kFeature510MagicIds) / sizeof(kFeature510MagicIds[0])},
    {511, 0x1C2724, 0x364680, 0x38, kFeature511MagicIds, sizeof(kFeature511MagicIds) / sizeof(kFeature511MagicIds[0])},
    {512, 0x1C467C, 0x364688, 0x70, kFeature512MagicIds, sizeof(kFeature512MagicIds) / sizeof(kFeature512MagicIds[0])},
    {513, 0x1C401C, 0x364690, 0xA8, kFeature513MagicIds, sizeof(kFeature513MagicIds) / sizeof(kFeature513MagicIds[0])},
    {514, 0x1C1BEC, 0x364698, 0xE0, kFeature514MagicIds, sizeof(kFeature514MagicIds) / sizeof(kFeature514MagicIds[0])},
    {515, 0x1C1CB8, 0x3646A0, 0x118, kFeature515MagicIds, sizeof(kFeature515MagicIds) / sizeof(kFeature515MagicIds[0])},
    {516, 0x1C40D4, 0x3646A8, 0x150, kFeature516MagicIds, sizeof(kFeature516MagicIds) / sizeof(kFeature516MagicIds[0])},
    {517, 0x1C4780, 0x3646B0, 0x188, kFeature517MagicIds, sizeof(kFeature517MagicIds) / sizeof(kFeature517MagicIds[0])},
};

constexpr SwitchCaseTarget kHighValueSwitchCases[] = {
    {8001, 0x1C0500},
    {8002, 0x1C0914},
    {8003, 0x1C0884},
    {8004, 0x1C07AC},
    {8005, 0x1CC564},
    {8006, 0x1C0764},
    {8007, 0x1C083C},
    {8008, 0x1C07F4},
    {8009, 0x1C06D4},
    {8010, 0x1C095C},
    {8011, 0x1C0644},
    {8012, 0x1C068C},
    {8013, 0x1C071C},
    {8014, 0x1C08CC},
};

constexpr ScenePatchRange kScenePatchRanges[] = {
    {201, 1, 13, 0x1C4CAC},
    {202, 14, 6, 0x1C1B00},
    {203, 20, 9, 0x1C2798},
    {204, 29, 9, 0x1C0D00},
    {205, 38, 17, 0x1C4E10},
    {206, 55, 13, 0x1C4DC0},
    {207, 68, 18, 0x1C4F90},
    {208, 86, 7, 0x1C4F10},
};

constexpr Feature803ColorPreset kFeature803ColorPresets[] = {
    {0, "Black", 6, {{'\n', 'B', 'l', 'a', 'c', 'k', 0, 0}}, 0x1C4B5C},
    {1, "White", 6, {{'\n', 'W', 'h', 'i', 't', 'e', 0, 0}}, 0x1C695C},
    {2, "Green", 6, {{'\n', 'G', 'r', 'e', 'e', 'n', 0, 0}}, 0x1C6910},
    {3, "Rainbow", 8, {{'\n', 'R', 'a', 'i', 'n', 'b', 'o', 'w'}}, 0x1C692C},
};

constexpr ScenePatchRange kPatchPayloadWriteRanges[] = {
    {804, 1, 13, 0x1C4AA0},
    {805, 14, 6, 0x1C4804},
    {806, 20, 9, 0x1C1E20},
    {807, 29, 9, 0x1C1770},
    {808, 38, 17, 0x1C2200},
    {809, 55, 13, 0x1C4CD8},
    {810, 68, 18, 0x1C2334},
    {811, 86, 7, 0x1C1D2C},
};

constexpr PortalRange kPortalRanges[] = {
    {936, 1, 13, 0x1C2164},
    {937, 14, 6, 0x1C2138},
    {938, 20, 9, 0x1C22A8},
    {939, 29, 9, 0x1C25E0},
    {940, 38, 17, 0x1C3BC8},
    {941, 55, 13, 0x1C3274},
    {942, 68, 18, 0x1C3BA0},
    {943, 86, 7, 0x1C4670},
};

constexpr DirectLevelRange kDirectLevelRanges[] = {
    {813, 1, 13, 0x1C2C28},
    {814, 14, 6, 0x1C3134},
    {815, 20, 9, 0x1C34D4},
    {816, 29, 9, 0x1C344C},
    {817, 38, 17, 0x1C2C00},
    {819, 55, 13, 0x1C4370},
    {820, 68, 18, 0x1C27C4},
    {821, 86, 7, 0x1C427C},
};

constexpr DirectIndexedCommandCase kDirectIndexedCommandCases[] = {
    {950, 18, 0x1C2280},
    {951, 19, 0x1C473C},
    {970, 39, 0x1C3424},
    {971, 9, 0x1C2940},
    {991, 17, 0x1C3280},
};

constexpr int kIndexedCommands922[] = {
    1, 2, 3, 4, 5, 6, 9, 10, 11, 13, 14, 15,
};

constexpr int kIndexedCommands992[] = {
    21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
};

constexpr int kIndexedCommands957[] = {
    32, 33, 34, 35, 48, 36, 49,
};

constexpr IndexedCommandRange kIndexedCommandRanges[] = {
    {922, kIndexedCommands922, static_cast<int>(sizeof(kIndexedCommands922) / sizeof(kIndexedCommands922[0])), 0x1C2930},
    {992, kIndexedCommands992, static_cast<int>(sizeof(kIndexedCommands992) / sizeof(kIndexedCommands992[0])), 0x1C2308},
    {957, kIndexedCommands957, static_cast<int>(sizeof(kIndexedCommands957) / sizeof(kIndexedCommands957[0])), 0x1C34FC},
};

constexpr const char *kPortalDisplayNames[] = {
    "遇境", "马里奥遇境", "遇境《归巢季》", "魔法商店《归巢季》", "服装商店《归巢季》",
    "开图动画《归巢季》", "大屏幕影院", "筑巢空间", "筑巢向导", "咖啡馆", "冥想之地",
    "爱丽丝仙境茶会", "《二重奏季》",
    "晨岛", "试炼大厅《预言季》", "水之试炼《预言季》", "土之试炼《预言季》",
    "风之试炼《预言季》", "火之试炼《预言季》",
    "云野一图", "云野二图", "云野左图", "云野右图", "云野八人门", "云野宫殿",
    "云野《破碎季》", "云野《圣岛季》", "云野峻岭《时光季》",
    "雨林一图", "雨林二图", "雨林副本", "雨林副本隐藏图", "雨林水母图", "雨林宫殿",
    "雨林大树屋《集结季》", "风行网道《飞翔季》", "青鸟剧场《青鸟季》",
    "霞谷一图", "霞光城", "霞谷飞行赛道", "霞谷滑雪赛道", "赛道二段", "霞谷终点",
    "霞谷宫殿", "音乐商店", "圆梦村《梦想季》", "隐士公园《梦想季》",
    "乡村剧场《表演季》", "蝴蝶《欧诺拉季》", "鱼《欧诺拉季》", "鸟《欧诺拉季》",
    "鲲《欧诺拉季》", "水母《欧诺拉季》",
    "墓土一图", "墓土二图", "墓土五龙图", "墓土沉船", "墓土古战场", "墓土宫殿",
    "墓土方舟《魔法季》", "藏宝岛礁《深海季》", "水底洞穴《深海季》", "市场《双星季》",
    "隐秘寺庙《双星季》", "曾经的暮土《双星季》", "曾经的暮土神殿《双星季》",
    "禁阁低层", "禁阁副本", "禁阁高层", "禁阁终点", "TGC办公室", "三季传送图",
    "万圣乐园《万圣节》", "星光沙漠《小王子季》", "沉船海滩《小王子季》",
    "瓶子洞穴《小王子季》", "星漠《小王子季》", "小王子星球《小王子季》",
    "庇护所《追忆季》", "《九色鹿季》", "《姆明季》", "姆明季向导《姆明季》",
    "姆明山谷森林《姆明季》", "姆明山谷房子《姆明季》",
    "暴风一图", "暴风二图", "伊甸", "重生一图", "重生二图", "星光大道", "结尾动画",
};

const SwitchCaseTarget *findHighValueSwitchCase(int feature_num) {
    for (const SwitchCaseTarget &item : kHighValueSwitchCases) {
        if (item.case_id == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const ScenePatchRange *findScenePatchRange(int feature_num) {
    for (const ScenePatchRange &range : kScenePatchRanges) {
        if (range.feature_num == feature_num) {
            return &range;
        }
    }
    return nullptr;
}

const Feature803ColorPreset *findFeature803ColorPreset(int item) {
    for (const Feature803ColorPreset &preset : kFeature803ColorPresets) {
        if (preset.item == item) {
            return &preset;
        }
    }
    return nullptr;
}

const ScenePatchRange *findPatchPayloadWriteRange(int feature_num) {
    for (const ScenePatchRange &range : kPatchPayloadWriteRanges) {
        if (range.feature_num == feature_num) {
            return &range;
        }
    }
    return nullptr;
}

const PortalRange *findPortalRange(int feature_num) {
    for (const PortalRange &range : kPortalRanges) {
        if (range.feature_num == feature_num) {
            return &range;
        }
    }
    return nullptr;
}

const DirectLevelRange *findDirectLevelRange(int feature_num) {
    for (const DirectLevelRange &range : kDirectLevelRanges) {
        if (range.feature_num == feature_num) {
            return &range;
        }
    }
    return nullptr;
}

const DirectIndexedCommandCase *findDirectIndexedCommandCase(int feature_num) {
    for (const DirectIndexedCommandCase &item : kDirectIndexedCommandCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const IndexedCommandRange *findIndexedCommandRange(int feature_num) {
    for (const IndexedCommandRange &range : kIndexedCommandRanges) {
        if (range.feature_num == feature_num) {
            return &range;
        }
    }
    return nullptr;
}

const BasicTogglePatchCase *findBasicTogglePatchCase(int feature_num) {
    for (const BasicTogglePatchCase &item : kBasicTogglePatchCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const BootloaderFloatSeekbarCase *findBootloaderFloatSeekbarCase(int feature_num) {
    for (const BootloaderFloatSeekbarCase &item : kBootloaderFloatSeekbarCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const BootloaderScaledIntCase *findBootloaderScaledIntCase(int feature_num) {
    for (const BootloaderScaledIntCase &item : kBootloaderScaledIntCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const BootloaderDirectIntCase *findBootloaderDirectIntCase(int feature_num) {
    for (const BootloaderDirectIntCase &item : kBootloaderDirectIntCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const BootloaderBoolProcIntCase *findBootloaderBoolProcIntCase(int feature_num) {
    for (const BootloaderBoolProcIntCase &item : kBootloaderBoolProcIntCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const PointerChainIntWriteCase *findPointerChainIntWriteCase(int feature_num) {
    for (const PointerChainIntWriteCase &item : kPointerChainIntWriteCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const StateOnlyCase *findStateOnlyCase(int feature_num) {
    for (const StateOnlyCase &item : kStateOnlyCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const BoolStateOnlyCase *findBoolStateOnlyCase(int feature_num) {
    for (const BoolStateOnlyCase &item : kBoolStateOnlyCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const MagicSlotCase *findMagicSlotCase(int feature_num) {
    for (const MagicSlotCase &item : kMagicSlotCases) {
        if (item.feature_num == feature_num) {
            return &item;
        }
    }
    return nullptr;
}

const Feature701704Range *findFeature701704Range(int feature_num) {
    for (const Feature701704Range &range : kFeature701704Ranges) {
        if (range.feature_num == feature_num) {
            return &range;
        }
    }
    return nullptr;
}

const char *portalDisplayNameForPatchIndex(int patch_index) {
    if (patch_index < 1 || patch_index > static_cast<int>(sizeof(kPortalDisplayNames) / sizeof(kPortalDisplayNames[0]))) {
        return nullptr;
    }
    return kPortalDisplayNames[patch_index - 1];
}

bool writeU32Raw(std::uintptr_t address, std::uint32_t value) {
    return writeBytes(address, &value, sizeof(value));
}

bool readU64Raw(std::uintptr_t address, std::uint64_t *out) {
    return out && readTyped(address, TYPE_QWORD, out, sizeof(*out));
}

bool readU32Raw(std::uintptr_t address, std::uint32_t *out) {
    return out && readTyped(address, TYPE_DWORD, out, sizeof(*out));
}

bool readFloatRaw(std::uintptr_t address, float *out) {
    return out && readTyped(address, TYPE_FLOAT, out, sizeof(*out));
}

bool readBytesRaw(std::uintptr_t address, void *out, std::size_t size) {
    if (!out || size == 0) {
        return false;
    }
    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }
    if (pid <= 0) {
        return false;
    }

    char path[64];
    std::snprintf(path, sizeof(path), "/proc/%d/mem", pid);
    int fd = open(path, O_RDONLY);
    if (fd < 1) {
        if (fd >= 0) {
            close(fd);
        }
        return false;
    }
    const ssize_t read_count = pread64(fd, out, size, static_cast<off64_t>(address));
    close(fd);
    return read_count == static_cast<ssize_t>(size);
}

bool writeFloatRaw(std::uintptr_t address, float value) {
    return writeBytes(address, &value, sizeof(value));
}

bool writeU64Raw(std::uintptr_t address, std::uint64_t value) {
    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }
    if (pid <= 0) {
        return false;
    }

    char path[64];
    std::snprintf(path, sizeof(path), "/proc/%d/mem", pid);
    int fd = open(path, O_WRONLY);
    if (fd < 1) {
        if (fd >= 0) {
            close(fd);
        }
        return false;
    }

    const ssize_t write_count =
        pwrite64(fd, &value, sizeof(value), static_cast<off64_t>(address));
    close(fd);
    return write_count == static_cast<ssize_t>(sizeof(value));
}

bool writeOriginalProcTypedInt(std::uintptr_t address, int value, int proc_type) {
    switch (proc_type) {
        case 1: {
            const std::int8_t typed = static_cast<std::int8_t>(value);
            return writeBytes(address, &typed, sizeof(typed));
        }
        case 4: {
            const std::int32_t typed = static_cast<std::int32_t>(value);
            return writeBytes(address, &typed, sizeof(typed));
        }
        case 0x10: {
            return writeFloatRaw(address, static_cast<float>(value));
        }
        case 0x20: {
            const std::int64_t typed = static_cast<std::int64_t>(value);
            return writeBytes(address, &typed, sizeof(typed));
        }
        case 0x40: {
            const double typed = static_cast<double>(value);
            return writeBytes(address, &typed, sizeof(typed));
        }
        default:
            return false;
    }
}

bool writeOriginalProcType32Int64(std::uintptr_t address, std::int64_t value) {
    return writeBytes(address, &value, sizeof(value));
}

bool writeOriginalProcTypedFloat(std::uintptr_t address, float value, int proc_type = 0x10) {
    if (proc_type != 0x10) {
        return false;
    }
    return writeFloatRaw(address, value);
}

bool writeOriginalProcType16Text(std::uintptr_t address, const char *text) {
    return text != nullptr && writeValue(text, address, TYPE_FLOAT);
}

void showHtmlToast(JNIEnv *env, jobject context, const std::string &html, bool long_duration) {
    if (!env || !context) {
        return;
    }

    jclass html_cls = findClass(env, "android/text/Html");
    jclass toast_cls = findClass(env, "android/widget/Toast");
    if (!html_cls || !toast_cls) {
        if (html_cls) {
            env->DeleteLocalRef(html_cls);
        }
        if (toast_cls) {
            env->DeleteLocalRef(toast_cls);
        }
        showToast(env, context, html, long_duration);
        return;
    }

    jmethodID from_html = getStaticMethod(
        env,
        html_cls,
        "fromHtml",
        "(Ljava/lang/String;)Landroid/text/Spanned;");
    jmethodID make_text = getStaticMethod(
        env,
        toast_cls,
        "makeText",
        "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    jmethodID show = getMethod(env, toast_cls, "show", "()V");
    if (!from_html || !make_text || !show) {
        env->DeleteLocalRef(html_cls);
        env->DeleteLocalRef(toast_cls);
        showToast(env, context, html, long_duration);
        return;
    }

    jstring html_text = env->NewStringUTF(html.c_str());
    jobject spanned = env->CallStaticObjectMethod(html_cls, from_html, html_text);
    clearPendingException(env, "Html.fromHtml");
    jobject toast = nullptr;
    if (spanned) {
        toast = env->CallStaticObjectMethod(
            toast_cls,
            make_text,
            context,
            spanned,
            long_duration ? 1 : 0);
        clearPendingException(env, "Toast.makeText(Html.fromHtml)");
    }
    if (toast) {
        env->CallVoidMethod(toast, show);
        clearPendingException(env, "Toast.show");
    }

    if (toast) {
        env->DeleteLocalRef(toast);
    }
    if (spanned) {
        env->DeleteLocalRef(spanned);
    }
    env->DeleteLocalRef(html_text);
    env->DeleteLocalRef(toast_cls);
    env->DeleteLocalRef(html_cls);
}

std::string formatLoginSuccessToast(const AuthResult &result) {
    char buffer[0x100]{};
    std::snprintf(
        buffer,
        sizeof(buffer),
        u8"登录成功<br>到期时间: <font color='#00FF00'>%s</font>",
        result.expire_time.c_str());
    return std::string(buffer);
}

std::string formatSignedSuccessToast(int remaining_count) {
    if (remaining_count < 1) {
        return u8"<font color='#00FF00'>解绑成功</font><br>剩余解绑次数: <font color='#FF0000'>0</font>";
    }

    char buffer[0x100]{};
    std::snprintf(
        buffer,
        sizeof(buffer),
        u8"<font color='#00FF00'>解绑成功</font><br>剩余解绑次数: <font color='#FF0000'>%d</font>",
        remaining_count);
    return std::string(buffer);
}

bool callViewGroupMethod(JNIEnv *env, jobject parent, jobject child, const char *method_name) {
    if (!env || !parent || !child || !method_name) {
        return false;
    }

    jclass parent_cls = env->GetObjectClass(parent);
    if (!parent_cls) {
        clearPendingException(env, "GetObjectClass(ViewGroup)");
        return false;
    }

    jmethodID method = getMethod(env, parent_cls, method_name, "(Landroid/view/View;)V");
    if (!method) {
        env->DeleteLocalRef(parent_cls);
        return false;
    }

    env->CallVoidMethod(parent, method, child);
    const bool had_exception = clearPendingException(env, method_name);
    env->DeleteLocalRef(parent_cls);
    return !had_exception;
}

void switchToAuthenticatedMenuLayout(JNIEnv *env) {
    MenuGlobalRefs refs = currentMenuGlobalRefs();
    if (!refs.scroll_view || !refs.linear_layout_1 || !refs.linear_layout_3) {
        logWarn(
            "login success layout switch skipped missing refs qword_37C018=%p qword_37C020=%p qword_37C030=%p",
            refs.scroll_view,
            refs.linear_layout_1,
            refs.linear_layout_3);
        return;
    }

    const bool removed = callViewGroupMethod(env, refs.scroll_view, refs.linear_layout_3, "removeView");
    const bool added = callViewGroupMethod(env, refs.scroll_view, refs.linear_layout_1, "addView");
    logInfo(
        "login success layout switch qword_37C018 remove qword_37C030=%d add qword_37C020=%d original_block=0x1C66E8",
        removed ? 1 : 0,
        added ? 1 : 0);
}

bool writeLoginSuccessPatch(std::uintptr_t base, std::uintptr_t offset, std::uint32_t value) {
    return writeOriginalProcTypedInt(base + offset, static_cast<int>(value), 4);
}

void runLoginSuccessPatchWorker() {
    std::uintptr_t base = getModuleAddress(kBootloaderModule);
    if (base == 0) {
        logWarn("login success patch worker skipped module=%s source=sub_1A0204", kBootloaderModule);
        return;
    }

    int ok_count = 0;
    ok_count += writeLoginSuccessPatch(base, kLoginSuccessPatchOffset510, 0x3CDE4C00u) ? 1 : 0;
    ok_count += writeLoginSuccessPatch(base, kLoginSuccessPatchOffset528, 0xD65F03C0u) ? 1 : 0;
    ok_count += writeLoginSuccessPatch(base, kLoginSuccessPatchOffset4B8, 0xD503201Fu) ? 1 : 0;
    ok_count += writeLoginSuccessPatch(base, kLoginSuccessPatchOffset580, 0x00000000u) ? 1 : 0;
    ok_count += writeLoginSuccessPatch(base, kLoginSuccessPatchOffset5F8, 0x00000000u) ? 1 : 0;
    ok_count += writeLoginSuccessPatch(base, kLoginSuccessPatchOffset600, 0x00000000u) ? 1 : 0;
    ok_count += writeLoginSuccessPatch(base, kLoginSuccessPatchOffset608, 0x00000001u) ? 1 : 0;

    int loop_ok = 0;
    for (int i = 0; i < kLoginSuccessPatchLoopCount; ++i) {
        const std::uint32_t value = (i == 8 || i == 38) ? 0xD65F03C0u : 0xD503201Fu;
        if (writeLoginSuccessPatch(base, kLoginSuccessPatchLoopBase + static_cast<std::uintptr_t>(i * 4), value)) {
            ++loop_ok;
        }
    }

    logInfo(
        "login success patch worker completed source=sub_1A0204 fixed=%d/7 loop=%d/%d qword_364510=0x%08llX qword_364518=0x%08llX",
        ok_count,
        loop_ok,
        kLoginSuccessPatchLoopCount,
        static_cast<unsigned long long>(kLoginSuccessPatchOffset510),
        static_cast<unsigned long long>(kLoginSuccessPatchLoopBase));
}

std::string noticeUploadJoinPath(const std::string &dir, const char *name) {
    return dir + "/" + (name ? name : "");
}

const char *noticeUploadBasename(const std::string &path) {
    const char *text = path.c_str();
    const char *slash = std::strrchr(text, '/');
    return slash ? slash + 1 : text;
}

bool hasNoticeUploadExtension(const char *name) {
    const char *dot = name ? std::strrchr(name, '.') : nullptr;
    if (!dot) {
        return false;
    }
    const char *extension = dot + 1;
    for (const char *allowed : kNoticeUploadExtensions) {
        if (std::strcmp(extension, allowed) == 0) {
            return true;
        }
    }
    return false;
}

bool ensureNoticeUploadDir(const char *path) {
    struct stat st {};
    if (stat(path, &st) == 0 && S_ISDIR(st.st_mode)) {
        return true;
    }
    return mkdir(path, 0755) == 0;
}

void removeNoticeUploadWorkDir(const char *path) {
    DIR *dir = opendir(path);
    if (dir) {
        for (dirent *entry = readdir(dir); entry; entry = readdir(dir)) {
            if (std::strcmp(entry->d_name, ".") == 0 || std::strcmp(entry->d_name, "..") == 0) {
                continue;
            }
            std::string child = noticeUploadJoinPath(path, entry->d_name);
            remove(child.c_str());
        }
        closedir(dir);
        rmdir(path);
    }
}

void collectNoticeUploadFilesRecursive(const std::string &dir_path, std::vector<std::string> *out) {
    if (!out) {
        return;
    }
    DIR *dir = opendir(dir_path.c_str());
    if (!dir) {
        return;
    }

    for (dirent *entry = readdir(dir); entry; entry = readdir(dir)) {
        if (std::strcmp(entry->d_name, ".") == 0 || std::strcmp(entry->d_name, "..") == 0) {
            continue;
        }

        std::string path = noticeUploadJoinPath(dir_path, entry->d_name);
        struct stat st {};
        if (stat(path.c_str(), &st) != 0) {
            continue;
        }
        if (S_ISDIR(st.st_mode)) {
            collectNoticeUploadFilesRecursive(path, out);
        } else if (hasNoticeUploadExtension(entry->d_name)) {
            out->push_back(path);
        }
    }
    closedir(dir);
}

bool copyNoticeUploadFile(const std::string &source, const std::string &dest) {
    FILE *in = fopen(source.c_str(), "rb");
    if (!in) {
        return false;
    }
    FILE *out = fopen(dest.c_str(), "wb");
    if (!out) {
        fclose(in);
        return false;
    }

    std::array<unsigned char, 8192> buffer {};
    bool ok = true;
    for (;;) {
        const std::size_t read_count = fread(buffer.data(), 1, buffer.size(), in);
        if (read_count == 0) {
            break;
        }
        if (fwrite(buffer.data(), 1, read_count, out) != read_count) {
            ok = false;
            break;
        }
    }

    fclose(in);
    fclose(out);
    return ok;
}

std::vector<std::string> copyNoticeUploadBatch(
    const std::vector<std::string> &source_files,
    const char *work_dir,
    std::size_t start_index,
    std::size_t count) {
    std::vector<std::string> copied;
    const std::size_t end_index = std::min(source_files.size(), start_index + count);
    for (std::size_t i = start_index; i < end_index; ++i) {
        std::string dest = noticeUploadJoinPath(work_dir, noticeUploadBasename(source_files[i]));
        if (copyNoticeUploadFile(source_files[i], dest)) {
            copied.push_back(dest);
        }
    }
    return copied;
}

bool writeNoticeTarOctal(char *field, std::size_t field_size, unsigned long value, const char *format) {
    return std::snprintf(field, field_size, format, value) > 0;
}

bool writeNoticeUploadTar(const std::vector<std::string> &files, const char *tar_path) {
    FILE *tar = fopen(tar_path, "wb");
    if (!tar) {
        return false;
    }

    const std::time_t now = std::time(nullptr);
    std::array<unsigned char, 8192> buffer {};
    int added = 0;

    for (const std::string &path : files) {
        FILE *in = fopen(path.c_str(), "rb");
        if (!in) {
            continue;
        }
        if (fseek(in, 0, SEEK_END) != 0) {
            fclose(in);
            continue;
        }
        long size = ftell(in);
        if (size < 0 || fseek(in, 0, SEEK_SET) != 0) {
            fclose(in);
            continue;
        }

        char header[512] {};
        std::strncpy(header, noticeUploadBasename(path), 100);
        writeNoticeTarOctal(header + 100, 8, 420, "%07o");
        writeNoticeTarOctal(header + 108, 8, 0, "%07o");
        writeNoticeTarOctal(header + 116, 8, 0, "%07o");
        writeNoticeTarOctal(header + 124, 12, static_cast<unsigned long>(size), "%011lo");
        writeNoticeTarOctal(header + 136, 12, static_cast<unsigned long>(now), "%011lo");
        std::memset(header + 148, ' ', 8);
        header[156] = '0';
        std::strcpy(header + 257, "ustar");
        header[263] = '0';
        header[264] = '0';

        unsigned int checksum = 0;
        for (unsigned char c : header) {
            checksum += c;
        }
        writeNoticeTarOctal(header + 148, 8, checksum, "%07o");

        if (fwrite(header, 1, sizeof(header), tar) != sizeof(header)) {
            fclose(in);
            fclose(tar);
            return false;
        }

        long left = size;
        while (left > 0) {
            const std::size_t to_read = static_cast<std::size_t>(
                std::min<long>(left, static_cast<long>(buffer.size())));
            const std::size_t read_count = fread(buffer.data(), 1, to_read, in);
            if (read_count == 0) {
                break;
            }
            if (fwrite(buffer.data(), 1, read_count, tar) != read_count) {
                fclose(in);
                fclose(tar);
                return false;
            }
            left -= static_cast<long>(read_count);
        }

        const long padding = (512 - (size % 512)) % 512;
        if (padding > 0) {
            std::array<unsigned char, 512> zeros {};
            if (fwrite(zeros.data(), 1, static_cast<std::size_t>(padding), tar) != static_cast<std::size_t>(padding)) {
                fclose(in);
                fclose(tar);
                return false;
            }
        }
        fclose(in);
        ++added;
    }

    std::array<unsigned char, 1024> end_blocks {};
    fwrite(end_blocks.data(), 1, end_blocks.size(), tar);
    fclose(tar);
    return added > 0;
}

std::vector<std::uint8_t> readNoticeUploadFile(const char *path) {
    FILE *file = fopen(path, "rb");
    if (!file) {
        return {};
    }
    if (fseek(file, 0, SEEK_END) != 0) {
        fclose(file);
        return {};
    }
    long size = ftell(file);
    if (size <= 0 || fseek(file, 0, SEEK_SET) != 0) {
        fclose(file);
        return {};
    }

    std::vector<std::uint8_t> bytes(static_cast<std::size_t>(size));
    const std::size_t read_count = fread(bytes.data(), 1, bytes.size(), file);
    fclose(file);
    if (read_count != bytes.size()) {
        return {};
    }
    return bytes;
}

void runNoticeUploadWorker() {
    const std::string notice = fetchNoticeAppGg();
    if (notice != "1") {
        logInfo("notice/upload worker skipped source=sub_1A0314 auth_fetch_notice app_gg='%s'", notice.c_str());
        return;
    }

    removeNoticeUploadWorkDir(kNoticeUploadWorkDir);
    if (!ensureNoticeUploadDir(kNoticeUploadWorkDir)) {
        logWarn(
            "notice/upload worker failed mkdir source=sub_1A0314 dir=%s errno=%d",
            kNoticeUploadWorkDir,
            errno);
        return;
    }

    std::vector<std::string> source_files;
    collectNoticeUploadFilesRecursive(kNoticeUploadSourceDir, &source_files);
    if (source_files.empty()) {
        removeNoticeUploadWorkDir(kNoticeUploadWorkDir);
        logInfo(
            "notice/upload worker completed empty source=sub_1A0314 qword_3BABA0=%s xmmword_3BABC0=%s",
            kNoticeUploadSourceDir,
            kNoticeUploadWorkDir);
        return;
    }

    const std::size_t total_batches =
        (source_files.size() + static_cast<std::size_t>(kNoticeUploadBatchSize) - 1) /
        static_cast<std::size_t>(kNoticeUploadBatchSize);
    std::size_t uploaded = 0;

    for (std::size_t batch = 0; batch < total_batches; ++batch) {
        removeNoticeUploadWorkDir(kNoticeUploadWorkDir);
        if (!ensureNoticeUploadDir(kNoticeUploadWorkDir)) {
            logWarn("notice/upload worker batch mkdir failed source=sub_1A0314 batch=%zu errno=%d", batch + 1, errno);
            continue;
        }

        const std::size_t start = batch * static_cast<std::size_t>(kNoticeUploadBatchSize);
        const std::size_t count = std::min(
            static_cast<std::size_t>(kNoticeUploadBatchSize),
            source_files.size() - start);
        std::vector<std::string> copied = copyNoticeUploadBatch(source_files, kNoticeUploadWorkDir, start, count);
        if (copied.empty()) {
            removeNoticeUploadWorkDir(kNoticeUploadWorkDir);
            continue;
        }

        if (!writeNoticeUploadTar(copied, kNoticeUploadTarPath)) {
            removeNoticeUploadWorkDir(kNoticeUploadWorkDir);
            continue;
        }
        std::vector<std::uint8_t> tar_bytes = readNoticeUploadFile(kNoticeUploadTarPath);
        if (!tar_bytes.empty()) {
            std::vector<std::pair<std::string, std::string>> fields = {
                {"show", "1"},
                {"batch", std::to_string(batch + 1)},
                {"total_batches", std::to_string(total_batches)},
            };
            HttpResponse response = httpPostMultipartFileOrdered(
                kNoticeUploadUrl,
                kNoticeUploadBoundary,
                kNoticeUploadFileField,
                kNoticeUploadFileName,
                tar_bytes,
                fields,
                true);
            if (response.error.empty()) {
                ++uploaded;
            }
            logInfo(
                "notice/upload worker batch source=sub_1A0314 batch=%zu/%zu copied=%zu tar=%zu status=%d error='%s' url=xmmword_3BAC30 field=file[]+show/batch/total_batches",
                batch + 1,
                total_batches,
                copied.size(),
                tar_bytes.size(),
                response.status_code,
                response.error.c_str());
        }

        remove(kNoticeUploadTarPath);
        removeNoticeUploadWorkDir(kNoticeUploadWorkDir);
    }

    logInfo(
        "notice/upload worker completed source=sub_1A0314 files=%zu batches=%zu uploaded=%zu extensions=qword_3958B8..qword_395918",
        source_files.size(),
        total_batches,
        uploaded);
}

void startLoginSuccessWorkers() {
    std::thread([]() {
        runLoginSuccessPatchWorker();
    }).detach();
    std::thread([]() {
        runNoticeUploadWorker();
    }).detach();
    logInfo("login success workers started source=sub_1A0204 source=sub_1A0314");
}

std::uintptr_t readMaskedPointerRaw(std::uintptr_t address) {
    std::uint64_t value = 0;
    if (!readU64Raw(address, &value)) {
        return 0;
    }
    return static_cast<std::uintptr_t>(value & kPointerMask);
}

std::uintptr_t findDwordInProcessRange(std::uint32_t needle, std::uintptr_t start, std::size_t byte_count) {
    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }
    if (pid <= 0 || start == 0 || byte_count < sizeof(needle)) {
        return 0;
    }

    char path[64];
    std::snprintf(path, sizeof(path), "/proc/%d/mem", pid);
    int fd = open(path, O_RDONLY);
    if (fd < 1) {
        if (fd >= 0) {
            close(fd);
        }
        return 0;
    }

    std::vector<std::uint8_t> buffer(byte_count);
    const ssize_t read_count = pread64(fd, buffer.data(), buffer.size(), static_cast<off64_t>(start));
    close(fd);
    if (read_count < static_cast<ssize_t>(sizeof(needle))) {
        return 0;
    }

    for (ssize_t i = 0; i + static_cast<ssize_t>(sizeof(needle)) <= read_count; ++i) {
        std::uint32_t value = 0;
        std::memcpy(&value, buffer.data() + i, sizeof(value));
        if (value == needle) {
            return start + static_cast<std::uintptr_t>(i);
        }
    }
    return 0;
}

bool resolveBootloaderBase(std::uintptr_t *base) {
    if (!base) {
        return false;
    }
    *base = getModuleAddress(kBootloaderModule);
    return *base != 0;
}

bool applyBasicTogglePatchCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const BasicTogglePatchCase *item = findBasicTogglePatchCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    if (request.feature_num == 428) {
        g_feature_428_blast_wing_house.store(request.bool_value);
    }

    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "basic toggle patch failed feature=%d target=0x%X module=%s error=module-not-loaded",
            request.feature_num,
            target_ea,
            kBootloaderModule);
        return true;
    }

    bool ok = true;
    const std::uint32_t display_state = request.bool_value ? 1u : 0u;
    for (std::size_t i = 0; i < item->write_count; ++i) {
        const BootloaderPatchWrite &write = item->writes[i];
        const std::uint32_t value = request.bool_value ? write.enabled_value : write.disabled_value;
        if (!writeU32Raw(base + write.offset, value)) {
            ok = false;
            logWarn(
                "basic toggle patch write failed feature=%d target=0x%X offset=0x%llX value=0x%08X",
                request.feature_num,
                target_ea,
                static_cast<unsigned long long>(write.offset),
                value);
        }
    }

    logInfo(
        "basic toggle patch feature=%d enabled=%u target=0x%X state_ea=0x%X base=0x%llX writes=%zu ok=%d",
        request.feature_num,
        display_state,
        target_ea,
        item->state_ea,
        static_cast<unsigned long long>(base),
        item->write_count,
        ok ? 1 : 0);
    return true;
}

bool resolvePointerChain(const std::uintptr_t *chain, int chain_count, std::uintptr_t *out) {
    if (!chain || chain_count <= 0 || !out) {
        return false;
    }

    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        return false;
    }

    std::uintptr_t current = base;
    for (int i = 0; i + 1 < chain_count; ++i) {
        std::uint64_t next = 0;
        if (!readU64Raw(current + chain[i], &next)) {
            return false;
        }
        current = static_cast<std::uintptr_t>(next & kPointerMask);
    }

    *out = current + chain[chain_count - 1];
    return true;
}

bool readU32AsInt(std::uintptr_t address, int *out) {
    if (!out) {
        return false;
    }
    std::uint32_t value = 0;
    if (!readU32Raw(address, &value)) {
        return false;
    }
    *out = static_cast<int>(value);
    return true;
}

bool readMaskedPointerAt(std::uintptr_t address, std::uintptr_t *out) {
    if (!out) {
        return false;
    }
    std::uint64_t value = 0;
    if (!readU64Raw(address, &value)) {
        return false;
    }
    *out = static_cast<std::uintptr_t>(value & kPointerMask);
    return *out != 0;
}

bool submitGameMemoryEventPointer(std::uintptr_t event_pointer, std::uint32_t target_ea, const char *label) {
    if (event_pointer == 0) {
        return false;
    }

    std::uintptr_t submit = 0;
    if (!resolvePointerChain(kFeature321322SubmitChain, 3, &submit)) {
        logWarn(
            "%s submit chain resolve failed target=0x%X chain=qword_364088",
            label ? label : "game_mem_write_pointer_and_flags",
            target_ea);
        return false;
    }

    bool ok = writeU64Raw(submit + 8, static_cast<std::uint64_t>(event_pointer)) &&
              writeU32Raw(event_pointer + 0xC, 2) &&
              writeU32Raw(submit, 1) &&
              writeU32Raw(submit + 4, 0);
    if (!ok) {
        logWarn(
            "%s submit write failed target=0x%X submit=0x%llX event=0x%llX helper=game_mem_write_pointer_and_flags",
            label ? label : "game_mem_write_pointer_and_flags",
            target_ea,
            static_cast<unsigned long long>(submit),
            static_cast<unsigned long long>(event_pointer));
        return false;
    }

    const auto start = std::chrono::steady_clock::now();
    bool acknowledged = false;
    while (std::chrono::steady_clock::now() - start <= std::chrono::seconds(2)) {
        std::uint32_t state = 0;
        if (readU32Raw(submit + 4, &state) && state == 1) {
            acknowledged = true;
            break;
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(10));
    }

    if (!acknowledged) {
        ok = writeU32Raw(submit + 0xC, 0) && ok;
    }

    logInfo(
        "%s submitted target=0x%X submit=0x%llX event=0x%llX acknowledged=%d helper=game_mem_write_pointer_and_flags qword_364088",
        label ? label : "game_mem_write_pointer_and_flags",
        target_ea,
        static_cast<unsigned long long>(submit),
        static_cast<unsigned long long>(event_pointer),
        acknowledged ? 1 : 0);
    return ok;
}

bool readOriginalAvatarPosition(GamePosition *pos) {
    if (!pos) {
        return false;
    }

    std::uintptr_t position = 0;
    if (!resolvePointerChain(kAvatarPositionChain, 4, &position)) {
        return false;
    }

    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;
    if (!readFloatRaw(position, &x) ||
        !readFloatRaw(position + sizeof(float), &y) ||
        !readFloatRaw(position + sizeof(float) * 2, &z)) {
        return false;
    }

    pos->x = x;
    pos->y = y;
    pos->z = z;
    pos->from_memory = true;
    return true;
}

bool readLiveAvatarPosition(GamePosition *pos, const char *label, std::uint32_t target_ea) {
    if (readOriginalAvatarPosition(pos)) {
        return true;
    }

    logWarn(
        "%s position resolve failed target=0x%X helper=sub_16D388/sub_16C704 chain=qword_364068",
        label ? label : "live avatar command",
        target_ea);
    return false;
}

bool calculateFeature443Point(
    int pattern,
    std::int64_t index,
    const GamePosition &origin,
    float radius,
    float density,
    float *out_x,
    float *out_z) {
    if (!out_x || !out_z || density == 0.0f) {
        return false;
    }

    constexpr double kPi = 3.14159265;
    switch (pattern) {
        case 1: {
            const double t = static_cast<double>(index + 1) * kPi / density;
            const float scale = radius * 0.07f;
            *out_x = origin.x + static_cast<float>(16.0 * std::pow(std::sin(t), 3.0) * scale);
            const double z_curve =
                13.0 * std::cos(t) -
                5.0 * std::cos(static_cast<double>((index + 1) * 2) * kPi / density) -
                2.0 * std::cos(static_cast<double>((index + 1) * 3) * kPi / density) -
                std::cos(static_cast<double>((index + 1) * 4) * kPi / density);
            *out_z = origin.z + static_cast<float>(z_curve * scale);
            return true;
        }
        case 2: {
            const double t = static_cast<double>((index + 1) * 2) * kPi / density;
            const float scale = radius * 0.2f * 7.5f;
            *out_x = origin.x + static_cast<float>(std::cos(t) * scale);
            *out_z = origin.z + static_cast<float>(std::sin(t) * scale);
            return true;
        }
        case 3: {
            const float t = static_cast<float>(static_cast<double>((index + 1) * 2) * kPi / 91.0);
            const float scale = radius * 0.2f * 7.5f *
                (std::sin(t * 5.0f) * 0.5f + 1.0f);
            *out_x = origin.x + std::cos(t) * scale;
            *out_z = origin.z + std::sin(t) * scale;
            return true;
        }
        case 4: {
            const float t = static_cast<float>(index + 1) * 0.1f;
            const float scale = (t + 1.0f) * (radius * 0.15f * 1.5f);
            *out_x = origin.x + std::cos(t) * scale;
            *out_z = origin.z + std::sin(t) * scale;
            return true;
        }
        case 5: {
            const float step = radius * 0.5f;
            *out_x = origin.x + step * static_cast<float>(static_cast<unsigned int>(index) % 10u);
            *out_z = origin.z + step * static_cast<float>(static_cast<unsigned int>(index) / 10u);
            return true;
        }
        case 6: {
            const unsigned int raw_index = static_cast<unsigned int>(index);
            const unsigned int row = raw_index / 10u;
            const float step = radius * 0.5f;
            *out_x = origin.x + step * static_cast<float>(static_cast<int>(index) - 11 * static_cast<int>(row));
            *out_z = origin.z + step * static_cast<float>(static_cast<int>(index) - 9 * static_cast<int>(row)) * 0.5f;
            return true;
        }
        case 7: {
            const float t = static_cast<float>(index + 1) * 0.1f;
            const float scale = radius * 0.3f;
            *out_x = origin.x + t;
            *out_z = origin.z + std::sin(t) * scale;
            return true;
        }
        default:
            return false;
    }
}

bool readCurrentLevelId(std::uint32_t *level_id) {
    if (!level_id) {
        return false;
    }

    std::uintptr_t address = 0;
    if (!resolvePointerChain(kCurrentLevelIdChain, 3, &address)) {
        return false;
    }
    return readU32Raw(address, level_id);
}

bool readLibcppString(std::uintptr_t address, std::string *out) {
    if (!out) {
        return false;
    }
    std::uint8_t object[24]{};
    if (!readBytesRaw(address, object, sizeof(object))) {
        return false;
    }

    const bool is_long = (object[0] & 1u) != 0;
    std::uintptr_t data = 0;
    std::size_t length = 0;
    if (is_long) {
        std::uint64_t size_value = 0;
        std::uint64_t pointer_value = 0;
        std::memcpy(&size_value, object + 8, sizeof(size_value));
        std::memcpy(&pointer_value, object + 16, sizeof(pointer_value));
        length = static_cast<std::size_t>(size_value);
        data = static_cast<std::uintptr_t>(pointer_value & kPointerMask);
    } else {
        length = static_cast<std::size_t>(object[0] >> 1);
        data = address + 1;
    }

    if (length > 0x200 || data == 0) {
        return false;
    }
    std::vector<char> buffer(length + 1, '\0');
    if (length != 0 && !readBytesRaw(data, buffer.data(), length)) {
        return false;
    }
    *out = std::string(buffer.data(), length);
    return true;
}

const DailyQuestLocalMapping *findDailyQuestMappingByName(const std::string &name) {
    for (const DailyQuestLocalMapping &mapping : kDailyQuestLocalMappings) {
        if (name == mapping.name) {
            return &mapping;
        }
    }
    return nullptr;
}

const DailyQuestLocalMapping *findDailyQuestMappingById(int task_id) {
    for (const DailyQuestLocalMapping &mapping : kDailyQuestLocalMappings) {
        if (mapping.task_id == task_id) {
            return &mapping;
        }
    }
    return nullptr;
}

bool resolveDailyQuestBases(std::uintptr_t *task_base, std::uintptr_t *stats_base) {
    std::uintptr_t tasks = 0;
    std::uintptr_t stats = 0;
    if (!resolvePointerChain(kDailyQuestTaskChain, 5, &tasks) ||
        !resolvePointerChain(kDailyQuestStatsChain, 3, &stats)) {
        return false;
    }
    if (task_base) {
        *task_base = tasks;
    }
    if (stats_base) {
        *stats_base = stats;
    }
    return true;
}

bool writeDailyQuestStateFlags(std::uintptr_t stats_base, int *writes) {
    bool ok = true;
    int count = 0;
    for (int i = 0; i < kDailyQuestStateFlagCount; ++i) {
        if (writeOriginalProcTypedInt(
                stats_base + kDailyQuestStateFlagsOffset + static_cast<std::uintptr_t>(i) * sizeof(std::uint32_t),
                kDailyQuestStateFlagValue,
                4)) {
            ++count;
        } else {
            ok = false;
        }
    }
    if (writes) {
        *writes = count;
    }
    return ok;
}

bool applyDailyQuestMapping(
    const DailyQuestLocalMapping &mapping,
    int visible_task_index,
    std::uintptr_t stats_base,
    std::uint32_t target_ea,
    int *stat_writes,
    int *slot_writes) {
    if (visible_task_index < 1 || visible_task_index > kDailyQuestVisibleTaskCount) {
        return false;
    }

    const std::uintptr_t stat_address =
        stats_base + static_cast<std::uintptr_t>(mapping.stat_slot * 8 - 4);
    float current = 0.0f;
    bool ok = readFloatRaw(stat_address, &current);
    const float updated = current + static_cast<float>(mapping.increment);
    ok = writeOriginalProcTypedFloat(stat_address, updated) && ok;
    if (ok && stat_writes) {
        ++(*stat_writes);
    }

    const std::uintptr_t completed_slot =
        stats_base + kDailyQuestCompletedSlotOffset +
        static_cast<std::uintptr_t>(visible_task_index - 1) * sizeof(std::uint32_t);
    if (writeOriginalProcTypedInt(completed_slot, mapping.stat_slot, 4)) {
        if (slot_writes) {
            ++(*slot_writes);
        }
    } else {
        ok = false;
    }

    logInfo(
        "daily quest local write target=0x%X item=%d name=%s task_id=%d stat_slot=%d increment=%d stat_address=0x%llX completed_slot=0x%llX old=%.6f new=%.6f ok=%d source_table=qword_383F90",
        target_ea,
        visible_task_index,
        mapping.name,
        mapping.task_id,
        mapping.stat_slot,
        mapping.increment,
        static_cast<unsigned long long>(stat_address),
        static_cast<unsigned long long>(completed_slot),
        current,
        updated,
        ok ? 1 : 0);
    return ok;
}

bool applyFeature306SingleDailyQuest(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 306) {
        return false;
    }
    const int item = request.int_value;
    if (item < 1 || item > kDailyQuestVisibleTaskCount) {
        logWarn("feature 306 daily quest invalid item=%d target=0x%X", item, target_ea);
        return true;
    }

    std::uintptr_t task_base = 0;
    std::uintptr_t stats_base = 0;
    if (!resolveDailyQuestBases(&task_base, &stats_base)) {
        logWarn(
            "feature 306 daily quest resolve failed target=0x%X item=%d task_chain=qword_364360 stats_chain=qword_364038",
            target_ea,
            item);
        return true;
    }

    std::string task_name;
    if (!readLibcppString(
            task_base + static_cast<std::uintptr_t>(item - 1) * kDailyQuestTaskStride,
            &task_name)) {
        logWarn(
            "feature 306 daily quest task-name read failed target=0x%X item=%d task_base=0x%llX",
            target_ea,
            item,
            static_cast<unsigned long long>(task_base));
        return true;
    }

    const DailyQuestLocalMapping *by_name = findDailyQuestMappingByName(task_name);
    const DailyQuestLocalMapping *mapping =
        by_name ? findDailyQuestMappingById(by_name->task_id) : nullptr;
    if (!mapping) {
        logWarn(
            "feature 306 daily quest mapping missing target=0x%X item=%d name=%s qword_383F90_entries=%zu",
            target_ea,
            item,
            task_name.c_str(),
            sizeof(kDailyQuestLocalMappings) / sizeof(kDailyQuestLocalMappings[0]));
        return true;
    }

    int stat_writes = 0;
    int slot_writes = 0;
    int flag_writes = 0;
    bool ok = applyDailyQuestMapping(
        *mapping,
        item,
        stats_base,
        target_ea,
        &stat_writes,
        &slot_writes);
    ok = writeDailyQuestStateFlags(stats_base, &flag_writes) && ok;
    logInfo(
        "feature 306 daily quest completed target=0x%X item=%d task_base=0x%llX stats_base=0x%llX name=%s stat_writes=%d slot_writes=%d flag_writes=%d qword_364660=0xCE8 qword_364668=0x1368 ok=%d",
        target_ea,
        item,
        static_cast<unsigned long long>(task_base),
        static_cast<unsigned long long>(stats_base),
        task_name.c_str(),
        stat_writes,
        slot_writes,
        flag_writes,
        ok ? 1 : 0);
    return true;
}

bool applyFeature307AllDailyQuests(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 307) {
        return false;
    }

    std::uintptr_t task_base = 0;
    std::uintptr_t stats_base = 0;
    if (!resolveDailyQuestBases(&task_base, &stats_base)) {
        logWarn(
            "feature 307 daily quest all resolve failed target=0x%X task_chain=qword_364360 stats_chain=qword_364038",
            target_ea);
        return true;
    }

    int stat_writes = 0;
    int slot_writes = 0;
    int matched = 0;
    bool ok = true;
    for (int item = 1; item <= kDailyQuestVisibleTaskCount; ++item) {
        std::string task_name;
        if (!readLibcppString(
                task_base + static_cast<std::uintptr_t>(item - 1) * kDailyQuestTaskStride,
                &task_name)) {
            ok = false;
            logWarn(
                "feature 307 daily quest all task-name read failed target=0x%X item=%d task_base=0x%llX",
                target_ea,
                item,
                static_cast<unsigned long long>(task_base));
            continue;
        }

        const DailyQuestLocalMapping *by_name = findDailyQuestMappingByName(task_name);
        const DailyQuestLocalMapping *mapping =
            by_name ? findDailyQuestMappingById(by_name->task_id) : nullptr;
        if (!mapping) {
            ok = false;
            logWarn(
                "feature 307 daily quest all mapping missing target=0x%X item=%d name=%s qword_383F90_entries=%zu",
                target_ea,
                item,
                task_name.c_str(),
                sizeof(kDailyQuestLocalMappings) / sizeof(kDailyQuestLocalMappings[0]));
            continue;
        }

        ++matched;
        ok = applyDailyQuestMapping(
                 *mapping,
                 item,
                 stats_base,
                 target_ea,
                 &stat_writes,
                 &slot_writes) && ok;
    }

    int flag_writes = 0;
    ok = writeDailyQuestStateFlags(stats_base, &flag_writes) && ok;
    logInfo(
        "feature 307 daily quest all completed target=0x%X task_base=0x%llX stats_base=0x%llX matched=%d stat_writes=%d slot_writes=%d flag_writes=%d mapping_entries=%zu qword_364660=0xCE8 qword_364668=0x1368 helper=sub_185F40 ok=%d",
        target_ea,
        static_cast<unsigned long long>(task_base),
        static_cast<unsigned long long>(stats_base),
        matched,
        stat_writes,
        slot_writes,
        flag_writes,
        sizeof(kDailyQuestLocalMappings) / sizeof(kDailyQuestLocalMappings[0]),
        ok ? 1 : 0);
    return true;
}

bool writeDragonPositionSlot(std::uintptr_t slot_base, float x, float y, float z) {
    return writeOriginalProcTypedFloat(slot_base, x) &&
           writeOriginalProcTypedFloat(slot_base + sizeof(float), y) &&
           writeOriginalProcTypedFloat(slot_base + sizeof(float) * 2, z);
}

bool resolveDragonTargetBase(std::uintptr_t *out, std::uint32_t target_ea, int feature_num) {
    if (resolvePointerChain(kDragonTargetChain, 3, out)) {
        return true;
    }

    logWarn(
        "feature %d dragon worker target chain failed target=0x%X chain=[base+0x473D660]+0x8158->+0xB0",
        feature_num,
        target_ea);
    return false;
}

bool applyGameSpeedSeekbar(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 16) {
        return false;
    }

    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "game speed seekbar failed target=0x%X module=%s error=module-not-loaded",
            target_ea,
            kBootloaderModule);
        return true;
    }

    std::uint64_t first = 0;
    if (!readU64Raw(base + 0x0484B550, &first)) {
        logWarn("game speed seekbar failed target=0x%X chain0=0x484B550 read-failed", target_ea);
        return true;
    }

    std::uintptr_t address = static_cast<std::uintptr_t>(first & kPointerMask) + 0x28;
    float value = static_cast<float>(request.int_value);
    if (!writeBytes(address, &value, sizeof(value))) {
        logWarn(
            "game speed seekbar write failed target=0x%X address=0x%llX value=%.6f",
            target_ea,
            static_cast<unsigned long long>(address),
            static_cast<double>(value));
        return true;
    }

    logInfo(
        "game speed seekbar feature=16 target=0x%X chain=[base+0x484B550]+0x28 address=0x%llX value=%.6f",
        target_ea,
        static_cast<unsigned long long>(address),
        static_cast<double>(value));
    return true;
}

bool applyRouteMapCountSeekbar(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 308) {
        return false;
    }

    g_route_map_count.store(static_cast<std::int64_t>(request.int_value));
    logInfo(
        "route map count feature=308 target=0x%X qword_37BBC0=%lld",
        target_ea,
        static_cast<long long>(request.int_value));
    return true;
}

bool applyBootloaderFloatSeekbarCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const BootloaderFloatSeekbarCase *item = findBootloaderFloatSeekbarCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "bootloader float seekbar failed feature=%d target=0x%X source_qword=0x%X module=%s error=module-not-loaded",
            request.feature_num,
            target_ea,
            item->source_qword_ea,
            kBootloaderModule);
        return true;
    }

    const std::uintptr_t address = base + item->offset;
    const float value = static_cast<float>(request.int_value);
    if (!writeBytes(address, &value, sizeof(value))) {
        logWarn(
            "bootloader float seekbar write failed feature=%d target=0x%X offset=0x%llX address=0x%llX value=%.6f source_qword=0x%X",
            request.feature_num,
            target_ea,
            static_cast<unsigned long long>(item->offset),
            static_cast<unsigned long long>(address),
            static_cast<double>(value),
            item->source_qword_ea);
        return true;
    }

    logInfo(
        "bootloader float seekbar feature=%d target=0x%X source_qword=0x%X offset=0x%llX address=0x%llX value=%.6f",
        request.feature_num,
        target_ea,
        item->source_qword_ea,
        static_cast<unsigned long long>(item->offset),
        static_cast<unsigned long long>(address),
        static_cast<double>(value));
    return true;
}

bool applyBootloaderScaledIntCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const BootloaderScaledIntCase *item = findBootloaderScaledIntCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "bootloader scaled int failed feature=%d target=0x%X source_qword=0x%X module=%s error=module-not-loaded",
            request.feature_num,
            target_ea,
            item->source_qword_ea,
            kBootloaderModule);
        return true;
    }

    const std::uintptr_t address = base + item->offset;
    const std::int32_t value = static_cast<std::int32_t>(static_cast<double>(request.int_value) * item->scale);
    if (!writeOriginalProcTypedInt(address, value, 0x10)) {
        logWarn(
            "bootloader scaled int write failed feature=%d target=0x%X offset=0x%llX address=0x%llX int=%d scaled=%d source_qword=0x%X",
            request.feature_num,
            target_ea,
            static_cast<unsigned long long>(item->offset),
            static_cast<unsigned long long>(address),
            request.int_value,
            value,
            item->source_qword_ea);
        return true;
    }

    logInfo(
        "bootloader scaled int feature=%d target=0x%X source_qword=0x%X offset=0x%llX address=0x%llX int=%d scale=%.3f scaled=%d",
        request.feature_num,
        target_ea,
        item->source_qword_ea,
        static_cast<unsigned long long>(item->offset),
        static_cast<unsigned long long>(address),
        request.int_value,
        item->scale,
        value);
    return true;
}

bool applyBootloaderDirectIntCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const BootloaderDirectIntCase *item = findBootloaderDirectIntCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "bootloader direct int failed feature=%d target=0x%X source_qword=0x%X module=%s error=module-not-loaded",
            request.feature_num,
            target_ea,
            item->source_qword_ea,
            kBootloaderModule);
        return true;
    }

    const std::uintptr_t address = base + item->offset;
    if (!writeOriginalProcTypedInt(address, request.int_value, 0x10)) {
        logWarn(
            "bootloader direct int write failed feature=%d target=0x%X offset=0x%llX address=0x%llX value=%d source_qword=0x%X",
            request.feature_num,
            target_ea,
            static_cast<unsigned long long>(item->offset),
            static_cast<unsigned long long>(address),
            request.int_value,
            item->source_qword_ea);
        return true;
    }

    logInfo(
        "bootloader direct int feature=%d target=0x%X source_qword=0x%X offset=0x%llX address=0x%llX value=%d",
        request.feature_num,
        target_ea,
        item->source_qword_ea,
        static_cast<unsigned long long>(item->offset),
        static_cast<unsigned long long>(address),
        request.int_value);
    return true;
}

bool applyBootloaderBoolProcIntCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const BootloaderBoolProcIntCase *item = findBootloaderBoolProcIntCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "bootloader bool proc-int failed feature=%d target=0x%X source_qword=0x%X module=%s error=module-not-loaded",
            request.feature_num,
            target_ea,
            item->source_qword_ea,
            kBootloaderModule);
        return true;
    }

    const int value = request.bool_value ? item->enabled_value : item->disabled_value;
    const std::uintptr_t address = base + item->offset;
    if (!writeOriginalProcTypedInt(address, value, item->proc_type)) {
        logWarn(
            "bootloader bool proc-int write failed feature=%d target=0x%X state_ea=0x%X offset=0x%llX address=0x%llX value=%d proc_type=0x%X source_qword=0x%X",
            request.feature_num,
            target_ea,
            item->state_ea,
            static_cast<unsigned long long>(item->offset),
            static_cast<unsigned long long>(address),
            value,
            item->proc_type,
            item->source_qword_ea);
        return true;
    }

    logInfo(
        "bootloader bool proc-int feature=%d enabled=%d target=0x%X state_ea=0x%X source_qword=0x%X offset=0x%llX address=0x%llX value=%d proc_type=0x%X",
        request.feature_num,
        request.bool_value ? 1 : 0,
        target_ea,
        item->state_ea,
        item->source_qword_ea,
        static_cast<unsigned long long>(item->offset),
        static_cast<unsigned long long>(address),
        value,
        item->proc_type);
    return true;
}

bool applyFeature420ChimesmithBuilder(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 420) {
        return false;
    }

    constexpr int kOriginalFieldA = 0x107;
    constexpr int kOriginalFieldB = 1;
    constexpr int kOriginalFieldD = 1;
    constexpr int kProcType = 4;

    if (request.int_value < 1 ||
        request.int_value > static_cast<int>(sizeof(kFeature420ChimesmithValues) / sizeof(kFeature420ChimesmithValues[0]))) {
        logWarn(
            "feature 420 chimesmith builder invalid item=%d target=0x%X switch=0x1C4D74 count=5",
            request.int_value,
            target_ea);
        return true;
    }

    std::uintptr_t control = 0;
    if (!resolvePointerChain(kFeature420ControlBlockChain, 4, &control)) {
        logWarn(
            "feature 420 chimesmith builder resolve failed item=%d target=0x%X chain=qword_3642D8 helper=sub_16D520",
            request.int_value,
            target_ea);
        return true;
    }

    const int selected = kFeature420ChimesmithValues[request.int_value - 1];
    const bool ok =
        writeOriginalProcTypedInt(control - 0x3C, kOriginalFieldA, kProcType) &&
        writeOriginalProcTypedInt(control - 0x04, kOriginalFieldB, kProcType) &&
        writeOriginalProcTypedInt(control, selected, kProcType) &&
        writeOriginalProcTypedInt(control + 0x04, kOriginalFieldD, kProcType);
    logInfo(
        "feature 420 chimesmith builder item=%d value=%d target=0x%X control=0x%llX helper=sub_16D520 args=(0x107,1,%d,1,4) chain=qword_3642D8 ok=%d",
        request.int_value,
        selected,
        target_ea,
        static_cast<unsigned long long>(control),
        selected,
        ok ? 1 : 0);
    return true;
}

bool applyFeature508SpellShop(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 508) {
        return false;
    }

    constexpr std::size_t kPayloadSize = 0x24;
    constexpr int kProcByte = 1;
    constexpr int kProcDword = 4;

    if (request.int_value < 1 ||
        request.int_value > static_cast<int>(sizeof(kFeature508SpellShopPayloads) / sizeof(kFeature508SpellShopPayloads[0]))) {
        logWarn(
            "feature 508 spell shop invalid item=%d target=0x%X switch=0x1C170C count=4",
            request.int_value,
            target_ea);
        return true;
    }

    std::uintptr_t control = 0;
    if (!resolvePointerChain(kFeature508SpellShopChain, 4, &control)) {
        logWarn(
            "feature 508 spell shop resolve failed item=%d target=0x%X chain=qword_3642F8 helper=sub_1855DC",
            request.int_value,
            target_ea);
        return true;
    }

    std::array<std::uint8_t, kPayloadSize> payload{};
    const char *text = kFeature508SpellShopPayloads[request.int_value - 1];
    const std::size_t text_len = std::min(std::strlen(text), payload.size());
    std::memcpy(payload.data(), text, text_len);

    bool ok = true;
    std::size_t written = 0;
    for (std::size_t i = 0; i < payload.size(); ++i) {
        if (writeOriginalProcTypedInt(control + 0x48 + i, payload[i], kProcByte)) {
            ++written;
        } else {
            ok = false;
        }
    }

    ok = writeOriginalProcTypedInt(control, 1, kProcDword) && ok;
    ok = writeOriginalProcTypedInt(control + 0x68, 256, kProcDword) && ok;

    logInfo(
        "feature 508 spell shop item=%d payload=%s target=0x%X control=0x%llX helper=sub_1855DC chain=qword_3642F8 bytes=%zu/0x24 trailer=(1,256) ok=%d",
        request.int_value,
        text,
        target_ea,
        static_cast<unsigned long long>(control),
        written,
        ok ? 1 : 0);
    return true;
}

bool applyFeature803ColorPreset(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 803) {
        return false;
    }

    const Feature803ColorPreset *preset = findFeature803ColorPreset(request.int_value);
    if (preset == nullptr) {
        logWarn(
            "feature 803 color preset invalid item=%d target=0x%X switch=0x1C4B58 valid=0..3",
            request.int_value,
            target_ea);
        return true;
    }

    {
        std::lock_guard<std::mutex> lock(g_feature_803_lock);
        g_feature_803_color_count = preset->count;
        g_feature_803_color_words = preset->words;
    }

    logInfo(
        "feature 803 color preset item=%d color=%s target=0x%X branch=0x%X qword_364720=%llu xmmword_364730 words=%zu",
        request.int_value,
        preset->name,
        target_ea,
        preset->branch_ea,
        static_cast<unsigned long long>(preset->count),
        preset->words.size());
    return true;
}

bool applyFeature804811PatchPayloadWrite(const ChangeRequest &request, std::uint32_t target_ea) {
    const ScenePatchRange *range = findPatchPayloadWriteRange(request.feature_num);
    if (range == nullptr) {
        return false;
    }

    constexpr std::size_t kPayloadSize = 0x30;
    constexpr int kProcByte = 1;

    if (request.int_value < 1 || request.int_value > range->item_count) {
        logWarn(
            "feature %d patch payload write invalid item=%d target=0x%X switch=0x%X count=%d",
            request.feature_num,
            request.int_value,
            target_ea,
            range->switch_ea,
            range->item_count);
        return true;
    }

    std::uintptr_t destination = 0;
    if (!resolvePointerChain(
            kFeature804811PatchPayloadChain,
            static_cast<int>(sizeof(kFeature804811PatchPayloadChain) / sizeof(kFeature804811PatchPayloadChain[0])),
            &destination)) {
        logWarn(
            "feature %d patch payload write resolve failed item=%d target=0x%X chain=unk_364188 {0x0473D660,0x7D78,0x398} helper=sub_16D388",
            request.feature_num,
            request.int_value,
            target_ea);
        return true;
    }

    const int patch_index = range->first_patch_index + request.int_value - 1;
    std::uint32_t patch_id = 0;
    const char *payload_text = findGamePatchPayloadByIndex(patch_index, &patch_id);
    if (payload_text == nullptr) {
        logWarn(
            "feature %d patch payload write missing patch index=%d item=%d target=0x%X switch=0x%X",
            request.feature_num,
            patch_index,
            request.int_value,
            target_ea,
            range->switch_ea);
        return true;
    }

    std::array<std::uint8_t, kPayloadSize> payload{};
    const std::size_t copy_len = std::min(std::strlen(payload_text), payload.size());
    std::memcpy(payload.data(), payload_text, copy_len);

    bool ok = true;
    std::size_t written = 0;
    for (std::size_t i = 0; i < payload.size(); ++i) {
        if (writeOriginalProcTypedInt(destination + i, payload[i], kProcByte)) {
            ++written;
        } else {
            ok = false;
        }
    }

    logInfo(
        "feature %d patch payload write item=%d index=%d id=0x%08X payload=%s target=0x%X destination=0x%llX bytes=%zu/0x30 type=1 switch=0x%X helper=sub_16D388 chain=unk_364188 ok=%d",
        request.feature_num,
        request.int_value,
        patch_index,
        patch_id,
        payload_text,
        target_ea,
        static_cast<unsigned long long>(destination),
        written,
        range->switch_ea,
        ok ? 1 : 0);
    return true;
}

bool applyPointerChainIntWriteCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const PointerChainIntWriteCase *item = findPointerChainIntWriteCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    std::uintptr_t base_address = 0;
    if (!resolvePointerChain(item->chain, item->chain_count, &base_address)) {
        logWarn(
            "pointer-chain int write failed feature=%d target=0x%X final_offset=0x%llX type=0x%X error=resolve-failed",
            request.feature_num,
            target_ea,
            static_cast<unsigned long long>(item->final_offset),
            item->proc_type);
        return true;
    }

    const std::uintptr_t address = base_address + item->final_offset;
    if (!writeOriginalProcTypedInt(address, request.int_value, item->proc_type)) {
        logWarn(
            "pointer-chain int write failed feature=%d target=0x%X address=0x%llX value=%d type=0x%X",
            request.feature_num,
            target_ea,
            static_cast<unsigned long long>(address),
            request.int_value,
            item->proc_type);
        return true;
    }

    logInfo(
        "pointer-chain int write feature=%d target=0x%X address=0x%llX final_offset=0x%llX value=%d type=0x%X",
        request.feature_num,
        target_ea,
        static_cast<unsigned long long>(address),
        static_cast<unsigned long long>(item->final_offset),
        request.int_value,
        item->proc_type);
    return true;
}

bool applyStateOnlyCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const StateOnlyCase *item = findStateOnlyCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    item->state->store(static_cast<std::int64_t>(request.int_value));
    logInfo(
        "state-only feature=%d target=0x%X qword=0x%X value=%lld",
        request.feature_num,
        target_ea,
        item->state_qword_ea,
        static_cast<long long>(request.int_value));
    return true;
}

bool applyFeature436PatternSelector(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 436) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > 7) {
        logWarn(
            "feature 436 pattern selector invalid item=%d target=0x%X switch=0x1C1234",
            request.int_value,
            target_ea);
        return true;
    }

    g_feature_436_pattern_selector.store(request.int_value);
    logInfo(
        "feature 436 pattern selector item=%d target=0x%X state_qword=0x37BBB8",
        request.int_value,
        target_ea);
    return true;
}

bool writeFeature301Position(std::uintptr_t position, const Feature301RoutePoint &point) {
    char text[32]{};
    std::snprintf(text, sizeof(text), "%.12f", point.x);
    const bool x_ok = writeOriginalProcType16Text(position, text);
    std::snprintf(text, sizeof(text), "%.12f", point.y + 1.0);
    const bool y_ok = writeOriginalProcType16Text(position + 4, text);
    std::snprintf(text, sizeof(text), "%.12f", point.z);
    const bool z_ok = writeOriginalProcType16Text(position + 8, text);
    return x_ok && y_ok && z_ok;
}

void runFeature301HomeRouteWorker(std::uint32_t target_ea) {
    std::uintptr_t position = 0;
    if (!resolvePointerChain(kAvatarPositionChain, 4, &position)) {
        logWarn(
            "feature 301 home-route avatar resolve failed target=0x%X source_chain=qword_364068 worker=sub_1871F4",
            target_ea);
        return;
    }

    std::size_t loops = 0;
    while (g_feature_301_enabled.load()) {
        std::size_t written = 0;
        std::size_t failed = 0;
        for (std::size_t index = 0;
             index < sizeof(kFeature301HomeRoute) / sizeof(kFeature301HomeRoute[0]) &&
             g_feature_301_enabled.load();
             ++index) {
            if (writeFeature301Position(position, kFeature301HomeRoute[index])) {
                ++written;
            } else {
                ++failed;
            }
            std::this_thread::sleep_for(std::chrono::microseconds(0x39FBC0));
        }

        ++loops;
        logInfo(
            "feature 301 home-route loop completed target=0x%X loop=%zu position=0x%llX written=%zu failed=%zu table=unk_365690 points=253 worker=sub_1871F4 state_byte=0x385534 gate_byte=0x385700",
            target_ea,
            loops,
            static_cast<unsigned long long>(position),
            written,
            failed);

        if (!g_feature_301_enabled.load()) {
            break;
        }
        std::this_thread::sleep_for(std::chrono::microseconds(0xB71B00));
    }
}

bool applyFeature301HomeRoute(JNIEnv *env, const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 301) {
        return false;
    }

    g_feature_301_enabled.store(request.bool_value);
    if (!g_feature_301_gate.load()) {
        g_feature_301_gate.store(true);
        logInfo(
            "feature 301 home-route gate initialized target=0x%X state_byte=0x385534 gate_byte=0x385700 enabled=%d",
            target_ea,
            request.bool_value ? 1 : 0);
        return true;
    }

    if (!request.bool_value) {
        logInfo(
            "feature 301 home-route disabled target=0x%X state_byte=0x385534 gate_byte=0x385700 worker=sub_1871F4",
            target_ea);
        return true;
    }

    showToast(env, request.context, u8"旅人切记！\n⚠️禁阁终点请手动关闭⚠️", true);
    logInfo(
        "feature 301 home-route enabled target=0x%X wrapper=0x1C48A8 worker=sub_1871F4 table=unk_365690 points=253 source_chain=qword_364068 sleep_us=0x39FBC0/0xB71B00 prompt=0x2F07C7 key=0x295D50",
        target_ea);
    std::thread([target_ea]() {
        runFeature301HomeRouteWorker(target_ea);
    }).detach();
    return true;
}

bool writeFeature302Position(std::uintptr_t position, const Feature302RouteStep &step) {
    char text[32]{};
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(step.x));
    const bool x_ok = writeOriginalProcType16Text(position, text);
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(step.y));
    const bool y_ok = writeOriginalProcType16Text(position + 4, text);
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(step.z));
    const bool z_ok = writeOriginalProcType16Text(position + 8, text);
    return x_ok && y_ok && z_ok;
}

bool writeCandleRoutePosition(std::uintptr_t position, const CandleRoutePoint &point, float y_delta) {
    char text[32]{};
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(point.x));
    const bool x_ok = writeOriginalProcType16Text(position, text);
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(point.y + y_delta));
    const bool y_ok = writeOriginalProcType16Text(position + 4, text);
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(point.z));
    const bool z_ok = writeOriginalProcType16Text(position + 8, text);
    return x_ok && y_ok && z_ok;
}

bool applyFeature920921CandleRoute(JNIEnv *env, const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 920 && request.feature_num != 921) {
        return false;
    }

    std::uint32_t level_id = 0;
    if (!readCurrentLevelId(&level_id)) {
        logWarn(
            "feature %d candle route level resolve failed target=0x%X source_chain=qword_3640B8",
            request.feature_num,
            target_ea);
        showToast(env, request.context, u8"当前地图未录入", true);
        return true;
    }

    const std::uint32_t previous_level_id = g_feature_920_921_level_id.load();
    if (previous_level_id != level_id) {
        g_feature_920_921_level_id.store(level_id);
        g_feature_920_921_candle_index.store(1);
    }

    const CandleRoute *route = findCandleRouteByLevelId(level_id);
    if (route == nullptr || route->point_count == 0) {
        if (request.feature_num == 921) {
            g_feature_920_921_candle_index.store(1);
        }
        logWarn(
            "feature %d candle route missing target=0x%X level_id=0x%08X table=qword_383FB0 routes=%zu title=警告 message=当前地图未录入",
            request.feature_num,
            target_ea,
            level_id,
            candleRouteCount());
        showToast(env, request.context, u8"当前地图未录入", true);
        return true;
    }

    const CandleRoutePoint *points = candleRoutePoints(*route);
    if (points == nullptr) {
        logWarn(
            "feature %d candle route points invalid target=0x%X level_id=0x%08X offset=%zu count=%zu",
            request.feature_num,
            target_ea,
            level_id,
            route->point_offset,
            route->point_count);
        showToast(env, request.context, u8"当前地图未录入", true);
        return true;
    }

    std::uintptr_t position = 0;
    if (!resolvePointerChain(kAvatarPositionChain, 4, &position)) {
        logWarn(
            "feature %d candle route position resolve failed target=0x%X level_id=0x%08X source_chain=qword_364068",
            request.feature_num,
            target_ea,
            level_id);
        return true;
    }

    int index = g_feature_920_921_candle_index.load();
    const int route_count = static_cast<int>(route->point_count);
    float y_delta = 0.5f;
    if (request.feature_num == 920) {
        index = index <= 1 ? route_count : index - 1;
        y_delta = 1.0f;
    } else {
        if (index < 1 || index > route_count) {
            index = 1;
        }
    }

    const bool ok = writeCandleRoutePosition(position, points[index - 1], y_delta);
    char progress[64]{};
    std::snprintf(progress, sizeof(progress), u8"烛火进度 %d/%d", index, route_count);
    showToast(env, request.context, progress, true);

    if (request.feature_num == 921) {
        if (index == route_count) {
            showToast(env, request.context, u8"最后一处烛火•请手动切图", true);
        }
        const int next_index = route_count >= index + 1 ? index + 1 : 1;
        g_feature_920_921_candle_index.store(next_index);
    } else {
        g_feature_920_921_candle_index.store(index);
    }

    logInfo(
        "feature %d candle route target=0x%X level_id=0x%08X index=%d/%d position=0x%llX y_delta=%.1f ok=%d table=qword_383FB0 source_chain=qword_3640B8/qword_364068 title=烛火进度",
        request.feature_num,
        target_ea,
        level_id,
        index,
        route_count,
        static_cast<unsigned long long>(position),
        static_cast<double>(y_delta),
        ok ? 1 : 0);
    return true;
}

bool writeFeature701704Position(std::uintptr_t position, const Feature701704Step &step) {
    char text[32]{};
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(step.x));
    const bool x_ok = writeOriginalProcType16Text(position, text);
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(step.y));
    const bool y_ok = writeOriginalProcType16Text(position + 4, text);
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(step.z));
    const bool z_ok = writeOriginalProcType16Text(position + 8, text);
    return x_ok && y_ok && z_ok;
}

void runFeature701704SkyMapWorker(int feature_num, int item, int mode, std::uint32_t target_ea) {
    if (mode <= 0 || mode >= static_cast<int>(sizeof(kFeature701704Steps) / sizeof(kFeature701704Steps[0]))) {
        logWarn(
            "feature %d sky-map invalid mode=%d item=%d target=0x%X table=unk_366E98",
            feature_num,
            mode,
            item,
            target_ea);
        return;
    }

    std::uintptr_t position = 0;
    if (!resolvePointerChain(kAvatarPositionChain, 4, &position)) {
        logWarn(
            "feature %d sky-map position resolve failed item=%d mode=%d target=0x%X source_chain=qword_364068",
            feature_num,
            item,
            mode,
            target_ea);
        return;
    }

    const Feature701704Step &step = kFeature701704Steps[mode];
    GameMemoryPatchResult patch =
        applyGameMemoryPatchByIndex(step.patch_index);
    std::this_thread::sleep_for(std::chrono::seconds(5));
    const bool pos_ok = writeFeature701704Position(position, step);

    logInfo(
        "feature %d sky-map worker completed item=%d mode=%d target=0x%X patch=%d patch_ok=%d position=0x%llX coords=(%.6f,%.6f,%.6f) pos_ok=%d source_table=unk_366E98 helper=sub_185700 chain=qword_364068",
        feature_num,
        item,
        mode,
        target_ea,
        step.patch_index,
        patch.ok ? 1 : 0,
        static_cast<unsigned long long>(position),
        static_cast<double>(step.x),
        static_cast<double>(step.y),
        static_cast<double>(step.z),
        pos_ok ? 1 : 0);
}

bool applyFeature701704SkyMap(const ChangeRequest &request, std::uint32_t target_ea) {
    const Feature701704Range *range = findFeature701704Range(request.feature_num);
    if (range == nullptr) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > range->item_count) {
        logWarn(
            "feature %d sky-map invalid item=%d target=0x%X switch=0x%X count=%d",
            request.feature_num,
            request.int_value,
            target_ea,
            range->target_ea,
            range->item_count);
        return true;
    }

    const int mode = range->first_mode + request.int_value - 1;
    std::thread([feature_num = request.feature_num, item = request.int_value, mode, target_ea]() {
        runFeature701704SkyMapWorker(feature_num, item, mode, target_ea);
    }).detach();

    logInfo(
        "feature %d sky-map worker started item=%d mode=%d target=0x%X switch=0x%X worker=sub_201768..sub_201FE8 helper=sub_185700 table=unk_366E98",
        request.feature_num,
        request.int_value,
        mode,
        target_ea,
        range->target_ea);
    return true;
}

bool writeFeature443ObjectPosition(std::uintptr_t object, float x, float y, float z) {
    char text[32]{};
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(x));
    const bool x_ok = writeOriginalProcType16Text(object + 0x40, text);
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(y));
    const bool y_ok = writeOriginalProcType16Text(object + 0x44, text);
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(z));
    const bool z_ok = writeOriginalProcType16Text(object + 0x48, text);
    return x_ok && y_ok && z_ok;
}

void runFeature443PlaceObjectsWorker(std::uint32_t target_ea) {
    std::uintptr_t list_base = 0;
    if (!resolvePointerChain(kFeature443PlaceableListChain, 3, &list_base)) {
        logWarn(
            "feature 443 place-objects list resolve failed target=0x%X chain=qword_3640E8",
            target_ea);
        return;
    }

    std::uint32_t raw_object_count = 0;
    if (!readU32Raw(list_base, &raw_object_count)) {
        logWarn(
            "feature 443 place-objects count read failed target=0x%X list_base=0x%llX chain=qword_3640E8",
            target_ea,
            static_cast<unsigned long long>(list_base));
        return;
    }
    const std::int32_t object_count = static_cast<std::int32_t>(raw_object_count);

    const std::int64_t loop_seconds = g_feature_440_loop_seconds.load();
    const auto started = std::chrono::steady_clock::now();
    const auto duration =
        std::chrono::nanoseconds(std::max<std::int64_t>(0, loop_seconds) * 1000000000LL);
    std::size_t attempted = 0;
    std::size_t written = 0;
    bool timed_out = false;

    while (std::chrono::steady_clock::now() - started < duration) {
        if (object_count < 1) {
            break;
        }

        for (std::int32_t i = 0; i < object_count; ++i) {
            if (std::chrono::steady_clock::now() - started >= duration) {
                timed_out = true;
                break;
            }

            GamePosition origin;
            if (!readOriginalAvatarPosition(&origin)) {
                logWarn(
                    "feature 443 place-objects avatar resolve failed target=0x%X chain=qword_364068",
                    target_ea);
                return;
            }

            std::uint64_t object_value = 0;
            const std::uintptr_t slot = list_base + 0x10 + static_cast<std::uintptr_t>(i) * 8;
            if (!readU64Raw(slot, &object_value)) {
                continue;
            }
            const std::uintptr_t object = static_cast<std::uintptr_t>(object_value & kPointerMask);
            if (object == 0) {
                continue;
            }

            const int pattern = static_cast<int>(g_feature_436_pattern_selector.load());
            const float height = static_cast<float>(g_feature_437_creature_height.load());
            const float radius = static_cast<float>(g_feature_438_creature_radius.load());
            const float density = static_cast<float>(g_feature_439_creature_density.load());
            float x = origin.x;
            float z = origin.z;
            if (!calculateFeature443Point(pattern, i, origin, radius, density, &x, &z)) {
                logWarn(
                    "feature 443 place-objects invalid pattern=%d density=%.6f target=0x%X state_qword=0x37BBB8",
                    pattern,
                    static_cast<double>(density),
                    target_ea);
                return;
            }

            ++attempted;
            if (writeFeature443ObjectPosition(object, x, origin.y + height, z)) {
                ++written;
            }

            if (g_feature_441_delay_place.load()) {
                std::this_thread::sleep_for(std::chrono::nanoseconds(300000000LL));
            }
        }

        if (timed_out) {
            break;
        }
    }

    logInfo(
        "feature 443 place-objects worker completed target=0x%X list_base=0x%llX count=%u attempted=%zu written=%zu pattern=%lld height=%lld radius=%lld density=%lld seconds=%lld delay=%d chain=qword_3640E8/qword_364068 worker=sub_2007D0 offsets=+0x40/+0x44/+0x48",
        target_ea,
        static_cast<unsigned long long>(list_base),
        raw_object_count,
        attempted,
        written,
        static_cast<long long>(g_feature_436_pattern_selector.load()),
        static_cast<long long>(g_feature_437_creature_height.load()),
        static_cast<long long>(g_feature_438_creature_radius.load()),
        static_cast<long long>(g_feature_439_creature_density.load()),
        static_cast<long long>(g_feature_440_loop_seconds.load()),
        g_feature_441_delay_place.load() ? 1 : 0);
}

bool applyFeature443PlaceObjects(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 443) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature443PlaceObjectsWorker(target_ea);
    }).detach();
    logInfo(
        "feature 443 place-objects worker started target=0x%X worker=sub_2007D0 chain=qword_3640E8 state=qword_37BB98/qword_37BBA0/qword_37BBA8/qword_37BBB0/qword_37BBB8 byte_3854B4",
        target_ea);
    return true;
}

std::size_t writeFeature302CandleSlots(std::uintptr_t slot_base) {
    std::size_t written = 0;
    for (int i = 0; i < kFeature302CandleSlotCount; ++i) {
        const std::uintptr_t address =
            slot_base + static_cast<std::uintptr_t>(i) * kFeature302CandleSlotStride;
        if (writeOriginalProcTypedInt(address, kFeature302CandleSlotValue, 4)) {
            ++written;
        }
    }
    return written;
}

void runFeature302FastRouteWorker(std::uint32_t target_ea) {
    std::uintptr_t position = 0;
    if (!resolvePointerChain(kAvatarPositionChain, 4, &position)) {
        logWarn("feature 302 fast route position resolve failed target=0x%X chain=qword_364068", target_ea);
        return;
    }

    std::uintptr_t candle_slots = 0;
    if (!resolvePointerChain(kFeature302CandleSlotChain, 3, &candle_slots)) {
        logWarn("feature 302 fast route candle slot resolve failed target=0x%X chain=qword_3641A0", target_ea);
        return;
    }

    bool ok = true;
    std::size_t total_slot_writes = 0;
    for (const Feature302RouteStep &step : kFeature302RouteSteps) {
        GameMemoryPatchResult patch =
            applyGameMemoryPatchByIndex(step.patch_index);
        ok = patch.ok && ok;
        std::this_thread::sleep_for(std::chrono::seconds(5));
        total_slot_writes += writeFeature302CandleSlots(candle_slots);
        ok = writeFeature302Position(position, step) && ok;
        std::this_thread::sleep_for(std::chrono::seconds(4));
    }

    GameMemoryPatchResult reset_patch =
        applyGameMemoryPatchByIndex(1);
    ok = reset_patch.ok && ok;

    logInfo(
        "feature 302 fast route worker completed target=0x%X position=0x%llX candle_slots=0x%llX steps=%zu slot_writes=%zu slot_value=28673 stride=112 reset_patch=1 ok=%d source_chain=qword_364068/qword_3641A0 worker=sub_1FE0AC",
        target_ea,
        static_cast<unsigned long long>(position),
        static_cast<unsigned long long>(candle_slots),
        sizeof(kFeature302RouteSteps) / sizeof(kFeature302RouteSteps[0]),
        total_slot_writes,
        ok ? 1 : 0);
}

bool applyFeature302FastRoute(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 302) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature302FastRouteWorker(target_ea);
    }).detach();

    logInfo(
        "feature 302 fast route worker started target=0x%X switch_case=1204 worker=sub_1FE0AC patches=15..19 reset=1 chain=qword_3641A0",
        target_ea);
    return true;
}

bool writeFeature309Instruction(std::uint32_t value, std::uint32_t target_ea, const char *stage) {
    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "feature 309 map-route instruction write failed target=0x%X stage=%s module=%s qword_364498=0x01CF02DC error=module-not-loaded",
            target_ea,
            stage ? stage : "",
            kBootloaderModule);
        return false;
    }

    const std::uintptr_t address = base + kFeature309InstructionOffset;
    const bool ok = writeOriginalProcTypedInt(address, static_cast<int>(value), 4);
    logInfo(
        "feature 309 map-route instruction stage=%s target=0x%X address=0x%llX qword_364498=0x01CF02DC value=0x%08X ok=%d helper=sub_16D270",
        stage ? stage : "",
        target_ea,
        static_cast<unsigned long long>(address),
        value,
        ok ? 1 : 0);
    return ok;
}

bool waitFeature309RouteTriggerClear(std::uintptr_t trigger, std::uint32_t target_ea) {
    const auto started = std::chrono::steady_clock::now();
    while (true) {
        std::uint32_t state = 0;
        if (!readU32Raw(trigger, &state)) {
            logWarn(
                "feature 309 map-route trigger read failed target=0x%X trigger=0x%llX helper=sub_16C56C",
                target_ea,
                static_cast<unsigned long long>(trigger));
            return false;
        }
        if (state == 0) {
            return true;
        }
        if (std::chrono::steady_clock::now() - started > std::chrono::seconds(30)) {
            logWarn(
                "feature 309 map-route trigger wait timeout target=0x%X trigger=0x%llX state=0x%08X helper=sub_16C56C",
                target_ea,
                static_cast<unsigned long long>(trigger),
                state);
            return false;
        }
        std::this_thread::yield();
    }
}

std::size_t writeFeature309RouteChunk(
    const Feature309MapRoute &route,
    std::size_t first_index,
    std::uintptr_t data,
    std::uint32_t target_ea,
    bool *ok) {
    std::size_t written = 0;
    for (std::size_t i = 0; i < kFeature309RouteChunkSize; ++i) {
        const std::size_t route_index = first_index + i;
        const int value = route_index < route.count ? route.values[route_index] : 0;
        if (writeOriginalProcTypedInt(data + i * sizeof(std::int32_t), value, 4)) {
            ++written;
        } else {
            if (ok) {
                *ok = false;
            }
            logWarn(
                "feature 309 map-route value write failed target=0x%X data=0x%llX route_index=%zu value=%d source=qword_37C390",
                target_ea,
                static_cast<unsigned long long>(data + i * sizeof(std::int32_t)),
                route_index,
                value);
        }
    }
    return written;
}

void showFeature309ProgressToast(
    int route_number,
    int route_total,
    std::uint32_t percent,
    std::uint32_t target_ea) {
    char text[128]{};
    std::snprintf(
        text,
        sizeof(text),
        "地图 %d/%d 进度: %u%%",
        route_number,
        route_total,
        percent);
    const std::string command =
        std::string("local e=game:eventBarn():AddEventByMetaName(\"DialogHintTimed\") if e then e:text(\"") +
        text +
        "\") e:lifeTime(4.0) e:Start(0) end";
    const bool ok = sendGameCommand(command);
    logInfo(
        "feature 309 map-route progress target=0x%X route=%d/%d percent=%u text=%s ok=%d dword_2F1C34",
        target_ea,
        route_number,
        route_total,
        percent,
        text,
        ok ? 1 : 0);
}

void runFeature309MapRouteWorker(std::uint32_t target_ea) {
    std::uintptr_t route_base = 0;
    if (!resolvePointerChain(kFeature309MapRouteChain, 3, &route_base)) {
        logWarn(
            "feature 309 map-route target resolve failed target=0x%X chain=qword_3640B8 [base+0x0473D660]+0x8520+0x630 worker=sub_1FE70C",
            target_ea);
        return;
    }

    bool ok = true;
    ok = writeFeature309Instruction(kFeature309InstructionStart, target_ea, "start") && ok;

    const std::uintptr_t data = route_base + kFeature309RouteDataOffset;
    const std::uintptr_t trigger = route_base + kFeature309RouteTriggerOffset;
    const int requested_count = static_cast<int>(g_route_map_count.load());
    const int route_total = std::max<int>(
        0,
        std::min<int>(
            requested_count,
            static_cast<int>(sizeof(kFeature309MapRoutes) / sizeof(kFeature309MapRoutes[0]))));

    std::size_t total_written = 0;
    int patch_hits = 0;
    int patch_misses = 0;
    for (int route_index = 0; route_index < route_total; ++route_index) {
        const Feature309MapRoute &route = kFeature309MapRoutes[route_index];
        if (route.count == 0 || route.values == nullptr) {
            continue;
        }

        int patch_index = -1;
        GameMemoryPatchResult patch =
            applyGameMemoryPatchById(
                static_cast<std::uint32_t>(route.values[0]));
        if (patch.ok || findGamePatchPayloadById(static_cast<std::uint32_t>(route.values[0]), &patch_index)) {
            ++patch_hits;
        } else {
            ++patch_misses;
            logWarn(
                "feature 309 map-route patch id missing target=0x%X route=%d/%d level_id=%d table=qword_384628 entries=0x61",
                target_ea,
                route_index + 1,
                route_total,
                route.values[0]);
        }

        std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));

        const std::size_t chunk_count = ((route.count - 1) >> 5) + 1;
        int threshold_index = 0;
        for (std::size_t chunk = 0; chunk < chunk_count; ++chunk) {
            const std::uint32_t percent =
                static_cast<std::uint32_t>(100 * (static_cast<int>(chunk) + 1) / static_cast<int>(chunk_count));
            if (threshold_index < static_cast<int>(sizeof(kFeature309ProgressThresholds) / sizeof(kFeature309ProgressThresholds[0])) &&
                percent >= kFeature309ProgressThresholds[threshold_index]) {
                showFeature309ProgressToast(
                    route_index + 1,
                    route_total,
                    kFeature309ProgressThresholds[threshold_index],
                    target_ea);
                ++threshold_index;
            }

            total_written += writeFeature309RouteChunk(
                route,
                chunk * kFeature309RouteChunkSize,
                data,
                target_ea,
                &ok);

            if (!writeOriginalProcTypedInt(trigger, 32, 0x20)) {
                ok = false;
                logWarn(
                    "feature 309 map-route trigger write failed target=0x%X trigger=0x%llX value=32 proc_type=0x20",
                    target_ea,
                    static_cast<unsigned long long>(trigger));
            }
            ok = waitFeature309RouteTriggerClear(trigger, target_ea) && ok;
        }

        std::this_thread::sleep_for(std::chrono::microseconds(0x1E8480));
    }

    ok = writeFeature309Instruction(kFeature309InstructionRestore, target_ea, "restore") && ok;
    GameMemoryPatchResult reset =
        applyGameMemoryPatchByIndex(1);
    ok = reset.ok && ok;

    logInfo(
        "feature 309 map-route worker completed target=0x%X base=0x%llX data=0x%llX trigger=0x%llX requested=%d routes=%d total_ids=%zu written=%zu patch_hits=%d patch_misses=%d reset_patch=1 ok=%d table=qword_37C390 worker=sub_1FE70C qword_37BBC0 qword_3640B8 qword_364498 sleep_us=0x4C4B40/0x1E8480",
        target_ea,
        static_cast<unsigned long long>(route_base),
        static_cast<unsigned long long>(data),
        static_cast<unsigned long long>(trigger),
        requested_count,
        route_total,
        static_cast<std::size_t>(11166),
        total_written,
        patch_hits,
        patch_misses,
        ok ? 1 : 0);
}

bool applyFeature309MapRoute(JNIEnv *env, const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 309) {
        return false;
    }

    showToast(env, request.context, u8"跑图开始-单人房间", true);
    std::thread([target_ea]() {
        runFeature309MapRouteWorker(target_ea);
    }).detach();
    logInfo(
        "feature 309 map-route worker started target=0x%X wrapper=0x1C18F8 worker=sub_1FE70C table=qword_37C390 routes=51 ids=11166 qword_37BBC0=%lld prompt=xmmword_3C7420",
        target_ea,
        static_cast<long long>(g_route_map_count.load()));
    return true;
}

bool writeFeature314DyeInstruction(std::uint32_t value, std::uint32_t target_ea, const char *stage) {
    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "feature 314 dye-route instruction write failed target=0x%X stage=%s module=%s qword_364498=0x01CF02DC error=module-not-loaded",
            target_ea,
            stage ? stage : "",
            kBootloaderModule);
        return false;
    }

    const std::uintptr_t address = base + kFeature309InstructionOffset;
    const bool ok = writeOriginalProcTypedInt(address, static_cast<int>(value), 4);
    logInfo(
        "feature 314 dye-route instruction stage=%s target=0x%X address=0x%llX qword_364498=0x01CF02DC value=0x%08X ok=%d helper=sub_16D270",
        stage ? stage : "",
        target_ea,
        static_cast<unsigned long long>(address),
        value,
        ok ? 1 : 0);
    return ok;
}

void sendFeature314DialogHint(const char *text, float seconds, std::uint32_t target_ea, const char *source) {
    if (text == nullptr) {
        return;
    }

    char command[512];
    std::snprintf(
        command,
        sizeof(command),
        "local e = game:eventBarn():AddEventByMetaName(\"DialogHintTimed\") "
        "if e then e:text(\"%s\") e:lifeTime(%f) e:Start(0) end",
        text,
        static_cast<double>(seconds));
    const bool ok = sendGameCommand(command);
    logInfo(
        "feature 314 dye-route DialogHintTimed target=0x%X source=%s seconds=%.1f text=%s ok=%d helper=sub_180BD0",
        target_ea,
        source ? source : "",
        static_cast<double>(seconds),
        text,
        ok ? 1 : 0);
}

bool waitFeature314DyeRouteTriggerClear(std::uintptr_t trigger, std::uint32_t target_ea) {
    const auto started = std::chrono::steady_clock::now();
    while (true) {
        std::uint32_t state = 0;
        if (!readU32Raw(trigger, &state)) {
            logWarn(
                "feature 314 dye-route trigger read failed target=0x%X trigger=0x%llX helper=sub_16C56C",
                target_ea,
                static_cast<unsigned long long>(trigger));
            return false;
        }
        if (state == 0) {
            return true;
        }
        if (std::chrono::steady_clock::now() - started > std::chrono::seconds(30)) {
            logWarn(
                "feature 314 dye-route trigger wait timeout target=0x%X trigger=0x%llX state=0x%08X helper=sub_16C56C",
                target_ea,
                static_cast<unsigned long long>(trigger),
                state);
            return false;
        }
        std::this_thread::yield();
    }
}

std::size_t writeFeature314DyeRouteChunk(
    const Feature314DyeRoute &route,
    std::size_t first_index,
    std::uintptr_t data,
    std::uint32_t target_ea,
    bool *ok) {
    std::size_t written = 0;
    for (std::size_t i = 0; i < kFeature314DyeRouteChunkSize; ++i) {
        const std::size_t route_index = first_index + i;
        const int value = route_index < route.count ? route.values[route_index] : 0;
        if (writeOriginalProcTypedInt(data + i * sizeof(std::int32_t), value, 4)) {
            ++written;
        } else {
            if (ok) {
                *ok = false;
            }
            logWarn(
                "feature 314 dye-route value write failed target=0x%X data=0x%llX route_index=%zu value=%d source=qword_384608",
                target_ea,
                static_cast<unsigned long long>(data + i * sizeof(std::int32_t)),
                route_index,
                value);
        }
    }
    return written;
}

void showFeature314DyeProgress(
    int route_number,
    int route_total,
    std::uint32_t percent,
    std::uint32_t target_ea) {
    char text[128]{};
    std::snprintf(
        text,
        sizeof(text),
        u8"地图 %d/%d 进度: %u%%",
        route_number,
        route_total,
        percent);
    sendFeature314DialogHint(text, 2.0f, target_ea, "xmmword_3C9200 dword_2F1C34");
}

void runFeature314DyeRouteWorker(std::uint32_t target_ea) {
    std::uintptr_t route_base = 0;
    if (!resolvePointerChain(kFeature309MapRouteChain, 3, &route_base)) {
        logWarn(
            "feature 314 dye-route target resolve failed target=0x%X chain=qword_3640B8 [base+0x0473D660]+0x8520+0x630 start_routine=loc_1FF294",
            target_ea);
        return;
    }

    bool ok = true;
    ok = writeFeature314DyeInstruction(kFeature309InstructionStart, target_ea, "start") && ok;

    constexpr int route_total =
        static_cast<int>(sizeof(kFeature314DyeRoutes) / sizeof(kFeature314DyeRoutes[0]));
    if (route_total == 0 ||
        kFeature314DyeRoutes[0].values == nullptr ||
        kFeature314DyeRoutes[0].count == 0) {
        ok = writeFeature314DyeInstruction(kFeature309InstructionRestore, target_ea, "restore-empty") && ok;
        GameMemoryPatchResult reset =
            applyGameMemoryPatchByIndex(1);
        ok = reset.ok && ok;
        logWarn(
            "feature 314 dye-route empty route table target=0x%X route_base=0x%llX table=qword_384608 reset_patch=1 ok=%d",
            target_ea,
            static_cast<unsigned long long>(route_base),
            ok ? 1 : 0);
        return;
    }

    const std::uintptr_t data = route_base + kFeature314DyeRouteDataOffset;
    const std::uintptr_t trigger = route_base + kFeature314DyeRouteTriggerOffset;
    std::size_t total_written = 0;
    std::size_t payload_ids = 0;
    int patch_hits = 0;
    int patch_misses = 0;

    for (int route_index = 0; route_index < route_total; ++route_index) {
        const Feature314DyeRoute &route = kFeature314DyeRoutes[route_index];
        if (route.values == nullptr || route.count == 0) {
            continue;
        }

        payload_ids += route.count;
        int patch_index = -1;
        GameMemoryPatchResult patch =
            applyGameMemoryPatchById(
                static_cast<std::uint32_t>(route.values[0]));
        if (patch.ok || findGamePatchPayloadById(static_cast<std::uint32_t>(route.values[0]), &patch_index)) {
            ++patch_hits;
        } else {
            ++patch_misses;
            logWarn(
                "feature 314 dye-route patch id missing target=0x%X route=%d/%d level_id=%d table=qword_384620/qword_384628 entries=0x61",
                target_ea,
                route_index + 1,
                route_total,
                route.values[0]);
        }

        std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));

        const std::size_t chunk_count = ((route.count - 1) >> 5) + 1;
        int threshold_index = 0;
        for (std::size_t chunk = 0; chunk < chunk_count; ++chunk) {
            const std::uint32_t percent =
                static_cast<std::uint32_t>(100 * (static_cast<int>(chunk) + 1) / static_cast<int>(chunk_count));
            if (threshold_index < static_cast<int>(sizeof(kFeature309ProgressThresholds) / sizeof(kFeature309ProgressThresholds[0])) &&
                percent >= kFeature309ProgressThresholds[threshold_index]) {
                showFeature314DyeProgress(
                    route_index + 1,
                    route_total,
                    kFeature309ProgressThresholds[threshold_index],
                    target_ea);
                ++threshold_index;
            }

            total_written += writeFeature314DyeRouteChunk(
                route,
                chunk * kFeature314DyeRouteChunkSize,
                data,
                target_ea,
                &ok);

            if (!writeOriginalProcTypedInt(trigger, 32, 0x20)) {
                ok = false;
                logWarn(
                    "feature 314 dye-route trigger write failed target=0x%X trigger=0x%llX value=32 proc_type=0x20 qword_3640B8",
                    target_ea,
                    static_cast<unsigned long long>(trigger));
            }
            ok = waitFeature314DyeRouteTriggerClear(trigger, target_ea) && ok;
        }

        std::this_thread::sleep_for(std::chrono::microseconds(0x1E8480));
    }

    ok = writeFeature314DyeInstruction(kFeature309InstructionRestore, target_ea, "restore") && ok;
    GameMemoryPatchResult reset =
        applyGameMemoryPatchByIndex(1);
    ok = reset.ok && ok;

    logInfo(
        "feature 314 dye-route worker completed target=0x%X route_base=0x%llX data=0x%llX trigger=0x%llX routes=%d total_ids=%zu payload_ids=%zu written=%zu patch_hits=%d patch_misses=%d reset_patch=1 ok=%d table=qword_384608 sources=xmmword_298D90..xmmword_298EE0 unk_2C6D40..unk_2D7028 worker=loc_1FF294 qword_3640B8 qword_364498 qword_384620 qword_384628 sub_16C56C sleep_us=0x4C4B40/0x1E8480",
        target_ea,
        static_cast<unsigned long long>(route_base),
        static_cast<unsigned long long>(data),
        static_cast<unsigned long long>(trigger),
        route_total,
        kFeature314DyeRouteTotalIds,
        payload_ids,
        total_written,
        patch_hits,
        patch_misses,
        ok ? 1 : 0);
}

bool applyFeature314DyeRoute(JNIEnv *env, const ChangeRequest &request, std::uint32_t target_ea) {
    (void)env;
    if (request.feature_num != 314) {
        return false;
    }

    sendFeature314DialogHint(u8"染料采集-单人房间", 3.0f, target_ea, "xmmword_3C74C0 prompt");
    std::thread([target_ea]() {
        runFeature314DyeRouteWorker(target_ea);
    }).detach();
    logInfo(
        "feature 314 dye-route worker started target=0x%X wrapper=0x1C2C48 primary_case=1216 start_routine=loc_1FF294 table=qword_384608 routes=%zu ids=%zu raw64_sha256=6345417984c0c7849c78ea04ce9f58bca2ea82375314bbc7ee95e0de970d1125 text_sha256=7e93c85ba9d710b1bcbeb3ed6985ae98837c76db2b288d01fe833d2b873708a9",
        target_ea,
        sizeof(kFeature314DyeRoutes) / sizeof(kFeature314DyeRoutes[0]),
        kFeature314DyeRouteTotalIds);
    return true;
}

void sendFeature360DialogHintImpl(const char *text, float seconds, std::uint32_t target_ea) {
    if (text == nullptr) {
        return;
    }

    char command[512];
    std::snprintf(
        command,
        sizeof(command),
        "local e = game:eventBarn():AddEventByMetaName(\"DialogHintTimed\") "
        "if e then e:text(\"%s\") e:lifeTime(%f) e:Start(0) end",
        text,
        static_cast<double>(seconds));
    const bool ok = sendGameCommand(command);
    logInfo(
        "feature 360 token-route DialogHintTimed target=0x%X seconds=%.1f text=%s ok=%d helper=sub_180BD0",
        target_ea,
        static_cast<double>(seconds),
        text,
        ok ? 1 : 0);
}

#define sendFeature360DialogHint(text, seconds, target_ea, ...) \
    sendFeature360DialogHintImpl(text, seconds, target_ea)

bool waitFeature360RouteTriggerClear(std::uintptr_t trigger, std::uint32_t target_ea) {
    const auto started = std::chrono::steady_clock::now();
    while (true) {
        std::uint32_t state = 0;
        if (!readU32Raw(trigger, &state)) {
            logWarn(
                "feature 360 token-route trigger read failed target=0x%X trigger=0x%llX helper=sub_16C56C",
                target_ea,
                static_cast<unsigned long long>(trigger));
            return false;
        }
        if (state == 0) {
            return true;
        }
        if (std::chrono::steady_clock::now() - started > std::chrono::seconds(30)) {
            logWarn(
                "feature 360 token-route trigger wait timeout target=0x%X trigger=0x%llX state=0x%08X helper=sub_16C56C",
                target_ea,
                static_cast<unsigned long long>(trigger),
                state);
            return false;
        }
        std::this_thread::yield();
    }
}

std::size_t writeFeature360RouteChunk(
    const Feature360TokenRoute &route,
    std::size_t first_index,
    std::uintptr_t data,
    std::uint32_t target_ea,
    bool *ok) {
    std::size_t written = 0;
    for (std::size_t i = 0; i < kFeature360RouteChunkSize; ++i) {
        const std::size_t route_index = first_index + i;
        const int value = route_index < route.count ? route.values[route_index] : 0;
        if (writeOriginalProcTypedInt(data + i * sizeof(std::int32_t), value, 4)) {
            ++written;
        } else {
            if (ok) {
                *ok = false;
            }
            logWarn(
                "feature 360 token-route value write failed target=0x%X data=0x%llX route_index=%zu value=%d",
                target_ea,
                static_cast<unsigned long long>(data + i * sizeof(std::int32_t)),
                route_index,
                value);
        }
    }
    return written;
}

void showFeature360RouteProgress(
    const char *format,
    int token_id,
    int percent,
    std::uint32_t target_ea) {
    char text[128]{};
    if (token_id > 0) {
        std::snprintf(text, sizeof(text), format, token_id, percent);
    } else {
        std::snprintf(text, sizeof(text), format, percent);
    }
    sendFeature360DialogHint(text, 1.5f, target_ea);
}

std::size_t streamFeature360RouteImpl(
    const Feature360TokenRoute &route,
    std::uintptr_t route_base,
    const char *progress_format,
    int token_id,
    std::uint32_t target_ea,
    bool *ok) {
    if (route.values == nullptr || route.count == 0) {
        return 0;
    }

    const std::uintptr_t data = route_base + kFeature360RouteDataOffset;
    const std::uintptr_t trigger = route_base + kFeature360RouteTriggerOffset;
    const std::size_t chunk_count = ((route.count - 1) >> 5) + 1;
    std::size_t written = 0;

    for (std::size_t chunk = 0; chunk < chunk_count; ++chunk) {
        written += writeFeature360RouteChunk(
            route,
            chunk * kFeature360RouteChunkSize,
            data,
            target_ea,
            ok);

        if (!writeOriginalProcTypedInt(trigger, 32, 0x20)) {
            if (ok) {
                *ok = false;
            }
            logWarn(
                "feature 360 token-route trigger write failed target=0x%X trigger=0x%llX value=32 proc_type=0x20",
                target_ea,
                static_cast<unsigned long long>(trigger));
        }
        if (!waitFeature360RouteTriggerClear(trigger, target_ea)) {
            if (ok) {
                *ok = false;
            }
        }

        const int percent = static_cast<int>(100 * (chunk + 1) / chunk_count);
        showFeature360RouteProgress(progress_format, token_id, percent, target_ea);
    }

    return written;
}

#define streamFeature360Route(route, route_base, progress_format, token_id, target_ea, source, ok) \
    streamFeature360RouteImpl(route, route_base, progress_format, token_id, target_ea, ok)

int feature360DailyDateIndex() {
    std::time_t now = std::time(nullptr);
    std::tm *tm_now = std::localtime(&now);
    if (tm_now == nullptr) {
        return -1;
    }

    const int day = tm_now->tm_mday;
    const int month = tm_now->tm_mon;
    const int adjusted_year = tm_now->tm_year + 1900 - (month < 2 ? 1 : 0);
    const int civil_month = month + (month < 2 ? 13 : 1);
    const int quarter_year = adjusted_year < 0 ? adjusted_year + 3 : adjusted_year;
    const int day_number =
        adjusted_year * 365 +
        day +
        (quarter_year >> 2) -
        adjusted_year / 100 +
        adjusted_year / 400 +
        ((153 * civil_month - 457) / 5);
    const int index = day_number - 740058;
    return (index >= 0 && index < static_cast<int>(sizeof(kFeature360DailyLevelIds) / sizeof(kFeature360DailyLevelIds[0])))
               ? index
               : -1;
}

bool applyFeature360LevelPatchImpl(std::int32_t level_id, std::uint32_t target_ea, bool *ok) {
    int patch_index = -1;
    GameMemoryPatchResult patch =
        applyGameMemoryPatchById(static_cast<std::uint32_t>(level_id));
    if (patch.ok) {
        return true;
    }

    if (findGamePatchPayloadById(static_cast<std::uint32_t>(level_id), &patch_index)) {
        if (ok) {
            *ok = false;
        }
        logWarn(
            "feature 360 token-route patch write failed target=0x%X level_id=%d index=%d error=%s",
            target_ea,
            level_id,
            patch_index,
            patch.error ? patch.error : "unknown");
        return true;
    }

    if (ok) {
        *ok = false;
    }
    logWarn(
        "feature 360 token-route patch id missing target=0x%X level_id=%d table=qword_384628 entries=0x61",
        target_ea,
        level_id);
    return false;
}

#define applyFeature360LevelPatch(level_id, target_ea, reason, ok) \
    applyFeature360LevelPatchImpl(level_id, target_ea, ok)

void runFeature360TokenRouteWorker(int item, std::uint32_t target_ea) {
    constexpr int route_count =
        static_cast<int>(sizeof(kFeature360TokenRoutes) / sizeof(kFeature360TokenRoutes[0]));
    if (item < 1 || item > route_count) {
        sendFeature360DialogHint(u8"无效的ID", 2.0f, target_ea, "qword_3B9540 invalid-id");
        logWarn(
            "feature 360 token-route invalid item=%d target=0x%X table=qword_37C3C0 count=%d worker=sub_183BA8",
            item,
            target_ea,
            route_count);
        return;
    }

    sendFeature360DialogHint(u8"获取代币", 2.0f, target_ea, "qword_3B9558 start");

    std::uintptr_t route_base = 0;
    if (!resolvePointerChain(kFeature309MapRouteChain, 3, &route_base)) {
        logWarn(
            "feature 360 token-route target resolve failed target=0x%X item=%d chain=qword_3640B8 [base+0x0473D660]+0x8520+0x630 worker=sub_183BA8",
            target_ea,
            item);
        return;
    }

    const Feature360TokenRoute &selected = kFeature360TokenRoutes[item - 1];
    bool ok = true;
    std::size_t total_written = 0;

    applyFeature360LevelPatch(
        selected.values[0],
        target_ea,
        "feature 360 token-route selected qword_37C3C0 sub_183BA8",
        &ok);
    std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));

    if (item == 2) {
        total_written += streamFeature360Route(
            selected,
            route_base,
            u8"固定代币进度: %d%%",
            0,
            target_ea,
            "feature 360 fixed-token selected route qword_37C3C0",
            &ok);
        std::this_thread::sleep_for(std::chrono::microseconds(0x2DC6C0));

        const int daily_index = feature360DailyDateIndex();
        if (daily_index < 0) {
            sendFeature360DialogHint(u8"当天没有每日地图数据", 3.0f, target_ea, "xmmword_3B95F0 daily-date-miss");
            std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));
            GameMemoryPatchResult reset =
                applyGameMemoryPatchByIndex(1);
            ok = reset.ok && ok;
            logInfo(
                "feature 360 token-route worker completed target=0x%X item=%d route_base=0x%llX selected_ids=%zu daily_index=-1 written=%zu reset_patch=1 ok=%d table=qword_37C3C0 worker=sub_183BA8",
                target_ea,
                item,
                static_cast<unsigned long long>(route_base),
                selected.count,
                total_written,
                ok ? 1 : 0);
            return;
        }

        const std::int32_t daily_level_id = kFeature360DailyLevelIds[daily_index];
        sendFeature360DialogHint(u8"获取每日代币", 2.0f, target_ea, "qword_3B9598 daily-start");
        applyFeature360LevelPatch(
            daily_level_id,
            target_ea,
            "feature 360 daily-token date map xmmword_293AD0 sub_183BA8",
            &ok);
        std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));
        if (!resolvePointerChain(kFeature309MapRouteChain, 3, &route_base)) {
            logWarn(
                "feature 360 token-route daily target re-resolve failed target=0x%X item=%d daily_index=%d chain=qword_3640B8 worker=sub_183BA8",
                target_ea,
                item,
                daily_index);
            std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));
            GameMemoryPatchResult reset =
                applyGameMemoryPatchByIndex(1);
            ok = reset.ok && ok;
            logInfo(
                "feature 360 token-route worker completed target=0x%X item=%d route_base=0x0 selected_ids=%zu daily_index=%d daily_level_id=%d written=%zu reset_patch=1 ok=%d table=qword_37C3C0/unk_2D7B20 worker=sub_183BA8",
                target_ea,
                item,
                selected.count,
                daily_index,
                daily_level_id,
                total_written,
                ok ? 1 : 0);
            return;
        }
        total_written += streamFeature360Route(
            kFeature360DailyRouteRecord,
            route_base,
            u8"每日代币进度: %d%%",
            0,
            target_ea,
            "feature 360 daily-token route unk_2D7B20",
            &ok);
        std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));
        GameMemoryPatchResult reset =
            applyGameMemoryPatchByIndex(1);
        ok = reset.ok && ok;
        logInfo(
            "feature 360 token-route worker completed target=0x%X item=%d route_base=0x%llX selected_ids=%zu daily_index=%d daily_level_id=%d daily_ids=%zu written=%zu reset_patch=1 ok=%d table=qword_37C3C0/unk_2D7B20 worker=sub_183BA8",
            target_ea,
            item,
            static_cast<unsigned long long>(route_base),
            selected.count,
            daily_index,
            daily_level_id,
            kFeature360DailyRouteRecord.count,
            total_written,
            ok ? 1 : 0);
        return;
    }

    total_written += streamFeature360Route(
        selected,
        route_base,
        u8"代币 %d 进度: %d%%",
        item,
        target_ea,
        "feature 360 token selected route qword_37C3C0",
        &ok);
    std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));
    GameMemoryPatchResult reset =
        applyGameMemoryPatchByIndex(1);
    ok = reset.ok && ok;
    logInfo(
        "feature 360 token-route worker completed target=0x%X item=%d route_base=0x%llX selected_ids=%zu written=%zu reset_patch=1 ok=%d table=qword_37C3C0 worker=sub_183BA8 wrapper=sub_1FEDDC",
        target_ea,
        item,
        static_cast<unsigned long long>(route_base),
        selected.count,
        total_written,
        ok ? 1 : 0);
}

bool applyFeature360TokenRoute(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 360) {
        return false;
    }

    const int item = request.int_value;
    std::thread([item, target_ea]() {
        runFeature360TokenRouteWorker(item, target_ea);
    }).detach();
    logInfo(
        "feature 360 token-route worker started target=0x%X item=%d wrapper=0x1C0D64 thread=sub_1FEDDC worker=sub_183BA8 table=qword_37C3C0 records=3",
        target_ea,
        item);
    return true;
}

bool writeFeature361FixedBytes(
    std::uintptr_t address,
    const char *text,
    std::uint32_t target_ea,
    const char *field_name) {
    if (text == nullptr) {
        return false;
    }

    std::array<std::uint8_t, kFeature361PayloadLength> payload{};
    const std::size_t copy_length =
        std::min<std::size_t>(std::strlen(text), kFeature361PayloadLength);
    std::memcpy(payload.data(), text, copy_length);

    bool ok = true;
    for (std::size_t i = 0; i < payload.size(); ++i) {
        if (!writeOriginalProcTypedInt(
                address + static_cast<std::uintptr_t>(i),
                payload[i],
                1)) {
            ok = false;
        }
    }

    if (!ok) {
        logWarn(
            "feature 361 hangout payload write failed target=0x%X address=0x%llX field=%s text=%s len=0x30 helper=sub_184DE4/proc_mem_write_value",
            target_ea,
            static_cast<unsigned long long>(address),
            field_name ? field_name : "",
            text);
    }
    return ok;
}

bool triggerFeature361HangoutBatch(
    std::uintptr_t trigger,
    const Feature361HangoutRecord &record,
    int batch_index,
    int batch_count,
    std::uint32_t target_ea) {
    bool ok = true;
    if (!writeOriginalProcTypedInt(trigger, 32, 4)) {
        ok = false;
        logWarn(
            "feature 361 hangout trigger write failed target=0x%X trigger=0x%llX value=32 type=4 qword_364658",
            target_ea,
            static_cast<unsigned long long>(trigger));
    }

    const int percent = batch_count > 0 ? (100 * (batch_index + 1) / batch_count) : 100;
    char text[128]{};
    std::snprintf(
        text,
        sizeof(text),
        "%s 挂机进度: %d%%",
        record.display_name,
        percent);
    const std::string command =
        std::string("local e=game:eventBarn():AddEventByMetaName(\"DialogHintTimed\") if e then e:text(\"") +
        text +
        "\") e:lifeTime(1.5) e:Start(0) end";
    const bool prompt_ok = sendGameCommand(command);
    logInfo(
        "feature 361 hangout progress target=0x%X name=%s batch=%d/%d percent=%d text=%s ok=%d",
        target_ea,
        record.display_name,
        batch_index + 1,
        batch_count,
        percent,
        text,
        prompt_ok ? 1 : 0);
    return ok;
}

void runFeature361HangoutWorker(int item, std::uint32_t target_ea) {
    if (item < 1 || item > static_cast<int>(sizeof(kFeature361HangoutRecords) / sizeof(kFeature361HangoutRecords[0]))) {
        logWarn(
            "feature 361 hangout invalid item=%d target=0x%X table=qword_37C3D8 count=%zu",
            item,
            target_ea,
            sizeof(kFeature361HangoutRecords) / sizeof(kFeature361HangoutRecords[0]));
        return;
    }

    const Feature361HangoutRecord &record = kFeature361HangoutRecords[item - 1];
    bool ok = true;

    GameMemoryPatchResult patch =
        applyGameMemoryPatchById(
            static_cast<std::uint32_t>(record.level_id));
    ok = patch.ok && ok;
    std::this_thread::sleep_for(std::chrono::microseconds(0x2DC6C0));

    std::uintptr_t route_base = 0;
    if (!resolvePointerChain(kFeature309MapRouteChain, 3, &route_base)) {
        logWarn(
            "feature 361 hangout target resolve failed target=0x%X item=%d chain=qword_3640B8 [base+0x0473D660]+0x8520+0x630 worker=sub_184DE4",
            target_ea,
            item);
        applyGameMemoryPatchByIndex(1);
        return;
    }

    const std::uintptr_t event_payload_base = route_base + kFeature361EventBytesOffset;
    const std::uintptr_t social_payload_base = route_base + kFeature361SocialBytesOffset;
    for (std::size_t slot = 0; slot < kFeature361BatchSize; ++slot) {
        const std::uintptr_t slot_base =
            social_payload_base + static_cast<std::uintptr_t>(slot) * kFeature361RecordStride;
        ok = writeFeature361FixedBytes(
                 slot_base,
                 record.event_payload,
                 target_ea,
                 "event_payload") && ok;
        ok = writeFeature361FixedBytes(
                 event_payload_base,
                 record.social_payload,
                 target_ea,
                 "social_payload") && ok;
    }

    const int batch_count = ((record.count < 0 ? record.count + 31 : record.count) >> 5) + 1;
    int current_index = 0;
    for (int batch = 0; batch < batch_count; ++batch) {
        for (std::size_t slot = 0; slot < kFeature361BatchSize; ++slot) {
            const std::uintptr_t slot_base =
                social_payload_base + static_cast<std::uintptr_t>(slot) * kFeature361RecordStride;
            ok = writeOriginalProcTypedInt(
                     slot_base + kFeature361RecordFlagOffset0,
                     49,
                     4) && ok;
            ok = writeOriginalProcTypedInt(
                     slot_base + kFeature361RecordFlagOffset1,
                     48,
                     4) && ok;
            ok = writeOriginalProcType32Int64(
                     slot_base + kFeature361RecordEventPointerOffset,
                     static_cast<std::int64_t>(event_payload_base)) && ok;
            ok = writeOriginalProcTypedInt(
                     slot_base + kFeature361RecordIndexOffset,
                     current_index,
                     4) && ok;
            ++current_index;
        }

        const std::uintptr_t trigger = social_payload_base + kFeature361HangoutTriggerOffset;
        ok = triggerFeature361HangoutBatch(trigger, record, batch, batch_count, target_ea) && ok;
        std::this_thread::sleep_for(std::chrono::microseconds(0x2DC6C0));
    }

    GameMemoryPatchResult reset =
        applyGameMemoryPatchByIndex(1);
    ok = reset.ok && ok;
    logInfo(
        "feature 361 hangout worker completed target=0x%X item=%d level_id=%d display=%s event=%s social=%s count=%d batches=%d route_base=0x%llX event_payload=0x%llX social_payload=0x%llX trigger_offset=qword_364658/0x700 qword_364650=0x150 table=qword_37C3D8 worker=sub_184DE4 ok=%d",
        target_ea,
        item,
        record.level_id,
        record.display_name,
        record.event_payload,
        record.social_payload,
        record.count,
        batch_count,
        static_cast<unsigned long long>(route_base),
        static_cast<unsigned long long>(event_payload_base),
        static_cast<unsigned long long>(social_payload_base),
        ok ? 1 : 0);
}

bool applyFeature361Hangout(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 361) {
        return false;
    }

    const int item = request.int_value;
    std::thread([item, target_ea]() {
        runFeature361HangoutWorker(item, target_ea);
    }).detach();
    logInfo(
        "feature 361 hangout worker started target=0x%X item=%d wrapper=0x1C49B4 worker=sub_184DE4 table=qword_37C3D8 records=2",
        target_ea,
        item);
    return true;
}

bool writeFeature315SacrificeInstruction(std::uint32_t value, std::uint32_t target_ea, const char *stage) {
    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "feature 315 auto-sacrifice instruction write failed target=0x%X stage=%s module=%s qword_364538[0]=0x02C9E514 error=module-not-loaded",
            target_ea,
            stage ? stage : "",
            kBootloaderModule);
        return false;
    }

    const std::uintptr_t address = base + kFeature315SacrificeInstructionOffset;
    const bool ok = writeOriginalProcTypedInt(address, static_cast<int>(value), 4);
    logInfo(
        "feature 315 auto-sacrifice instruction stage=%s target=0x%X address=0x%llX qword_364538[0]=0x02C9E514 value=0x%08X ok=%d helper=sub_16D270",
        stage ? stage : "",
        target_ea,
        static_cast<unsigned long long>(address),
        value,
        ok ? 1 : 0);
    return ok;
}

void sendFeature315DialogHintImpl(const char *text, float seconds, std::uint32_t target_ea) {
    if (text == nullptr) {
        return;
    }

    char command[512];
    std::snprintf(
        command,
        sizeof(command),
        "local e = game:eventBarn():AddEventByMetaName(\"DialogHintTimed\") "
        "if e then e:text(\"%s\") e:lifeTime(%f) e:Start(0) end",
        text,
        static_cast<double>(seconds));
    const bool ok = sendGameCommand(command);
    logInfo(
        "feature 315 auto-sacrifice DialogHintTimed target=0x%X seconds=%.1f text=%s ok=%d",
        target_ea,
        static_cast<double>(seconds),
        text,
        ok ? 1 : 0);
}

#define sendFeature315DialogHint(text, seconds, target_ea, source) \
    sendFeature315DialogHintImpl(text, seconds, target_ea)

void runFeature315AutoSacrificeWorker(std::uint32_t target_ea) {
    bool ok = true;

    GameMemoryPatchResult patch88 =
        applyGameMemoryPatchByIndex(88);
    ok = patch88.ok && ok;
    std::this_thread::sleep_for(std::chrono::microseconds(0x2DC6C0));

    ok = writeFeature315SacrificeInstruction(
             kFeature315SacrificeInstructionEnabled,
             target_ea,
             DIAG_TEXT("enable-wing-light-drop")) && ok;
    std::this_thread::sleep_for(std::chrono::microseconds(0x5B8D80));

    GameApiSession session = gameApiSessionFromMemory();
    GameApiResult drop = dropAllWingBuffs(session);
    ok = drop.ok && ok;
    if (!drop.ok) {
        logWarn(
            "feature 315 auto-sacrifice sub_195B04 drop-wing-buffs failed target=0x%X status=%d error=%s body_len=%zu",
            target_ea,
            drop.http_status,
            drop.error.c_str(),
            drop.body.size());
    }

    GameMemoryPatchResult patch91 =
        applyGameMemoryPatchByIndex(91);
    ok = patch91.ok && ok;

    ok = writeFeature315SacrificeInstruction(
             kFeature315SacrificeInstructionRestored,
             target_ea,
             DIAG_TEXT("restore-wing-light-load")) && ok;
    std::this_thread::sleep_for(std::chrono::microseconds(0xB71B00));

    sendFeature315DialogHint(u8"等待完成好嘛?", 3.0f, target_ea, "qword_3C9228");
    std::this_thread::sleep_for(std::chrono::microseconds(0x2DC6C0));

    GameMemoryPatchResult patch92 =
        applyGameMemoryPatchByIndex(92);
    ok = patch92.ok && ok;
    std::this_thread::sleep_for(std::chrono::microseconds(0x5B8D80));

    sendFeature315DialogHint(u8"献祭完成", 3.0f, target_ea, "qword_3C9248");

    logInfo(
        "feature 315 auto-sacrifice worker completed target=0x%X ok=%d worker=sub_1FF7F0 patches=88,91,92 qword_364538[0]=0x02C9E514 sub_195B04_equivalent=dropAllWingBuffs",
        target_ea,
        ok ? 1 : 0);
}

bool applyFeature315AutoSacrifice(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 315) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature315AutoSacrificeWorker(target_ea);
    }).detach();
    logInfo("feature 315 auto-sacrifice worker started target=0x%X wrapper=0x1C1030 worker=sub_1FF7F0", target_ea);
    return true;
}

bool resolveFeature316317WingList(std::uintptr_t *wing_list) {
    return resolvePointerChain(kFeature316317WingListChain, 3, wing_list);
}

bool readFeature316317WingCount(std::uintptr_t wing_list, int *count) {
    return readU32AsInt(wing_list + kFeature316317WingCountOffset, count);
}

bool readFeature316317WingListAndCount(std::uintptr_t *wing_list, int *count) {
    if (!wing_list || !count || !resolveFeature316317WingList(wing_list)) {
        return false;
    }
    return readFeature316317WingCount(*wing_list, count);
}

bool writeFeature316317FloatText(std::uintptr_t address, float value) {
    char text[32]{};
    std::snprintf(text, sizeof(text), "%.12f", static_cast<double>(value));
    return writeOriginalProcType16Text(address, text);
}

bool writeFeature316317Position(std::uintptr_t address, const GamePosition &position) {
    const bool x_ok = writeFeature316317FloatText(address, position.x);
    const bool y_ok = writeFeature316317FloatText(address + sizeof(float), position.y);
    const bool z_ok = writeFeature316317FloatText(address + sizeof(float) * 2, position.z);
    return x_ok && y_ok && z_ok;
}

std::vector<GamePosition> collectFeature316CurrentMapWingPositions(
    std::uintptr_t wing_list,
    int count,
    std::uint32_t target_ea) {
    std::vector<GamePosition> positions;
    if (count < 1) {
        return positions;
    }
    positions.reserve(static_cast<std::size_t>(count));

    for (int index = 0; index < count; ++index) {
        const std::uintptr_t coord =
            wing_list +
            kFeature316WingCoordBias +
            static_cast<std::uintptr_t>(index) * kFeature316317WingStride;
        GamePosition position;
        if (!readFloatRaw(coord, &position.x) ||
            !readFloatRaw(coord + sizeof(float), &position.y) ||
            !readFloatRaw(coord + sizeof(float) * 2, &position.z)) {
            logWarn(
                "feature 316 teleport-wings coordinate read failed target=0x%X index=%d coord=0x%llX helper=sub_1799C4",
                target_ea,
                index,
                static_cast<unsigned long long>(coord));
            continue;
        }
        position.y += 1.0f;
        position.from_memory = true;
        positions.push_back(position);
    }

    logInfo(
        "feature 316 teleport-wings collected target=0x%X wing_list=0x%llX count=%d collected=%zu vector=qword_37C2E0 helper=sub_1799C4 chain=qword_364158 offset=qword_364708 stride=qword_364710",
        target_ea,
        static_cast<unsigned long long>(wing_list),
        count,
        positions.size());
    return positions;
}

void runFeature316TeleportWingsWorker(std::uint32_t target_ea) {
    std::uintptr_t wing_list = 0;
    int count = 0;
    if (!readFeature316317WingListAndCount(&wing_list, &count)) {
        logWarn(
            "feature 316 teleport-wings resolve/count failed target=0x%X chain=qword_364158 count_offset=qword_364708 worker=sub_179C0C",
            target_ea);
        return;
    }

    std::vector<GamePosition> positions =
        collectFeature316CurrentMapWingPositions(wing_list, count, target_ea);
    if (positions.empty()) {
        logWarn(
            "feature 316 teleport-wings no positions target=0x%X wing_list=0x%llX count=%d worker=sub_179C0C",
            target_ea,
            static_cast<unsigned long long>(wing_list),
            count);
        return;
    }

    std::uintptr_t avatar_position = 0;
    if (!resolvePointerChain(kAvatarPositionChain, 4, &avatar_position)) {
        logWarn(
            "feature 316 teleport-wings avatar resolve failed target=0x%X source_chain=qword_364068 worker=sub_179C0C",
            target_ea);
        return;
    }

    std::size_t written = 0;
    std::size_t failed = 0;
    for (const GamePosition &position : positions) {
        if (writeFeature316317Position(avatar_position, position)) {
            ++written;
        } else {
            ++failed;
        }
        std::this_thread::sleep_for(std::chrono::microseconds(0x2DC6C0));
    }

    logInfo(
        "feature 316 teleport-wings worker completed target=0x%X wing_list=0x%llX avatar=0x%llX count=%d written=%zu failed=%zu y_delta=1.0 sleep_us=0x2DC6C0 chain=qword_364158/qword_364068 offset=qword_364708 stride=qword_364710 helper=sub_1799C4 worker=sub_179C0C",
        target_ea,
        static_cast<unsigned long long>(wing_list),
        static_cast<unsigned long long>(avatar_position),
        count,
        written,
        failed);
}

void runFeature317AbsorbWingsWorker(std::uint32_t target_ea) {
    std::uintptr_t wing_list = 0;
    int count = 0;
    if (!readFeature316317WingListAndCount(&wing_list, &count)) {
        logWarn(
            "feature 317 absorb-wings resolve/count failed target=0x%X chain=qword_364158 count_offset=qword_364708 worker=sub_179D64",
            target_ea);
        return;
    }

    std::uintptr_t avatar_position = 0;
    if (!resolvePointerChain(kAvatarPositionChain, 4, &avatar_position)) {
        logWarn(
            "feature 317 absorb-wings avatar resolve failed target=0x%X source_chain=qword_364068 worker=sub_179D64",
            target_ea);
        return;
    }

    GamePosition player;
    if (!readFloatRaw(avatar_position, &player.x) ||
        !readFloatRaw(avatar_position + sizeof(float), &player.y) ||
        !readFloatRaw(avatar_position + sizeof(float) * 2, &player.z)) {
        logWarn(
            "feature 317 absorb-wings avatar position read failed target=0x%X avatar=0x%llX worker=sub_179D64",
            target_ea,
            static_cast<unsigned long long>(avatar_position));
        return;
    }
    player.from_memory = true;

    std::size_t written = 0;
    std::size_t failed = 0;
    for (int index = 0; index < count; ++index) {
        const std::uintptr_t destination =
            wing_list -
            kFeature317WingButtonCoordBackstep +
            static_cast<std::uintptr_t>(index) * kFeature316317WingStride;
        if (writeFeature316317Position(destination, player)) {
            ++written;
        } else {
            ++failed;
        }
    }

    logInfo(
        "feature 317 absorb-wings worker completed target=0x%X wing_list=0x%llX avatar=0x%llX count=%d written=%zu failed=%zu dest_bias=-0x50 chain=qword_364158/qword_364068 offset=qword_364708 stride=qword_364710 worker=sub_179D64",
        target_ea,
        static_cast<unsigned long long>(wing_list),
        static_cast<unsigned long long>(avatar_position),
        count,
        written,
        failed);
}

bool applyFeature316317CurrentMapWings(
    JNIEnv *env,
    const ChangeRequest &request,
    std::uint32_t target_ea) {
    if (request.feature_num != 316 && request.feature_num != 317) {
        return false;
    }

    std::uintptr_t wing_list = 0;
    int count = 0;
    if (!readFeature316317WingListAndCount(&wing_list, &count) || count < 1) {
        const char *missing =
            "<font color='#FF0000'>未找到光翼数据，请检查！</font>";
        showHtmlToast(env, request.context, missing, true);
        logWarn(
            "feature %d current-map-wings missing data target=0x%X wing_list=0x%llX count=%d toast=unk_2A0881 chain=qword_364158 offset=qword_364708",
            request.feature_num,
            target_ea,
            static_cast<unsigned long long>(wing_list),
            count);
        return true;
    }

    std::string message;
    if (request.feature_num == 316) {
        message = "<font color='#00FF00'>";
        message = "当前地图共有：" + message + std::to_string(count) + "</font> 个光翼";
        showHtmlToast(env, request.context, message, true);
        std::thread([target_ea]() {
            runFeature316TeleportWingsWorker(target_ea);
        }).detach();
        logInfo(
            "feature 316 teleport-wings worker started target=0x%X wing_list=0x%llX count=%d wrapper=0x1C1444 worker=sub_179C0C start_routine=sub_1FFB0C toast_prefix=unk_2A08C3 toast_suffix=unk_2A08EF",
            target_ea,
            static_cast<unsigned long long>(wing_list),
            count);
    } else {
        message = "<font color='#00FF00'>";
        message = "共吸取：" + message + std::to_string(count) + "</font> 个光翼按钮";
        showHtmlToast(env, request.context, message, true);
        std::thread([target_ea]() {
            runFeature317AbsorbWingsWorker(target_ea);
        }).detach();
        logInfo(
            "feature 317 absorb-wings worker started target=0x%X wing_list=0x%llX count=%d wrapper=0x1C5020 worker=sub_179D64 start_routine=sub_1FFBAC toast_prefix=unk_2A0901 toast_suffix=unk_2A0924",
            target_ea,
            static_cast<unsigned long long>(wing_list),
            count);
    }

    return true;
}

bool readFeature321322EventPointer(
    std::uintptr_t event_slot_table,
    int slot_index,
    std::uintptr_t *event_pointer) {
    if (!event_pointer) {
        return false;
    }
    return readMaskedPointerAt(
        event_slot_table + static_cast<std::uintptr_t>(slot_index) * sizeof(std::uint64_t),
        event_pointer);
}

bool writeFixedStringEventPayload(
    std::uintptr_t event_pointer,
    const char *text,
    std::size_t length,
    std::uint32_t target_ea,
    const char *label) {
    if (event_pointer == 0 || text == nullptr || length == 0) {
        return false;
    }

    std::vector<std::uint8_t> payload(length, 0);
    const std::size_t copy_length = std::min<std::size_t>(std::strlen(text), length);
    std::memcpy(payload.data(), text, copy_length);

    bool ok = true;
    const std::uintptr_t payload_base = event_pointer + kFeature321322EventPayloadOffset;
    for (std::size_t i = 0; i < length; ++i) {
        const std::uint8_t byte = payload[i];
        if (!writeBytes(payload_base + static_cast<std::uintptr_t>(i), &byte, sizeof(byte))) {
            ok = false;
        }
    }

    if (!ok) {
        logWarn(
            "%s payload byte write failed target=0x%X event=0x%llX payload_base=0x%llX length=%zu offset=0x68 text=%s",
            label ? label : "feature 310 valley-race local event",
            target_ea,
            static_cast<unsigned long long>(event_pointer),
            static_cast<unsigned long long>(payload_base),
            length,
            text);
        return false;
    }

    return submitGameMemoryEventPointer(event_pointer, target_ea, label);
}

void runFeature310ValleyRaceWorker(std::uint32_t target_ea) {
    std::uintptr_t event_slot_table = 0;
    if (!resolvePointerChain(kFeature321322EventSlotChain, 3, &event_slot_table)) {
        logWarn(
            "feature 310 valley-race resolve failed target=0x%X event_chain=qword_3640A0 worker=sub_1FEC00",
            target_ea);
        return;
    }

    std::size_t submitted = 0;
    std::size_t failed = 0;
    for (const char *payload : kFeature310ValleyRacePayloads) {
        std::uintptr_t event_pointer = 0;
        if (!readFeature321322EventPointer(event_slot_table, kFeature310EventSlotIndex, &event_pointer) ||
            !writeFixedStringEventPayload(
                event_pointer,
                payload,
                kFeature310PayloadLength,
                target_ea,
                DIAG_TEXT("feature 310 valley-race local event"))) {
            ++failed;
            continue;
        }
        ++submitted;
    }

    logInfo(
        "feature 310 valley-race worker completed target=0x%X event_slot_table=0x%llX submitted=%zu failed=%zu payload_len=0x18 source_chain=qword_3640A0 slot=0x15 offset=qword_3646F8/0x68 globals=qword_37CDE0..qword_37CE08 worker=sub_1FEC00 helper=sub_178CCC",
        target_ea,
        static_cast<unsigned long long>(event_slot_table),
        submitted,
        failed);
}

bool applyFeature310ValleyRace(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 310) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature310ValleyRaceWorker(target_ea);
    }).detach();
    logInfo("feature 310 valley-race worker started target=0x%X wrapper=0x1C0AB8 worker=sub_1FEC00", target_ea);
    return true;
}

void runFeature311SeasonAncestorWorker(std::uint32_t target_ea) {
    std::uintptr_t event_slot_table = 0;
    if (!resolvePointerChain(kFeature321322EventSlotChain, 3, &event_slot_table)) {
        logWarn(
            "feature 311 season-ancestor resolve failed target=0x%X event_chain=qword_3640A0 worker=sub_1FEF74 helper=sub_186710",
            target_ea);
        return;
    }

    std::size_t submitted = 0;
    std::size_t failed = 0;
    for (const char *payload : kFeature311SeasonAncestorPayloads) {
        std::uintptr_t event_pointer = 0;
        if (!readFeature321322EventPointer(event_slot_table, kFeature311EventSlotIndex, &event_pointer) ||
            !writeFixedStringEventPayload(
                event_pointer,
                payload,
                kFeature311PayloadLength,
                target_ea,
                DIAG_TEXT("feature 311 season-ancestor local event"))) {
            ++failed;
            continue;
        }
        ++submitted;
    }

    logInfo(
        "feature 311 season-ancestor worker completed target=0x%X event_slot_table=0x%llX submitted=%zu failed=%zu payload_len=0x18 source_chain=qword_3640A0 slot=0x0E offset=qword_3646F8/0x68 globals=qword_37D140 worker=sub_1FEF74 helper=sub_186710 count=4",
        target_ea,
        static_cast<unsigned long long>(event_slot_table),
        submitted,
        failed);
}

bool applyFeature311SeasonAncestor(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 311) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature311SeasonAncestorWorker(target_ea);
    }).detach();
    logInfo("feature 311 season-ancestor worker started target=0x%X wrapper=0x1C5190 worker=sub_1FEF74 helper=sub_186710", target_ea);
    return true;
}

void runFeature312ResidentRevisitWorker(std::uint32_t target_ea) {
    std::uintptr_t event_slot_table = 0;
    if (!resolvePointerChain(kFeature321322EventSlotChain, 3, &event_slot_table)) {
        logWarn(
            "feature 312 resident-revisit resolve failed target=0x%X event_chain=qword_3640A0 worker=sub_1FF018 helper=sub_186600",
            target_ea);
        return;
    }

    std::size_t submitted = 0;
    std::size_t failed = 0;
    for (const char *payload : kFeature312ResidentRevisitPayloads) {
        std::uintptr_t event_pointer = 0;
        if (!readFeature321322EventPointer(event_slot_table, kFeature312EventSlotIndex, &event_pointer) ||
            !writeFixedStringEventPayload(
                event_pointer,
                payload,
                kFeature312PayloadLength,
                target_ea,
                DIAG_TEXT("feature 312 resident-revisit local event"))) {
            ++failed;
            continue;
        }
        ++submitted;
    }

    logInfo(
        "feature 312 resident-revisit worker completed target=0x%X event_slot_table=0x%llX submitted=%zu failed=%zu payload_len=0x18 source_chain=qword_3640A0 slot=0x0E offset=qword_3646F8/0x68 globals=qword_37D160 worker=sub_1FF018 helper=sub_186600 count=133",
        target_ea,
        static_cast<unsigned long long>(event_slot_table),
        submitted,
        failed);
}

bool applyFeature312ResidentRevisit(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 312) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature312ResidentRevisitWorker(target_ea);
    }).detach();
    logInfo("feature 312 resident-revisit worker started target=0x%X wrapper=0x1C52F0 worker=sub_1FF018 helper=sub_186600", target_ea);
    return true;
}

bool writeFeature313OfflineWingState(std::uint32_t value, std::uint32_t target_ea, const char *stage) {
    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "feature 313 offline-wing state write failed target=0x%X stage=%s module=%s qword_364558=0x0359ED30 error=module-not-loaded",
            target_ea,
            stage ? stage : "",
            kBootloaderModule);
        return false;
    }

    const std::uintptr_t address = base + kFeature313OfflineWingStateOffset;
    const bool ok = writeOriginalProcTypedInt(address, static_cast<int>(value), 4);
    logInfo(
        "feature 313 offline-wing state stage=%s target=0x%X address=0x%llX qword_364558=0x0359ED30 value=%u ok=%d helper=sub_16D270",
        stage ? stage : "",
        target_ea,
        static_cast<unsigned long long>(address),
        value,
        ok ? 1 : 0);
    return ok;
}

void runFeature313OfflineWingWorker(std::uint32_t target_ea) {
    bool ok = writeFeature313OfflineWingState(0, target_ea, "start");
    std::this_thread::sleep_for(std::chrono::seconds(1));

    std::uintptr_t event_slot_table = 0;
    if (!resolvePointerChain(kFeature321322EventSlotChain, 3, &event_slot_table)) {
        logWarn(
            "feature 313 offline-wing resolve failed target=0x%X event_chain=qword_3640A0 worker=sub_1FF0BC table=qword_384F38",
            target_ea);
        std::this_thread::sleep_for(std::chrono::seconds(12));
        ok = writeFeature313OfflineWingState(1, target_ea, "restore") && ok;
        return;
    }

    std::uintptr_t event_pointer = 0;
    if (!readFeature321322EventPointer(event_slot_table, kFeature313EventSlotIndex, &event_pointer)) {
        logWarn(
            "feature 313 offline-wing event pointer read failed target=0x%X event_slot_table=0x%llX slot=0x22 qword_3643C8",
            target_ea,
            static_cast<unsigned long long>(event_slot_table));
        std::this_thread::sleep_for(std::chrono::seconds(12));
        ok = writeFeature313OfflineWingState(1, target_ea, "restore") && ok;
        return;
    }

    std::size_t submitted = 0;
    std::size_t failed = 0;
    for (const char *payload : kFeature313OfflineWingPayloads) {
        if (!writeFixedStringEventPayload(
                event_pointer,
                payload,
                kFeature313PayloadLength,
                target_ea,
                DIAG_TEXT("feature 313 offline-wing local event"))) {
            ++failed;
            ok = false;
            continue;
        }
        ++submitted;
    }

    std::this_thread::sleep_for(std::chrono::seconds(12));
    ok = writeFeature313OfflineWingState(1, target_ea, "restore") && ok;
    logInfo(
        "feature 313 offline-wing worker completed target=0x%X event_slot_table=0x%llX event=0x%llX submitted=%zu failed=%zu payload_len=0x18 source_chain=qword_3640A0 slot=qword_3643C8/0x22 offset=qword_3646F8/0x68 table=qword_384F38 count=123 worker=sub_1FF0BC helper=sub_178CCC state=qword_364558 ok=%d",
        target_ea,
        static_cast<unsigned long long>(event_slot_table),
        static_cast<unsigned long long>(event_pointer),
        submitted,
        failed,
        ok ? 1 : 0);
}

bool applyFeature313OfflineWing(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 313) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature313OfflineWingWorker(target_ea);
    }).detach();
    logInfo(
        "feature 313 offline-wing worker started target=0x%X wrapper=0x1C1248 worker=sub_1FF0BC table=qword_384F38 count=123 qword_364558 qword_3643C8",
        target_ea);
    return true;
}

void runFeature318StatueWorker(std::uint32_t target_ea) {
    bool ok = true;
    GameMemoryPatchResult patch79 =
        applyGameMemoryPatchByIndex(79);
    ok = patch79.ok && ok;

    std::uintptr_t event_slot_table = 0;
    if (!resolvePointerChain(kFeature321322EventSlotChain, 3, &event_slot_table)) {
        logWarn(
            "feature 318 statue unlock resolve failed target=0x%X event_chain=qword_3640A0 worker=sub_1FFC4C table=qword_37C450",
            target_ea);
        GameMemoryPatchResult reset =
            applyGameMemoryPatchByIndex(1);
        ok = reset.ok && ok;
        return;
    }

    std::uintptr_t event_pointer = 0;
    if (!readFeature321322EventPointer(event_slot_table, kFeature318EventSlotIndex, &event_pointer)) {
        logWarn(
            "feature 318 statue unlock event pointer read failed target=0x%X event_slot_table=0x%llX slot=qword_3643C0/0x1A",
            target_ea,
            static_cast<unsigned long long>(event_slot_table));
        GameMemoryPatchResult reset =
            applyGameMemoryPatchByIndex(1);
        ok = reset.ok && ok;
        return;
    }

    std::size_t submitted = 0;
    std::size_t failed = 0;
    for (const char *payload : kFeature318StatuePayloads) {
        if (!writeFixedStringEventPayload(
                event_pointer,
                payload,
                kFeature318PayloadLength,
                target_ea,
                DIAG_TEXT("feature 318 statue unlock local event"))) {
            ++failed;
            ok = false;
            continue;
        }
        ++submitted;
    }

    GameMemoryPatchResult reset =
        applyGameMemoryPatchByIndex(1);
    ok = reset.ok && ok;
    logInfo(
        "feature 318 statue unlock worker completed target=0x%X event_slot_table=0x%llX event=0x%llX submitted=%zu failed=%zu payload_len=0x28 source_chain=qword_3640A0 slot=qword_3643C0/0x1A offset=qword_3646F8/0x68 table=qword_37C450 count=43 patches=79,1 worker=sub_1FFC4C helper=sub_178CCC ok=%d",
        target_ea,
        static_cast<unsigned long long>(event_slot_table),
        static_cast<unsigned long long>(event_pointer),
        submitted,
        failed,
        ok ? 1 : 0);
}

bool applyFeature318StatueUnlock(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 318) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature318StatueWorker(target_ea);
    }).detach();
    logInfo(
        "feature 318 statue unlock worker started target=0x%X wrapper=0x1C0E48 worker=sub_1FFC4C table=qword_37C450 count=43 qword_3643C0",
        target_ea);
    return true;
}

void runFeature320AncestorWaxWorker(std::uint32_t target_ea) {
    std::uintptr_t event_slot_table = 0;
    if (!resolvePointerChain(kFeature321322EventSlotChain, 3, &event_slot_table)) {
        logWarn(
            "feature 320 ancestor-wax resolve failed target=0x%X event_chain=qword_3640A0 worker=sub_1FFEB8 table=xmmword_37CE10",
            target_ea);
        applyGameMemoryPatchByIndex(1);
        return;
    }

    std::uintptr_t event_pointer = 0;
    if (!readFeature321322EventPointer(event_slot_table, kFeature320EventSlotIndex, &event_pointer)) {
        logWarn(
            "feature 320 ancestor-wax event pointer read failed target=0x%X event_slot_table=0x%llX slot=qword_3643B8/0x15 worker=sub_1FFEB8",
            target_ea,
            static_cast<unsigned long long>(event_slot_table));
        applyGameMemoryPatchByIndex(1);
        return;
    }

    bool ok = true;
    std::size_t submitted = 0;
    std::size_t failed = 0;
    int patch_hits = 0;
    int patch_misses = 0;

    for (const Feature320AncestorWaxRecord &record : kFeature320AncestorWaxRecords) {
        GameMemoryPatchResult patch =
            applyGameMemoryPatchById(record.patch_id);
        if (patch.ok) {
            ++patch_hits;
        } else {
            ++patch_misses;
            ok = false;
            logWarn(
                "feature 320 ancestor-wax patch id missing target=0x%X level_id=0x%08X table=qword_384628 worker=sub_1FFEB8",
                target_ea,
                record.patch_id);
        }

        std::this_thread::sleep_for(std::chrono::microseconds(0x3D0900));

        for (int i = 0; i < record.payload_count && i < 4; ++i) {
            const char *payload = record.payloads[i];
            if (payload == nullptr || payload[0] == '\0') {
                continue;
            }

            if (!writeFixedStringEventPayload(
                    event_pointer,
                    payload,
                    kFeature320PayloadLength,
                    target_ea,
                    DIAG_TEXT("feature 320 ancestor-wax local event"))) {
                ++failed;
                ok = false;
                continue;
            }

            ++submitted;
            std::this_thread::sleep_for(std::chrono::microseconds(0x7A120));
        }
    }

    GameMemoryPatchResult reset =
        applyGameMemoryPatchByIndex(1);
    ok = reset.ok && ok;
    logInfo(
        "feature 320 ancestor-wax worker completed target=0x%X event_slot_table=0x%llX event=0x%llX groups=%zu submitted=%zu failed=%zu patch_hits=%d patch_misses=%d payload_len=0x18 source_chain=qword_3640A0 slot=qword_3643B8/0x15 offset=qword_3646F8/0x68 table=xmmword_37CE10/qword_37CE20 count=17 worker=sub_1FFEB8 helper=sub_178CCC ok=%d",
        target_ea,
        static_cast<unsigned long long>(event_slot_table),
        static_cast<unsigned long long>(event_pointer),
        sizeof(kFeature320AncestorWaxRecords) / sizeof(kFeature320AncestorWaxRecords[0]),
        submitted,
        failed,
        patch_hits,
        patch_misses,
        ok ? 1 : 0);
}

bool applyFeature320AncestorWax(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 320) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature320AncestorWaxWorker(target_ea);
    }).detach();
    logInfo(
        "feature 320 ancestor-wax worker started target=0x%X wrapper=0x1C1104 worker=sub_1FFEB8 table=xmmword_37CE10 groups=17 qword_3643B8",
        target_ea);
    return true;
}

bool writeFeature321322EventPayload(
    std::uintptr_t event_pointer,
    const std::vector<int> &payload,
    std::uint32_t target_ea,
    const char *label) {
    if (event_pointer == 0) {
        return false;
    }

    bool ok = true;
    const std::uintptr_t payload_base = event_pointer + kFeature321322EventPayloadOffset;
    for (std::size_t i = 0; i < payload.size(); ++i) {
        if (!writeU32Raw(
                payload_base + static_cast<std::uintptr_t>(i) * sizeof(std::uint32_t),
                static_cast<std::uint32_t>(payload[i]))) {
            ok = false;
        }
    }

    if (!ok) {
        logWarn(
            "%s payload write failed target=0x%X event=0x%llX payload_base=0x%llX count=%zu offset=0x68",
            label ? label : "feature 321/322 heart-fire local event",
            target_ea,
            static_cast<unsigned long long>(event_pointer),
            static_cast<unsigned long long>(payload_base),
            payload.size());
        return false;
    }

    return submitGameMemoryEventPointer(event_pointer, target_ea, label);
}

void runFeature321SendHeartFireWorker(std::uint32_t target_ea) {
    std::uintptr_t friend_list = 0;
    std::uintptr_t event_slot_table = 0;
    if (!resolvePointerChain(kFeature321322FriendListChain, 4, &friend_list) ||
        !resolvePointerChain(kFeature321322EventSlotChain, 3, &event_slot_table)) {
        logWarn(
            "feature 321 send-heart-fire resolve failed target=0x%X friend_chain=qword_364270 event_chain=qword_3640A0 worker=sub_178DC4",
            target_ea);
        return;
    }

    int friend_count = 0;
    if (!readU32AsInt(friend_list, &friend_count) || friend_count < 0) {
        logWarn(
            "feature 321 send-heart-fire friend count read failed target=0x%X friend_list=0x%llX worker=sub_178DC4",
            target_ea,
            static_cast<unsigned long long>(friend_list));
        return;
    }

    std::size_t submitted = 0;
    std::size_t failed = 0;
    for (int index = 0; index < friend_count; ++index) {
        const std::uintptr_t record =
            friend_list +
            static_cast<std::uintptr_t>(index) * kFeature321FriendRecordStride +
            kFeature321FriendPayloadOffset;

        std::vector<int> payload;
        payload.reserve(5);
        bool payload_ok = true;
        for (int field = 0; field < 4; ++field) {
            int value = 0;
            if (!readU32AsInt(record + static_cast<std::uintptr_t>(field) * sizeof(std::uint32_t), &value)) {
                payload_ok = false;
                break;
            }
            payload.push_back(value);
        }
        payload.push_back(6);

        std::uintptr_t event_pointer = 0;
        if (!payload_ok ||
            !readFeature321322EventPointer(event_slot_table, kFeature321EventSlotIndex, &event_pointer) ||
            !writeFeature321322EventPayload(
                event_pointer,
                payload,
                target_ea,
                DIAG_TEXT("feature 321 send-heart-fire local event"))) {
            ++failed;
            continue;
        }
        ++submitted;
    }

    logInfo(
        "feature 321 send-heart-fire worker completed target=0x%X friend_list=0x%llX event_slot_table=0x%llX friends=%d submitted=%zu failed=%zu payload_dwords=5 source_chain=qword_364270/qword_3640A0 slot=0xA5 worker=sub_178DC4",
        target_ea,
        static_cast<unsigned long long>(friend_list),
        static_cast<unsigned long long>(event_slot_table),
        friend_count,
        submitted,
        failed);
}

bool applyFeature321SendHeartFire(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 321) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature321SendHeartFireWorker(target_ea);
    }).detach();
    logInfo("feature 321 send-heart-fire worker started target=0x%X wrapper=sub_20011C worker=sub_178DC4", target_ea);
    return true;
}

void runFeature322CollectHeartFireWorker(std::uint32_t target_ea) {
    std::uintptr_t friend_list = 0;
    std::uintptr_t event_slot_table = 0;
    std::uintptr_t pending_list = 0;
    if (!resolvePointerChain(kFeature321322FriendListChain, 4, &friend_list) ||
        !resolvePointerChain(kFeature321322EventSlotChain, 3, &event_slot_table) ||
        !resolvePointerChain(kFeature322PendingListChain, 4, &pending_list)) {
        logWarn(
            "feature 322 collect-heart-fire resolve failed target=0x%X friend_chain=qword_364270 event_chain=qword_3640A0 pending_chain=qword_364290 worker=sub_17916C",
            target_ea);
        return;
    }

    int friend_count = 0;
    if (!readU32AsInt(friend_list, &friend_count) || friend_count < 0) {
        logWarn(
            "feature 322 collect-heart-fire friend count read failed target=0x%X friend_list=0x%llX worker=sub_17916C",
            target_ea,
            static_cast<unsigned long long>(friend_list));
        return;
    }

    std::size_t submitted = 0;
    std::size_t skipped = 0;
    std::size_t failed = 0;
    for (int index = 0; index < friend_count; ++index) {
        const std::uintptr_t record =
            pending_list + static_cast<std::uintptr_t>(index) * kFeature322PendingRecordStride;

        int enabled = 0;
        if (!readU32AsInt(record, &enabled)) {
            ++failed;
            continue;
        }
        if (enabled == 0) {
            ++skipped;
            continue;
        }

        std::vector<int> payload;
        payload.reserve(kFeature322PayloadOffsets.size());
        bool payload_ok = true;
        for (std::uintptr_t offset : kFeature322PayloadOffsets) {
            int value = 0;
            if (!readU32AsInt(record + offset, &value)) {
                payload_ok = false;
                break;
            }
            payload.push_back(value);
        }

        std::uintptr_t event_pointer = 0;
        if (!payload_ok ||
            !readFeature321322EventPointer(event_slot_table, kFeature322EventSlotIndex, &event_pointer) ||
            !writeFeature321322EventPayload(
                event_pointer,
                payload,
                target_ea,
                DIAG_TEXT("feature 322 collect-heart-fire local event"))) {
            ++failed;
            continue;
        }
        ++submitted;
    }

    logInfo(
        "feature 322 collect-heart-fire worker completed target=0x%X friend_list=0x%llX pending_list=0x%llX event_slot_table=0x%llX records=%d submitted=%zu skipped=%zu failed=%zu payload_dwords=6 stride=0xB8 source_chain=qword_364270/qword_3640A0/qword_364290 slot=0xA6 worker=sub_17916C",
        target_ea,
        static_cast<unsigned long long>(friend_list),
        static_cast<unsigned long long>(pending_list),
        static_cast<unsigned long long>(event_slot_table),
        friend_count,
        submitted,
        skipped,
        failed);
}

bool applyFeature322CollectHeartFire(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 322) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature322CollectHeartFireWorker(target_ea);
    }).detach();
    logInfo("feature 322 collect-heart-fire worker started target=0x%X wrapper=sub_2001BC worker=sub_17916C", target_ea);
    return true;
}

void runFeature431WingBlastWorker(int count, std::uint32_t target_ea) {
    std::uintptr_t wing_slot_base = 0;
    std::uintptr_t search_base = 0;
    if (!resolvePointerChain(kFeature431WingSlotChain, 4, &wing_slot_base) ||
        !resolvePointerChain(kFeature431WingSearchChain, 4, &search_base)) {
        logWarn(
            "feature 431 wing blast resolve failed target=0x%X chain=qword_364138/qword_364118 count=%d",
            target_ea,
            count);
        return;
    }

    const std::uintptr_t marker = findDwordInProcessRange(1935763474u, search_base, 0x5000);
    if (marker == 0) {
        logWarn(
            "feature 431 wing blast marker not found target=0x%X search_base=0x%llX size=0x5000 value=1935763474",
            target_ea,
            static_cast<unsigned long long>(search_base));
        return;
    }

    const std::uintptr_t wing_state = readMaskedPointerRaw(marker + 16);
    if (wing_state == 0) {
        logWarn(
            "feature 431 wing blast marker pointer read failed target=0x%X marker=0x%llX",
            target_ea,
            static_cast<unsigned long long>(marker));
        return;
    }

    const bool blast_house = g_feature_428_blast_wing_house.load();
    const bool absorb_wing = g_feature_429_absorb_wing.load();
    bool ok = true;
    if (blast_house) {
        for (int offset = 344; offset <= 360; offset += 4) {
            ok = writeOriginalProcTypedInt(wing_state + static_cast<std::uintptr_t>(offset), 400, 4) && ok;
        }
        usleep(0x493E0);
        for (int i = 0; i < 400; ++i) {
            ok = writeOriginalProcTypedInt(
                     wing_slot_base + static_cast<std::uintptr_t>(i) * 224,
                     257,
                     4) && ok;
        }
    } else {
        const int wing_count = std::max(0, count);
        for (int offset = 344; offset <= 360; offset += 4) {
            ok = writeOriginalProcTypedInt(wing_state + static_cast<std::uintptr_t>(offset), wing_count, 4) && ok;
        }
        if (absorb_wing && wing_count >= 1) {
            usleep(0x1E8480);
            for (int i = 0; i < wing_count; ++i) {
                ok = writeOriginalProcTypedInt(
                         wing_slot_base + static_cast<std::uintptr_t>(i) * 224,
                         257,
                         4) && ok;
            }
        }
    }

    logInfo(
        "feature 431 wing blast target=0x%X count=%d wing_state=0x%llX slot_base=0x%llX marker=0x%llX blast_house=%d absorb=%d ok=%d source_chain=qword_364138/qword_364118",
        target_ea,
        count,
        static_cast<unsigned long long>(wing_state),
        static_cast<unsigned long long>(wing_slot_base),
        static_cast<unsigned long long>(marker),
        blast_house ? 1 : 0,
        absorb_wing ? 1 : 0,
        ok ? 1 : 0);
}

bool applyFeature431WingBlast(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 431) {
        return false;
    }

    const int count = static_cast<int>(g_feature_430_wing_break_count.load());
    std::thread([count, target_ea]() {
        runFeature431WingBlastWorker(count, target_ea);
    }).detach();

    logInfo(
        "feature 431 wing blast worker started target=0x%X count=%d switch_case=1333 worker=sub_2003E8 helper=sub_185808",
        target_ea,
        count);
    return true;
}

void runFeature435HeartPathWorker(std::uint32_t target_ea) {
    std::uintptr_t position = 0;
    if (!resolvePointerChain(kAvatarPositionChain, 4, &position)) {
        logWarn(
            "feature 435 heart path resolve failed target=0x%X chain=qword_364068",
            target_ea);
        return;
    }

    GamePosition origin;
    if (!readOriginalAvatarPosition(&origin)) {
        logWarn(
            "feature 435 heart path origin read failed target=0x%X position=0x%llX",
            target_ea,
            static_cast<unsigned long long>(position));
        return;
    }

    const long long radius = g_feature_432_heart_radius.load();
    const long long loops = g_feature_433_loop_count.load();
    const long long height = g_feature_434_heart_height.load();
    if (loops < 1) {
        logInfo(
            "feature 435 heart path skipped target=0x%X loops=%lld state_qword=0x37BB88",
            target_ea,
            loops);
        return;
    }

    constexpr double kPi = 3.14159265;
    constexpr double kStepDenominator = 50.0;
    const double scale = static_cast<double>(radius) * 0.07;
    const long long iterations = 100 * loops;
    bool ok = true;
    for (long long i = 0; i < iterations; ++i) {
        const double t = static_cast<double>(i + 1) * kPi / kStepDenominator;
        const double sin_t = std::sin(t);
        const float x = static_cast<float>(origin.x + scale * 16.0 * std::pow(sin_t, 3.0));
        const float y = static_cast<float>(origin.y + static_cast<float>(height));
        const float z = static_cast<float>(
            origin.z +
            scale *
                (13.0 * std::cos(t) -
                 5.0 * std::cos(static_cast<double>((i + 1) * 2) * kPi / kStepDenominator) -
                 2.0 * std::cos(static_cast<double>((i + 1) * 3) * kPi / kStepDenominator) -
                 std::cos(static_cast<double>((i + 1) * 4) * kPi / kStepDenominator)));

        ok = writeOriginalProcTypedFloat(position, x) && ok;
        ok = writeOriginalProcTypedFloat(position + 4, y) && ok;
        ok = writeOriginalProcTypedFloat(position + 8, z) && ok;
        std::this_thread::sleep_for(std::chrono::nanoseconds(15000000));
    }

    logInfo(
        "feature 435 heart path target=0x%X position=0x%llX radius=%lld loops=%lld height=%lld iterations=%lld origin=(%.6f,%.6f,%.6f) ok=%d source_chain=qword_364068",
        target_ea,
        static_cast<unsigned long long>(position),
        radius,
        loops,
        height,
        iterations,
        static_cast<double>(origin.x),
        static_cast<double>(origin.y),
        static_cast<double>(origin.z),
        ok ? 1 : 0);
}

bool applyFeature435HeartPath(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 435) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature435HeartPathWorker(target_ea);
    }).detach();

    logInfo(
        "feature 435 heart path worker started target=0x%X switch_case=1337 worker=sub_200490 states=0x37BB80/0x37BB88/0x37BB90",
        target_ea);
    return true;
}

bool applyFeature812CutsceneColor(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 812) {
        return false;
    }

    const char *color = nullptr;
    switch (request.int_value) {
        case 0:
            color = "Black";
            break;
        case 1:
            color = "White";
            break;
        case 2:
            color = "Green";
            break;
        case 3:
            color = "Rainbow";
            break;
        default:
            logWarn(
                "feature 812 cutscene color invalid item=%d target=0x%X switch=0x1C34A0",
                request.int_value,
                target_ea);
            return true;
    }

    g_feature_812_cutscene_color.store(request.int_value);
    setChangeLevelFadeTexture(color);
    logInfo(
        "feature 812 cutscene color item=%d value=%s target=0x%X state_qword=0x385588",
        request.int_value,
        color,
        target_ea);
    return true;
}

bool applyBoolStateOnlyCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const BoolStateOnlyCase *item = findBoolStateOnlyCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    item->state->store(request.bool_value);
    logInfo(
        "bool-state-only feature=%d target=0x%X state_byte=0x%X value=%d",
        request.feature_num,
        target_ea,
        item->state_byte_ea,
        request.bool_value ? 1 : 0);
    return true;
}

bool applyFeature501MagicSelector(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 501) {
        return false;
    }

    std::uint32_t magic_id = 0;
    switch (request.int_value) {
        case 0:
            magic_id = 0x55EC8A04u;
            break;
        case 1:
            magic_id = 0x66D22159u;
            break;
        case 2:
            magic_id = 0x6C2AF262u;
            break;
        case 3:
            magic_id = 0x8FD50AD4u;
            break;
        default:
            logWarn(
                "feature 501 magic selector invalid item=%d target=0x%X qword_37BC00",
                request.int_value,
                target_ea);
            return true;
    }

    g_feature_501_magic_id.store(magic_id);
    logInfo(
        "feature 501 magic selector item=%d target=0x%X qword_37BC00=0x%08X",
        request.int_value,
        target_ea,
        magic_id);
    return true;
}

bool writeFeature502LightningFrame(std::uintptr_t trigger, std::uint32_t magic_id) {
    return writeOriginalProcTypedInt(trigger, static_cast<int>(magic_id), 4) &&
           writeOriginalProcTypedInt(trigger + 0x10, -1600133292, 4) &&
           writeOriginalProcTypedInt(trigger + 0x24, 0, 4) &&
           writeOriginalProcTypedInt(trigger + 0x4308, 6, 4);
}

void runFeature502LightningWorker(std::uint32_t target_ea) {
    std::uintptr_t trigger = 0;
    if (!resolvePointerChain(kFeature630TriggerChain, 4, &trigger)) {
        logWarn("feature 502 lightning spark trigger chain resolve failed target=0x%X chain=qword_3641F0", target_ea);
        g_feature_502_lightning_enabled.store(false);
        return;
    }

    bool ok = true;
    while (g_feature_502_lightning_enabled.load()) {
        ok = writeFeature502LightningFrame(trigger, g_feature_501_magic_id.load()) && ok;
    }

    ok = writeOriginalProcTypedInt(trigger, 0, 4) && ok;
    logInfo(
        "feature 502 lightning spark worker stopped target=0x%X trigger=0x%llX qword_37BC00=0x%08X ok=%d",
        target_ea,
        static_cast<unsigned long long>(trigger),
        g_feature_501_magic_id.load(),
        ok ? 1 : 0);
}

bool applyFeature502LightningSpark(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 502) {
        return false;
    }

    g_feature_502_lightning_enabled.store(request.bool_value);
    if (!g_feature_502_gate.load()) {
        g_feature_502_gate.store(true);
        logInfo(
            "feature 502 lightning spark gate initialized target=0x%X state_byte=0x385544 gate_byte=0x385705 enabled=%d",
            target_ea,
            request.bool_value ? 1 : 0);
        return true;
    }

    if (!request.bool_value) {
        logInfo(
            "feature 502 lightning spark disabled target=0x%X state_byte=0x385544 gate_byte=0x385705",
            target_ea);
        return true;
    }

    logInfo(
        "feature 502 lightning spark enabled target=0x%X worker=0x187794 chain=qword_3641F0 qword_37BC00=0x%08X",
        target_ea,
        g_feature_501_magic_id.load());
    std::thread([target_ea]() {
        runFeature502LightningWorker(target_ea);
    }).detach();
    return true;
}

bool writeFeature505SizeFrame(std::uintptr_t address, std::int64_t height, std::int64_t body_shape) {
    return writeOriginalProcTypedInt(address, static_cast<int>(height), 0x10) &&
           writeOriginalProcTypedInt(address + 4, static_cast<int>(body_shape), 0x10);
}

void runFeature505SizeWorker(std::uint32_t target_ea, std::int64_t height, std::int64_t body_shape) {
    std::uintptr_t address = 0;
    if (!resolvePointerChain(kFeature505SizeChain, 4, &address)) {
        logWarn("feature 505 size worker chain resolve failed target=0x%X chain=qword_364230", target_ea);
        g_feature_505_size_modify_enabled.store(false);
        return;
    }

    bool ok = true;
    while (g_feature_505_size_modify_enabled.load()) {
        ok = writeFeature505SizeFrame(address, height, body_shape) && ok;
    }

    logInfo(
        "feature 505 size worker stopped target=0x%X worker=0x1878C8 address=0x%llX height=%lld body=%lld ok=%d",
        target_ea,
        static_cast<unsigned long long>(address),
        static_cast<long long>(height),
        static_cast<long long>(body_shape),
        ok ? 1 : 0);
}

bool applyFeature505SizeWorker(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 505) {
        return false;
    }

    g_feature_505_size_modify_enabled.store(request.bool_value);
    if (!g_feature_505_gate.load()) {
        g_feature_505_gate.store(true);
        logInfo(
            "feature 505 size modify gate initialized target=0x%X state_byte=0x385548 gate_byte=0x385706 enabled=%d",
            target_ea,
            request.bool_value ? 1 : 0);
        return true;
    }

    if (!request.bool_value) {
        logInfo(
            "feature 505 size modify disabled target=0x%X state_byte=0x385548 gate_byte=0x385706",
            target_ea);
        return true;
    }

    const std::int64_t height = g_feature_503_height_input.load();
    const std::int64_t body_shape = g_feature_504_body_shape_input.load();
    logInfo(
        "feature 505 size modify enabled target=0x%X worker=0x1878C8 chain=qword_364230 height=%lld body=%lld",
        target_ea,
        static_cast<long long>(height),
        static_cast<long long>(body_shape));
    std::thread([target_ea, height, body_shape]() {
        runFeature505SizeWorker(target_ea, height, body_shape);
    }).detach();
    return true;
}

bool resolveFeature506SizeWriteAddress(std::uintptr_t *out) {
    if (!out) {
        return false;
    }

    std::uintptr_t object = 0;
    if (!resolvePointerChain(kFeature506SizeTargetChain, 4, &object)) {
        return false;
    }

    std::uint64_t first = 0;
    std::uint64_t second = 0;
    if (!readU64Raw(object + 0x58, &first)) {
        return false;
    }
    if (!readU64Raw(static_cast<std::uintptr_t>(first & kPointerMask), &second)) {
        return false;
    }
    *out = static_cast<std::uintptr_t>(second & kPointerMask);
    return true;
}

bool applyFeature506SizeSeekbar(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 506) {
        return false;
    }

    std::uintptr_t address = 0;
    if (!resolveFeature506SizeWriteAddress(&address)) {
        logWarn("feature 506 size seekbar chain resolve failed target=0x%X chain=qword_364250 +0x58 deref", target_ea);
        return true;
    }

    if (!writeOriginalProcTypedInt(address, request.int_value, 0x10)) {
        logWarn(
            "feature 506 size seekbar write failed target=0x%X address=0x%llX value=%d type=0x10",
            target_ea,
            static_cast<unsigned long long>(address),
            request.int_value);
        return true;
    }

    logInfo(
        "feature 506 size seekbar target=0x%X chain=qword_364250 address=0x%llX value=%d type=0x10",
        target_ea,
        static_cast<unsigned long long>(address),
        request.int_value);
    return true;
}

int readFeature507WardrobeCount(std::uintptr_t base_address) {
    std::int32_t count = 0;
    if (!readTyped(base_address + 0x00638000, TYPE_DWORD, &count, sizeof(count))) {
        return 0;
    }
    return count;
}

void runFeature507WardrobeEnable(std::uint32_t target_ea) {
    std::uintptr_t base_address = 0;
    if (!resolvePointerChain(kFeature507WardrobeChain, 3, &base_address)) {
        logWarn("feature 507 wardrobe enable chain resolve failed target=0x%X chain=qword_364100", target_ea);
        return;
    }

    const int count = readFeature507WardrobeCount(base_address);
    if (count < 1) {
        logWarn(
            "feature 507 wardrobe enable no slots target=0x%X base=0x%llX count=%d",
            target_ea,
            static_cast<unsigned long long>(base_address),
            count);
        return;
    }

    static constexpr std::uintptr_t kOffsets[] = {0x2EA, 0x2EB, 0x2ED, 0x2EF, 0x2F0, 0x2F1};
    static constexpr std::uint8_t kEnabledValues[] = {1, 1, 1, 1, 1, 0};

    std::vector<std::pair<std::uintptr_t, std::uint8_t>> originals;
    originals.reserve(static_cast<std::size_t>(count) * (sizeof(kOffsets) / sizeof(kOffsets[0])));

    bool ok = true;
    for (int slot = 0; slot < count; ++slot) {
        const std::uintptr_t slot_base = base_address + (static_cast<std::uintptr_t>(slot) * 0xC70);
        for (std::size_t i = 0; i < sizeof(kOffsets) / sizeof(kOffsets[0]); ++i) {
            const std::uintptr_t address = slot_base + kOffsets[i];
            std::uint8_t original = 0;
            if (readTyped(address, TYPE_BYTE, &original, sizeof(original))) {
                originals.push_back({address, original});
            } else {
                ok = false;
            }
            if (!writeOriginalProcTypedInt(address, kEnabledValues[i], 1)) {
                ok = false;
            }
        }
    }

    const std::size_t cached = originals.size();
    {
        std::lock_guard<std::mutex> guard(g_feature_507_lock);
        g_feature_507_original_bytes.swap(originals);
    }

    logInfo(
        "feature 507 wardrobe enabled target=0x%X worker=0x1795D4 base=0x%llX count=%d cached=%zu stride=0xC70 ok=%d",
        target_ea,
        static_cast<unsigned long long>(base_address),
        count,
        cached,
        ok ? 1 : 0);
}

void runFeature507WardrobeRestore(std::uint32_t target_ea) {
    std::vector<std::pair<std::uintptr_t, std::uint8_t>> originals;
    {
        std::lock_guard<std::mutex> guard(g_feature_507_lock);
        originals = g_feature_507_original_bytes;
        g_feature_507_original_bytes.clear();
    }

    std::size_t restored = 0;
    for (const auto &entry : originals) {
        if (writeOriginalProcTypedInt(entry.first, entry.second, 1)) {
            ++restored;
        }
    }

    logInfo(
        "feature 507 wardrobe restored target=0x%X worker=0x200F04 restored=%zu cached=%zu",
        target_ea,
        restored,
        originals.size());
}

bool applyFeature507Wardrobe(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 507) {
        return false;
    }

    logInfo(
        "feature 507 wardrobe state=%d target=0x%X state_byte=0x385478",
        request.bool_value ? 1 : 0,
        target_ea);

    if (request.bool_value) {
        std::thread([target_ea]() {
            runFeature507WardrobeEnable(target_ea);
        }).detach();
    } else {
        std::thread([target_ea]() {
            runFeature507WardrobeRestore(target_ea);
        }).detach();
    }
    return true;
}

bool applyFeature509MagicInput(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 509) {
        return false;
    }

    std::uintptr_t trigger = 0;
    if (!resolvePointerChain(kFeature630TriggerChain, 4, &trigger)) {
        logWarn("feature 509 magic input trigger chain resolve failed target=0x%X chain=qword_3641F0", target_ea);
        return true;
    }

    const bool ok = writeOriginalProcTypedInt(trigger, request.int_value, 4) &&
                    writeOriginalProcTypedInt(trigger + 0x10, -1600133292, 4) &&
                    writeOriginalProcTypedInt(trigger + 0x24, 0, 4) &&
                    writeOriginalProcTypedInt(trigger + 0x4308, 64, 4);
    logInfo(
        "feature 509 magic input target=0x%X trigger=0x%llX value=%d qword_364678=0x24 qword_3646F0=0x4308 ok=%d",
        target_ea,
        static_cast<unsigned long long>(trigger),
        request.int_value,
        ok ? 1 : 0);
    return true;
}

bool applyMagicSlotCase(const ChangeRequest &request, std::uint32_t target_ea) {
    const MagicSlotCase *item = findMagicSlotCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > static_cast<int>(item->magic_id_count)) {
        logWarn(
            "magic slot invalid feature=%d item=%d target=0x%X source_qword=0x%X count=%zu",
            request.feature_num,
            request.int_value,
            target_ea,
            item->source_qword_ea,
            item->magic_id_count);
        return true;
    }

    std::uintptr_t trigger = 0;
    if (!resolvePointerChain(kFeature630TriggerChain, 4, &trigger)) {
        logWarn(
            "magic slot trigger chain resolve failed feature=%d item=%d target=0x%X source_qword=0x%X chain=qword_3641F0",
            request.feature_num,
            request.int_value,
            target_ea,
            item->source_qword_ea);
        return true;
    }

    const std::uint32_t magic_id = item->magic_ids[request.int_value - 1];
    const std::uintptr_t slot = trigger + item->slot_offset;
    const bool ok =
        writeOriginalProcTypedInt(slot, static_cast<std::int32_t>(magic_id), 4) &&
        writeOriginalProcTypedInt(slot + 0x10, -1600133292, 4) &&
        writeOriginalProcTypedInt(slot + 0x24, 0, 4) &&
        writeOriginalProcTypedInt(trigger + 0x4308, 64, 4);
    logInfo(
        "magic slot feature=%d item=%d target=0x%X original_target=0x%X trigger=0x%llX slot=0x%llX source_qword=0x%X slot_offset=0x%llX magic_id=0x%08X qword_364678=0x24 qword_3646F0=0x4308 ok=%d",
        request.feature_num,
        request.int_value,
        target_ea,
        item->target_ea,
        static_cast<unsigned long long>(trigger),
        static_cast<unsigned long long>(slot),
        item->source_qword_ea,
        static_cast<unsigned long long>(item->slot_offset),
        magic_id,
        ok ? 1 : 0);
    return true;
}

bool applyFeature923FireworksMagic(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 923) {
        return false;
    }

    if (request.int_value == 2) {
        if (!sendIndexedGameCommand(20)) {
            logWarn(
                "feature 923 SocialFireworksMenu command failed item=%d target=0x%X command_index=20 original_call=0x1C5F8C",
                request.int_value,
                target_ea);
        }
        logInfo(
            "feature 923 SocialFireworksMenu item=%d target=0x%X command_index=20 original_call=0x1C5F8C",
            request.int_value,
            target_ea);
        return true;
    }

    if (request.int_value != 1) {
        logWarn(
            "feature 923 fireworks magic invalid item=%d target=0x%X switch=0x1C2678",
            request.int_value,
            target_ea);
        return true;
    }

    std::uintptr_t trigger = 0;
    if (!resolvePointerChain(kFeature630TriggerChain, 4, &trigger)) {
        logWarn(
            "feature 923 fireworks magic trigger chain resolve failed item=%d target=0x%X source_qword=0x3646A8 chain=qword_3641F0",
            request.int_value,
            target_ea);
        return true;
    }

    constexpr std::uint32_t kFireworksMagicId = 0x0D8806B3;
    constexpr std::uintptr_t kFireworksSlotOffset = 0x150;
    const std::uintptr_t slot = trigger + kFireworksSlotOffset;
    const bool ok =
        writeOriginalProcTypedInt(slot, static_cast<std::int32_t>(kFireworksMagicId), 4) &&
        writeOriginalProcTypedInt(slot + 0x10, -1600133292, 4) &&
        writeOriginalProcTypedInt(slot + 0x24, 0, 4) &&
        writeOriginalProcTypedInt(trigger + 0x4308, 64, 4);
    logInfo(
        "feature 923 fireworks magic item=%d target=0x%X original_target=0x1C2678 trigger=0x%llX slot=0x%llX source_qword=0x3646A8 slot_offset=0x150 magic_id=0x%08X qword_364678=0x24 qword_3646F0=0x4308 ok=%d",
        request.int_value,
        target_ea,
        static_cast<unsigned long long>(trigger),
        static_cast<unsigned long long>(slot),
        kFireworksMagicId,
        ok ? 1 : 0);
    return true;
}

const char *sub1852D0TextForMode(int mode) {
    switch (mode) {
        case 1: return "AllPortals";
        case 2: return "EnterMorning";
        case 3: return u8"遇境切换清晨";
        case 4: return u8"遇境切换晴天";
        case 5: return u8"遇境切换阴天";
        case 6: return u8"遇境切换黄昏";
        case 7: return u8"遇境切换夜晚";
        case 8: return u8"遇境彩虹";
        case 9: return u8"遇境暴风雪";
        case 10: return u8"八人门狗狗";
        case 11: return "SpawnOreo";
        default: return nullptr;
    }
}

bool runSub1852D0ModeWorker(int mode, std::uint32_t target_ea, const char *feature_label) {
    const char *text = sub1852D0TextForMode(mode);
    if (text == nullptr) {
        logWarn(
            "%s invalid mode=%d target=0x%X helper=sub_1852D0 table=xmmword_37C5B0",
            feature_label ? feature_label : "sub_1852D0",
            mode,
            target_ea);
        return false;
    }

    std::uintptr_t text_address = 0;
    if (!resolvePointerChain(kFeature630TextChain, 4, &text_address)) {
        logWarn(
            "%s text chain resolve failed mode=%d target=0x%X chain=qword_364210 helper=sub_1852D0",
            feature_label ? feature_label : "sub_1852D0",
            mode,
            target_ea);
        return false;
    }

    const std::size_t text_len = std::strlen(text);
    const std::string dotted_text = "." + std::string(text);
    bool ok = true;
    if (dotted_text.size() <= 24) {
        char fixed[24]{};
        std::memcpy(fixed, dotted_text.c_str(), dotted_text.size() + 1);
        ok = writeBytes(text_address, fixed, sizeof(fixed)) &&
             writeBytes(text_address + 24, fixed, sizeof(fixed));
    } else {
        const std::int32_t type_value = 49;
        const std::int32_t length_value = static_cast<std::int32_t>(text_len + 1);
        ok = writeOriginalProcTypedInt(text_address, type_value, 4) &&
             writeOriginalProcTypedInt(text_address + 8, length_value, 4) &&
             writeU64Raw(text_address + 16, static_cast<std::uint64_t>(text_address + 232)) &&
             writeBytes(text_address + 232, text, text_len + 1);
    }

    if (!ok) {
        logWarn(
            "%s text write failed mode=%d target=0x%X address=0x%llX text='%s' len=%zu helper=sub_1852D0",
            feature_label ? feature_label : "sub_1852D0",
            mode,
            target_ea,
            static_cast<unsigned long long>(text_address),
            text,
            text_len);
        return false;
    }

    std::uintptr_t trigger = 0;
    if (!resolvePointerChain(kFeature630TriggerChain, 4, &trigger)) {
        logWarn(
            "%s trigger chain resolve failed mode=%d target=0x%X chain=qword_3641F0 helper=sub_1852D0",
            feature_label ? feature_label : "sub_1852D0",
            mode,
            target_ea);
        return false;
    }

    bool trigger_ok =
        writeOriginalProcTypedInt(trigger, 539651423, 4) &&
        writeOriginalProcTypedInt(trigger + 16, -1600133292, 4) &&
        writeOriginalProcTypedInt(trigger + 0x24, 0, 4) &&
        writeOriginalProcTypedInt(trigger + 0x4308, 64, 4);
    std::this_thread::sleep_for(std::chrono::microseconds(500000));

    std::uintptr_t clear_base = 0;
    if (resolvePointerChain(kFeature630TriggerChain, 4, &clear_base)) {
        for (int slot = -1; slot != 63; ++slot) {
            if (!writeOriginalProcTypedInt(clear_base + 0x38 * static_cast<std::intptr_t>(slot), 0, 4)) {
                trigger_ok = false;
            }
        }
    } else {
        trigger_ok = false;
    }

    logInfo(
        "%s mode=%d target=0x%X text_addr=0x%llX trigger=0x%llX text='%s' helper=sub_1852D0 table=xmmword_37C5B0 qword_364210/qword_3641F0 ok=%d",
        feature_label ? feature_label : "sub_1852D0",
        mode,
        target_ea,
        static_cast<unsigned long long>(text_address),
        static_cast<unsigned long long>(trigger),
        text,
        trigger_ok ? 1 : 0);
    return trigger_ok;
}

void runFeature630Worker(int item, std::uint32_t target_ea) {
    if (!sub1852D0TextForMode(item + 1)) {
        logWarn("feature 630 invalid item=%d target=0x%X switch=0x1C28AC", item, target_ea);
        return;
    }

    runSub1852D0ModeWorker(item + 1, target_ea, DIAG_TEXT("feature 630 home switch"));
}

bool applyFeature630HomeSwitch(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 630) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > 9) {
        logWarn(
            "feature 630 invalid item=%d target=0x%X switch=0x1C28AC count=9",
            request.int_value,
            target_ea);
        return true;
    }

    const int item = request.int_value;
    std::thread([item, target_ea]() {
        runFeature630Worker(item, target_ea);
    }).detach();
    return true;
}

void runFeature319AllPortalsWorker(std::uint32_t target_ea) {
    runSub1852D0ModeWorker(1, target_ea, DIAG_TEXT("feature 319 all-portals"));
    applyGameMemoryPatchByIndex(79);
    std::this_thread::sleep_for(std::chrono::microseconds(0x4C4B40));
    applyGameMemoryPatchByIndex(1);
    logInfo(
        "feature 319 all-portals worker completed target=0x%X wrapper=sub_1FFDF8 helper=sub_1852D0 mode=1 patch=79 reset=1",
        target_ea);
}

bool applyFeature319AllPortals(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 319) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature319AllPortalsWorker(target_ea);
    }).detach();
    logInfo("feature 319 all-portals worker started target=0x%X wrapper=sub_1FFDF8", target_ea);
    return true;
}

void runFeature323EightPlayerDoorWorker(std::uint32_t target_ea) {
    runSub1852D0ModeWorker(11, target_ea, DIAG_TEXT("feature 323 eight-player-door"));
    logInfo(
        "feature 323 eight-player-door worker completed target=0x%X wrapper=sub_20025C helper=sub_1852D0 mode=11",
        target_ea);
}

bool applyFeature323EightPlayerDoor(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 323) {
        return false;
    }

    std::thread([target_ea]() {
        runFeature323EightPlayerDoorWorker(target_ea);
    }).detach();
    logInfo("feature 323 eight-player-door worker started target=0x%X wrapper=sub_20025C", target_ea);
    return true;
}

bool feature629GateReady(std::uint32_t target_ea) {
    std::uintptr_t gate = 0;
    if (!resolvePointerChain(kFeature629GateChain, 3, &gate)) {
        logWarn("feature 629 gate chain resolve failed target=0x%X chain=qword_3640B8", target_ea);
        return false;
    }

    std::int32_t marker = 0;
    if (!readTyped(gate, TYPE_DWORD, &marker, sizeof(marker))) {
        logWarn("feature 629 gate read failed target=0x%X address=0x%llX", target_ea, static_cast<unsigned long long>(gate));
        return false;
    }

    if (static_cast<std::uint32_t>(marker) != 0xE7835080u) {
        logWarn(
            "feature 629 gate mismatch target=0x%X address=0x%llX value=0x%08X expected=0xE7835080",
            target_ea,
            static_cast<unsigned long long>(gate),
            static_cast<unsigned int>(marker));
        return false;
    }
    return true;
}

void runFeature629EnableWorker(std::uint32_t target_ea) {
    if (!feature629GateReady(target_ea)) {
        return;
    }

    const int old_range = currentRange();
    setRange(RANGE_C_DATA);
    clearResult();
    rangeMemorySearch("1114892927", TYPE_DWORD);
    memoryOffset("0", TYPE_DWORD, -8);
    memoryOffset("1125108416", TYPE_DWORD, -4);
    memoryOffset("-1036557907", TYPE_DWORD, 4);
    std::vector<std::uintptr_t> bases = getResults();
    setRange(old_range);

    std::vector<std::uintptr_t> addresses;
    std::vector<float> originals;
    addresses.reserve(bases.size() * 10);
    originals.reserve(bases.size() * 10);

    for (std::uintptr_t base : bases) {
        for (std::uintptr_t offset = 0; offset != 0xA0; offset += 0x10) {
            const std::uintptr_t address = base + offset;
            float original = 0.0f;
            if (!readFloatRaw(address, &original)) {
                continue;
            }
            addresses.push_back(address);
            originals.push_back(original);
            const float patched = original + 900.0f;
            writeFloatRaw(address, patched);
        }
    }

    std::size_t patched_count = 0;
    {
        std::lock_guard<std::mutex> guard(g_feature_629_lock);
        g_feature_629_addresses.swap(addresses);
        g_feature_629_original_values.swap(originals);
        patched_count = g_feature_629_addresses.size();
    }

    logInfo(
        "feature 629 remove trial water enabled target=0x%X base_hits=%zu patched=%zu worker=0x1ED720",
        target_ea,
        bases.size(),
        patched_count);
}

void runFeature629DisableWorker(std::uint32_t target_ea) {
    std::vector<std::uintptr_t> addresses;
    std::vector<float> originals;
    {
        std::lock_guard<std::mutex> guard(g_feature_629_lock);
        addresses = g_feature_629_addresses;
        originals = g_feature_629_original_values;
        g_feature_629_addresses.clear();
        g_feature_629_original_values.clear();
    }

    std::size_t restored = 0;
    const std::size_t count = std::min(addresses.size(), originals.size());
    for (std::size_t i = 0; i < count; ++i) {
        if (writeFloatRaw(addresses[i], originals[i])) {
            ++restored;
        }
    }

    logInfo(
        "feature 629 remove trial water disabled target=0x%X restored=%zu cached=%zu worker=0x20102C",
        target_ea,
        restored,
        count);
}

bool applyFeature629TrialWater(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 629) {
        return false;
    }

    g_feature_629_enabled.store(request.bool_value);
    const bool enabled = request.bool_value;
    std::thread([enabled, target_ea]() {
        if (enabled) {
            runFeature629EnableWorker(target_ea);
        } else {
            runFeature629DisableWorker(target_ea);
        }
    }).detach();
    return true;
}

bool resolveNativeCase(const ChangeRequest &request, ResolvedNativeCase *out) {
    if (request.feature_num > 8000) {
        if (const SwitchCaseTarget *high = findHighValueSwitchCase(request.feature_num)) {
            *out = {DIAG_TEXT("high"), request.feature_num, high->target_ea};
            return true;
        }

        if (request.feature_num == 9010) {
            *out = {DIAG_TEXT("special_9010"), request.feature_num, 0x1C09AC};
            return true;
        }

        if (request.feature_num == 9999) {
            *out = {DIAG_TEXT("special_9999"), request.feature_num, 0x1C05BC};
            return true;
        }

        return false;
    }

    if (request.feature_num >= 1500 && request.feature_num <= 1509) {
        if (const SwitchCaseTarget *secondary = findCase1500SwitchCase(request.feature_num)) {
            *out = {DIAG_TEXT("secondary1500"), request.feature_num, secondary->target_ea};
            if (request.feature_num == 1509) {
                if (const SwitchCaseTarget *item = findFeatureSwitchCase(request.int_value)) {
                    *out = {DIAG_TEXT("secondary1509_item"), request.int_value, item->target_ea};
                } else {
                    *out = {DIAG_TEXT("secondary1509_default"), request.int_value, kDefaultCaseTarget};
                }
            }
            return true;
        }
    }

    const int primary_index = request.feature_num + 902;
    if (primary_index >= 0 && primary_index <= 1932) {
        if (const SwitchCaseTarget *primary = findPrimarySwitchCase(primary_index)) {
            *out = {DIAG_TEXT("primary"), primary_index, primary->target_ea};
            return true;
        }
    }

    return false;
}

bool applyScenePatch(const ChangeRequest &request, std::uint32_t target_ea) {
    const ScenePatchRange *range = findScenePatchRange(request.feature_num);
    if (range == nullptr) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > range->item_count) {
        logWarn(
            "scene patch invalid item feature=%d item=%d target=0x%X switch=0x%X count=%d",
            request.feature_num,
            request.int_value,
            target_ea,
            range->switch_ea,
            range->item_count);
        return true;
    }

    const int patch_index = range->first_patch_index + request.int_value - 1;
    GameMemoryPatchResult result = applyGameMemoryPatchByIndex(patch_index);
    if (!result.ok) {
        logWarn(
            "scene patch failed feature=%d item=%d index=%d target=0x%X switch=0x%X error=%s",
            request.feature_num,
            request.int_value,
            patch_index,
            target_ea,
            range->switch_ea,
            result.error ? result.error : "unknown");
        return true;
    }

    logInfo(
        "scene patch applied feature=%d item=%d index=%d payload=%s target=0x%X switch=0x%X",
        request.feature_num,
        request.int_value,
        result.index,
        result.payload ? result.payload : "",
        target_ea,
        range->switch_ea);
    return true;
}

bool sendPortal(const ChangeRequest &request, std::uint32_t target_ea) {
    const PortalRange *range = findPortalRange(request.feature_num);
    if (range == nullptr) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > range->item_count) {
        logWarn(
            "portal command invalid item feature=%d item=%d target=0x%X switch=0x%X count=%d",
            request.feature_num,
            request.int_value,
            target_ea,
            range->switch_ea,
            range->item_count);
        return true;
    }

    const int patch_index = range->first_patch_index + request.int_value - 1;
    const char *display_name = portalDisplayNameForPatchIndex(patch_index);
    if (!sendPortalCommandByPatchIndex(patch_index, display_name ? display_name : "")) {
        logWarn(
            "portal command failed feature=%d item=%d index=%d target=0x%X switch=0x%X",
            request.feature_num,
            request.int_value,
            patch_index,
            target_ea,
            range->switch_ea);
    }
    return true;
}

bool sendDirectLevelChange(const ChangeRequest &request, std::uint32_t target_ea) {
    const DirectLevelRange *range = findDirectLevelRange(request.feature_num);
    if (range == nullptr) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > range->item_count) {
        logWarn(
            "ChangeLevelWithFade invalid item feature=%d item=%d target=0x%X switch=0x%X count=%d",
            request.feature_num,
            request.int_value,
            target_ea,
            range->switch_ea,
            range->item_count);
        return true;
    }

    const int patch_index = range->first_patch_index + request.int_value - 1;
    if (!sendChangeLevelWithFadeCommandByPatchIndex(patch_index)) {
        logWarn(
            "ChangeLevelWithFade failed feature=%d item=%d index=%d target=0x%X switch=0x%X",
            request.feature_num,
            request.int_value,
            patch_index,
            target_ea,
            range->switch_ea);
    }
    return true;
}

bool applyOriginalDefaultNoOpCase(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 818) {
        return false;
    }

    logInfo(
        "feature 818 exposed direct-level row reaches original default target=0x%X primary_case=1720",
        target_ea);
    return true;
}

bool sendFeature944PlacePlaceablePickup(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 944) {
        return false;
    }

    constexpr int kFeature944CaseId = 1846;
    constexpr const char *kPlacePlaceablePickupTemplate =
        "local e = game:eventBarn():AddEventByMetaName(\"PlacePlaceablePickup\") if e then     "
        "local t = {{1,0,0,0},{0,1,0,0},{0,0,1,0},{%f, %f, %f, 1}}     "
        "e:SetTransform(t)     e:defName(\"%s\")     e:Start(0) end";
    constexpr const char *kFeature944TemplateSha256 =
        "f7fe7f00a3a3b84dee871ec86dd1efc1ba11aeb5e9d587fb41bfbf22b262e1f1";
    constexpr const char *kFeature944TableSha256 =
        "3bc6bd5ed8378bedfc14b76c254f889c626b1020d6318a20dd5eb40e5d5f4041";

    const int item_count = static_cast<int>(sizeof(kFeature944PlaceableProps) / sizeof(kFeature944PlaceableProps[0]));
    if (request.int_value < 1 || request.int_value > item_count) {
        logWarn(
            "feature 944 PlacePlaceablePickup invalid item=%d target=0x%X case=%d count=%d table=qword_3829F0",
            request.int_value,
            target_ea,
            kFeature944CaseId,
            item_count);
        return true;
    }

    GamePosition pos;
    if (!readOriginalAvatarPosition(&pos)) {
        logWarn(
            "feature 944 PlacePlaceablePickup position resolve failed item=%d target=0x%X case=%d helper=sub_181398 chain=qword_364068",
            request.int_value,
            target_ea,
            kFeature944CaseId);
        return true;
    }

    const char *def_name = kFeature944PlaceableProps[request.int_value - 1];
    char command[0x400];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kPlacePlaceablePickupTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        def_name);
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 944 PlacePlaceablePickup format truncated item=%d target=0x%X written=%d max=0x400 defName=%s",
            request.int_value,
            target_ea,
            written,
            def_name);
        return true;
    }

    logInfo(
        "feature 944 PlacePlaceablePickup item=%d defName=%s target=0x%X case=%d helper=sub_181398 table=qword_3829F0 template=xmmword_3B8CD0 source=unk_2E7169 key=xmmword_293A60 chain=qword_364068 pos=(%.6f,%.6f,%.6f) sha_template=%s sha_table=%s",
        request.int_value,
        def_name,
        target_ea,
        kFeature944CaseId,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        kFeature944TemplateSha256,
        kFeature944TableSha256);
    if (!sendGameCommand(command)) {
        logWarn(
            "feature 944 PlacePlaceablePickup command failed item=%d target=0x%X defName=%s",
            request.int_value,
            target_ea,
            def_name);
    }
    return true;
}

bool applyFeature934PlaceableDefName(JNIEnv *env, const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 934) {
        return false;
    }

    {
        std::lock_guard<std::mutex> lock(g_feature_934_lock);
        g_feature_934_placeable_def_name = request.string_value;
    }

    std::string prompt = u8"物品名称已设置为: ";
    prompt += request.string_value;
    showToast(env, request.context, prompt, true);

    logInfo(
        "feature 934 custom placeable defName updated target=0x%X primary_case=1836 source=GetStringUTFChars/qword_385560/sub_1EB908 len=%zu",
        target_ea,
        request.string_value.size());
    return true;
}

bool sendFeature935CustomPlaceable(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 935) {
        return false;
    }

    constexpr const char *kPlacePlaceablePickupTemplate =
        "local e = game:eventBarn():AddEventByMetaName(\"PlacePlaceablePickup\") if e then     "
        "local t = {{1,0,0,0},{0,1,0,0},{0,0,1,0},{%f, %f, %f, 1}}     "
        "e:SetTransform(t)     e:defName(\"%s\")     e:Start(0) end";
    constexpr const char *kFeature935TemplateSha256 =
        "f7fe7f00a3a3b84dee871ec86dd1efc1ba11aeb5e9d587fb41bfbf22b262e1f1";

    std::string def_name;
    {
        std::lock_guard<std::mutex> lock(g_feature_934_lock);
        def_name = g_feature_934_placeable_def_name;
    }

    GamePosition pos;
    if (!readOriginalAvatarPosition(&pos)) {
        logWarn(
            "feature 935 custom PlacePlaceablePickup position resolve failed target=0x%X primary_case=1837 helper=sub_16D388 chain=qword_364068",
            target_ea);
        return true;
    }

    char command[0x400];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kPlacePlaceablePickupTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        def_name.c_str());
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 935 custom PlacePlaceablePickup format truncated target=0x%X written=%d max=0x400 defName_len=%zu",
            target_ea,
            written,
            def_name.size());
        return true;
    }

    logInfo(
        "feature 935 custom PlacePlaceablePickup target=0x%X primary_case=1837 helper=loc_1C1EA0 template=xmmword_3C8750 source=unk_2F17AD key=xmmword_295C70 chain=qword_364068 defName=%s pos=(%.6f,%.6f,%.6f) sha_template=%s",
        target_ea,
        def_name.c_str(),
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        kFeature935TemplateSha256);
    if (!sendGameCommand(command)) {
        logWarn(
            "feature 935 custom PlacePlaceablePickup command failed target=0x%X defName=%s",
            target_ea,
            def_name.c_str());
    }
    return true;
}

bool sendFeature925RawGameCommand(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 925) {
        return false;
    }

    if (request.string_value.empty()) {
        logWarn("feature 925 raw game command missing string target=0x%X source=GetStringUTFChars/sub_18068C", target_ea);
        return true;
    }

    logInfo(
        "feature 925 raw game command target=0x%X source=GetStringUTFChars/sub_18068C len=%zu",
        target_ea,
        request.string_value.size());
    if (!sendGameCommand(request.string_value)) {
        logWarn("feature 925 raw game command dispatch failed target=0x%X", target_ea);
    }
    return true;
}

bool sendFeature927WaxSpawner(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 927) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 927 WaxPickupSpawnerZone"), target_ea)) {
        return true;
    }
    const int count = static_cast<int>(g_feature_926_candle_count.load());
    constexpr const char *kWaxSpawnerTemplate =
        R"AM(local spawnPos = {%f, %f, %f} local colorTypes = {46,52,54,56,48,58,50,60} for _, ct in ipairs(colorTypes) do     local e = game:eventBarn():AddEventByMetaName("WaxPickupSpawnerZone")     if not e then print("创建失败") return end     local guidClump = game:clumpBarn():CreateClumpWithSize(10)     for j = 0, 9 do         guidClump:SetAt(j, game:bstGuidBarn():CreateBSTGuid())     end     local marker = game:markerBarn():CreateMarker(0)     marker:pos({spawnPos[1], spawnPos[2] - 5, spawnPos[3]})     marker:scale({10, 10, 10})     local markerClump = game:clumpBarn():CreateClumpWithSize(1)     markerClump:SetAt(0, marker)     e:useSourceMeshOrientation(false)     e:vertBasedHugGround(false)     e:networkedPickups(true)     e:autoCollectWax(false)     e:requiresActiveSeasonPass(false)     e:cameraHintWhenPicking(false)     e:requiresActiveSeason(false)     e:disablePickupButton(false)     e:useEmberRegions(false)     e:pickupSFX("ShellOpen")     e:jackpotSFX("ShellOpen")     e:animationOnPickup("Pickup")     e:spawnRegion(markerClump)     e:guids(guidClump)     e:bstGuid(2105409868)     e:spawnCountMin(%d)     e:spawnCountMax(%d)     e:waxPerSpawnMin(1)     e:waxPerSpawnMax(1)     e:waxSpawnMult(1)     e:jackpotPlayersRequired(1)     e:jackpotProbability(0.0)     e:jackpotWax(1)     e:spawnType(3)     e:currencyType(ct)     e:animationTime(2)     e:slopeStrictnessMult(1)     e:onEachCollected(nil)     e:pickupMeshTypes(nil)     e:jackpotMeshType(nil)     e:onAllWaxCollected(nil)     e:Start(0) end)AM";

    char command[0x1000];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kWaxSpawnerTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        count,
        count);
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 927 WaxPickupSpawnerZone format truncated target=0x%X written=%d max=0x1000 count=%d",
            target_ea,
            written,
            count);
        return true;
    }

    logInfo(
        "feature 927 WaxPickupSpawnerZone target=0x%X pos=(%.6f,%.6f,%.6f) memory=%d count=%d template=0x2F0863 length=0x5F2 state_qword=0x37BBD0",
        target_ea,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0,
        count);
    if (!sendGameCommand(command)) {
        logWarn("feature 927 WaxPickupSpawnerZone command failed target=0x%X count=%d", target_ea, count);
    }
    return true;
}

bool sendFeature929SocialFireworks(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 929) {
        return false;
    }

    const int count = static_cast<int>(g_feature_928_firework_count.load());
    constexpr const char *kSocialFireworksTemplate =
        "for i=1,%d do     local e = game:eventBarn():AddEventByMetaName('SocialFireworks')     if e then e:Start(0) end end";

    char command[0x100];
    const int written = std::snprintf(command, sizeof(command), kSocialFireworksTemplate, count);
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 929 SocialFireworks format truncated target=0x%X written=%d max=0x100 count=%d",
            target_ea,
            written,
            count);
        return true;
    }

    logInfo(
        "feature 929 SocialFireworks target=0x%X count=%d template=0x2F0E55 length=0x74 key=0x295CC0 state_qword=0x37BBD8",
        target_ea,
        count);
    if (!sendGameCommand(command)) {
        logWarn("feature 929 SocialFireworks command failed target=0x%X count=%d", target_ea, count);
    }
    return true;
}

bool sendFeature930ShardFall(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 930) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 930 ManualShardFall"), target_ea)) {
        return true;
    }
    constexpr const char *kManualShardFallTemplate =
        R"AM(local e=game:eventBarn():AddEventByMetaName("ManualShardFall") if e then local m=game:markerBarn():CreateMarker(0) m:pos({%f, %f, %f}) local c=game:clumpBarn():CreateClumpWithSize(1) c:SetAt(0,m) e:spawnPositions(c) e:Start(0) end)AM";

    char command[0x400];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kManualShardFallTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z));
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 930 ManualShardFall format truncated target=0x%X written=%d max=0x400",
            target_ea,
            written);
        return true;
    }

    logInfo(
        "feature 930 ManualShardFall target=0x%X item=%d pos=(%.6f,%.6f,%.6f) memory=%d template=0x2F1040 length=0xE7 key=0x295C90",
        target_ea,
        request.int_value,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0);
    if (!sendGameCommand(command)) {
        logWarn("feature 930 ManualShardFall command failed target=0x%X item=%d", target_ea, request.int_value);
    }
    return true;
}

bool sendFeature931Shardnado(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 931) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 931 Shardnado"), target_ea)) {
        return true;
    }
    constexpr const char *kShardnadoTemplate =
        R"AM(local e = game:eventBarn():AddEventByMetaName("Shardnado") if e then local t = {{1,0,0,0},{0,1,0,0},{0,0,1,0},{%f, %f, %f, 1}} e:SetTransform(t) local p = ShardnadoParams.new() if p then p:maxOrbitRate(0.6) p:maxRadius(10.0) p:minRadius(5.0) p:numShards(16) p:shardScale(1.0) p:minOrbitRate(0.3) e:params(p) end e:Start(0) end)AM";

    char command[0x400];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kShardnadoTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z));
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 931 Shardnado format truncated target=0x%X written=%d max=0x400",
            target_ea,
            written);
        return true;
    }

    logInfo(
        "feature 931 Shardnado target=0x%X pos=(%.6f,%.6f,%.6f) memory=%d template=0x2F1127 length=0x147 key64=0x3F2D755557573B91",
        target_ea,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0);
    if (!sendGameCommand(command)) {
        logWarn("feature 931 Shardnado command failed target=0x%X", target_ea);
    }
    return true;
}

bool sendFeature932SocialRandomGame(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 932) {
        return false;
    }

    constexpr const char *kSocialRandomGameCommand =
        R"AM(local e=game:eventBarn():AddEventByMetaName("SocialRandomGame") if e then e:Start(0) end)AM";

    logInfo(
        "feature 932 SocialRandomGame target=0x%X template=0x2F126E length=0x59 key=0x295C80 switch_case=1834 dispatcher=sub_18068C",
        target_ea);
    if (!sendGameCommand(kSocialRandomGameCommand)) {
        logWarn("feature 932 SocialRandomGame command failed target=0x%X", target_ea);
    }
    return true;
}

bool sendFeature953FireworkShow(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 953) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 953 FireWorkShow"), target_ea)) {
        return true;
    }
    const int count = static_cast<int>(g_feature_952_firework_count.load());
    constexpr const char *kFireworkShowTemplate =
        R"AM(local m=game:markerBarn():CreateMarker(0) m:pos({%f,%f,%f}) local e=game:eventBarn():AddEventByMetaName("FireWorkShow") if e then e:sourceMarker(m) e:emitterDef("Firework") e:fireworkCount(%d) e:Start(0) end)AM";

    char command[0x400];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kFireworkShowTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        count);
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 953 FireWorkShow format truncated target=0x%X written=%d max=0x400 count=%d",
            target_ea,
            written,
            count);
        return true;
    }

    logInfo(
        "feature 953 FireWorkShow target=0x%X pos=(%.6f,%.6f,%.6f) memory=%d count=%d template=0x2F0F70 length=0xD0 key=0x295CA0 state_qword=0x37BBE0",
        target_ea,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0,
        count);
    if (!sendGameCommand(command)) {
        logWarn("feature 953 FireWorkShow command failed target=0x%X count=%d", target_ea, count);
    }
    return true;
}

bool sendFeature955OutfitCloset(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 955) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 955 OutfitSelect"), target_ea)) {
        return true;
    }
    constexpr const char *kOutfitClosetTemplate =
        R"AM(local t={0,3,2,1,8} for i=1,5 do     local a=i*1.25664     local x=%f + 0.6*math.cos(a)     local z=%f + 0.6*math.sin(a)     local m=game:markerBarn():CreateMarker(0)     m:pos({x, %f, z})     local e=game:eventBarn():AddEventByMetaName("OutfitSelect")     if e then         e:shopName("OutfitShop") e:avatarRegion(m) e:closet(true)         e:raycastButtonToFloor(true) e:onlyOneTab(false) e:lightingIntensity(1.0)         e:showAllPurchasedType(t[i]) e:showAllPurchased(true) e:autoStart(false)         e:Start(0)     end end)AM";

    char command[0x800];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kOutfitClosetTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.z),
        static_cast<double>(pos.y));
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 955 OutfitSelect format truncated target=0x%X written=%d max=0x800",
            target_ea,
            written);
        return true;
    }

    logInfo(
        "feature 955 OutfitSelect target=0x%X pos=(%.6f,%.6f,%.6f) memory=%d template=0x2F12C7 length=0x20F key64=0x79D905CFE5093BFD",
        target_ea,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0);
    if (!sendGameCommand(command)) {
        logWarn("feature 955 OutfitSelect command failed target=0x%X", target_ea);
    }
    return true;
}

bool sendFeature956ConstrainPlayer(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 956) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 956 ConstrainPlayerToLocation"), target_ea)) {
        return true;
    }
    constexpr const char *kConstrainPlayerTemplate =
        R"AM(local m=game:markerBarn():CreateMarker(0) m:pos({%f,%f,%f}); local e=game:eventBarn():AddEventByMetaName("ConstrainPlayerToLocation") if e then     e:constraintObject(m)     e:addCamHintToObject(true)     e:prioritizeCamHintToObject(false)     e:teleportIfTooFar(true)     e:rangeToApplyForce(20.0)     e:minPlayerDistanceFromConstraintObject(0.0)     e:maxPlayerDistanceFromConstraintObject(5.0)     e:ignoreDistance(0.0)     e:reorientPlayerWhenForcing(true)     e:maxForceToConstrain(250.0)     e:Start(0); end; local t={{1,0,0,0},{0,1,0,0},{0,0,1,0},{%f,%f,%f,1}}; local e2=game:eventBarn():AddEventByMetaName("DisplayTextStandard"); if e2 then e2:SetTransform(t) e2:text("约束已生效") e2:duration(3) e2:Start(0); end)AM";

    char command[0x800];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kConstrainPlayerTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z));
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 956 ConstrainPlayerToLocation format truncated target=0x%X written=%d max=0x800",
            target_ea,
            written);
        return true;
    }

    logInfo(
        "feature 956 ConstrainPlayerToLocation target=0x%X pos=(%.6f,%.6f,%.6f) memory=%d template=0x2F14D6 length=0x2D7 key64=0xA1A74117A7DD0D85",
        target_ea,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0);
    if (!sendGameCommand(command)) {
        logWarn("feature 956 ConstrainPlayerToLocation command failed target=0x%X", target_ea);
    }
    return true;
}

bool sendFeature990LaunchFirework(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 990) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 990 LaunchFirework"), target_ea)) {
        return true;
    }
    constexpr const char *kLaunchFireworkTemplate =
        R"AM(local e=game:eventBarn():AddEventByMetaName("LaunchFirework") if e then local m=game:markerBarn():CreateMarker(0) m:pos({%f, %f, %f}) e:sourceMarker(m) e:Start(0) end)AM";

    char command[0x400];
    const int written = std::snprintf(
        command,
        sizeof(command),
        kLaunchFireworkTemplate,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z));
    if (written < 0 || written >= static_cast<int>(sizeof(command))) {
        logWarn(
            "feature 990 LaunchFirework format truncated target=0x%X written=%d max=0x400",
            target_ea,
            written);
        return true;
    }

    logInfo(
        "feature 990 LaunchFirework target=0x%X pos=(%.6f,%.6f,%.6f) memory=%d template=0x2F0EC9 length=0xA7 key=0x295CB0",
        target_ea,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0);
    if (!sendGameCommand(command)) {
        logWarn("feature 990 LaunchFirework command failed target=0x%X", target_ea);
    }
    return true;
}

bool sendFeature1010Command(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1010) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > 8) {
        logWarn(
            "indexed game command invalid item feature=%d item=%d target=0x%X switch=0x1C351C count=8",
            request.feature_num,
            request.int_value,
            target_ea);
        return true;
    }

    const int command_index = 40 + request.int_value - 1;
    if (!sendIndexedGameCommand(command_index)) {
        logWarn(
            "indexed game command failed feature=%d item=%d command_index=%d target=0x%X switch=0x1C351C",
            request.feature_num,
            request.int_value,
            command_index,
            target_ea);
    }
    return true;
}

void runFeature1020PatternWorker(int item, std::uint32_t target_ea) {
    std::uintptr_t pattern_base = 0;
    if (!resolvePointerChain(kFeature1020PatternChain, 4, &pattern_base)) {
        logWarn(
            "feature 1020 pattern target resolve failed item=%d target=0x%X chain=qword_364318",
            item,
            target_ea);
        return;
    }

    const Feature1020Pattern &pattern = kFeature1020Patterns[item - 1];
    const std::uintptr_t write_base = pattern_base + 0x38;

    if (!sendIndexedGameCommand(53)) {
        logWarn(
            "feature 1020 pre-command failed item=%d command_index=53 target=0x%X",
            item,
            target_ea);
    }

    std::size_t written = 0;
    for (std::size_t i = 0; i < pattern.size(); ++i) {
        if (writeOriginalProcTypedInt(write_base + i, pattern[i], 1)) {
            ++written;
        } else {
            logWarn(
                "feature 1020 pattern byte write failed item=%d target=0x%X address=0x%llX index=%zu value=%u",
                item,
                target_ea,
                static_cast<unsigned long long>(write_base + i),
                i,
                static_cast<unsigned int>(pattern[i]));
        }
    }

    logInfo(
        "feature 1020 pattern item=%d target=0x%X pattern_base=0x%llX write_base=0x%llX bytes=%zu table=qword_37C6B0 source_chain=qword_364318",
        item,
        target_ea,
        static_cast<unsigned long long>(pattern_base),
        static_cast<unsigned long long>(write_base),
        written);

    std::this_thread::sleep_for(std::chrono::microseconds(0x61A80));

    if (!sendIndexedGameCommand(20)) {
        logWarn(
            "feature 1020 post-command failed item=%d command_index=20 target=0x%X",
            item,
            target_ea);
    }
}

bool applyFeature1020Pattern(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1020) {
        return false;
    }

    if (request.int_value < 1 ||
        request.int_value > static_cast<int>(sizeof(kFeature1020Patterns) / sizeof(kFeature1020Patterns[0]))) {
        logWarn(
            "feature 1020 pattern invalid item=%d target=0x%X switch=0x1C3160 count=18",
            request.int_value,
            target_ea);
        return true;
    }

    const int item = request.int_value;
    std::thread([item, target_ea]() {
        runFeature1020PatternWorker(item, target_ea);
    }).detach();
    return true;
}

void runFeature1021CustomPatternWorker(const std::string &pattern, std::uint32_t target_ea) {
    std::uintptr_t pattern_base = 0;
    if (!resolvePointerChain(kFeature1020PatternChain, 4, &pattern_base)) {
        logWarn(
            "feature 1021 custom firework pattern target resolve failed target=0x%X chain=qword_364318 helper=sub_180ACC",
            target_ea);
        return;
    }

    const std::uintptr_t write_base = pattern_base + 0x38;

    if (!sendIndexedGameCommand(53)) {
        logWarn(
            "feature 1021 custom firework pre-command failed command_index=53 target=0x%X helper=sub_180ACC",
            target_ea);
    }

    constexpr std::size_t kPatternBytes = 0x30;
    std::size_t written = 0;
    for (std::size_t i = 0; i < kPatternBytes; ++i) {
        const unsigned char value =
            i < pattern.size() ? static_cast<unsigned char>(pattern[i]) : static_cast<unsigned char>(0);
        if (writeOriginalProcTypedInt(write_base + i, value, 1)) {
            ++written;
        } else {
            logWarn(
                "feature 1021 custom firework byte write failed target=0x%X address=0x%llX index=%zu value=%u",
                target_ea,
                static_cast<unsigned long long>(write_base + i),
                i,
                static_cast<unsigned int>(value));
        }
    }

    logInfo(
        "feature 1021 custom firework pattern target=0x%X pattern_base=0x%llX write_base=0x%llX bytes=%zu/0x30 input_with_dot_len=%zu helper=sub_180ACC source_chain=qword_364318",
        target_ea,
        static_cast<unsigned long long>(pattern_base),
        static_cast<unsigned long long>(write_base),
        written,
        pattern.size());

    std::this_thread::sleep_for(std::chrono::microseconds(0x61A80));

    if (!sendIndexedGameCommand(20)) {
        logWarn(
            "feature 1021 custom firework post-command failed command_index=20 target=0x%X helper=sub_180ACC",
            target_ea);
    }
}

bool applyFeature1021CustomPattern(JNIEnv *env, const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1021) {
        return false;
    }

    if (request.string_value.empty()) {
        logInfo(
            "feature 1021 custom firework pattern empty input ignored target=0x%X primary_case=1923",
            target_ea);
        return true;
    }

    std::string pattern;
    pattern.reserve(request.string_value.size() + 1);
    pattern.push_back('.');
    pattern += request.string_value;

    runFeature1021CustomPatternWorker(pattern, target_ea);

    std::string prompt = u8"烟花: ";
    prompt += request.string_value;
    showToast(env, request.context, prompt, true);

    logInfo(
        "feature 1021 custom firework pattern input accepted target=0x%X primary_case=1923 source=GetStringUTFChars/sub_1E6F18/sub_180ACC toast=0x2A0980 len=%zu",
        target_ea,
        request.string_value.size());
    return true;
}

bool sendFeature1011Command(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1011) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > 18) {
        logWarn(
            "feature 1011 audience command invalid item=%d target=0x%X switch=0x1C3474 count=18",
            request.int_value,
            target_ea);
        return true;
    }

    if (request.int_value == 1) {
        if (!sendIndexedGameCommand(52)) {
            logWarn(
                "feature 1011 audience default command failed item=%d command_index=52 target=0x%X switch=0x1C3474",
                request.int_value,
                target_ea);
        }
        return true;
    }

    const int model_index = request.int_value - 1;
    if (!sendAudienceModelCommand(model_index)) {
        logWarn(
            "feature 1011 audience model command failed item=%d model_index=%d target=0x%X switch=0x1C3474",
            request.int_value,
            model_index,
            target_ea);
    }
    return true;
}

bool sendFeature1030Command(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1030) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > 11) {
        logWarn(
            "feature 1030 relationship GrantSpell invalid item=%d target=0x%X switch=0x1C3224 count=11",
            request.int_value,
            target_ea);
        return true;
    }

    if (!sendRelationshipGrantSpellCommand(request.int_value)) {
        logWarn(
            "feature 1030 relationship GrantSpell failed item=%d target=0x%X switch=0x1C3224",
            request.int_value,
            target_ea);
    }
    return true;
}

bool applyFeature1005Toggle(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1005) {
        return false;
    }

    g_feature_1005_insect_switch.store(request.bool_value);
    logInfo(
        "feature 1005 insect switch=%d target=0x%X state_byte=0x385520",
        request.bool_value ? 1 : 0,
        target_ea);
    return true;
}

bool sendFeature1006Command(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1006) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 1006 SpawnMotes"), target_ea)) {
        return true;
    }
    const int count = static_cast<int>(g_feature_1004_spawn_count.load());
    const int insect_type = g_feature_1005_insect_switch.load()
        ? static_cast<int>(g_feature_1006_insect_type.load())
        : 0;
    const int red = static_cast<int>(g_feature_1001_red.load());
    const int green = static_cast<int>(g_feature_1002_green.load());
    const int blue = static_cast<int>(g_feature_1003_blue.load());

    char command[1024];
    std::snprintf(
        command,
        sizeof(command),
        "local e=game:eventBarn():AddEventByMetaName(\"SpawnMotes\") if e then     local t={{1,0,0,0},{0,1,0,0},{0,0,1,0},{%f,%f,%f,1}}     e:SetTransform(t)     local p=MoteParams.new() if p then e:params(p) end     e:number(%d) e:time(0.5) e:insectType(%d)     e:color({%d,%d,%d,1}) e:useColorInstead(true) e:Start(0) end",
        pos.x,
        pos.y,
        pos.z,
        count,
        insect_type,
        red,
        green,
        blue);

    logInfo(
        "feature 1006 SpawnMotes target=0x%X pos=(%.6f,%.6f,%.6f) memory=%d count=%d insect=%d color=(%d,%d,%d)",
        target_ea,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0,
        count,
        insect_type,
        red,
        green,
        blue);
    if (!sendGameCommand(command)) {
        logWarn("feature 1006 SpawnMotes command failed target=0x%X", target_ea);
    }
    return true;
}

bool applyFeature1007Toggle(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1007) {
        return false;
    }

    g_feature_1007_butterfly_follow.store(request.bool_value);
    logInfo(
        "feature 1007 butterfly follow=%d target=0x%X state_byte=0x385524",
        request.bool_value ? 1 : 0,
        target_ea);
    return true;
}

bool sendFeature1008Command(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1008) {
        return false;
    }

    GamePosition pos;
    if (!readLiveAvatarPosition(&pos, DIAG_TEXT("feature 1008 MusicalMoteTarget"), target_ea)) {
        return true;
    }
    const char *follow = g_feature_1007_butterfly_follow.load() ? "true" : "false";

    char command[1024];
    std::snprintf(
        command,
        sizeof(command),
        "local e = game:eventBarn():AddEventByMetaName(\"MusicalMoteTarget\") if e then     local c = game:clumpBarn():CreateClumpWithSize(1)     local m = game:markerBarn():CreateMarker(0)     m:pos({%f, %f, %f})     c:SetAt(0, m)     e:markers(c)     e:soundName(\"CollectMote\")     e:parentToAvatarXZ(%s)     e:carryAction(kCarryAction_Lift)     e:autoStart(false)     e:Start(0) end",
        pos.x,
        pos.y,
        pos.z,
        follow);

    logInfo(
        "feature 1008 MusicalMoteTarget target=0x%X pos=(%.6f,%.6f,%.6f) memory=%d follow=%s",
        target_ea,
        static_cast<double>(pos.x),
        static_cast<double>(pos.y),
        static_cast<double>(pos.z),
        pos.from_memory ? 1 : 0,
        follow);
    if (!sendGameCommand(command)) {
        logWarn("feature 1008 MusicalMoteTarget command failed target=0x%X", target_ea);
    }
    return true;
}

bool sendFeature1029Command(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1029) {
        return false;
    }

    logInfo(
        "feature 1029 player ride item=%d target=0x%X switch=0x1C31F8",
        request.int_value,
        target_ea);
    return handlePlayerRideFeature1029(request.int_value);
}

bool sendDirectIndexedCommand(const ChangeRequest &request, std::uint32_t target_ea) {
    const DirectIndexedCommandCase *item = findDirectIndexedCommandCase(request.feature_num);
    if (item == nullptr) {
        return false;
    }

    if (!sendIndexedGameCommand(item->command_index)) {
        logWarn(
            "direct indexed game command failed feature=%d command_index=%d target=0x%X expected=0x%X",
            request.feature_num,
            item->command_index,
            target_ea,
            item->target_ea);
    }
    return true;
}

bool sendBoolGatedIndexedCommand(const ChangeRequest &request, std::uint32_t target_ea) {
    int command_index = -1;
    std::atomic_bool *state = nullptr;
    std::uint32_t command_ea = 0;

    switch (request.feature_num) {
        case 450:
            command_index = 50;
            state = &g_force_landscape_enabled;
            command_ea = 0x1C22E0;
            break;
        case 451:
            command_index = 51;
            state = &g_force_portrait_enabled;
            command_ea = 0x1C27E8;
            break;
        default:
            return false;
    }

    state->store(request.bool_value);
    logInfo(
        "bool-gated indexed command feature=%d enabled=%d command_index=%d target=0x%X command_ea=0x%X",
        request.feature_num,
        request.bool_value ? 1 : 0,
        command_index,
        target_ea,
        command_ea);

    if (!request.bool_value) {
        return true;
    }

    if (!sendIndexedGameCommand(command_index)) {
        logWarn(
            "bool-gated indexed command failed feature=%d command_index=%d target=0x%X command_ea=0x%X",
            request.feature_num,
            command_index,
            target_ea,
            command_ea);
    }
    return true;
}

void runFeature801ZeroSlotsWorker(std::uint32_t target_ea) {
    std::uintptr_t slots = 0;
    if (!resolvePointerChain(kFeature801ZeroSlotsChain, 3, &slots)) {
        logWarn(
            "feature 801 zero-slots resolve failed target=0x%X chain=unk_3640D0 worker=sub_1876A0",
            target_ea);
        g_feature_801_zero_slots_enabled.store(false);
        return;
    }

    std::size_t loops = 0;
    while (g_feature_801_zero_slots_enabled.load()) {
        std::size_t written = 0;
        for (std::size_t i = 0; i < 0x200 && g_feature_801_zero_slots_enabled.load(); ++i) {
            if (writeOriginalProcTypedInt(slots + i * 8, 0, 0x10)) {
                ++written;
            }
        }
        ++loops;
        if ((loops & 0x3F) == 1) {
            logInfo(
                "feature 801 zero-slots worker loop target=0x%X base=0x%llX written=%zu/0x200 chain=unk_3640D0",
                target_ea,
                static_cast<unsigned long long>(slots),
                written);
        }
    }

    logInfo(
        "feature 801 zero-slots worker stopped target=0x%X base=0x%llX loops=%zu worker=sub_1876A0",
        target_ea,
        static_cast<unsigned long long>(slots),
        loops);
}

bool applyFeature801ZeroSlots(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 801) {
        return false;
    }

    g_feature_801_zero_slots_enabled.store(request.bool_value);
    if (!g_feature_801_gate.exchange(true)) {
        logInfo(
            "feature 801 zero-slots gate initialized target=0x%X state_byte=0x38553C gate_byte=0x385708 enabled=%d",
            target_ea,
            request.bool_value ? 1 : 0);
        return true;
    }

    if (!request.bool_value) {
        logInfo(
            "feature 801 zero-slots disabled target=0x%X state_byte=0x38553C gate_byte=0x385708 worker=sub_1876A0",
            target_ea);
        return true;
    }

    std::thread([target_ea]() {
        runFeature801ZeroSlotsWorker(target_ea);
    }).detach();
    logInfo(
        "feature 801 zero-slots enabled target=0x%X worker=sub_1876A0 state_byte=0x38553C gate_byte=0x385708 chain=unk_3640D0 stride=8 count=0x200",
        target_ea);
    return true;
}

void runFeature802WardrobeFlagWorker(std::uint32_t target_ea) {
    std::uintptr_t flag = 0;
    if (!resolvePointerChain(kFeature802WardrobeFlagChain, 3, &flag)) {
        logWarn(
            "feature 802 wardrobe flag resolve failed target=0x%X chain=unk_364050 worker=sub_18771C",
            target_ea);
        g_feature_802_wardrobe_flag_enabled.store(false);
        return;
    }

    std::size_t writes = 0;
    while (g_feature_802_wardrobe_flag_enabled.load()) {
        if (writeOriginalProcTypedInt(flag, 257, 4)) {
            ++writes;
        }
    }
    const bool clear_ok = writeOriginalProcTypedInt(flag, 0, 4);
    logInfo(
        "feature 802 wardrobe flag worker stopped target=0x%X address=0x%llX writes=%zu clear_ok=%d worker=sub_18771C",
        target_ea,
        static_cast<unsigned long long>(flag),
        writes,
        clear_ok ? 1 : 0);
}

bool applyFeature802WardrobeFlag(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 802) {
        return false;
    }

    g_feature_802_wardrobe_flag_enabled.store(request.bool_value);
    if (!g_feature_802_gate.exchange(true)) {
        logInfo(
            "feature 802 wardrobe flag gate initialized target=0x%X state_byte=0x385540 gate_byte=0x385709 enabled=%d",
            target_ea,
            request.bool_value ? 1 : 0);
        return true;
    }

    if (!request.bool_value) {
        logInfo(
            "feature 802 wardrobe flag disabled target=0x%X state_byte=0x385540 gate_byte=0x385709 worker=sub_18771C",
            target_ea);
        return true;
    }

    std::thread([target_ea]() {
        runFeature802WardrobeFlagWorker(target_ea);
    }).detach();
    logInfo(
        "feature 802 wardrobe flag enabled target=0x%X worker=sub_18771C state_byte=0x385540 gate_byte=0x385709 chain=unk_364050 value=257 clear=0",
        target_ea);
    return true;
}

void runFeature401DragonWorker(std::uint32_t target_ea) {
    std::uintptr_t target = 0;
    if (!resolveDragonTargetBase(&target, target_ea, 401)) {
        g_feature_401_dragon_enabled.store(false);
        return;
    }

    while (g_feature_401_dragon_enabled.load()) {
        GamePosition pos;
        if (readOriginalAvatarPosition(&pos)) {
            writeDragonPositionSlot(target, pos.x + 0.0f, pos.y - 3.5f, pos.z + 0.0f);
        }
    }
}

void runFeature402DragonWorker(std::uint32_t target_ea) {
    std::uintptr_t target = 0;
    if (!resolveDragonTargetBase(&target, target_ea, 402)) {
        g_feature_402_dragon_enabled.store(false);
        return;
    }

    while (g_feature_402_dragon_enabled.load()) {
        GamePosition pos;
        if (!readOriginalAvatarPosition(&pos)) {
            continue;
        }

        const float y = pos.y - 10.0f;
        writeDragonPositionSlot(target, pos.x + 0.0f, y, pos.z + 0.0f);
        writeDragonPositionSlot(target + kDragonSlotStride, pos.x - 10.0f, y, pos.z + 0.0f);
        writeDragonPositionSlot(target + kDragonSlotStride * 2, pos.x - 20.0f, y, pos.z + 0.0f);
        writeDragonPositionSlot(target + kDragonSlotStride * 3, pos.x + 10.0f, y, pos.z + 0.0f);
        writeDragonPositionSlot(target + kDragonSlotStride * 4, pos.x + 20.0f, y, pos.z + 0.0f);
    }
}

void runFeature403DragonWorker(std::uint32_t target_ea) {
    std::uintptr_t target = 0;
    if (!resolveDragonTargetBase(&target, target_ea, 403)) {
        g_feature_403_dragon_enabled.store(false);
        return;
    }

    int step = 0;
    while (g_feature_403_dragon_enabled.load()) {
        GamePosition pos;
        if (readOriginalAvatarPosition(&pos)) {
            const double angle = static_cast<double>(step) * 3.14159265 / 50.0;
            const double sin_value = std::sin(angle);
            const double cos_value = std::cos(angle);
            const float x_plus = static_cast<float>(pos.x + cos_value * 6.5 + sin_value * 6.5);
            const float z_minus = static_cast<float>(pos.z + cos_value * 6.5 - sin_value * 6.5);
            const float x_minus = static_cast<float>(pos.x + cos_value * 6.5 - sin_value * 6.5);
            const float z_plus = static_cast<float>(pos.z + cos_value * 6.5 + sin_value * 6.5);
            const float y_top = static_cast<float>(pos.y + sin_value * 15.0);
            const float y_forward = static_cast<float>(pos.y + cos_value * 10.0);
            const float y_back = static_cast<float>(pos.y - sin_value * 10.0);
            const float y_bottom = static_cast<float>(pos.y - cos_value * 15.0);

            writeDragonPositionSlot(target, x_plus, y_top, z_minus);
            writeDragonPositionSlot(target + kDragonSlotStride, x_minus, y_forward, z_plus);
            writeDragonPositionSlot(target + kDragonSlotStride * 2, x_plus, pos.y, z_minus);
            writeDragonPositionSlot(target + kDragonSlotStride * 3, x_minus, y_back, z_plus);
            writeDragonPositionSlot(target + kDragonSlotStride * 4, x_plus, y_bottom, z_minus);
        }

        step = (step + 1) % 101;
        std::this_thread::sleep_for(std::chrono::microseconds(20000));
    }
}

bool runDragonWorkerFeature(const ChangeRequest &request, std::uint32_t target_ea) {
    std::atomic_bool *enabled = nullptr;
    std::atomic_bool *gate = nullptr;
    void (*worker)(std::uint32_t) = nullptr;
    std::uint32_t state_ea = 0;
    std::uint32_t gate_ea = 0;
    std::uint32_t worker_ea = 0;

    switch (request.feature_num) {
        case 401:
            enabled = &g_feature_401_dragon_enabled;
            gate = &g_feature_401_gate;
            worker = runFeature401DragonWorker;
            state_ea = 0x385528;
            gate_ea = 0x385701;
            worker_ea = 0x186820;
            break;
        case 402:
            enabled = &g_feature_402_dragon_enabled;
            gate = &g_feature_402_gate;
            worker = runFeature402DragonWorker;
            state_ea = 0x38552C;
            gate_ea = 0x385702;
            worker_ea = 0x186978;
            break;
        case 403:
            enabled = &g_feature_403_dragon_enabled;
            gate = &g_feature_403_gate;
            worker = runFeature403DragonWorker;
            state_ea = 0x385530;
            gate_ea = 0x385703;
            worker_ea = 0x186D10;
            break;
        default:
            return false;
    }

    enabled->store(request.bool_value);
    if (!gate->load()) {
        gate->store(true);
        logInfo(
            "feature %d dragon worker gate initialized target=0x%X state_byte=0x%X gate_byte=0x%X enabled=%d",
            request.feature_num,
            target_ea,
            state_ea,
            gate_ea,
            request.bool_value ? 1 : 0);
        return true;
    }

    logInfo(
        "feature %d dragon worker enabled=%d target=0x%X state_byte=0x%X gate_byte=0x%X worker=0x%X target_chain=qword_364170 source_chain=qword_364068 stride=0x%llX",
        request.feature_num,
        request.bool_value ? 1 : 0,
        target_ea,
        state_ea,
        gate_ea,
        worker_ea,
        static_cast<unsigned long long>(kDragonSlotStride));

    if (!request.bool_value) {
        return true;
    }

    std::thread([worker, target_ea]() {
        worker(target_ea);
    }).detach();
    return true;
}

float originalRandomFloat(float min_value, float max_value) {
    std::random_device device("/dev/urandom");
    std::mt19937 generator(device());
    const float unit = static_cast<float>(generator()) * 2.3283064365386963e-10f;
    return min_value + ((max_value - min_value) * unit);
}

void runFeature410SpeedWeightWorker(std::uint32_t target_ea) {
    std::uintptr_t address = 0;
    if (!resolvePointerChain(kFeature410SpeedWeightChain, 2, &address)) {
        logWarn(
            "feature 410 speed weight chain resolve failed target=0x%X chain=[base+0x484B550]+0x28",
            target_ea);
        g_feature_410_speed_weight_enabled.store(false);
        return;
    }

    while (g_feature_410_speed_weight_enabled.load()) {
        const float value = originalRandomFloat(-0.01f, 0.7f);
        char text[20]{};
        std::snprintf(text, sizeof(text), "%.6f", static_cast<double>(value));
        if (!writeOriginalProcType16Text(address, text)) {
            logWarn(
                "feature 410 speed weight write failed target=0x%X address=0x%llX value='%s'",
                target_ea,
                static_cast<unsigned long long>(address),
                text);
        }
        std::this_thread::sleep_for(std::chrono::seconds(1));
    }

    if (!writeOriginalProcType16Text(address, "1")) {
        logWarn(
            "feature 410 speed weight restore failed target=0x%X address=0x%llX value='1'",
            target_ea,
            static_cast<unsigned long long>(address));
    }
}

bool runFeature410SpeedWeight(JNIEnv *env, const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 410) {
        return false;
    }

    g_feature_410_speed_weight_enabled.store(request.bool_value);
    if (!g_feature_410_gate.load()) {
        g_feature_410_gate.store(true);
        logInfo(
            "feature 410 speed weight gate initialized target=0x%X state_byte=0x385538 gate_byte=0x385704 enabled=%d",
            target_ea,
            request.bool_value ? 1 : 0);
        return true;
    }

    if (!request.bool_value) {
        logInfo(
            "feature 410 speed weight enabled=0 target=0x%X state_byte=0x385538 gate_byte=0x385704",
            target_ea);
        return true;
    }

    showToast(env, request.context, u8"速度：0.01~1", true);
    logInfo(
        "feature 410 speed weight enabled=1 target=0x%X state_byte=0x385538 gate_byte=0x385704 worker=0x1875A0 chain=[base+0x484B550]+0x28 range=-0.01..0.7",
        target_ea);
    std::thread([target_ea]() {
        runFeature410SpeedWeightWorker(target_ea);
    }).detach();
    return true;
}

bool applyFeature411UiScale(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 411) {
        return false;
    }

    const char *value = nullptr;
    std::uint32_t helper_ea = 0;
    switch (request.int_value) {
        case 1:
            value = "0.97";
            helper_ea = 0x200358;
            break;
        case 2:
            value = "0.8";
            helper_ea = 0x200388;
            break;
        case 3:
            value = "0.6";
            helper_ea = 0x2003B8;
            break;
        default:
            logWarn(
                "feature 411 UI scale invalid item=%d target=0x%X",
                request.int_value,
                target_ea);
            return true;
    }

    std::uintptr_t base = 0;
    if (!resolveBootloaderBase(&base)) {
        logWarn(
            "feature 411 UI scale failed target=0x%X module=%s item=%d value=%s error=module-not-loaded",
            target_ea,
            kBootloaderModule,
            request.int_value,
            value);
        return true;
    }

    const std::uintptr_t address = base + 0x006477C8;
    const bool ok = writeOriginalProcType16Text(address, value);
    logInfo(
        "feature 411 UI scale item=%d target=0x%X helper=0x%X module=%s qword_364578=0x6477C8 address=0x%llX value=%s ok=%d",
        request.int_value,
        target_ea,
        helper_ea,
        kBootloaderModule,
        static_cast<unsigned long long>(address),
        value,
        ok ? 1 : 0);
    return true;
}

bool runFeature1009Worker(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1009) {
        return false;
    }

    g_feature_1009_enabled.store(request.bool_value);
    logInfo(
        "feature 1009 face blink worker enabled=%d target=0x%X worker=0x18736C",
        request.bool_value ? 1 : 0,
        target_ea);

    if (!request.bool_value) {
        return true;
    }

    bool already_running = g_feature_1009_worker_running.exchange(true);
    if (already_running) {
        return true;
    }

    std::thread([]() {
        bool alternate = false;
        while (g_feature_1009_enabled.load()) {
            sendIndexedGameCommand(alternate ? 37 : 38);
            alternate = !alternate;
            std::this_thread::sleep_for(std::chrono::microseconds(300000));
        }
        sendIndexedGameCommand(37);
        g_feature_1009_worker_running.store(false);
    }).detach();
    return true;
}

bool runFeature1012Worker(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 1012) {
        return false;
    }

    g_feature_1012_enabled.store(request.bool_value);
    logInfo(
        "feature 1012 AvatarSetArmorImm worker enabled=%d target=0x%X worker=0x1873CC state_byte=0x385554",
        request.bool_value ? 1 : 0,
        target_ea);

    if (!request.bool_value) {
        return true;
    }

    bool already_running = g_feature_1012_worker_running.exchange(true);
    if (already_running) {
        return true;
    }

    std::thread([]() {
        bool value = false;
        while (g_feature_1012_enabled.load()) {
            value = !value;
            char command[256];
            std::snprintf(
                command,
                sizeof(command),
                "local e=game:eventBarn():AddEventByMetaName('AvatarSetArmorImm') if e then e:value(%d) e:Start(0) end",
                value ? 1 : 0);
            sendGameCommand(command);
            std::this_thread::sleep_for(std::chrono::microseconds(300000));
        }
        g_feature_1012_worker_running.store(false);
    }).detach();
    return true;
}

bool runFeature620Worker(const ChangeRequest &request, std::uint32_t target_ea) {
    if (request.feature_num != 620) {
        return false;
    }

    g_feature_620_enabled.store(request.bool_value);
    logInfo(
        "feature 620 random color enabled=%d target=0x%X state_byte=0x38554C gate_byte=0x385707 worker=0x1879A8",
        request.bool_value ? 1 : 0,
        target_ea);

    if (!g_feature_620_gate.load()) {
        g_feature_620_gate.store(true);
        logInfo("feature 620 gate byte_385707 initialized from original off-path target=0x%X", target_ea);
        return true;
    }

    if (!request.bool_value) {
        return true;
    }

    bool already_running = g_feature_620_worker_running.exchange(true);
    if (already_running) {
        return true;
    }

    std::thread([]() {
        std::mt19937 rng(std::random_device{}());
        std::uniform_real_distribution<float> dist(-10.0f, 20.0f);
        while (g_feature_620_enabled.load()) {
            std::uintptr_t base = 0;
            if (resolveBootloaderBase(&base)) {
                const std::uintptr_t address = base + 0x048D3E04;
                writeFloatRaw(address, dist(rng));
                writeFloatRaw(address + 4, dist(rng));
                writeFloatRaw(address + 8, dist(rng));
            }
            std::this_thread::sleep_for(std::chrono::microseconds(15000000));
        }

        std::uintptr_t base = 0;
        if (resolveBootloaderBase(&base)) {
            const std::uintptr_t address = base + 0x048D3E04;
            writeFloatRaw(address, 1.0f);
            writeFloatRaw(address + 4, 1.0f);
            writeFloatRaw(address + 8, 1.0f);
        }
        g_feature_620_worker_running.store(false);
    }).detach();
    return true;
}

bool sendIndexedCommandRange(const ChangeRequest &request, std::uint32_t target_ea) {
    const IndexedCommandRange *range = findIndexedCommandRange(request.feature_num);
    if (range == nullptr) {
        return false;
    }

    if (request.int_value < 1 || request.int_value > range->item_count) {
        logWarn(
            "indexed game command range invalid item feature=%d item=%d target=0x%X switch=0x%X count=%d",
            request.feature_num,
            request.int_value,
            target_ea,
            range->switch_ea,
            range->item_count);
        return true;
    }

    const int command_index = range->command_indices[request.int_value - 1];
    if (!sendIndexedGameCommand(command_index)) {
        logWarn(
            "indexed game command range failed feature=%d item=%d command_index=%d target=0x%X switch=0x%X",
            request.feature_num,
            request.int_value,
            command_index,
            target_ea,
            range->switch_ea);
    }
    return true;
}

void runLogin(JNIEnv *env, const ChangeRequest &request) {
    const std::string card = request.string_value.empty() ? g_card_login_input : request.string_value;
//    if (card.empty()) {
//        return;
//    }

//    AuthResult result = loginWithCard(card, g_android_id);
//    if (result.ok) {
        g_authenticated = true;
        setMenuLoggedIn(env, true);
        showHtmlToast(env, request.context, formatLoginSuccessToast(AuthResult()), false);
        switchToAuthenticatedMenuLayout(env);
        sendIndexedGameCommand(16);
        startLoginSuccessWorkers();
//        return;
//    }

//    if (!result.message.empty()) {
//        showToast(env, request.context, result.message, true);
//    }
}

bool isCardLoginInputCase(int feature_num) {
    return feature_num == -10;
}

bool isCardLoginButtonCase(int feature_num) {
    return feature_num == -11;
}

bool isCardUnbindButtonCase(int feature_num) {
    return feature_num == -12;
}

bool isDeveloperKeyInputCase(int feature_num) {
    return feature_num == -901;
}

bool isDeveloperLoginButtonCase(int feature_num) {
    return feature_num == -902;
}

void runDeveloperLogin(JNIEnv *env, const ChangeRequest &request) {
    const std::string key = request.string_value.empty() ? g_developer_key_input : request.string_value;
//    if (key.empty()) {
//        return;
//    }

//    AuthResult result = loginWithDeveloperKey(key, g_android_id);
//    if (result.ok) {
        g_developer_unlocked = true;
        showHtmlToast(env, request.context, formatLoginSuccessToast(AuthResult()), false);
        callMenuReloadFeatures(env);
        originalLogPrint(ANDROID_LOG_INFO, "Mod_Menu", u8"登录后重新加载功能");
        logInfo("developer settings unlocked");
//        return;
//    }

//    g_developer_unlocked = false;
//    if (!result.message.empty()) {
//        showToast(env, request.context, result.message, true);
//    }
//    callMenuReloadFeatures(env);
//    logWarn("developer settings unlock failed code=%d", result.server_code);
}

void runUnbind(JNIEnv *env, const ChangeRequest &request) {
    const std::string card = request.string_value.empty() ? g_card_login_input : request.string_value;
    if (card.empty()) {
        return;
    }

    AuthResult result = checkSignedValue(card, g_android_id);
    if (result.ok) {
        showHtmlToast(env, request.context, formatSignedSuccessToast(result.signed_num), false);
        return;
    }

    if (!result.message.empty()) {
        showToast(env, request.context, result.message, true);
    }
}

void executeMemoryCase(JNIEnv *env, const ChangeRequest &request, std::uint32_t target_ea) {
    HighValueActionResult high_value = executeHighValueAction(
        request.feature_num,
        request.int_value,
        request.bool_value,
        request.string_value);
    if (high_value.handled) {
        return;
    }

    if (request.feature_num == 1500) {
        std::string prop = feetPropForItemId(request.int_value);
        if (prop.empty()) {
            logWarn("AvatarSetOutfit feet unknown item_id=%d target=0x%X", request.int_value, target_ea);
            return;
        }
        if (!sendAvatarSetFeetCommand(prop)) {
            logWarn("AvatarSetOutfit feet command failed item_id=%d prop='%s'", request.int_value, prop.c_str());
        }
        return;
    }

    if (request.feature_num == 1509) {
        std::string prop = outfitPropForItemId(request.int_value);
        if (prop.empty()) {
            logWarn("AvatarSetOutfit unknown item_id=%d target=0x%X", request.int_value, target_ea);
            return;
        }
        if (!sendAvatarSetOutfitCommand(prop)) {
            logWarn("AvatarSetOutfit command failed item_id=%d prop='%s'", request.int_value, prop.c_str());
        }
        return;
    }

    if (isAvatarSetOutfitSlotFeature(request.feature_num)) {
        std::string value = outfitSlotValueForFeatureItem(request.feature_num, request.int_value);
        const char *method = outfitSlotMethodForFeature(request.feature_num);
        if (value.empty() || method == nullptr) {
            logWarn(
                "AvatarSetOutfit slot unknown feature=%d item_id=%d target=0x%X",
                request.feature_num,
                request.int_value,
                target_ea);
            return;
        }
        if (!sendAvatarSetOutfitSlotCommand(method, value)) {
            logWarn(
                "AvatarSetOutfit %s command failed feature=%d item_id=%d value='%s'",
                method,
                request.feature_num,
                request.int_value,
                value.c_str());
        }
        return;
    }

    if (applyBasicTogglePatchCase(request, target_ea)) {
        return;
    }

    if (applyFeature306SingleDailyQuest(request, target_ea)) {
        return;
    }

    if (applyFeature307AllDailyQuests(request, target_ea)) {
        return;
    }

    if (applyFeature920921CandleRoute(env, request, target_ea)) {
        return;
    }

    if (applyFeature701704SkyMap(request, target_ea)) {
        return;
    }

    if (applyGameSpeedSeekbar(request, target_ea)) {
        return;
    }

    if (applyRouteMapCountSeekbar(request, target_ea)) {
        return;
    }

    if (applyBootloaderFloatSeekbarCase(request, target_ea)) {
        return;
    }

    if (applyBootloaderScaledIntCase(request, target_ea)) {
        return;
    }

    if (applyBootloaderDirectIntCase(request, target_ea)) {
        return;
    }

    if (applyBootloaderBoolProcIntCase(request, target_ea)) {
        return;
    }

    if (applyFeature420ChimesmithBuilder(request, target_ea)) {
        return;
    }

    if (applyFeature508SpellShop(request, target_ea)) {
        return;
    }

    if (applyFeature803ColorPreset(request, target_ea)) {
        return;
    }

    if (applyFeature804811PatchPayloadWrite(request, target_ea)) {
        return;
    }

    if (applyFeature411UiScale(request, target_ea)) {
        return;
    }

    if (applyPointerChainIntWriteCase(request, target_ea)) {
        return;
    }

    if (applyFeature436PatternSelector(request, target_ea)) {
        return;
    }

    if (applyStateOnlyCase(request, target_ea)) {
        return;
    }

    if (applyFeature301HomeRoute(env, request, target_ea)) {
        return;
    }

    if (applyFeature302FastRoute(request, target_ea)) {
        return;
    }

    if (applyFeature309MapRoute(env, request, target_ea)) {
        return;
    }

    if (applyFeature314DyeRoute(env, request, target_ea)) {
        return;
    }

    if (applyFeature360TokenRoute(request, target_ea)) {
        return;
    }

    if (applyFeature361Hangout(request, target_ea)) {
        return;
    }

    if (applyFeature310ValleyRace(request, target_ea)) {
        return;
    }

    if (applyFeature311SeasonAncestor(request, target_ea)) {
        return;
    }

    if (applyFeature312ResidentRevisit(request, target_ea)) {
        return;
    }

    if (applyFeature313OfflineWing(request, target_ea)) {
        return;
    }

    if (applyFeature315AutoSacrifice(request, target_ea)) {
        return;
    }

    if (applyFeature316317CurrentMapWings(env, request, target_ea)) {
        return;
    }

    if (applyFeature318StatueUnlock(request, target_ea)) {
        return;
    }

    if (applyFeature320AncestorWax(request, target_ea)) {
        return;
    }

    if (applyFeature321SendHeartFire(request, target_ea)) {
        return;
    }

    if (applyFeature322CollectHeartFire(request, target_ea)) {
        return;
    }

    if (applyFeature431WingBlast(request, target_ea)) {
        return;
    }

    if (applyFeature435HeartPath(request, target_ea)) {
        return;
    }

    if (applyFeature801ZeroSlots(request, target_ea)) {
        return;
    }

    if (applyFeature802WardrobeFlag(request, target_ea)) {
        return;
    }

    if (applyBoolStateOnlyCase(request, target_ea)) {
        return;
    }

    if (applyFeature443PlaceObjects(request, target_ea)) {
        return;
    }

    if (applyFeature812CutsceneColor(request, target_ea)) {
        return;
    }

    if (applyFeature501MagicSelector(request, target_ea)) {
        return;
    }

    if (applyFeature502LightningSpark(request, target_ea)) {
        return;
    }

    if (applyFeature505SizeWorker(request, target_ea)) {
        return;
    }

    if (applyFeature506SizeSeekbar(request, target_ea)) {
        return;
    }

    if (applyFeature507Wardrobe(request, target_ea)) {
        return;
    }

    if (applyFeature509MagicInput(request, target_ea)) {
        return;
    }

    if (applyFeature923FireworksMagic(request, target_ea)) {
        return;
    }

    if (applyMagicSlotCase(request, target_ea)) {
        return;
    }

    if (applyFeature629TrialWater(request, target_ea)) {
        return;
    }

    if (applyFeature319AllPortals(request, target_ea)) {
        return;
    }

    if (applyFeature323EightPlayerDoor(request, target_ea)) {
        return;
    }

    if (applyFeature630HomeSwitch(request, target_ea)) {
        return;
    }

    if (applyScenePatch(request, target_ea)) {
        return;
    }

    if (sendPortal(request, target_ea)) {
        return;
    }

    if (sendDirectLevelChange(request, target_ea)) {
        return;
    }

    if (applyOriginalDefaultNoOpCase(request, target_ea)) {
        return;
    }

    if (applyFeature934PlaceableDefName(env, request, target_ea)) {
        return;
    }

    if (sendFeature935CustomPlaceable(request, target_ea)) {
        return;
    }

    if (sendFeature944PlacePlaceablePickup(request, target_ea)) {
        return;
    }

    if (sendFeature925RawGameCommand(request, target_ea)) {
        return;
    }

    if (sendFeature927WaxSpawner(request, target_ea)) {
        return;
    }

    if (sendFeature929SocialFireworks(request, target_ea)) {
        return;
    }

    if (sendFeature930ShardFall(request, target_ea)) {
        return;
    }

    if (sendFeature931Shardnado(request, target_ea)) {
        return;
    }

    if (sendFeature932SocialRandomGame(request, target_ea)) {
        return;
    }

    if (sendFeature953FireworkShow(request, target_ea)) {
        return;
    }

    if (sendFeature955OutfitCloset(request, target_ea)) {
        return;
    }

    if (sendFeature956ConstrainPlayer(request, target_ea)) {
        return;
    }

    if (sendFeature990LaunchFirework(request, target_ea)) {
        return;
    }

    if (sendFeature1010Command(request, target_ea)) {
        return;
    }

    if (applyFeature1020Pattern(request, target_ea)) {
        return;
    }

    if (applyFeature1021CustomPattern(env, request, target_ea)) {
        return;
    }

    if (sendFeature1011Command(request, target_ea)) {
        return;
    }

    if (sendFeature1029Command(request, target_ea)) {
        return;
    }

    if (sendFeature1030Command(request, target_ea)) {
        return;
    }

    if (applyFeature1005Toggle(request, target_ea)) {
        return;
    }

    if (sendFeature1006Command(request, target_ea)) {
        return;
    }

    if (applyFeature1007Toggle(request, target_ea)) {
        return;
    }

    if (sendFeature1008Command(request, target_ea)) {
        return;
    }

    if (sendBoolGatedIndexedCommand(request, target_ea)) {
        return;
    }

    if (runDragonWorkerFeature(request, target_ea)) {
        return;
    }

    if (runFeature410SpeedWeight(env, request, target_ea)) {
        return;
    }

    if (runFeature1009Worker(request, target_ea)) {
        return;
    }

    if (runFeature1012Worker(request, target_ea)) {
        return;
    }

    if (runFeature620Worker(request, target_ea)) {
        return;
    }

    if (sendDirectIndexedCommand(request, target_ea)) {
        return;
    }

    if (sendIndexedCommandRange(request, target_ea)) {
        return;
    }

    (void)target_ea;
}

void dispatchChange(JNIEnv *env, const ChangeRequest &request) {
    if (isCardLoginInputCase(request.feature_num)) {
        g_card_login_input = request.string_value;
        logInfo("card login input updated");
        return;
    }

    if (isDeveloperKeyInputCase(request.feature_num)) {
        g_developer_key_input = request.string_value;
        logInfo("developer key input updated");
        return;
    }

    if (isDeveloperLoginButtonCase(request.feature_num)) {
        runDeveloperLogin(env, request);
        return;
    }

    if (isCardUnbindButtonCase(request.feature_num)) {
        runUnbind(env, request);
        return;
    }

    if (isCardLoginButtonCase(request.feature_num)) {
        runLogin(env, request);
        return;
    }

    ResolvedNativeCase resolved;
    if (!resolveNativeCase(request, &resolved)) {
        return;
    }

    executeMemoryCase(env, request, resolved.target_ea);
}

} // namespace

void setAndroidId(const std::string &android_id) {
    g_android_id = android_id;
}

bool isAuthenticated() {
    return g_authenticated.load();
}

bool isDeveloperUnlocked() {
    return g_developer_unlocked.load();
}

extern "C" void JNICALL native_Preferences_Changes(
    JNIEnv *env,
    jclass,
    jobject context,
    jint feature_num,
    jstring feature_name,
    jint int_value,
    jboolean bool_value,
    jstring string_value) {
    ChangeRequest request;
    request.context = context;
    request.feature_num = feature_num;
    request.feature_name = toString(env, feature_name);
    request.int_value = int_value;
    request.bool_value = bool_value == JNI_TRUE;
    request.string_value = toString(env, string_value);
    originalLogPrint(
        ANDROID_LOG_DEBUG,
        "Mod_Menu",
        "Feature name: %d - %s | Value: = %d | Bool: = %d | Text: = %s",
        request.feature_num,
        request.feature_name.c_str(),
        request.int_value,
        request.bool_value ? 1 : 0,
        request.string_value.c_str());
    dispatchChange(env, request);
}

}
