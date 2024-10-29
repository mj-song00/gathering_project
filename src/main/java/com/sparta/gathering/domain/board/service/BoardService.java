package com.sparta.gathering.domain.board.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.board.dto.request.BoardRequestDto;
import com.sparta.gathering.domain.board.dto.response.BoardResponseDto; // BoardResponseDto 추가
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.board.repository.BoardRepository;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final GatherRepository gatherRepository;

    @Transactional
    public BoardResponseDto createBoard(Long gatherId, BoardRequestDto boardRequestDto) {
        // gatherId를 사용하여 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        // Board 엔티티 생성 및 Gather 엔티티 설정
        Board board = new Board(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardContent());

        gather.getBoardList().add(board); // 양방향 연관관계 설정
        boardRepository.save(board);

        // 저장된 Board를 DTO로 변환하여 반환
        return new BoardResponseDto(board.getId(), board.getBoardTitle(), board.getBoardContent());
    }

    @Transactional
    public BoardResponseDto updateBoard(Long gatherId, Long boardsId, BoardRequestDto boardRequestDto) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        // gather의 boardList에서 boardsId와 일치하는 Board를 찾음
        Board board = gather.getBoardList().stream()
                .filter(b -> b.getId().equals(boardsId))
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionEnum.BOARD_NOT_FOUND));

        // 보드 내용 업데이트
        board.update(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardContent());

        // 변경된 보드 저장
        Board updatedBoard = boardRepository.save(board);  // 저장 후 반환

        // 저장된 Board를 DTO로 변환하여 반환
        return new BoardResponseDto(updatedBoard.getId(), updatedBoard.getBoardTitle(), updatedBoard.getBoardContent());
    }

    @Transactional
    public void deleteBoard(Long gatherId, Long boardsId) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        // gather의 boardList에서 boardsId와 일치하는 Board를 찾음
        Board board = gather.getBoardList().stream()
                .filter(b -> b.getId().equals(boardsId))
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionEnum.BOARD_NOT_FOUND));

        // gather의 boardList에서 보드를 제거
        gather.getBoardList().remove(board);

        board.delete(LocalDateTime.now());

        // 보드 삭제
        boardRepository.delete(board);
    }
}
