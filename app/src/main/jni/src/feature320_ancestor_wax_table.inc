struct Feature320AncestorWaxRecord {
    std::uint32_t patch_id;
    int payload_count;
    const char *payloads[4];
};

// Original xmmword_37CE10 runtime table, initialized by sub_14B9DC.
constexpr Feature320AncestorWaxRecord kFeature320AncestorWaxRecords[] = {
    {0x62507247u, 3, {"dawn_nothanks", "dawn_come", "dawn_point", nullptr}},
    {0x93A94B82u, 1, {"prairie_butterfly", nullptr, nullptr, nullptr}},
    {0x8C1A4650u, 4, {"prairie_thumbsup", "prairie_wave", "prairie_laugh", "prairie_yawn"}},
    {0xC169BA0Du, 1, {"prairie_wipe", nullptr, nullptr, nullptr}},
    {0x1298D15Du, 1, {"prairie_bird", nullptr, nullptr, nullptr}},
    {0x09D001F3u, 1, {"rain_cold", nullptr, nullptr, nullptr}},
    {0xA940A36Eu, 3, {"rain_shy", "rain_seek", "rain_pout", nullptr}},
    {0x80B98897u, 4, {"rain_whale", "rain_cry", "rain_sorry", "rain_ohno"}},
    {0x61A20627u, 1, {"sunset_strong", nullptr, nullptr, nullptr}},
    {0x81E94770u, 2, {"sunset_handstand", "sunset_manta", nullptr, nullptr}},
    {0x0FB5EBD3u, 4, {"sunset_backflip", "sunset_cheer", "sunset_proud", "sunset_bow"}},
    {0x44655688u, 1, {"dusk_scared", nullptr, nullptr, nullptr}},
    {0x338632B5u, 3, {"dusk_sneaky", "dusk_brave", "dusk_die", nullptr}},
    {0x9E01DB8Du, 1, {"dusk_salute", nullptr, nullptr, nullptr}},
    {0x5F319852u, 1, {"dusk_lookaround", nullptr, nullptr, nullptr}},
    {0x8C9A1101u, 4, {"night_pray", "night_love", "night_force", "night_float"}},
    {0x89891349u, 1, {"night_ghost", nullptr, nullptr, nullptr}},
};
