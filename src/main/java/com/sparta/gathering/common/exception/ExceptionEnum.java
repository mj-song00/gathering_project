package com.sparta.gathering.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {

    // 공통
    NOT_INSTANTIABLE_CLASS(HttpStatus.INTERNAL_SERVER_ERROR, "NOT_INSTANTIABLE_CLASS",
            "인스턴스화할 수 없습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "DATA_INTEGRITY_VIOLATION",
            "데이터 처리 중 문제가 발생했습니다. 요청을 확인하고 다시 시도해주세요"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
            "서버에서 문제가 발생하였습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "요청 값이 올바르지 않습니다."),
    UNAUTHORIZED_ACTION(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACTION", "권한이 없습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "INVALID_USER_ID", "유저 ID가 올바르지 않습니다."),
    API_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "API_TIMEOUT", "API 요청 시간이 초과되었습니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_USER", "인증되지 않은 사용자입니다."),

    // 토큰 관련
    JWT_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT_TOKEN_NOT_FOUND", "JWT 토큰이 존재하지 않습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "INVALID_JWT_SIGNATURE", "잘못된 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_JWT_TOKEN", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "UNSUPPORTED_JWT_TOKEN", "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_JWT_TOKEN", "잘못된 JWT 토큰입니다."),
    MALFORMED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "MALFORMED_TOKEN", "JWT 토큰 형식이 잘못되었습니다."),

    // 유저 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "ALREADY_DELETED", "탈퇴된 사용자입니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다."),
    EMAIL_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "EMAIL_PASSWORD_MISMATCH",
            "이메일 혹은 비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMAIL_NOT_FOUND", "이메일을 찾을 수 없습니다."),
    KAKAO_DATA_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "KAKAO_DATA_PROCESSING_ERROR",
            "카카오 데이터 처리 중 문제가 발생했습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "PASSWORD_SAME_AS_OLD", "새 비밀번호가 기존 비밀번호와 동일합니다."),
    NICKNAME_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "NICKNAME_SAME_AS_OLD", "새 닉네임이 기존 닉네임과 동일합니다."),

    // 이미지 파일 등록 관련
    PERMISSION_DENIED(HttpStatus.BAD_REQUEST, "PERMISSION_DENIED", "사용자 ID와 일치하지 않는 파일입니다."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "FILE_NOT_FOUND", "해당 파일이 존재하지 않습니다."),
    DELETE_FAILED(HttpStatus.BAD_REQUEST, "DELETE_FAILED", "파일 삭제 실패되었습니다."),
    INVALID_FILE_SIZE(HttpStatus.BAD_REQUEST, "INVALID_FILE_SIZE", "파일 크기가 5MB를 초과합니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "INVALID_FILE_TYPE", "지원하지 않는 파일 형식입니다."),
    PERMISSION_DENIED_ROLE(HttpStatus.BAD_REQUEST, "PERMISSION_DENIED_ROLE", "권한이 없습니다."),
    UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "UPLOAD_FAILED", "S3에 파일 업로드 실패되었습니다. "),
    PROFILE_IMAGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PROFILE_IMAGE_ALREADY_EXISTS",
            "이미 존재하는 이미지입니다."),

    // 카테고리 관련
    NOT_ADMIN_ROLE(HttpStatus.BAD_REQUEST, "NOT_ADMIN_ROLE", "관리자만 이용할 수 있습니다."),
    NOT_FOUNT_CATEGORY(HttpStatus.BAD_REQUEST, "NOT_FOUNT_CATEGORY", "존재하지 않는 카테고리입니다."),
    ALREADY_HAVE_CATEGORY(HttpStatus.BAD_REQUEST, "ALREADY_HAVE_TITLE", "이미 존재하는 카테고리입니다."),

    // 해시태그 관련
    ALREADY_HAVE_HASHTAG(HttpStatus.BAD_REQUEST, "ALREADY_HAVE_TITLE", "이미 존재하는 해시태그입니다."),
    NOT_FOUNT_HASHTAG(HttpStatus.BAD_REQUEST, "NOT_FOUNT_CATEGORY", "존재하지 않는 해시태그입니다."),

    // 게더 관련
    GATHER_NOT_FOUND(HttpStatus.NOT_FOUND, "GATHER_NOT_FOUND", "해당 모임을 찾을 수 없습니다."),

    //멤버 관련
    MANAGER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MANAGER_NOT_FOUND", "권한을 확인해주세요"),
    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MEMBER_NOT_FOUND", "멤버를 찾을 수 없습니다."),
    ALREADY_DELETED_MEMBER(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "탈퇴한 게스트입니다."),
    MEMBER_NOT_ALLOWED(HttpStatus.BAD_REQUEST,"MEMBER_NOT_ALLIOWED","올바른 요청이 아닙니다" ),

    // 보드 관련
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_NOT_FOUND", "해당 보드를 찾을 수 없습니다."),

    //댓글 관련
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글 내용을 찾을 수 없습니다."),
    //카카오 맵 관련
    NOT_JSON_TYPE_STRING(HttpStatus.FORBIDDEN, "NOT_JSON_TYPE_STRING", "json 타입의 문자열이 아닙니다."),
    JSON_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "JSON_TYPE_MISMATCH", "json 타입의 형식이 다릅니다."),

    // 스케쥴 관련
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_NOT_FOUND", "해당 스케쥴을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    ExceptionEnum(HttpStatus status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

}
