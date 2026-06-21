#include "include/game_command.h"

#include "include/game_memory_patch_table.h"
#include "include/jni_helpers.h"
#include "include/memory_tool.h"

#include <cstdint>
#include <cstdio>
#include <cstring>
#include <mutex>
#include <string>

namespace android_mod {
namespace {

constexpr const char *kBootloaderModule = "libBootloader.so";
constexpr std::uintptr_t kCommandHookSignatureOffset = 0x019AB9BC;
constexpr std::uintptr_t kCommandHookPatchOffset = 0x02F9CA9C;
constexpr std::uintptr_t kCommandHookCodeOffset = kCommandHookPatchOffset + 0x15C;
constexpr std::uintptr_t kCommandReadyOffset = 0x49F0000;
constexpr std::uintptr_t kCommandPayloadOffset = 0x49F0008;
constexpr std::uintptr_t kPointerMask = 0x00FFFFFFFFFFFFFFULL;
constexpr std::uintptr_t kAvatarRootOffset = 0x0473D660;
constexpr std::uintptr_t kAvatarPositionSecondOffset = 0x7F18;
constexpr std::uintptr_t kAvatarPositionThirdOffset = 0x1C68;
constexpr std::uint32_t kCommandHookOriginalSignature = 0xA9BA7BFDu;
constexpr std::uint32_t kCommandHookInstalledBranch = 0x1457C48Fu;
constexpr std::uint32_t kCommandHookPatchSignature = 0xA9BD7BFDu;
constexpr double kPortalYOffset = 1.2;
constexpr double kPortalZOffset = 3.0;

std::mutex g_fade_texture_lock;
std::string g_change_level_fade_texture = "White";

bool writeByte(std::uintptr_t address, unsigned char value) {
    char text[4];
    std::snprintf(text, sizeof(text), "%u", static_cast<unsigned int>(value));
    return writeValue(text, address, TYPE_BYTE);
}

bool writeDword(std::uintptr_t address, std::uint32_t value) {
    return writeValue(std::to_string(value), address, TYPE_DWORD);
}

bool readDword(std::uintptr_t address, std::uint32_t *out) {
    return out && readTyped(address, TYPE_DWORD, out, sizeof(*out));
}

bool writeRawDword(std::uintptr_t address, std::uint32_t value) {
    return writeBytes(address, &value, sizeof(value));
}

bool writeRawQword(std::uintptr_t address, std::uint64_t value) {
    return writeBytes(address, &value, sizeof(value));
}

bool encodeArm64Branch(std::uintptr_t source, std::uintptr_t target, bool link, std::uint32_t *out) {
    if (!out) {
        return false;
    }
    const std::int64_t delta = static_cast<std::int64_t>(target) - static_cast<std::int64_t>(source);
    if ((delta & 3) != 0) {
        return false;
    }
    const std::int64_t imm = delta >> 2;
    if (imm < -(1LL << 25) || imm >= (1LL << 25)) {
        return false;
    }
    *out = (link ? 0x94000000u : 0x14000000u) | (static_cast<std::uint32_t>(imm) & 0x03FFFFFFu);
    return true;
}

std::uintptr_t bootloaderBase() {
    std::uintptr_t base = getModuleAddress(kBootloaderModule);
    if (base == 0) {
        logWarn("game command target module not loaded: %s", kBootloaderModule);
    }
    return base;
}

bool ensureCommandBridgeInstalled(std::uintptr_t base) {
    const std::uintptr_t signature = base + kCommandHookSignatureOffset;
    const std::uintptr_t patch = base + kCommandHookPatchOffset;
    const std::uintptr_t code = base + kCommandHookCodeOffset;
    const std::uintptr_t ready = base + kCommandReadyOffset;
    const std::uintptr_t payload = base + kCommandPayloadOffset;
    const std::uintptr_t avatar_root = base + kAvatarRootOffset;

    std::uint32_t signature_value = 0;
    if (!readDword(signature, &signature_value)) {
        logWarn("game command bridge signature read failed");
        return false;
    }
    if (signature_value == kCommandHookInstalledBranch) {
        return true;
    }
    if (signature_value != kCommandHookOriginalSignature) {
        logWarn("game command bridge unexpected signature=0x%08X", signature_value);
        return false;
    }

    std::uint32_t patch_signature = 0;
    if (!readDword(patch, &patch_signature)) {
        logWarn("game command bridge patch signature read failed");
        return false;
    }
    if (patch_signature != kCommandHookPatchSignature) {
        logWarn("game command bridge unexpected patch signature=0x%08X", patch_signature);
        return false;
    }

    std::uint32_t branch_to_handler = 0;
    std::uint32_t branch_back = 0;
    if (!encodeArm64Branch(code + 0x28, patch, true, &branch_to_handler) ||
        !encodeArm64Branch(code + 0x38, signature + sizeof(std::uint32_t), false, &branch_back)) {
        logWarn("game command bridge branch encode failed");
        return false;
    }

    const std::uint32_t bridge_words[] = {
        0x580001F0u,
        0xB9400211u,
        0x34000171u,
        0xA9BF7BFDu,
        0xA9BF07E0u,
        0xB900021Fu,
        0x58000160u,
        0xF9400000u,
        0xF97E7C00u,
        0x58000141u,
        branch_to_handler,
        0xA8C107E0u,
        0xA8C17BFDu,
        kCommandHookOriginalSignature,
        branch_back,
    };

    for (std::size_t i = 0; i < sizeof(bridge_words) / sizeof(bridge_words[0]); ++i) {
        if (!writeRawDword(code + i * sizeof(std::uint32_t), bridge_words[i])) {
            logWarn("game command bridge code write failed index=%zu", i);
            return false;
        }
    }

    if (!writeRawQword(patch + 0x198, ready) ||
        !writeRawQword(patch + 0x1A0, avatar_root) ||
        !writeRawQword(patch + 0x1A8, payload) ||
        !writeDword(ready, 0) ||
        !writeRawDword(signature, kCommandHookInstalledBranch)) {
        logWarn("game command bridge slot write failed");
        return false;
    }

    logInfo("game command bridge installed base=0x%llX", static_cast<unsigned long long>(base));
    return true;
}

struct OutfitPropEntry {
    const char *suffix;
    bool npc;
};

constexpr const char *kKidPropPrefix = "CharSkyKid_Prop_";
constexpr const char *kNpcPropPrefix = "CharSkyNPC_Prop_";
constexpr const char *kKidFeetPrefix = "CharSkyKid_Feet_";
constexpr const char *kNpcFeetPrefix = "NPC_";

struct FeetEntry {
    const char *suffix;
    bool npc;
};

struct PortalPosition {
    double x = 0.0;
    double y = kPortalYOffset;
    double z = kPortalZOffset;
    std::uintptr_t avatar_position = 0;
};

struct IndexedGameCommand {
    int index;
    const char *command;
};

struct AudienceModelEntry {
    int index;
    const char *vis_type;
};

struct RelationshipSpellEntry {
    int index;
    const char *buff_name;
};

constexpr IndexedGameCommand kIndexedGameCommands[] = {
    {0, "UILogo"},
    {1, "Debug_StartSnakeGame(game)"},
    {2, "Debug_AddSnakeJoint(game)"},
    {3, "Debug_StopSnakeGame(game)"},
    {4, "DEBUG_TestExplosionFx(game)"},
    {5, "BrowseEmitters(game)"},
    {6, "NextPage(game)"},
    {7, "Disconnect(game)"},
    {8, "WarpToNextActiveNpc(game)"},
    {9, "SpawnWaxChunk(game)"},
    {10, "CreateVineCrawler(game)"},
    {11, "Randomize(game)"},
    {12, "WearMaxOutfit(game)"},
    {13, "ClearOutfit(game)"},
    {14, "SetAncestorMode(game)"},
    {15, "CheckpointResumeLatest(game)"},
    {16, "local e=game:eventBarn():AddEventByMetaName('DisplayTitleCard') if e then e:mainTitle('"
         "\xF0\x9D\x99\x8E\xF0\x9D\x99\x86\xF0\x9D\x99\x94\xE2\x80\xA2"
         "\xF0\x9D\x99\x88\xF0\x9D\x99\x8A\xF0\x9D\x98\xBF"
         "') e:subTitle('Process hollowing successful. Sky client hijacked') e:shadow(false) e:forceFirstTime(8000) e:yOffset(0.5) e:Start(0) end"},
    {17, "NestedEmitterStressTest(game)"},
    {18, "NestedEmitter_Launch(game)"},
    {19, "MeshEmitter_Launch(game)"},
    {20, "local e=game:eventBarn():AddEventByMetaName('SocialFireworksMenu') if e then e:Start(0) end"},
    {21, "Purchase(game)"},
    {22, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(0) e:Start(0) end"},
    {23, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(1) e:Start(0) end"},
    {24, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(2) e:Start(0) end"},
    {25, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(3) e:Start(0) end"},
    {26, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(4) e:Start(0) end"},
    {27, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(5) e:Start(0) end"},
    {28, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(6) e:Start(0) end"},
    {29, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(7) e:Start(0) end"},
    {30, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(8) e:Start(0) end"},
    {31, "local e=game:eventBarn():AddEventByMetaName('PlayPurchaseFX') if e then e:invisibleItem(false) e:stuffType(9) e:Start(0) end"},
    {32, "local e=game:eventBarn():AddEventByMetaName('AvatarShrinkGrow') if e then e:targetHeight(-6) e:targetScale(-6) e:speed(1) e:Start(0) end"},
    {33, "local e=game:eventBarn():AddEventByMetaName('AvatarShrinkGrow') if e then e:targetHeight(0) e:targetScale(0) e:speed(1) e:Start(0) end"},
    {34, "local e=game:eventBarn():AddEventByMetaName('AvatarShrinkGrow') if e then e:targetHeight(6) e:targetScale(6) e:speed(1) e:Start(0) end"},
    {35, "local e=game:eventBarn():AddEventByMetaName('SetAvatarMicroMode') if e then e:Start(0) end"},
    {36, "local e=game:eventBarn():AddEventByMetaName('SetAvatarCapeTattered') if e then e:amount(1) e:Start(0) end"},
    {37, "local e=game:eventBarn():AddEventByMetaName('AvatarSetOutfit') if e then e:face('CharSkyKid_Face_None') e:Start(0) end"},
    {38, "local e=game:eventBarn():AddEventByMetaName('AvatarSetOutfit') if e then e:face('CharSkyKid_Mask_ElderSunsetB_02') e:Start(0) end"},
    {39, "local e=game:eventBarn():AddEventByMetaName('SocialShowPresence') if e then e:Start(0) end"},
    {40, "local e=game:eventBarn():AddEventByMetaName('SocialGrapplingHookMenu') if e then e:Start(0) end"},
    {41, "local e=game:eventBarn():AddEventByMetaName('SocialGrapplingHookLaunch') if e then e:Start(0) end"},
    {42, "local e=game:eventBarn():AddEventByMetaName('OpenDanceMenu') if e then e:muteAnims(false) e:controlsCameraPos(true) e:mode(0) e:autoStart(false) e:onMenuClose(game:clumpBarn():CreateClumpWithSize(0)) e:Start(0) end"},
    {43, "local e=game:eventBarn():AddEventByMetaName('UiSocialStickerMenu') if e then e:autoStart(false) e:Start(0) end"},
    {44, "local e=game:eventBarn():AddEventByMetaName('AvatarSetOutfit') if e then e:prop('CharSkyKid_Prop_WaterBucket') e:playAnim(true) e:Start(0) end"},
    {45, "local e=game:eventBarn():AddEventByMetaName('AvatarScoopWater') if e then e:Start(0) end"},
    {46, "local e=game:eventBarn():AddEventByMetaName('AvatarPourWater') if e then e:Start(0) end"},
    {47, "local e=game:eventBarn():AddEventByMetaName('AvatarSetOutfit') if e then e:prop('CharSkyKid_Prop_None') e:Start(0) end"},
    {48, "local e = game:eventBarn():AddEventByMetaName('AvatarRainbowParty') if e then e:tintLocalPlayer(true) e:tintRemotePlayers(false) e:hueShiftRate(3) e:innerGlow(0.1) e:Start(0) end"},
    {49, "local e=game:eventBarn():AddEventByMetaName('AvatarSetInnerGlow') if e then e:stayActive(true) e:autoStart(false) e:fadeInDuration(0) e:snap(0.5) e:amount(99999) e:npc(nil) e:amountFloat(nil) e:Start(0) end"},
    {50, "ReSetLandscapeTest(game)"},
    {51, "ForcePortraitTest(game)"},
    {52, "local e = game:eventBarn():AddEventByMetaName('AudienceSettings') if e then e:replaceLocalAvatar(false) e:Start(0) end"},
    {53, "local e=game:eventBarn():AddEventByMetaName('CloseMenu') if e then e:closeAll(true) e:autoStart(false) e:menuType(0) e:Start(0) end"},
};

constexpr AudienceModelEntry kAudienceModels[] = {
    {0, "UILogo"},
    {1, "Broomstick"},
    {2, "SkyKidFlying"},
    {3, "SkyKidAbstract"},
    {4, "Jelly"},
    {5, "Manta"},
    {6, "Bird"},
    {7, "Butterfly"},
    {8, "Crab"},
    {9, "FishH"},
    {10, "DragonDanceBody"},
    {11, "DragonDanceHead"},
    {12, "DragonDanceTail"},
    {13, "SkyKidAP18MaskedBear"},
    {14, "SkyKidAP18MaskedSerow"},
    {15, "SkyKidAP18MaskedBoar"},
    {16, "SkyKidAP18MaskedRaccoon"},
    {17, "SkyKidAP18MaskedMonkey"},
};

constexpr RelationshipSpellEntry kRelationshipSpells[] = {
    {0, "UILogo"},
    {1, "relationship_allow_carry"},
    {2, "relationship_allow_warp"},
    {3, "relationship_allow_bearhug"},
    {4, "relationship_allow_playfight"},
    {5, "relationship_allow_side_hug"},
    {6, "relationship_allow_piggyback"},
    {7, "relationship_allow_cradle_carry"},
    {8, "relationship_allow_duet_bow_special"},
    {9, "relationship_allow_bearhug2"},
    {10, "relationship_flair_soulmate"},
    {11, "relationship_flair_you_and_i"},
};

bool readU64Raw(std::uintptr_t address, std::uint64_t *out) {
    return out && readTyped(address, TYPE_QWORD, out, sizeof(*out));
}

bool readFloatRaw(std::uintptr_t address, float *out) {
    return out && readTyped(address, TYPE_FLOAT, out, sizeof(*out));
}

bool readLiveAvatarPosition(PortalPosition *out) {
    if (!out) {
        return false;
    }

    const std::uintptr_t base = bootloaderBase();
    if (base == 0) {
        return false;
    }

    std::uint64_t first = 0;
    std::uint64_t second = 0;
    std::uint64_t third = 0;
    if (!readU64Raw(base + kAvatarRootOffset, &first)) {
        logWarn("portal position read failed at qword_364068[0] base+0x%llX", static_cast<unsigned long long>(kAvatarRootOffset));
        return false;
    }
    if (!readU64Raw(static_cast<std::uintptr_t>(first & kPointerMask) + kAvatarPositionSecondOffset, &second)) {
        logWarn("portal position read failed at qword_364068[1] +0x%llX", static_cast<unsigned long long>(kAvatarPositionSecondOffset));
        return false;
    }
    if (!readU64Raw(static_cast<std::uintptr_t>(second & kPointerMask) + kAvatarPositionThirdOffset, &third)) {
        logWarn("portal position read failed at qword_364068[2] +0x%llX", static_cast<unsigned long long>(kAvatarPositionThirdOffset));
        return false;
    }

    const std::uintptr_t position = static_cast<std::uintptr_t>(third & kPointerMask);
    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;
    if (!readFloatRaw(position, &x) ||
        !readFloatRaw(position + sizeof(float), &y) ||
        !readFloatRaw(position + sizeof(float) * 2, &z)) {
        logWarn("portal position float read failed at avatar=0x%llX", static_cast<unsigned long long>(position));
        return false;
    }

    out->x = static_cast<double>(x);
    out->y = static_cast<double>(y) + kPortalYOffset;
    out->z = static_cast<double>(z) + kPortalZOffset;
    out->avatar_position = position;
    return true;
}

bool currentPortalPosition(PortalPosition *pos) {
    if (!readLiveAvatarPosition(pos)) {
        logWarn("portal command aborted: qword_364068 live avatar position unavailable helper=sub_180D6C");
        return false;
    }

    logInfo(
        "portal position source=qword_364068 helper=sub_180D6C avatar=0x%llX pos=(%.6f,%.6f,%.6f) delta=(0,%.1f,%.1f)",
        static_cast<unsigned long long>(pos->avatar_position),
        pos->x,
        pos->y,
        pos->z,
        kPortalYOffset,
        kPortalZOffset);
    return true;
}

constexpr FeetEntry kFeetProps[] = {
    {"None", false},
    {"ClassicSocks", false},
    {"AP09UniformBoots", false},
    {"AP10Overalls", false},
    {"AP17RaggedTunic", false},
    {"AP19CargoShorts", false},
    {"AP20ShortStraps", false},
    {"AP20LongStraps", false},
    {"AP20RobeSandals", false},
    {"AP23WrapSandals", false},
    {"AP24BellShoes", false},
    {"AP25BootShort", false},
    {"AP25BootTall", false},
    {"AP26LumberjackBoots", false},
    {"AP27GuardBoots", false},
    {"AP28FluffyBoots", false},
    {"RainbowDarkLoafers", false},
    {"SunlightSandals", false},
    {"Witch", false},
    {"GothBoots", false},
    {"BalletFlats", false},
    {"BunnySlippers", false},
    {"YetiBoots", false},
    {"FeastLegWarmers", false},
    {"AuroraSneakers", false},
    {"TreasureBoots", false},
    {"SeaFoam", false},
    {"AnnivButterfly", false},
    {"FluffyMoth", false},
    {"TriumphIceSkates", false},
    {"CivilianShoes", true},
    {"LaborerShoes", true},
};

static_assert(sizeof(kFeetProps) / sizeof(kFeetProps[0]) == 32,
              "qword_37D5F0 feet prop table size mismatch");

constexpr OutfitPropEntry kOutfitProps[] = {
    {"None", false},
    {"Harp", false},
    {"Harp_02", false},
    {"Bass", false},
    {"Piano", false},
    {"Voice", false},
    {"AP02Bell_01", false},
    {"AP02Bell_02", false},
    {"AP03Flute_01", false},
    {"AP03PanFlute_01", false},
    {"AP04Guitar", false},
    {"AP04Guitar_02", false},
    {"AP04Ukulele", false},
    {"AP05Xylophone", false},
    {"APPiano_01", false},
    {"APPiano_02", false},
    {"AP07HandPan", false},
    {"AP07HandPan_02", false},
    {"AP08Dundun", false},
    {"AP08Dundun_02", false},
    {"AP09Lute_01", false},
    {"AP10Bugle_01", false},
    {"AP10Bugle_02", false},
    {"AP12Kalimba_01", false},
    {"AP14Guitar_01", false},
    {"AP14Guitar_02", false},
    {"AP15DarkHorn_01", false},
    {"AP16Microphone", false},
    {"Microphone", false},
    {"AP18Ocarina", false},
    {"AP23PianoAnims", false},
    {"AP23CelloBasic", false},
    {"AP23CelloFancy", false},
    {"AP24Harmonica", false},
    {"AP25Cymbals", false},
    {"Ocarina_01", false},
    {"Drum", false},
    {"Violin", false},
    {"Saxophone", false},
    {"FortuneDrum", false},
    {"Firework", false},
    {"Umbrella", false},
    {"ScepterWand", false},
    {"Wrench", false},
    {"AP03Umbrella_01", false},
    {"AP03Umbrella_02", false},
    {"AP19UltimateCamera", false},
    {"AP19Camera", false},
    {"AP24Umbrella", false},
    {"AP27ShepherdStaff", false},
    {"AP27ManateeFigurine", false},
    {"AP27Spear", false},
    {"AP27Shield", false},
    {"AP29Piccolo", false},
    {"FortuneUmbrella", false},
    {"FortuneFan", false},
    {"SnakeGameHead", false},
    {"HeartFirework", false},
    {"LilyUmbrella", false},
    {"BloomSunflowerUmbrella", false},
    {"JenovaFan", false},
    {"AnniversaryClapboard", false},
    {"CompetitionTorch", false},
    {"TriumphIceBoard", false},
    {"Lantern", false},
    {"SummerUmbrella_01", false},
    {"MantaFloat", false},
    {"MischiefBroom", false},
    {"TreasureShovel", false},
    {"SnowBallMittens", false},
    {"FortunePony", false},
    {"AP03Umbrella_03", false},
    {"ShepherdStaff", false},
    {"WaterBucket", false},
    {"Table", false},
    {"JarRound", false},
    {"LanternPost", false},
    {"Brick", false},
    {"UnitCube", false},
    {"UnitCylinder", false},
    {"UnitSphere", false},
    {"Stool", false},
    {"JarTall", false},
    {"APFlagAnim", false},
    {"NightBook", false},
    {"SpellFire", false},
    {"Sparkler", false},
    {"MuralWater", false},
    {"MuralEarth", false},
    {"MuralAir", false},
    {"MuralFire", false},
    {"MuralVoid", false},
    {"MuralMind", false},
    {"MuralDark", false},
    {"MuralLight", false},
    {"PipeBroken_01", false},
    {"AP04Bonfire", false},
    {"AP05Brazier_01", false},
    {"AP08Brazier_01", false},
    {"AP10Brazier_01", false},
    {"AP10Bookshelf_01", false},
    {"AP10Hammock_01", false},
    {"AP10Jar_01", false},
    {"AP10Pillow_01", false},
    {"AP10Torch_01", false},
    {"AP10Tent_01", false},
    {"AP10Spotlight_01", false},
    {"AP10Hoop_01", false},
    {"AP11Star_01", false},
    {"AP11Rose_01", false},
    {"AP11Fox_01", false},
    {"AP11Fox_02", false},
    {"AP14VaseBouquet_01", false},
    {"AP17Chimes", false},
    {"AP17Plants", false},
    {"AP17Kettle", false},
    {"AP17UltimateMemoryBook", false},
    {"AP17Crab", false},
    {"AP17Manta", false},
    {"AP18Ball", false},
    {"AP19CrystalJar", false},
    {"AP21Basket", false},
    {"AP24Tent", false},
    {"AP24ClassicPlush", false},
    {"AP24ClassicPlushAlt", false},
    {"AP25ClothDivider", false},
    {"AP26TreePerch", false},
    {"AP27ButterflyToy", false},
    {"AP27ManateePlush", false},
    {"AP27CrabFigurine", false},
    {"AP27PlantVine", false},
    {"AP27TeaStall", false},
    {"AP27Poster", false},
    {"AP27Projector", false},
    {"AP28MigrationBell", false},
    {"SunlightProjector", false},
    {"SunlightSandcastleA", false},
    {"SunlightSandcastleB", false},
    {"SunlightSandcastleC", false},
    {"SunlightSandcastleD", false},
    {"SunlightSandcastleE", false},
    {"SunlightSandcastleF", false},
    {"SunlightSandcastleG", false},
    {"SnowmanArm_01", false},
    {"SnowmanArm_02", false},
    {"SnowmanEye", false},
    {"SnowmanMouth", false},
    {"SnowmanButton", false},
    {"SnowmanNose", false},
    {"SnowmanHead", false},
    {"SnowmanBodySmall", false},
    {"SnowmanBodyMedium", false},
    {"SnowmanBodyLarge", false},
    {"SnowmanElk", false},
    {"SnowmanHolly", false},
    {"SnowmanSantaHat", false},
    {"SnowmanYeti", false},
    {"SnowmanScarf", false},
    {"SnowmanBeard", false},
    {"LotusBathtub", false},
    {"MoonlightPoster", false},
    {"AnniversaryTrophyG_00", false},
    {"AnniversaryTrophyG_01", false},
    {"AnniversaryTrophyG_02", false},
    {"AnniversaryTrophyG_03", false},
    {"AnniversaryTrophyG_04", false},
    {"AnniversaryTrophyG_05", false},
    {"AnniversaryTrophyG_06", false},
    {"AnniversaryTrophyG_07", false},
    {"AnniversaryTrophyG_08", false},
    {"AnniversaryTrophyG_09", false},
    {"AnniversaryTrophyS_00", false},
    {"AnniversaryTrophyS_01", false},
    {"AnniversaryTrophyS_02", false},
    {"AnniversaryTrophyS_03", false},
    {"AnniversaryTrophyS_04", false},
    {"AnniversaryTrophyS_05", false},
    {"AnniversaryTrophyS_06", false},
    {"AnniversaryTrophyS_07", false},
    {"AnniversaryTrophyS_08", false},
    {"AnniversaryTrophyS_09", false},
    {"AnniversaryTrophyS_10", false},
    {"AnniversaryTrophyG_Generic", false},
    {"AnniversaryTrophyS_Generic", false},
    {"AnniversaryPopcorn", false},
    {"AnniversaryMovieSeats", false},
    {"AnniversaryCarpet", false},
    {"SkyBalloon", false},
    {"BalloonArch", false},
    {"KrillCarpet", false},
    {"MischiefPumpkinLamp", false},
    {"MischiefWeb", false},
    {"MischiefTree", false},
    {"TwinRibbonPoster", false},
    {"TriumphHangingFlags", false},
    {"TriumphPodium", false},
    {"TriumphFlagSunset", false},
    {"TriumphFlagDusk", false},
    {"TriumphFlagRain", false},
    {"TriumphFlagPrairie", false},
    {"FortuneCarousel", false},
    {"SkyArtBook01_Closed", false},
    {"SkyStoryBook01_Closed", false},
    {"Swing", false},
    {"Seesaw", false},
    {"LoveBoat", false},
    {"FlowerArch", false},
    {"TeaTable", false},
    {"Tent_01", false},
    {"WisteriaTeaTable", false},
    {"PicnicBlanket", false},
    {"Pinwheel_01", false},
    {"BirthdayFlags_01", false},
    {"Balloon_01", false},
    {"LightFence_01", false},
    {"Cannon_01", false},
    {"Cannon_Pin", false},
    {"BirthdayCake_Giant", false},
    {"JarBubbleMachine", false},
    {"IslandUmbrella_01", false},
    {"BeachBall", false},
    {"BeachRecliner", false},
    {"BeachChairWood", false},
    {"BeachChairCloth", false},
    {"MischiefTable", false},
    {"MischiefPumpkin", false},
    {"MischiefCat", false},
    {"MischiefCat_02", false},
    {"TableXmas", false},
    {"PillowXmas", false},
    {"SequencerStation", false},
    {"SequencerAnni", false},
    {"SnowGlobe", false},
    {"MarshmallowSet", false},
    {"MarshmallowStick", false},
    {"StupaBell", false},
    {"SkyBallGoals", false},
    {"Snowman", false},
    {"ButterflyFountain", false},
    {"Bucket", false},
    {"HangingLights", false},
    {"MusicShell", false},
    {"MusicShellOffice", false},
    {"ShellMusicBox", false},
    {"DiscoLight", false},
    {"BirthdayOreo", false},
    {"OreoPlush", false},
    {"HeartPlush", false},
    {"JuiceCup", false},
    {"PickupTest", false},
    {"Surfboard", false},
    {"Snowboard", false},
    {"BigMantaPlush", false},
    {"CompanionCube", false},
    {"PlaceableRace", false},
    {"PuzzleKey", false},
    {"PuzzlePressurePlate", false},
    {"PlaceableMessage", false},
    {"PuzzleChest", false},
    {"PuzzleBox", false},
    {"PuzzleCage", false},
    {"BloomSapling", false},
    {"LemonadeStand", false},
    {"LargeBonfire", false},
    {"ScreenBlue", false},
    {"ScreenYellow", false},
    {"ScreenGreen", false},
    {"ScreenRed", false},
    {"CurtainDoorYellow", false},
    {"WindowYellow", false},
    {"CeilingYellow", false},
    {"StoneStool", false},
    {"StoneBenchSingle", false},
    {"StoneChair", false},
    {"StoneArmchair", false},
    {"StoneLoveseat", false},
    {"StoneBench", false},
    {"StoneSofaCorner", false},
    {"StoneSofaSide", false},
    {"StoneTableSmall", false},
    {"StoneDiningTableSquare", false},
    {"StoneDiningTableRound", false},
    {"StoneDiningTableLong", false},
    {"StoneDesk", false},
    {"StoneConsoleTable", false},
    {"StoneCoffeeTable", false},
    {"StoneCoffeeTableNew", false},
    {"StoneBedSingle", false},
    {"StoneDresserTall", false},
    {"StoneDresserLong", false},
    {"StoneBathtubSmall", false},
    {"StoneBathtubLarge", false},
    {"StoneSink", false},
    {"StoneWallMirror", false},
    {"StoneWallTowelRack", false},
    {"StoneKitchenStove", false},
    {"StoneKitchenOven", false},
    {"StoneKitchenDrawers", false},
    {"StoneKitchenCabinet", false},
    {"StoneWallPotRack", false},
    {"StoneWallMugRack", false},
    {"StoneBoxEmpty", false},
    {"StoneBoxClosed", false},
    {"StoneCubeSmall", false},
    {"StoneCubeWide", false},
    {"StoneCubeTall", false},
    {"StoneWallShelf", false},
    {"DecorPillowA", false},
    {"DecorPillowB", false},
    {"DecorFoldedCloth", false},
    {"StonePlantStand", false},
    {"StoneCandleHolder", false},
    {"StoneCandleLight", false},
    {"StoneWallSconce", false},
    {"RugSmallSolid", false},
    {"RugSmallStripes", false},
    {"RugSmallClassic", false},
    {"RugSmallHalfCircle", false},
    {"RugMediumSolid", false},
    {"RugMediumStripes", false},
    {"RugMediumDiamonds", false},
    {"RugMediumArgyle", false},
    {"RugMediumCircle", false},
    {"RugLargeSolid", false},
    {"RugLargeCircle", false},
    {"IceBathtubLarge", false},
    {"IceBedSingle", false},
    {"IceConsoleTable", false},
    {"IceDresserTall", false},
    {"IceKitchenDrawers", false},
    {"IceKitchenOven", false},
    {"IceKitchenStove", false},
    {"IceSofaSide", false},
    {"IceStool", false},
    {"IceTableSmall", false},
    {"IceBlockHorizontal", false},
    {"IceBlockVertical", false},
    {"IceSlideConcave", false},
    {"IceSlideConvex", false},
    {"StoneMannequin", false},
    {"HangingMask", false},
    {"AP22BedSingle", false},
    {"AP22Chair", false},
    {"AP22DiningTable", false},
    {"AP22Loveseat", false},
    {"AP22PendantLight", false},
    {"AP22BathTub", false},
    {"AP22StepStool", false},
    {"AP22HangingLight", false},
    {"AP22WallPaintingSet", false},
    {"AP22WallPainting", false},
    {"AP22WallSpiceRack", false},
    {"AP22HangingPlanter", false},
    {"AP22Lamp", false},
    {"AP23WallPosterDuet", false},
    {"AP23WallPosterPiano", false},
    {"AP23WallPosterCello", false},
    {"AP23WallPosterConch", false},
    {"AP23WallPosterPianoKeys", false},
    {"AP23Vanity", false},
    {"AP23ClothDivider", false},
    {"AP23Curtain", false},
    {"AP23HalfRug", false},
    {"AP23Vase", false},
    {"AP23DecorCup", false},
    {"AP23Cup", false},
    {"AP23Bench", false},
    {"AP23PlaceablePianoBasic", false},
    {"AP23PlaceablePianoFancy", false},
    {"AP23PlaceablePianoUpright", false},
    {"RugSmallSolid_AP23Pianist", false},
    {"RugSmallSolid_AP23Cellist", false},
    {"AP24Portrait", false},
    {"AP24WallPhotos_01", false},
    {"AP24WallPhotos_02", false},
    {"AP24Chandelier", false},
    {"AP24Clock", false},
    {"MannequinFigurine", false},
    {"InstrumentRack", false},
    {"StarJar", false},
    {"CrabOfCompetition_Stone", false},
    {"CrabOfCompetition_Gold", false},
    {"MoonlightLantern", false},
    {"PastryPlush", false},
    {"PastryTeaTable", false},
    {"MischiefCauldron", false},
    {"TeaBath", false},
    {"CrabbitPortal", false},
    {"PianoUpright", false},
    {"FortunePlant", false},
    {"FortuneCoupletPoster", false},
    {"FortuneCandleFlags", false},
    {"MeteorHeart", false},
    {"RoseJar", false},
    {"BubbleBeanBag", false},
    {"TwinRibbonOrnament", false},
    {"RibbonHourglass", false},
    {"KizunaAiTVPillow", false},
    {"SunflowerBarTable", false},
    {"SunflowerLadder", false},
    {"SunflowerPillow", false},
    {"SunflowerRugHalfCircle", false},
    {"SunflowerWallShelf", false},
    {"TreasurePirateBed", false},
    {"Book", true},
    {"Booze", true},
    {"ButterflyNet", true},
    {"Clipboard", true},
    {"FireStick", true},
    {"Flag", true},
    {"Horn", true},
    {"Jar", true},
    {"Oar", true},
    {"PickAxe", true},
    {"PowerGlove", true},
    {"Shield", true},
    {"Spear", true},
    {"Sword", true},
    {"Shield_01", false},
    {"AP30BirdPlush", false},
    {"AP30ChallengeBouncePad", false},
    {"AP30ChallengeBouncePad_2", false},
    {"AP30ChallengeBouncePad_3", false},
    {"AP30ChallengeLantern", false},
    {"AP30ChallengeToken", false},
    {"AP30ChallengeBallDispenser", false},
    {"AP30ChallengeTarget", false},
    {"AP30ChallengePlatform", false},
    {"ChallengeTreasure", false},
};

static_assert(sizeof(kOutfitProps) / sizeof(kOutfitProps[0]) == 429,
              "qword_380F10 outfit prop table size mismatch");

} // namespace

const char *indexedGameCommand(int command_index) {
    for (const IndexedGameCommand &entry : kIndexedGameCommands) {
        if (entry.index == command_index) {
            return entry.command;
        }
    }
    return nullptr;
}

bool sendGameCommand(const std::string &command) {
    if (command.empty()) {
        return false;
    }

    std::uintptr_t base = bootloaderBase();
    if (base == 0) {
        return false;
    }
    if (!ensureCommandBridgeInstalled(base)) {
        return false;
    }

    const std::uintptr_t ready = base + kCommandReadyOffset;
    const std::uintptr_t payload = base + kCommandPayloadOffset;

    if (!writeDword(ready, 0)) {
        logWarn("game command failed clearing ready flag at 0x%llX", static_cast<unsigned long long>(ready));
        return false;
    }

    for (std::size_t i = 0; i < command.size(); ++i) {
        if (!writeByte(payload + i, static_cast<unsigned char>(command[i]))) {
            logWarn("game command failed writing payload byte %zu", i);
            return false;
        }
    }
    if (!writeByte(payload + command.size(), 0)) {
        logWarn("game command failed writing payload terminator");
        return false;
    }

    if (!writeDword(ready, 1)) {
        logWarn("game command failed setting ready flag");
        return false;
    }

    std::uint32_t read_back = 0;
    if (!readTyped(ready, TYPE_DWORD, &read_back, sizeof(read_back))) {
        logWarn("game command failed reading ready flag");
        return false;
    }

    bool ok = read_back == 0;
    logInfo("game command sent ok=%d len=%zu", ok ? 1 : 0, command.size());
    return ok;
}

bool sendIndexedGameCommand(int command_index) {
    const char *command = indexedGameCommand(command_index);
    if (command == nullptr || std::strcmp(command, "custom") == 0) {
        logWarn("indexed game command unavailable index=%d", command_index);
        return false;
    }

    logInfo("indexed game command index=%d", command_index);
    return sendGameCommand(command);
}

bool sendAudienceModelCommand(int model_index) {
    if (model_index < 0 || model_index >= static_cast<int>(sizeof(kAudienceModels) / sizeof(kAudienceModels[0]))) {
        logWarn("audience model unavailable index=%d", model_index);
        return false;
    }

    const char *vis_type = kAudienceModels[model_index].vis_type;
    std::string command =
        "local e = game:eventBarn():AddEventByMetaName(\"AudienceSettings\") "
        "if e then     e:replaceLocalAvatar(true)     e:replaceOtherAvatars(false)     "
        "e:visType(\"" + std::string(vis_type) + "\")     e:allowHandhold(true)     e:Start(0) end";
    logInfo("audience model command index=%d visType=%s", model_index, vis_type);
    return sendGameCommand(command);
}

bool sendRelationshipGrantSpellCommand(int relation_index) {
    if (relation_index <= 0 ||
        relation_index >= static_cast<int>(sizeof(kRelationshipSpells) / sizeof(kRelationshipSpells[0]))) {
        logWarn("relationship GrantSpell unavailable index=%d", relation_index);
        return false;
    }

    const char *buff_name = kRelationshipSpells[relation_index].buff_name;
    std::string command =
        "local e = game:eventBarn():AddEventByMetaName(\"GrantSpell\") "
        "if e then e:buffName(\"" + std::string(buff_name) +
        "\") e:duration(3600.000000) e:networked(true) e:checkSameType(false) "
        "e:autoStart(false) e:Start(0) end";
    logInfo("relationship GrantSpell command index=%d buffName=%s", relation_index, buff_name);
    return sendGameCommand(command);
}

bool sendPortalCommandByPatchIndex(int patch_index, const std::string &display_name) {
    std::uint32_t id = 0;
    const char *level_name = findGamePatchPayloadByIndex(patch_index, &id);
    if (level_name == nullptr || level_name[0] == '\0') {
        logWarn("portal command unknown patch index=%d", patch_index);
        return false;
    }

    PortalPosition pos;
    if (!currentPortalPosition(&pos)) {
        return false;
    }

    char command[2048];
    std::snprintf(
        command,
        sizeof(command),
        "local pos = {%f, %f, %f}\n"
        "local portal = game:portalBarn():CreatePortal(getmetatable(portalBarn))\n"
        "if portal then\n"
        "    local transform = {\n"
        "        {3, 0, 0, 0},\n"
        "        {0, 3, 0, 0},\n"
        "        {0, 0, 3, 0},\n"
        "        {pos[1], pos[2], pos[3], 1}\n"
        "    }\n"
        "    portal:SetTransform(transform)\n"
        "    portal:tryToResume(false)\n"
        "    portal:visualOnly(false)\n"
        "    portal:enabled(true)\n"
        "    portal:resourceBundle(\"\")\n"
        "    portal:customColor({1.0, 1.0, 1.0})\n"
        "    portal:portalType(10)\n"
        "    portal:customLevelName(\"%s\")\n"
        "    portal:customSoundName(\"\")\n"
        "    portal:customHintText(\"传送到%s\")\n"
        "    portal:texture(\"PortalWhite\")\n"
        "    portal:Initialize(game:eventBarn())\n"
        "end\n\n",
        pos.x,
        pos.y,
        pos.z,
        level_name,
        display_name.empty() ? level_name : display_name.c_str());

    logInfo(
        "portal command index=%d id=0x%08X level=%s display=%s",
        patch_index,
        id,
        level_name,
        display_name.empty() ? level_name : display_name.c_str());
    return sendGameCommand(command);
}

void setChangeLevelFadeTexture(const std::string &fade_texture) {
    std::lock_guard<std::mutex> guard(g_fade_texture_lock);
    g_change_level_fade_texture = fade_texture.empty() ? "White" : fade_texture;
    logInfo("ChangeLevelWithFade fadeTexture state=qword_385588 value=%s", g_change_level_fade_texture.c_str());
}

bool sendChangeLevelWithFadeCommandByPatchIndex(int patch_index) {
    std::uint32_t id = 0;
    const char *level_name = findGamePatchPayloadByIndex(patch_index, &id);
    if (level_name == nullptr || level_name[0] == '\0') {
        logWarn("ChangeLevelWithFade unknown patch index=%d", patch_index);
        return false;
    }

    std::string fade_texture;
    {
        std::lock_guard<std::mutex> guard(g_fade_texture_lock);
        fade_texture = g_change_level_fade_texture;
    }
    if (fade_texture.empty()) {
        fade_texture = "White";
    }

    char command[1024];
    std::snprintf(
        command,
        sizeof(command),
        "local e = game:eventBarn():AddEventByMetaName(\"ChangeLevelWithFade\") "
        "if e then     e:levelName(\"%s\")     e:isLevelComplete(false)     "
        "e:fadeOutDuration(0.3)     e:fadeInDuration(0.5)     "
        "e:fadeHoldDuration(0.3)     e:fadeTexture(\"%s\")     e:Start(0) end",
        level_name,
        fade_texture.c_str());

    logInfo(
        "ChangeLevelWithFade command index=%d id=0x%08X level=%s fade=%s",
        patch_index,
        id,
        level_name,
        fade_texture.c_str());
    return sendGameCommand(command);
}

bool sendAvatarSetOutfitCommand(const std::string &outfit_prop) {
    if (outfit_prop.empty()) {
        return false;
    }

    std::string command =
        "local e=game:eventBarn():AddEventByMetaName('AvatarSetOutfit') "
        "if e then e:prop('" + outfit_prop + "') e:playAnim(true) e:Start(0) end";
    return sendGameCommand(command);
}

bool sendAvatarSetFeetCommand(const std::string &feet_prop) {
    if (feet_prop.empty()) {
        return false;
    }

    std::string command =
        "local e=game:eventBarn():AddEventByMetaName('AvatarSetOutfit') "
        "if e then e:feet('" + feet_prop + "') e:playAnim(true) e:Start(0) end";
    return sendGameCommand(command);
}

std::string outfitPropForItemId(int item_id) {
    if (item_id <= 0 || item_id > static_cast<int>(sizeof(kOutfitProps) / sizeof(kOutfitProps[0]))) {
        return {};
    }

    const OutfitPropEntry &entry = kOutfitProps[item_id - 1];
    std::string prop = entry.npc ? kNpcPropPrefix : kKidPropPrefix;
    prop += entry.suffix;
    return prop;
}

std::string feetPropForItemId(int item_id) {
    if (item_id <= 0 || item_id > static_cast<int>(sizeof(kFeetProps) / sizeof(kFeetProps[0]))) {
        return {};
    }

    const FeetEntry &entry = kFeetProps[item_id - 1];
    std::string prop = entry.npc ? kNpcFeetPrefix : kKidFeetPrefix;
    prop += entry.suffix;
    return prop;
}

}
