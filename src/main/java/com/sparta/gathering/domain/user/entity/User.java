package com.sparta.gathering.domain.user.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;


import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "User")
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false)

    private UUID id; // UUID를 BINARY(16)으로 저장

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String nickName; // 닉네임

    @Column
    private String password; // 일반 로그인 사용자의 비밀번호 (소셜 로그인 사용자는 null 가능)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole; // 사용자 역할 (ROLE_USER, ROLE_ADMIN)

    @Column(nullable = false)
    private boolean isDelete = false; // 탈퇴 여부 (기본값 FALSE)

    @Column
    private String providerId; // 소셜 로그인 제공자의 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdentityProvider identityProvider; // 소셜 로그인 제공자 (KAKAO, GOOGLE, NONE)

    @Column
    private String profileImage; // 사용자 프로필 이미지 URL (null일 경우 디폴트 이미지)

    public User(String email, String nickName, String password, UserRole userRole, IdentityProvider identityProvider) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.userRole = userRole;
        this.identityProvider = identityProvider;
    }

    public User(UUID uuid, String email, String nickName, String password, UserRole userRole, IdentityProvider identityProvider) {
        this.id = uuid;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.userRole = userRole;
        this.identityProvider = identityProvider;
    }

    public User(UUID uuid, String email, String nickName, UserRole userRole) {
        this.id = uuid;
        this.email = email;
        this.nickName = nickName;
        this.userRole = userRole;
    }
}
