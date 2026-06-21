#include "include/text_codec.h"

#include <cctype>
#include <cstdlib>

namespace android_mod {
namespace {

const char kBase64Alphabet[] =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

const char *findJsonValue(const std::string &json, const char *key) {
    std::string marker = std::string("\"") + key + "\"";
    std::size_t key_pos = json.find(marker);
    if (key_pos == std::string::npos) {
        return nullptr;
    }
    std::size_t colon = json.find(':', key_pos + marker.size());
    if (colon == std::string::npos) {
        return nullptr;
    }
    ++colon;
    while (colon < json.size() && std::isspace(static_cast<unsigned char>(json[colon]))) {
        ++colon;
    }
    return colon < json.size() ? json.c_str() + colon : nullptr;
}

std::size_t jsonValueLength(const char *value) {
    if (!value || !*value) {
        return 0;
    }
    if (*value == '"') {
        bool escape = false;
        for (std::size_t i = 1; value[i]; ++i) {
            if (escape) {
                escape = false;
                continue;
            }
            if (value[i] == '\\') {
                escape = true;
                continue;
            }
            if (value[i] == '"') {
                return i + 1;
            }
        }
        return 0;
    }
    std::size_t len = 0;
    while (value[len] && value[len] != ',' && value[len] != '}' && value[len] != '\r' && value[len] != '\n') {
        ++len;
    }
    while (len > 0 && std::isspace(static_cast<unsigned char>(value[len - 1]))) {
        --len;
    }
    return len;
}

int base64Index(unsigned char c) {
    if (c >= 'A' && c <= 'Z') return c - 'A';
    if (c >= 'a' && c <= 'z') return c - 'a' + 26;
    if (c >= '0' && c <= '9') return c - '0' + 52;
    if (c == '+') return 62;
    if (c == '/') return 63;
    return -1;
}

} // namespace

std::string trimAscii(std::string value) {
    while (!value.empty() && std::isspace(static_cast<unsigned char>(value.front()))) {
        value.erase(value.begin());
    }
    while (!value.empty() && std::isspace(static_cast<unsigned char>(value.back()))) {
        value.pop_back();
    }
    return value;
}

std::string base64Encode(const std::string &binary) {
    std::string out;
    out.reserve(((binary.size() + 2) / 3) * 4);

    std::size_t i = 0;
    while (i + 3 <= binary.size()) {
        unsigned int block =
            (static_cast<unsigned char>(binary[i]) << 16) |
            (static_cast<unsigned char>(binary[i + 1]) << 8) |
            static_cast<unsigned char>(binary[i + 2]);
        out.push_back(kBase64Alphabet[(block >> 18) & 0x3f]);
        out.push_back(kBase64Alphabet[(block >> 12) & 0x3f]);
        out.push_back(kBase64Alphabet[(block >> 6) & 0x3f]);
        out.push_back(kBase64Alphabet[block & 0x3f]);
        i += 3;
    }

    if (i < binary.size()) {
        unsigned int block = static_cast<unsigned char>(binary[i]) << 16;
        bool have_second = false;
        if (i + 1 < binary.size()) {
            block |= static_cast<unsigned char>(binary[i + 1]) << 8;
            have_second = true;
        }
        out.push_back(kBase64Alphabet[(block >> 18) & 0x3f]);
        out.push_back(kBase64Alphabet[(block >> 12) & 0x3f]);
        out.push_back(have_second ? kBase64Alphabet[(block >> 6) & 0x3f] : '=');
        out.push_back('=');
    }

    return out;
}

bool base64Decode(const std::string &text, std::string *out) {
    if (!out) {
        return false;
    }
    out->clear();
    int value = 0;
    int bits = -8;
    int padding = 0;
    for (unsigned char c : text) {
        if (std::isspace(c)) {
            continue;
        }
        if (c == '=') {
            ++padding;
            continue;
        }
        if (padding != 0) {
            return false;
        }
        int index = base64Index(c);
        if (index < 0) {
            return false;
        }
        value = (value << 6) | index;
        bits += 6;
        if (bits >= 0) {
            out->push_back(static_cast<char>((value >> bits) & 0xff));
            bits -= 8;
        }
    }
    return true;
}

std::string jsonEscape(const std::string &value) {
    std::string out;
    out.reserve(value.size() + 8);
    for (unsigned char c : value) {
        switch (c) {
            case '\\': out += "\\\\"; break;
            case '"': out += "\\\""; break;
            case '\n': out += "\\n"; break;
            case '\r': out += "\\r"; break;
            case '\t': out += "\\t"; break;
            default:
                if (c < 0x20) {
                    static const char *hex = "0123456789ABCDEF";
                    out += "\\u00";
                    out.push_back(hex[c >> 4]);
                    out.push_back(hex[c & 0x0f]);
                } else {
                    out.push_back(static_cast<char>(c));
                }
                break;
        }
    }
    return out;
}

std::string jsonExtractString(const std::string &json, const char *key) {
    const char *value = findJsonValue(json, key);
    if (!value || *value != '"') {
        return {};
    }
    ++value;
    std::string out;
    bool escape = false;
    for (; *value; ++value) {
        if (escape) {
            switch (*value) {
                case 'n': out.push_back('\n'); break;
                case 'r': out.push_back('\r'); break;
                case 't': out.push_back('\t'); break;
                default: out.push_back(*value); break;
            }
            escape = false;
            continue;
        }
        if (*value == '\\') {
            escape = true;
            continue;
        }
        if (*value == '"') {
            break;
        }
        out.push_back(*value);
    }
    return out;
}

std::string jsonExtractRawValue(const std::string &json, const char *key) {
    const char *value = findJsonValue(json, key);
    std::size_t len = jsonValueLength(value);
    return len ? trimAscii(std::string(value, len)) : std::string{};
}

bool jsonExtractInt64(const std::string &json, const char *key, long long *out) {
    std::string raw = jsonExtractRawValue(json, key);
    if (raw.empty()) {
        return false;
    }
    char *end = nullptr;
    long long parsed = std::strtoll(raw.c_str(), &end, 10);
    if (end == raw.c_str()) {
        return false;
    }
    if (out) {
        *out = parsed;
    }
    return true;
}

bool jsonReplaceOrInsertInt(std::string *json, const char *key, int value) {
    if (!json || !key || json->empty()) {
        return false;
    }
    std::string marker = std::string("\"") + key + "\"";
    std::size_t key_pos = json->find(marker);
    std::string replacement = std::to_string(value);
    if (key_pos != std::string::npos) {
        std::size_t colon = json->find(':', key_pos + marker.size());
        if (colon == std::string::npos) {
            return false;
        }
        std::size_t begin = colon + 1;
        while (begin < json->size() && std::isspace(static_cast<unsigned char>((*json)[begin]))) {
            ++begin;
        }
        std::size_t end = begin;
        bool in_string = begin < json->size() && (*json)[begin] == '"';
        if (in_string) {
            bool escape = false;
            ++end;
            while (end < json->size()) {
                if (escape) {
                    escape = false;
                } else if ((*json)[end] == '\\') {
                    escape = true;
                } else if ((*json)[end] == '"') {
                    ++end;
                    break;
                }
                ++end;
            }
        } else {
            while (end < json->size() && (*json)[end] != ',' && (*json)[end] != '}') {
                ++end;
            }
        }
        json->replace(begin, end - begin, replacement);
        return true;
    }

    std::size_t close = json->find_last_of('}');
    if (close == std::string::npos) {
        return false;
    }
    std::size_t open = json->find('{');
    bool has_member = open != std::string::npos && open + 1 < close && trimAscii(json->substr(open + 1, close - open - 1)).size() != 0;
    std::string insertion = has_member ? "," : "";
    insertion += "\"";
    insertion += key;
    insertion += "\":";
    insertion += replacement;
    json->insert(close, insertion);
    return true;
}

}
