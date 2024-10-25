package com.sparta.gathering.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {

    // 공통
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "DATA_INTEGRITY_VIOLATION", "데이터 처리 중 문제가 발생했습니다. 요청을 확인하고 다시 시도해주세요"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에서 문제가 발생하였습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "요청 값이 올바르지 않습니다."),
    UNAUTHORIZED_ACTION(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACTION", "권한이 없습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "INVALID_USER_ID", "유저 ID가 올바르지 않습니다."),

    // 토큰 관련
    JWT_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT_TOKEN_NOT_FOUND", "JWT 토큰이 존재하지 않습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "INVALID_JWT_SIGNATURE", "잘못된 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_JWT_TOKEN", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "UNSUPPORTED_JWT_TOKEN", "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_JWT_TOKEN", "잘못된 JWT 토큰입니다."),
    MALFORMED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "MALFORMED_TOKEN", "JWT 토큰 형식이 잘못되었습니다."),

    // 유저 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "ALREADY_DELETED", "이미 탈퇴된 사용자입니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다."),
    EMAIL_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "EMAIL_PASSWORD_MISMATCH", "이메일 혹은 비밀번호가 일치하지 않습니다."),

    // 카테고리 관련
    NOT_ADMIN_ROLE(HttpStatus.BAD_REQUEST,"NOT_ADMIN_ROLE","관리자만 이용할 수 있습니다."),
    NOT_FOUNT_CATEGORY(HttpStatus.BAD_REQUEST,"NOT_FOUNT_CATEGORY","존재하지 않는 카테고리입니다."),
    ALREADY_HAVE_CATEGORY(HttpStatus.BAD_REQUEST,"ALREADY_HAVE_TITLE","이미 존재하는 카테고리입니다."),

    // 해시태그 관련
    ALREADY_HAVE_HASHTAG(HttpStatus.BAD_REQUEST,"ALREADY_HAVE_TITLE","이미 존재하는 해시태그입니다."),
    NOT_FOUNT_HASHTAG(HttpStatus.BAD_REQUEST,"NOT_FOUNT_CATEGORY","존재하지 않는 해시태그입니다."),

    // 게더 관련
    GATHER_NOT_FOUND(HttpStatus.NOT_FOUND,"GATHER_NOT_FOUND","해당 모임을 찾을 수 없습니다."),

    //멤버 관련
    MANAGER_NOT_FOUND(HttpStatus.UNAUTHORIZED,"MANAGER_NOT_FOUND","권한을 확인해주세요"),
    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED,"MEMBER_NOT_FOUND","멤버를 찾을 수 없습니다."),
    ALREADY_DELETED_MEMBER(HttpStatus.NOT_FOUND,"MEMBER_NOT_FOUND", "탈퇴한 게스트입니다."),

    //댓글 관련
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"COMMENT_NOT_FOUND","댓글 내용을 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,"SCHEDULE_NOT_FOUND","일정 내용을 찾을 수 없습니다.");
    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    ExceptionEnum(HttpStatus status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

}
