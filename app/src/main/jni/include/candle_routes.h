#pragma once

#include <cstddef>
#include <cstdint>

namespace android_mod {

struct CandleRoutePoint {
    float x;
    float y;
    float z;
};

struct CandleRoute {
    std::uint32_t level_id;
    std::size_t point_offset;
    std::size_t point_count;
};

const CandleRoute *findCandleRouteByLevelId(std::uint32_t level_id);
const CandleRoutePoint *candleRoutePoints(const CandleRoute &route);
std::size_t candleRouteCount();
std::size_t candleRoutePointCount();

}
