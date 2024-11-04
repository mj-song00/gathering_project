package com.sparta.gathering.domain.board.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.board.dto.request.BoardRequestDto;
import com.sparta.gathering.domain.board.dto.response.BoardResponseDto; // BoardResponseDto 추가
import com.sparta.gathering.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/gathers/{gatherId}/boards")
@Tag(name = "Board API", description = "보드 API")
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    @Operation(summary = "보드 생성", description = "Board 생성")
    public ResponseEntity<ApiResponse<BoardResponseDto>> createBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @RequestBody BoardRequestDto boardRequestDto)
    {
        BoardResponseDto board = boardService.createBoard(gatherId, boardRequestDto);
        return ResponseEntity.ok(ApiResponse.successWithData(board, ApiResponseEnum.BOARD_CREATED));
    }

    @PutMapping("/{boardsId}")
    @Operation(summary = "보드 수정", description = "Board 수정")
    public ResponseEntity<ApiResponse<BoardResponseDto>> updateBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "boardsId") Long boardsId,
            @RequestBody BoardRequestDto boardRequestDto)
    {
        BoardResponseDto updatedBoard = boardService.updateBoard(gatherId, boardsId, boardRequestDto);
        return ResponseEntity.ok(ApiResponse.successWithData(updatedBoard, ApiResponseEnum.BOARD_UPDATED));
    }

    @PatchMapping("/{boardsId}")
    @Operation(summary = "보드 삭제", description = "Board 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "boardsId") Long boardsId)
    {
        boardService.deleteBoard(gatherId, boardsId);
        return ResponseEntity.ok(ApiResponse.successWithData(null, ApiResponseEnum.BOARD_DELETED));
    }
}
