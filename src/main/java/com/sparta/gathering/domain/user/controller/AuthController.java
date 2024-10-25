package com.sparta.gathering.domain.user.controller;

import com.sparta.gathering.common.config.JwtTokenProvider;
import com.sparta.gathering.domain.user.dto.request.LoginRequest;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;

  @Autowired
  public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
    this.userService = userService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  // 일반 로그인
  @Operation(summary = "로그인", description = "로그인을 진행합니다.")
  @PostMapping("/login")
  public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
    User user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
    String token = jwtTokenProvider.createToken(user.getId(), user.getEmail(),
        user.getUserRole());
    return ResponseEntity.ok()
        .header("Authorization", token)
        .build();
  }

}

