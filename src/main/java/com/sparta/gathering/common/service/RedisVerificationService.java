package com.sparta.gathering.common.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisVerificationService {

    private final RedisTemplate<String, String> redisTemplate;

    // Redis에 인증 코드 저장
    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
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

    // Redis에서 인증 완료된 이메일 상태 저장 (10분 유효)
    public void saveVerifiedStatus(String email) {
        redisTemplate.opsForValue().set(email + ":verified", "true", 10, TimeUnit.MINUTES);
    }

    // 인증 완료 상태 확인
    public boolean isCodeVerified(String email) {
        return "true".equals(redisTemplate.opsForValue().get(email + ":verified"));
    }

}
