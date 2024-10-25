package com.sparta.gathering.domain.user.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/profile-image")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;


    // 이미지 수정
    @PutMapping
    public ResponseEntity<ApiResponse<?>> updateProfileImage(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") UUID userId,
            @RequestParam("file") MultipartFile newImage
    ) throws IOException {
        String res = userProfileService.updateProfileImage(user, userId, newImage);
        return ResponseEntity.ok(ApiResponse.successWithData(res, ApiResponseEnum.USER_PROFILE_GET_SUCCESS));
    }

    // 이미지 조회
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUserProfileImages(
            @PathVariable("userId") UUID userId
    ) {
        String res = userProfileService.getUserProfileImages(userId);
        return ResponseEntity.ok(ApiResponse.successWithData(res, ApiResponseEnum.USER_PROFILE_GET_SUCCESS));
    }

    // 이미지 삭제
    @DeleteMapping()
    public ResponseEntity<ApiResponse<?>> deleteUserProfileImage(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") UUID userId
    ) {
        userProfileService.deleteUserProfileImage(user, userId);
        return ResponseEntity.ok(ApiResponse.successWithOutData(ApiResponseEnum.USER_PROFILE_DELETE_SUCCESS));
    }
}
