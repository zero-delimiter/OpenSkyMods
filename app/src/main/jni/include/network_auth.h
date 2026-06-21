#pragma once

#include <cstdint>
#include <map>
#include <utility>
#include <string>
#include <vector>

namespace android_mod {

struct HttpResponse {
    int status_code = 0;
    std::string headers;
    std::string body;
    std::string error;
};

struct AuthConfig {
    std::string host = "wy.llua.cn";
    int port = 80;
    std::string app_id = "64767";
    std::string app_id_alt = "68890";
    std::string rc4_key = "wcwnCD4EcvkI0";
    std::string rc4_key_alt = "40LDM6oG43NtE6i";
    std::string sign_salt = "0kiG0I31Mvg600M0";
    int expected_code = 893;
    int expected_code_alt = 2183;
    int expected_signed_code = 200;
};

struct AuthResult {
    bool ok = false;
    int server_code = 0;
    int signed_num = 0;
    std::string message;
    std::string expire_time;
    std::string raw_body;
    std::string decrypted_body;
};

std::vector<std::uint8_t> rc4(const std::vector<std::uint8_t> &input, const std::string &key);
std::string rc4String(const std::string &input, const std::string &key);
std::string rc4HexString(const std::string &input, const std::string &key);
std::string rc4DecryptHexString(const std::string &hex_input, const std::string &key);

HttpResponse httpPostForm(
    const std::string &host,
    int port,
    const std::string &path,
    const std::map<std::string, std::string> &form);

HttpResponse httpPostRawBody(
    const std::string &host,
    int port,
    const std::string &path,
    const std::string &body);

HttpResponse httpPostMultipartFile(
    const std::string &url,
    const std::string &boundary,
    const std::string &field_name,
    const std::string &filename,
    const std::vector<std::uint8_t> &file_bytes,
    const std::map<std::string, std::string> &fields);

HttpResponse httpPostMultipartFileOrdered(
    const std::string &url,
    const std::string &boundary,
    const std::string &field_name,
    const std::string &filename,
    const std::vector<std::uint8_t> &file_bytes,
    const std::vector<std::pair<std::string, std::string>> &fields,
    bool file_part_first);

AuthResult loginWithCard(
    const std::string &card,
    const std::string &device_id,
    const AuthConfig &config = AuthConfig{});

AuthResult loginWithDeveloperKey(
    const std::string &key,
    const std::string &device_id,
    const AuthConfig &config = AuthConfig{});

AuthResult checkSignedValue(
    const std::string &card,
    const std::string &device_id,
    const AuthConfig &config = AuthConfig{});

HttpResponse fetchNotice(const AuthConfig &config = AuthConfig{});
std::string fetchNoticeAppGg(const AuthConfig &config = AuthConfig{});

}
