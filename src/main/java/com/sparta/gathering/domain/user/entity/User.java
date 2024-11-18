package com.sparta.gathering.domain.user.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.agreement.enums.AgreementStatus;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.useragreement.entity.UserAgreement;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "User")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED) // 외부 직접 호출을 막기 위해 protected 설정
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID id; // UUID BINARY(16)으로 저장

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (불변 필드로 유지)

    @Column(nullable = false)
    private String nickName; // 닉네임

    @Column
    private String password; // 일반 로그인 사용자의 비밀번호 (소셜 로그인 사용자는 null 가능)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole; // 사용자 역할 (ROLE_USER, ROLE_ADMIN)

    @Column
    private LocalDateTime deletedAt; // 회원 탈퇴 여부 및 탈퇴 시간

    @Column
    private String providerId; // 소셜 로그인 제공자의 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdentityProvider identityProvider; // 소셜 로그인 제공자 (KAKAO, NONE)

    @Column
    private String profileImage; // 사용자 프로필 이미지 URL (null 경우 디폴트 이미지)

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserAgreement> userAgreements = new ArrayList<>();

    // 비밀번호 변경
    public void setPassword(String password) {
        this.password = password;
    }

    // 닉네임 변경
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    // 회원 탈퇴
    public void setDeletedAt() {
        this.deletedAt = LocalDateTime.now();
    }

    // 프로필 이미지 삭제
    public void setDeleteProfileImage() {
        this.profileImage = null;

    }

    // 프로필 이미지 업데이트
    public void setUpdateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    // 약관 동의 추가
    public void addUserAgreement(UserAgreement userAgreement) {
        userAgreements.add(userAgreement);
        if (userAgreement.getUser() != this) {
            userAgreement.setUser(this);
        }
    }

    // 필수 약관의 만료 상태를 확인하는 메서드
    public boolean isInactiveDueToExpiredAgreements() {
        return userAgreements.stream()
                .anyMatch(agreement -> agreement.isRequired() && agreement.getStatus() == AgreementStatus.EXPIRED);
    }

}
