package com.sparta.gathering.domain.board.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.board.dto.request.BoardRequestDto;
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/{gatherId}")
@Tag(name = "Board API", description = "보드 API")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/board")
    @Operation(summary = "create board", description = "Board 생성")
    public ApiResponse<?> createBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @RequestBody BoardRequestDto boardRequestDto) {
        Board board = boardService.createBoard(gatherId, boardRequestDto);
        return ApiResponse.successWithData(board, ApiResponseEnum.BOARD_CREATED);
    }

    @PutMapping("/boards/{boardsId}")
    @Operation(summary = "update board", description = "Board 수정")
    public ApiResponse<?> updateBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "boardsId") Long boardsId,
            @RequestBody BoardRequestDto boardRequestDto) {
        Board updatedBoard = boardService.updateBoard(gatherId, boardsId, boardRequestDto);
        return ApiResponse.successWithData(updatedBoard, ApiResponseEnum.BOARD_UPDATED);
    }

    @DeleteMapping("/boards/{boardsId}")
    @Operation(summary = "delete board", description = "Board 삭제")
    public ApiResponse<?> deleteBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "boardsId") Long boardsId) {
        boardService.deleteBoard(gatherId, boardsId);
        return ApiResponse.successWithData(null, ApiResponseEnum.BOARD_DELETED);
    }

}
