package com.sparta.gathering.domain.like.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Like", description = "좋아요 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 +1", description = "gather의 좋아요를 +1씩 올려줍니다.")
    @PostMapping("/gather/{gatherId}/member/{memberId}")
    public ResponseEntity<ApiResponse<Void>>addLike(
            @PathVariable Long gatherId,
            @PathVariable Long memberId
    ){
        likeService.addLike(memberId, gatherId);
        ApiResponse<Void> response =  ApiResponse.successWithOutData(
                ApiResponseEnum.REQUEST_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
