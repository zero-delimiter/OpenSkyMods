struct Feature361HangoutRecord {
    std::int32_t level_id;
    const char *event_payload;
    const char *social_payload;
    const char *display_name;
    int count;
};

constexpr Feature361HangoutRecord kFeature361HangoutRecords[] = {
    {-1574275404, ".RainGrandmaTable", "social_rain_grandmatable", u8"秘密花园", 1000},
    {-1050035699, ".PrairieCozyCave", "social_prairie_cozycave", u8"幽光山洞", 250},
};
