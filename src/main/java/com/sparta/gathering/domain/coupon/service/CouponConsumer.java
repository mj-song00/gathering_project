package com.sparta.gathering.domain.coupon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.domain.coupon.dto.CouponRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponConsumer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService = Executors.newFixedThreadPool(100); // 100개 스레드로 병렬 처리

    private static final int BATCH_SIZE = 2000; // 한 번에 처리할 요청 수
    private static final int MAX_THREADS = 100; // 최대 스레드 개수

    @Scheduled(fixedDelay = 500) // 0.5초마다 작업 실행
    public void processCouponQueue() {
        String lockKey = "couponQueueLock"; // 락을 위한 키
        String lockValue = UUID.randomUUID().toString(); // 락 소유자 식별값

        try {
            // 락 획득 (5초 유효)
            Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(lockAcquired)) {
                for (int i = 0; i < MAX_THREADS; i++) { // 20개 스레드로 병렬 처리
                    executorService.submit(() -> {
                        try {
                            List<Object> batch = redisTemplate.opsForList().rightPop("couponQueue", BATCH_SIZE);

                            if (batch != null && !batch.isEmpty()) {
                                for (Object data : batch) {
                                    if (data instanceof String) {
                                        processCouponRequest((String) data);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("쿠폰 큐를 처리하는 중 오류가 발생했습니다: {}", e.getMessage());
                        }
                    });
                }
            } else {
                log.info("다른 인스턴스가 큐를 처리 중입니다. 이번 사이클은 건너뜁니다.");
            }
        } finally {
            // 락 소유자가 현재 작업일 경우 락 해제
            if (lockValue.equals(redisTemplate.opsForValue().get(lockKey))) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    private void processCouponRequest(String data) {
        try {
            // JSON 문자열을 CouponRequest 객체로 변환
            CouponRequest couponRequest = objectMapper.readValue(data, CouponRequest.class);
            UUID userId = couponRequest.getUserId();

            if (userId != null) {
                // 쿠폰 발급 처리
                boolean isCouponIssued = issueCoupon(userId);
                String status = isCouponIssued ? "SUCCESS" : "FAILED";
                redisTemplate.opsForValue().set("couponIssued:" + userId, status);
                redisTemplate.expire("couponIssued:" + userId, 86400, TimeUnit.SECONDS); // TTL 설정 (24시간)
                log.info("쿠폰 처리 중인 유저의 상태 {}: {}", userId, status);
            }
        } catch (Exception e) {
            log.error("쿠폰 요청 중 오류가 발생했습니다: {}", e.getMessage());
        }
    }

    private boolean issueCoupon(UUID userId) {
        String couponCode = UUID.randomUUID().toString();
        String couponKey = "userCoupon:" + userId;

        String couponStockKey = "couponStock";  // 쿠폰 재고를 관리하는 Redis 키

        try {
            // 쿠폰 재고 초기화
            if (redisTemplate.opsForValue().get(couponStockKey) == null) {
                redisTemplate.opsForValue().set(couponStockKey, "10000");
            }

            // 쿠폰 재고를 감소시키기 위해 DECR 명령어 사용
            Long remainingStock = redisTemplate.opsForValue().decrement(couponStockKey);

            log.info("쿠폰 재고: {} / 남은 재고: {}", redisTemplate.opsForValue().get(couponStockKey), remainingStock);

            // 만약 remainingStock 가 null 이거나 0 미만이라면 0으로 설정
            if (remainingStock == null || remainingStock < 0) {
                redisTemplate.opsForValue().increment(couponStockKey); // 재고를 되돌림
                log.warn("쿠폰 발급 실패: 재고 부족 (userId: {})", userId);
                return false;
            }

            // 쿠폰 정보를 사용자 해시에 저장
            redisTemplate.opsForHash().put(couponKey, "couponCode", couponCode);
            redisTemplate.opsForHash().put(couponKey, "userId", userId.toString());

            // TTL 설정: 24시간 후 자동 만료
            redisTemplate.expire(couponKey, 86400, TimeUnit.SECONDS); // TTL 설정 (24시간)

            return true;
        } catch (Exception e) {
            log.error("쿠폰요청 중 오류가 발생했습니다. {}: {}", userId, e.getMessage());
            return false;
        }
    }


}
