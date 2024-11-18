package com.sparta.gathering.domain.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.coupon.service.CouponService;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    @DisplayName("쿠폰 요청 성공")
    void test1() throws JsonProcessingException {
        // Given
        UUID userId = UUID.randomUUID();
        when(memberRepository.findPermissionByUserId(userId)).thenReturn(Permission.MANAGER);
        when(valueOperations.get("couponIssued:" + userId)).thenReturn(null);
        when(valueOperations.increment("couponCount", 1)).thenReturn(1L);

        // When
        couponService.requestCoupon(userId);

        // Then
        verify(listOperations).leftPush(eq("couponQueue"), anyString());
        verify(valueOperations).set("couponIssued:" + userId, "REQUEST");
    }

    @Test
    @DisplayName("쿠폰 요청 중복 예외처리")
    void test2() {
        // Given
        UUID userId = UUID.randomUUID();
        when(memberRepository.findPermissionByUserId(userId)).thenReturn(Permission.MANAGER);
        when(valueOperations.get("couponIssued:" + userId)).thenReturn("SUCCESS");

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> couponService.requestCoupon(userId));
        assertEquals(ExceptionEnum.ALREADY_ISSUED_COUPON, exception.getExceptionEnum());
    }

    @Test
    @DisplayName("쿠폰 요청 소진 예외 처리")
    void test3() {
        // Given
        UUID userId = UUID.randomUUID();
        when(memberRepository.findPermissionByUserId(userId)).thenReturn(Permission.MANAGER);
        when(valueOperations.get("couponIssued:" + userId)).thenReturn(null);
        when(valueOperations.increment("couponCount", 1)).thenReturn(101L);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> couponService.requestCoupon(userId));
        assertEquals(ExceptionEnum.SOLD_OUT_COUPON, exception.getExceptionEnum());
        verify(valueOperations).decrement("couponCount");
    }

//    @Test
//    @DisplayName("쿠폰 조회 성공")
//    void test4() {
//        // Given
//        UUID userId = UUID.randomUUID();
//        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//        when(authenticatedUser.getUserId()).thenReturn(userId);
//        when(valueOperations.get("couponIssued:" + userId)).thenReturn("SUCCESS");
//        when(redisTemplate.opsForHash().get("userCoupon:" + userId, "couponCode")).thenReturn("COUPON123");
//
//        // When
//        String status = couponService.getCouponStatus(authenticatedUser);
//
//        // Then
//        assertEquals("상태 : SUCCESS / 쿠폰 번호: COUPON123", status);
//    }

    @Test
    @DisplayName("쿠폰 없음 예외처리")
    void test5() {
        // Given
        UUID userId = UUID.randomUUID();
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        when(authenticatedUser.getUserId()).thenReturn(userId);
        when(valueOperations.get("couponIssued:" + userId)).thenReturn(null);

        // When & Then
        BaseException exception = assertThrows(BaseException.class,
                () -> couponService.getCouponStatus(authenticatedUser));
        assertEquals(ExceptionEnum.NOT_FOUND_COUPON, exception.getExceptionEnum());
    }
//
//    @Test
//    @DisplayName("쿠폰 번호 없음 예외처리")
//    void test6() {
//        // Given
//        UUID userId = UUID.randomUUID();
//        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//        when(authenticatedUser.getUserId()).thenReturn(userId);
//        when(valueOperations.get("couponIssued:" + userId)).thenReturn("SUCCESS");
//        when(redisTemplate.opsForHash().get("userCoupon:" + userId, "couponCode")).thenReturn(null);
//
//        // When
//        String status = couponService.getCouponStatus(authenticatedUser);
//
//        // Then
//        assertEquals("상태 : SUCCESS / 쿠폰 번호: 쿠폰 번호 없음", status);
//    }
}