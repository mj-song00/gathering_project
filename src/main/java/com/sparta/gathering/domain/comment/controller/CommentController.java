package com.sparta.gathering.domain.comment.controller;
import com.sparta.gathering.common.annotation.Auth;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.comment.dto.request.CommentRequest;
import com.sparta.gathering.domain.comment.service.CommentService;
import com.sparta.gathering.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 생성
    @Operation(summary = "댓글 생성", description = "모든 사용자 생성 가능합니다.")
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<ApiResponse<Void>> saveComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(commentService.createComment(boardId, user, request));

    }
//
//    /**
//     * 댓글 조회
//     * @param boardId 게시판 ID
//     * @return 댓글 작성 유저 이름, 내용, 작성일, 수정일
//     */
//    @GetMapping("/{boardId}/comments")
//    public ResponseEntity<List<GetCommentListResponseDto>> getComment(
//            @PathVariable Long boardId
//    ){
//        return ResponseEntity.ok(commentService.getComment(boardId));
//    }

    /**
     * 댓글 수정
     * @param request 수정할 댓글 내용
     * @param boardId 게시판 ID
     * @param commentId 댓글 ID
     * @param user 유저 ID, 유저 이메일
     * @return 수정된 댓글 내용
     */
    @PutMapping("/{scheduleId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @RequestBody CommentRequest request,
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user
    ) {
        commentService.updateComment(request,boardId,commentId,user);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.CREATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 삭제
     * @param user 유저 ID, 유저 이메일, 닉네임
     * @param scheduleId 게시판 ID
     * @param commentId 댓글 ID
     * @return 삭제한 댓글 ID
     */
    @PatchMapping("/{scheduleId}/comments/{commentId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long scheduleId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(user, scheduleId, commentId);
        return ResponseEntity.status(HttpStatus.);
    }
}

