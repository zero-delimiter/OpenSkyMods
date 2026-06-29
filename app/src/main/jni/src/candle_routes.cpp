#include "include/candle_routes.h"

namespace android_mod {
namespace {

#include "candle_routes_data.inc"
#include "candle_routes_lightwing_data.inc"

const CandleRoute *routeTable(CandleRouteTable table) {
    return table == CandleRouteTable::WithLightwing ? kCandleRoutes_with_lightwing : kCandleRoutes;
}

std::size_t routeTableCount(CandleRouteTable table) {
    return table == CandleRouteTable::WithLightwing
        ? sizeof(kCandleRoutes_with_lightwing) / sizeof(kCandleRoutes_with_lightwing[0])
        : sizeof(kCandleRoutes) / sizeof(kCandleRoutes[0]);
}

const CandleRoutePoint *pointTable(CandleRouteTable table) {
    return table == CandleRouteTable::WithLightwing ? kCandleRoutePoints_with_lightwing : kCandleRoutePoints;
}

std::size_t pointTableCount(CandleRouteTable table) {
    return table == CandleRouteTable::WithLightwing
        ? sizeof(kCandleRoutePoints_with_lightwing) / sizeof(kCandleRoutePoints_with_lightwing[0])
        : sizeof(kCandleRoutePoints) / sizeof(kCandleRoutePoints[0]);
}

} // namespace

const CandleRoute *findCandleRouteByLevelId(std::uint32_t level_id, CandleRouteTable table) {
    const CandleRoute *routes = routeTable(table);
    const std::size_t count = routeTableCount(table);
    for (std::size_t i = 0; i < count; ++i) {
        const CandleRoute &route = routes[i];
        if (route.level_id == level_id) {
            return &route;
        }
    }
    return nullptr;
}

const CandleRoutePoint *candleRoutePoints(const CandleRoute &route, CandleRouteTable table) {
    if (route.point_offset >= candleRoutePointCount(table)) {
        return nullptr;
    }
    return pointTable(table) + route.point_offset;
}

std::size_t candleRouteCount(CandleRouteTable table) {
    return routeTableCount(table);
}

std::size_t candleRoutePointCount(CandleRouteTable table) {
    return pointTableCount(table);
}

}
