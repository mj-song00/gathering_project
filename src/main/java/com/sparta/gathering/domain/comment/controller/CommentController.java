package com.sparta.gathering.domain.comment.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.comment.dto.request.CommentRequest;
import com.sparta.gathering.domain.comment.dto.response.CommentResponse;
import com.sparta.gathering.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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


@Tag(name = "Comment", description = "댓글 API / 변영덕")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class CommentController {

    private final CommentService commentService;

    //댓글 생성

    /**
     * 댓글생성
     *
     * @param scheduleId        댓글을 생성할 스케줄의 아이디
     * @param request           댓글 내용
     * @param authenticatedUser 댓글을 생성하는 유저의 정보
     */
    @Operation(summary = "댓글 생성", description = "모든 멤버 생성 가능합니다.")
    @PostMapping("/{scheduleId}/comments")
    public ResponseEntity<ApiResponse<Void>> saveComment(
            @PathVariable Long scheduleId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        commentService.createComment(scheduleId, authenticatedUser, request);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.CREATED_COMMENT_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 조회
     *
     * @param scheduleId 게시판 ID
     * @return 댓글 작성 유저 이름, 내용, 작성일, 수정일
     */
    @Operation(summary = "댓글 조회", description = "모든 사용자 조회 가능합니다.")
    @GetMapping("/{scheduleId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComment(
            @PathVariable Long scheduleId) {
        List<CommentResponse> list = commentService.getComment(scheduleId);
        ApiResponse<List<CommentResponse>> response = ApiResponse.successWithData(list,
                ApiResponseEnum.GET_COMMENT_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 댓글 수정
     *
     * @param request           수정할 댓글 내용
     * @param scheduleId        게시판 ID
     * @param commentId         댓글 ID
     * @param authenticatedUser 유저 ID, 유저 이메일
     */
    @Operation(summary = "댓글 수정", description = "댓글 생성자만 수정 가능합니다.")
    @PatchMapping("/{scheduleId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @RequestBody CommentRequest request,
            @PathVariable Long scheduleId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        commentService.updateComment(request, scheduleId, commentId, authenticatedUser);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.UPDATE_COMMENT_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 댓글 삭제
     *
     * @param authenticatedUser 유저 ID, 유저 이메일, 닉네임
     * @param scheduleId        게시판 ID
     * @param commentId         댓글 ID
     * @return 삭제한 댓글 ID
     */
    @Operation(summary = "댓글 삭제", description = "댓글 생성자와 매니저만 삭제 가능합니다.")
    @PatchMapping("/{scheduleId}/comments/{commentId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long scheduleId,
            @PathVariable Long commentId) {
        commentService.deleteComment(authenticatedUser, scheduleId, commentId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.DELETED_COMMENT_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

