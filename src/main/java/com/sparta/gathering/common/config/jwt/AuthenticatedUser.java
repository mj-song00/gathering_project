package com.sparta.gathering.common.config.jwt;

import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final UUID userId; // 사용자 식별자
    private final String email; // 사용자 이메일
    private final Collection<? extends GrantedAuthority> authorities; // 사용자 권한 정보

}
