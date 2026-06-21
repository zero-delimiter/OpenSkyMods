#pragma once

#include <string>

namespace android_mod {

bool sendGameCommand(const std::string &command);
bool sendIndexedGameCommand(int command_index);
bool sendAudienceModelCommand(int model_index);
bool sendRelationshipGrantSpellCommand(int relation_index);
bool sendPortalCommandByPatchIndex(int patch_index, const std::string &display_name);
void setChangeLevelFadeTexture(const std::string &fade_texture);
bool sendChangeLevelWithFadeCommandByPatchIndex(int patch_index);
bool sendAvatarSetOutfitCommand(const std::string &outfit_prop);
bool sendAvatarSetFeetCommand(const std::string &feet_prop);
bool sendAvatarSetOutfitSlotCommand(const char *method, const std::string &value);
bool sendAvatarSetOutfitFeatureCommand(int feature_num, int item_id);
std::string outfitPropForItemId(int item_id);
std::string feetPropForItemId(int item_id);
std::string outfitSlotValueForFeatureItem(int feature_num, int item_id);
const char *outfitSlotMethodForFeature(int feature_num);
bool isAvatarSetOutfitSlotFeature(int feature_num);

}
