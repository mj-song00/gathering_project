package com.sparta.gathering.domain.user.controller;

import com.sparta.gathering.common.config.JwtTokenProvider;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.service.UserService;
import com.sparta.gathering.domain.user.dto.request.UserRequest;
import com.sparta.gathering.domain.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userService.createUser(userRequest);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.SIGNUP_SUCCESS);
        // 201 Created 상태와 함께 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 사용자 삭제 (Soft Delete)
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.USER_DELETED_SUCCESS);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

}


