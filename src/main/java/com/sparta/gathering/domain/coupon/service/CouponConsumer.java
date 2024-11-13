package com.sparta.gathering.domain.coupon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.domain.coupon.dto.CouponRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponConsumer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000) // 1초마다 한 명씩 처리
    public void processCouponQueue() {
        try {
            Object data = redisTemplate.opsForList().rightPop("couponQueue");

            if (data instanceof String) {
                // JSON 문자열을 CouponRequest 객체로 변환
                CouponRequest couponRequest = objectMapper.readValue((String) data, CouponRequest.class);
                UUID userId = couponRequest.getUserId();

                if (userId != null) {
                    // 쿠폰 발급 처리
                    boolean isCouponIssued = issueCoupon(userId);
                    String status = isCouponIssued ? "SUCCESS" : "FAILED";
                    redisTemplate.opsForValue().set("couponIssued:" + userId, status);
                    log.info("couponIssue status: " + status);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean issueCoupon(UUID userId) {
        String couponCode = UUID.randomUUID().toString();
        String couponKey = "userCoupon:" + userId;

        try {
            redisTemplate.opsForHash().put(couponKey, "couponCode", couponCode);
            redisTemplate.opsForHash().put(couponKey, "userId", userId.toString());
            return true;
        } catch (Exception e) {
            log.error("Error issuing coupon for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
}

