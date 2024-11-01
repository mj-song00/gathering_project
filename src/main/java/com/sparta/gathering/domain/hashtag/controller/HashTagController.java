package com.sparta.gathering.domain.hashtag.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.hashtag.dto.request.HashTagsReq;
import com.sparta.gathering.domain.hashtag.dto.response.HashTagRes;
import com.sparta.gathering.domain.hashtag.service.HashTagService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gatherings/{gatherId}/hashtags")
@RequiredArgsConstructor
public class HashTagController {

    private final HashTagService hashTagService;

    // 해시태그 생성
    @Operation(summary = "해시태그 생성", description = "모임의 MANAGER 만 생성 가능합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<List<HashTagRes>>> createHashTag(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Gather gatherId,
            @RequestBody HashTagsReq hashTagReq) {
        List<HashTagRes> res = hashTagService.createHashTag(authenticatedUser, gatherId, hashTagReq);
        ApiResponse<List<HashTagRes>> response = ApiResponse.successWithData(res,
                ApiResponseEnum.CREATED_HASHTAG_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 해시태그 조회
    @Operation(summary = "해시태그 조회", description = "모임의 모든 해시태그 조회입니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<HashTagRes>>> getHashTagList(
            @PathVariable Gather gatherId) {
        List<HashTagRes> list = hashTagService.getHashTagList(gatherId);
        ApiResponse<List<HashTagRes>> response = ApiResponse.successWithData(list,
                ApiResponseEnum.CREATED_CATEGORY_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 해시태그 삭제
    @Operation(summary = "해시태그 삭제", description = "모임의 MANAGER 만 삭제 가능합니다.")
    @PatchMapping("/{hashtagId}")
    public ResponseEntity<ApiResponse<?>> deleteHashTag(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Gather gatherId,
            @PathVariable Long hashtagId) {
        hashTagService.deleteHashTag(authenticatedUser, gatherId, hashtagId);
        ApiResponse<?> response = ApiResponse.successWithOutData(
                ApiResponseEnum.DELETED_HASHTAG_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
