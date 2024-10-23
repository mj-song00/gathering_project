
package com.sparta.gathering.domain.user.controller;

import com.sparta.gathering.common.config.JwtTokenProvider;
import com.sparta.gathering.domain.user.dto.request.LoginRequest;
import com.sparta.gathering.domain.user.dto.request.RefreshTokenRequest;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.service.RefreshTokenService;
import com.sparta.gathering.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService; // 보류

    @Autowired
    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    // 일반 로그인
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getNickName(), user.getUserRole());
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }

   /* 보류 ############################################################
   // 소셜 로그인 성공 후, JWT 토큰 발급
    @PostMapping("/oauth2/login-success")
    public ResponseEntity<Map<String, String>> oauth2LoginSuccess(Authentication authentication) {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String providerId = oauthUser.getAttribute("sub"); // OAuth2 제공자별 ID 가져오기 (구글은 "sub", 카카오는 다름)
        String email = oauthUser.getAttribute("email");

        User user = userService.findByProviderIdAndIdentityProvider(providerId, IdentityProvider.GOOGLE); // 구글 예시
        if (user == null) {
            // 소셜 계정으로 새 사용자 생성
            user = userService.createUser(email, oauthUser.getAttribute("name"), null, UserRole.ROLE_USER, IdentityProvider.GOOGLE);
        }

        String token = jwtTokenProvider.createToken(user.getId());
        String refreshToken = refreshTokenService.createRefreshToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("access_token", token);
        response.put("refresh_token", refreshToken);

        return ResponseEntity.ok(response);
    }

    // Refresh Token를 통해 Access Token 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        boolean isValid = refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());

        if (!isValid) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = refreshTokenService.findUserByRefreshToken(refreshTokenRequest.getRefreshToken());
        String newToken = jwtTokenProvider.createToken(user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("access_token", newToken);

        return ResponseEntity.ok(response);
    }*/

}

