package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.JwtTokenProvider;
import com.sparta.gathering.domain.user.dto.request.LoginRequest;
import com.sparta.gathering.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final KakaoSocialAuthService kakaoSocialAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(LoginRequest loginRequest) {
        User user = userService.authenticateUser(loginRequest.getEmail(),
                loginRequest.getPassword());
        return generateToken(user);
    }

    public String kakaoSocialLogin(OAuth2User oauth2User) {
        User user = kakaoSocialAuthService.processOauth2User(oauth2User);
        return generateToken(user);
    }

    private String generateToken(User user) {
        return jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getUserRole());
    }
}
