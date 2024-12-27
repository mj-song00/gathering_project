package com.sparta.gathering.domain.comment.dto.response;

import com.sparta.gathering.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CommentResponse {

    private final Long commentId;
    private final String nickName;
    private final String comment;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;


    public CommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.nickName = comment.getNickName();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
