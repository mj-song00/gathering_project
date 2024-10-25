package com.sparta.gathering.domain.user.controller;

import com.sparta.gathering.common.config.JwtTokenProvider;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.service.UserService;
import com.sparta.gathering.domain.user.dto.request.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider JwtTokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = JwtTokenProvider;
    }

    // 회원가입
    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userService.createUser(userRequest);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.SIGNUP_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "회원 탈퇴", description = "본인의 계정을 탈퇴합니다.")
    @PatchMapping("/me/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId().toString());
        return ResponseEntity.ok(ApiResponse.successWithOutData(ApiResponseEnum.USER_DELETED_SUCCESS));
    }

}


