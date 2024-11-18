package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.config.jwt.JwtTokenProvider;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.common.repository.RefreshTokenRepository;
import com.sparta.gathering.domain.user.dto.request.LoginRequest;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import com.sparta.gathering.domain.user.validation.UserValidation;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserValidation userValidation;

    // 로그인 처리
    public String login(LoginRequest loginRequest) {
        User user = findByEmail(loginRequest.getEmail());
        userValidation.validateUserNotDeleted(user);
        authenticateUser(user, loginRequest.getPassword());
        return generateAccessToken(user);
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(String email) {
        User user = findByEmail(email);
        return jwtTokenProvider.createRefreshToken(user.getId());
    }

    // 리프레시 토큰 쿠키 설정
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge((long) 7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 액세스 토큰 재발급
    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshTokenRepository.isBlacklisted(refreshToken)
                || !jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new BaseException(ExceptionEnum.INVALID_REFRESH_TOKEN);
        }

        Claims claims = jwtTokenProvider.extractClaims(refreshToken);
        UUID userId = UUID.fromString(claims.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        return generateAccessToken(user);
    }

    // 로그아웃 처리
    public void logout(String refreshToken, HttpServletResponse response) {

        if (refreshToken != null) {
            long expiration = jwtTokenProvider.getRemainingExpiration(refreshToken);
            refreshTokenRepository.addToBlacklist(refreshToken, expiration);
        }

        // 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "") // 빈 문자열 사용
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }

    // 소셜 로그인 처리
    public String kakaoSocialLogin(OAuth2User oauth2User) {
        User user = kakaoSocialAuthService.processOauth2User(oauth2User);
        userValidation.validateUserNotDeleted(user);
        return generateAccessToken(user);
    }

    // 이메일로 사용자 조회
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    // 비밀번호 인증
    public void authenticateUser(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.EMAIL_PASSWORD_MISMATCH);
        }
    }

    // 액세스 토큰 생성
    private String generateAccessToken(User user) {
        return jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getUserRole());
    }

}
