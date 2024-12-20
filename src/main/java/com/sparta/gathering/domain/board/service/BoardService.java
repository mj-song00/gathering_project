package com.sparta.gathering.domain.board.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.board.dto.request.BoardRequestDto;
import com.sparta.gathering.domain.board.dto.response.BoardResponseDto;
import com.sparta.gathering.domain.board.entity.Board;
import com.sparta.gathering.domain.board.repository.BoardRepository;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final GatherRepository gatherRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public BoardResponseDto createBoard(Long gatherId, BoardRequestDto boardRequestDto, AuthenticatedUser authUser) {
        // gatherId를 사용하여 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        //모임의 멤버중에 유저의 아이디가 있는지 확인
        checkAuth(gatherId, authUser);

        // Board 엔티티 생성 및 Gather 엔티티 설정
        Board board = new Board(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardContent(),
                gather);

        gather.getBoardList().add(board); // 양방향 연관관계 설정
        boardRepository.save(board);

        // 저장된 Board를 DTO로 변환하여 반환
        return new BoardResponseDto(board.getId(), board.getBoardTitle(), board.getBoardContent());
    }

    @Transactional
    public BoardResponseDto updateBoard(Long gatherId, Long boardsId,
            BoardRequestDto boardRequestDto, AuthenticatedUser authUser) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        checkAuth(gatherId, authUser);

        // gather의 boardList에서 boardsId와 일치하는 Board를 찾음
        Board board = gather.getBoardList().stream()
                .filter(b -> b.getId().equals(boardsId))
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionEnum.BOARD_NOT_FOUND));

        // 보드 내용 업데이트
        board.update(boardRequestDto.getBoardTitle(), boardRequestDto.getBoardContent(), gather);

        // 변경된 보드 저장
        Board updatedBoard = boardRepository.save(board);  // 저장 후 반환

        // 저장된 Board를 DTO로 변환하여 반환
        return new BoardResponseDto(updatedBoard.getId(), updatedBoard.getBoardTitle(),
                updatedBoard.getBoardContent());
    }

    @Transactional
    public void deleteBoard(Long gatherId, Long boardsId, AuthenticatedUser authUser) {
        // gatherId로 Gather 엔티티 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        //모임의 멤버인지 검사
        checkAuth(gatherId, authUser);

        // gather의 boardList에서 boardsId와 일치하는 Board를 찾음
        Board board = gather.getBoardList().stream()
                .filter(b -> b.getId().equals(boardsId))
                .findFirst()
                .orElseThrow(() -> new BaseException(ExceptionEnum.BOARD_NOT_FOUND));

        // gather의 boardList에서 보드를 제거
        gather.getBoardList().remove(board);

        board.delete(LocalDateTime.now());

//         보드 삭제
        boardRepository.delete(board);
    }


    private void checkAuth(Long gatherId, AuthenticatedUser authUser) {
        Member member = memberRepository.findByGatherIdAndUserId(gatherId, authUser.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.MEMBER_NOT_FOUND));
        if (!member.getGather().getId().equals(gatherId)) {
            throw new BaseException(ExceptionEnum.MEMBER_NOT_ALLOWED);
        }
    }
}
