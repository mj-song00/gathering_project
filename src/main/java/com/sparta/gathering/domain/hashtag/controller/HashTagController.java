package com.sparta.gathering.domain.hashtag.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.hashtag.dto.request.HashTagReq;
import com.sparta.gathering.domain.hashtag.dto.response.HashTagRes;
import com.sparta.gathering.domain.hashtag.service.HashTagService;
import com.sparta.gathering.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
//@RequestMapping("/api/gatherings/{gatheringId}/hashtags")
@RequestMapping("/api/hashtags")
@RequiredArgsConstructor
public class HashTagController {
    private final HashTagService hashTagService;

    // 해시태그 생성
    @PostMapping
    public ResponseEntity<ApiResponse<HashTagRes>> createHashTag(@AuthenticationPrincipal User user, @RequestBody HashTagReq hashTagReq) {
        HashTagRes res = hashTagService.createHashTag(user, hashTagReq);
        ApiResponse<HashTagRes> response = ApiResponse.successWithData(res, ApiResponseEnum.CREATED_HASHTAG_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 해시태그 조회
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<HashTagRes>>> getHashTagList(@AuthenticationPrincipal U) {
//        List<HashTagRes> list = hashTagService.getHashTagList(user);
//        ApiResponse<List<HashTagRes>> response = ApiResponse.successWithData(list,ApiResponseEnum.CREATED_CATEGORY_SUCCESS);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    // 해시태그 삭제
    @PatchMapping("/{hashtagId}")
    public ResponseEntity<ApiResponse<?>> deleteHashTag(
            @AuthenticationPrincipal User user,
            @PathVariable UUID hashtagId) {
        hashTagService.deleteHashTag(user, hashtagId);
        ApiResponse<?> response = ApiResponse.successWithOutData(ApiResponseEnum.DELETED_HASHTAG_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
