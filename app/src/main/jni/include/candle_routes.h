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

enum class CandleRouteTable {
    NoLightwing,
    WithLightwing,
};

const CandleRoute *findCandleRouteByLevelId(
    std::uint32_t level_id,
    CandleRouteTable table = CandleRouteTable::NoLightwing);
const CandleRoutePoint *candleRoutePoints(
    const CandleRoute &route,
    CandleRouteTable table = CandleRouteTable::NoLightwing);
std::size_t candleRouteCount(CandleRouteTable table = CandleRouteTable::NoLightwing);
std::size_t candleRoutePointCount(CandleRouteTable table = CandleRouteTable::NoLightwing);

}
