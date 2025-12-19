package com.yourcode.mirae.speedrun.redis;

public final class RedisKeyUtil {

    private RedisKeyUtil() {
    }

    private static final String PREFIX = "speed:";

    // Session keys
    public static String sessionKey(String sessionId) {
        return PREFIX + "session:" + sessionId;
    }

    public static String sessionPlayersKey(String sessionId) {
        return PREFIX + "session:" + sessionId + ":players";
    }

    public static String sessionUserKey(String sessionId, Long userId) {
        return PREFIX + "session:" + sessionId + ":user:" + userId;
    }

    public static String sessionProblemsKey(String sessionId) {
        return PREFIX + "session:" + sessionId + ":problems";
    }

    public static String sessionClaimsKey(String sessionId, Long userId) {
        return PREFIX + "session:" + sessionId + ":claims:" + userId;
    }

    // Leaderboard keys
    public static String sessionLeaderboardKey(String sessionId) {
        return PREFIX + "lb:" + sessionId;
    }

    // Global ranking keys
    public static String globalRankingKey() {
        return PREFIX + "global:ranking";
    }

    public static String weeklyRankingKey() {
        return PREFIX + "global:ranking:weekly";
    }

    public static String monthlyRankingKey() {
        return PREFIX + "global:ranking:monthly";
    }

    // User penalty key
    public static String userPenaltyKey(Long userId) {
        return "user:penalty:" + userId;
    }

    // Session TTL in seconds (24 hours)
    public static final long SESSION_TTL_SECONDS = 24 * 60 * 60;
}
