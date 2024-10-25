package com.sparta.gathering.domain.comment.dto.response;

import com.sparta.gathering.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private String userName;
    private String comment;
    private LocalDateTime creation_Date;
    private LocalDateTime modified_Date;

    public CommentResponse(Comment comment) {
        this.userName = comment.getNickName();
        this.comment = comment.getComment();
        this.creation_Date = comment.getCreatedAt();
        this.modified_Date = comment.getUpdatedAt();
    }
}
