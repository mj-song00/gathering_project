package com.sparta.gathering.domain.comment.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void createComment(Long scheduleId, AuthenticatedUser authenticatedUser, CommentRequest request,
            Long gatherId) {
        Member member = isValidMember(authenticatedUser, gatherId);
        Schedule schedule = findSchedule(scheduleId);
        Comment comment = new Comment(request.getComment(), schedule, member);
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(CommentRequest requestDto, Long scheduleId, Long commentId,
            AuthenticatedUser authenticatedUser, Long gatherId) {
        Member member = isValidMember(authenticatedUser, gatherId);
        checkAuth(member, authenticatedUser, getComment(scheduleId, commentId));
        Comment comment = getComment(scheduleId, commentId);
        if (!authenticatedUser.getUserId().equals(comment.getMember().getUser().getId())) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
        comment.update(requestDto.getComment());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCommentPage(Long scheduleId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findAllByScheduleIdAndDeletedAtIsNull(scheduleId, pageable);

        List<CommentResponse> commentResponses = commentPage.getContent().stream()
                .map(CommentResponse::new)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("comments", commentResponses);
        result.put("currentPage", page);
        result.put("totalPages", commentPage.getTotalPages());

        return result;
    }

    @Transactional
    public void deleteComment(AuthenticatedUser authenticatedUser, Long scheduleId, Long commentId, Long gatherId) {
        Member member = isValidMember(authenticatedUser, gatherId);
        Comment comment = getComment(scheduleId, commentId);
        checkAuth(member, authenticatedUser, comment);
        comment.delete();
    }

    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.SCHEDULE_NOT_FOUND));
    }

    private void checkAuth(Member member, AuthenticatedUser authenticatedUser, Comment comment) {
        if (!(authenticatedUser.getUserId().equals(comment.getMember().getUser().getId()) || member.getPermission()
                .equals(Permission.MANAGER))) {
            throw new BaseException(ExceptionEnum.PERMISSION_DENIED_ROLE);
        }
    }

    public Member isValidMember(AuthenticatedUser authenticatedUser, Long gatherId) {
        return memberRepository.findByGatherIdAndUserId(gatherId, authenticatedUser.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    private Comment getComment(Long scheduleId, Long commentId) {
        return commentRepository.findByScheduleIdAndIdAndDeletedAtIsNull(scheduleId, commentId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.COMMENT_NOT_FOUND));
    }
}