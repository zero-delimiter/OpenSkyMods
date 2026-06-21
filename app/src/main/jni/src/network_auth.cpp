#include "include/network_auth.h"

#include "include/jni_helpers.h"

#include <arpa/inet.h>
#include <netdb.h>
#include <sys/socket.h>
#include <unistd.h>

#include <algorithm>
#include <cerrno>
#include <cctype>
#include <cstdlib>
#include <cstring>
#include <ctime>
#include <cstdint>
#include <sstream>

#if ENABLE_DIAGNOSTIC_LOGS
#define NETWORK_AUTH_TEXT(text) text
#else
#define NETWORK_AUTH_TEXT(text) ""
#endif

namespace android_mod {
namespace {

std::string trimAscii(std::string value) {
    while (!value.empty() && std::isspace(static_cast<unsigned char>(value.front()))) {
        value.erase(value.begin());
    }
    while (!value.empty() && std::isspace(static_cast<unsigned char>(value.back()))) {
        value.pop_back();
    }
    return value;
}

std::string formEncode(const std::map<std::string, std::string> &form) {
    std::ostringstream out;
    bool first = true;
    for (const auto &item : form) {
        if (!first) {
            out << '&';
        }
        first = false;
        out << item.first << '=';
        for (unsigned char c : item.second) {
            if (std::isalnum(c) || c == '-' || c == '_' || c == '.') {
                out << static_cast<char>(c);
            } else {
                static const char *hex = "0123456789ABCDEF";
                out << '%' << hex[c >> 4] << hex[c & 0x0f];
            }
        }
    }
    return out.str();
}

struct ParsedHttpUrl {
    std::string host;
    int port = 80;
    std::string path;
};

bool parseHttpUrl(const std::string &url, ParsedHttpUrl *out) {
    static constexpr const char *kPrefix = "http://";
    if (!out || url.compare(0, std::strlen(kPrefix), kPrefix) != 0) {
        return false;
    }

    std::size_t host_start = std::strlen(kPrefix);
    std::size_t slash = url.find('/', host_start);
    std::string host_port = slash == std::string::npos
        ? url.substr(host_start)
        : url.substr(host_start, slash - host_start);
    if (host_port.empty()) {
        return false;
    }

    out->path = slash == std::string::npos ? "/" : url.substr(slash);
    std::size_t colon = host_port.rfind(':');
    if (colon != std::string::npos) {
        out->host = host_port.substr(0, colon);
        out->port = std::atoi(host_port.c_str() + colon + 1);
        if (out->host.empty() || out->port <= 0) {
            return false;
        }
    } else {
        out->host = host_port;
        out->port = 80;
    }
    return true;
}

bool isHexText(const std::string &text) {
    std::string value = trimAscii(text);
    if (value.empty() || (value.size() & 1u) != 0) {
        return false;
    }
    for (unsigned char c : value) {
        if (!std::isxdigit(c)) {
            return false;
        }
    }
    return true;
}

int hexNibble(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return c - 'a' + 10;
    if (c >= 'A' && c <= 'F') return c - 'A' + 10;
    return -1;
}

std::vector<std::uint8_t> hexToBytes(const std::string &text) {
    std::string value = trimAscii(text);
    std::vector<std::uint8_t> out;
    if ((value.size() & 1u) != 0) {
        return out;
    }
    out.reserve(value.size() / 2);
    for (std::size_t i = 0; i < value.size(); i += 2) {
        int hi = hexNibble(value[i]);
        int lo = hexNibble(value[i + 1]);
        if (hi < 0 || lo < 0) {
            out.clear();
            return out;
        }
        out.push_back(static_cast<std::uint8_t>((hi << 4) | lo));
    }
    return out;
}

std::string bytesToLowerHex(const std::vector<std::uint8_t> &bytes) {
    static const char *hex = "0123456789abcdef";
    std::string out;
    out.resize(bytes.size() * 2);
    for (std::size_t i = 0; i < bytes.size(); ++i) {
        out[i * 2] = hex[bytes[i] >> 4];
        out[i * 2 + 1] = hex[bytes[i] & 0x0f];
    }
    return out;
}

int connectTcp(const std::string &host, int port, std::string *error) {
    addrinfo hints{};
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    addrinfo *res = nullptr;
    std::string port_text = std::to_string(port);
    int gai = getaddrinfo(host.c_str(), port_text.c_str(), &hints, &res);
    if (gai != 0) {
        if (error) *error = gai_strerror(gai);
        return -1;
    }
    int fd = -1;
    for (addrinfo *it = res; it; it = it->ai_next) {
        fd = socket(it->ai_family, it->ai_socktype, it->ai_protocol);
        if (fd < 0) {
            continue;
        }
        if (connect(fd, it->ai_addr, it->ai_addrlen) == 0) {
            break;
        }
        close(fd);
        fd = -1;
    }
    freeaddrinfo(res);
    if (fd < 0 && error) {
        *error = std::strerror(errno);
    }
    return fd;
}

bool sendAll(int fd, const std::string &data, std::string *error) {
    const char *cursor = data.data();
    std::size_t left = data.size();
    while (left) {
        ssize_t sent = send(fd, cursor, left, 0);
        if (sent < 0) {
            if (error) *error = std::strerror(errno);
            return false;
        }
        cursor += sent;
        left -= static_cast<std::size_t>(sent);
    }
    return true;
}

std::string decodeChunked(const std::string &body) {
    std::string out;
    std::size_t pos = 0;
    for (;;) {
        std::size_t line_end = body.find("\r\n", pos);
        if (line_end == std::string::npos) {
            return out.empty() ? body : out;
        }
        std::string size_text = body.substr(pos, line_end - pos);
        std::size_t semi = size_text.find(';');
        if (semi != std::string::npos) {
            size_text.resize(semi);
        }
        long chunk_size = std::strtol(size_text.c_str(), nullptr, 16);
        pos = line_end + 2;
        if (chunk_size <= 0) {
            break;
        }
        if (pos + static_cast<std::size_t>(chunk_size) > body.size()) {
            return out.empty() ? body : out;
        }
        out.append(body, pos, static_cast<std::size_t>(chunk_size));
        pos += static_cast<std::size_t>(chunk_size);
        if (body.compare(pos, 2, "\r\n") == 0) {
            pos += 2;
        }
    }
    return out;
}

bool headerHasChunkedEncoding(const std::string &headers) {
    std::string lower = headers;
    std::transform(lower.begin(), lower.end(), lower.begin(), [](unsigned char c) {
        return static_cast<char>(std::tolower(c));
    });
    return lower.find("transfer-encoding:") != std::string::npos && lower.find("chunked") != std::string::npos;
}

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

std::string extractJsonString(const std::string &json, const char *key) {
    const char *value = findJsonValue(json, key);
    if (!value || *value != '"') {
        return {};
    }
    ++value;
    std::string out;
    for (; *value; ++value) {
        if (*value == '\\' && value[1]) {
            ++value;
            switch (*value) {
                case 'n': out.push_back('\n'); break;
                case 'r': out.push_back('\r'); break;
                case 't': out.push_back('\t'); break;
                default: out.push_back(*value); break;
            }
            continue;
        }
        if (*value == '"') {
            break;
        }
        out.push_back(*value);
    }
    return out;
}

bool extractJsonInt(const std::string &json, const char *key, int *out) {
    const char *value = findJsonValue(json, key);
    if (!value) {
        return false;
    }
    char *end = nullptr;
    long parsed = std::strtol(value, &end, 10);
    if (end == value) {
        return false;
    }
    if (out) {
        *out = static_cast<int>(parsed);
    }
    return true;
}

std::string extractJsonObject(const std::string &json, const char *key) {
    const char *value = findJsonValue(json, key);
    if (!value || *value != '{') {
        return {};
    }

    const char *end = value;
    int depth = 0;
    bool in_string = false;
    bool escaped = false;
    for (; *end; ++end) {
        char c = *end;
        if (escaped) {
            escaped = false;
            continue;
        }
        if (in_string) {
            if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                in_string = false;
            }
            continue;
        }
        if (c == '"') {
            in_string = true;
        } else if (c == '{') {
            ++depth;
        } else if (c == '}') {
            --depth;
            if (depth == 0) {
                ++end;
                break;
            }
        }
    }
    return std::string(value, static_cast<std::size_t>(end - value));
}

std::string formatUnixTime(int value) {
    if (value <= 0) {
        return {};
    }
    std::time_t time_value = static_cast<std::time_t>(value);
    std::tm tm_value{};
    if (!localtime_r(&time_value, &tm_value)) {
        return {};
    }
    char buffer[32]{};
    if (std::strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", &tm_value) == 0) {
        return {};
    }
    return buffer;
}

std::uint32_t rotateLeft(std::uint32_t value, std::uint32_t shift) {
    return (value << shift) | (value >> (32 - shift));
}

std::string md5Lower(const std::string &input) {
    static constexpr std::uint32_t kShift[64] = {
        7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
        5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
        4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
        6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21,
    };
    static constexpr std::uint32_t kTable[64] = {
        0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
        0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
        0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
        0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
        0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
        0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
        0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
        0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
        0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
        0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
        0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
        0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
        0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
        0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
        0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
        0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391,
    };

    std::vector<std::uint8_t> data(input.begin(), input.end());
    std::uint64_t bit_len = static_cast<std::uint64_t>(data.size()) * 8;
    data.push_back(0x80);
    while ((data.size() % 64) != 56) {
        data.push_back(0);
    }
    for (int i = 0; i < 8; ++i) {
        data.push_back(static_cast<std::uint8_t>((bit_len >> (8 * i)) & 0xff));
    }

    std::uint32_t a0 = 0x67452301;
    std::uint32_t b0 = 0xefcdab89;
    std::uint32_t c0 = 0x98badcfe;
    std::uint32_t d0 = 0x10325476;

    for (std::size_t offset = 0; offset < data.size(); offset += 64) {
        std::uint32_t words[16]{};
        for (int i = 0; i < 16; ++i) {
            words[i] = static_cast<std::uint32_t>(data[offset + i * 4])
                | (static_cast<std::uint32_t>(data[offset + i * 4 + 1]) << 8)
                | (static_cast<std::uint32_t>(data[offset + i * 4 + 2]) << 16)
                | (static_cast<std::uint32_t>(data[offset + i * 4 + 3]) << 24);
        }

        std::uint32_t a = a0;
        std::uint32_t b = b0;
        std::uint32_t c = c0;
        std::uint32_t d = d0;

        for (int i = 0; i < 64; ++i) {
            std::uint32_t f = 0;
            int g = 0;
            if (i < 16) {
                f = (b & c) | (~b & d);
                g = i;
            } else if (i < 32) {
                f = (d & b) | (~d & c);
                g = (5 * i + 1) % 16;
            } else if (i < 48) {
                f = b ^ c ^ d;
                g = (3 * i + 5) % 16;
            } else {
                f = c ^ (b | ~d);
                g = (7 * i) % 16;
            }
            std::uint32_t temp = d;
            d = c;
            c = b;
            b = b + rotateLeft(a + f + kTable[i] + words[g], kShift[i]);
            a = temp;
        }

        a0 += a;
        b0 += b;
        c0 += c;
        d0 += d;
    }

    std::vector<std::uint8_t> digest;
    digest.reserve(16);
    for (std::uint32_t word : {a0, b0, c0, d0}) {
        digest.push_back(static_cast<std::uint8_t>(word & 0xff));
        digest.push_back(static_cast<std::uint8_t>((word >> 8) & 0xff));
        digest.push_back(static_cast<std::uint8_t>((word >> 16) & 0xff));
        digest.push_back(static_cast<std::uint8_t>((word >> 24) & 0xff));
    }
    return bytesToLowerHex(digest);
}

AuthResult parseNativeLoginResult(
    const HttpResponse &http,
    const std::string &decrypted,
    int expected_code) {
    AuthResult result;
    result.raw_body = http.body;
    result.decrypted_body = decrypted;
    if (!http.error.empty()) {
        result.message = http.error;
        return result;
    }
    if (decrypted.empty()) {
        result.message = "解密失败请重试！";
        return result;
    }

    int code = 0;
    if (!extractJsonInt(decrypted, "code", &code)) {
        result.message = "解析失败请重试！";
        return result;
    }
    result.server_code = code;

    if (code == expected_code) {
        std::string msg_object = extractJsonObject(decrypted, "msg");
        std::string kmtype = extractJsonString(msg_object, "kmtype");
        if (kmtype.find(u8"永久") != std::string::npos) {
            result.expire_time = kmtype;
        } else {
            int vip = 0;
            if (extractJsonInt(msg_object, "vip", &vip) || extractJsonInt(decrypted, "vip", &vip)) {
                result.expire_time = formatUnixTime(vip);
            }
        }
        result.ok = true;
        result.message = "success";
        return result;
    }

    std::string msg = extractJsonString(decrypted, "msg");
    result.message = msg.empty() ? "解析失败请重试！" : msg;
    return result;
}

AuthResult parseNativeSignedResult(
    const HttpResponse &http,
    const std::string &decrypted,
    int expected_code) {
    AuthResult result = parseNativeLoginResult(http, decrypted, expected_code);
    if (result.ok) {
        std::string msg_object = extractJsonObject(decrypted, "msg");
        int num = 0;
        if (extractJsonInt(msg_object, "num", &num)) {
            result.signed_num = num;
        }
    }
    return result;
}

std::string buildLoginPlaintext(const std::string &card, const std::string &device_id, int now) {
    std::string now_text = std::to_string(now);
    return "kami=" + card + "&markcode=" + device_id + "&t=" + now_text + "&value=" + now_text;
}

AuthResult submitNativeLogin(
    const std::string &card,
    const std::string &device_id,
    const std::string &app_id,
    const std::string &rc4_key,
    int expected_code,
    const AuthConfig &config) {
    AuthResult result;
    int now = static_cast<int>(std::time(nullptr));
    std::string plaintext = buildLoginPlaintext(card, device_id, now);
    std::string data = rc4HexString(plaintext, rc4_key);
    if (data.empty()) {
        result.message = "加密失败请重试！";
        return result;
    }

    std::string path = "api/?id=kmlogon&app=" + app_id;
    HttpResponse http = httpPostRawBody(config.host, config.port, path, "&data=" + data);
    std::string decrypted = rc4DecryptHexString(http.body, rc4_key);
    return parseNativeLoginResult(http, decrypted, expected_code);
}

} // namespace

std::vector<std::uint8_t> rc4(const std::vector<std::uint8_t> &input, const std::string &key) {
    if (key.empty()) {
        return {};
    }
    std::vector<std::uint8_t> s(256);
    for (int i = 0; i < 256; ++i) {
        s[static_cast<std::size_t>(i)] = static_cast<std::uint8_t>(i);
    }
    int j = 0;
    for (int i = 0; i < 256; ++i) {
        j = (j + s[static_cast<std::size_t>(i)] + static_cast<unsigned char>(key[static_cast<std::size_t>(i) % key.size()])) & 0xff;
        std::swap(s[static_cast<std::size_t>(i)], s[static_cast<std::size_t>(j)]);
    }
    std::vector<std::uint8_t> out(input.size());
    int i = 0;
    j = 0;
    for (std::size_t n = 0; n < input.size(); ++n) {
        i = (i + 1) & 0xff;
        j = (j + s[static_cast<std::size_t>(i)]) & 0xff;
        std::swap(s[static_cast<std::size_t>(i)], s[static_cast<std::size_t>(j)]);
        std::uint8_t k = s[(s[static_cast<std::size_t>(i)] + s[static_cast<std::size_t>(j)]) & 0xff];
        out[n] = input[n] ^ k;
    }
    return out;
}

std::string rc4String(const std::string &input, const std::string &key) {
    std::vector<std::uint8_t> bytes(input.begin(), input.end());
    auto out = rc4(bytes, key);
    return std::string(out.begin(), out.end());
}

std::string rc4HexString(const std::string &input, const std::string &key) {
    std::vector<std::uint8_t> bytes(input.begin(), input.end());
    auto out = rc4(bytes, key);
    return bytesToLowerHex(out);
}

std::string rc4DecryptHexString(const std::string &hex_input, const std::string &key) {
    if (!isHexText(hex_input)) {
        return {};
    }
    auto bytes = hexToBytes(hex_input);
    auto out = rc4(bytes, key);
    return std::string(out.begin(), out.end());
}

HttpResponse httpPostRawBody(
    const std::string &host,
    int port,
    const std::string &path,
    const std::string &body) {
    HttpResponse response;
    std::string error;
    int fd = connectTcp(host, port, &error);
    if (fd < 0) {
        response.error = error;
        return response;
    }

    std::ostringstream request;
    request << "POST /" << path << " HTTP/1.1\r\n"
            << "Host: " << host << "\r\n"
            << "Content-Type: application/x-www-form-urlencoded\r\n"
            << "User-Agent: Mozilla/4.0(compatible)\r\n"
            << "Content-Length: " << body.size() << "\r\n\r\n"
            << body << "\r\n\r\n";

    std::string request_text = request.str();
    if (!sendAll(fd, request_text, &response.error)) {
        close(fd);
        return response;
    }

    char buffer[4096];
    std::string raw;
    for (;;) {
        ssize_t n = recv(fd, buffer, sizeof(buffer), 0);
        if (n <= 0) {
            break;
        }
        raw.append(buffer, buffer + n);
    }
    close(fd);

    std::size_t first_space = raw.find(' ');
    if (first_space != std::string::npos && first_space + 4 <= raw.size()) {
        response.status_code = std::atoi(raw.c_str() + first_space + 1);
    }
    std::size_t split = raw.find("\r\n\r\n");
    if (split == std::string::npos) {
        response.body = raw;
    } else {
        response.headers = raw.substr(0, split);
        response.body = raw.substr(split + 4);
        if (headerHasChunkedEncoding(response.headers)) {
            response.body = decodeChunked(response.body);
        }
    }
    return response;
}

HttpResponse httpPostMultipartFile(
    const std::string &url,
    const std::string &boundary,
    const std::string &field_name,
    const std::string &filename,
    const std::vector<std::uint8_t> &file_bytes,
    const std::map<std::string, std::string> &fields) {
    std::vector<std::pair<std::string, std::string>> ordered_fields;
    ordered_fields.reserve(fields.size());
    for (const auto &field : fields) {
        ordered_fields.push_back(field);
    }
    return httpPostMultipartFileOrdered(
        url,
        boundary,
        field_name,
        filename,
        file_bytes,
        ordered_fields,
        false);
}

HttpResponse httpPostMultipartFileOrdered(
    const std::string &url,
    const std::string &boundary,
    const std::string &field_name,
    const std::string &filename,
    const std::vector<std::uint8_t> &file_bytes,
    const std::vector<std::pair<std::string, std::string>> &fields,
    bool file_part_first) {
    HttpResponse response;

    ParsedHttpUrl parsed;
    if (!parseHttpUrl(url, &parsed)) {
        response.error = NETWORK_AUTH_TEXT("invalid http url");
        return response;
    }

    std::string body;

    auto append_file_part = [&]() {
        body += "--" + boundary + "\r\n";
        body += "Content-Disposition: form-data; name=\"" + field_name + "\"; filename=\"" + filename + "\"\r\n";
        body += "Content-Type: application/octet-stream\r\n\r\n";
        body.append(reinterpret_cast<const char *>(file_bytes.data()), file_bytes.size());
        body += "\r\n";
    };

    auto append_field_part = [&](const std::pair<std::string, std::string> &field) {
        body += "--" + boundary + "\r\n";
        body += "Content-Disposition: form-data; name=\"" + field.first + "\"\r\n\r\n";
        body += field.second;
        body += "\r\n";
    };

    if (file_part_first) {
        append_file_part();
    }
    for (const auto &field : fields) {
        append_field_part(field);
    }
    if (!file_part_first) {
        append_file_part();
    }
    body += "--" + boundary + "--\r\n";

    std::string error;
    int fd = connectTcp(parsed.host, parsed.port, &error);
    if (fd < 0) {
        response.error = error;
        return response;
    }

    std::ostringstream request;
    request << "POST " << parsed.path << " HTTP/1.1\r\n"
            << "Host: " << parsed.host << "\r\n"
            << "Content-Type: multipart/form-data; boundary=" << boundary << "\r\n"
            << "Content-Length: " << body.size() << "\r\n"
            << "Connection: close\r\n\r\n";

    std::string request_text = request.str();
    request_text += body;
    if (!sendAll(fd, request_text, &response.error)) {
        close(fd);
        return response;
    }

    char buffer[4096];
    std::string raw;
    for (;;) {
        ssize_t n = recv(fd, buffer, sizeof(buffer), 0);
        if (n <= 0) {
            break;
        }
        raw.append(buffer, buffer + n);
    }
    close(fd);

    std::size_t first_space = raw.find(' ');
    if (first_space != std::string::npos && first_space + 4 <= raw.size()) {
        response.status_code = std::atoi(raw.c_str() + first_space + 1);
    }
    std::size_t split = raw.find("\r\n\r\n");
    if (split == std::string::npos) {
        response.body = raw;
    } else {
        response.headers = raw.substr(0, split);
        response.body = raw.substr(split + 4);
        if (headerHasChunkedEncoding(response.headers)) {
            response.body = decodeChunked(response.body);
        }
    }
    return response;
}

HttpResponse httpPostForm(
    const std::string &host,
    int port,
    const std::string &path,
    const std::map<std::string, std::string> &form) {
    return httpPostRawBody(host, port, path, formEncode(form));
}

AuthResult loginWithCard(const std::string &card, const std::string &device_id, const AuthConfig &config) {
    return submitNativeLogin(card, device_id, config.app_id, config.rc4_key, config.expected_code, config);
}

AuthResult loginWithDeveloperKey(const std::string &key, const std::string &device_id, const AuthConfig &config) {
    return submitNativeLogin(key, device_id, config.app_id_alt, config.rc4_key_alt, config.expected_code_alt, config);
}

AuthResult checkSignedValue(const std::string &card, const std::string &device_id, const AuthConfig &config) {
    AuthResult result;
    int now = static_cast<int>(std::time(nullptr));
    std::string sign_material =
        "kami=" + card + "&markcode=" + device_id + "&t=" + std::to_string(now) + "&" + config.sign_salt;
    std::string sign = md5Lower(sign_material);
    std::string value = std::to_string(now) + std::to_string(std::rand());
    std::string plaintext =
        "kami=" + card + "&markcode=" + device_id + "&t=" + std::to_string(now) + "&sign=" + sign + "&value=" + value;
    std::string data = rc4HexString(plaintext, config.rc4_key);
    if (data.empty()) {
        result.message = "加密失败请重试！";
        return result;
    }

    std::string path = "api/?id=kmdismiss&app=" + config.app_id;
    HttpResponse http = httpPostRawBody(config.host, config.port, path, "&data=" + data);
    std::string decrypted = rc4DecryptHexString(http.body, config.rc4_key);
    return parseNativeSignedResult(http, decrypted, config.expected_signed_code);
}

HttpResponse fetchNotice(const AuthConfig &config) {
    return httpPostRawBody(config.host, config.port, "api/?id=notice", "app=" + config.app_id);
}

std::string fetchNoticeAppGg(const AuthConfig &config) {
    HttpResponse http = fetchNotice(config);
    if (!http.error.empty()) {
        return {};
    }
    int code = 0;
    if (!extractJsonInt(http.body, "code", &code) || code != 200) {
        return {};
    }

    std::string msg_object = extractJsonObject(http.body, "msg");
    if (msg_object.empty()) {
        return {};
    }
    return extractJsonString(msg_object, "app_gg");
}

}
