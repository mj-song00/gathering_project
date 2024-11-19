package com.sparta.gathering.common.config.jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class AuthenticatedUser {

    private final UUID userId; // 사용자 식별자
    private final String email; // 사용자 이메일
    private final Collection<? extends GrantedAuthority> authorities; // 사용자 권한 정보

    public AuthenticatedUser(UUID userId, String email, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities != null ? authorities : Collections.emptyList(); // 기본값 설정
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
