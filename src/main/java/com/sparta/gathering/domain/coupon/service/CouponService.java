package com.sparta.gathering.domain.coupon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.coupon.dto.CouponRequest;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 를 추가하여 JSON 직렬화 사용

    private static final int MAX_COUPON_COUNT = 100;

    @Transactional
    public void requestCoupon(UUID userId) throws JsonProcessingException {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));

        if (!member.getPermission().equals(Permission.MANAGER)) {
            throw new BaseException(ExceptionEnum.MANAGER_NOT_FOUND);
        }

        log.info("쿠폰 요청 유저 : {}", userId);

        // 이미 쿠폰을 발급받았는지 확인
        String issuedStatus = (String) redisTemplate.opsForValue().get("couponIssued:" + userId);

        if ("SUCCESS".equals(issuedStatus) || "REQUEST".equals(issuedStatus)) {
            throw new BaseException(ExceptionEnum.ALREADY_ISSUED_COUPON);
        }

        // 현재 발급된 쿠폰 개수를 가져와 증가
        Long currentCount = redisTemplate.opsForValue().increment("couponCount", 1);

        if (currentCount != null && currentCount <= MAX_COUPON_COUNT) {
            // 현재 발급된 쿠폰이 몇 번째인지 표시하고 요청 대기열에 추가
            String couponRequestJson = objectMapper.writeValueAsString(new CouponRequest(userId));
            redisTemplate.opsForList().leftPush("couponQueue", couponRequestJson);

            // 발급 상태를 "쿠폰 요청"으로 설정
            redisTemplate.opsForValue().set("couponIssued:" + userId, "REQUEST");
            log.info("성공적으로 쿠폰 요청되었습니다. 대기열 : {}", currentCount);

        } else {
            // 남은 쿠폰이 없는 경우 예외 발생
            redisTemplate.opsForValue().decrement("couponCount");
            throw new BaseException(ExceptionEnum.SOLD_OUT_COUPON);
        }
    }


    public String getCouponStatus(AuthenticatedUser authenticatedUser) {
        UUID userId = authenticatedUser.getUserId();


        // Redis 에서 쿠폰 발급 상태 조회
        String issuedStatus = (String) redisTemplate.opsForValue().get("couponIssued:" + userId);

        // 쿠폰이 발급되지 않았고, 대기열에 없는 경우 에러 반환
        if ("ISSUED".equals(issuedStatus)) {
            throw new BaseException(ExceptionEnum.ALREADY_ISSUED_COUPON);
        }

        if (issuedStatus == null) {
            throw new BaseException(ExceptionEnum.NOT_FOUND_COUPON);
        }

        return issuedStatus;

    }


}