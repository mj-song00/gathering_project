package com.sparta.gathering.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KakaoAttributes {
    KAKAO_ACCOUNT("kakao_account"),
    PROFILE("profile"),
    EMAIL("email"),
    NICKNAME("nickname"),
    PROFILE_IMAGE_URL("profile_image_url"),
    DEFAULT_NICKNAME("카카오 사용자");

    private final String attribute;

}