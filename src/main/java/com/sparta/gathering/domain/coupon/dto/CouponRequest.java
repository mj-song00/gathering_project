package com.sparta.gathering.domain.coupon.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자를 추가하여 Jackson이 역직렬화할 수 있도록 합니다.
public class CouponRequest implements Serializable {
    private UUID userId;

    public CouponRequest(UUID userId) {
        this.userId = userId;
    }
}

