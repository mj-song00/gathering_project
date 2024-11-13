package com.sparta.gathering.domain.coupon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Coupon", description = "쿠폰 API")
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @Operation(summary = "쿠폰 요청", description = "100건 까지 대기열 후 1초마다 1개씩 승인됩니다.")
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<?>> requestCoupon(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws JsonProcessingException {
        UUID userId = authenticatedUser.getUserId();
        couponService.requestCoupon(userId);
        return ResponseEntity.ok(ApiResponse.successWithOutData(ApiResponseEnum.SUCCESS_COUPON));
    }

    @Operation(summary = "쿠폰 상태 조회", description = "쿠폰의 상태가 REQUEST,SUCCESS,FAILED 중 상태로 확인가능합니다.")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getCouponStatus(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        String responseMessage = couponService.getCouponStatus(authenticatedUser);
        return ResponseEntity.ok(ApiResponse.successWithData(responseMessage, ApiResponseEnum.SUCCESS_GET_COUPON));
    }
}