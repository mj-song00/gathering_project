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

    // 리프레시 토큰 관련
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "잘못된 리프레시 토큰입니다."),

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
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE", "인증 코드가 일치하지 않습니다."),
    EMAIL_VERIFICATION_REQUIRED(HttpStatus.BAD_REQUEST, "EMAIL_VERIFICATION_REQUIRED", "이메일 인증이 필요합니다."),

    // 약관 관련
    AGREEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "AGREEMENT_NOT_FOUND", "해당 약관을 찾을 수 없습니다."),
    DUPLICATE_AGREEMENT_VERSION(HttpStatus.BAD_REQUEST, "DUPLICATE_AGREEMENT_VERSION", "중복된 약관 버전입니다."),
    SAME_AGREEMENT(HttpStatus.BAD_REQUEST, "SAME_AGREEMENT", "이전 약관과 동일한 내용입니다."),

    // 사용자 약관 관련
    USER_AGREEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_AGREEMENT_NOT_FOUND", "해당 사용자의 약관을 찾을 수 없습니다."),
    LATEST_AGREEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "LATEST_AGREEMENT_NOT_FOUND", "최신 약관에 동의하지 않았습니다."),
    AGREEMENT_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "AGREEMENT_NOT_ACCEPTED", "약관에 동의하지 않았습니다."),
    ALREADY_AGREED(HttpStatus.BAD_REQUEST, "ALREADY_AGREED", "이미 동의한 약관입니다."),

    // 이메일 인증 관련
    EMAIL_SEND_FAILURE(HttpStatus.BAD_REQUEST, "EMAIL_SEND_FAILURE", "이메일 전송에 실패하였습니다."),
    DAILY_SEND_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "DAILY_SEND_LIMIT_EXCEEDED", "일일 최대 이메일 발송 횟수를 초과했습니다."),
    DAILY_VERIFY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "DAILY_VERIFY_LIMIT_EXCEEDED", "일일 최대 인증 시도 횟수를 초과했습니다."),


    // 배치 관련
    BATCH_JOB_EXEC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH_JOB_EXEC_FAILED", "배치 작업 실행에 실패하였습니다."),

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

    // 멤버 관련
    MANAGER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MANAGER_NOT_FOUND", "권한을 확인해주세요"),
    MEMBER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "MEMBER_NOT_FOUND", "멤버를 찾을 수 없습니다."),
    ALREADY_DELETED_MEMBER(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "탈퇴한 게스트입니다."),
    MEMBER_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "MEMBER_NOT_ALLIOWED", "올바른 요청이 아닙니다"),
    DUPLICATE_MEMBER(HttpStatus.BAD_REQUEST,"DUPLICATE_MEMBER","중복된 요청입니다."),

    // 보드 관련
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_NOT_FOUND", "해당 보드를 찾을 수 없습니다."),

    // 댓글 관련
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글 내용을 찾을 수 없습니다."),

    // 카카오 맵 관련
    NOT_JSON_TYPE_STRING(HttpStatus.FORBIDDEN, "NOT_JSON_TYPE_STRING", "json 타입의 문자열이 아닙니다."),
    JSON_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "JSON_TYPE_MISMATCH", "json 타입의 형식이 다릅니다."),

    // 쿠폰 관련
    SOLD_OUT_COUPON(HttpStatus.BAD_REQUEST, "SOLD_OUT_COUPON", "모든 쿠폰이 소진되었습니다."),
    ALREADY_ISSUED_COUPON(HttpStatus.BAD_REQUEST, "ALREADY_ISSUED_COUPON", "이미 발급받은 유저입니다."),
    NOT_FOUND_COUPON(HttpStatus.NOT_FOUND, "NOT_FOUND_COUPON", "쿠폰이 존재하지 않습니다."),


    // 스케쥴 관련
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_NOT_FOUND", "해당 스케쥴을 찾을 수 없습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "토큰이 유효하지 않습니다."),
    SERIALIZE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "SERIALIZE_FAILURE", "토큰 직렬화에 실패하였습니다."),
    DESERIALIZE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "DESERIALIZE_FAILURE", "토큰 역직렬화에 실패하였습니다."),

    // 좋아요 관련
    LIKE_NOT_ALLOWED(HttpStatus.CONFLICT,"LIKE_NOT_ALLOWED","이미 좋아요를 눌렀습니다."),
    NOT_FOUND_AVAILABLE_PORT(HttpStatus.BAD_REQUEST,"PORT_NOT_FOUND","포트를 찾지 못했습니다." ),
    ERROR_EXECUTING_EMBEDDED_REDIS(HttpStatus.BAD_REQUEST,"ERROR_EXECUTING_EMBEDDED_REDIS" ,"포트를 찾을 수 없습니다" ),
    REDIS_SERVER_EXCUTABLE_NOT_FOUND(HttpStatus.BAD_REQUEST,"REDIS_SERVER_EXCUTABLE_NOT_FOUND" , "서버를 찾을 수 없습니다."),

    // 채팅 관련
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND", "해당 채팅방을 찾을 수 없습니다."),

    //image 관련
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND,"IMAGE_NOT_FOUND","이미지를 찾을 수 없습니다." );

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    ExceptionEnum(HttpStatus status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

}
