package com.sparta.gathering.domain.user.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "User")
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID id; // UUID를 BINARY(16)으로 저장

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
    private String profileImage; // 사용자 프로필 이미지 URL (null일 경우 디폴트 이미지)

    // UUID 자동 생성
    public static User createWithAutoUUID(String email, String nickName, String password,
            UserRole userRole, IdentityProvider identityProvider, String profileImage) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.email = email;
        user.nickName = nickName;
        user.password = password;
        user.userRole = userRole;
        user.identityProvider = identityProvider;
        user.profileImage = profileImage;
        return user;
    }

    // 소셜 로그인 사용자를 위한 생성 메서드
    public static User createSocialUser(String email, String nickName, UserRole userRole,
            IdentityProvider identityProvider, String providerId, String profileImage) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.email = email;
        user.nickName = nickName;
        user.userRole = userRole;
        user.identityProvider = identityProvider;
        user.providerId = providerId;
        user.profileImage = profileImage;
        return user;
    }

    // 회원 탈퇴
    public void setDeletedAt() {
        this.deletedAt = LocalDateTime.now();
    }

    // 소셜 로그인 정보 업데이트
    public void updateProviderInfo(String nickName, IdentityProvider provider, String providerId,
            String profileImage) {
        this.nickName = nickName != null ? nickName : this.nickName;
        this.identityProvider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage != null ? profileImage : this.profileImage;
    }

    // 프로필 이미지 삭제
    public void setDeleteProfileImage() {
        this.profileImage = null;

    }

    // 프로필 이미지 업데이트
    public void setUpdateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
