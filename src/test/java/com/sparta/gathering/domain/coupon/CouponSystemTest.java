package com.sparta.gathering.domain.coupon;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.coupon.service.CouponService;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponSystemTest {

    @InjectMocks
    private CouponService couponService;

    @Autowired
    private TestRestTemplate restTemplate;

    private AuthenticatedUser authenticatedUser;
    private User testUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = new AuthenticatedUser(UUID.randomUUID(), "test@example.com", null);
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("password123A!")
                .nickName("nickname")
                .userRole(UserRole.ROLE_ADMIN)
                .identityProvider(IdentityProvider.NONE)
                .profileImage(null)
                .build();
    }

    @Test
    @DisplayName("쿠폰 요청 성공")
    public void test1() {
        ResponseEntity<String> response = restTemplate.postForEntity("api/coupons/request", null, String.class);
        assertThat(response.getBody()).contains("Your request is accepted");
    }

    @Test
    @DisplayName("쿠폰 조회 성공")
    public void test2() {
        // 쿠폰 요청 후 상태 확인
        restTemplate.postForEntity("/api/coupons/request", null, String.class);
        ResponseEntity<String> response = restTemplate.getForEntity("/api/coupons/status", String.class);
        assertThat(response.getBody()).contains("Coupon status for user user2");
    }
}
