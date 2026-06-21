#pragma once

#include <cstdint>

namespace android_mod {

struct GameMemoryPatchResult {
    bool ok = false;
    int index = -1;
    std::uint32_t id = 0;
    const char *payload = nullptr;
    const char *error = nullptr;
    std::uintptr_t base = 0;
};

const char *findGamePatchPayloadById(std::uint32_t id, int *index);
const char *findGamePatchPayloadByIndex(int index, std::uint32_t *id);
bool findGamePatchIdByPayload(const char *payload, std::uint32_t *id, int *index);
GameMemoryPatchResult applyGameMemoryPatchById(std::uint32_t id);
GameMemoryPatchResult applyGameMemoryPatchByIndex(int index);

}
