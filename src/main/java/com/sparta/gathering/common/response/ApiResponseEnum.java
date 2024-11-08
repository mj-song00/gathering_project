package com.sparta.gathering.common.response;

import lombok.Getter;

@Getter
public enum ApiResponseEnum {

    // 유저 관련
    SIGNUP_SUCCESS("회원가입이 완료되었습니다."),
    USER_DELETED_SUCCESS("회원 탈퇴가 완료되었습니다."),
    PROFILE_RETRIEVED_SUCCESS("프로필 조회가 성공적으로 완료되었습니다."),
    PASSWORD_CHANGED_SUCCESS("비밀번호 변경이 성공적으로 완료되었습니다."),
    NICKNAME_CHANGED_SUCCESS("닉네임 변경이 성공적으로 완료되었습니다."),
    EMAIL_VERIFICATION_CODE_SENT("이메일로 인증 코드가 발송되었습니다."),
    EMAIL_VERIFICATION_SUCCESS("이메일 인증이 완료되었습니다."),

    // 약관 관련
    AGREEMENT_CREATED_SUCCESS("약관이 성공적으로 생성되었습니다."),
    AGREEMENT_RETRIEVED_SUCCESS("약관이 성공적으로 조회되었습니다."),
    AGREEMENT_UPDATED_SUCCESS("약관이 성공적으로 수정되었습니다."),

    // 사용자 약관 관련
    AGREEMENT_STATUS_RETRIEVED_SUCCESS("약관 동의 상태 조회가 성공적으로 완료되었습니다."),
    AGREEMENT_AGREED_SUCCESS("약관 동의가 성공적으로 완료되었습니다."),
    AGREEMENT_REAGREED_SUCCESS("약관 재동의가 성공적으로 완료되었습니다."),

    // 유저 프로필 등록
    USER_PROFILE_UPLOAD_SUCCESS("프로필 이미지가 정상적으로 등록되었습니다."),
    USER_PROFILE_GET_SUCCESS("프로필 이미지가 정상적으로 조회되었습니다."),
    USER_PROFILE_DELETE_SUCCESS("프로필 이미지가 정상적으로 삭제되었습니다."),

    // 카테고리 관련
    CREATED_CATEGORY_SUCCESS("카테고리가 성공적으로 생성되었습니다."),
    DELETED_CATEGORY_SUCCESS("카테고리가 성공적으로 삭제되었습니다."),
    UPDATE_CATEGORY_SUCCESS("카테고리가 성공적으로 수정되었습니다."),
    GET_CATEGORY_SUCCESS("카테고리가 성공적으로 조회되었습니다."),

    // 해시태그 관련
    CREATED_HASHTAG_SUCCESS("해시태그가 성공적으로 생성되었습니다."),
    DELETED_HASHTAG_SUCCESS("해시태그가 성공적으로 삭제되었습니다."),
    GET_HASHTAG_SUCCESS("해시태그가 성공적으로 조회되었습니다."),

    // 보드 관련
    BOARD_CREATED("보드가 성공적으로 생성되었습니다."),
    BOARD_UPDATED("보드가 성공적으로 수정되었습니다."),
    BOARD_DELETED("보드가 성공적으로 삭제되었습니다."),

    // 스케쥴 관련
    SCHEDULE_CREATED("스케쥴이 성공적으로 생성되었습니다."),
    SCHEDULE_UPDATED("스케쥴이 성공적으로 수정되었습니다."),
    SCHEDULE_DELETED("스케쥴이 성공적으로 ��제되었습니다."),

    //gather 관련
    GATHER_CREATE_SUCCESS("생성 성공"),
    GATHER_UPDATE_SUCCESS("수정 완료"),
    GATHER_DELETE_SUCCESS("삭제 완료"),
    GET_SUCCESS("조회 성공"),

    //댓글 관련
    CREATED_COMMENT_SUCCESS("댓글이 성공적으로 생성되었습니다."),
    DELETED_COMMENT_SUCCESS("댓글이 성공적으로 삭제되었습니다."),
    UPDATE_COMMENT_SUCCESS("댓글이 성공적으로 수정되었습니다."),
    GET_COMMENT_SUCCESS("댓글이 성공적으로 조회되었습니다."),

    //카카오맵 관련
    SAVED_MAP_SUCCESS("위치가 성공적으로 저장 되었습니다."),

    //멤버 관련
    CREATE_SUCCESS("멤버 생성에 성공하였습니다."),
    APPROVAL_SUCCESS("승인이 완료되었습니다."),
    REFUSAL_SUCCESS("가입이 거절되었습니다."),
    WITHDRAWAL_SUCCESS("모임탈퇴가 완료되었습니다.");


    private final String message;

    ApiResponseEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
