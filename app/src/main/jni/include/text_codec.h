#pragma once

#include <string>

namespace android_mod {

std::string trimAscii(std::string value);

std::string base64Encode(const std::string &binary);
bool base64Decode(const std::string &text, std::string *out);

std::string jsonEscape(const std::string &value);
std::string jsonExtractString(const std::string &json, const char *key);
std::string jsonExtractRawValue(const std::string &json, const char *key);
bool jsonExtractInt64(const std::string &json, const char *key, long long *out);
bool jsonReplaceOrInsertInt(std::string *json, const char *key, int value);

}
