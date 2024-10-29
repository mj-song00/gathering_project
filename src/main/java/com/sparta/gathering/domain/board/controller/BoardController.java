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
@RequestMapping("/api/gather/{gatherId}/boards")
@Tag(name = "Board API", description = "보드 API")
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    @Operation(summary = "Create Board", description = "Board 생성")
    public ResponseEntity<ApiResponse<BoardResponseDto>> createBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @RequestBody BoardRequestDto boardRequestDto)
    {
        BoardResponseDto board = boardService.createBoard(gatherId, boardRequestDto);
        return ResponseEntity.ok(ApiResponse.successWithData(board, ApiResponseEnum.BOARD_CREATED));
    }

    @PutMapping("/{boardsId}")
    @Operation(summary = "Update Board", description = "Board 수정")
    public ResponseEntity<ApiResponse<BoardResponseDto>> updateBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "boardsId") Long boardsId,
            @RequestBody BoardRequestDto boardRequestDto)
    {
        BoardResponseDto updatedBoard = boardService.updateBoard(gatherId, boardsId, boardRequestDto);
        return ResponseEntity.ok(ApiResponse.successWithData(updatedBoard, ApiResponseEnum.BOARD_UPDATED));
    }

    @DeleteMapping("/{boardsId}")
    @Operation(summary = "Delete Board", description = "Board 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable(name = "gatherId") Long gatherId,
            @PathVariable(name = "boardsId") Long boardsId)
    {
        boardService.deleteBoard(gatherId, boardsId);
        return ResponseEntity.ok(ApiResponse.successWithData(null, ApiResponseEnum.BOARD_DELETED));
    }
}
