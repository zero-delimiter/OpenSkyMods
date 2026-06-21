#pragma once

#include <map>
#include <string>
#include <utility>
#include <vector>

namespace android_mod {

struct GameApiSession {
    std::string user;
    std::string user_id;
    std::string session;
    std::string rc4_state_hex;
    std::map<std::string, std::string> headers;
};

struct GameApiResult {
    bool ok = false;
    int http_status = 0;
    std::string body;
    std::string error;
};

GameApiSession gameApiSessionFromMemory();
GameApiResult gameApiPostSelector1(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector2(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector3(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector4(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector5(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector6(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector7(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector8(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector9(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector0A(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector0B(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector0C(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector0D(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector0E(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector0F(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector10(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector11(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector12(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector13(const GameApiSession &session, const std::string &plain_body);
GameApiResult gameApiPostSelector14(const GameApiSession &session, const std::string &plain_body);
GameApiResult queryWingLight(const GameApiSession &session);
GameApiResult queryCurrency(const GameApiSession &session);
GameApiResult runDailyQuest(const GameApiSession &session);
GameApiResult sacrificeWingLight(const GameApiSession &session);
GameApiResult dropAllWingBuffs(const GameApiSession &session);
GameApiResult queryWingStats(const GameApiSession &session);
GameApiResult sendHeartFireToFriends(const GameApiSession &session);
GameApiResult collectHeartFire(const GameApiSession &session);
GameApiResult collectHearts(const GameApiSession &session);
GameApiResult claimAncestorCandles(const GameApiSession &session);
GameApiResult setPreferredHomeLevel(int preferred_home_level, const GameApiSession &session);
GameApiResult claimSeasonCandle(const GameApiSession &session);
GameApiResult runFastMapRoute(const GameApiSession &session);
void recordGamePatchId(unsigned int id);

}
