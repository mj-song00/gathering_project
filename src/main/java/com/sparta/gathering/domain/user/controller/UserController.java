package com.sparta.gathering.domain.user.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.user.dto.request.ChangeNickNameRequest;
import com.sparta.gathering.domain.user.dto.request.ChangePasswordRequest;
import com.sparta.gathering.domain.user.dto.request.SignupRequest;
import com.sparta.gathering.domain.user.dto.response.UserProfileResponse;
import com.sparta.gathering.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "User", description = "사용자 API / 이정현")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {
        userService.createUser(signupRequest);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.SIGNUP_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 프로필 조회", description = "본인의 프로필을 조회합니다.")
    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        UserProfileResponse profile = userService.getUserProfile(authenticatedUser);
        ApiResponse<UserProfileResponse> response =
                ApiResponse.successWithData(profile, ApiResponseEnum.PROFILE_RETRIEVED_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경", description = "본인의 비밀번호를 변경합니다.")
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(
                authenticatedUser,
                changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword());
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.PASSWORD_CHANGED_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "닉네임 변경", description = "본인의 닉네임을 변경합니다.")
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<Void>> changeNickName(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody ChangeNickNameRequest changeNickNameRequest) {
        userService.changeNickName(
                authenticatedUser,
                changeNickNameRequest.getNewNickName());
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.NICKNAME_CHANGED_SUCCESS);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 탈퇴", description = "본인의 계정을 탈퇴합니다.")
    @PatchMapping("/me/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        userService.deleteUser(authenticatedUser, refreshToken, response);
        ApiResponse<Void> responseBody =
                ApiResponse.successWithOutData(ApiResponseEnum.USER_DELETED_SUCCESS);
        return ResponseEntity.ok(responseBody);
    }

}


