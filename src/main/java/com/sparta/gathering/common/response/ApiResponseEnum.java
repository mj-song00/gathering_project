package com.sparta.gathering.common.response;

import lombok.Getter;

@Getter
public enum ApiResponseEnum {

    // 유저 관련
    SIGNUP_SUCCESS("회원가입이 완료되었습니다. 로그인 화면으로 이동합니다."),
    USER_DELETED_SUCCESS("회원 탈퇴가 완료되었습니다.");

    private final String message;

    ApiResponseEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
