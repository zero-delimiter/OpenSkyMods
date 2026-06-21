#pragma once

#include <cstddef>
#include <cstdint>

namespace android_mod {

struct Feature8013Route {
    const char *level_name;
    const std::uint64_t *pickup_ids;
    std::size_t pickup_count;
};

const Feature8013Route *feature8013Routes();
std::size_t feature8013RouteCount();

}
