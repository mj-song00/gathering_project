package com.sparta.gathering.domain.user.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "UserProfile", description = "유저 프로필 이미지 API / 고결")
@RestController
@RequestMapping("/api/users/{userId}/profile-image")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // 이미지 수정
    @Operation(summary = "프로필 이미지 수정", description = "이미지를 변경할 수 있습니다. 사이즈는 5 * 1024 * 1024 보다 커야하며 .jpeg, .png 파일만 허용됩니다 ")
    @PutMapping
    public ResponseEntity<ApiResponse<?>> updateProfileImage(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("userId") UUID userId,
            @RequestParam("file") MultipartFile newImage
    ) {
        String res = userProfileService.updateProfileImage(authenticatedUser, userId, newImage);
        return ResponseEntity.ok(
                ApiResponse.successWithData(res, ApiResponseEnum.USER_PROFILE_UPLOAD_SUCCESS));
    }

    // 이미지 조회
    @Operation(summary = "프로필 이미지 조회", description = "누구나 이미지를 조회할 수 있으며 이미지 미설정 시 기본이미지가 보여집니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUserProfileImages(
            @PathVariable("userId") UUID userId
    ) {
        String res = userProfileService.getUserProfileImages(userId);
        return ResponseEntity.ok(
                ApiResponse.successWithData(res, ApiResponseEnum.USER_PROFILE_GET_SUCCESS));
    }

    // 이미지 삭제
    @Operation(summary = "프로필 이미지 삭제", description = "이미지를 삭제하면 기본이미지로 변경됩니다.")
    @DeleteMapping()
    public ResponseEntity<ApiResponse<?>> deleteUserProfileImage(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable("userId") UUID userId
    ) {
        userProfileService.deleteUserProfileImage(authenticatedUser, userId);
        return ResponseEntity.ok(
                ApiResponse.successWithOutData(ApiResponseEnum.USER_PROFILE_DELETE_SUCCESS));
    }
}
