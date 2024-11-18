package com.sparta.gathering.domain.comment.repository;

import com.sparta.gathering.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findAllByScheduleIdAndDeleteAtIsNullOrderByUpdatedAtDesc(Long scheduleId);

    Optional<Comment> findByScheduleIdAndIdAndDeleteAtIsNull(Long scheduleId, Long commentId);
}
