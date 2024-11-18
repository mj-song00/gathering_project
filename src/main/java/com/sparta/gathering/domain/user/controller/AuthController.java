package com.sparta.gathering.domain.user.controller;

import com.sparta.gathering.domain.user.dto.request.LoginRequest;
import com.sparta.gathering.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API / 이정현")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 일반 로그인
    @Operation(summary = "로그인", description = "일반 사용자의 로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // 로그인 후 토큰 발급
        String accessToken = authService.login(loginRequest);
        String refreshToken = authService.generateRefreshToken(loginRequest.getEmail());

        // 리프레시 토큰을 HTTP-Only 쿠키로 설정
        authService.setRefreshTokenCookie(response, refreshToken);

        // 액세스 토큰은 헤더로 반환
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    // 리프레시 토큰으로 액세스 토큰 재발급
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    // 로그아웃
    @Operation(summary = "로그아웃", description = "사용자가 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok().build();
    }

    // 카카오 소셜 로그인 성공
    @GetMapping("/social-login/kakao/success")
    public void kakaoSocialLoginSuccess(@AuthenticationPrincipal OAuth2User oauth2user, HttpServletResponse response)
            throws IOException {
        String accessToken = authService.kakaoSocialLogin(oauth2user);
        String refreshToken = authService.generateRefreshToken(oauth2user.getAttribute("email"));

        // 리프레시 토큰 쿠키 설정
        authService.setRefreshTokenCookie(response, refreshToken);

        response.sendRedirect("/home.html?token=" + "Bearer " + accessToken);
    }

    // 소셜 로그인 실패
    @Operation(summary = "소셜 로그인 / 실패", description = "소셜 로그인 실패 시 처리")
    @GetMapping("/social-login/failure")
    public void socialLoginFailure(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login.html?error=true");
    }
    
}

