#include "include/candle_routes.h"

namespace android_mod {
namespace {

#include "candle_routes_data.inc"

} // namespace

const CandleRoute *findCandleRouteByLevelId(std::uint32_t level_id) {
    for (const CandleRoute &route : kCandleRoutes) {
        if (route.level_id == level_id) {
            return &route;
        }
    }
    return nullptr;
}

const CandleRoutePoint *candleRoutePoints(const CandleRoute &route) {
    if (route.point_offset >= candleRoutePointCount()) {
        return nullptr;
    }
    return kCandleRoutePoints + route.point_offset;
}

std::size_t candleRouteCount() {
    return sizeof(kCandleRoutes) / sizeof(kCandleRoutes[0]);
}

std::size_t candleRoutePointCount() {
    return sizeof(kCandleRoutePoints) / sizeof(kCandleRoutePoints[0]);
}

}
