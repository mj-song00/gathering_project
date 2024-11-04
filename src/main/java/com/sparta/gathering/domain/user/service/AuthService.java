package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.JwtTokenProvider;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.dto.request.LoginRequest;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.user.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoSocialUserService kakaoSocialAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserValidation userValidation;

    public String login(LoginRequest loginRequest) {

        // 이메일로 사용자 조회
        User user = findByEmail(loginRequest.getEmail());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 사용자 인증 처리
        authenticateUser(user, loginRequest.getPassword());

        // 토큰 생성
        return generateToken(user);

    }

    // 카카오 소셜 로그인
    public String kakaoSocialLogin(OAuth2User oauth2User) {

        // 카카오 소셜 로그인 처리로 사용자 조회 또는 생성
        User user = kakaoSocialAuthService.processOauth2User(oauth2User);

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 토큰 생성
        return generateToken(user);

    }

    // 이메일로 사용자 조회
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    // 로그인 시 인증 처리
    public void authenticateUser(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.EMAIL_PASSWORD_MISMATCH);
        }
    }

    // 토큰 생성
    private String generateToken(User user) {
        return jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getUserRole());
    }


}
