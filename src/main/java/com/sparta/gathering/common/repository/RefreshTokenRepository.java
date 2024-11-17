package com.sparta.gathering.common.repository;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 리프레시 토큰을 블랙리스트에 추가합니다.
     *
     * @param refreshToken 블랙리스트에 추가할 리프레시 토큰
     * @param expiration   토큰의 남은 유효 시간 (밀리초)
     */
    public void addToBlacklist(String refreshToken, long expiration) {
        redisTemplate.opsForValue().set("blacklist:" + refreshToken, "true", expiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 리프레시 토큰이 블랙리스트에 있는지 확인합니다.
     *
     * @param refreshToken 확인할 리프레시 토큰
     * @return 블랙리스트에 포함되어 있으면 true, 아니면 false
     */
    public boolean isBlacklisted(String refreshToken) {
        return redisTemplate.hasKey("blacklist:" + refreshToken);
    }

    /**
     * 블랙리스트에 포함된 리프레시 토큰을 삭제합니다.
     *
     * @param refreshToken 삭제할 리프레시 토큰
     */
    public void removeFromBlacklist(String refreshToken) {
        redisTemplate.delete("blacklist:" + refreshToken);
    }
}
