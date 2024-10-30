package com.sparta.gathering.domain.comment.dto.response;

import com.sparta.gathering.domain.comment.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final String nickName;
    private final String comment;
    private final LocalDateTime creationDate;
    private final LocalDateTime modifiedDate;

    public CommentResponse(Comment comment) {
        this.nickName = comment.getNickName();
        this.comment = comment.getComment();
        this.creationDate = comment.getCreatedAt();
        this.modifiedDate = comment.getUpdatedAt();
    }
}
