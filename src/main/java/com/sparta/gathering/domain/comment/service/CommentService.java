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
import com.sparta.gathering.domain.schedule.entity.Schedule;
import com.sparta.gathering.domain.schedule.repository.ScheduleRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;

    //댓글 생성
    @Transactional
    public void createComment(Long scheduleId, User user, CommentRequest request) {

        Member member = isValidMember(user);//유저가 모임의 멤버 또는 매니저인지 확인

        Schedule schedule = findSchedule(scheduleId); //PathVariable 에서 scheduleId를 가져와서 일치하는 schedule 객체를 찾아 저장한다.

        Comment comment = new Comment( // comment객체를 생성하면서 값을 넣어준다.
                request.getComment(),
                schedule,
                member
        );
        commentRepository.save(comment); // 데이터 베이스에 comment 를 저장한다.
    }

    //댓글 수정
    @Transactional
    public void updateComment(CommentRequest requestDto, Long scheduleId, Long commentId, User user) {

        //댓글 찾기
        Comment comment = commentRepository.findByScheduleIdAndId(scheduleId, commentId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.COMMENT_NOT_FOUND));

        // 댓글 작성자가 아니라면 예외처리
        if(! user.getId().equals(comment.getMember().getUser().getId())){
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }

        comment.update(requestDto.getComment());
    }

    /* 댓글 조회 */
    public List<CommentResponse> getComment(Long scheduleId) {
        return commentRepository.findAllByScheduleIdAndDeleteAtIsNullOrderByUpdatedAtDesc(scheduleId)
                .stream()
                .map(CommentResponse::new)
                .toList();
    }

    /* 댓글 삭제 */
    @Transactional
    public void deleteComment(User user, Long scheduleId, Long commentId) {
        // 유저 인증
        Member member = isValidMember(user); //멤버가 게스트이거나 매니저가 아닌경우 예외처리

        // 댓글 찾기
        Comment comment = commentRepository.findByScheduleIdAndId(scheduleId, commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물의 해당 댓글을 찾지 못했습니다."));

        // 댓글 작성자 or 매니저가 아니라면 예외처리
        checkAuth(member, user, comment);

        // 댓글 삭제
        comment.delete();
    }

    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.SCHEDULE_NOT_FOUND));
    }

    private void checkAuth(Member member, User user,Comment comment) {
        if (!user.getId().equals(comment.getMember().getUser().getId()) || member.getPermission().equals(Permission.MANAGER)) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }

    public Member isValidMember(User user) throws BaseException {

        Member member = memberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        if (!(member.getPermission().equals(Permission.GUEST) || member.getPermission().equals(Permission.MANAGER))) {
            throw new BaseException(ExceptionEnum.PERMISSION_DENIED_ROLE);
        }

        return member;
    }
}
