package com.sparta.gathering.domain.coupon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.domain.coupon.dto.CouponRequest;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponConsumer {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient; // Redisson 클라이언트 주입
    private final ExecutorService executorService = new ThreadPoolExecutor(
            10, // 최소 스레드 수
            100, // 최대 스레드 수
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(500), // 큐 크기 제한
            new ThreadPoolExecutor.CallerRunsPolicy() // 큐가 꽉 찰 경우 호출자 스레드에서 실행
    );

    private static final int BATCH_SIZE = 2000; // 한 번에 처리할 요청 수
    private static final int MAX_THREADS = 100; // 최대 스레드 개수

    @Scheduled(fixedDelay = 500) // 0.5초마다 작업 실행
    public void processCouponQueue() throws InterruptedException {
        RLock lock = redissonClient.getLock("couponQueueLock"); // 락 생성

        if (!lock.tryLock(10, TimeUnit.SECONDS)) {
            log.info("다른 인스턴스가 락을 보유 중입니다. 이번 작업은 건너뜁니다.");
            return;
        }

        try {
            for (int i = 0; i < MAX_THREADS; i++) {
                executorService.submit(() -> {
                    try {
                        List<Object> batch = redisTemplate.opsForList().rightPop("couponQueue", BATCH_SIZE);

                        if (batch != null && !batch.isEmpty()) {
                            for (Object data : batch) {
                                try {
                                    if (data instanceof String) {
                                        processCouponRequest((String) data);
                                    }
                                } catch (Exception e) {
                                    log.error("쿠폰 요청 처리 실패: {}", e.getMessage());
                                    redisTemplate.opsForList().leftPush("failureQueue", data); // 실패 큐에 저장
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("쿠폰 큐를 처리하는 중 오류가 발생했습니다: {}", e.getMessage());
                    }
                });
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void processCouponRequest(String data) {
        try {
            // JSON 문자열을 CouponRequest 객체로 변환
            CouponRequest couponRequest = objectMapper.readValue(data, CouponRequest.class);
            UUID userId = couponRequest.getUserId();

            if (userId != null) {
                boolean isCouponIssued = issueCoupon(userId);
                String status = isCouponIssued ? "SUCCESS" : "FAILED";
                redisTemplate.opsForValue().set("couponIssued:" + userId, status, 86400, TimeUnit.SECONDS);

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
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(couponStockKey, "10000"))) {
                log.info("쿠폰 재고 초기화 완료");
            }

            // 쿠폰 재고를 감소시키기 위해 DECR 명령어 사용
            Long remainingStock = redisTemplate.opsForValue().decrement(couponStockKey);

            log.info("쿠폰 재고: {} / 남은 재고: {}", redisTemplate.opsForValue().get(couponStockKey), remainingStock);

            if (remainingStock == null || remainingStock < 0) {
                redisTemplate.opsForValue().increment(couponStockKey); // 재고를 되돌림
                log.warn("쿠폰 발급 실패: 재고 부족 (userId: {})", userId);
                return false;
            }

            redisTemplate.opsForHash().put(couponKey, "couponCode", couponCode);
            redisTemplate.opsForHash().put(couponKey, "userId", userId.toString());

            redisTemplate.expire(couponKey, 86400, TimeUnit.SECONDS);

            return true;
        } catch (Exception e) {
            log.error("쿠폰 요청 중 오류가 발생했습니다. {}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Scheduled(fixedDelay = 10000) // 10초마다 실패 데이터 재처리
    public void processFailureQueue() {
        List<Object> failures = redisTemplate.opsForList().range("failureQueue", 0, -1);
        if (failures != null) {
            for (Object failure : failures) {
                try {
                    processCouponRequest((String) failure);
                    redisTemplate.opsForList().remove("failureQueue", 1, failure); // 처리 후 삭제
                } catch (Exception e) {
                    log.error("실패 데이터 재처리 중 오류 발생: {}", e.getMessage());
                }
            }
        }
    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
