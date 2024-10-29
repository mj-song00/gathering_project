package com.sparta.gathering.common.config.jwt;

import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class AuthenticatedUser implements UserDetails {

    private final UUID userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // 비밀번호는 JWT 인증에서 사용하지 않음 UserDetails와 호환성을 위해 남겨눈 두되 null로 설정 + 소셜 로그인은 비밀번호가 없어서
    }

    @Override
    public String getUsername() {
        return email; // 이메일을 사용자 이름으로 사용 UserDetails와 호환성을 위해 메서드명은 getUsername으로 설정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true: 만료되지 않음 , false: 만료됨)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true: 잠기지 않음, false: 잠김)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 (true: 만료되지 않음, false: 만료됨)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true: 활성화, false: 비활성화)
    }

}
