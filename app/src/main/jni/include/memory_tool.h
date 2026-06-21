#pragma once

#include <cstddef>
#include <cstdint>
#include <string>
#include <vector>

namespace android_mod {

enum MemoryRange {
    RANGE_ALL = 0,
    RANGE_C_BSS = 1,
    RANGE_C_HEAP = 2,
    RANGE_C_ALLOC = 3,
    RANGE_C_DATA = 4,
    RANGE_STACK = 5,
    RANGE_B_BAD = 6,
    RANGE_ANONYMOUS = 7,
    RANGE_JAVA_HEAP = 8,
    RANGE_CODE_APP = 9,
    RANGE_CODE_SYSTEM = 10,
    RANGE_VIDEO = 11,
    RANGE_ASHMEM = 12,
    RANGE_OTHER = 13,
};

enum ValueType {
    TYPE_DWORD = 1,
    TYPE_FLOAT = 2,
    TYPE_DOUBLE = 3,
    TYPE_QWORD = 4,
    TYPE_BYTE = 5,
    TYPE_WORD = 6,
};

struct MapRange {
    std::uintptr_t start = 0;
    std::uintptr_t end = 0;
    std::string permissions;
    std::string pathname;
};

struct FreezeItem {
    std::string value;
    std::uintptr_t address = 0;
    int type = TYPE_DWORD;
};

int getPackageNamePid(const std::string &name);
void setPackageName(const std::string &name);
void setRange(int range);
int currentPid();
int currentRange();

std::uintptr_t getModuleAddress(const std::string &module);
std::vector<MapRange> readMaps(int pid);
std::vector<MapRange> filterRanges(const std::vector<MapRange> &ranges, int range_kind);

void clearResult();
int getResultCount();
std::vector<std::uintptr_t> getResults();

void rangeMemorySearch(const std::string &value, int type);
void memoryOffset(const std::string &value, int type, std::int64_t offset);
void memoryWrite(const std::string &value, int type, std::int64_t offset);

std::uint64_t readLong(std::uintptr_t address);
bool readTyped(std::uintptr_t address, int type, void *out, std::size_t out_size);
bool writeValue(const std::string &value, std::uintptr_t address, int type);
bool writeBytes(std::uintptr_t address, const void *data, std::size_t size);

void addFreezeItem(const std::string &value, std::uintptr_t address, int type);
void removeFreezeItem(std::uintptr_t address);
void startFreeze();
void stopFreeze();

}
