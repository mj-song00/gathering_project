package com.sparta.gathering.common.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReagreeOneTimeTokenService {

    // 30일 TTL 설정
    private static final long TOKEN_EXPIRATION_TIME = 30L * 24 * 60 * 60; // 초 단위 (30일)
    private static final String REDIS_KEY_PREFIX = "user-agreement:token:"; // 네임스페이스
    private final StringRedisTemplate redisTemplate;

    // Redis 키 생성
    private String generateRedisKey(String token) {
        return REDIS_KEY_PREFIX + token;
    }

    // 토큰 생성
    public String generateToken(UUID userId, UUID agreementId) {
        String token = UUID.randomUUID().toString(); // 랜덤 토큰 생성
        String redisKey = generateRedisKey(token);  // 네임스페이스를 포함한 Redis 키 생성
        String value = userId + ":" + agreementId; // 토큰 데이터

        // Redis에 저장 및 TTL 설정
        redisTemplate.opsForValue().set(redisKey, value, TOKEN_EXPIRATION_TIME, TimeUnit.SECONDS);
        return token;
    }

    // 토큰 검증 및 사용
    public TokenData validateAndUseToken(String token) {
        String redisKey = generateRedisKey(token); // 네임스페이스 포함 Redis 키 생성
        String value = redisTemplate.opsForValue().get(redisKey); // Redis에서 데이터 조회

        if (value == null) {
            throw new BaseException(ExceptionEnum.INVALID_TOKEN);
        }

        // 사용 후 삭제
        redisTemplate.delete(redisKey);

        // 데이터 파싱
        String[] parts = value.split(":");
        return new TokenData(UUID.fromString(parts[0]), UUID.fromString(parts[1]));
    }

    @Data
    @AllArgsConstructor
    public static class TokenData {

        private UUID userId;
        private UUID agreementId;
    }

    /* TTL 확인 현재 사용하지 않음, 추후 필요 시 사용, 관리자 API에서 사용자 토큰의 남은 유효 시간을 확인...등등
    public long getTokenTTL(String token) {
        String redisKey = generateRedisKey(token);
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS); // TTL 조회

        if (ttl == null || ttl < 0) {
            throw new BaseException(ExceptionEnum.INVALID_TOKEN);
        }

        log.info("Token [{}] with key [{}] has TTL [{}] seconds remaining", token, redisKey, ttl);
        return ttl;
    }*/

}
