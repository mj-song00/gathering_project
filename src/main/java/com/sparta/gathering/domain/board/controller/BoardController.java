package com.sparta.gathering.domain.board.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.board.dto.request.BoardRequestDto;
import com.sparta.gathering.domain.board.dto.response.BoardResponseDto; // BoardResponseDto 추가
import com.sparta.gathering.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gathers/{gatherId}/boards")
@Tag(name = "Board API", description = "보드 API")
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    @Operation(summary = "보드 생성", description = "Board 생성")
    public ResponseEntity<ApiResponse<BoardResponseDto>> createBoard(@PathVariable(name = "gatherId") Long gatherId, @RequestBody BoardRequestDto boardRequestDto, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        BoardResponseDto board = boardService.createBoard(gatherId, boardRequestDto, authenticatedUser);
        return ResponseEntity.ok(ApiResponse.successWithData(board, ApiResponseEnum.BOARD_CREATED));
    }

    @PutMapping("/{boardsId}")
    @Operation(summary = "보드 수정", description = "Board 수정")
    public ResponseEntity<ApiResponse<BoardResponseDto>> updateBoard(@PathVariable(name = "gatherId") Long gatherId, @PathVariable(name = "boardsId") Long boardsId, @RequestBody BoardRequestDto boardRequestDto, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        BoardResponseDto updatedBoard = boardService.updateBoard(gatherId, boardsId, boardRequestDto, authenticatedUser);
        return ResponseEntity.ok(ApiResponse.successWithData(updatedBoard, ApiResponseEnum.BOARD_UPDATED));
    }

    @PatchMapping("/delete/{boardsId}")
    @Operation(summary = "보드 삭제", description = "Board 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable(name = "gatherId") Long gatherId, @PathVariable(name = "boardsId") Long boardsId, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        boardService.deleteBoard(gatherId, boardsId, authenticatedUser);
        return ResponseEntity.ok(ApiResponse.successWithData(null, ApiResponseEnum.BOARD_DELETED));
    }
}
