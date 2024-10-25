package com.sparta.gathering.domain.board.service;

import com.sparta.gathering.domain.board.dto.request.BoardRequestDto;
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.board.repository.BoardRepository;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final GatherRepository gatherRepository;

    public Board createBoard(Long gatherId, BoardRequestDto boardRequestDto) {

        // gatherId를 사용하여 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다. gatherId: " + gatherId));

        // Board 엔티티 생성 및 Gather 엔티티 설정
        Board board = new Board(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardContent());
        board.setGather(gather);  // Board에 Gather 엔티티 설정

        // Board 저장
        boardRepository.save(board);

        // 저장된 Board 반환
        return board;
    }

    public Board updateBoard(Long gatherId, Long boardsId, BoardRequestDto boardRequestDto) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));

        // gather의 boardList에서 boardsId와 일치하는 Board를 찾음
        Board board = gather.getBoardList().stream()
                .filter(b -> b.getId().equals(boardsId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 보드를 찾을 수 없습니다."));

        // 보드 내용 업데이트
        board.update(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardContent());

        // 변경된 보드 저장
        return boardRepository.save(board);  // 저장 후 바로 반환
    }

    public void deleteBoard(Long gatherId, Long boardsId) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));

        // gather의 boardList에서 boardsId와 일치하는 Board를 찾음
        Board board = gather.getBoardList().stream()
                .filter(b -> b.getId().equals(boardsId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 보드를 찾을 수 없습니다."));

        // gather의 boardList에서 보드를 제거
        gather.getBoardList().remove(board);

        // 보드 삭제
        boardRepository.delete(board);
    }
}
