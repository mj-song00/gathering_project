package com.sparta.gathering.domain.comment.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.comment.dto.request.CommentRequest;
import com.sparta.gathering.domain.comment.dto.response.CommentResponse;
import com.sparta.gathering.domain.comment.entity.Comment;
import com.sparta.gathering.domain.comment.repository.CommentRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    //댓글 생성
    @Transactional
    public void createComment(Long scheduleId, User user, CommentRequest request) {

        isValidMember(user);//유저가 모임의 멤버 또는 매니저인지 확인

        Schedule schedule = findSchedule(scheduleId); //PathVariable 에서 scheduleId를 가져와서 일치하는 schedule 객체를 찾아 저장한다.

        Comment comment = new Comment( // comment객체를 생성하면서 값을 넣어준다.
                user,
                request.getComment(),
                schedule
        );

        commentRepository.save(comment); // 데이터 베이스에 comment 를 저장한다.
    }

    //댓글 수정
    @Transactional
    public void updateComment(CommentRequest requestDto, Long boardId, Long commentId, User user) {
        //유저 인증 (댓글 작성자 or 게시판 작성자


        //댓글 찾기
        Comment comment = commentRepository.findByBoardIdAndId(boardId, commentId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.COMMENT_NOT_FOUND));

        // 댓글 작성자가 아니라면 예외처리
        if(! user.getId().equals(comment.getUser().getId())){
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }

        comment.update(requestDto.getComment());
    }

    /* 댓글 조회 */
    public List<CommentResponse> getComment(Long boardId) {
        return commentRepository.findAllByBoardIdOrderByModifiedDateDesc(boardId)
                .stream()
                .map(CommentResponse::new)
                .toList();
    }

    /* 댓글 삭제 */
    @Transactional
    public Long deleteComment(User user, Long boardId, Long commentId) {
        // 유저 인증 (댓글 작성자 or 게시판 작성자)

        isValidMember(user);
        // 댓글 찾기
        Comment comment = commentRepository.findByBoardIdAndId(boardId, commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물의 해당 댓글을 찾지 못했습니다."));

        // 게시판 찾기
        Schedule schedule = comment.getSchedule();

        // 댓글 작성자 or 게시판 작성자가 아니라면 예외처리
        checkAuth(user.getId(), comment.getUser().getId(), schedule.getUser().getId());

        // 댓글 삭제
        comment.delete();

        return commentId;
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BaseException(ExceptionEnum.USER_NOT_FOUND)
        );
    }

    private Schedule findSchedule(Long boardId) {
        return scheduleRepository.findById(boardId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.SCHEDULE_NOT_FOUND));
    }

    private void checkAuth(Long userId, Long commentUserId,
                           Long boardUserId) {
        if (!userId.equals(commentUserId) && !userId.equals(boardUserId)) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }

    public void isValidMember(User user) throws BaseException {

        Member member = memberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        if (!(member.getPermission().equals(Permission.GUEST) || member.getPermission().equals(Permission.MANAGER))) {
            throw new BaseException(ExceptionEnum.PERMISSION_DENIED_ROLE);
        }
    }
}
