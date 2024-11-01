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
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token).build();
    }

    // 카카오 소셜 로그인 성공
    @Operation(summary = "카카오 소셜 로그인 / 성공", description = "카카오 소셜 로그인 성공 후 JWT 토큰을 발급하고 홈 화면으로 리디렉트합니다.")
    @GetMapping("/social-login/kakao/success")
    public void kakaoSocialLoginSuccess(
            @AuthenticationPrincipal OAuth2User oauth2user,
            HttpServletResponse response) throws IOException {
        String token = authService.kakaoSocialLogin(oauth2user);
        response.sendRedirect("/home.html?token=" + "Bearer " + token);
    }

    // 소셜 로그인 실패
    @Operation(summary = "소셜 로그인 / 실패", description = "소셜 로그인 실패 시 처리")
    @GetMapping("/social-login/failure")
    public void socialLoginFailure(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login.html?error=true");
    }

}

