package com.sparta.gathering.domain.board.service;

import com.sparta.gathering.domain.board.dto.request.BoardRequestDto;
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Board createBoard(Long gatherId, BoardRequestDto boardRequestDto) {

        // GatherId를 사용하여 추가 검증이나 관련 로직을 구현할 수 있습니다.
        Board board = new Board(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardContent(), gatherId);

        // 보드 생성 후 저장
        boardRepository.save(board);

        // 저장된 보드를 반환
        return board;
    }

    public Board updateBoard(Long gatherId, Long boardsId, BoardRequestDto boardRequestDto) {
        // 보드가 존재하는지 검증
        Board board = boardRepository.findByIdAndGatherId(boardsId, gatherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 보드를 찾을 수 없습니다."));

        // 보드 내용 업데이트
        board.update(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardContent());

        // 변경된 보드 저장
        boardRepository.save(board);

        // 업데이트된 보드 반환
        return board;
    }

    public void deleteBoard(Long gatherId, Long boardsId) {
        // 보드가 존재하는지 검증
        Board board = boardRepository.findByIdAndGatherId(boardsId, gatherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 보드를 찾을 수 없습니다."));

        // 보드 삭제
        boardRepository.delete(board);
    }
}
