package com.sparta.gathering.domain.board.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResponseDto {
    private Long boardId;
    private String boardTitle;
    private String boardContent;

    public BoardResponseDto(Long id, String boardTitle, String boardContent) {
        this.boardId = id;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
    }
}
