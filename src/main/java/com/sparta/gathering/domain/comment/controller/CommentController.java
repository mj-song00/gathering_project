package com.sparta.gathering.domain.comment.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.comment.dto.request.CommentRequest;
import com.sparta.gathering.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Comment", description = "댓글 API / 변영덕")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class CommentController {

    private final CommentService commentService;

    //댓글 생성
    @Operation(summary = "댓글 생성", description = "모든 멤버 생성 가능합니다.")
    @PostMapping("/{scheduleId}/gather/{gatherId}/comments")
    public ResponseEntity<ApiResponse<Void>> saveComment(
            @PathVariable Long gatherId,
            @PathVariable Long scheduleId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        commentService.createComment(scheduleId, authenticatedUser, request, gatherId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.CREATED_COMMENT_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "댓글 조회", description = "모든 사용자 조회 가능합니다.")
    @GetMapping("/{scheduleId}/comments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getComment(
            @PathVariable Long scheduleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size) {

        Map<String, Object> result = commentService.getCommentPage(scheduleId, page, size);
        ApiResponse<Map<String, Object>> response = ApiResponse.successWithData(result,
                ApiResponseEnum.GET_COMMENT_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary = "댓글 수정", description = "댓글 생성자만 수정 가능합니다.")
    @PatchMapping("/{scheduleId}/gather/{gatherId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @RequestBody CommentRequest request,
            @PathVariable Long gatherId,
            @PathVariable Long scheduleId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        commentService.updateComment(request, scheduleId, commentId, authenticatedUser, gatherId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.UPDATE_COMMENT_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "댓글 삭제", description = "댓글 생성자와 매니저만 삭제 가능합니다.")
    @PatchMapping("/{scheduleId}/gather/{gatherId}/comments/{commentId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long gatherId,
            @PathVariable Long scheduleId,
            @PathVariable Long commentId) {
        commentService.deleteComment(authenticatedUser, scheduleId, commentId, gatherId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.DELETED_COMMENT_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

