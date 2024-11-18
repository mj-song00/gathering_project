package com.sparta.gathering.domain.comment.repository;

import com.sparta.gathering.domain.comment.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByScheduleIdAndDeletedAtIsNullOrderByUpdatedAtDesc(Long boardId);

    Optional<Comment> findByScheduleIdAndIdAndDeletedAtIsNull(Long scheduleId, Long commentId);
}
