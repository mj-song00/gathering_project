package com.sparta.gathering.domain.agreement.enums;

public enum AgreementStatus {
    AGREED,           // 사용자가 약관에 동의한 상태
    PENDING_REAGREE,  // 약관 변경으로 인해 재동의가 필요한 상태
    DISAGREED,        // 약관에 동의하지 않은 상태
    EXPIRED           // 유예 기간 만료로 인해 자동 탈퇴된 상태
}
