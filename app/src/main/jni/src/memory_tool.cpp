#include "include/memory_tool.h"

#include "include/jni_helpers.h"

#include <fcntl.h>
#include <pthread.h>
#include <sys/mman.h>
#include <unistd.h>

#include <algorithm>
#include <atomic>
#include <cerrno>
#include <charconv>
#include <cctype>
#include <cstdio>
#include <cstring>
#include <dirent.h>
#include <fstream>
#include <mutex>
#include <sstream>
#include <thread>
#include <type_traits>

namespace android_mod {
namespace {

std::mutex g_lock;
int g_pid = -1;
int g_range = RANGE_ANONYMOUS;
std::vector<std::uintptr_t> g_results;
std::vector<FreezeItem> g_freeze_items;
std::atomic_bool g_freeze{false};
pthread_t g_freeze_thread{};

template <typename T>
bool parseValue(const std::string &text, T *out) {
    if (!out) {
        return false;
    }
    if constexpr (std::is_floating_point<T>::value) {
        char *end = nullptr;
        double value = std::strtod(text.c_str(), &end);
        if (end == text.c_str()) {
            return false;
        }
        *out = static_cast<T>(value);
        return true;
    } else {
        std::int64_t value = 0;
        const char *begin = text.data();
        const char *end = text.data() + text.size();
        int base = 10;
        if (text.size() > 2 && text[0] == '0' && (text[1] == 'x' || text[1] == 'X')) {
            begin += 2;
            base = 16;
        }
        auto result = std::from_chars(begin, end, value, base);
        if (result.ec != std::errc()) {
            return false;
        }
        *out = static_cast<T>(value);
        return true;
    }
}

std::size_t typeSize(int type) {
    switch (type) {
        case TYPE_BYTE: return 1;
        case TYPE_WORD: return 2;
        case TYPE_DWORD: return 4;
        case TYPE_QWORD: return 8;
        case TYPE_FLOAT: return sizeof(float);
        case TYPE_DOUBLE: return sizeof(double);
        default: return 4;
    }
}

bool encodeValue(const std::string &text, int type, std::uint8_t *out, std::size_t out_size) {
    if (!out || out_size < typeSize(type)) {
        return false;
    }
    switch (type) {
        case TYPE_BYTE: {
            std::int8_t v = 0;
            if (!parseValue(text, &v)) return false;
            std::memcpy(out, &v, sizeof(v));
            return true;
        }
        case TYPE_WORD: {
            std::int16_t v = 0;
            if (!parseValue(text, &v)) return false;
            std::memcpy(out, &v, sizeof(v));
            return true;
        }
        case TYPE_DWORD: {
            std::int32_t v = 0;
            if (!parseValue(text, &v)) return false;
            std::memcpy(out, &v, sizeof(v));
            return true;
        }
        case TYPE_QWORD: {
            std::int64_t v = 0;
            if (!parseValue(text, &v)) return false;
            std::memcpy(out, &v, sizeof(v));
            return true;
        }
        case TYPE_FLOAT: {
            float v = 0.0f;
            if (!parseValue(text, &v)) return false;
            std::memcpy(out, &v, sizeof(v));
            return true;
        }
        case TYPE_DOUBLE: {
            double v = 0.0;
            if (!parseValue(text, &v)) return false;
            std::memcpy(out, &v, sizeof(v));
            return true;
        }
        default:
            return false;
    }
}

std::string procPath(int pid, const char *leaf) {
    char path[64];
    std::snprintf(path, sizeof(path), "/proc/%d/%s", pid, leaf);
    return path;
}

bool protectPage(std::uintptr_t address, int prot) {
    long page = sysconf(_SC_PAGESIZE);
    if (page <= 0) {
        return false;
    }
    std::uintptr_t aligned = address & ~(static_cast<std::uintptr_t>(page) - 1);
    return mprotect(reinterpret_cast<void *>(aligned), static_cast<std::size_t>(page), prot) == 0;
}

bool contains(const std::string &s, const char *needle) {
    return s.find(needle) != std::string::npos;
}

bool isReadableWritable(const MapRange &range) {
    return range.permissions.size() >= 2 && range.permissions[0] == 'r' && range.permissions[1] == 'w';
}

void *freezeThread(void *) {
    while (g_freeze.load()) {
        std::vector<FreezeItem> items;
        {
            std::lock_guard<std::mutex> guard(g_lock);
            items = g_freeze_items;
        }
        for (const auto &item : items) {
            writeValue(item.value, item.address, item.type);
        }
        usleep(100 * 1000);
    }
    return nullptr;
}

} // namespace

int getPackageNamePid(const std::string &name) {
    DIR *dir = opendir("/proc");
    if (!dir) {
        return -1;
    }
    int found = -1;
    while (dirent *entry = readdir(dir)) {
        char *end = nullptr;
        long pid = std::strtol(entry->d_name, &end, 10);
        if (!end || *end != '\0' || pid <= 0) {
            continue;
        }
        std::ifstream cmdline(procPath(static_cast<int>(pid), "cmdline"), std::ios::binary);
        std::string cmd;
        std::getline(cmdline, cmd, '\0');
        if (cmd == name) {
            found = static_cast<int>(pid);
            break;
        }
    }
    closedir(dir);
    return found;
}

void setPackageName(const std::string &name) {
    std::lock_guard<std::mutex> guard(g_lock);
    g_pid = getPackageNamePid(name);
    logInfo("setPackageName(%s) -> pid=%d", name.c_str(), g_pid);
}

void setRange(int range) {
    std::lock_guard<std::mutex> guard(g_lock);
    g_range = range;
}

int currentPid() {
    std::lock_guard<std::mutex> guard(g_lock);
    return g_pid;
}

int currentRange() {
    std::lock_guard<std::mutex> guard(g_lock);
    return g_range;
}

std::vector<MapRange> readMaps(int pid) {
    std::vector<MapRange> out;
    std::ifstream maps(procPath(pid, "maps"));
    std::string line;
    while (std::getline(maps, line)) {
        std::istringstream iss(line);
        std::string address;
        MapRange range;
        if (!(iss >> address >> range.permissions)) {
            continue;
        }
        std::size_t dash = address.find('-');
        if (dash == std::string::npos) {
            continue;
        }
        range.start = static_cast<std::uintptr_t>(std::strtoull(address.substr(0, dash).c_str(), nullptr, 16));
        range.end = static_cast<std::uintptr_t>(std::strtoull(address.substr(dash + 1).c_str(), nullptr, 16));

        std::string ignored;
        iss >> ignored >> ignored >> ignored;
        std::getline(iss, range.pathname);
        range.pathname.erase(range.pathname.begin(), std::find_if(range.pathname.begin(), range.pathname.end(), [](unsigned char c) {
            return !std::isspace(c);
        }));
        out.push_back(range);
    }
    return out;
}

std::vector<MapRange> filterRanges(const std::vector<MapRange> &ranges, int range_kind) {
    std::vector<MapRange> out;
    for (const auto &range : ranges) {
        if (!isReadableWritable(range) && range_kind != RANGE_ALL) {
            continue;
        }
        const std::string &path = range.pathname;
        bool keep = false;
        switch (range_kind) {
            case RANGE_ALL:
                keep = true;
                break;
            case RANGE_C_HEAP:
                keep = contains(path, "[heap]");
                break;
            case RANGE_C_ALLOC:
                keep = contains(path, "[anon:libc_malloc]");
                break;
            case RANGE_ANONYMOUS:
                keep = path.empty() || contains(path, "[anon:");
                break;
            case RANGE_STACK:
                keep = contains(path, "[stack]");
                break;
            case RANGE_VIDEO:
                keep = contains(path, "/dev/kgsl-3d0");
                break;
            case RANGE_CODE_APP:
                keep = contains(path, "/data/app/");
                break;
            case RANGE_CODE_SYSTEM:
                keep = contains(path, "/system/") || contains(path, "/apex/");
                break;
            case RANGE_ASHMEM:
                keep = contains(path, "/dev/ashmem") || contains(path, "[anon:dalvik");
                break;
            case RANGE_C_DATA:
            case RANGE_C_BSS:
                keep = contains(path, "/data/app/") && isReadableWritable(range);
                break;
            case RANGE_JAVA_HEAP:
                keep = contains(path, "dalvik") || contains(path, "zygote");
                break;
            case RANGE_B_BAD:
            case RANGE_OTHER:
                keep = !path.empty();
                break;
            default:
                keep = isReadableWritable(range);
                break;
        }
        if (keep) {
            out.push_back(range);
        }
    }
    return out;
}

std::uintptr_t getModuleAddress(const std::string &module) {
    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }
    for (const auto &range : readMaps(pid)) {
        if (range.pathname.find(module) != std::string::npos) {
            return range.start;
        }
    }
    return 0;
}

void clearResult() {
    std::lock_guard<std::mutex> guard(g_lock);
    g_results.clear();
}

int getResultCount() {
    std::lock_guard<std::mutex> guard(g_lock);
    return static_cast<int>(g_results.size());
}

std::vector<std::uintptr_t> getResults() {
    std::lock_guard<std::mutex> guard(g_lock);
    return g_results;
}

bool readTyped(std::uintptr_t address, int type, void *out, std::size_t out_size) {
    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }
    if (pid <= 0 || !out || out_size < typeSize(type)) {
        return false;
    }
    std::string mem = procPath(pid, "mem");
    int fd = open(mem.c_str(), O_RDONLY);
    if (fd < 1) {
        if (fd >= 0) {
            close(fd);
        }
        return false;
    }
    ssize_t n = pread64(fd, out, typeSize(type), static_cast<off64_t>(address));
    close(fd);
    return n == static_cast<ssize_t>(typeSize(type));
}

std::uint64_t readLong(std::uintptr_t address) {
    std::uint64_t value = 0;
    readTyped(address, TYPE_QWORD, &value, sizeof(value));
    return value;
}

void rangeMemorySearch(const std::string &value, int type) {
    std::uint8_t needle[8]{};
    const std::size_t needle_size = typeSize(type);
    if (!encodeValue(value, type, needle, sizeof(needle))) {
        logWarn("rangeMemorySearch: cannot encode '%s' as type %d", value.c_str(), type);
        return;
    }

    int pid = currentPid();
    int range_kind = currentRange();
    if (pid <= 0) {
        return;
    }

    std::vector<std::uintptr_t> results;
    std::string mem = procPath(pid, "mem");
    int fd = open(mem.c_str(), O_RDONLY);
    if (fd < 1) {
        logWarn("open %s failed: %s", mem.c_str(), std::strerror(errno));
        if (fd >= 0) {
            close(fd);
        }
        return;
    }

    constexpr std::size_t kChunk = 64 * 1024;
    std::vector<std::uint8_t> buffer(kChunk + 8);
    for (const auto &range : filterRanges(readMaps(pid), range_kind)) {
        for (std::uintptr_t pos = range.start; pos < range.end;) {
            std::size_t to_read = std::min<std::uintptr_t>(kChunk, range.end - pos);
            ssize_t n = pread64(fd, buffer.data(), to_read, static_cast<off64_t>(pos));
            if (n <= 0) {
                pos += kChunk;
                continue;
            }
            for (ssize_t i = 0; i + static_cast<ssize_t>(needle_size) <= n; ++i) {
                if (std::memcmp(buffer.data() + i, needle, needle_size) == 0) {
                    results.push_back(pos + static_cast<std::uintptr_t>(i));
                }
            }
            pos += static_cast<std::uintptr_t>(n);
        }
    }

    close(fd);
    {
        std::lock_guard<std::mutex> guard(g_lock);
        g_results.swap(results);
    }
}

void memoryOffset(const std::string &value, int type, std::int64_t offset) {
    std::uint8_t needle[8]{};
    const std::size_t needle_size = typeSize(type);
    if (!encodeValue(value, type, needle, sizeof(needle))) {
        return;
    }
    std::vector<std::uintptr_t> current = getResults();
    std::vector<std::uintptr_t> next;
    for (auto address : current) {
        std::uint8_t read_back[8]{};
        if (readTyped(static_cast<std::uintptr_t>(address + offset), type, read_back, sizeof(read_back)) &&
            std::memcmp(read_back, needle, needle_size) == 0) {
            next.push_back(address);
        }
    }
    std::lock_guard<std::mutex> guard(g_lock);
    g_results.swap(next);
}

void memoryWrite(const std::string &value, int type, std::int64_t offset) {
    std::vector<std::uintptr_t> current = getResults();
    for (auto address : current) {
        writeValue(value, static_cast<std::uintptr_t>(address + offset), type);
    }
}

bool writeValue(const std::string &value, std::uintptr_t address, int type) {
    std::uint8_t encoded[8]{};
    std::size_t size = typeSize(type);
    if (!encodeValue(value, type, encoded, sizeof(encoded))) {
        return false;
    }
    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }

    std::string mem = procPath(pid, "mem");
    int fd = open(mem.c_str(), O_WRONLY);
    if (fd < 1) {
        if (fd >= 0) {
            close(fd);
        }
        return false;
    }
    protectPage(address, PROT_READ | PROT_WRITE | PROT_EXEC);
    ssize_t n = pwrite64(fd, encoded, size, static_cast<off64_t>(address));
    protectPage(address, PROT_WRITE | PROT_EXEC);
    close(fd);
    return n == static_cast<ssize_t>(size);
}

bool writeBytes(std::uintptr_t address, const void *data, std::size_t size) {
    if (!data || size == 0) {
        return false;
    }

    int pid = currentPid();
    if (pid <= 0) {
        pid = getpid();
    }

    std::string mem = procPath(pid, "mem");
    int fd = open(mem.c_str(), O_WRONLY);
    if (fd < 1) {
        if (fd >= 0) {
            close(fd);
        }
        return false;
    }
    protectPage(address, PROT_READ | PROT_WRITE | PROT_EXEC);
    ssize_t n = pwrite64(fd, data, size, static_cast<off64_t>(address));
    protectPage(address, PROT_WRITE | PROT_EXEC);
    close(fd);
    return n == static_cast<ssize_t>(size);
}

void addFreezeItem(const std::string &value, std::uintptr_t address, int type) {
    std::lock_guard<std::mutex> guard(g_lock);
    g_freeze_items.push_back(FreezeItem{value, address, type});
}

void removeFreezeItem(std::uintptr_t address) {
    std::lock_guard<std::mutex> guard(g_lock);
    g_freeze_items.erase(
        std::remove_if(g_freeze_items.begin(), g_freeze_items.end(), [address](const FreezeItem &item) {
            return item.address == address;
        }),
        g_freeze_items.end());
}

void startFreeze() {
    bool expected = false;
    if (g_freeze.compare_exchange_strong(expected, true)) {
        pthread_create(&g_freeze_thread, nullptr, freezeThread, nullptr);
    }
}

void stopFreeze() {
    bool expected = true;
    if (g_freeze.compare_exchange_strong(expected, false)) {
        pthread_join(g_freeze_thread, nullptr);
    }
}

}
