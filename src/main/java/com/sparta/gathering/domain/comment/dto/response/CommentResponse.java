package com.sparta.gathering.domain.comment.dto.response;

import com.sparta.gathering.domain.comment.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final String nickName;
    private final String comment;
    private final LocalDateTime creation_Date;
    private final LocalDateTime modified_Date;

    public CommentResponse(Comment comment) {
        this.nickName = comment.getNickName();
        this.comment = comment.getComment();
        this.creation_Date = comment.getCreatedAt();
        this.modified_Date = comment.getUpdatedAt();
    }
}
