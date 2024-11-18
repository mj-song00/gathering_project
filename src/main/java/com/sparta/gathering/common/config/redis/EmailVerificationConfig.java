package com.sparta.gathering.common.config.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
public class EmailVerificationConfig {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String SEND_ATTEMPTS = ":send_attempts";
    private static final String VERIFY_ATTEMPTS = ":verify_attempts";
    private static final String VERIFIED = ":verified";

    // TTL 상수 추가
    private static final long VERIFICATION_CODE_TTL = 5;  // 5분
    private static final long VERIFIED_STATUS_TTL = 10;   // 10분
    private static final long DAILY_ATTEMPTS_TTL = 24;    // 24시간

    // Redis에 인증 코드 저장
    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, VERIFICATION_CODE_TTL, TimeUnit.MINUTES);
    }

    // Redis에서 인증 코드 확인
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return code.equals(storedCode);
    }

    // Redis에서 인증 코드 삭제
    public void deleteCode(String email) {
        redisTemplate.delete(email);
    }

    // 인증 완료 상태 저장
    public void saveVerifiedStatus(String email) {
        redisTemplate.opsForValue().set(email + VERIFIED, "true", VERIFIED_STATUS_TTL, TimeUnit.MINUTES);
    }

    // 인증 완료 상태 확인
    public boolean isCodeVerified(String email) {
        return "true".equals(redisTemplate.opsForValue().get(email + VERIFIED));
    }

    // 일일 시도 횟수 가져오기
    private int getDailyAttempts(String email, String attemptType) {
        String key = generateKey(email, attemptType);
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts == null ? 0 : Integer.parseInt(attempts);
    }

    // 일일 시도 횟수 증가
    private void incrementDailyAttempts(String email, String attemptType) {
        String key = generateKey(email, attemptType);
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, DAILY_ATTEMPTS_TTL, TimeUnit.HOURS);  // 24시간 TTL 설정
    }

    // 일일 시도 횟수 초기화
    private void resetDailyAttempts(String email, String attemptType) {
        String key = generateKey(email, attemptType);
        redisTemplate.delete(key);
    }

    // 발송 시도 횟수 관리 메서드
    public int getDailySendAttempts(String email) {
        return getDailyAttempts(email, SEND_ATTEMPTS);
    }

    public void incrementDailySendAttempts(String email) {
        incrementDailyAttempts(email, SEND_ATTEMPTS);
    }

    public void resetDailySendAttempts(String email) {
        resetDailyAttempts(email, SEND_ATTEMPTS);
    }

    // 인증 시도 횟수 관리 메서드
    public int getDailyVerifyAttempts(String email) {
        return getDailyAttempts(email, VERIFY_ATTEMPTS);
    }

    public void incrementDailyVerifyAttempts(String email) {
        incrementDailyAttempts(email, VERIFY_ATTEMPTS);
    }

    public void resetDailyVerifyAttempts(String email) {
        resetDailyAttempts(email, VERIFY_ATTEMPTS);
    }

    // 키 생성 메서드
    private String generateKey(String email, String attemptType) {
        return email + attemptType;
    }
}