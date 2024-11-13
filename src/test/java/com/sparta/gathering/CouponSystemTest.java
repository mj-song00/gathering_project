package com.sparta.gathering;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponSystemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testRequestCoupon() {
        ResponseEntity<String> response = restTemplate.postForEntity("/request-coupon?userId=user1", null, String.class);
        assertThat(response.getBody()).contains("Your request is accepted");
    }

    @Test
    public void testCouponStatus() {
        // 쿠폰 요청 후 상태 확인
        restTemplate.postForEntity("/request-coupon?userId=user2", null, String.class);
        ResponseEntity<String> response = restTemplate.getForEntity("/coupon-status?userId=user2", String.class);
        assertThat(response.getBody()).contains("Coupon status for user user2");
    }
}
